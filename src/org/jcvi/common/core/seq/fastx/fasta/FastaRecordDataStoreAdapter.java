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
package org.jcvi.common.core.seq.fastx.fasta;

import java.io.IOException;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreIterator;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code FastaRecordDataStoreAdapter} adapts a {@link DataStore} of {@link FastaRecord}s
 * into a {@link DataStore} of the value returned by {@link FastaRecord#getSequence()}.
 * @author dkatzel
 *
 *
 */
public class FastaRecordDataStoreAdapter<S extends Symbol,T extends Sequence<S>,F extends FastaRecord<S,T>> implements DataStore<T> {

    private final DataStore<F> delegate;
    /**
     * Convert of {@code DataStore<F>} into a DataStore{@code DataStore<T>}
     * @param <T> the values of the fastaRecord.
     * @param <F> a FastaRecord.
     * @param datastoreOfFastaRecords the DataStore of F to wrap.
     * @return a new {@code DataStore<T>} which wraps the given datastore. 
     */
    public static <S extends Symbol,T extends Sequence<S>,F extends FastaRecord<S,T>> FastaRecordDataStoreAdapter<S,T,F> adapt(DataStore<F> datastoreOfFastaRecords){
        return new FastaRecordDataStoreAdapter<S,T,F>(datastoreOfFastaRecords);
    }
    public FastaRecordDataStoreAdapter(DataStore<F> datastoreOfFastaRecords){
        this.delegate = datastoreOfFastaRecords;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return delegate.contains(id);
    }

    @Override
    public T get(String id) throws DataStoreException {
        return delegate.get(id).getSequence();
    }

    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        return delegate.idIterator();
    }

    @Override
    public long getNumberOfRecords() throws DataStoreException {
        return delegate.getNumberOfRecords();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
        
    }

    @Override
    public StreamingIterator<T> iterator() {
        return new DataStoreIterator<T>(this);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return delegate.isClosed();
    }
    
    
}
