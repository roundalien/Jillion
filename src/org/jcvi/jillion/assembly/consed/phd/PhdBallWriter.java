/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
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
package org.jcvi.jillion.assembly.consed.phd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.JoinedStringBuilder;

public class PhdBallWriter implements PhdWriter{

	 private static final int INITIAL_SIZE_READ_TAG_BUFFER = 500;
	private static final String BEGIN_SEQUENCE = "BEGIN_SEQUENCE";
	    private static final String BEGIN_COMMENT = "BEGIN_COMMENT";
	    private static final String END_SEQUENCE = "END_SEQUENCE";
	    private static final String END_COMMENT = "END_COMMENT";
	    
	    private static final String BEGIN_DNA = "BEGIN_DNA";
	    private static final String END_DNA = "END_DNA";
	    
	    private static final String BEGIN_TAG = "BEGIN_TAG\n";
	    private static final String END_TAG = "END_TAG\n";

	    private static final String NEW_LINE = String.format("%n");
	    /**
	     * Initial size of StringBuffer used to write out phd
	     * text data.  Buffer will grow if this size is exceeded.
	     */
		private static final int INITIAL_PHD_BUFFER_SIZE = 1024;
	
	private final Writer writer;
	/**
	 * Create a new {@link PhdBallWriter} instance
	 * that will write its contents to the given {@link OutputStream}.
	 * @param out the {@link OutputStream} to write to;
	 * can not be null.  
	 * @throws NullPointerException if outputFile is null.
	 */
	public PhdBallWriter(OutputStream out) throws IOException{
		this(out, null);
	}
	/**
	 * Create a new {@link PhdBallWriter} instance
	 * that will write its contents to the given {@link OutputStream}.
	 * @param out the {@link OutputStream} to write to;
	 * can not be null.  
	 * @param fileComment a comment String to be written
	 * at the top of the phdball; Should only be 
	 * one line. The writer will format the comment
	 * so phd's know that it is a comment.  If fileComment
	 * is null, then no comment will be written.
	 * @throws NullPointerException if outputFile is null.
	 * @throws IllegalArgumentException if fileComment is more
	 * than one line.
	 * @throws IOException if there are problems writing
	 * the fileComment to the outputStream.
	 */
	public PhdBallWriter(OutputStream out, String fileComment) throws IOException{
		if(out ==null){
			throw new NullPointerException("output stream can not be null");
		}
		assertCommentIsOnlyOneLine(fileComment);
		this.writer = new BufferedWriter(new OutputStreamWriter(out, IOUtil.UTF_8));
		if(fileComment !=null){
			writer.write(String.format("#%s%n", fileComment));
		}
	}
	/**
	 * Create a new {@link PhdBallWriter} instance
	 * that will write its contents to the given file.
	 * @param outputFile the {@link File} to write to;
	 * can not be null.  If this file already exists,
	 * then it will be overwritten.  If the File does
	 * not exist it will be created along with any non-existent
	 * parent directories.
	 * @param fileComment a comment String to be written
	 * at the top of the phdball; Should only be 
	 * one line. The writer will format the comment
	 * so phd's know that it is a comment.  If fileComment
	 * is null, then no comment will be written.
	 * @throws IOException if there is a problem creating
	 * the file or writing the fileComment (if non-null).
	 * @throws NullPointerException if outputFile is null.
	 * @throws IllegalArgumentException if fileComment is more
	 * than one line.
	 */
	public PhdBallWriter(File outputFile, String fileComment) throws IOException{
		if(outputFile ==null){
			throw new NullPointerException("output file can not be null");
		}
		assertCommentIsOnlyOneLine(fileComment);
		IOUtil.mkdirs(outputFile.getParentFile());
		this.writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(outputFile), IOUtil.UTF_8));
		if(fileComment !=null){
			writer.write(String.format("#%s%n", fileComment));
		}
	}
	
	private void assertCommentIsOnlyOneLine(String fileComment){
		if(fileComment !=null){
			Scanner scanner = new Scanner(fileComment);
			try{
				scanner.nextLine();
				if(scanner.hasNext()){
					throw new IllegalArgumentException("fileComment can not be multi-line");
				}
			}finally{
				scanner.close();
			}
		}
	}
	/**
	 * Create a new {@link PhdBallWriter} instance
	 * that will write its contents to the given file.
	 * @param outputFile the {@link File} to write to;
	 * can not be null.  If this file already exists,
	 * then it will be overwritten.  If the File does
	 * not exist it will be created along with any non-existent
	 * parent directories.
	 * @throws IOException if there is a problem creating
	 * the file.  
	 * @throws NullPointerException if outputFile is null.
	 */
	public PhdBallWriter(File outputFile) throws IOException{
		this(outputFile, null);
	}

	@Override
	public void close() throws IOException {
		writer.close();
		
	}

	@Override
	public void write(Phd phd) throws IOException {
		write(phd, null);
		
	}

	@Override
	public void write(Phd phd, Integer version) throws IOException {
		if(phd ==null){
			throw new NullPointerException("phd can not be null");
		}
		if(version !=null && version.intValue() <1){
			throw new IllegalArgumentException("version must be >=1");
		}
		writePhd(phd, version);
		
	}
	
	
	    private void writePhd(Phd phd, Integer version) throws IOException{
	        try{
	            StringBuilder phdRecord = new StringBuilder(INITIAL_PHD_BUFFER_SIZE);
	            if(version ==null){
	            	phdRecord.append( String.format("%s %s%n%n",BEGIN_SEQUENCE, phd.getId()));
	            }else{
	            	phdRecord.append( String.format("%s %s %d%n%n",BEGIN_SEQUENCE, phd.getId(),version));
	            }
	            phdRecord.append(createComments(phd))
			            .append(writeDnaSection(phd))
			            .append(NEW_LINE);
	            List<PhdReadTag> tags = phd.getReadTags();
	            if(!tags.isEmpty()){
	            	 phdRecord.append(writeReadTags(tags));
	            }
	            phdRecord.append(String.format("%s%n",END_SEQUENCE))
	            	.append(createWholeReadItems(phd));
	            writer.write(phdRecord.toString());
	        }catch(Throwable t){
	            throw new IOException("error writing phd record for "+phd.getId(), t);
	        }
	        
	    }
	    

	    private String writeReadTags(List<PhdReadTag> tags) {
			List<String> printedTags = new ArrayList<String>(tags.size());
	    	for(PhdReadTag tag : tags){
	    		Range range = tag.getUngappedRange();
				StringBuilder builder = new StringBuilder(INITIAL_SIZE_READ_TAG_BUFFER)
					.append(BEGIN_TAG)
					.append(String.format("TYPE: %s%n",tag.getType()))
					.append(String.format("SOURCE: %s%n",tag.getSource()))
				
					.append(String.format("UNPADDED_READ_POS: %d %d%n",
						range.getBegin(Range.CoordinateSystem.RESIDUE_BASED),
						range.getEnd(Range.CoordinateSystem.RESIDUE_BASED)))
				
						.append(String.format("DATE: %s%n", PhdUtil.formatReadTagDate(tag.getDate())));
				if(tag.getComment() !=null){
					builder.append(BEGIN_COMMENT).append(NEW_LINE)
					.append(tag.getComment()).append(NEW_LINE)
					.append(END_COMMENT).append(NEW_LINE);
				}
				if(tag.getFreeFormData() !=null){
					builder.append(tag.getFreeFormData()).append(NEW_LINE);
				}
				
				printedTags.add(builder.append(END_TAG)
									.toString());
			}
			return new JoinedStringBuilder(printedTags)
						.glue(NEW_LINE)
						.build();
		}
		private StringBuilder createWholeReadItems(Phd phd) {
	        StringBuilder tags = new StringBuilder();
	        for(PhdWholeReadItem tag : phd.getWholeReadItems()){
	        	String lines = new JoinedStringBuilder(tag.getLines())
	        						.glue(NEW_LINE)
	        						.build();
	            tags.append(String.format("WR{%n%s%n}%n",lines));
	        }
	        return tags;
	        
	        
	    }

	    private StringBuilder writeDnaSection(Phd phd) {
	        StringBuilder dna = new StringBuilder(INITIAL_PHD_BUFFER_SIZE);
	        dna.append(String.format("%s%n",BEGIN_DNA))
	        .append(writeCalledInfo(phd))
	        .append(String.format("%s%n",END_DNA));   
	        return dna;
	    }

	    private StringBuilder writeCalledInfo( Phd phd){
	       
	        NucleotideSequence nucleotideSequence = phd.getNucleotideSequence();
	        int seqLength = (int)nucleotideSequence.getLength();
			Iterator<Nucleotide> basesIter = nucleotideSequence.iterator();
	        Iterator<PhredQuality> qualIter = phd.getQualitySequence().iterator();
	       
	        PositionSequence peaks = phd.getPeakSequence();
	        StringBuilder result = new StringBuilder(seqLength *10);
	        if(peaks==null){
	            while(basesIter.hasNext()){
	            	result.append(String.format("%s %d%n",
	            			basesIter.next(), 
	                        qualIter.next().getQualityScore()));
	            }
	        }else{
	        	 //optimization to convert to array instead 
	            //of iterating over Position objects
	            //this way we get primitives.
	        	short[] positions = phd.getPeakSequence().toArray();
	        	int i=0;
	            while(basesIter.hasNext()){
	            	result.append(String.format("%s %d %d%n",
	            			basesIter.next(), 
	                        qualIter.next().getQualityScore(),
	                        IOUtil.toUnsignedShort(positions[i])));
	            	i++;
	            }
	        }
	       
	        return result;
	        
	    }

	    private StringBuilder createComments(Phd phd) {
	        StringBuilder comments = new StringBuilder(INITIAL_SIZE_READ_TAG_BUFFER)
								        .append(BEGIN_COMMENT)
								        .append(NEW_LINE);
	        for(Entry<String, String> entry :phd.getComments().entrySet()){
	            comments.append(String.format("%s: %s%n",entry.getKey(),entry.getValue()));
	        }
	        comments.append(END_COMMENT)
	        		.append(NEW_LINE);
	        
	        return comments;
	    }
	
}
