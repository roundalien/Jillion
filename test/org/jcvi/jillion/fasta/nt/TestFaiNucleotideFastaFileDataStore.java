package org.jcvi.jillion.fasta.nt;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Test;

public class TestFaiNucleotideFastaFileDataStore {

	NucleotideFastaDataStore sut;
	NucleotideFastaDataStore delegate;
	@Before
	public void setup() throws IOException{
		ResourceHelper helper = new ResourceHelper(getClass());
		
		File fasta = helper.getFile("files/no_extra_on_defline.XXXXX.combo2.i.contigs");
		File fai = helper.getFile("files/no_extra_on_defline.XXXXX.combo2.i.contigs.fai");
		delegate = DefaultNucleotideFastaFileDataStore.create(fasta);
		sut = FaiNucleotideFastaFileDataStore.create(fasta, fai, delegate);
		
		
	}
	
	@Test
	public void getSequence() throws DataStoreException{
		String id = "MAINb";
		
		assertEquals(delegate.getSequence(id), sut.getSequence(id));
	}
	
	@Test
	public void getSubSequenceAtOffset0ShouldBeFullLength() throws DataStoreException{
		String id = "MAINb";
		
		assertEquals(delegate.getSequence(id), sut.getSubSequence(id, 0));
	}
	
	@Test
	public void getSubSequence() throws DataStoreException{
		String id = "MAINb";
		
		assertEquals(delegate.getSubSequence(id, 345), sut.getSubSequence(id, 345));
	}
	
	@Test
	public void getSubSequenceRange() throws DataStoreException{
		String id = "MAINb";
		Range range = Range.of(123, 456);
		assertEquals(delegate.getSubSequence(id, range), sut.getSubSequence(id, range));
	}
}
