/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
/*
 * Created on Jul 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.archive;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.archive.DefaultFolderTraceArchiveDataStore;
import org.jcvi.jillion.trace.archive.DefaultTraceArchiveTrace;
import org.jcvi.jillion.trace.archive.NameTagTraceArchiveRecordIdGenerator;
import org.jcvi.jillion.trace.archive.TraceArchiveInfo;
import org.jcvi.jillion.trace.archive.TraceArchiveRecordIdGenerator;
import org.jcvi.jillion.trace.archive.TraceArchiveTrace;
import org.jcvi.jillion.trace.archive.TraceInfoXMLTraceArchiveInfoBuilder;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;
public class TestDefaultFolderTraceArchiveDataStore {

    private DefaultFolderTraceArchiveDataStore sut;
    private static final String FOLDER_ROOT_DIR = "files/exampleTraceArchive";
    private static final TraceArchiveRecordIdGenerator ID_GENERATOR= new NameTagTraceArchiveRecordIdGenerator();
    private TraceArchiveInfo traceInfo ;
    private String absoluteRootPath;
    private final static ResourceHelper RESOURCES = new ResourceHelper(TestDefaultFolderTraceArchiveDataStore.class);
	
    @Before
    public void setup() throws IOException{
       traceInfo = 
                new TraceInfoXMLTraceArchiveInfoBuilder(
                ID_GENERATOR, 
                RESOURCES.getFileAsStream(FOLDER_ROOT_DIR+"/TRACEINFO.xml")).build();
       
       absoluteRootPath = RESOURCES.getFile(FOLDER_ROOT_DIR).getAbsolutePath();
       sut = new DefaultFolderTraceArchiveDataStore(
               absoluteRootPath ,
                traceInfo);
       
    }
    private static void assertTraceArchiveTraceValuesEqual(TraceArchiveTrace expected, TraceArchiveTrace actual){
        assertEquals(expected.getNucleotideSequence(), actual.getNucleotideSequence());
        assertEquals(expected.getQualitySequence(), actual.getQualitySequence());
        assertEquals(expected.getPositionSequence(), actual.getPositionSequence());
    }
    @Test
    public void getTrace() throws DataStoreException{
        String tracename = "XX08A02T44F09PB11F";
        TraceArchiveTrace expectedTrace = new DefaultTraceArchiveTrace(traceInfo.get(tracename),absoluteRootPath);
        assertTraceArchiveTraceValuesEqual(expectedTrace, sut.get(tracename));
    }
    
    @Test
    public void traceDoesNotExistShouldReturnNull() throws DataStoreException{
    	assertNull(sut.get("doesNotExist"));
        
    }
}