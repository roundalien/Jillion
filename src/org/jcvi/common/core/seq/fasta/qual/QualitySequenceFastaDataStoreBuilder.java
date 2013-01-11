package org.jcvi.common.core.seq.fasta.qual;

import org.jcvi.common.core.seq.fasta.FastaDataStoreBuilder;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
/**
 * {@code QualityFastaDataStoreBuilder} is a {@link FastaDataStoreBuilder}
 * that builds a {@link QualitySequenceFastaDataStore}.
 * @author dkatzel
 *
 */
public interface QualitySequenceFastaDataStoreBuilder extends FastaDataStoreBuilder<PhredQuality, QualitySequence, QualitySequenceFastaRecord, QualitySequenceFastaDataStore>{
	/**
	 * Adds the given {@link QualitySequenceFastaRecord} to this builder.
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	QualitySequenceFastaDataStoreBuilder addFastaRecord(QualitySequenceFastaRecord fastaRecord);
}
