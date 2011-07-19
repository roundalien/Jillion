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

package org.jcvi.common.core.seq.read.trace.sanger;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.seq.fastx.fasta.pos.PositionDataStore;
import org.jcvi.common.core.seq.read.trace.AbstractTraceDataStoreAdapter;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortGlyph;


/**
 * @author dkatzel
 *
 *
 */
public class SangerTracePositionDataStoreAdapter <T extends SangerTrace> extends AbstractTraceDataStoreAdapter<T,Sequence<ShortGlyph>> implements PositionDataStore{

    public static <T extends SangerTrace, E extends T> SangerTracePositionDataStoreAdapter<T> adapt(DataStore<E> delegate){
        return (SangerTracePositionDataStoreAdapter<T>) new SangerTracePositionDataStoreAdapter<E>(delegate);
    }
    /**
     * @param delegate
     */
    public SangerTracePositionDataStoreAdapter(DataStore<T> delegate) {
      super(delegate);
    }

    @Override
    protected Sequence<ShortGlyph> adapt(T delegate) {
        return delegate.getPeaks().getData();
    }

}
