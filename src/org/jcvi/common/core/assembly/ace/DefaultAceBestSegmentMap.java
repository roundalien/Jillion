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
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.util.slice.IdedSlice;
import org.jcvi.common.core.assembly.util.slice.IdedSliceElement;
import org.jcvi.common.core.assembly.util.slice.SliceMap;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;

public class DefaultAceBestSegmentMap implements AceBestSegmentMap {

    private final Map<Range,AceBestSegment> bestSegments = new LinkedHashMap<Range, AceBestSegment>();
    public DefaultAceBestSegmentMap(SliceMap sliceMap, NucleotideSequence consensus){
        if(sliceMap ==null){
            throw new NullPointerException("slice map can not be null");            
        }
        if(consensus ==null){
            throw new NullPointerException("consensus can not be null");
        }
        String currentElement =null;
        int start=0;
        int end=0;
        Range previouslyEnteredRange=null;
        for(int i=0; i<consensus.getLength(); i++){
            IdedSlice slice = sliceMap.getSlice(i);
            Nucleotide consensusCall = consensus.get(i);
            
            //short circuit to try our currentElement first
            //to see if we can extend the current best segment
            if(slice.containsElement(currentElement) &&
                    slice.getSliceElement(currentElement).getBase().equals(consensusCall)){
                end=i;
                continue;
            }
            boolean foundMatch=false;
            for(IdedSliceElement element : slice){
                if(element.getBase().equals(consensusCall)){
                    //we can only get here if element is 
                    //different than currentElement so 
                    //we don't have to check if we match currentelement
                    if( currentElement !=null){
                        previouslyEnteredRange = Range.create(start,end);
                        bestSegments.put(previouslyEnteredRange, new DefaultAceBestSegment(currentElement, previouslyEnteredRange));
                    }
                    currentElement = element.getId();
                    start=i;
                    end=i;
                    foundMatch=true;
                    break;
                }
            }
            if(!foundMatch){
                System.out.println("could not find a match at "+i);
                //keep current element?
            }
        }
        Range lastRange = Range.create(start,end);
        if(!lastRange.equals(previouslyEnteredRange)){       
            bestSegments.put(lastRange, new DefaultAceBestSegment(currentElement, lastRange));
        }
    }
    @Override
    public AceBestSegment getBestSegmentFor(long gappedConsensusOffset) {       
        return getBestSegmentsFor(
                Range.create(gappedConsensusOffset, gappedConsensusOffset))
                .get(0);
    }

    @Override
    public List<AceBestSegment> getBestSegmentsFor(Range gappedConsensusRange) {
        List<AceBestSegment> result = new ArrayList<AceBestSegment>();
        for(Range r : bestSegments.keySet()){
            if(gappedConsensusRange.endsBefore(r)){
                break;
            }
            if(r.intersects(gappedConsensusRange)){
                result.add(bestSegments.get(r));
            }
        }
        return result;
    }

    @Override
    public Iterator<AceBestSegment> iterator() {
        return bestSegments.values().iterator();
    }
    @Override
    public int getNumberOfBestSegments() {
        return bestSegments.size();
    }

}
