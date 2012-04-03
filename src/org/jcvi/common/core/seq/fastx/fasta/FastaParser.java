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
/*
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.io.TextLineParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaVisitor.DeflineReturnCode;
import org.jcvi.common.core.seq.fastx.fasta.FastaVisitor.EndOfBodyReturnCode;
/**
 * {@code FastaParser} is a utility class
 * to parse Fasta formated files.
 * @author dkatzel
 */
public final class FastaParser {
	
	private static final Pattern TRAILING_WHITE_SPACE_PATTERN = Pattern.compile("\\s+$");
	
	private static boolean endsWithWhiteSpace(String line){
		Matcher m = TRAILING_WHITE_SPACE_PATTERN.matcher(line);
		return m.find();
	}
    /**
     * private constructor.
     */
    private FastaParser(){}
    
    /**
     * Parse the given Fasta file and call the appropriate
     * visitXXX methods on the given visitor.
     * @param fastaFile the Fasta file to parse.
     * @param visitor the visitor to call the visit methods on.
     * @throws FileNotFoundException if the given fasta file does not 
     * exist.
     * @throws NullPointerException if fastaFile or visitor are null.
     */
    public static void parseFasta(File fastaFile, FastaVisitor visitor) throws FileNotFoundException{
        InputStream in = new FileInputStream(fastaFile);
        try{
            parseFasta(in,visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    /**
     * Parse the given Fasta file and call the appropriate
     * visitXXX methods on the given visitor.  This version
     * will try to acquire this given semaphore
     * before parsing each fasta record in the file
     * (or block until a permit has been released).
     * This is useful if parsing needs to be halted
     * and restarted where the parser left off. 
     * @param fastaFile the Fasta file to parse.
     * @param visitor the visitor to call the visit methods on.
     * @param semaphore the {@link Semaphore} which 
     * this parser will try to acquire when parsing each fasta
     * record.
     * @throws FileNotFoundException if the given fasta file does not 
     * exist.
     * @throws NullPointerException if fastaFile or visitor are null.
     */
    public static void blockingParseFasta(File fastaFile, FastaVisitor visitor) throws FileNotFoundException{
        InputStream in = new FileInputStream(fastaFile);
        try{
            parseFasta(in,visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    /**
     * Parse the given InputStream of Fasta data and call the appropriate
     * visitXXX methods on the given visitor.
     * @param in the Inputstream of Fasta data to parse.
     * @param visitor the visitor to call the visit methods on.
     * @throws NullPointerException if inputstream or visitor are null.
     */    
    public static void parseFasta(InputStream in, FastaVisitor visitor){
    	
    	
    	try {
    	    ParserState parserState = new ParserState(in);           
            visitor.visitFile();
            try{
                while(!parserState.done()){
                   parserState = SectionHandler.handleNextSection(parserState, visitor);
                }
                parserState.visitFinalRecord(visitor);
            }finally{
                IOUtil.closeAndIgnoreErrors(parserState);
            }
            visitor.visitEndOfFile();
        } catch (IOException e) {
            throw new IllegalStateException("error reading file",e);
        }
    }
    
    private static class ParserState implements Closeable{
        private boolean keepParsing;
        private final TextLineParser parser;
        private boolean done;
        private boolean skipCurrentRecord;
        private boolean seenFirstDefline=true;
        ParserState(InputStream in) throws IOException{
            if(in ==null){
                throw new NullPointerException("input stream can not be null");
            }
            parser = new TextLineParser(new BufferedInputStream(in));
            done= !parser.hasNextLine();
        }
        
        public boolean done(){
            return done;
        }
        
        public boolean skipCurrentRecord() {
			return skipCurrentRecord;
		}

		public String getNextLine() throws IOException{
            String nextLine= parser.nextLine();
            if(!parser.hasNextLine()){
                done = true;
            }
            return nextLine;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            parser.close();            
        }
        
        public void visitFinalRecord(FastaVisitor visitor){
            if(keepParsing && !skipCurrentRecord){
            	EndOfBodyReturnCode ret =visitor.visitEndOfBody();
            	if(ret ==null){
            		throw new IllegalStateException("return from visitDefline can not be null");                	
            	}
            }
        }
        
    }
    /**
     * {@code SectionHandler} has implementations
     * which "handle" each section of a Fasta
     * Record differently.
     * @author dkatzel
     *
     *
     */
    private enum SectionHandler{
        DEFLINE{
            @Override
            boolean canHandle(String line) {
                return line.startsWith(">");
            }
            @Override
            ParserState sectionSpecificHandle(String lineWithCR, ParserState parserState,FastaVisitor visitor){
                
                visitor.visitLine(lineWithCR);
                String lineWithoutCR = lineWithCR.substring(0, lineWithCR.length()-1);
                if(!parserState.seenFirstDefline && !parserState.skipCurrentRecord()){  
                	EndOfBodyReturnCode ret =visitor.visitEndOfBody();
                	if(ret ==null){
                		throw new IllegalStateException("return from visitDefline can not be null");
                	}
                	if(ret == EndOfBodyReturnCode.KEEP_PARSING){
                		parserState.keepParsing = true;
                	}else{
                		parserState.keepParsing = false;
                	}
                }                    
                DeflineReturnCode visitDefline = visitor.visitDefline(lineWithoutCR);
                if(visitDefline ==null){
                	throw new IllegalStateException("return from visitDefline can not be null");
                }
                if(visitDefline == DeflineReturnCode.SKIP_CURRENT_RECORD){
                	parserState.skipCurrentRecord=true;
                	parserState.keepParsing=true;
                }else if(visitDefline == DeflineReturnCode.STOP_PARSING){
                	parserState.keepParsing=false;
                }else{
                	parserState.skipCurrentRecord=false;
                	parserState.keepParsing=true;
                }
                parserState.seenFirstDefline = false;
                return parserState;
            }
            
        },
        BODY{
            @Override
            boolean canHandle(String line) {
                return true;
            }
            
            @Override
            ParserState sectionSpecificHandle(String lineWithCR, ParserState parserState,FastaVisitor visitor){
                
                visitor.visitLine(lineWithCR);
                final String lineWithoutCR ;
                if(endsWithWhiteSpace(lineWithCR)){
                	lineWithoutCR = lineWithCR.trim();
                }else{
                	lineWithoutCR = lineWithCR;
                }
                
                if(!parserState.skipCurrentRecord()){
                	visitor.visitBodyLine(lineWithoutCR);
                }
                return parserState;
            }
            
        };
        
        static ParserState handleNextSection(ParserState parserState,FastaVisitor visitor) throws IOException{
            String line =parserState.getNextLine();            
            for(SectionHandler handler : values()){
                if(handler.canHandle(line)){
                    return handler.sectionSpecificHandle(line, parserState, visitor);
                    
                }
            }
            //should never happen since body will accept anything
            throw new IllegalStateException("could not find handler for line " + line);
        }
        
        abstract boolean canHandle(String line);
        abstract ParserState sectionSpecificHandle(String lineWithCR, ParserState parserState,FastaVisitor visitor);
        
        
    }
    
}
