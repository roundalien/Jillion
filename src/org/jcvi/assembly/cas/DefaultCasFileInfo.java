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
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class DefaultCasFileInfo implements CasFileInfo{

    private final List<String> names;
    private final BigInteger numberOfResidues;
    private final long numberOfSequences;
    
    
    /**
     * @param names
     * @param numberOfSequences
     * @param numberOfResidues
     */
    public DefaultCasFileInfo(List<String> names, long numberOfSequences,
            BigInteger numberOfResidues) {
        this.names = new ArrayList<String>(names);
        this.numberOfSequences = numberOfSequences;
        this.numberOfResidues = numberOfResidues;
    }

    @Override
    public List<String> getFileNames() {
        return names;
    }

    @Override
    public BigInteger getNumberOfResidues() {
        return numberOfResidues;
    }

    @Override
    public long getNumberOfSequences() {
        return numberOfSequences;
    }

    @Override
    public String toString() {
        return "DefaultCasFileInfo [numberOfSequences=" + numberOfSequences
                + ", numberOfResidues=" + numberOfResidues + ", names=" + names
                + "]";
    }

}
