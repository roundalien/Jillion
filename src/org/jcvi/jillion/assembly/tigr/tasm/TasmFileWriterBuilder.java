/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.tigr.tasm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.Builder;
/**
 * {@code TasmFileWriterBuilder} builds a {@link TasmWriter}
 * using which has been configured by the the parameters given
 * to its various methods.
 * @author dkatzel
 *
 */
public final class TasmFileWriterBuilder implements Builder<TasmWriter>{

	private final File outputFile;
	private final OutputStream out;
	
	private boolean annotationContigs=false;
	/**
	 * Create a new Builder which will build a 
	 * {@link TasmWriter}
	 *  that will write its data to the given
	 *  output file.
	 * @param outputTasmFile the output File to write the data
	 * to; can not be null.  If this file already exists, then the old data will
	 * be overwritten, if this file does not exist or if
	 * any parent directories for this file do not exist,
	 * then they will be created when the TasmWriter is built.
	 * @throws NullPointerException if outputTasmFile is null.
	 */
	public TasmFileWriterBuilder(File outputTasmFile){
		if(outputTasmFile ==null){
			throw new NullPointerException("output file can not be null");
		}
		this.outputFile = outputTasmFile;
		this.out = null;
	}
	/**
	 * Configure the TasmWriter to write 
	 * "TIGR annotation contigs" which are used
	 * by the annotation teams.  This type of contig
	 * differs from a normal contig, in that the
	 * annotation contig only has the consensus and related
	 * metadata, the underlying read information is not
	 * included in the tasm output.  
	 * <strong>This method should only be used
	 * internally by JCVI</strong> since only
	 * internal JCVI utilities require annotation tasm files.
	 * 
	 * @return this
	 */
	public TasmFileWriterBuilder writeAnnotationContigs(){
		this.annotationContigs=true;
		return this;
	}
	/**
	 * Create a new Builder which will build a 
	 * {@link TasmWriter}
	 *  that will write its data to the given
	 *  {@link OutputStream}.
	 * @param out the{@link OutputStream} to write the data
	 * to; can not be null.
	 * @throws NullPointerException if outputTasmFile is null.
	 */
	public TasmFileWriterBuilder(OutputStream out){
		if(out ==null){
			throw new NullPointerException("outputStream can not be null");
		}
		this.outputFile = null;
		this.out = out;
	}
	
	@Override
	public TasmWriter build() {
		if(outputFile ==null){
			return new TasmFileWriter(out, annotationContigs);
		}else{
			try {
				return new TasmFileWriter(outputFile, annotationContigs);
			} catch (IOException e) {
				throw new IllegalStateException("error creating tasm writer",e);
			}
		}
	}

	private static final class TasmFileWriter implements TasmWriter{

		private final OutputStream out;
		private final boolean writeAnnotationContigs;
		private int numberOfContigsWritten=0;
		
		public TasmFileWriter(File outputFile,boolean writeAnnotationContigs) throws IOException{
			IOUtil.mkdirs(outputFile);
			out = new BufferedOutputStream(new FileOutputStream(outputFile));
			this.writeAnnotationContigs = writeAnnotationContigs;
		}
		
		public TasmFileWriter(OutputStream out,boolean writeAnnotationContigs){
			
			this.out = out;
			this.writeAnnotationContigs = writeAnnotationContigs;
		}
		
		@Override
		public void close() throws IOException {
			out.close();			
		}

		@Override
		public void write(TasmContig contig) throws IOException {
			if(numberOfContigsWritten>0){
				TasmFileWriterUtil.writeContigSeparator(out);
			}
			if(writeAnnotationContigs){
				TasmFileWriterUtil.writeAnnotationRecord(contig, out);
			}else{
				TasmFileWriterUtil.write(contig, out);
			}
			numberOfContigsWritten++;
			
		}
		
	}
}
