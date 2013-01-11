package org.jcvi.common.annotation.hmm;

import java.util.List;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.Symbol;

public interface HmmPathDecoder<S extends Symbol> {
    /**
     * Decode the path traversed in the HMM by the given
     * Sequence.
     * @param sequence the sequence to get the path for.
     * @return a list of states in the Hmm 
     */
	List<Integer> decodePath(Sequence<S> sequence);
}
