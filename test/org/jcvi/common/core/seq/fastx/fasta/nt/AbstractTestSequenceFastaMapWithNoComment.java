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
 * Created on Feb 20, "2009" +
 *
 * @author "dkatzel" +
 */
package org.jcvi.common.core.seq.fastx.fasta.nt;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.junit.Test;

public abstract class AbstractTestSequenceFastaMapWithNoComment extends AbstractTestSequenceFastaDataStoreWithNoComment{
    @Test
    public void parseStream() throws IOException, DataStoreException{
        DataStore<NucleotideSequenceFastaRecord> sut = buildMap(
        		RESOURCES.getFile(FASTA_FILE_PATH));
        assertEquals(1, sut.getNumberOfRecords());
        assertEquals(hrv_61, sut.get("hrv-61"));
    }

}
