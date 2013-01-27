/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.assembly.clc.cas.align.CasAlignment;
import org.jcvi.jillion.assembly.clc.cas.align.CasAlignmentRegionType;
import org.jcvi.jillion.assembly.clc.cas.align.CasAlignmentScore;
import org.jcvi.jillion.assembly.clc.cas.align.CasAlignmentScoreBuilder;
import org.jcvi.jillion.assembly.clc.cas.align.CasAlignmentType;
import org.jcvi.jillion.assembly.clc.cas.align.CasScoreType;
import org.jcvi.jillion.assembly.clc.cas.align.CasScoringScheme;
import org.jcvi.jillion.assembly.clc.cas.align.DefaultCasAlignment;
import org.jcvi.jillion.assembly.clc.cas.align.DefaultCasMatch;
import org.jcvi.jillion.assembly.clc.cas.align.DefaultCasScoringScheme;
import org.jcvi.jillion.core.io.IOUtil;

public final class CasParser {
	
	
	private static final byte[] CAS_MAGIC_NUMBER = new byte[]{
        (byte)0x43,
        (byte)0x4c,
        (byte)0x43,
        (byte)0x80,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x01,
    };
	
    private  int numberOfBytesForContigPosition,numberOfBytesForContigNumber;
    private  long numberOfReads;
    private CasScoringScheme scoringScheme;
    
    private CasParser(File file, CasFileVisitor visitor, boolean parseMatches) throws IOException{
        parseMetaData(file,visitor);
        if(parseMatches){
            parseMatches(file,visitor);
        }
        visitor.visitEndOfFile();
    }
    private void parseMatches(File file,
            CasFileVisitor visitor) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        try{
        IOUtil.blockingSkip(dataIn, 16);
        for(int i=0; i<numberOfReads; i++){
            byte info = dataIn.readByte();
            boolean hasMatch= (info & 0x01)!=0;
            boolean hasMultipleMatches= (info & 0x02)!=0;
            boolean hasMultipleAlignments= (info & 0x04)!=0;
            boolean isPartOfPair= (info & 0x08)!=0;
            long totalNumberOfMatches=hasMatch?1:0, numberOfReportedAlignments=hasMatch?1:0;
            if(hasMultipleMatches){                
                totalNumberOfMatches = CasUtil.parseByteCountFrom(dataIn) +2;
            }
            if(hasMultipleAlignments){
                numberOfReportedAlignments = CasUtil.parseByteCountFrom(dataIn) +2;
            }
            
            int score=0;
            CasAlignment chosenAlignment=null;
            if(hasMatch){
           
                long numberOfBytesInForThisMatch =CasUtil.parseByteCountFrom(dataIn);
                long contigSequenceId = CasUtil.readCasUnsignedInt(dataIn, this.numberOfBytesForContigNumber);
                long startPosition = CasUtil.readCasUnsignedInt(dataIn, this.numberOfBytesForContigPosition);
                boolean isreverse = dataIn.readBoolean();
                DefaultCasAlignment.Builder builder = new DefaultCasAlignment.Builder(
                                                    contigSequenceId, startPosition, 
                                                    isreverse);
                long count=0;
                
                while(count <numberOfBytesInForThisMatch){
                    short matchValue = CasUtil.readCasUnsignedByte(dataIn);
                    if(matchValue == 255){
                        builder.addPhaseChange(dataIn.readByte());                        
                        count++;
                    }
                    else if(matchValue<128){
                        builder.addRegion(CasAlignmentRegionType.MATCH_MISMATCH, matchValue +1);                        
                    }
                    else if(matchValue<192){
                        builder.addRegion(CasAlignmentRegionType.INSERT, matchValue -127);
                    }
                    else{
                        builder.addRegion(CasAlignmentRegionType.DELETION, matchValue -191);
                    }
                    count++;
                }
                chosenAlignment =builder.build();
            }
            visitor.visitMatch(new DefaultCasMatch(hasMatch, totalNumberOfMatches, numberOfReportedAlignments,
                    isPartOfPair, chosenAlignment,score));
        }
        }finally{
            IOUtil.closeAndIgnoreErrors(dataIn);
        }
        
    }
    private void parseMetaData(File file, CasFileVisitor visitor) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        try{
            byte[] magicNumber = IOUtil.toByteArray(dataIn, 8);
            if(!Arrays.equals(CAS_MAGIC_NUMBER, magicNumber)){
                throw new IllegalArgumentException("input stream not a valid cas file wrong magic number");
            }
            
            visitor.visitFile();
            BigInteger offset = CasUtil.readCasUnsignedLong(dataIn);
           IOUtil.blockingSkip(dataIn, offset.longValue()-16);
          
           
           long numberOfContigSequences = CasUtil.readCasUnsignedInt(dataIn);
           
           numberOfReads = CasUtil.readCasUnsignedInt(dataIn);
          
            visitor.visitMetaData(numberOfContigSequences, numberOfReads);
            String nameOfAssemblyProgram = CasUtil.parseCasStringFrom(dataIn);
            String version = CasUtil.parseCasStringFrom(dataIn);
            String parameters = CasUtil.parseCasStringFrom(dataIn);
            visitor.visitAssemblyProgramInfo(nameOfAssemblyProgram, version, parameters);
            
            long numberOfContigFiles =CasUtil.parseByteCountFrom(dataIn);
           visitor.visitNumberOfReferenceFiles(numberOfContigFiles);            
            for(long i=0; i< numberOfContigFiles; i++){
              boolean twoFiles =(dataIn.read() & 0x01)==1;
              long numberOfSequencesInFile = CasUtil.readCasUnsignedInt(dataIn);
              BigInteger residuesInFile = CasUtil.readCasUnsignedLong(dataIn);
              List<String> names = new ArrayList<String>();
              names.add(CasUtil.parseCasStringFrom(dataIn));
              if(twoFiles){
                  names.add(CasUtil.parseCasStringFrom(dataIn));
              }
              visitor.visitReferenceFileInfo(new DefaultCasFileInfo(names, numberOfSequencesInFile, residuesInFile));
            }
            
            long numberOfReadFiles =CasUtil.parseByteCountFrom(dataIn);
            visitor.visitNumberOfReadFiles(numberOfReadFiles);            
             for(long i=0; i< numberOfReadFiles; i++){
               boolean twoFiles =(dataIn.read() & 0x01)==1;
               long numberOfSequencesInFile = CasUtil.readCasUnsignedInt(dataIn);
               BigInteger residuesInFile = CasUtil.readCasUnsignedLong(dataIn);
               List<String> names = new ArrayList<String>();
               names.add(CasUtil.parseCasStringFrom(dataIn));
               if(twoFiles){
                   names.add(CasUtil.parseCasStringFrom(dataIn));
               }
               visitor.visitReadFileInfo(new DefaultCasFileInfo(names, numberOfSequencesInFile, residuesInFile));
             }
         
            CasScoreType scoreType = CasScoreType.valueOf((byte)dataIn.read());
            if(scoreType != CasScoreType.NO_SCORE){
                CasAlignmentScoreBuilder alignmentScoreBuilder = new CasAlignmentScoreBuilder()
                                    .firstInsertion(CasUtil.readCasUnsignedShort(dataIn))
                                    .insertionExtension(CasUtil.readCasUnsignedShort(dataIn))
                                    .firstDeletion(CasUtil.readCasUnsignedShort(dataIn))
                                    .deletionExtension(CasUtil.readCasUnsignedShort(dataIn))
                                    .match(CasUtil.readCasUnsignedShort(dataIn))
                                    .transition(CasUtil.readCasUnsignedShort(dataIn))
                                    .transversion(CasUtil.readCasUnsignedShort(dataIn))
                                    .unknown(CasUtil.readCasUnsignedShort(dataIn));
                if(scoreType == CasScoreType.COLOR_SPACE_SCORE){
                    alignmentScoreBuilder.colorSpaceError(dataIn.readShort());
                }
                CasAlignmentScore score = alignmentScoreBuilder.build();
                CasAlignmentType alignmentType = CasAlignmentType.valueOf((byte)dataIn.read());
                scoringScheme = new DefaultCasScoringScheme(scoreType, score, alignmentType);
                visitor.visitScoringScheme(scoringScheme);
                long maxContigLength=0;
                for(long i=0; i<numberOfContigSequences; i++){
                    long contigLength = CasUtil.readCasUnsignedInt(dataIn);
                    boolean isCircular = (dataIn.readUnsignedShort() & 0x01)==1;
                    visitor.visitReferenceDescription(new DefaultCasReferenceDescription(contigLength, isCircular));
                    maxContigLength = Math.max(maxContigLength, contigLength);
                }
                numberOfBytesForContigNumber = CasUtil.numberOfBytesRequiredFor(numberOfContigSequences);
                            
                numberOfBytesForContigPosition =CasUtil.numberOfBytesRequiredFor(maxContigLength);
                //contig pairs not currently used so ignore them
                
            }
        }
        finally{
            IOUtil.closeAndIgnoreErrors(dataIn);
        }
        
    }
    
    /**
     * 
     * @param file
     * @param visitor
     * @throws IOException
     */
    public static void parseCas(File file, CasFileVisitor visitor) throws IOException{
        new CasParser(file, visitor,true);        
    }
    /**
     * Parse only the meta data associated with this cas file.  This will only 
     * call:
     * <ol>
     * <li>{@link CasFileVisitor#visitMetaData(long, long)}</li>
     * <li>{@link CasFileVisitor#visitAssemblyProgramInfo(String, String, String)}</li>
     * <li>{@link CasFileVisitor#visitNumberOfReferenceFiles(long)}</li>
     * <li>n calls to {@link CasFileVisitor#visitReferenceFileInfo(CasFileInfo)} where n is the number of contig files</li>
     * <li>{@link CasFileVisitor#visitNumberOfReadFiles(long)}</li>
     * <li>n calls to {@link CasFileVisitor#visitReadFileInfo(CasFileInfo)} where n is the number of read files</li>
     * <li>{@link CasFileVisitor#visitScoringScheme(CasScoringScheme)}</li>
     * <li> n calls to {@link CasFileVisitor#visitReferenceDescription(CasContigDescription)} where n is the number of contig files</li>
     * </ol>
     * @param file
     * @param visitor
     * @throws IOException
     */
    public static void parseOnlyMetaData(File file, CasFileVisitor visitor) throws IOException{
        new CasParser(file, visitor,false);
        
    }
}