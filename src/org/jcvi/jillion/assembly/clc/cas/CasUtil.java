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
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import org.jcvi.jillion.core.io.IOUtil;
/**
 * {@code CasUtil} is a utility class for dealing with the binary
 * encodings inside a .cas file.
 * @author dkatzel
 *
 *
 */
public final class CasUtil {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    
    private CasUtil(){}
    /**
     * Get the number of bytes required to store the given number.
     * To save space, .cas files use a varible length field to 
     * store counters.  The length of the field depends on the max number
     * to be stored.
     * @param i the number to store.
     * @return the number of bytes needed to store the given 
     * input number as an int (which may be {@code 0}).
     * @throws IllegalArgumentException if {@code i<1}.
     */
    static int numberOfBytesRequiredFor(long i){
        if(i < 1){
            throw new IllegalArgumentException("input number must be > 0 : " + i);
        }
       
        return (int)Math.ceil(Math.log(i)/Math.log(256));
    }
    /**
     * Parse a byte count from the given {@link InputStream}.
     * To save space, CAS files have a variable length field for byte counts
     * which range from 1 to 5 bytes long.
     * @param in the inputstream to read.
     * @return a byte count as a long; should always be >=0.
     * @throws IOException if there is a problem reading from the inputstream
     * @throws NullPointerException if {@code in == null}.
     */
    static long parseByteCountFrom(InputStream in) throws IOException{
        
        int firstByte =in.read();
        if(firstByte<254){
            return firstByte;
        }
        
        if(firstByte ==254){
            //read next 2 bytes
           return readCasUnsignedShort(in);
        }
        return readCasUnsignedInt(in);
    }
    /**
     * parse a CAS encoded String from the given {@link InputStream}.
     * CAS files store strings in Pascal like format with 
     * the number of bytes in the string first, followed by the
     * characters in the string, there is no terminating character.
     * @param in the inputstream to parse.
     * @return the next String in the InputStream.
     * @throws IOException if there is a problem reading the String.
     * @throws NullPointerException if {@code in == null}.
     */
    static String parseCasStringFrom(InputStream in) throws IOException{
        int length = (int)parseByteCountFrom(in);
       
        byte bytes[] = IOUtil.toByteArray(in, length);
        
        return new String(bytes, UTF_8);
        
    }
    /**
     * Read the next unsigned byte in the given inputStream.
     * CAS files are encoded in little endian.
     * @param in the inputstream to parse.
     * @return an unsigned byte as a short.
     * @throws IOException if there is a problem reading the inputStream.
     */
    static short readCasUnsignedByte(InputStream in) throws IOException{
    	return IOUtil.readUnsignedByte(in, ByteOrder.LITTLE_ENDIAN);
     }
    /**
     * Read the next unsigned short in the given inputStream.
     * CAS files are encoded in little endian.
     * @param in the inputstream to parse.
     * @return an unsigned short as an int.
     * @throws IOException if there is a problem reading the inputStream.
     */
    static int readCasUnsignedShort(InputStream in) throws IOException{
    	return IOUtil.readUnsignedShort(in, ByteOrder.LITTLE_ENDIAN);
     }
    /**
     * Read the next unsigned int in the given inputStream.
     * this is the same as {@link #readCasUnsignedInt(InputStream, int)
     * readCasUnsignedInt(in,4)}
     * CAS files are encoded in little endian.
     * @param in the inputstream to parse.
     * @return an unsigned int as an long.
     * @throws IOException if there is a problem reading the inputStream.
     * @see #readCasUnsignedInt(InputStream, int)
     */
    static long readCasUnsignedInt(InputStream in) throws IOException{
       return readCasUnsignedInt(in, 4);
    }
    /**
     * Read the next X bytes as an unsigned int in the given inputStream.
     * CAS files are encoded in little endian.
     * @param in the inputstream to parse.
     * @param numberOfBytesInNumber number of bytes to read from the inputStream.
     * @return an unsigned int as an long.
     * @throws IOException if there is a problem reading the inputStream.
     * @see #readCasUnsignedInt(InputStream, int)
     */
    static long readCasUnsignedInt(InputStream in, int numberOfBytesInNumber) throws IOException{
    	byte[] array =IOUtil.readByteArray(in, numberOfBytesInNumber);
    	array = IOUtil.switchEndian(array);    	
    	return new BigInteger(1,array).longValue();
     }
    /**
     * Read the next unsigned long in the given inputStream.
     * CAS files are encoded in little endian.
     * @param in the inputstream to parse.
     * @return an unsigned long as an {@link BigInteger}; never null.
     * @throws IOException if there is a problem reading the inputStream.
     */
    static BigInteger readCasUnsignedLong(InputStream in) throws IOException{
    	return IOUtil.readUnsignedLong(in,ByteOrder.LITTLE_ENDIAN);
     }
    
    
   

    /**
     * Get the java File object for a filepath in a cas file.
     * @param workingDir the working directory this cas file was
     * created in (usually the same location as the cas file itself);
     * If workingDir is {@code null}, then the working dir is the 
     * current directory.
     * @param filePath the path to the file which may or may not
     * be relative.
     * @return a new File object that represents the file.
     * @throws FileNotFoundException if the file does not exist.
     * @throws NullPointerException if filePath is null.
     */
    public static File getFileFor(File workingDir,String filePath) throws FileNotFoundException {
    	if(filePath ==null){
    		throw new NullPointerException("filePath can not be null");
    	}
        boolean isAbsolutePath = filePath.charAt(0) == File.separatorChar;
        final File dataStoreFile;
        if(isAbsolutePath){
            dataStoreFile = new File(filePath);
        }else{
            dataStoreFile = new File(workingDir, filePath);
        }            
         
        if(!dataStoreFile.exists()){
            throw new FileNotFoundException(dataStoreFile.getAbsolutePath());
        }
        return dataStoreFile;
    }
}
