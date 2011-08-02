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

package org.jcvi.common.core.symbol.residue.nuc;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultNucleotideSequence {

    private String gappedBasecalls = "ACGTACGT-ACGTACG-T";
    
    DefaultNucleotideSequence sut = new DefaultNucleotideSequence(gappedBasecalls);
    
    @Test
    public void decode(){
        List<Nucleotide> expected = Nucleotides.parse(gappedBasecalls);
        assertEquals(expected, sut.decode());
    }
    
    @Test
    public void getNumberOfGaps(){
        assertEquals(2, sut.getNumberOfGaps());
    }
    
    @Test
    public void getNumberOfGapsUntil(){
        assertEquals("before any gaps", 0, sut.getNumberOfGapsUntil(5));
        assertEquals("on the gap", 1, sut.getNumberOfGapsUntil(8));
        assertEquals("after 1 gap", 1, sut.getNumberOfGapsUntil(9));
        assertEquals("after all gaps gap", 2, sut.getNumberOfGapsUntil((int)sut.getLength()-1));
    }
    
    @Test
    public void iterator(){
        Iterator<Nucleotide> expected = Nucleotides.parse(gappedBasecalls).iterator();
        Iterator<Nucleotide> actual = sut.iterator();
        assertTrue(actual.hasNext());
        while(actual.hasNext()){
            assertEquals(expected.next(), actual.next());
        }
        assertFalse(expected.hasNext());
    }
    
    @Test
    public void testToString(){
        assertEquals(gappedBasecalls, sut.toString());
    }
}
