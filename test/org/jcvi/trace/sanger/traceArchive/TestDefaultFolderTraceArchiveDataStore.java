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
 * Created on Jul 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import java.io.File;
import java.io.IOException;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.trace.TraceDecoderException;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;
public class TestDefaultFolderTraceArchiveDataStore {

    private DefaultFolderTraceArchiveDataStore sut;
    private static final String FOLDER_ROOT_DIR = "files/exampleTraceArchive";
    private static final TraceArchiveRecordIdGenerator ID_GENERATOR= new NameTagTraceArchiveRecordIdGenerator();
    private TraceArchiveInfo traceInfo ;
    private String absoluteRootPath;
    
    @Before
    public void setup() throws IOException{
       traceInfo = new DefaultTraceArchiveInfo(
                new TraceInfoXMLTraceArchiveInfoBuilder<TraceArchiveRecord>(
                ID_GENERATOR, 
                TestDefaultFolderTraceArchiveDataStore.class.getResourceAsStream(FOLDER_ROOT_DIR+"/TRACEINFO.xml")));
       
       absoluteRootPath = new File(TestDefaultFolderTraceArchiveDataStore.class.getResource(FOLDER_ROOT_DIR).getFile()).getAbsolutePath();
       sut = new DefaultFolderTraceArchiveDataStore(
               absoluteRootPath ,
                traceInfo);
       
    }
    private static void assertTraceArchiveTraceValuesEqual(TraceArchiveTrace expected, TraceArchiveTrace actual){
        assertEquals(expected.getBasecalls().decode(), actual.getBasecalls().decode());
        assertEquals(expected.getQualities().decode(), actual.getQualities().decode());
        assertEquals(expected.getPeaks().getData().decode(), actual.getPeaks().getData().decode());
    }
    @Test
    public void getTrace() throws DataStoreException{
        String tracename = "XX08A02T44F09PB11F";
        TraceArchiveTrace expectedTrace = new DefaultTraceArchiveTrace(traceInfo.get(tracename),absoluteRootPath);
        assertTraceArchiveTraceValuesEqual(expectedTrace, sut.get(tracename));
    }
    
    @Test
    public void traceDoesNotExistShouldThrowTraceDecoderException(){
        String idThatDoesNotExist = "doesNotExist";
        try{
            sut.get(idThatDoesNotExist);
            fail("should Throw TraceDecoderException");
        }
        catch(DataStoreException e){
            assertEquals(idThatDoesNotExist + " does not exist", e.getMessage());
        }
        
    }
}
