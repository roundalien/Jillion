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
 * Created on Jan 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

import java.nio.ShortBuffer;
import java.util.List;

public class ShortGlyph extends DefaultNumericGlyph implements Comparable<ShortGlyph>{


    public ShortGlyph(short s){
        super(Short.valueOf(s));
    }
    @Override
    public Short getNumber() {
        return (Short)super.getNumber();
    }
    @Override
    public int compareTo(ShortGlyph o) {
        return getNumber().compareTo(o.getNumber());
    }
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    public static short[] toArray(List<ShortGlyph> shorts){
        ShortBuffer buf = ShortBuffer.allocate(shorts.size());
        for(ShortGlyph aShort : shorts){
            buf.put(aShort.getNumber());
        }
        return buf.array();
    }
}
