package org.jcvi.common.core.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static org.junit.Assert.*;

import org.easymock.IAnswer;
import org.junit.Test;
import static org.easymock.EasyMock.*;
public class TestIOUtil_copy {

	@Test
	public void copySmallFile() throws IOException{
		String inputString = "this is input/blah";
		InputStream inStream = new ByteArrayInputStream(inputString.getBytes(IOUtil.UTF_8));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		long numberOfBytes =IOUtil.copy(inStream, out);
		String actual = new String(out.toByteArray(), IOUtil.UTF_8);
		assertEquals(inputString, actual);
		assertEquals(out.toByteArray().length, numberOfBytes);
	}
	
	@Test(expected = NullPointerException.class)
	public void nullInputStreamShouldThrowNPE() throws IOException{
		IOUtil.copy(null, new ByteArrayOutputStream());
	}
	@Test(expected = NullPointerException.class)
	public void nullOutputStreamShouldThrowNPE() throws IOException{
		String inputString = "this is input/blah";
		InputStream inStream = new ByteArrayInputStream(inputString.getBytes(IOUtil.UTF_8));
		IOUtil.copy(inStream, null);
	}
	
	@Test
	public void copyLargeFile() throws IOException{
		InputStream in = createMock(InputStream.class);
		OutputStream out = createMock(OutputStream.class);
		
		
		out.write(isA(byte[].class), anyInt(), anyInt());
		expectLastCall().anyTimes();
		
		out.flush();
		expectLastCall().anyTimes();
		
		final LargeCopyHelper helper = new LargeCopyHelper();
		expect(in.available()).andStubAnswer(new IAnswer<Integer>() {

			@Override
			public Integer answer() throws Throwable {
				return helper.available();
			}
			
		});
		
		expect(in.read(isA(byte[].class))).andStubAnswer(new IAnswer<Integer>() {

			@Override
			public Integer answer() throws Throwable {
				return helper.read((byte[])getCurrentArguments()[0]);
			}
			
		});
		
		
		replay(in,out);
		long actualNumberOfBytes = IOUtil.copy(in, out);
		assertEquals(LargeCopyHelper.actualNumberOfBytes, actualNumberOfBytes);
	}
	
	private static class LargeCopyHelper{
		
		public static  long actualNumberOfBytes = ((long)Integer.MAX_VALUE)+1;
		
		private long numberOfBytesLeft= actualNumberOfBytes;
		public int available(){
			if( numberOfBytesLeft >0){
				return (int) Math.min(Integer.MAX_VALUE, numberOfBytesLeft);
			}else{
				return 0;
			}
		}
		
		public int read(byte[] array){
			int arrayLength = array.length;
			if(numberOfBytesLeft > arrayLength){
				numberOfBytesLeft-=arrayLength;
				return arrayLength;
			}
			int returnValue = (int)numberOfBytesLeft;
			numberOfBytesLeft=0;
			return returnValue;
		}
	}
}
