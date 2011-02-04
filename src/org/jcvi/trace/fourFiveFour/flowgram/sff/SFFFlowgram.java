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
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.util.Arrays;
import java.util.List;

import org.jcvi.CommonUtil;
import org.jcvi.Range;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityEncodedGlyphs;
import org.jcvi.trace.fourFiveFour.flowgram.Flowgram;

public class SFFFlowgram implements Flowgram {

    private NucleotideEncodedGlyphs basecalls;
    private QualityEncodedGlyphs qualities;
    private Range qualitiesClip;
    private Range adapterClip;
    private final short[] values;

    /**
     * @param basecalls
     * @param qualities
     * @param values
     * @param qualitiesClip
     * @param adapterClip
     */
    public SFFFlowgram(NucleotideEncodedGlyphs basecalls, QualityEncodedGlyphs qualities,
            List<Short> values, Range qualitiesClip, Range adapterClip) {
        canNotBeNull(basecalls, qualities, values, qualitiesClip, adapterClip);
        this.basecalls = basecalls;
        this.qualities = qualities;
        this.values = new short[values.size()];
        for(int i=0; i< values.size(); i++){
            this.values[i]= values.get(i);
        }
        this.qualitiesClip = qualitiesClip;
        this.adapterClip = adapterClip;
    }

    private void canNotBeNull(NucleotideEncodedGlyphs basecalls, EncodedGlyphs<PhredQuality> qualities,
            List<Short> values, Range qualitiesClip, Range adapterClip) {
        CommonUtil.cannotBeNull(basecalls, "basecalls can not be null");
        CommonUtil.cannotBeNull(qualities, "qualities can not be null");
        CommonUtil.cannotBeNull(values, "values can not be null");
        CommonUtil.cannotBeNull(qualitiesClip, "qualitiesClip can not be null");
        CommonUtil.cannotBeNull(adapterClip, "adapterClip can not be null");

        if(values.isEmpty()){
            throw new IllegalArgumentException("values can not be empty");
        }
    }

    @Override
    public NucleotideEncodedGlyphs getBasecalls() {
        return basecalls;
    }

    @Override
    public QualityEncodedGlyphs getQualities() {
        return qualities;
    }

    /**
     * @return the qualityClip
     */
    @Override
    public Range getQualitiesClip() {
        return qualitiesClip;
    }
    /**
     * @return the adapterClip
     */
    @Override
    public Range getAdapterClip() {
        return adapterClip;
    }
    @Override
    public int getSize() {
        return values.length;
    }
    @Override
    public float getValueAt(int index) {
        return SFFUtil.convertFlowgramValue(values[index]);
    }
    /**
     * Returns the hash code for this {@link SFFFlowgram}.
     * Hash code based on hashcodes for values, qualities, and clip points.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + basecalls.decode().hashCode();
        result = prime * result + Arrays.hashCode(values);
        result = prime * result + qualities.decode().hashCode();
        result = prime * result + qualitiesClip.hashCode();
        result = prime * result + adapterClip.hashCode();
        
        return result;
    }
    /**
     * Compares this {@link SFFFlowgram} with the specified Object for equality.
     * This method considers two {@link SFFFlowgram} objects equal 
     * only if they are have equal values, qualities and clip points. 
     * (basecalls can be derived from the values so basecalls 
     * are not taken into account).
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof SFFFlowgram)){
            return false;
        }
        SFFFlowgram other = (SFFFlowgram) obj;
        
        return
        CommonUtil.similarTo(basecalls.decode(), other.basecalls.decode()) &&
        CommonUtil.similarTo(qualities.decode(), other.qualities.decode()) &&
        CommonUtil.similarTo(qualitiesClip, other.qualitiesClip) &&
        CommonUtil.similarTo(adapterClip, other.adapterClip) &&
        Arrays.equals(values, other.values);
        
       
        
    }

    
}
