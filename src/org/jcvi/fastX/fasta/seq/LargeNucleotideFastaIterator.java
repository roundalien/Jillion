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

package org.jcvi.fastX.fasta.seq;

import java.io.File;

import org.jcvi.fastX.fasta.AbstractLargeFastaRecordIterator;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

/**
 * @author dkatzel
 *
 *
 */
public class LargeNucleotideFastaIterator extends AbstractLargeFastaRecordIterator<NucleotideEncodedGlyphs, NucleotideSequenceFastaRecord>{

    /**
     * @param fastaFile
     * @param recordFactory
     */
    public LargeNucleotideFastaIterator(File fastaFile) {
        super(fastaFile, DefaultNucleotideFastaRecordFactory.getInstance());
    }

}
