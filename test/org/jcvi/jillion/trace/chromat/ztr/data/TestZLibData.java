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
/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.ztr.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

import org.jcvi.jillion.internal.trace.chromat.ztr.data.ZLibData;
import org.junit.Before;
import org.junit.Test;
public class TestZLibData {

    byte[] uncompressed = "blahblahblah??".getBytes();
    private byte[] compressed;
    
    @Before
    public void setup(){
    	Deflater compresser = new Deflater();
        compresser.setInput(uncompressed);
        compresser.finish();
        byte[] compressed = new byte[100];
        int compressedDataLength = compresser.deflate(compressed);
        
        ByteBuffer compressedInput= ByteBuffer.allocate(5+compressedDataLength);
        compressedInput.put((byte)2); // use zlib
        compressedInput.put((byte)uncompressed.length);
        compressedInput.put((byte)0);
        compressedInput.put((byte)0);
        compressedInput.put((byte)0);
        
        compressedInput.put(compressed, 0, compressedDataLength);
        this.compressed = Arrays.copyOfRange(compressedInput.array(),0 ,compressedInput.position());
 
    }
    ZLibData sut = ZLibData.INSTANCE;
    @Test
    public void parse() throws IOException{
       
        byte[] actual = sut.parseData(compressed);
        assertTrue(Arrays.equals(actual, uncompressed));
    }
    
    @Test
    public void invalidParseShouldThrowTraceDeocoderException(){
        ByteBuffer compressedInput= ByteBuffer.allocate(5+uncompressed.length);
        compressedInput.put((byte)2); // use zlib
        compressedInput.put((byte)uncompressed.length);
        compressedInput.put((byte)0);
        compressedInput.put((byte)0);
        compressedInput.put((byte)0);
        
        compressedInput.put(uncompressed, 0, uncompressed.length);
        try {
            sut.parseData(compressedInput.array());
            fail("invalid zlib data should throw IOException");
        } catch (IOException e) {
            assertEquals("could not parse ZLibData", e.getMessage());
            assertTrue(e.getCause() instanceof DataFormatException);
        }
    }
    
    @Test
    public void encode() throws IOException{
    	byte[] encodedData = sut.encodeData(uncompressed);
    	assertArrayEquals(encodedData, compressed);
    }

}
