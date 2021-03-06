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
package org.jcvi.jillion.assembly.util.slice;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.JoinedStringBuilder;

final class VariableWidthNucleotideSlice implements VariableWidthSlice<Nucleotide, NucleotideSequence>{

	
	private final List<VariableWidthSliceElement<Nucleotide>> list = new ArrayList<VariableWidthSliceElement<Nucleotide>>();
	private final NucleotideSequence gappedReference;
	private VariableWidthNucleotideSlice(Builder builder){
		
		for(Entry<List<Nucleotide>, LongAdder> entry : builder.countMap.entrySet()){
			list.add(new VariableWidthNucleotideSliceElement(entry.getKey(), entry.getValue().intValue()));
		}
		//sort them
		Collections.sort(list);
		
		this.gappedReference = builder.gappedReference;
		
	}
	
	
	@Override
	public NucleotideSequence getGappedReferenceSequence() {
		return gappedReference;
	}



	@Override
	public int getSliceLength() {
		return list.stream().mapToInt(e-> e.getLength()).max().orElse(0);
	}



	@Override
	public int getCoverageDepth() {
		int coverage=0;
		for(VariableWidthSliceElement<Nucleotide> e : list){
			coverage +=e.getCount();
		}
		return coverage;
	}


	public int getCountFor(List<Nucleotide> sliceElementSeq){
		Objects.requireNonNull(sliceElementSeq);
		Optional<VariableWidthSliceElement<Nucleotide>> ret =list.stream()
															.filter(e -> sliceElementSeq.equals(e.get()))
															.findFirst();
		if(ret.isPresent()){
			return ret.get().getCount();
		}
		return 0;
	}

	@Override
	public Stream<? extends VariableWidthSliceElement<Nucleotide>> elements() {
		return list.stream();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + list.hashCode();
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
		if( !(obj instanceof VariableWidthSlice)){
			return false;
		}
		if (obj instanceof VariableWidthNucleotideSlice) {
			VariableWidthNucleotideSlice other = (VariableWidthNucleotideSlice) obj;
			if (!list.equals(other.list)) {
				return false;
			}
			return true;
		}
		VariableWidthSlice<?,?> other = (VariableWidthSlice<?,?>) obj;
		return list.equals(other.elements()
								.map(o-> (VariableWidthSlice<?,?>)o)
								.collect(Collectors.toList()));
	}

	public static class Builder{
		private final int width;
		private final int startOffset;
		private final NucleotideSequence gappedReference;
		
		private final Map<List<Nucleotide>, LongAdder> countMap = new ConcurrentHashMap<>();
		
		public Builder(NucleotideSequence gappedReference) {
			this(gappedReference, 0);
		}
		public Builder(NucleotideSequence gappedReference, int startOffset) {
			Objects.requireNonNull(gappedReference);

			if(gappedReference.getUngappedLength() <0){
				throw new IllegalArgumentException("ungappedWidth must be >=1");
			}
			
			this.width = (int)gappedReference.getLength();
			this.gappedReference = gappedReference;
			this.startOffset = startOffset;
		}

		private void assertNoElementsNull(List<Nucleotide> list){
			for(Nucleotide n : list){
				Objects.requireNonNull(n);
			}
		}
		public Builder add(Iterator<Nucleotide> iter){
			int count=0;
			List<Nucleotide> list = new ArrayList<>(width);
			while(iter.hasNext() && count < width){
				list.add(iter.next());
				count++;
			}
			if(count == width){
				return add(list);
			}
			//else skip
			return this;
		}
		public Builder add(Nucleotide...nucleotides){
			return add(Arrays.asList(nucleotides));
		}
		public Builder add(List<Nucleotide> list){
			if(list.size() != width){
				throw new IllegalArgumentException("width is not length " + width + " : " + list);
			}			
			assertNoElementsNull(list);
			countMap.computeIfAbsent(list, k -> new LongAdder()).increment();
			
			return this;
		}
		
		public VariableWidthNucleotideSlice build(){
			return new VariableWidthNucleotideSlice(this);
		}

		public Builder addMultiple(int i, NucleotideSequence seq) {
			List<Nucleotide> list = new ArrayList<>();
			for(Nucleotide n : seq){
				list.add(n);
			}
			countMap.computeIfAbsent(list, k -> new LongAdder()).add(i);
			return this;
		}

		public void skipBases(int gappedOffset, Iterator<Nucleotide> iter) {
			int numberOfBasesToSkip = width - (gappedOffset - startOffset);
			for(int i=0; iter.hasNext() && i<numberOfBasesToSkip; i++){
				iter.next();
			}
			
		}

		public void addBeginningOfRead(int gappedStartOffset,
				Iterator<Nucleotide> iter) {
			if(startOffset == gappedStartOffset){
				add(iter);
			}else{
				skipBases(gappedStartOffset, iter);
			}
			
		}
	}

	@Override
	public String toString() {
		return JoinedStringBuilder.create(list)
									.prefix("[ ")
									.glue(", ")
									.suffix(" ]")
									.build();
	}

}
