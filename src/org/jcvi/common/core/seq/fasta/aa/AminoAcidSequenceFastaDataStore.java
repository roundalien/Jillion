package org.jcvi.common.core.seq.fasta.aa;

import org.jcvi.common.core.seq.fasta.FastaDataStore;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;

/**
 * {@code AminoAcidSequenceFastaDataStore} is a 
 * marker interface for {@link FastaDataStore}s
 * of {@link AminoAcidSequenceFastaRecord}s.
 * @author dkatzel
 *
 */
public interface AminoAcidSequenceFastaDataStore extends FastaDataStore<AminoAcid, AminoAcidSequence, AminoAcidSequenceFastaRecord> {

}