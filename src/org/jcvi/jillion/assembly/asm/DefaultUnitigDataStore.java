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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.asm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.frg.FragmentDataStore;

/**
 * @author dkatzel
 *
 *
 */
public final class DefaultUnitigDataStore{

	private DefaultUnitigDataStore(){
		//private constructor.
	}
    public static UnitigDataStore create(File asmFile, FragmentDataStore frgDataStore) throws IOException{
        UnitigDataStoreBuilder builder = new UnitigDataStoreBuilder(frgDataStore);
        AsmParser.parseAsm(asmFile, builder);
        return builder.build();
    }
    
    
    private static class UnitigDataStoreBuilder extends AbstractAsmVisitor implements Builder<UnitigDataStore>{
        private AsmContigBuilder currentBuilder=null;
        private final FragmentDataStore frgDataStore;
        private final Map<String, AsmUnitig> unitigMap = new HashMap<String, AsmUnitig>();
        private final Map<String, Range> clearRanges = new HashMap<String, Range>();
        
        public UnitigDataStoreBuilder(FragmentDataStore frgDataStore) {
            this.frgDataStore = frgDataStore;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitRead(String externalId, long internalId,
                MateStatus mateStatus, boolean isSingleton, Range clearRange) {
            clearRanges.put(externalId, clearRange);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean visitUnitig(String externalId, long internalId,
                float aStat, float measureOfPolymorphism, UnitigStatus status,
                NucleotideSequence consensusSequence,
                QualitySequence consensusQualities, int numberOfReads) {
            
            currentBuilder =  DefaultAsmContig.createBuilder(externalId, consensusSequence);
            return true;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndOfUnitig() {
            unitigMap.put(currentBuilder.getContigId(),new DefaultAsmUnitig(currentBuilder.build()));
            currentBuilder=null;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitReadLayout(char readType, String externalReadId,
                DirectedRange readRange, List<Integer> gapOffsets) {
            if(currentBuilder !=null){
                //in unitig
                try {
                    NucleotideSequence fullLengthSequence = frgDataStore.get(externalReadId).getNucleotideSequence();
                    Range clearRange = clearRanges.get(externalReadId);
                    if(clearRange==null){
                        throw new IllegalStateException("do not have clear range information for read "+ externalReadId);
                    }
                   
                    NucleotideSequenceBuilder validBases = new NucleotideSequenceBuilder(fullLengthSequence)
                    											.trim(clearRange);
                    if(readRange.getDirection() == Direction.REVERSE){
                        validBases.reverseComplement();
                    }
                    validBases = AsmUtil.computeGappedSequence(validBases, gapOffsets);
                    
                    currentBuilder.addRead(externalReadId, validBases.toString(),
                            (int)readRange.asRange().getBegin(),readRange.getDirection(),
                            clearRange, 
                            (int)fullLengthSequence.getLength(),
                            false);
                } catch (DataStoreException e) {
                    throw new IllegalStateException("error getting read id "+ externalReadId
                           + " from frg file",e);
                }
            }
        }
        @Override
        public UnitigDataStore build(){
            clearRanges.clear();
            DataStore<AsmUnitig> datastore = DataStoreUtil.adapt(unitigMap);
            return new UnitigDataStoreImpl(datastore);
        }
        
        private static class UnitigDataStoreImpl implements UnitigDataStore{
            private final DataStore<AsmUnitig> delegate;

            public UnitigDataStoreImpl(DataStore<AsmUnitig> delegate) {
                this.delegate = delegate;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public StreamingIterator<String> idIterator() throws DataStoreException {
                return delegate.idIterator();
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public AsmUnitig get(String id) throws DataStoreException {
                return delegate.get(id);
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public boolean contains(String id) throws DataStoreException {
                return delegate.contains(id);
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public long getNumberOfRecords() throws DataStoreException {
                return delegate.getNumberOfRecords();
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public boolean isClosed() {
                return delegate.isClosed();
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public void close() throws IOException {
                delegate.close();
                
            }

            /**
            * {@inheritDoc}
             * @throws DataStoreException 
            */
            @Override
            public StreamingIterator<AsmUnitig> iterator() throws DataStoreException {
                return delegate.iterator();
            }
            
            
        }
    }
}