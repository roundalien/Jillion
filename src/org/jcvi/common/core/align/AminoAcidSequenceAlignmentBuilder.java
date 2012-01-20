package org.jcvi.common.core.align;

import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequenceBuilder;
import org.jcvi.common.core.symbol.residue.aa.AminoAcids;

public class AminoAcidSequenceAlignmentBuilder extends AbstractSequenceAlignmentBuilder<AminoAcid, AminoAcidSequence, AminoAcidSequenceAlignment, AminoAcidSequenceBuilder>{

	@Override
	protected AminoAcidSequenceBuilder createSequenceBuilder() {
		return new AminoAcidSequenceBuilder();
	}

	@Override
	protected AminoAcidSequenceAlignment createAlignment(
			double percentIdentity, int alignmentLength, int numMismatches,
			int numGap, AminoAcidSequence queryAlignment,
			AminoAcidSequence subjectAlignment) {
		return new AminoAcidSequenceAlignmentImpl(
				percentIdentity, alignmentLength, numMismatches,
				numGap, queryAlignment, subjectAlignment);
	}

	@Override
	protected Iterable<AminoAcid> parse(String sequence) {
		return AminoAcids.parse(sequence);
	}
	

	
	
	
	@Override
	public AminoAcidSequenceAlignmentBuilder addMatches(
			String matchedSequence) {
		super.addMatches(matchedSequence);
		return this;
	}

	@Override
	public AminoAcidSequenceAlignmentBuilder addMatch(
			AminoAcid match) {
		super.addMatch(match);
		return this;
	}

	@Override
	public AminoAcidSequenceAlignmentBuilder addMatches(
			Iterable<AminoAcid> matches) {
		super.addMatches(matches);
		return this;
	}

	@Override
	public AminoAcidSequenceAlignmentBuilder addMismatch(
			AminoAcid query, AminoAcid subject) {
		super.addMismatch(query, subject);
		return this;
	}

	@Override
	public AminoAcidSequenceAlignmentBuilder addGap(
			AminoAcid query, AminoAcid subject) {
		super.addGap(query, subject);
		return this;
	}

	@Override
	public AminoAcidSequenceAlignmentBuilder addGap(
			char query, char subject) {
		super.addGap(query, subject);
		return this;
	}
	@Override
	public AminoAcidSequenceAlignmentBuilder reverse() {
		super.reverse();
		return this;
	}
	@Override
	protected AminoAcid parse(char aa) {
		return AminoAcid.parse(aa);
	}





	private final class AminoAcidSequenceAlignmentImpl extends SequenceAlignmentImpl implements AminoAcidSequenceAlignment{
		

		public AminoAcidSequenceAlignmentImpl(double percentIdentity,
				int alignmentLength, int numMismatches, int numGap,
				AminoAcidSequence queryAlignment,
				AminoAcidSequence subjectAlignment) {
			super(percentIdentity, alignmentLength, numMismatches, numGap, queryAlignment,
					subjectAlignment);
		}

		
		
		
	}

}
