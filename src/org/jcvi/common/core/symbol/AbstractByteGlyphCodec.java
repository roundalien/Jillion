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
 * Created on Feb 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class AbstractByteGlyphCodec<G extends ByteSymbol> implements ByteGlyphCodec<G>{

    protected abstract G getValueOf(byte b);
    @Override
    public List<G> decode(byte[] encodedGlyphs) {
        List<G> glyphs = new ArrayList<G>();
        ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
        while(buf.hasRemaining()){
            glyphs.add(getValueOf(buf.get()));
        }
        return glyphs;
    }

    @Override
    public G decode(byte[] encodedGlyphs, int index) {
        return getValueOf(encodedGlyphs[index]);
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        return encodedGlyphs.length;
    }

    @Override
    public byte[] encode(Collection<G> glyphs) {
        ByteBuffer buf = ByteBuffer.allocate(glyphs.size());
        for(ByteSymbol g : glyphs){
            buf.put(g.getNumber().byteValue());
        }
        return buf.array();
    }
}
