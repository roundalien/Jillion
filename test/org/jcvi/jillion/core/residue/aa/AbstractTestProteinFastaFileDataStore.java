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
package org.jcvi.jillion.core.residue.aa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.aa.ProteinFastaDataStore;
import org.jcvi.jillion.fasta.aa.ProteinFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.aa.ProteinFastaRecord;
import org.jcvi.jillion.fasta.aa.ProteinFastaRecordBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.AfterClass;
import org.junit.Test;
public abstract class AbstractTestProteinFastaFileDataStore {

	private static ProteinFastaDataStore sut;
	private static File fastaFile;
	private static DataStoreProviderHint hint;
	
	private final ProteinFastaRecord last = new ProteinFastaRecordBuilder("read4","SEDDEHIKFTW")
																.build();
	private final ProteinFastaRecord second = new ProteinFastaRecordBuilder("read2","IKFTWMKAILSEDDEH")
																.comment("comment")
																.build();
	private final ProteinFastaRecord first = new ProteinFastaRecordBuilder("read1","IKFTW")
																	.build();
	public AbstractTestProteinFastaFileDataStore(DataStoreProviderHint hint) throws IOException{
		ResourceHelper helper = new ResourceHelper(AbstractTestProteinFastaFileDataStore.class);
		fastaFile = helper.getFile("files/aa.fasta");
		AbstractTestProteinFastaFileDataStore.hint = hint;
		
	}
	
	@AfterClass
	public static void closeDataStore() throws IOException{
		sut.close();
		sut=null;
	}
	
	private static ProteinFastaDataStore getSut() throws IOException{
		if(sut ==null){
			sut = new ProteinFastaFileDataStoreBuilder(fastaFile)
			.hint(hint)
			.filter(DataStoreFilters.newExcludeFilter(Arrays.asList("read3")))
			.build();
		}
		return sut;
	}
	@Test
	public void size() throws DataStoreException, IOException{
		assertEquals(3, getSut().getNumberOfRecords());
	}
	
	@Test
	public void getFirst() throws DataStoreException, IOException{		
		assertEquals(first, getSut().get("read1"));
	}
	@Test
	public void getSecond() throws DataStoreException, IOException{		
		assertEquals(second, getSut().get("read2"));
	}
	@Test
	public void getLast() throws DataStoreException, IOException{		
		assertEquals(last, getSut().get("read4"));
	}
	
	@Test
	public void doesNotContainFilteredRead() throws DataStoreException, IOException{
		assertFalse(getSut().contains("read3"));
	}
	
	@Test
	public void iterator() throws DataStoreException, IOException{
		Iterator<ProteinFastaRecord> expected = Arrays.asList(first,second,last).iterator();
		
		StreamingIterator<ProteinFastaRecord> actual = null;
		try{
			actual = getSut().iterator();
			while(expected.hasNext()){
				assertTrue(actual.hasNext());
				assertEquals(expected.next(), actual.next());
			}
			assertFalse(actual.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(actual);
		}
	}
	@Test
	public void idIterator() throws DataStoreException, IOException{
		Iterator<String> expected = Arrays.asList(first.getId(),
															second.getId(),
															last.getId())
																.iterator();
		
		StreamingIterator<String> actual = null;
		try{
			actual = getSut().idIterator();
			while(expected.hasNext()){
				assertTrue(actual.hasNext());
				assertEquals(expected.next(), actual.next());
			}
			assertFalse(actual.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(actual);
		}
	}
	
}
