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

package org.jcvi.assembly.ace;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.Session;
import org.ggf.drmaa.SessionFactory;
import org.jcvi.common.command.Command;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.command.grid.GridJobBuilder;
import org.jcvi.common.command.grid.GridJobBuilders;
import org.jcvi.common.command.grid.GridJobExecutorService;
import org.jcvi.common.command.grid.PostExecutionHook;
import org.jcvi.common.command.grid.SimpleGridJob;
import org.jcvi.common.command.grid.GridJob.MemoryUnit;
import org.jcvi.common.core.assembly.contig.ace.AceContigDataStore;
import org.jcvi.common.core.assembly.contig.ace.IndexedAceFileDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DefaultExcludeDataStoreFilter;
import org.jcvi.common.core.datastore.DefaultIncludeDataStoreFilter;
import org.jcvi.common.core.datastore.EmptyDataStoreFilter;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.internal.command.grid.JcviQueue;
import org.jcvi.common.io.idReader.DefaultFileIdReader;
import org.jcvi.common.io.idReader.IdReader;
import org.jcvi.common.io.idReader.IdReaderException;
import org.jcvi.common.io.idReader.StringIdParser;

/**
 * @author dkatzel
 *
 *
 */
public class GridFindAbacusErrorsInAce {

    private static final File ABACUS_WORKER_EXE = new File("/usr/local/devel/DAS/software/Elvira/bin/workers/detectAbacusErrorsInAce");
    /**
     * @param args
     * @throws DrmaaException 
     * @throws IOException 
     * @throws DataStoreException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws DrmaaException, IOException, DataStoreException, InterruptedException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("a", "path to ace file (required)")
                .longName("ace")
                .isRequired(true)
                .build());
        
        options.addOption(new CommandLineOptionBuilder("nav", "path to optional consed navigation file to see abacus errors easier in consed")
        .isRequired(true)
        .build());
        
        options.addOption(CommandLineUtils.createHelpOption());
        OptionGroup group = new OptionGroup();
        
        group.addOption(new CommandLineOptionBuilder("i", "include file of ids to include")
                            .build());
        group.addOption(new CommandLineOptionBuilder("e", "exclude file of ids to exclude")
                            .build());
        options.addOptionGroup(group);
        
        options.addOption(new CommandLineOptionBuilder("P", "grid project code (required)")
        .isRequired(true)
        .build());
        
        
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        
        SessionFactory factory = SessionFactory.getFactory();
        final Session session = factory.getSession();
        session.init("");
        GridJobExecutorService executor=null;
        try{
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            String projectCode = commandLine.getOptionValue("project_code");
            boolean wantsNav = commandLine.hasOption("nav");
            File navFile=null;
            if(wantsNav){
                navFile = new File(commandLine.getOptionValue("nav"));
                IOUtil.deleteIgnoreError(navFile);
                if(!navFile.createNewFile()){
                    throw new IOException("error creating file; already exists and cannot delete"+ navFile.getAbsolutePath());
                }
            }
           
            executor = new GridJobExecutorService(session,"abacusErrorDetector", 100);
            List<SimpleGridJob> jobs = new ArrayList<SimpleGridJob>();
            File aceFile = new File(commandLine.getOptionValue("a"));
            final DataStoreFilter filter = getDataStoreFilter(commandLine);
            AceContigDataStore datastore = IndexedAceFileDataStore.create(aceFile);
            Iterator<String> contigIds = datastore.getIds();
            Set<File> files = new HashSet<File>();
            
            while(contigIds.hasNext()){
                final String contigId = contigIds.next();
                if(filter.accept(contigId)){
                    Command findAbacusErrorWorker = new Command(ABACUS_WORKER_EXE);
                    findAbacusErrorWorker.setOption("-ace", aceFile.getAbsolutePath());
                    findAbacusErrorWorker.setOption("-c", contigId);
                    if(wantsNav){
                        File temp = File.createTempFile(aceFile.getName(), "ctg."+contigId+".nav");
                        findAbacusErrorWorker.setOption("-nav", temp.getAbsolutePath());
                        files.add(temp);
                    }
                    GridJobBuilder<SimpleGridJob> job = GridJobBuilders.createSimpleGridJobBuilder(
                                                        session,
                                                        findAbacusErrorWorker, 
                                                        projectCode);
                    job.postExecutionHook(new PostExecutionHook() {
                        
                        @Override
                        public int execute(Map<String, JobInfo> jobInfoMap) throws Exception {
                            for(Entry<String, JobInfo> entry : jobInfoMap.entrySet()){
                                System.out.printf("grid job %s for contig id  %s finished%n", 
                                        entry.getKey(), contigId);
                            }
                            return 0;
                        }
                    });
                    job.setMemory(16, MemoryUnit.GB);
                    jobs.add(job.build());
                }             
            }
       
           for(Future<?> future : executor.invokeAll(jobs)){
               try {
                future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
           }
            executor.shutdown();
            
            PrintWriter navWriter= new PrintWriter(navFile);
           
            for(File partialNav : files){
                Scanner scanner = new Scanner(partialNav);
                while(scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    navWriter.println(line);
                }
                scanner.close();                    
            }
            navWriter.close();
            for(File partialNav : files){
                IOUtil.deleteIgnoreError(partialNav);
            }
            
           
        } catch (ParseException e) {
            e.printStackTrace();
            printHelp(options);
            System.exit(1);
        }finally{
            session.exit();
        }
    }
    private static DataStoreFilter getDataStoreFilter(CommandLine commandLine)
                                                        throws IdReaderException {
        final DataStoreFilter filter;
        File idFile;
        if(commandLine.hasOption("i")){
            idFile =new File(commandLine.getOptionValue("i"));
            Set<String> includeList=parseIdsFrom(idFile);
            if(commandLine.hasOption("e")){
                Set<String> excludeList=parseIdsFrom(new File(commandLine.getOptionValue("e")));
                includeList.removeAll(excludeList);
            }
            filter = new DefaultIncludeDataStoreFilter(includeList);
            
        }else if(commandLine.hasOption("e")){
            idFile =new File(commandLine.getOptionValue("e"));
            filter = new DefaultExcludeDataStoreFilter(parseIdsFrom(idFile));
        }else{
            filter = EmptyDataStoreFilter.INSTANCE;
        }
        return filter;
    }
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "gridFindAbacusErrorsInAceFile -a <ace file>", 
                
                "Parse an ace file and write out ungapped consensus coordinates of abacus assembly errors",
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
