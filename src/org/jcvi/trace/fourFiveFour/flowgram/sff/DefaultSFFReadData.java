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

import java.math.BigInteger;
import java.util.Arrays;

import org.jcvi.CommonUtil;
import org.jcvi.util.MathUtil;

public class DefaultSFFReadData implements SFFReadData {

    private String basecalls;
    private byte[] indexes;
    private short[] values;
    private byte[] qualities;


    /**
     * @param basecalls
     * @param indexes
     * @param values
     * @param qualities
     */
    public DefaultSFFReadData(String basecalls, byte[] indexes, short[] values,
            byte[] qualities) {
        validateArguments(basecalls, indexes, values, qualities);
        this.basecalls = basecalls;
        //make defensive copies
        
        this.indexes = Arrays.copyOf(indexes, indexes.length);
        this.values = Arrays.copyOf(values, values.length);
        this.qualities = Arrays.copyOf(qualities, qualities.length);
    }

    private void validateArguments(String basecalls, byte[] indexes,
            short[] values, byte[] qualities) {
        canNotBeNull(basecalls, indexes, values, qualities);
        lengthsMatch(basecalls, indexes, qualities);
        indexesWithinBounds(indexes, values);
    }

    private void indexesWithinBounds(byte[] indexes, short[] values) {
        final BigInteger sum = MathUtil.sumOf(indexes);
        if(sum.compareTo(BigInteger.valueOf(values.length)) >0){
            throw new ArrayIndexOutOfBoundsException("indexed flowgram value refers to "+ sum +
                    "flowgram value length is" + values.length);
        }
    }

    private void lengthsMatch(String basecalls, byte[] indexes, byte[] qualities) {
        if(basecalls.length() !=indexes.length || indexes.length !=qualities.length){
            throw new IllegalArgumentException("basecalls, indexes and qualities must be the same length");
        }
    }

    private void canNotBeNull(String basecalls, byte[] indexes, short[] values,
            byte[] qualities) {
        CommonUtil.cannotBeNull(basecalls, "basecalls can not be null");
        CommonUtil.cannotBeNull(indexes, "indexes can not be null");
        CommonUtil.cannotBeNull(values, "flowgram values can not be null");
        CommonUtil.cannotBeNull(qualities, "qualities can not be null");
    }

    @Override
    public String getBasecalls() {
        return basecalls;
    }

    @Override
    public byte[] getFlowIndexPerBase() {
        //defensive copy
        return Arrays.copyOf(indexes, indexes.length);
    }

    @Override
    public short[] getFlowgramValues() {
        //defensive copy
        return Arrays.copyOf(values, values.length);
    }

    @Override
    public byte[] getQualities() {
        //defensive copy
        return Arrays.copyOf(qualities, qualities.length);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result+ basecalls.hashCode();
        result = prime * result + Arrays.hashCode(indexes);
        result = prime * result + Arrays.hashCode(qualities);
        result = prime * result + Arrays.hashCode(values);
        return result;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof DefaultSFFReadData)){
            return false;
        }
        final DefaultSFFReadData other = (DefaultSFFReadData) obj;
        return CommonUtil.similarTo(getBasecalls(), other.getBasecalls())
                && Arrays.equals(indexes, other.indexes)
                && Arrays.equals(qualities, other.qualities)
                && Arrays.equals(values, other.values);

    }

}
