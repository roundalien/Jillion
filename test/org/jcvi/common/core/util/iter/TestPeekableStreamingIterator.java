package org.jcvi.common.core.util.iter;


import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestPeekableStreamingIterator {

private final List<String> dwarfs = Arrays.asList("Happy","Sleepy","Dopey","Doc","Bashful","Sneezy","Grumpy");
	
	@Test
	public void noPeeking(){
		
		Iterator<String> expected = dwarfs.iterator();
		PeekableStreamingIterator<String> actual = IteratorUtil.createPeekableStreamingIterator(dwarfs.iterator());
		while(expected.hasNext()){
			assertTrue(actual.hasNext());
			assertEquals(expected.next(), actual.next());
		}
		assertFalse(actual.hasNext());
	}
	
	@Test
	public void withPeeking(){
		Iterator<String> expected = dwarfs.iterator();
		PeekableStreamingIterator<String> actual = IteratorUtil.createPeekableStreamingIterator(dwarfs.iterator());
		while(expected.hasNext()){
			assertTrue(actual.hasNext());			
			String next = expected.next();
			assertEquals(next,actual.peek());
			//can peek multiple times
			assertEquals(next, actual.next());
		}
		assertFalse(actual.hasNext());
	}
	
	@Test
	public void multiplePeeks(){
		Iterator<String> expected = dwarfs.iterator();
		PeekableStreamingIterator<String> actual = IteratorUtil.createPeekableStreamingIterator(dwarfs.iterator());
		while(expected.hasNext()){
			assertTrue(actual.hasNext());			
			String next = expected.next();
			for(int i=0; i<5;i++){
				assertEquals(next,actual.peek());
			}
			assertEquals(next, actual.next());
		}
		assertFalse(actual.hasNext());
	}
	
	@Test
	public void closing() throws IOException{
		
		PeekableStreamingIterator<String> actual = IteratorUtil.createPeekableStreamingIterator(dwarfs.iterator());
		for(int i=0; i<5;i++){
			actual.next();
		}
		actual.close();
		
		assertFalse(actual.hasNext());
		try{
			actual.peek();
			fail("should throw NoSuchElementException if already closed");
		}catch(NoSuchElementException expected){
			
		}
		try{
			actual.next();
			fail("should throw NoSuchElementException if already closed");
		}catch(NoSuchElementException expected){
			
		}
	}
}
