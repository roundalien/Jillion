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
 * Created on Mar 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.symbol.pos.SangerPeak;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.Nucleotides;

public class DefaultPhd implements Phd {

    private final String id;
    private final NucleotideSequence basecalls;
    private final QualitySequence qualities;
    private final SangerPeak peaks;
    private final Properties comments;
    private final List<PhdTag> tags;
    
    public DefaultPhd(String id, NucleotideSequence basecalls,
            QualitySequence qualities,
            SangerPeak peaks, Properties comments,
            List<PhdTag> tags){
    	this.id = id;
        this.basecalls = basecalls;
        this.qualities = qualities;
        this.peaks = peaks;
        this.comments = comments;
        this.tags = tags;
    }
    public DefaultPhd(String id, NucleotideSequence basecalls,
            QualitySequence qualities,
            SangerPeak peaks,Properties comments){
        this(id,basecalls, qualities, peaks, comments,Collections.<PhdTag>emptyList());
    }
    public DefaultPhd(String id, NucleotideSequence basecalls,
            QualitySequence qualities,
            SangerPeak peaks){
        this(id,basecalls, qualities, peaks, new Properties());
    }
    
    @Override
    public Properties getComments() {
        return comments;
    }

    @Override
    public SangerPeak getPeaks() {
        return peaks;
    }

    @Override
    public NucleotideSequence getNucleotideSequence() {
        return basecalls;
    }

    @Override
    public QualitySequence getQualitySequence() {
        return qualities;
    }

	@Override
	public String getId() {
		return id;
	}
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id.hashCode();
        result = prime * result
                + basecalls.asList().hashCode();
        result = prime * result
                + comments.hashCode();
        result = prime * result + peaks.hashCode();
        result = prime * result
                + qualities.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (!(obj instanceof Phd)){
            return false;
        }
        Phd other = (Phd) obj;
        if(!id.equals(other.getId())){
        	return false;
        }
       if (!basecalls.asList().equals(other.getNucleotideSequence().asList())){
            return false;
       }
        if (!comments.equals(other.getComments())){
            return false;
        }
        if (!peaks.getData().asList().equals(other.getPeaks().getData().asList())){
            return false;
        }
        if (!qualities.asList().equals(other.getQualitySequence().asList())){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return Nucleotides.asString(basecalls.asList());
    }

    @Override
    public int getNumberOfTracePositions() {
        Sequence<ShortSymbol> encodedPeaks= peaks.getData();        
        int lastIndex= (int)encodedPeaks.getLength() -1;
        return encodedPeaks.get(lastIndex).getValue();
    }

    @Override
    public List<PhdTag> getTags() {
        return tags;
    }
    
    
    
}
