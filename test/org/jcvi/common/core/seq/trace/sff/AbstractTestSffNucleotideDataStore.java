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
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sff;

import java.io.File;
import java.util.Iterator;

import org.jcvi.common.core.seq.trace.sff.DefaultSffFileDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class AbstractTestSffNucleotideDataStore extends AbstractTestExampleSffFile{

    private final FlowgramDataStore dataStore;
    
    {
        
        try {
        	dataStore = DefaultSffFileDataStore.create(SFF_FILE);
        } catch (Exception e) {
            throw new IllegalStateException("could not parse sff file");
        } 
    }
    
    protected abstract NucleotideSequenceDataStore createSut(File sffFile) throws Exception;
    
    @Test
    public void datastoresMatch() throws Exception{
        NucleotideSequenceDataStore sut = createSut(SFF_FILE);
        assertEquals(sut.getNumberOfRecords(), dataStore.getNumberOfRecords());
        Iterator<String> ids = sut.idIterator();
        while(ids.hasNext()){
            String id = ids.next();
            assertEquals(sut.get(id),
                    dataStore.get(id).getNucleotideSequence());
        }
    }
    
}
