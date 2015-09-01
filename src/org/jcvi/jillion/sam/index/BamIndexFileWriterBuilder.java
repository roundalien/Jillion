/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
package org.jcvi.jillion.sam.index;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.sam.index.BamIndexer;
import org.jcvi.jillion.internal.sam.index.IndexUtil;
import org.jcvi.jillion.sam.SamParser;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.SortOrder;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.header.SamHeader;
/**
 * {@code BamIndexFileWriterBuilder}
 * is a class that will create a BAM
 * index file from a sorted BAM input file.
 * @author dkatzel
 *
 */
public final class BamIndexFileWriterBuilder {

	
	private boolean includeMetaData=false;
	private boolean assumeSorted=false;
	
	private File outputBaiFile, inputBamFile;
	
	/**
	 * Create a new Builder instance with the given input BAM and 
	 * output index files.
	 * @param inputBamFile the input BAM file to parse and create
	 * 			an index from; can not be null, must exist
	 * 			and end with {@literal ".bam"}.
	 * @param outputBaiFile the output BAM index file to write; can not be null
	 * 			and end with {@literal ".bai"}. If the output path does not exist,
	 * 			then the file and any non-existent parent directories will be created.
	 * @throws IOException if there are any problems creating any missing output files
	 * 			or directories.
	 * @throws NullPointerException if any parameter is null.
	 * @throws IllegalArgumentException if the file extensions aren't correct.
	 */
	public BamIndexFileWriterBuilder(File inputBamFile, File outputBaiFile) throws IOException{
		if(inputBamFile == null){
			throw new NullPointerException("input bam file can not be null");
		}
		if(outputBaiFile == null){
			throw new NullPointerException("output bai file can not be null");
		}
		
		if(!"bam".equalsIgnoreCase(FileUtil.getExtension(inputBamFile))){
			throw new IllegalArgumentException("input file is not a bam file " + inputBamFile.getAbsolutePath());
		}
		if(!"bai".equalsIgnoreCase(FileUtil.getExtension(outputBaiFile))){
			throw new IllegalArgumentException("input file is not a bai file " + outputBaiFile.getAbsolutePath());
		}
		if(!inputBamFile.exists()){
			throw new FileNotFoundException("input bam file must exist " + inputBamFile.getAbsolutePath());
		}
		IOUtil.mkdirs(outputBaiFile.getParentFile());
		this.inputBamFile = inputBamFile;
		this.outputBaiFile = outputBaiFile;
		
	}
	/**
	 * Assume that the given input BAM file contains {@link SortOrder#COORDINATE} 
	 * sorted data even if the header says otherwise.  If not called,
	 * by default, this value is set to {@code false}.
	 * If this value is set to {@code false}, then during the {@link #build()}, the header
	 * of the input BAM is checked to make sure the sort order is set to 
	 * {@link SortOrder#COORDINATE} and will throw an {@link IllegalStateException}
	 * if it's not.
	 * @param assumeSorted {@code true} if the header should not be checked
	 * and assumed to be {@link SortOrder#COORDINATE}; {@code false} if the header
	 * should be checked and fail if not (the default).
	 * @return this.
	 */
	public BamIndexFileWriterBuilder assumeSorted(boolean assumeSorted){
		this.assumeSorted = assumeSorted;
		return this;
	}
	/**
	 * BAM Index files created by some other SAM libraries add additional
	 * metadata to the index such as the number of unaligned reads. This metadata
	 * is not specified in the BAM Index specification so some SAM parsers might not be able to correctly
	 * handle them.  If this method is not called, then by default, the metadata is not included.
	 *
	 * @param includeMetaData {@code true} if metadata should be included;
	 * {@code false} otherwise (the default).
	 * @return this.
	 */
	public BamIndexFileWriterBuilder includeMetaData(boolean includeMetaData){
		this.includeMetaData = includeMetaData;
		return this;
	}
	/**
	 * Actually parse the input BAM file and write out the corresponding
	 * BAM index file to the given output file.
	 * 
	 * @return the output indexed {@link File}
         * (this is the same file as was passed into the constructor)
         * 
	 * @throws IOException if there are any problems parsing the 
	 * BAM file or writing out the index file.
	 * @throws IllegalStateException if the BAM file is not in {@link SortOrder#COORDINATE}
	 * order and {@link #assumeSorted(boolean)} is set to {@code false}.
	 */
	public File build() throws IOException{
		SamParser parser = SamParserFactory.create(inputBamFile);
		BamIndexSamVisitor visitor = new BamIndexSamVisitor(assumeSorted);
		parser.accept(visitor);
		
		OutputStream out =null;
		try{
			out = new BufferedOutputStream(new FileOutputStream(outputBaiFile));
			IndexUtil.writeIndex(out, visitor.createBamIndex(), includeMetaData);
			return outputBaiFile;
		}finally{
			IOUtil.closeAndIgnoreErrors(out);
		}
	}
	
	
	private static class BamIndexSamVisitor implements SamVisitor {
		private BamIndexer indexer;
		private final boolean assumeSorted;
		
		

		public BamIndexSamVisitor(boolean assumeSorted) {
			this.assumeSorted = assumeSorted;
		}

		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record,
				VirtualFileOffset start, VirtualFileOffset end) {
			indexer.addRecord(record, start, end);
			
		}
		
		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record) {
			// TODO no-op or throw exception?
			
		}
		
		@Override
		public void visitHeader(SamVisitorCallback callback, SamHeader header) {
			if(!assumeSorted && !header.getSortOrder().equals(SortOrder.COORDINATE)){
				throw new IllegalStateException("bam file not in coordinate sort order : " + header.getSortOrder());
			}
			indexer = new BamIndexer(header);
		}
		
		@Override
		public void visitEnd() {
			//no-op
		}
		
		@Override
		public void halted() {
			//no-op
		}
		
		public BamIndex createBamIndex(){
			return indexer.createBamIndex();
		}
		
	}
}
