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
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.alignment;

import java.util.HashMap;
import java.util.Map;

public enum CasAlignmentType {

    LOCAL((byte) 0),
    SEMI_LOCAL((byte)1),
    REVERSE_SEMI_LOCAL((byte)2),
    GLOBAL((byte)3)
    ;
    
    private static Map<Byte, CasAlignmentType> MAP;
    
    static{
        MAP = new HashMap<Byte, CasAlignmentType>();
        for(CasAlignmentType type : values()){
            MAP.put(type.getValue(), type);
        }
    }
    private final byte value;
    
    private CasAlignmentType(byte value){
        this.value =value;
    }

    public byte getValue() {
        return value;
    }
    
    public static CasAlignmentType valueOf(byte value){
        return MAP.get(Byte.valueOf(value));
    }
}
