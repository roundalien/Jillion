package org.jcvi.common.core.util;

import java.util.ArrayList;
import java.util.Arrays;

import org.jcvi.common.core.Range;
/**
 * A {@code GrowableShortArray} is a utility class
 * that wraps a short array that will dynamically
 * grow as needed when data is
 * appended, inserted, replaced and removed etc.
 * This is similar to an {@link ArrayList}
 * or {@link StringBuilder}
 * for primitive shorts.
 * This class is not Thread-safe.
 * @author dkatzel
 *
 */
public final class GrowableShortArray {
	/**
	 * The current length of valid data
	 * this is not the same as the length
	 * of the short array (capacity) since
	 * there still might be room to grow.
	 * There might even be old data in the array
	 * past current length if the array
	 * has been modified via the {@link #remove(int)}
	 * methods.
	 */
	private int currentLength=0;
	/**
	 * Our actual short array,
	 * the capacity is the size of the array.
	 */
	private short[] data;
	/**
	 * Creates a new {@link GrowableShortArray}
	 * with the given initial capacity.
	 * @param initialCapacity the initial size 
	 * of the backing short array.  When adding
	 * shorts will cause the short array to overflow,
	 * the backing short array will automatically
	 * grow larger.
	 * @throws IllegalArgumentException if initialCapacity is <=0.
	 */
	public GrowableShortArray(int initialCapacity){
		if(initialCapacity <=0){
			throw new IllegalArgumentException("initial capacity should be > 0 :"+initialCapacity);
		}
		data = new short[initialCapacity];		
	}
	/**
	 * Creates a new {@link GrowableShortArray}
	 * where the backing short array is an exact
	 * copy of the input array and the initial
	 * capacity is set to the array length.
	 * This has similar (although optimized)
	 * functionality to
	 * <pre>
	 * short[] shorts = ...
	 * GrowableShortArray gba = new GrowableShortArray(shorts.length);
	 * gba.append(shorts);
	 * </pre>
	 * @param shorts the initial short values to set
	 * to the backing array.
	 * @throws NullPointerException if shorts is null.
	 */
	public GrowableShortArray(short[] shorts){
		data = Arrays.copyOf(shorts, shorts.length);
		currentLength=data.length;
	}
	private GrowableShortArray(GrowableShortArray copy){
		data = Arrays.copyOf(copy.data, copy.data.length);
		currentLength = copy.currentLength;
	}
	/**
	 * Create a new instance of GrowableShortArray
	 * that is an exact copy of this instance.
	 * Any future modifications to either the original
	 * instance or the copy will NOT be reflected 
	 * in the other.
	 * @return
	 */
	public GrowableShortArray copy(){
		return new GrowableShortArray(this);
	}
	private void assertValidOffset(int offset) {
		if (offset <0 || offset >= currentLength){
		    throw new IndexOutOfBoundsException(
			"Index: "+offset+", Size: "+currentLength);
	    }
	}
	private void assertValidRange(Range range) {
		if (range.getBegin()<0 || range.getEnd() >= currentLength){
		    throw new IndexOutOfBoundsException(
			"range: "+range+", array size: "+currentLength);
	    }
	}
	
	public void reverse(){
		int pivotPoint = currentLength/2;
		for(int i=0; i<pivotPoint;i++){
			short temp=data[i];
			int reverseI = currentLength-i-1;
			data[i] = data[reverseI];
			data[reverseI] = temp;
		}
	}
	public int getCurrentLength() {
		return currentLength;
	}

	public void append(short value){
		ensureCapacity(currentLength+1);
		data[currentLength++]=value;
	}
	
	public void append(short[] values){
		ensureCapacity(currentLength+values.length);
		System.arraycopy(values, 0, data, currentLength, values.length);
		currentLength+=values.length;
	}
	public void append(GrowableShortArray other){
		ensureCapacity(currentLength+other.currentLength);
		System.arraycopy(other.data, 0, data, currentLength, other.currentLength);
		currentLength+=other.currentLength;
	}
	public short get(int offset){
		assertValidOffset(offset);
		return data[offset];
	}
	
	public void prepend(short value){
		insert(0,value);
	}
	
	public void prepend(short[] values){
		insert(0,values);
	}
	public void prepend(GrowableShortArray other){
		insert(0,other);
	}
	public void replace(int offset, short value){
		assertValidOffset(offset);
		data[offset]=value;
	}
	public void insert(int offset, short[] values){
		assertValidInsertOffset(offset);
		ensureCapacity(currentLength+values.length);
		System.arraycopy(data, offset, data, offset + values.length,
				currentLength - offset);
		
		System.arraycopy(values, 0, data, offset, values.length);
		currentLength+=values.length;
		
	}
	
	public void insert(int offset, GrowableShortArray other){
		assertValidInsertOffset(offset);
		ensureCapacity(currentLength+other.currentLength);
		System.arraycopy(data, offset, data, offset + other.currentLength,
				currentLength - offset);
		
		System.arraycopy(other.data, 0, data, offset, other.currentLength);
		currentLength+=other.currentLength;
		
	}
	public void insert(int offset, short value){
		assertValidInsertOffset(offset);
		ensureCapacity(currentLength+1);
		System.arraycopy(data, offset, data, offset + 1,
				currentLength - offset);
		data[offset] = value;
		currentLength++;
	}
	private void assertValidInsertOffset(int offset) {
		//inserts allow offset to be length
		if(offset !=currentLength){
			assertValidOffset(offset);
		}
		
		
	}
	public void remove(Range range){
		assertValidRange(range);
		int numMoved = currentLength -(int)range.getBegin()-(int) range.getLength();
		if (numMoved > 0){
			System.arraycopy(data, (int)range.getEnd()+1, 
					data, (int)range.getBegin(),  numMoved);
		}
		currentLength-=(int)range.getLength();    
	}
	public short remove(int offset){
		assertValidOffset(offset);
		short oldValue = data[offset];

		int numMoved = currentLength - offset - 1;
		if (numMoved > 0){
		    System.arraycopy(data, offset+1, data, offset,    numMoved);
		}
		currentLength--;
		return oldValue;
	}
	
	public void ensureCapacity(int minCapacity) {
		int oldCapacity = data.length;
		if (minCapacity > oldCapacity) {
		    //algorithm borrowed from ArrayList
		    int newCapacity = (oldCapacity * 3)/2 + 1;
    	    if (newCapacity < minCapacity){
    	    	newCapacity = minCapacity;
    	    }
            // minCapacity is usually close to size, so this is a win:
            data = Arrays.copyOf(data, newCapacity);
		}
    }
	
	public short[] toArray(){
		return Arrays.copyOf(data,currentLength);
	}
}
