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
 * Created on Nov 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreAdapter;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
/**
 * {@code TraceNucleotideDataStoreAdapter} adapts a {@link TraceDataStore} into
 * a {@link NucleotideSequenceDataStore} by delegating all the get() calls
 * to the wrapped datastore and then returned only the {@link NucleotideSequence}
 *  from the desired trace.
 * @author dkatzel
 */
public final class TraceNucleotideDataStoreAdapter <T extends Trace> {
	
	private TraceNucleotideDataStoreAdapter(){
		//can not instantiate
	}
	/**
	 * Create a new {@link NucleotideSequenceDataStore} instance
	 * by adapting the given DataStore of traces.
	 * @param delegate the {@link DataStore} to adapt.
	 * @return a new {@link NucleotideSequenceDataStore} instance; never null.
	 * @throws NullPointerException if delegate is null.
	 */
	public static <T extends Trace> NucleotideSequenceDataStore adapt(DataStore<T> delegate){
        return DataStoreAdapter.adapt(NucleotideSequenceDataStore.class, delegate, new DataStoreAdapter.AdapterCallback<T, NucleotideSequence>() {

			@Override
			public NucleotideSequence get(T from) {
				return from.getNucleotideSequence();
			}
		
        });
    }
}
