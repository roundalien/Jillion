package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.align.SequenceAlignment;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.Residue;
/**
 * {@code PairwiseSequenceAlignment} is 
 * @author dkatzel
 *
 * @param <R>
 * @param <S>
 */
public interface PairwiseSequenceAlignment<R extends Residue, S extends Sequence<R>> extends SequenceAlignment<R, S> {
	/**
	 * Get the score of this alignment that 
	 * was computed from the {@link ScoringMatrix}
	 * used to make the alignment.
	 * @return the score as a float depending
	 * on the type of alignment and values
	 * of the scoring matrix this could negative.
	 */
	float getScore();
}
