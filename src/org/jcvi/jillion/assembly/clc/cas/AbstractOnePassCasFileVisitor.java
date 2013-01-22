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
 * Created on Dec 31, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

import org.jcvi.jillion.assembly.clc.cas.align.CasScoringScheme;

public abstract class AbstractOnePassCasFileVisitor extends AbstractCasFileVisitor{
    private boolean initialized = false;
    private long readCounter =0;
    
    /**
	 * @return the readCounter
	 */
	public long getReadCounter() {
		return readCounter;
	}
	protected synchronized void checkNotYetInitialized(){
        if(isInitialized()){
            throw new IllegalStateException("already initialized");
        }
    }
    protected synchronized void checkIsInitialized(){
        if(!isInitialized()){
            throw new IllegalStateException("not initialized");
        }
    }
    protected boolean isInitialized(){
        return initialized;
    }
    @Override
    public synchronized void visitAssemblyProgramInfo(String name, String version,
            String parameters) {
        checkNotYetInitialized();
        super.visitAssemblyProgramInfo(name, version, parameters);
    }

    @Override
    public synchronized void visitReferenceDescription(CasReferenceDescription description) {
        checkNotYetInitialized();
        super.visitReferenceDescription(description);
    }

    @Override
    public synchronized void visitReferenceFileInfo(CasFileInfo contigFileInfo) {
        checkNotYetInitialized();
        super.visitReferenceFileInfo(contigFileInfo);
    }

    @Override
    public synchronized void visitContigPair(CasContigPair contigPair) {
        checkNotYetInitialized();
        super.visitContigPair(contigPair);
    }

    @Override
    public synchronized void visitEndOfFile() {
        checkNotYetInitialized();
        initialized = true;
        super.visitEndOfFile();
    }

    @Override
    public synchronized void visitFile() {
        checkNotYetInitialized();
        super.visitFile();
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public synchronized void visitMatch(CasMatch match) {
        checkNotYetInitialized();
        visitMatch(match, readCounter);
        incrementReadCounter();
    }

    protected final void incrementReadCounter(){
        readCounter++;
    }
    protected abstract void visitMatch(CasMatch match, long readCounter);
    
    @Override
    public synchronized void visitMetaData(long numberOfContigSequences, long numberOfReads) {
        checkNotYetInitialized();
        super.visitMetaData(numberOfContigSequences, numberOfReads);
    }

    @Override
    public synchronized void visitNumberOfReferenceFiles(long numberOfContigFiles) {
        checkNotYetInitialized();
        super.visitNumberOfReferenceFiles(numberOfContigFiles);
    }

    @Override
    public synchronized void visitNumberOfReadFiles(long numberOfReadFiles) {
        checkNotYetInitialized();
        super.visitNumberOfReadFiles(numberOfReadFiles);
    }

    @Override
    public synchronized void visitReadFileInfo(CasFileInfo readFileInfo) {
        checkNotYetInitialized();
        super.visitReadFileInfo(readFileInfo);
    }

    @Override
    public synchronized void visitScoringScheme(CasScoringScheme scheme) {
        checkNotYetInitialized();
        super.visitScoringScheme(scheme);
    }

}
