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
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta.qual;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fastx.fasta.LargeFastaIdIterator;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code LargeQualityFastaFileDataStore} is an implementation
 * of {@link AbstractQualityFastaFileDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fastq file
 * which can take some time.  It is recommended that instances are wrapped
 * in {@link CachedDataStore}.
 * @author dkatzel
 *
 *
 */
public final class LargeQualityFastaFileDataStore extends AbstractQualityFastaFileDataStore implements QualitySequenceFastaDataStore{
   private final File fastaFile;

    private Long size;
    /**
     * Construct a {@link LargeQualityFastaFileDataStore}
     * for the given Fasta file and the given {@link QualityFastaRecordFactory}.
     * @param fastaFile the Fasta File to use, can not be null.
     * @param fastaRecordFactory the QualityFastaRecordFactory implementation to use.
     * @throws NullPointerException if fastaFile is null.
     */
    public LargeQualityFastaFileDataStore(File fastaFile,
            QualityFastaRecordFactory fastaRecordFactory) {
        super(fastaRecordFactory);
        if(fastaFile ==null){
            throw new NullPointerException("fasta file can not be null");
        }
        this.fastaFile = fastaFile;
    }
    /**
     * Convenience constructor using the {@link DefaultQualityFastaRecordFactory}.
     * This call is the same as {@link #LargeQualityFastaFileDataStore(File,QualityFastaRecordFactory)
     * new LargeQualityFastaFileDataStore(fastaFile,DefaultQualityFastaRecordFactory.getInstance());}
     * @see LargeQualityFastaFileDataStore#LargeQualityFastaFileDataStore(File, QualityFastaRecordFactory)
     */
    public LargeQualityFastaFileDataStore(File fastaFile) {
        super();
        this.fastaFile = fastaFile;
    }
    
    @Override
	public EndOfBodyReturnCode visitEndOfBody() {
		return EndOfBodyReturnCode.KEEP_PARSING;
	}

    @Override
    public boolean contains(String id) throws DataStoreException {
    	StreamingIterator<String> iter =idIterator();
    	while(iter.hasNext()){
    		String nextId = iter.next();
    		if(nextId.equals(id)){
    			IOUtil.closeAndIgnoreErrors(iter);
    			return true;
    		}
    	}
    	return false;
    }

    @Override
    public synchronized QualitySequenceFastaRecord get(String id)
            throws DataStoreException {
    	StreamingIterator<QualitySequenceFastaRecord> iter =iterator();
    	while(iter.hasNext()){
    		QualitySequenceFastaRecord fasta = iter.next();
    		if(fasta.getId().equals(id)){
    			IOUtil.closeAndIgnoreErrors(iter);
    			return fasta;
    		}
    	}
    	 throw new DataStoreException("could not get record for "+id);
       
    }

    @Override
    public synchronized StreamingIterator<String> idIterator() throws DataStoreException {
        checkNotYetClosed();
        return LargeFastaIdIterator.createNewIteratorFor(fastaFile);

    }

    @Override
    public synchronized long getNumberOfRecords() throws DataStoreException {
        checkNotYetClosed();
            if(size ==null){
            	
                RecordCounter recordCounter = new RecordCounter();
				
				try {
					FastaFileParser.parse(fastaFile, recordCounter);
					LargeQualityFastaFileDataStore.this.size=recordCounter.getCount();
				} catch (FileNotFoundException e) {
					throw new DataStoreException("error parsing fasta file",e);
				}
            	
            } 

        return size;

    }
    /**
     * visits a fasta file and counts how many records there are.
     * @author dkatzel
     *
     *
     */
    private static final class RecordCounter extends AbstractFastaVisitor{
        long count=0;
        @Override
        public synchronized boolean visitRecord(String id, String comment, String entireBody) {
            count++;
            return true;
        }
        /**
         * @return the count
         */
        public synchronized long getCount() {
            return count;
        }
        
        
    }

    @Override
    public synchronized StreamingIterator<QualitySequenceFastaRecord> iterator() {
        checkNotYetClosed();
        LargeQualityFastaIterator iter = new LargeQualityFastaIterator(fastaFile);
            iter.start();
        
        return iter;
    }

  
}
