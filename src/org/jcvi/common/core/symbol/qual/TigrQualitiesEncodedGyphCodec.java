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
 * Created on Mar 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.qual;

import java.util.Collection;
import java.util.List;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.GlyphCodec;

public final class TigrQualitiesEncodedGyphCodec implements GlyphCodec<PhredQuality>{

    private static TigrQualitiesEncodedGyphCodec INSTANCE = new TigrQualitiesEncodedGyphCodec();
    
    private TigrQualitiesEncodedGyphCodec(){}
    
    public static TigrQualitiesEncodedGyphCodec getINSTANCE(){
        return INSTANCE;
    }
    
    @Override
    public List<PhredQuality> decode(byte[] encodedGlyphs) {       
        return PhredQuality.valueOf(TigrQualitiesEncoder.decode(new String(encodedGlyphs,IOUtil.UTF_8)));       
    }

    @Override
    public PhredQuality decode(byte[] encodedGlyphs, int index) {
        return PhredQuality.valueOf(TigrQualitiesEncoder.decode((char)encodedGlyphs[index]));
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        return encodedGlyphs.length;
    }

    @Override
    public byte[] encode(Collection<PhredQuality> glyphs) {
        return TigrQualitiesEncoder.encode(PhredQuality.toArray(glyphs)).getBytes(IOUtil.UTF_8);
    }

    public byte[] encode(QualitySequence glyphs) {
        return TigrQualitiesEncoder.encode(PhredQuality.toArray(glyphs)).getBytes(IOUtil.UTF_8);
    }
}
