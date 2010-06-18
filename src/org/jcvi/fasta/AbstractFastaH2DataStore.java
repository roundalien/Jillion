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

package org.jcvi.fasta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.AbstractH2EncodedGlyphDataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;

/**
 * {@code AbstractFastaH2DataStore} is an abstract implementation to the record
 * bodies (basecall or qual data) of FastaRecords inside an H2 data store.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractFastaH2DataStore <G extends Glyph, E extends EncodedGlyphs<G>> implements FastaVisitor, DataStore<E>{

    private final AbstractH2EncodedGlyphDataStore<G, E> h2Datastore;
    public AbstractFastaH2DataStore(File fastaFile,AbstractH2EncodedGlyphDataStore<G, E> h2Datastore) throws FileNotFoundException{
        this.h2Datastore = h2Datastore;
        FastaParser.parseFasta(fastaFile, this);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public void visitBodyLine(String bodyLine) {
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitDefline(String defline) {
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitRecord(String id, String comment, String entireBody) {
        try{
            h2Datastore.insertRecord(id, entireBody);
        }
        catch (DataStoreException e) {
            throw new IllegalStateException("could not insert record into datastore",e);
        }
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitLine(String line) {
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfFile() {
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitFile() {
        
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return h2Datastore.contains(id);
    }

    @Override
    public E get(String id) throws DataStoreException {
        return h2Datastore.get(id);
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        return h2Datastore.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        return h2Datastore.size();
    }

    @Override
    public void close() throws IOException {
        h2Datastore.close();
        
    }

    @Override
    public Iterator<E> iterator() {
        return h2Datastore.iterator();
    }

}
