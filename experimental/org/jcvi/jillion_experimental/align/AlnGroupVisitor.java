package org.jcvi.jillion_experimental.align;

import java.util.List;



public interface AlnGroupVisitor {

	 /**
     * {@code ConservationInfo} contains information
     * about how each column (slice) in a group block
     * match.
     * @author dkatzel
     */
    public enum ConservationInfo{
        /**
         * The residues in the column are identical
         * in all sequences in the alignment.
         */
        IDENTICAL,
        /**
         * A conserved substitution has been
         * observed in this column.
         */
        CONSERVED_SUBSITUTION,
        /**
         * A semi-conserved substitution has been
         * observed in this column.
         */
        SEMI_CONSERVED_SUBSITUTION,
        /**
         * There is no conservation
         * in this column.  This could
         * mean that there are gaps
         * in the alignment at this column.
         */
        NOT_CONSERVED
        ;
    }
	 /**
     * End of the current  group of aligned reads is about to be
     * visited.  If there are more groups, then the next method to be
     * visited will be {@link #visitBeginGroup()}
     * or {@link #visitEndOfFile()} if the file has been
     * completely parsed.
     */
    void visitEndGroup();
    /**
     * Visit a single read in the current group.
     * @param id the id of this read.
     * @param gappedAlignment the gapped alignment of this read
     * in this read group.  Usually groups are only about 60 residues long
     * so if this read is longer than that, then only a partial alignment
     * will be presented. (Longer reads will span multiple groups).
     */
    void visitAlignedSegment(String id, String gappedAlignment);
    /**
     * Visit a description of the conservation of the residues
     * in the current group.
     * @param conservationInfos a List of {@link ConservationInfo}; 
     * will never be null.
     * there will be one record in the list for each residue in the group.
     * the ith record in the list corresponds to the ith residue in the group.
     */
    void visitConservationInfo(List<ConservationInfo> conservationInfos);
}
