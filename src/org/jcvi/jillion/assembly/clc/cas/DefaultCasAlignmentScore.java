/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

final class DefaultCasAlignmentScore implements CasAlignmentScore {

    private final int firstInsertion, insertionExtension,
                firstDeletion, deletionExtension,
                match,
                transition, transversion,unknown;
    
  
    public DefaultCasAlignmentScore(int firstInsertion, int insertionExtension,
            int firstDeletion, int deletionExtension, int match,
            int transition, int transversion, int unknown) {
        this.firstInsertion = firstInsertion;
        this.insertionExtension = insertionExtension;
        this.firstDeletion = firstDeletion;
        this.deletionExtension = deletionExtension;
        this.match = match;
        this.transition = transition;
        this.transversion = transversion;
        this.unknown = unknown;
    }

    @Override
    public int getDeletionExtensionCost() {
        return deletionExtension;
    }

    @Override
    public int getFirstDeletionCost() {
        return firstDeletion;
    }

    @Override
    public int getFirstInsertionCost() {
        return firstInsertion;
    }

    @Override
    public int getInsertionExtensionCost() {
        return insertionExtension;
    }

    @Override
    public int getMatchScore() {
        return match;
    }

    @Override
    public int getTransitionScore() {
        return transition;
    }

    @Override
    public int getTransversionScore() {
        return transversion;
    }

    @Override
    public int getUnknownScore() {
        return unknown;
    }

    @Override
    public String toString() {
        return "DefaultCasAlignmentScore [deletionExtension="
                + deletionExtension + ", firstDeletion=" + firstDeletion
                + ", firstInsertion=" + firstInsertion
                + ", insertionExtension=" + insertionExtension + ", match="
                + match + ", transition=" + transition + ", transversion="
                + transversion + ", unknown=" + unknown + "]";
    }

}
