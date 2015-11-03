package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * Wraps a NucleotideFastaWriter that writes to an output 
 * file and on {@link #close()} will parse the completed
 * output fasta file and write out the index fai file to the
 * specified output file.
 * 
 * @author dkatzel
 *
 * @since 5.1
 */
class FaiNucleotideFastaWriter implements NucleotideFastaWriter {

	private NucleotideFastaWriter delegate;
	private final File inputFasta, outputfaiFile;
	private volatile boolean closed=false;
	
	public FaiNucleotideFastaWriter(File inputFasta, File outputfaiFile, NucleotideFastaWriter delegate) {
		this.inputFasta = inputFasta;
		this.outputfaiFile = outputfaiFile;
		this.delegate = delegate;
		
	}

	@Override
	public void close() throws IOException {
		if(!closed){
			closed = true;
			delegate.close();
			new FaiNucleotideWriterBuilder(inputFasta)
				.outputFile(outputfaiFile)
				.build();
			
		}
	}

	@Override
	public void write(NucleotideFastaRecord record) throws IOException {
		delegate.write(record);
	}

	@Override
	public void write(String id, NucleotideSequence sequence) throws IOException {
		delegate.write(id, sequence);
	}

	@Override
	public void write(String id, NucleotideSequence sequence, String optionalComment) throws IOException {
		delegate.write(id, sequence, optionalComment);
	}

}
