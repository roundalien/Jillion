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
 * Created on Feb 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Rangeable;
import org.jcvi.glyph.qualClass.QualityClass;

public class QualityClassRegion implements Rangeable{

    private QualityClass qualityClass;
    private Range placed;
    
    public QualityClassRegion(QualityClass qualityClass, Range range){
        if(qualityClass ==null){
            throw new IllegalArgumentException("qualityClass can not be null");
        }
        if(range ==null){
            throw new IllegalArgumentException("range can not be null");
        }
        this.qualityClass = qualityClass;
        placed = range;
    }
   
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(placed)
            .append(" = quality class value ")
            .append(qualityClass.getValue());
        return builder.toString();
    }
    public QualityClass getQualityClass() {
        return qualityClass;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + placed.hashCode();
        result = prime * result + qualityClass.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof QualityClassRegion))
            return false;
        QualityClassRegion other = (QualityClassRegion) obj;
        return placed.equals(other.placed) && qualityClass.equals(other.qualityClass);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return placed.asRange();
    }
    
    
}
