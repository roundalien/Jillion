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
 * Created on Dec 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger.phd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.sanger.phd.DefaultPhdFileDataStore;
import org.jcvi.jillion.trace.sanger.phd.Phd;
import org.jcvi.jillion.trace.sanger.phd.PhdDataStore;
import org.jcvi.jillion.trace.sanger.phd.PhdDataStoreBuilder;
import org.jcvi.jillion.trace.sanger.phd.PhdParser;
import org.jcvi.jillion.trace.sanger.phd.PhdTag;
import org.junit.Test;
public class TestFakePhdReadParser {

    private static final String PHD_FILE = "files/fake.phd";
    
    private static ResourceHelper RESOURCES = new ResourceHelper(TestFakePhdReadParser.class);

    @Test
    public void parseFakeReads() throws IOException, DataStoreException{
        PhdDataStoreBuilder builder = DefaultPhdFileDataStore.createBuilder();        
        PhdParser.parsePhd(RESOURCES.getFileAsStream(PHD_FILE), builder);
        PhdDataStore dataStore = builder.build();
        Phd fakePhd = dataStore.get("HA");
        assertIsFake(fakePhd);
        assertEquals(1738, fakePhd.getNucleotideSequence().getLength());
        
        assertIsFake(dataStore.get("contig00001"));
        
        Phd realPhd = dataStore.get("FTF2AAH02G7TE3.6-91");
        assertEquals(
                "TCAGCGCGTAGTCGACGCAGCTGTCGTGTGCAGCAAAAGCAGGTAGATATTGAAAGATGAGTCTTCTAACCGAGGTCGAAACGTACGTTCTCTCTATCGTCCCGTCAGGCCCCCTCCAAGACCGCGATCGCGCAGAGACTTGTAAGAATGTGTTTGTCAGGGAAAAACGAAACCGACTCTTGTAGGCGGCTCATGGAAGTAGGGTCCGTAAAAGAACAAGAACCAACTCCTCGTTCACCTCCTGACTAAGGGGTAAGTTTTAGGTTAGTTTGTTGGTTCTACGCTCACCGTCGCCACGTGAGCGAGGACGTGCGACGCGTAGGTAACGGCCGTTTGTTCCGAAAACTAAGCCCGTTAACTTAGGGAAGTAGGGGTAGGTCCAACCAACATGGACGAGAGCGGTCGAACTACGTACAACGAAGGACTTAAAAGGGTAAAAGTAAACAATTACCTACTAGGGGCGGAAAAGAAGGTGGCGACCTACCTAGTTAAGTTTACTAACCTAGGTTGGCACTTAGTCACGCTGCGACTGGGTCCGTCCTATGTTACAACAGGAGTAGGGACGGTGTGACCACTGAGTAGGCGATTGGTCCCGAACGACGGACAGCGTGCGTACG" 
        		, 
        		realPhd.getNucleotideSequence().toString());
    }

    private void assertIsFake(Phd fakePhd) {
        boolean isFake=false;
       for(PhdTag tag :fakePhd.getTags()){
           if("WR".equals(tag.getTagName())){
               if(tag.getTagValue().contains("type: fake")){
                   isFake=true;
                   break;
               }
           }
       }
       assertTrue(isFake);
    }
}