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
/*
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceFileDataStore;
import org.jcvi.jillion.assembly.tigr.contig.TigrContig;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigDataStore;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileDataStoreBuilder;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public abstract class  AbstractTestAceParserMatchesAce2Contig {
    private static TigrContigDataStore expectedContigDataStore;

    /**
     * Subclasses need to set this field directory in static initializer
     */
    protected static AceFileDataStore sut=null;
    protected static File ACE_FILE = null;
    protected static File CONTIG_FILE = null;
    
    private static final List<String> IDS = Arrays.asList(
			"22934-PB2",
			"22934-PB1",
			"22934-PA",
			"22934-HA",
			"22934-NP",
			"22934-NA",
			"22934-MP",
			"22934-NS"
			);

   
    @BeforeClass
    public static void createDataStores() throws IOException{
    	
    	ResourceHelper RESOURCES = new ResourceHelper(AbstractTestAceParserMatchesAce2Contig.class);
    	CONTIG_FILE = RESOURCES.getFile("files/fluSample.contig");
    	ACE_FILE =  RESOURCES.getFile("files/fluSample.ace");
    	expectedContigDataStore = new TigrContigFileDataStoreBuilder(CONTIG_FILE,
    											AceContigTestUtil.createFullLengthSeqDataStoreFrom(ACE_FILE))
										.build();
    }
    
    @AfterClass
    public static void closeDataStores() throws IOException{
    	expectedContigDataStore.close();
    	expectedContigDataStore = null;
    	
    	sut.close();
    	sut = null;
    }

    
    @Test
    public void numberOfContigs() throws DataStoreException{
    	assertEquals(expectedContigDataStore.getNumberOfRecords(), sut.getNumberOfRecords());    	
    }
    
    @Test
    public void numberOfTotalReads() throws DataStoreException{
    	assertEquals(543, sut.getNumberOfTotalReads());
    }
    @Test
    public void idIterator() throws DataStoreException{
    	StreamingIterator<String> ids = null;
    	try{
    		ids =sut.idIterator();  	
    		while(ids.hasNext()){
    			assertTrue(expectedContigDataStore.contains(ids.next()));
    		}
    	}finally{
    		IOUtil.closeAndIgnoreErrors(ids);
    	}
    }
    
    @Test
    public void iterator() throws DataStoreException{
    	StreamingIterator<AceContig> iter = null;
    	try{
    		iter =sut.iterator();  	
    		while(iter.hasNext()){
    			AceContig actualContig = iter.next();
    			assertTrue(expectedContigDataStore.contains(actualContig.getId()));
			  TigrContig expectedContig = expectedContigDataStore.get(actualContig.getId());
    	      AceContigTestUtil.assertContigsEqual(expectedContig, actualContig);
    	       
    		}
    	}finally{
    		IOUtil.closeAndIgnoreErrors(iter);
    	}
    }
    
    @Test
    public void get() throws DataStoreException{
    	
    	
    	for(String id : IDS){
    		TigrContig expected = expectedContigDataStore.get(id);
    		AceContig actual = sut.get(id);
    		AceContigTestUtil.assertContigsEqual(expected, actual);
    	}
    }
  
	@Test
	public void sizeVsIterator() throws IOException, DataStoreException{
		
		long size = sut.getNumberOfRecords();
		long expected = 0;
		StreamingIterator<AceContig> iter = sut.iterator();
		try{
			while(iter.hasNext()){
				expected++;
				iter.next();
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		assertEquals(expected, size);
	}
	
	@Test
	public void contains() throws DataStoreException, IOException{
		for(String id : IDS){
			assertTrue(sut.contains(id));
		}
	}
	
	@Test
	public void containsIdNotInDataStoreShouldReturnFalse() throws DataStoreException, FileNotFoundException, IOException{
		assertFalse(sut.contains("not in datastore"));
	}
	
	@Test
	public void getVsIterator() throws DataStoreException, IOException{
		StreamingIterator<AceContig> iter=null;
		try{
			iter = sut.iterator();
			while(iter.hasNext()){
				AceContig fromIter = iter.next();
				AceContig fromGet = sut.get(fromIter.getId());
				AceContigTestUtil.assertContigsEqual(fromIter, fromGet);
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}
}
