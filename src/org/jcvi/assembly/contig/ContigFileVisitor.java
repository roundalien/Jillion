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
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import org.jcvi.Range;
import org.jcvi.assembly.Contig;
import org.jcvi.io.TextFileVisitor;
import org.jcvi.sequence.SequenceDirection;
/**
 * {@code ContigFileVisitor} is a {@link TextFileVisitor}
 * that visits files that contain {@link Contig} data.
 * @author dkatzel
 *
 *
 */
public interface ContigFileVisitor extends TextFileVisitor{
    /**
     * Begin visiting a new contig with the given ID.
     * @param contigId the id of the contig being visited.
     */
    void visitNewContig(String contigId);
    /**
     * Visit a line of basecalls.
     * @param line
     */
    void visitBasecallsLine(String line);
    /**
     * Visit an underlying read for the current contig being visited.
     * @param readId the id of the read.
     * @param offset the start offset of the read (0-based).
     * @param validRange the Range of the read that contains valid basecall data.
     * @param dir the {@link SequenceDirection} of this read.
     */
    void visitNewRead(String readId, int offset, Range validRange, SequenceDirection dir);
}
