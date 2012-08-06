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
 * Created on Nov 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly;

import java.util.List;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
/**
 * {@code AssemblyUtil} is a utility class for working
 * with {@link AssembledRead}s and gapped {@link NucleotideSequence}.
 * @author dkatzel
 */
public final class AssemblyUtil {

    private AssemblyUtil(){}
    /**
     * Create a List of {@link Nucleotide}s that corresponds to the gapped full range
     * (untrimmed, uncomplemented, gapped) version of the given PlacedRead.
     * This method is equivalent to 
     * {@link #buildGappedComplementedFullRangeBases(NucleotideSequence, Direction, Range, List)
     * buildGappedComplimentedFullRangeBases(placedRead.getEncodedGlyphs(), placedRead.getSequenceDirection(), placedRead.getValidRange(), ungappedUncomplementedFullRangeBases)}
     * @param placedRead the read to work on.
     * @param ungappedUncomplementedFullRangeBases the ungapped uncomplemented
     * full (raw) version of the basecalls as originally called from the sequencer.
     * @return a new List of {@link Nucleotide}s of the gapped, untrimmed uncomplemented
     * basecalls of the given read.
     * @see #buildGappedComplementedFullRangeBases(NucleotideSequence, Direction, Range, List)
     */
    public static NucleotideSequence buildGappedComplementedFullRangeBases(AssembledRead placedRead, NucleotideSequence ungappedUncomplementedFullRangeBases){
        Direction dir =placedRead.getDirection();
        Range validRange = placedRead.getReadInfo().getValidRange();
        NucleotideSequenceBuilder ungappedFullRangeComplimentedBuilder = new NucleotideSequenceBuilder(ungappedUncomplementedFullRangeBases);
        if(dir==Direction.REVERSE){
            validRange = AssemblyUtil.reverseComplementValidRange(
                    validRange,
                    ungappedUncomplementedFullRangeBases.getLength());
            ungappedFullRangeComplimentedBuilder.reverseComplement();
            
        }
        NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder((int)ungappedFullRangeComplimentedBuilder.getLength());
        builder.append(ungappedFullRangeComplimentedBuilder.copy().trim(Range.createOfLength(validRange.getBegin())));
        //need to use read's sequence since that might be gapped
        builder.append(placedRead.getNucleotideSequence());
        builder.append(ungappedFullRangeComplimentedBuilder.copy().trim(Range.create(validRange.getEnd()+1, ungappedFullRangeComplimentedBuilder.getLength() -1)));
     
        return builder.build();
    }
    
    
    /**
     * Reverse Compliment the given validRange with regards to its fullLength.
     * @param validRange the valid Range to reverseCompliment.
     * @param fullLength the full length of the untrimmed basecalls.
     * @return a new Range that corresponds to the reverse complemented valid range.
     * @throws IllegalArgumentException if valid range is larger than fullLength
     * @throws NullPointerException if validRange is null.
     */
    public static Range reverseComplementValidRange(Range validRange, long fullLength){
        if(validRange ==null){
            throw new NullPointerException("valid range can not be null");
        }
        if(fullLength < validRange.getLength()){
            throw new IllegalArgumentException(
                    String.format("valid range  %s is larger than fullLength %d", validRange, fullLength));
        }
        long newStart = fullLength - validRange.getEnd()-1;
        long newEnd = fullLength - validRange.getBegin()-1;
        return Range.create(newStart, newEnd);
    }
    /**
     * Convert the given gapped valid range offset of a given read into its
     * corresponding ungapped full length (untrimmed) equivalent.
     * @param placedRead the read
     * @param ungappedFullLength the ungapped full length of the untrimmed (raw) read.
     * @param gappedOffset the gapped offset to convert into an ungapped full range offset
     * @return the ungapped full range offset as a positive int.
     */
    public static  int convertToUngappedFullRangeOffset(AssembledRead placedRead, int ungappedFullLength,int gappedOffset) {
        Range validRange = placedRead.getReadInfo().getValidRange();
        return convertToUngappedFullRangeOffset(placedRead, ungappedFullLength,
                gappedOffset, validRange);
    }
    public static  int convertToUngappedFullRangeOffset(AssembledRead placedRead, int gappedOffset) {
        Range validRange = placedRead.getReadInfo().getValidRange();
        return convertToUngappedFullRangeOffset(placedRead, placedRead.getReadInfo().getUngappedFullLength(),
                gappedOffset, validRange);
    }
    
    private static int convertToUngappedFullRangeOffset(AssembledRead placedRead,
            int fullLength, int gappedOffset, Range validRange) {
       
        
        NucleotideSequence nucleotideSequence = placedRead.getNucleotideSequence();
        if(placedRead.getDirection() == Direction.REVERSE){
            int ungappedOffset=nucleotideSequence.getUngappedOffsetFor(gappedOffset);
            int numberOfLeadingBasesTrimmed = fullLength-1 - (int)validRange.getEnd();
            return numberOfLeadingBasesTrimmed + ungappedOffset;
        }        
        int ungappedValidRangeIndex =  nucleotideSequence.getUngappedOffsetFor(gappedOffset);
        return ungappedValidRangeIndex + (int)validRange.getBegin();
    }
   
    /**
     * Get the first non-gap {@link Nucleotide} from the left side of the given
     * gappedReadIndex on the given encoded glyphs.  If the given base is not a gap, 
     * then that is the value returned.
     * @param gappedNucleotides the gapped nucleotides to search 
     * @param gappedReadIndex the gapped offset (0-based) to start the search from.
     * @return the first non-gap position on the placedRead that is {@code <= gappedReadIndex};
     * may be negative if the sequence starts with gaps.
     */
    public static int getLeftFlankingNonGapIndex(NucleotideSequence gappedNucleotides, int gappedReadIndex) {
        if(gappedReadIndex< 0){
            return gappedReadIndex;
        }
        if(gappedNucleotides.isGap(gappedReadIndex)){
            return getLeftFlankingNonGapIndex(gappedNucleotides,gappedReadIndex-1);
        }
        
        return gappedReadIndex;
    }
    
    
    /**
     * Get the first non-gap {@link Nucleotide} from the right side of the given
     * gappedOffset on the given encoded glyphs.  If the given base is not a gap, 
     * then that is the value returned.
     * @param sequence the gapped {@link NucleotideSequence} to search 
     * @param gappedOffset the gapped offset (0-based) to start the search from.
     * @return the first non-gap position on the placedRead that is {@code >= gappedReadIndex}
     */
    public static int getRightFlankingNonGapIndex(NucleotideSequence sequence, int gappedOffset) {
        if(gappedOffset > sequence.getLength() -1){
            return gappedOffset;
        }
        if(sequence.isGap(gappedOffset)){
            return getRightFlankingNonGapIndex(sequence,gappedOffset+1);
        }
        return gappedOffset;
    }
    /**
     * Get the corresponding ungapped Range (where the start and end values
     * of the range are in ungapped coordinate space) for the given
     * gapped {@link NucleotideSequence} and gapped {@link Range}.
     * @param gappedSequence the gapped {@link NucleotideSequence} needed
     * to compute the ungapped coordinates from.  The resulting
     * ungapped range will be in the same coordinate system that the
     * input gapped range is in.
     * @param gappedRange the Range of gapped coordinates.
     * @return a new Range in the same coordinate system as the input
     * range; never null.
     * @throws NullPointerException if either argument is null.
     */
    public static Range toUngappedRange(final NucleotideSequence gappedSequence,
            Range gappedRange) {
        if(gappedSequence ==null){
            throw new NullPointerException("gapped sequence can not be null");
        }
        if(gappedRange ==null){
            throw new NullPointerException("gappedFeatureValidRange can not be null");
        }
        return Range.create(
                gappedSequence.getUngappedOffsetFor((int)gappedRange.getBegin()),
                gappedSequence.getUngappedOffsetFor((int)gappedRange.getEnd())
                );
        
    }
}
