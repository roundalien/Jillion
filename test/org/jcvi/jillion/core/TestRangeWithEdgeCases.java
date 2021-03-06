/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core;

import java.util.Collections;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestRangeWithEdgeCases {

	@Test
	public void intersectionLongMax(){
		Range r1 = Range.of(Long.MAX_VALUE-9L,Long.MAX_VALUE);
		Range r2 = Range.of(Long.MAX_VALUE-19L,Long.MAX_VALUE);
		
		Range intersection = r1.intersection(r2);
		assertEquals(r1, intersection);
	}
	
	@Test
	public void intersectionLongMin(){
		Range r1 = new Range.Builder(10)
						.shift(Long.MIN_VALUE)
						.build();
		Range r2 = new Range.Builder(20)
							.shift(Long.MIN_VALUE)
							.build();
		
		Range intersection = r1.intersection(r2);
		assertEquals(r1, intersection);
	}
	
	@Test
	public void complimentLongMaxWithLargerRangeShouldBeEmpty(){
		Range r1 = Range.of(Long.MAX_VALUE-9L,Long.MAX_VALUE);
		Range r2 = Range.of(Long.MAX_VALUE-19L,Long.MAX_VALUE);
		
		List<Range> intersection = r1.complement(r2);
		assertTrue(intersection.isEmpty());
	}
	
	@Test
	public void complimentLongMinWithLargerRangeShouldBeEmpty(){
		Range r1 = new Range.Builder(10)
						.shift(Long.MIN_VALUE)
						.build();
		Range r2 = new Range.Builder(20)
						.shift(Long.MIN_VALUE)
						.build();
		
		List<Range> intersection = r1.complement(r2);
		assertTrue(intersection.isEmpty());
	}
	@Test
	public void complimentLongMaxWithSmallerRangeShouldBeNonEmpty(){
		Range r1 = Range.of(Long.MAX_VALUE-9L,Long.MAX_VALUE);
		Range r2 = Range.of(Long.MAX_VALUE-19L,Long.MAX_VALUE);
		
		List<Range> intersection = r2.complement(r1);
		Range expected = Range.of(Long.MAX_VALUE-19L,Long.MAX_VALUE-10L);
		assertEquals(Collections.singletonList(expected),intersection);
	}
	
	@Test
	public void complimentLongMinWithSmallerRangeShouldBeNonEmpty(){
		Range r1 = new Range.Builder(10)
							.shift(Long.MIN_VALUE)
							.build();
		Range r2 = new Range.Builder(20)
				.shift(Long.MIN_VALUE)
				.build();
		
		List<Range> intersection = r2.complement(r1);
		Range expected = new Range.Builder(10)
							.shift(Long.MIN_VALUE+10)
							.build();
		assertEquals(Collections.singletonList(expected),intersection);
	}
}
