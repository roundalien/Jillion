package org.jcvi.jillion.fasta.nt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public class TestNucleotideFastaDataStoreBuilderWithLambdaRecordFilter {

	private static File fastaFile;
	
	private final Consumer<NucleotideFastaFileDataStoreBuilder> hinter;
	
	@BeforeClass
	public static void setup() throws IOException{
		ResourceHelper helper = new ResourceHelper(TestNucleotideFastaDataStoreBuilderWithLambdaRecordFilter.class);
		fastaFile = helper.getFile("files/giv_XX_15050.seq");
	}
	
	@Parameters
	public static List<Object[]> data(){
		Consumer<NucleotideFastaFileDataStoreBuilder> inMem = builder->builder.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED);
		Consumer<NucleotideFastaFileDataStoreBuilder> memento = builder->builder.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY);		
		Consumer<NucleotideFastaFileDataStoreBuilder> iterOnly = builder->builder.hint(DataStoreProviderHint.ITERATION_ONLY);
		
		return Arrays.asList(
				new Object[]{inMem},
				new Object[]{memento},
				new Object[]{iterOnly});
	}
	
	public TestNucleotideFastaDataStoreBuilderWithLambdaRecordFilter(Consumer<NucleotideFastaFileDataStoreBuilder> hinter){
		this.hinter = hinter;
	}
	
	@Test
	public void noFilter() throws IOException, DataStoreException{
		NucleotideFastaFileDataStoreBuilder builder = new NucleotideFastaFileDataStoreBuilder(fastaFile);
		hinter.accept(builder);
		try(NucleotideFastaDataStore sut = builder.build()){
			assertEquals(274, sut.getNumberOfRecords());
		}
	}
	
	@Test
	public void onlyKeepLongReads() throws IOException, DataStoreException{
		NucleotideFastaFileDataStoreBuilder builder = new NucleotideFastaFileDataStoreBuilder(fastaFile);
		hinter.accept(builder);
		
		try(NucleotideFastaDataStore sut =builder
													.filterRecords(record-> record.getSequence().getLength() >1000)
													.build();
			StreamingIterator<NucleotideFastaRecord> iter = sut.iterator();
			){
			assertEquals(33, sut.getNumberOfRecords());
			while(iter.hasNext()){
				assertTrue(iter.next().getSequence().getLength() > 1000);
			}
		}
	}
	
}