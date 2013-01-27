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
 * Created on Oct 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

import org.jcvi.jillion.assembly.clc.cas.align.CasScoringScheme;
/**
 * {@code AbstractCasFileVisitor} is an abstract implementation
 * of {@link CasFileVisitor} with all the methods of the interface
 * implemented as no-ops.  This simplifies implementing the interface
 * since subclasses will only have to override methods they care about.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractCasFileVisitor implements CasFileVisitor {
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitAssemblyProgramInfo(String name, String version,
            String parameters) {
    	//no-op
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitReferenceDescription(CasReferenceDescription description) {
    	//no-op
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitReferenceFileInfo(CasFileInfo contigFileInfo) {
    	//no-op
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitContigPair(CasContigPair contigPair) {
    	//no-op
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitMatch(CasMatch match) {
    	//no-op
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitMetaData(long numberOfContigSequences, long numberOfReads) {
    	//no-op
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitNumberOfReferenceFiles(long numberOfContigFiles) {
    	//no-op
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitNumberOfReadFiles(long numberOfReadFiles) {
    	//no-op
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitReadFileInfo(CasFileInfo readFileInfo) {
    	//no-op
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitScoringScheme(CasScoringScheme scheme) {
    	//no-op
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitEndOfFile() {
    	//no-op
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitFile() {
    	//no-op
    }

}