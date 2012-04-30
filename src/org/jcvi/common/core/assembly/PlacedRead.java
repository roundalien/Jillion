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
 * Created on Jan 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Rangeable;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.ReferenceEncodedNucleotideSequence;
/**
 * A {@code PlacedRead} is a {@link Read}
 * that has been placed
 * at a particular location in an assembly.
 * The location is specific to the {@link Contig}
 * this read was placed in.
 * @author dkatzel
 */
public interface PlacedRead extends Rangeable{

	/**
     * Get the 0-based, gapped
     * start coordinate of this read.
     * @return the start coordinate as a long.
     */
    long getGappedContigStart();
    /**
     * Get the 0-based, gapped
     * end coordinate of this read.
     * @return the end as a long.
     */
    long getGappedContigEnd();
    /**
     * Get the gapped
     * length of this read.
     * @return the length of this placed object.
     */
    long getGappedLength();

    /**
     * Get the valid {@link Range} which is ungapped "good" part of the basecalls.  Depending
     * on what this {@link NucleotideSequence} represents can change the 
     * meaning of valid range some possible meanings include:
     * <ul>
     * <li>the high quality region<li>
     * <li>the region that aligns to a reference</li>
     * <li>the region used to compute assembly consensus</li>
     * </ul>
     * @return
     */
    Range getValidRange();
    /**
     * Get the {@link Direction} that this read is relative to the contig
     * this read was placed in. If the read is {@link Direction#REVERSE}
     * then the {@link NucleotideSequence} of this read has already been
     * reverse complemented so that it matches the consensus.
     * @return either {@link Direction#FORWARD} or {@link Direction#REVERSE}.
     */
    Direction getDirection();
    /**
     * Convert the given zero based reference offset into the equivalent gapped
     * valid range offset on this read.  For example if this read
     * starts at consensus offset 100 then {@code toGappedValidRangeOffset(100)}
     * will return {@code 0}.
     * 
     * @param referenceOffset the zero based reference offset.
     * @return equivalent gapped
     * valid range offset on this read; this value
     * <strong>may</strong> be negative if the reference offset
     * provided is before the read starts aligning to the contig.
     * @see #toReferenceOffset(long)
     */
    long toGappedValidRangeOffset(long referenceOffset);
    /**
     * Convert the given zero based gapped valid range offset into the equivalent gapped
     * reference offset on the reference.  For example if this read
     * starts at consensus offset 100 then {@code toReferenceOffset(0)}
     * will return {@code 100}.
     * @param gappedValidRangeOffset zero based gapped valid range offset.
     * @return the equivalent zero based gapped reference offset as a long.
     */
    long toReferenceOffset(long gappedValidRangeOffset);
    /**
     * Get the ungapped full length of this read <strong>including bases outside of the valid range</strong>.
     * If this read has any portion of the read that was trimmed off because of bad quality, primer/vector sequence
     * or because it did not fully align to the reference then those portions still counted by this method.
     * @return the full length including bases outside of the valid range; always positive.
     */
    int getUngappedFullLength();
    /**
     * Get the location of this read on its contig
     * as a {@link Range} in gapped contig coordinates.
     * @return a Range; never null.
     */
    Range getGappedContigRange();
    /**
     * Delegates to {@link #getGappedContigRange()}
     * 
     * {@inheritDoc}
     */
    @Override
    Range asRange();
    
    /**
     * Get the id of this read.
     * @return the id as a String; will never be null.
     */
    String getId();
    /**
     * Get the ungapped {@link NucleotideSequence} of this read.
     * @return the {@link NucleotideSequence} of this read; will
     * never be null.
     */
    ReferenceEncodedNucleotideSequence getNucleotideSequence();
}
