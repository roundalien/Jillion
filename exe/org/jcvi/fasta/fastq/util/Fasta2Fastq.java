/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.fasta.fastq.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.fastX.ExcludeFastXIdFilter;
import org.jcvi.fastX.FastXFilter;
import org.jcvi.fastX.IncludeFastXIdFilter;
import org.jcvi.fastX.NullFastXFilter;
import org.jcvi.fastX.fasta.AbstractFastaVisitor;
import org.jcvi.fastX.fasta.FastaParser;
import org.jcvi.fastX.fasta.FastaVisitor;
import org.jcvi.fastX.fasta.qual.QualityFastaH2DataStore;
import org.jcvi.fastX.fastq.DefaultFastQRecord;
import org.jcvi.fastX.fastq.FastQQualityCodec;
import org.jcvi.fastX.fastq.FastQRecord;
import org.jcvi.fastX.fastq.FastQUtil;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.datastore.H2QualityDataStore;
import org.jcvi.io.IOUtil;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;
import org.jcvi.io.idReader.DefaultFileIdReader;
import org.jcvi.io.idReader.IdReader;
import org.jcvi.io.idReader.IdReaderException;
import org.jcvi.io.idReader.StringIdParser;

/**
 * @author dkatzel
 *
 *
 */
public class Fasta2Fastq {

    /**
     * @param args
     * @throws IOException 
     * @throws DataStoreException 
     * @throws IdReaderException 
     */
    public static void main(String[] args) throws IOException, DataStoreException, IdReaderException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("s", 
                                    "input sequence FASTA file")
                        .longName("sequence")
                        .build());
        options.addOption(new CommandLineOptionBuilder("q", 
                     "input quality FASTA file")
                    .longName("quality")
                    .build());
        options.addOption(new CommandLineOptionBuilder("sanger", 
                        "should encode output fastq file in SANGER fastq file format (default is ILLUMINA 1.3+)")
                        .isFlag(true)
                       .build());
        
        options.addOption(new CommandLineOptionBuilder("o", 
                        "output fastq file")
                        .isRequired(true)
                        .build());
        options.addOption(new CommandLineOptionBuilder("tempDir", "temp directory")
                                        .build());
        options.addOption(CommandLineUtils.createHelpOption());
        OptionGroup group = new OptionGroup();
        
        group.addOption(new CommandLineOptionBuilder("i", "include file of ids to include")
                            .build());
        group.addOption(new CommandLineOptionBuilder("e", "exclude file of ids to exclude")
                            .build());
        options.addOptionGroup(group);
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            if(commandLine.hasOption("h")){
                printHelp(options);
                System.exit(0);
            }
            
            boolean useSanger = commandLine.hasOption("sanger");
            
            final File idFile;
            final FastXFilter filter;
            if(commandLine.hasOption("i")){
                idFile =new File(commandLine.getOptionValue("i"));
                Set<String> includeList=parseIdsFrom(idFile);
                if(commandLine.hasOption("e")){
                    Set<String> excludeList=parseIdsFrom(new File(commandLine.getOptionValue("e")));
                    includeList.removeAll(excludeList);
                }
                filter = new IncludeFastXIdFilter(includeList);
                
            }else if(commandLine.hasOption("e")){
                idFile =new File(commandLine.getOptionValue("e"));
                filter = new ExcludeFastXIdFilter(parseIdsFrom(idFile));
            }else{
                filter = NullFastXFilter.INSTANCE;
            }
            final FastQQualityCodec fastqQualityCodec = useSanger? FastQQualityCodec.SANGER: FastQQualityCodec.ILLUMINA;
        
            //parse nucleotide data to temp file
            final ReadWriteDirectoryFileServer tempDir;
            H2QualityDataStore h2DataStore;
            if(!commandLine.hasOption("tempDir")){
                tempDir=null;
                h2DataStore = new H2QualityDataStore();
            }else{
                File t =new File(commandLine.getOptionValue("tempDir"));
                IOUtil.mkdirs(t);
                tempDir = DirectoryFileServer.createTemporaryDirectoryFileServer(t);
                h2DataStore = new H2QualityDataStore(tempDir.createNewFile("h2Qualities"));
            }
          
            File qualFile = new File(commandLine.getOptionValue("q"));
            final QualityFastaH2DataStore qualityDataStore = new QualityFastaH2DataStore(qualFile, h2DataStore,filter);
            
            File seqFile = new File(commandLine.getOptionValue("s"));
            final PrintWriter writer = new PrintWriter(commandLine.getOptionValue("o"));
            
            FastaVisitor visitor = new AbstractFastaVisitor() {
                
                @Override
                public boolean visitRecord(String id, String comment, String entireBody) {
                    try {
                        if(filter.accept(id, comment)){
                            EncodedGlyphs<PhredQuality> qualities =qualityDataStore.get(id);
                            if(qualities ==null){
                                throw new IllegalStateException("no quality values for "+ id);
                            }
                            FastQRecord fastq = new DefaultFastQRecord(id, 
                                    new DefaultNucleotideEncodedGlyphs(entireBody.replaceAll("\\s+", "")), qualities,comment);
    
                            writer.print(FastQUtil.encode(fastq, fastqQualityCodec));
                        }
                    } catch (DataStoreException e) {
                        throw new IllegalStateException("error getting quality data for "+ id);
                    }
                    return true;
                    
                }
            };
            FastaParser.parseFasta(seqFile, visitor);
            writer.close();
            IOUtil.closeAndIgnoreErrors(qualityDataStore);
            
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }

    }
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "fasta2Fastq [OPTIONS] -s <seq file> -q <qual file> -o <fastq file>", 
                
                "Parse a  seq and qual file and write the results out a fastq file",
                options,
               "Created by Danny Katzel"
                  );
    }
    private static Set<String> parseIdsFrom(final File idFile)   throws IdReaderException {
        IdReader<String> idReader = new DefaultFileIdReader<String>(idFile,new StringIdParser());
        Set<String> ids = new HashSet<String>();
        Iterator<String> iter =idReader.getIds();
        while(iter.hasNext()){
            ids.add(iter.next());
        }
        return ids;
    }
}
