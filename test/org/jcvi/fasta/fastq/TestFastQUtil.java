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

package org.jcvi.fasta.fastq;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.fasta.fastq.illumina.IlluminaFastQQualityCodec;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestFastQUtil {
    private static final String file = "files/example.fastq";
    private static final ResourceFileServer RESOURCES = new ResourceFileServer(TestFastQUtil.class);
   
    FastQQualityCodec qualityCodec = new IlluminaFastQQualityCodec(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE);
    DefaultFastQFileDataStore expectedDataStore;
    @Before
    public void setup() throws FileNotFoundException, IOException{
        expectedDataStore = new DefaultFastQFileDataStore(qualityCodec);
        FastQFileParser.parse(RESOURCES.getFile(file), expectedDataStore);
    }
    @After
    public void tearDown() throws IOException{
        expectedDataStore.close();
    }
    @Test
    public void encode() throws DataStoreException{
        FastQRecord fastq = expectedDataStore.get("SOLEXA1:4:1:12:1489#0/1");
        String encodedFastQ = FastQUtil.encode(fastq, qualityCodec);
        ByteArrayInputStream in = new ByteArrayInputStream(encodedFastQ.getBytes());
        
        DefaultFastQFileDataStore actualDataStore = new DefaultFastQFileDataStore(qualityCodec);
        FastQFileParser.parse(in, actualDataStore);
        assertEquals(fastq, actualDataStore.get("SOLEXA1:4:1:12:1489#0/1"));
    }
}
