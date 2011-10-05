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

package org.jcvi.common.core.assembly.contig.cas;

import org.jcvi.common.core.assembly.trim.TrimDataStore;
import org.jcvi.common.core.assembly.trim.TrimDataStoreUtil;

/**
 * @author dkatzel
 *
 *
 */
public class ExternalTrimInfo {

    private static final ExternalTrimInfo EMPTY = new ExternalTrimInfo(
            EmptyCasTrimMap.getInstance(),
            TrimDataStoreUtil.EMPTY_DATASTORE);
    
    private final TrimDataStore trimDataStore;
    private final CasTrimMap casTrimMap;
    
    public static ExternalTrimInfo createEmptyInfo(){
        return EMPTY;
    }
    public static ExternalTrimInfo create(CasTrimMap casTrimMap, TrimDataStore trimDataStore){
        return new ExternalTrimInfo(casTrimMap,trimDataStore);
    }
    private ExternalTrimInfo(CasTrimMap casTrimMap, TrimDataStore trimDataStore) {
        this.casTrimMap = casTrimMap;
        this.trimDataStore = trimDataStore;
    }
    /**
     * @return the trimDataStore
     */
    public TrimDataStore getTrimDataStore() {
        return trimDataStore;
    }
    /**
     * @return the casTrimMap
     */
    public CasTrimMap getCasTrimMap() {
        return casTrimMap;
    }
   
    
}