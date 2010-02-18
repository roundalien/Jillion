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
 * Created on Dec 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.consensus;

import org.jcvi.assembly.slice.Slice;
import org.jcvi.glyph.nuc.NucleotideGlyph;
/**
 * {@code ConsensusResult} is the base call
 * and quality value that best represents
 * a particular {@link Slice} in a Contig.
 * 
 * @author dkatzel
 *
 *
 */
public interface ConsensusResult {
    /**
     * The best {@link NucleotideGlyph}
     * represented by the Slice.
     * @return a {@link NucleotideGlyph} will never be null.
     */
    NucleotideGlyph getConsensus();
    /**
     * Return the quality of the consensus.  This number may be
     * in the hundreds or thousands depending on the depth of
     * coverage.
     * @return an int; will always be {@code >= 0}
     */
    int getConsensusQuality();
}
