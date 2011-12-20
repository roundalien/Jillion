package org.jcvi.common.core.seq.fastx.fasta.pos;

import org.jcvi.common.core.seq.fastx.fasta.FastaFileDataStoreBuilderVisitor;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;

public interface PositionFastaDataStoreBuilderVisitor extends FastaFileDataStoreBuilderVisitor<ShortSymbol, Sequence<ShortSymbol>, PositionFastaRecord<Sequence<ShortSymbol>>, PositionFastaDataStore>{

	@Override
	PositionFastaDataStoreBuilderVisitor addFastaRecord(
			PositionFastaRecord<Sequence<ShortSymbol>> fastaRecord);
}
