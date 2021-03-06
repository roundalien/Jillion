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
package org.jcvi.jillion.core.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.util.GrowableByteArray;
import org.junit.Test;
public class TestGrowableByteArray {

	@Test
	public void append(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		byte[] expected = new byte[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void replace(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		sut.replace(1, (byte)15);
		byte[] expected = new byte[]{10,15,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendBeyondCapacityShouldGrowArray(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		sut.append((byte)50);
		sut.append((byte)60);
		
		byte[] expected = new byte[]{10,20,30,40,50,60};
		assertArrayEquals(expected, sut.toArray());
	}
	
	@Test
	public void remove(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.remove(2);
		
		byte[] expected = new byte[]{10,20,40};
		assertToArrayCorrect(sut, expected);
	}
	private void assertToArrayCorrect(GrowableByteArray sut, byte[] expected) {
		assertEquals(expected.length, sut.getCurrentLength());
		assertArrayEquals(expected, sut.toArray());
		for(byte i=0; i< expected.length; i++){
			assertEquals(expected[i], sut.get(i));
		}
	}
	@Test
	public void removeRange(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.remove(Range.of(1,2));
		
		byte[] expected = new byte[]{10,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void removeEmptyRangeShouldDoNothing(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.remove(new Range.Builder().shift(2).build());
		
		byte[] expected = new byte[]{10,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insert(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.insert(1, (byte)15);
		
		byte[] expected = new byte[]{10,15,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	
	@Test
	public void insertAtOffsetLengthShouldActLikeAppend(){
		GrowableByteArray sut = new GrowableByteArray(4);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.insert(4, (byte)50);
		
		byte[] expected = new byte[]{10,20,30,40,50};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insertArray(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.insert(1, new byte[]{15,16,17,18,19});
		
		byte[] expected = new byte[]{10,15,16,17,18,19,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insertOtherGrowableByteArray(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.insert(1, new GrowableByteArray(new byte[]{15,16,17,18,19}));
		
		byte[] expected = new byte[]{10,15,16,17,18,19,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	
	@Test
	public void prepend(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.prepend((byte)10);
		
		byte[] expected = new byte[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void prependArray(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.prepend(new byte[]{10,11,12,13});
		
		byte[] expected = new byte[]{10,11,12,13,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	
	@Test
	public void prependOtherGrowableByteArray(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.prepend(new GrowableByteArray(new byte[]{10,11,12,13}));
		
		byte[] expected = new byte[]{10,11,12,13,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendArray(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.append(new byte[]{50,60,70,80,90});
		byte[] expected = new byte[]{10,20,30,40,50,60,70,80,90};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendOtherGrowableByteArray(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.append(new GrowableByteArray(new byte[]{50,60,70,80,90}));
		byte[] expected = new byte[]{10,20,30,40,50,60,70,80,90};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void constructUsingInitialArray(){
		byte[] array = new byte[]{10,20,30,40,50};
		GrowableByteArray sut = new GrowableByteArray(array);
		assertArrayEquals(array, sut.toArray());
	}
	@Test
	public void mixOfAllOperations(){
		byte[] initialArray = new byte[]{10,20,30,40,50};
		GrowableByteArray sut = new GrowableByteArray(initialArray);
		sut.append((byte)60);  //10,20,30,40,50,60
		sut.remove(3);  //10,20,30,50,60
		sut.prepend((byte)5);  //5,10,20,30,50,60
		sut.replace(2, (byte)15); //5,10,15,30,50,60
		sut.insert(3, new byte[]{25,26,27,28,29}); //5,10,15,25,26,27,28,29,30,50,60
		
		sut.reverse();  //60,50,30,29,28,27,26,25,15,10,5
		sut.remove(1);  //60,30,29,28,27,26,25,15,10,5
		sut.remove(Range.of(3,5)); //60,30,29,25,15,10,5
		sut.append((byte)99);
		
		byte[] expected = new byte[]{60,30,29,25,15,10,5,99};
		assertArrayEquals(expected, sut.toArray());
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void constructorWithNegativeSizeShouldThrowException(){
		new GrowableByteArray(-1);
	}
	@Test
	public void constructorWithSizeZeroShouldBeAllowed(){
		GrowableByteArray sut =new GrowableByteArray(0);
		assertEquals(0, sut.getCurrentLength());
		sut.append((byte)10);
		assertEquals(1, sut.getCurrentLength());
		assertEquals(10, sut.get(0));
	}
	@Test(expected = NullPointerException.class)
	public void constructorWithNullArrayShouldThrowException(){
		new GrowableByteArray((byte[])null);
	}
	
	@Test(expected = NullPointerException.class)
	public void constructorWithNullCollectionShouldThrowException(){
		new GrowableByteArray((Collection<Byte>)null);
	}
	@Test(expected = NullPointerException.class)
	public void constructorWithNullElementInCollectionShouldThrowException(){
		List<Byte> list = new ArrayList<Byte>();
		list.add(Byte.valueOf((byte)1));
		list.add(null);
		
		new GrowableByteArray(list);
	}
	@Test
	public void constructorWithCollection(){
		List<Byte> list = Arrays.asList((byte)10,(byte)20,(byte)30,(byte)40,(byte)50);
		GrowableByteArray sut =new GrowableByteArray(list);
		
		byte[] expected = new byte[]{10,20,30,40, 50};
		
		assertArrayEquals(expected, sut.toArray());
	}
	
	
	@Test
	public void copy(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		GrowableByteArray copy = sut.copy();
		
		byte[] expected = new byte[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
		assertArrayEquals(expected, copy.toArray());
	}
	
	@Test
	public void modificationsToCopyShouldNotAffectOriginal(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		GrowableByteArray copy = sut.copy();
		copy.remove(2);

		assertArrayEquals(new byte[]{10,20,30,40}, sut.toArray());
		assertArrayEquals(new byte[]{10,20,40}, copy.toArray());
	}
	@Test
	public void modificationsToOriginalShouldNotAffectCopy(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		GrowableByteArray copy = sut.copy();
		sut.remove(2);

		assertArrayEquals(new byte[]{10,20,30,40}, copy.toArray());
		assertArrayEquals(new byte[]{10,20,40}, sut.toArray());
	}
	
	@Test
	public void reverseEvenNumberOfValues(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		sut.reverse();
		
		assertArrayEquals(new byte[]{40,30,20,10}, sut.toArray());
	}
	@Test
	public void reverseOddNumberOfValues(){
		GrowableByteArray sut = new GrowableByteArray(10);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		sut.append((byte)50);
		sut.reverse();
		
		assertArrayEquals(new byte[]{50,40,30,20,10}, sut.toArray());
	}
	
	@Test
	public void sortUnSortedValues(){
		GrowableByteArray sut = new GrowableByteArray(10);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		sut.append((byte)50);
		sut.reverse();
		sut.sort();
		assertArrayEquals(new byte[]{10,20,30,40,50}, sut.toArray());
	}
	
	@Test
	public void binarySearch(){
		GrowableByteArray sut = new GrowableByteArray(10);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		sut.append((byte)50);
		for(byte i=0; i<sut.getCurrentLength(); i++){
			assertEquals(i, sut.binarySearch(sut.get(i)));
		}
		assertEquals("after all",-6, sut.binarySearch((byte)60));
		assertEquals("before all", -1, sut.binarySearch((byte)6));
		assertEquals("in between", -4, sut.binarySearch((byte)35));
	}
	
	@Test
	public void sortedRemove(){
		GrowableByteArray sut = new GrowableByteArray(10);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		sut.append((byte)50);
		
		
		assertTrue(sut.sortedRemove((byte)30));
		assertFalse(sut.sortedRemove((byte)45));
		assertArrayEquals(new byte[]{10, 20,40,50}, sut.toArray());

	}
	
	@Test
	public void sortedInsert(){
		GrowableByteArray sut = new GrowableByteArray(10);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		sut.append((byte)50);
		
		
		assertEquals(3,sut.sortedInsert((byte)35));
		assertEquals(2,sut.sortedInsert((byte)30));
		assertArrayEquals(new byte[]{10, 20,30,30,35, 40,50}, sut.toArray());

	}
	
	@Test
	public void iterator(){
		GrowableByteArray sut = new GrowableByteArray(new byte[]{10, 20,30,30,35, 40,50});
		
		Iterator<Byte> expected = Arrays.asList((byte)10,(byte) 20,(byte)30,(byte)30,(byte)35,(byte) 40, (byte)50).iterator();
		
		Iterator<Byte> actual = sut.iterator();
		
		while(expected.hasNext()){
			assertTrue(actual.hasNext());
			assertEquals(expected.next(), actual.next());
		}
		assertFalse(actual.hasNext());
	}
	
	
	@Test
	public void sortedInsertSmallerArray(){
		GrowableByteArray sut = new GrowableByteArray(new byte[]{10, 20,30,30,35, 40,50});
		
		sut.sortedInsert(new byte[]{15,17,31,37,60});
		assertArrayEquals(new byte[]{10,15,17, 20,30,30,31, 35, 37, 40,50,60}, sut.toArray());
	}
	@Test
	public void sortedInsertLargerArray(){
		GrowableByteArray sut = new GrowableByteArray(new byte[]{10, 20,30,30,35, 40,50});
		
		sut.sortedInsert(new byte[]{15,17,31,37,60,61,62,63,64,65});
		assertArrayEquals(new byte[]{10,15,17, 20,30,30,31, 35, 37, 40,50,60,61,62,63,64,65}, sut.toArray());
	}
	@Test
	public void sortedInsertSameSizedArray(){
		GrowableByteArray sut = new GrowableByteArray(new byte[]{10, 20,30,30,35, 40,50});
		
		sut.sortedInsert(new byte[]{15,17,31,37,60,61,62});
		assertArrayEquals(new byte[]{10,15,17, 20,30,30,31, 35, 37, 40,50,60,61,62}, sut.toArray());
	}
	
	@Test
	public void sortedInsertEmptyArraShouldMakeNoChanges(){
		GrowableByteArray sut = new GrowableByteArray(new byte[]{10, 20,30,30,35, 40,50});
		
		sut.sortedInsert(new byte[0]);
		assertArrayEquals(new byte[]{10, 20,30,30,35, 40,50}, sut.toArray());
	}
	
	@Test
	public void sortedInsertOnEmptyGrowableArrayShouldAppend(){
		GrowableByteArray sut = new GrowableByteArray(10);
		
		sut.sortedInsert(new byte[]{15,17,31,37,60,61,62});
		assertArrayEquals(new byte[]{15,17,31,37,60,61,62}, sut.toArray());
	}
	
	@Test
	public void getCount(){
		GrowableByteArray sut = new GrowableByteArray(new byte[]{10, 20,30,30,35, 40,50});
		assertEquals(2, sut.getCount((byte)30));
	}
	@Test
	public void getCountNoMatchesShouldReturn0(){
		GrowableByteArray sut = new GrowableByteArray(new byte[]{10, 20,30,30,35, 40,50});
		assertEquals(0, sut.getCount((byte)-1));
	}

	@Test
	public void forEachIndexed(){
		GrowableByteArray sut = new GrowableByteArray(new byte[]{10, 20,30,30,35, 40,50});
		List<Byte> expected = new ArrayList<>();
		for(Byte b : sut){
			expected.add(b);
		}

		List<Byte> actual = new ArrayList<>();
		for(int i=0; i< expected.size(); i++){
			actual.add(null);
		}

		sut.forEachIndexed((i, b)-> actual.set(i, b));

		assertEquals(expected, actual);
	}

	@Test
	public void forEachIndexedRange(){
		GrowableByteArray sut = new GrowableByteArray(new byte[]{10, 20,30,30,35, 40,50});
		List<Byte> expected = new ArrayList<>();
		Range r = Range.of(2,5);
		for(int i=0; i<sut.getCurrentLength(); i++){
			if(r.intersects(Range.of(i))){
				expected.add(sut.get(i));
			}else{
				expected.add(null);
			}

		}


		List<Byte> actual = new ArrayList<>();
		for(int i=0; i< expected.size(); i++){
			actual.add(null);
		}

		sut.forEachIndexed(r, (i, b)-> actual.set(i, b));

		assertEquals(expected, actual);
	}
}
