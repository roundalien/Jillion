/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jan 14, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas.read;

import org.jcvi.jillion.assembly.clc.cas.CasFileInfo;
import org.jcvi.jillion.assembly.clc.cas.CasMatch;

public class ReferenceCasFileNucleotideDataStore  extends AbstractCasFileNucleotideDataStore {

    public ReferenceCasFileNucleotideDataStore(
            CasDataStoreFactory casDataStoreFactory) {
        super(casDataStoreFactory);
    }


    @Override
    public synchronized void visitReferenceFileInfo(CasFileInfo contigFileInfo) {
        super.visitReferenceFileInfo(contigFileInfo);
        loadNucleotidesFrom(contigFileInfo);
    }
    /**
     * {@inheritDoc}
     */
     @Override
     protected void visitMatch(CasMatch match, long readCounter) {
         // no-op
         
     }
}
