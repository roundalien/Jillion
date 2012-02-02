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
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.residue.nuc;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.util.CommonUtil;

final class DefaultReferenceEncodedNucleotideSequence extends AbstractNucleotideSequence implements ReferenceEncodedNucleotideSequence{

	private static final int BITS_PER_SNP_VALUE=4;
    private NucleotideSequence beforeValues=null;
    private NucleotideSequence afterValues=null;
    private int overhangOffset=0;
    private final int length;
    private final int startOffset;
    private final NucleotideSequence reference;
    private final byte[] encodedSnpsInfo;
    
    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfBasesBeforeReference() {
        return beforeValues==null?0 : (int)beforeValues.getLength();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfBasesAfterReference() {
        return afterValues==null?0 : (int)afterValues.getLength();
    }

    @Override
    public List<Integer> getSnpOffsets() {
        ByteBuffer buf = ByteBuffer.wrap(encodedSnpsInfo);
        ValueSizeStrategy numSnpsSizeStrategy = ValueSizeStrategy.values()[buf.get()];
		int size = numSnpsSizeStrategy.getNextValue(buf);
        ValueSizeStrategy snpSizeStrategy = ValueSizeStrategy.values()[buf.get()];
        List<Integer> snps = new ArrayList<Integer>(size);
        for(int i=0; i<size; i++){
            snps.add(snpSizeStrategy.getNextValue(buf));
        }
        return snps;
    }

    public DefaultReferenceEncodedNucleotideSequence(NucleotideSequence reference,
            String toBeEncoded, int startOffset){
        List<Integer> tempGapList = new ArrayList<Integer>();     
        this.startOffset = startOffset;
        this.length = toBeEncoded.length();
        this.reference = reference;
        TreeMap<Integer, Nucleotide> differentGlyphMap = populateFields(reference, toBeEncoded, startOffset, tempGapList);
        
        int numSnps = differentGlyphMap.size();
        final ValueSizeStrategy snpSizeStrategy;
        if(numSnps ==0){
        	snpSizeStrategy = ValueSizeStrategy.NONE;
        }else{
        	snpSizeStrategy = ValueSizeStrategy.getStrategyFor(differentGlyphMap.lastKey().intValue());
        }
        int bufferSize = computeSnpBufferSize(numSnps,snpSizeStrategy);
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        ValueSizeStrategy numSnpsStrategy = ValueSizeStrategy.getStrategyFor(bufferSize);
        buffer.put((byte)numSnpsStrategy.ordinal());
		numSnpsStrategy.putNextValue(buffer, numSnps);
        
        buffer.put((byte)snpSizeStrategy.ordinal());
        
        for(Integer offset : differentGlyphMap.keySet()){
        	snpSizeStrategy.putNextValue(buffer, offset.intValue());
        }
        BitSet bits = new BitSet(BITS_PER_SNP_VALUE*numSnps);
        int i=0;
        for(Nucleotide n : differentGlyphMap.values()){
        	
        	byte ordinal = n.getOrdinalAsByte();
			BitSet temp = IOUtil.toBitSet(ordinal);
			for(int j =0; j< BITS_PER_SNP_VALUE; j++){
				if(temp.get(j)){
					bits.set(i+j);
				}
			}
        	
        	i+=BITS_PER_SNP_VALUE;
            
        }
        byte[] byteArray = IOUtil.toByteArray(bits);
        try{
		buffer.put(byteArray);
        }catch(Exception e){
        	throw new RuntimeException(e);
        }
        encodedSnpsInfo = buffer.array();
    }
    
    
    private int computeSnpBufferSize(int numSnps,ValueSizeStrategy snpSizeStrategy) {
    	int numBytesPerSnpIndex = snpSizeStrategy.getNumberOfBytesPerValue();
    	int numBitsRequiredToStoreSnp = (numSnps+1)/2;
    	int numberOfBytesToStoreSnpOffsets=numBytesPerSnpIndex*numSnps;
    	int numBytesForLength=getNumberOfBytesFor(numSnps);
    	return numBytesForLength+2+numberOfBytesToStoreSnpOffsets + numBitsRequiredToStoreSnp;
	}

	private int getNumberOfBytesFor(int numSnps) {
		return ValueSizeStrategy.getStrategyFor(numSnps).getNumberOfBytesPerValue();
	}

	private TreeMap<Integer, Nucleotide> populateFields(Sequence<Nucleotide> reference,
            String toBeEncoded, int startOffset, List<Integer> tempGapList) {
        handleBeforeReference(toBeEncoded, startOffset);
        handleAfterReference(reference, toBeEncoded, startOffset);
        TreeMap<Integer, Nucleotide> differentGlyphMap = new TreeMap<Integer, Nucleotide>();
        
        int startReferenceEncodingOffset = computeStartReferenceEncodingOffset();
        int endReferenceEncodingOffset = computeEndReferenceEncodingOffset(toBeEncoded);
        
        for(int i=startReferenceEncodingOffset; i<endReferenceEncodingOffset; i++){
            //get the corresponding index to this reference
            int referenceIndex = i + startOffset;
            Nucleotide g = Nucleotide.parse(toBeEncoded.charAt(i));
            final Nucleotide referenceGlyph = reference.get(referenceIndex);            
            
            final Integer indexAsInteger = Integer.valueOf(i);
            if(g.isGap()){
                tempGapList.add(indexAsInteger);
            }
            if(isDifferent(g, referenceGlyph)){
                    differentGlyphMap.put(indexAsInteger, g);
            }
        }
        return differentGlyphMap;
    }

    private int computeStartReferenceEncodingOffset(){
        return beforeValues==null?0: (int)beforeValues.getLength();
    }

    private int computeEndReferenceEncodingOffset(String toBeEncoded){
        return afterValues==null?toBeEncoded.length(): overhangOffset;
    }
    private void handleAfterReference(Sequence<Nucleotide> reference,
            String toBeEncoded, int startOffset) {
        int lastOffsetOfSequence = toBeEncoded.length()+startOffset;
        if(lastOffsetOfSequence > reference.getLength()){
            int overhang = (int)(toBeEncoded.length()+startOffset - reference.getLength());
            overhangOffset = toBeEncoded.length()-overhang;
            afterValues = DefaultNucleotideSequence.create(toBeEncoded.substring(overhangOffset));
        }
    }


    private void handleBeforeReference(String toBeEncoded, int startOffset) {
        if(startOffset<0){
            //handle before values
            beforeValues = DefaultNucleotideSequence.create(toBeEncoded.substring(0, Math.abs(startOffset)));
        }
    }

    private boolean isDifferent(Nucleotide g, final Nucleotide referenceGlyph) {
        return g!=referenceGlyph;
    }

    @Override
    public List<Nucleotide> asList() {
        List<Nucleotide> result = new ArrayList<Nucleotide>(length);
        for(int i=0; i< length; i++){
            result.add(get(i));
        }
        return result;
    }
    @Override
    public Nucleotide get(int index) {
        if(isBeforeReference(index)){
            return beforeValues.get(index);
        }
        if(isAfterReference(index)){
            return afterValues.get(index-overhangOffset);
        }
        
        ByteBuffer buf = ByteBuffer.wrap(encodedSnpsInfo);
        
        ValueSizeStrategy numSnpsSizeStrategy = ValueSizeStrategy.values()[buf.get()];
		int size = numSnpsSizeStrategy.getNextValue(buf);
        ValueSizeStrategy sizeStrategy = ValueSizeStrategy.values()[buf.get()];
        int from = numSnpsSizeStrategy.getNumberOfBytesPerValue()+2+size*sizeStrategy.getNumberOfBytesPerValue();
        byte[] snpSubArray = Arrays.copyOfRange(encodedSnpsInfo, from, encodedSnpsInfo.length);
        BitSet bits = IOUtil.toBitSet(snpSubArray);
        for(int i=0; i<size; i++){        	
            int nextValue = sizeStrategy.getNextValue(buf);
			if(index ==nextValue){            	
				return getSnpValueFrom(bits, i);
            }
        }
        
        int referenceIndex = index+startOffset;
        return reference.get(referenceIndex);
    }

	private Nucleotide getSnpValueFrom(BitSet bits, int offset) {
		int i = offset*BITS_PER_SNP_VALUE;
		byte[] byteArray = IOUtil.toByteArray(bits.get(i, i+BITS_PER_SNP_VALUE));
		final int ordinal;
		if(byteArray.length==0){
			ordinal=0; 
		}else{
			ordinal =new BigInteger(byteArray).intValue();
		}
		
		
		return Nucleotide.values()[ordinal];
	}


    private boolean isAfterReference(int index) {
        return afterValues !=null && index >=overhangOffset;
    }


    private boolean isBeforeReference(int index) {
        return beforeValues!=null && beforeValues.getLength()>index;
    }

    @Override
    public boolean isGap(int index) {
        return getGapOffsets().contains(Integer.valueOf(index));
        
    }
    
    @Override
    public long getLength() {
        return length;
    }

    @Override
    public List<Integer> getGapOffsets() {
      //first, get gaps from our aligned section of the reference
        //we may have a snp in the gap location
        //so we need to check for that
       
        List<Integer> refGapOffsets = reference.getGapOffsets();
        List<Integer> gaps = new ArrayList<Integer>(refGapOffsets.size());
        for(Integer refGap : refGapOffsets){
            int adjustedCoordinate = refGap.intValue() - startOffset;
            if(adjustedCoordinate >=0 && adjustedCoordinate<length){
                gaps.add(Integer.valueOf(adjustedCoordinate));
            }
        }
        //now check our snps to see
        //1. if we have snp where the ref has a gap
        //2. if we have gap
        ByteBuffer buf = ByteBuffer.wrap(encodedSnpsInfo);
        int size = ValueSizeStrategy.values()[buf.get()].getNextValue(buf);
        ValueSizeStrategy sizeStrategy = ValueSizeStrategy.values()[buf.get()];
        List<Integer> snps = new ArrayList<Integer>(size);
        for(int i=0; i<size; i++){
            Integer snpOffset = sizeStrategy.getNextValue(buf);
            //if we have a snp where 
            //the reference has a gap
            //remove it from our list of gaps
            if(gaps.contains(snpOffset)){
                gaps.remove(snpOffset);
            }
            snps.add(snpOffset);
        }
        if(buf.hasRemaining()){
        	int numBytesRemaining =buf.remaining();
        	
        	 byte[] snpSubArray = Arrays.copyOfRange(encodedSnpsInfo, encodedSnpsInfo.length- numBytesRemaining, encodedSnpsInfo.length);
             BitSet bits = IOUtil.toBitSet(snpSubArray);
             for(int i=0; i<size; i++){
            	 if(Nucleotide.Gap == getSnpValueFrom(bits, i)){
            		 gaps.add(snps.get(i));
            	 }
             }
            
        }
        //sort gaps so they are in order
        //before this line, our gaps are in
        //sorted ref gaps
        //followed by sorted snps which happen to be gaps
        Collections.sort(gaps);
        return gaps;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        
        result = prime * result + reference.hashCode();
        result = prime * result + length;
        result = prime * result + startOffset;
        result = prime * result + Arrays.hashCode(encodedSnpsInfo);
        result = prime * result + (beforeValues==null ? 0 : beforeValues.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultReferenceEncodedNucleotideSequence)) {
            return false;
        }
        DefaultReferenceEncodedNucleotideSequence other = (DefaultReferenceEncodedNucleotideSequence) obj;
       
        if(!reference.equals(other.reference)){
            return false;
        }
        if (length != other.length) {
            return false;
        }
        if (startOffset != other.startOffset) {
            return false;
        }
        if (!Arrays.equals(encodedSnpsInfo,other.encodedSnpsInfo)) {
            return false;
        }
        if(!CommonUtil.similarTo(beforeValues, other.beforeValues)){
            return false;
        }
        if(!CommonUtil.similarTo(afterValues, other.afterValues)){
            return false;
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
     @Override
     public int getNumberOfGaps() {
         return getGapOffsets().size();
     }
    
    
     @Override
     public String toString(){
         return Nucleotides.asString(asList());
     }
    /**
     * Helper class to support variable
     * number of bytes being stored
     * based on the largest possible value.
     * Stores everything as unsigned
     * for twice the saving.
     * @author dkatzel
     *
     */
     private enum ValueSizeStrategy{
    	 
    	 NONE{

			@Override
			int getNumberOfBytesPerValue() {
				return 0;
			}

			@Override
			int getNextValue(ByteBuffer buf) {
				return 0;
			}

			@Override
			void putNextValue(ByteBuffer buf, int value) {			
			}
    		 
    	 },
    	 /**
    	  * Reads/writes unsigned bytes.
    	  */
    	 BYTE{
    		@Override
 			int getNumberOfBytesPerValue() {
 				return 1;
 			}

			@Override
			int getNextValue(ByteBuffer buf) {
				return IOUtil.convertToUnsignedByte(buf.get());
			}

			@Override
			void putNextValue(ByteBuffer buf, int value) {
				buf.put(IOUtil.convertUnsignedByteToByteArray((short)value));

			}
    		 
    	 },
    	 /**
    	  * Reads/writes unsigned shorts.
    	  */
    	 SHORT{
    		 @Override
  			int getNumberOfBytesPerValue() {
  				return 2;
  			}

 			@Override
 			int getNextValue(ByteBuffer buf) {
 				return IOUtil.convertToUnsignedShort(buf.getShort());
 			}

 			@Override
 			void putNextValue(ByteBuffer buf, int value) {
 				
 				buf.put(IOUtil.convertUnsignedShortToByteArray(value));
 				
 			}
    	 },
    	 /**
    	  * Reads/writes unsigned ints.
    	  */
    	 INT{
    		 @Override
  			int getNumberOfBytesPerValue() {
  				return 4;
  			}

 			@Override
 			int getNextValue(ByteBuffer buf) { 				
 				return buf.getInt();
 			}

 			@Override
 			void putNextValue(ByteBuffer buf, int value) {
 				buf.putInt(value);
 			}
    	 },
    	 ;
    	 
    	 abstract int getNumberOfBytesPerValue();
    	 
    	 abstract int getNextValue(ByteBuffer buf);
    	 
    	 abstract void putNextValue(ByteBuffer buf, int value);
    	 
    	
    	 
    	 public static ValueSizeStrategy getStrategyFor(int largestValue){
    		 //used unsigned values for twice the storage space
    		 //since these values will always be positive.
    		 if(largestValue <= 0xFF){
    			 return BYTE;
    		 }
    		 if(largestValue <= 0xFFFF){
    			 return SHORT;
    		 }
    		 return INT;
    	 }
    	
     }
}
