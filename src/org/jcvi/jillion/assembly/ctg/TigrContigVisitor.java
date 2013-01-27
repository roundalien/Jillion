package org.jcvi.jillion.assembly.ctg;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public interface TigrContigVisitor {

	void visitConsensus(NucleotideSequence consensus);
	
	TigrContigReadVisitor visitRead(String readId, long gappedStartOffset, Direction dir, Range validRange);
	
	void visitIncompleteEnd();
	void visitEnd();
}