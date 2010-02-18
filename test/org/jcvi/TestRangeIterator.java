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
 * Created on Mar 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi;


import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestRangeIterator {

    Range range = Range.buildRange(1, 10);
    RangeIterator sut;
    @Before
    public void setup(){
        sut = new RangeIterator(range);
    }
    @Test
    public void iterate(){
        for(long i= range.getStart(); i<=range.getEnd(); i++){
            assertTrue(sut.hasNext());
            assertEquals(Long.valueOf(i), sut.next());
        }
        assertFalse(sut.hasNext());
    }
    
    @Test
    public void removeShouldthrowUnsupportedOperationException(){
        
        try{
            sut.remove();
            fail("should throw Unsupported operation exception");
        }catch(UnsupportedOperationException e){
            assertEquals("can not remove from Range", e.getMessage());
        }
    }
    
    @Test
    public void iteratorOverRange(){
        for(Long i : range){
            assertEquals(i, sut.next());
        }
        assertFalse(sut.hasNext());
    }
}
