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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fastq;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIteratorAdapter;
/**
 * {@code DefaultFastqDataStore} is a {@link FastqDataStore}
 * implementation that stores all {@link FastqRecord}s
 * in a Map.
 * @author dkatzel
 *
 */
final class DefaultFastqDataStore implements FastqDataStore {

    private final Map<String, FastqRecord> map;
    private boolean closed = false;

    private void checkNotClosed() throws DataStoreException{
        if(closed){
            throw new DataStoreException("can not access closed dataStore");
        }
    }
    /**
     * @param map
     */
    private DefaultFastqDataStore(Map<String, FastqRecord> map) {
        this.map = map;
    }

    @Override
    public boolean contains(String id) throws DataStoreException{
        checkNotClosed();
        return map.containsKey(id);
    }

    @Override
    public FastqRecord get(String id) throws DataStoreException{
        checkNotClosed();
        return map.get(id);
    }

    @Override
    public long getNumberOfRecords() throws DataStoreException{
        checkNotClosed();
        return map.size();
    }

    @Override
    public CloseableIterator<String> idIterator() throws DataStoreException {
        checkNotClosed();
        return CloseableIteratorAdapter.adapt(map.keySet().iterator());
    }

    @Override
    public void close() throws IOException {
        closed=true;
        map.clear();
        
    }

    @Override
    public CloseableIterator<FastqRecord> iterator() {
        try {
            checkNotClosed();
        } catch (DataStoreException e) {
            throw new IllegalStateException("could not create iterator", e);
        }
        return CloseableIteratorAdapter.adapt(map.values().iterator());
    }
    
    public static class Builder implements org.jcvi.common.core.util.Builder<DefaultFastqDataStore>{
        private final Map<String, FastqRecord> map;
        
        public Builder(){
            map = new LinkedHashMap<String, FastqRecord>();
        }
        public Builder(int numberOfRecords){
            map = new LinkedHashMap<String, FastqRecord>(numberOfRecords);
        }
        public Builder put(FastqRecord fastQRecord){
            map.put(fastQRecord.getId(), fastQRecord);
            return this;
        }
        public Builder remove(FastqRecord fastQRecord){
            map.remove(fastQRecord.getId());
            return this;
        }
        public Builder putAll(Collection<FastqRecord> fastQRecords){
            for(FastqRecord fastQRecord : fastQRecords){
                put(fastQRecord);
            }           
            return this;
        }
        
        public Builder removeAll(Collection<FastqRecord> fastQRecords){
            for(FastqRecord fastQRecord : fastQRecords){
                remove(fastQRecord);
            }           
            return this;
        }
        @Override
        public DefaultFastqDataStore build() {
            return new DefaultFastqDataStore(map);
        }
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return closed;
    }

}
