package org.jcvi.jillion.testutils.assembly.cas;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriter;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriterBuilder;

public final class FastaRecordWriter implements RecordWriter{
	private final NucleotideFastaWriter writer;
	private final File fastaFile;
	private final int maxNumberOfRecordsToWrite;
	private int counter=0;
	
	public FastaRecordWriter(File workingDir) throws IOException{
		this(workingDir, Integer.MAX_VALUE);
	}
	public FastaRecordWriter(File workingDir, int maxRecordsToWrite) throws IOException{
		if(maxRecordsToWrite <1){
			throw new IllegalArgumentException("max records can not < 1");
		}
		fastaFile = File.createTempFile("reads", ".fasta", workingDir);
		writer = new NucleotideFastaWriterBuilder(fastaFile).build();
		maxNumberOfRecordsToWrite = maxRecordsToWrite;
		
	}
	
	public FastaRecordWriter(File workingDir, String filename, int maxRecordsToWrite) throws IOException{
		if(maxRecordsToWrite <1){
			throw new IllegalArgumentException("max records can not < 1");
		}
		fastaFile = new File(workingDir, filename);
		writer = new NucleotideFastaWriterBuilder(fastaFile).build();
		maxNumberOfRecordsToWrite = maxRecordsToWrite;
		
	}
	
	@Override
	public void close() throws IOException {
		writer.close();
	}
	@Override
	public void write(String id, NucleotideSequence seq) throws IOException {
		if(!canWriteAnotherRecord()){
			throw new IllegalStateException("too many records to write");
		}
		counter++;
		writer.write(id, seq);
	}
	
	public boolean canWriteAnotherRecord(){
		return counter != maxNumberOfRecordsToWrite;
	}
	
	public File getFile(){
		return fastaFile;
	}
	
	
}