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
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

import java.nio.ByteBuffer;
/**
 * <code>IntValueSizeStrategy</code> is an implementation
 * of {@link ValueSizeStrategy} that reads/writes single bytes.
 * @author dkatzel
 *
 */
public class IntValueSizeStrategy implements ValueSizeStrategy<Integer> {
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public Integer getNext(ByteBuffer buf) {
        return buf.getInt();
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public void put(long value, ByteBuffer buf) {
        buf.putInt((int)value);
    }
    @Override
    public int numberOfBytesPerValue() {
        return 4;
    }
}
