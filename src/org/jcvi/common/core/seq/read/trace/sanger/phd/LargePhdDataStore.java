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
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * {@code LargePhdDataStore} is a {@link PhdDataStore} implementation
 * to be used a very large phd files or phdballs.  No data contained in this
 * phd file is stored in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the phd file
 * which can take some time.  It is recommended that instances of 
 * {@link LargePhdDataStore} are wrapped by {@link CachedDataStore}
 * @author dkatzel
 *
 *
 */
public class LargePhdDataStore implements PhdDataStore{

    static final Pattern BEGIN_SEQUENCE_PATTERN = Pattern.compile("BEGIN_SEQUENCE\\s+(\\S+)");
    private final File phdFile;
    private Integer size=null;
    boolean closed = false;
    
    /**
     * @param phdFile
     */
    public LargePhdDataStore(File phdFile) {
        if(!phdFile.exists()){
            throw new IllegalArgumentException("phd file does not exists "+ phdFile.getAbsolutePath());
        }
        this.phdFile = phdFile;
    }

    private void checkIfClosed(){
        if(closed){
            throw new IllegalStateException("datastore is closed");
        }
    }
    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        checkIfClosed();
        CloseableIterator<Phd> iter = iterator();
        while(iter.hasNext()){
            Phd phd = iter.next();
            if(phd.getId().equals(id)){
                IOUtil.closeAndIgnoreErrors(iter);
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized Phd get(String id) throws DataStoreException {
        checkIfClosed();
        CloseableIterator<Phd> iter = iterator();
        while(iter.hasNext()){
            Phd phd = iter.next();
            if(phd.getId().equals(id)){
                IOUtil.closeAndIgnoreErrors(iter);
                return phd;
            }
        }
        throw new DataStoreException("could not find phd for "+id);
        
    }

    @Override
    public synchronized CloseableIterator<String> getIds() throws DataStoreException {
        checkIfClosed();
        return new PhdIdIterator();
       
    }

    @Override
    public synchronized int size() throws DataStoreException {
        checkIfClosed();
        if(size ==null){
            int count=0;
            CloseableIterator<Phd> iter = iterator();
            while(iter.hasNext()){
                count++;
                iter.next();
            }
            size = Integer.valueOf(count);
        }
        return size;
    }

    @Override
    public synchronized void close() throws IOException {
        closed = true;
        
    }

    @Override
    public synchronized CloseableIterator<Phd> iterator() {
        checkIfClosed();
        return LargePhdIterator.createNewIterator(phdFile);
    }

    
    private class PhdIdIterator implements CloseableIterator<String>{

        private final CloseableIterator<Phd> phdIter;
        
        private PhdIdIterator(){
            phdIter = iterator();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void remove() {
            phdIter.remove();
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean hasNext() {
            return phdIter.hasNext();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            phdIter.close();
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public String next() {
            return phdIter.next().getId();
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
