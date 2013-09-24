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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.aa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;
import org.jcvi.jillion.internal.core.util.GrowableByteArray;

public final class AminoAcidSequenceBuilder implements ResidueSequenceBuilder<AminoAcid,AminoAcidSequence>{
	private static final AminoAcid[] AMINO_ACID_VALUES = AminoAcid.values();
	private static final byte GAP_ORDINAL = AminoAcid.Gap.getOrdinalAsByte();
	
	private static final int DEFAULT_CAPACITY = 20;
	private GrowableByteArray builder;
	private int numberOfGaps=0;
	public AminoAcidSequenceBuilder(){
		builder = new GrowableByteArray(DEFAULT_CAPACITY);
	}
	
	public AminoAcidSequenceBuilder(int initialCapacity){
		builder = new GrowableByteArray(initialCapacity);
	}
	public AminoAcidSequenceBuilder(CharSequence sequence){
		builder = new GrowableByteArray(sequence.length());
		append(parse(sequence.toString()));
	}
	public AminoAcidSequenceBuilder(AminoAcidSequence sequence){
		builder = new GrowableByteArray((int)sequence.getLength());
		append(sequence);
	}
	private AminoAcidSequenceBuilder(AminoAcidSequenceBuilder copy){
		builder = copy.builder.copy();
	}
	
	private static List<AminoAcid> parse(String aminoAcids){
		List<AminoAcid> result = new ArrayList<AminoAcid>(aminoAcids.length());
        for(int i=0; i<aminoAcids.length(); i++){
            char charAt = aminoAcids.charAt(i);
            if(!Character.isWhitespace(charAt)){
            	result.add(AminoAcid.parse(charAt));
            }
        }
        return result;
	}
	
	@Override
	public AminoAcidSequenceBuilder append(
			AminoAcid residue) {
		if(residue==AminoAcid.Gap){
			numberOfGaps++;
		}
		builder.append(residue.getOrdinalAsByte());
		return this;
	}

	
	@Override
	public AminoAcidSequenceBuilder clear() {
		numberOfGaps=0;
		builder.clear();
		return this;
	}

	@Override
	public AminoAcidSequenceBuilder append(
			Iterable<AminoAcid> sequence) {
		for(AminoAcid aa : sequence){
			append(aa);
		}
		return this;
	}


	public AminoAcidSequenceBuilder append(
			AminoAcidSequenceBuilder otherBuilder) {
		builder.append(otherBuilder.builder.toArray());
		return this;
	}

	@Override
	public AminoAcidSequenceBuilder append(
			String sequence) {
		return append(parse(sequence));
	}

	@Override
	public AminoAcidSequenceBuilder insert(
			int offset, String sequence) {
		List<AminoAcid> list = parse(sequence);
		byte[] array = new byte[list.size()];
		int i=0;
		for(AminoAcid aa :list){
			if(aa == AminoAcid.Gap){
				numberOfGaps++;
			}
			array[i]=(aa.getOrdinalAsByte());
		}		
		builder.insert(offset, array);
		return this;
	}

	
	@Override
	public AminoAcid get(int offset) {
		return AMINO_ACID_VALUES[builder.get(offset)];
	}

	@Override
	public long getLength() {
		return builder.getCurrentLength();
	}
	@Override
	public long getUngappedLength() {
		return builder.getCurrentLength() - numberOfGaps;
	}
	
	@Override
	public AminoAcidSequenceBuilder replace(
			int offset, AminoAcid replacement) {
		if(AMINO_ACID_VALUES[builder.get(offset)] == AminoAcid.Gap){
			numberOfGaps--;			
		}
		if(replacement == AminoAcid.Gap){
			numberOfGaps++;
		}
		builder.replace(offset, replacement.getOrdinalAsByte());
		return this;
	}

	@Override
	public AminoAcidSequenceBuilder delete(
			Range range) {
		for(AminoAcid aa : asList(range)){
			if(aa == AminoAcid.Gap){
				numberOfGaps --;
			}
		}
		builder.remove(range);
		return this;
	}

	@Override
	public int getNumGaps() {
		return numberOfGaps;
	}

	@Override
	public AminoAcidSequenceBuilder prepend(
			String sequence) {			
		return insert(0, sequence);
	}

	@Override
	public AminoAcidSequenceBuilder insert(
			int offset, Iterable<AminoAcid> sequence) {
		GrowableByteArray temp = new GrowableByteArray(DEFAULT_CAPACITY);
		for(AminoAcid aa :sequence){
			if(aa == AminoAcid.Gap){
				numberOfGaps++;
			}
			temp.append(aa.getOrdinalAsByte());
		}		
		builder.insert(offset, temp);
		return this;
	}

	@Override
	public AminoAcidSequenceBuilder insert(
			int offset,
			ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> otherBuilder) {
		return insert(offset,otherBuilder.toString());
	}

	@Override
	public AminoAcidSequenceBuilder insert(
			int offset, AminoAcid base) {
		if(base == AminoAcid.Gap){
			numberOfGaps++;
		}
		builder.insert(offset, base.getOrdinalAsByte());
		return this;
	}

	@Override
	public AminoAcidSequenceBuilder prepend(
			Iterable<AminoAcid> sequence) {
		return insert(0, sequence);
	}

	@Override
	public AminoAcidSequenceBuilder prepend(
			ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> otherBuilder) {
		return prepend(otherBuilder.toString());
	}

	@Override
	public AminoAcidSequence build() {
		return build(builder.toArray());
	}


	
	private List<AminoAcid> convertFromBytes(byte[] array){
		List<AminoAcid> aas = new ArrayList<AminoAcid>(array.length);
		for(int i=0; i<array.length; i++){
			aas.add(AMINO_ACID_VALUES[array[i]]);
		}
		return aas;
	}
	private AminoAcidSequence build(byte[] seqToBuild){
		List<AminoAcid> asList = convertFromBytes(seqToBuild);
		if(numberOfGaps>0 && hasGaps(asList)){
			return new CompactAminoAcidSequence(asList);
		}
		//no gaps
		
		return new UngappedAminoAcidSequence(asList);
	}
	private boolean hasGaps(List<AminoAcid> asList) {
		for(AminoAcid aa : asList){
			if(aa == AminoAcid.Gap){
				return true;
			}
		}
		return false;
	}

	private List<AminoAcid> asList(Range range) {
		AminoAcidSequence s = build();
		List<AminoAcid> list = new ArrayList<AminoAcid>((int)range.getLength());
		Iterator<AminoAcid> iter = s.iterator(range);
		while(iter.hasNext()){
			list.add(iter.next());
		}
		return list;
	}


	@Override
	public AminoAcidSequenceBuilder trim(Range range) {
		Range intersection = range.intersection(Range.ofLength(getLength()));
		builder =builder.subArray(intersection);		
		this.numberOfGaps =builder.getCount(GAP_ORDINAL);
		return this;
		
		
	}


	@Override
	public AminoAcidSequenceBuilder copy() {
		return new AminoAcidSequenceBuilder(this);
		
	}

	@Override
	public AminoAcidSequenceBuilder reverse() {
		builder.reverse();
		return this;
	}

	@Override
	public AminoAcidSequenceBuilder ungap() {

		AminoAcidSequence list = build(builder.toArray());
		if(list.getNumberOfGaps() !=0){
			List<Integer> gapOffsets =list.getGapOffsets();
			for(int i=gapOffsets.size()-1; i>=0; i--){
				builder.remove(i);
			}
		}
		numberOfGaps=0;
		return this;
	}

	@Override
	public String toString() {
		byte[] array =builder.toArray();
		StringBuilder stringBuilder = new StringBuilder(array.length);
		AminoAcid[] values = AminoAcid.values();
		for(int i=0; i<array.length; i++){
			
			stringBuilder.append(values[array[i]]);
		}
		return stringBuilder.toString();
	}

	@Override
	public Iterator<AminoAcid> iterator() {
		return new IteratorImpl();
	}

	private class IteratorImpl implements Iterator<AminoAcid>{
		private int currentOffset=0;

		@Override
		public boolean hasNext() {
			return currentOffset<builder.getCurrentLength();
		}

		@Override
		public AminoAcid next() {
			AminoAcid next = AMINO_ACID_VALUES[builder.get(currentOffset)];
			currentOffset++;
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			
		}
		
	}
}
