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
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.consensus;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceElement;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public class MostCommonBasecallConsensusCaller implements ConsensusCaller{

    @Override
    public ConsensusResult callConsensus(Slice slice) {
        if(slice==null){
            return new DefaultConsensusResult(NucleotideGlyph.Unknown, 0);
        }
        Map<NucleotideGlyph, Integer> histogramMap = new EnumMap<NucleotideGlyph, Integer>(NucleotideGlyph.class);
        Map<NucleotideGlyph, Integer> qualitySums = new EnumMap<NucleotideGlyph, Integer>(NucleotideGlyph.class);
        for(SliceElement sliceElement : slice){
            NucleotideGlyph base =sliceElement.getBase();
            if(!qualitySums.containsKey(base)){
                qualitySums.put(base, Integer.valueOf(0));
            }
            qualitySums.put(base, qualitySums.get(base) + sliceElement.getQuality().getNumber().intValue());
            incrementHistogram(histogramMap, base);
        }
        NucleotideGlyph consensus= findMostOccuringBase(histogramMap);
        int sum=0;
        for(Entry<NucleotideGlyph, Integer> entry : qualitySums.entrySet()){
            if(entry.getKey() == consensus){
                sum+= entry.getValue();
            }
            else{
                sum -= entry.getValue();
            }
        }
        return new DefaultConsensusResult(consensus, sum);
    }

    private void incrementHistogram(Map<NucleotideGlyph, Integer> histogramMap,
            NucleotideGlyph base) {
        if(!histogramMap.containsKey(base)){
            histogramMap.put(base, Integer.valueOf(0));
        }
        histogramMap.put(base, Integer.valueOf(histogramMap.get(base).intValue()+1));
    }

    private NucleotideGlyph findMostOccuringBase(Map<NucleotideGlyph, Integer> histogramMap){
        int max=-1;
        NucleotideGlyph mostOccuringBase = NucleotideGlyph.Unknown;
        for(Entry<NucleotideGlyph, Integer> entry : histogramMap.entrySet()){
            int value = entry.getValue();
            if(value > max){
                max = value;
                mostOccuringBase = entry.getKey();
            }
        }
        return mostOccuringBase;
    }
}
