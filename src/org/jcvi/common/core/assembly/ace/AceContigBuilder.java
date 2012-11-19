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

package org.jcvi.common.core.assembly.ace;

import java.util.Collection;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ContigBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.Builder;

/**
 * {@code AceContigBuilder} is a {@link Builder}
 * for {@link AceContig}s that allows
 * creating a contig object by adding/removing/modifying
 *  {@link AceAssembledRead}s
 * and modifying/recalling a consensus sequence.  
 * An {@link AceContigBuilder}
 * can be used to create AceContig objects that 
 * have been created by an assembler or can be used
 * to create contigs from the scratch.
 * @author dkatzel
 *
 *
 */
public interface AceContigBuilder extends ContigBuilder<AceAssembledRead,AceContig>{
    /**
     * Add a read to this contig with the given values.  This read
     * can later get modified via the {@link #getAssembledReadBuilder(String)}.
     * @param readId the Id this read should have
     * @param validBases the gapped bases of this read that align (however well/badly)
     * to this contig and will be used as underlying sequence data for this contig.
     * @param offset the gapped start offset of this read into the contig
     * consensus.
     * @param dir the {@link Direction} of this read.
     * @param clearRange the ungapped clear range of the valid bases
     * relative to the full length non-trimmed raw full length
     * read from the sequence machine.
     * @param phdInfo the {@link PhdInfo} object for this read.
     * @param ungappedFullLength the ungapped full length
     * non-trimmed raw full length
     * read from the sequence machine.
     * @return this.
     */
    AceContigBuilder addRead(String readId, NucleotideSequence validBases, int offset,
            Direction dir, Range clearRange, PhdInfo phdInfo,
            int ungappedFullLength);
    /**
     * 
    * {@inheritDoc}
    * <p/>
    * Get the {@link AceAssembledReadBuilder} for the given read id.
     */
    @Override
    AceAssembledReadBuilder getAssembledReadBuilder(String readId);
    
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    Collection<AceAssembledReadBuilder> getAllAssembledReadBuilders();

    /**
     * Set this contig as being complemented.
     * @param complemented {@code true} 
     * if this contig should be considered complemented;
     * {@code false} otherwise.
     * @return this
     */
    AceContigBuilder setComplemented(boolean complemented);
}