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
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig;

import java.util.LinkedHashSet;
import java.util.Set;


import org.jcvi.assembly.tasm.DefaultTigrAssemblerPlacedRead;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.jcvi.common.core.symbol.residue.nuc.ReferenceEncodedNucleotideSequence;

public class DefaultContig<P extends PlacedRead> extends AbstractContig<P>{

    

    public DefaultContig(String id, NucleotideSequence consensus,
            Set<P> reads) {
        super(id, consensus, reads);
    }
    
    /* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public static class Builder extends AbstractContigBuilder<PlacedRead, DefaultContig<PlacedRead>>{
        public Builder(String id, String consensus){
           this(id, NucleotideSequenceFactory.create(consensus));
        }
        public Builder(String id, NucleotideSequence consensus){
            super(id,consensus);
        }
        public Builder addRead(String id, int offset,String basecalls){
            return addRead(id, offset, basecalls, Direction.FORWARD);
        }
        public Builder addRead(String id, int offset,String basecalls, Direction dir){
            int numberOfGaps = computeNumberOfGapsIn(basecalls);
            return addRead(id, offset, Range.buildRangeOfLength(0,basecalls.length()-numberOfGaps),basecalls, dir);
        }
        /**
         * @param basecalls
         * @return
         */
        private int computeNumberOfGapsIn(String basecalls) {
            int count=0;
            for(int i=0; i<basecalls.length(); i++){
                if(basecalls.charAt(i) == '-'){
                    count++;
                }
            }
            return count;
        }
        @Override
        public Builder addRead(String id, int offset,Range validRange, String basecalls, Direction dir){            
            if(offset <0){
                throw new IllegalArgumentException("circular reads not supported");
                
              }
            super.addRead(id, offset, validRange, basecalls, dir);
            return this;            
        }
        @Override
        protected PlacedRead createPlacedRead(Read<ReferenceEncodedNucleotideSequence> read, long offset, Direction dir,
                int ungappedFullLength,Range validRange){
            return new DefaultPlacedRead(read,offset,dir,ungappedFullLength, validRange);
        }
       
        public DefaultContig<PlacedRead> build(){
            Set<PlacedRead> reads = new LinkedHashSet<PlacedRead>();
            for(PlacedReadBuilder<PlacedRead> builder : getAllPlacedReadBuilders()){
                reads.add(builder.build());
            }
            return new DefaultContig<PlacedRead>(getContigId(), getConsensusBuilder().build(), reads);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        protected PlacedReadBuilder<PlacedRead> createPlacedReadBuilder(
                PlacedRead read) {
            return DefaultPlacedRead.createBuilder(
                    getConsensusBuilder().build(), 
                    read.getId(), 
                    Nucleotides.asString(read.getNucleotideSequence().asList()), 
                    (int)read.getStart(), 
                    read.getDirection(), 
                    read.getValidRange(),
                    //TODO need to actually compute ungapped full length here
                    //should we pull from frg or db?
                    (int)read.getValidRange().getEnd());
        }
        /**
        * {@inheritDoc}
        */
        @Override
        protected PlacedReadBuilder<PlacedRead> createPlacedReadBuilder(
                String id, int offset, Range validRange, String basecalls,
                Direction dir) {
            return DefaultPlacedRead.createBuilder(
                    getConsensusBuilder().build(), 
                    id, 
                    basecalls, 
                    offset, 
                    dir, 
                    validRange,
                    //TODO need to actually compute ungapped full length here
                    //should we pull from frg or db?
                    (int)validRange.getEnd());
        }
    }

}
