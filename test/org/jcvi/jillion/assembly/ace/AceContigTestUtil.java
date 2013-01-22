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
package org.jcvi.jillion.assembly.ace;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

/**
 * @author dkatzel
 *
 *
 */
public final class AceContigTestUtil {

   

    
    public static  void assertContigsEqual(Contig<? extends AssembledRead> expected, Contig<? extends AssembledRead> actual) {
        assertEquals(expected.getId(), actual.getId()); 
        assertEquals(expected.getConsensusSequence(), actual.getConsensusSequence());
        assertEquals(expected.getId(),expected.getNumberOfReads(), actual.getNumberOfReads());
        StreamingIterator<? extends AssembledRead> iter = null;
        try{
        	iter = expected.getReadIterator();
        	while(iter.hasNext()){
        		AssembledRead expectedRead = iter.next();
        		assertPlacedReadParsedCorrectly(expectedRead, actual.getRead(expectedRead.getId()));
        	}
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        //now iterate over actual
        try{
        	iter = actual.getReadIterator();
        	while(iter.hasNext()){
        		AssembledRead actualRead = iter.next();
        		assertPlacedReadParsedCorrectly(actualRead, expected.getRead(actualRead.getId()));
        	}
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        
    }

    public static  void assertPlacedReadParsedCorrectly(AssembledRead expected,
            AssembledRead actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getId(), expected.getGappedStartOffset(), actual.getGappedStartOffset());
        assertEquals(expected.getId(), expected.getGappedEndOffset(), actual.getGappedEndOffset());
        assertEquals(expected.getId(), expected.getGappedLength(), actual.getGappedLength());
        assertEquals(expected.getId(), expected.getReadInfo().getValidRange(), actual.getReadInfo().getValidRange());
        assertEquals(expected.getId(), expected.getNucleotideSequence(), actual.getNucleotideSequence());
        
    }
}
