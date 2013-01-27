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
package org.jcvi.jillion.trace.sanger;

import org.jcvi.jillion.trace.sanger.Position;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestPosition {

	@Test
	public void createValidPosition(){
		Position sut = Position.valueOf(1234);
		assertEquals(1234, sut.getValue());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void creatingNegativeValueShouldThrowException(){
		Position.valueOf(-1);
	}
	
	@Test
	public void flyweightReusesSameValues(){
		Position a = Position.valueOf(123);
		Position b = Position.valueOf(123);
		assertSame(a,b);
	}
	
	@Test
	public void valueLargerThanShortMax(){
		Position sut = Position.valueOf(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, sut.getValue());
	}
}