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
 * Created on Nov 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.cas.CasIdLookup;
import org.jcvi.assembly.cas.read.CasPlacedRead;
import org.jcvi.glyph.nuc.NucleotideSequence;

public class AceContigAdapter implements AceContig{

    private final Contig<CasPlacedRead> delegate;
    private final Map<String, AcePlacedRead> adaptedReads = new HashMap<String, AcePlacedRead>();
    
    /**
     * @param delegate
     */
    public AceContigAdapter(Contig<CasPlacedRead> delegate, Date phdDate,CasIdLookup idLookup) {
        this.delegate = delegate;
        for(CasPlacedRead read : delegate.getPlacedReads()){
            final String readId = read.getId();
            adaptedReads.put(readId, new AcePlacedReadAdapter(read,
                    phdDate, 
                    idLookup.getFileFor(readId),
                    read.getUngappedFullLength()));
        }
    }

    @Override
    public boolean containsPlacedRead(String placedReadId) {
        return delegate.containsPlacedRead(placedReadId);
    }

    @Override
    public NucleotideSequence getConsensus() {
        return delegate.getConsensus();
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public int getNumberOfReads() {
        return delegate.getNumberOfReads();
    }

    @Override
    public AcePlacedRead getPlacedReadById(String id) {
        return adaptedReads.get(id);
    }

    @Override
    public Set<AcePlacedRead> getPlacedReads() {
        return new HashSet<AcePlacedRead>(adaptedReads.values());
    }

   
    
}
