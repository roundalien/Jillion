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

package org.jcvi.assembly.ace;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.contig.ace.AceContig;
import org.jcvi.common.core.assembly.contig.ace.AceContigDataStore;
import org.jcvi.common.core.assembly.contig.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.contig.ace.AllAceUnitTests;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.datastore.DefaultAceFileDataStore;
import org.jcvi.datastore.DefaultContigFileDataStore;
import org.jcvi.io.IOUtil;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestAce2Contig {

    private ResourceFileServer resources;
    File actualContigFile;
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Before
    public void setup() throws IOException{
        resources= new ResourceFileServer(AllAceUnitTests.class);
        actualContigFile = folder.newFile("actual.contig");
    }
    
   
    @Test
    public void parseAllContigs() throws IOException, DataStoreException{
        File aceFile = resources.getFile("files/sample.ace");
        File expectedContigFile = resources.getFile("files/sample.contig");
        
        Ace2Contig.main(new String[]{
                "-a", aceFile.getAbsolutePath(),
                "-c", actualContigFile.getAbsolutePath()
        });
        
        FileInputStream actualStream = new FileInputStream(actualContigFile);
       IOUtil.closeAndIgnoreErrors(actualStream);
      DefaultContigFileDataStore contigFileDataStore = new DefaultContigFileDataStore(actualContigFile);
      Contig<PlacedRead> contig = contigFileDataStore.get("Contig1");
      
      AceContigDataStore aceContigDataStore = new DefaultAceFileDataStore(aceFile);
      AceContig aceContig = aceContigDataStore.get("Contig1");
      assertEquals(aceContig.getConsensus().decode(), contig.getConsensus().decode());
      for(AcePlacedRead expectedRead : aceContig.getPlacedReads()){
          PlacedRead actualRead = contig.getPlacedReadById(expectedRead.getId());
          assertEquals(expectedRead.getEncodedGlyphs().decode(),actualRead.getEncodedGlyphs().decode());
          assertEquals(expectedRead.asRange(), actualRead.asRange());
      }
      assertTrue( FileUtils.contentEquals(expectedContigFile, actualContigFile));
    }
   
}
