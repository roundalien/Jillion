package org.jcvi.common.core.seq.fastx.fasta.pos;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code DefaultPositionFastaDataStoreBuilder} is a {@link PositionFastaDataStoreBuilder}
 * that stores all {@link PositionSequenceFastaRecord} added to it via the {@link #addFastaRecord(PositionSequenceFastaRecord)}
 * in  a Map.  All fastas are stored in memory so if too many records are added, this object could
 * take up considerable memory and could cause an {@link OutOfMemoryError}.
 * @author dkatzel
 *
 */
public final class DefaultPositionFastaDataStoreBuilder implements PositionFastaDataStoreBuilder{

	private final Map<String, PositionSequenceFastaRecord<Sequence<ShortSymbol>>> map = new LinkedHashMap<String, PositionSequenceFastaRecord<Sequence<ShortSymbol>>>();
	@Override
	public PositionFastaDataStore build() {
		return new PositionFastaDataStoreImpl(map);
	}

	@Override
	public <F extends PositionSequenceFastaRecord<Sequence<ShortSymbol>>> DefaultPositionFastaDataStoreBuilder addFastaRecord(
			F fastaRecord) {
		if(fastaRecord ==null){
			throw new NullPointerException("fasta record can not be null");
		}
		map.put(fastaRecord.getId(), fastaRecord);
		return this;
	}
	
	private static final class PositionFastaDataStoreImpl implements PositionFastaDataStore{
		private final DataStore<PositionSequenceFastaRecord<Sequence<ShortSymbol>>> delegate;
		private PositionFastaDataStoreImpl(Map<String, PositionSequenceFastaRecord<Sequence<ShortSymbol>>> map){
			delegate = MapDataStoreAdapter.adapt(map);
		}
		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			return delegate.idIterator();
		}

		@Override
		public PositionSequenceFastaRecord<Sequence<ShortSymbol>> get(String id)
				throws DataStoreException {
			return delegate.get(id);
		}

		@Override
		public boolean contains(String id) throws DataStoreException {
			return delegate.contains(id);
		}

		@Override
		public long getNumberOfRecords() throws DataStoreException {
			return delegate.getNumberOfRecords();
		}

		@Override
		public boolean isClosed() throws DataStoreException {
			return delegate.isClosed();
		}

		@Override
		public void close() throws IOException {
			delegate.close();
			
		}

		@Override
		public StreamingIterator<PositionSequenceFastaRecord<Sequence<ShortSymbol>>> iterator() throws DataStoreException {
			return delegate.iterator();
		}
		
	}
}
