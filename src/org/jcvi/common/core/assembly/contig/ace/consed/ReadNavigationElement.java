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

package org.jcvi.common.core.assembly.contig.ace.consed;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.contig.PlacedRead;

/**
 * {@code ReadNavigationElement} is a {@link NavigationElement}
 * that tells consed how to navigate to a particular feature 
 * of a specific read.
 * @author dkatzel
 *
 *
 */
public class ReadNavigationElement extends AbstractNavigationElement{

    /**
     * Constructs a new {@link ReadNavigationElement}.
     * @param targetId the id of the target of that is to be navigated.
     * @param ungappedPositionRange the ungapped position
     * range of this element; cannot be null.
     * @param comment a comment that describes why this element exists
     * (may be null).
     * @throws NullPointerException if type, targetId or 
     * ungappedPositionRange are null.
     */
    public ReadNavigationElement(String readId,
            Range ungappedPositionRange, String comment) {
        super(Type.READ, readId, ungappedPositionRange, comment);
    }
    /**
     * Constructs a new {@link ReadNavigationElement}.
     * @param targetId the id of the target of that is to be navigated.
     * @param ungappedPositionRange the ungapped position
     * range of this element; cannot be null.
     * @throws NullPointerException if type, targetId or 
     * ungappedPositionRange are null.
     */
    public ReadNavigationElement(String readId,
            Range ungappedPositionRange){
        super(Type.READ, readId, ungappedPositionRange);
    }
    /**
     * Build a new {@link ReadNavigationElement} for the given
     * PlacedRead, that will navigate to the given GAPPED range.  This
     * is a convenience method that handles converting the gapped
     * range into an ungapped range and reverse complimenting required by the consed.
     * This is the same as {@link #buildReadNavigationElement(PlacedRead, Range, int,String)
     * buildReadNavigationElementFrom(read, gappedFeatureValidRange, fullLength,null)}
     * @param read the read to make a {@link ReadNavigationElement}
     * for; cannot be null.
     * @param gappedFeatureRange the gapped feature range coordinates; cannot be null.
     * @return a new ReadNavigationElement.
     * @see #buildReadNavigationElement(PlacedRead, Range, int,String)
     */
    public ReadNavigationElement buildReadNavigationElement(PlacedRead read, 
            Range gappedFeatureValidRange, 
            int fullLength){
        return buildReadNavigationElement(read, gappedFeatureValidRange, fullLength,null);
    }
    /**
     * Build a new {@link ReadNavigationElement} for the given
     * PlacedRead, that will navigate to the given GAPPED range.  This
     * is a convenience method that handles converting the gapped
     * range into an ungapped range and reverse complimenting required by the consed.
     * @param read the read to make a {@link ReadNavigationElement}
     * for; cannot be null.
     * @param gappedFeatureRange the gapped feature range coordinates; cannot be null.
     * @param comment a comment that describes why this element exists
     * (may be null).
     * @return a new ReadNavigationElement.
     */
    public ReadNavigationElement buildReadNavigationElement(PlacedRead read, 
            Range gappedFeatureValidRange, 
            int fullLength,
            String comment){
        Range ungappedRange = AssemblyUtil.convertGappedRangeIntoUngappedRange(read.getNucleotideSequence(), gappedFeatureValidRange);
        if(read.getDirection() == Direction.REVERSE){
            ungappedRange =AssemblyUtil.reverseComplimentValidRange(ungappedRange, fullLength);
        }
        return new ReadNavigationElement(read.getId(), ungappedRange, comment);
    }
   
    
    
}
