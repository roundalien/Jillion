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
package org.jcvi.common.core.assembly;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.iter.CloseableIterator;

public class DefaultContig<P extends AssembledRead> extends AbstractContig<P>{

    

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

	public static class Builder extends AbstractContigBuilder<AssembledRead, Contig<AssembledRead>>{
        public Builder(String id, String consensus){
           this(id, new NucleotideSequenceBuilder(consensus).build());
        }
        
        public <R extends AssembledRead, C extends Contig<R>> Builder(C copy){
            this(copy.getId(), copy.getConsensus());
            CloseableIterator<R> iter =null;
            try{
            	 iter = copy.getReadIterator();
            	 while(iter.hasNext()){
            		 R read = iter.next();
            		 addRead(read);
            	 }
            }finally{
            	IOUtil.closeAndIgnoreErrors(iter);
            }
         }
        public Builder(String id, NucleotideSequence consensus){
            super(id,consensus);
        }
        public Builder addRead(String id, int offset,String basecalls){
            return addRead(id, offset, basecalls, Direction.FORWARD);
        }
        public Builder addRead(String id, int offset,String basecalls, Direction dir){
            int numberOfGaps = computeNumberOfGapsIn(basecalls);
            int ungappedLength = basecalls.length()-numberOfGaps;
            return addRead(id, offset, 
            		Range.createOfLength(0,ungappedLength),basecalls, 
            		dir,ungappedLength);
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
        public Builder addRead(String id, int offset,Range validRange, String basecalls, Direction dir, int fullUngappedLength){            
            if(offset <0){
                throw new IllegalArgumentException("circular reads not supported");
                
              }
            super.addRead(id, offset, validRange, basecalls, dir,fullUngappedLength);
            return this;            
        }
        
       
        public DefaultContig<AssembledRead> build(){
            Set<AssembledRead> reads = new LinkedHashSet<AssembledRead>();
            for(PlacedReadBuilder<AssembledRead> builder : getAllPlacedReadBuilders()){
                reads.add(builder.build());
            }
            return new DefaultContig<AssembledRead>(getContigId(), getConsensusBuilder().build(), reads);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        protected PlacedReadBuilder<AssembledRead> createPlacedReadBuilder(
                AssembledRead read) {
            return DefaultPlacedRead.createBuilder(
                    getConsensusBuilder().build(), 
                    read.getId(), 
                    read.getNucleotideSequence().toString(), 
                    (int)read.getGappedStartOffset(), 
                    read.getDirection(), 
                    read.getReadInfo().getValidRange(),
                    (int)read.getReadInfo().getUngappedFullLength());
        }
        /**
        * {@inheritDoc}
        */
        @Override
        protected PlacedReadBuilder<AssembledRead> createPlacedReadBuilder(
                String id, int offset, Range validRange, String basecalls,
                Direction dir, int fullUngappedLength) {
            return DefaultPlacedRead.createBuilder(
                    getConsensusBuilder().build(), 
                    id, 
                    basecalls, 
                    offset, 
                    dir, 
                    validRange,
                    fullUngappedLength);
        }
    }

}
