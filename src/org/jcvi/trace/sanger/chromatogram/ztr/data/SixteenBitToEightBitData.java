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
 * Created on Oct 31, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

/**
 * <code>SixteenBitToEightBitData</code> is the implementation of the ZTR 16 bit to 8 bit conversion format.
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 */
public class SixteenBitToEightBitData extends AbstractToEightBitData {
    /**
     * Constructor.
     */
    public SixteenBitToEightBitData() {
        super(new ShortValueSizeStrategy());
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    protected int getMaxPossibleDecodedSize(int numberOfEncodedBytes) {
        return numberOfEncodedBytes*2;
    }
}
