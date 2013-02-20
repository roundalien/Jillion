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
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas.read;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.clc.cas.CasTrimMap;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.qual.QualitySequenceFastaFileDataStoreBuilder;
/**
 * {@code FastaCasDataStoreFactory} is a {@link CasDataStoreFactory}
 * implementation for .fasta files.
 * @author dkatzel
 *
 *
 */
public class FastaCasDataStoreFactory extends AbstractCasDataStoreFactory
        {

    private final int cacheSize;

    /**
     * Create a FastaCasDataStoreFactory which will automatically
     * trim any records with the given {@link CasTrimMap} and using
     * the given cacheSize.
     * @param workingDir the casWorkingDirectory that all files are relative to.
     * @param trimToUntrimmedMap a non-null CasTrimMap which may trim any
     * records parsed.
     * @param cacheSize the max number of (trimmed) fasta records to store in memory. 
     */
    public FastaCasDataStoreFactory(File workingDir,CasTrimMap trimToUntrimmedMap,int cacheSize){
        super(workingDir,trimToUntrimmedMap);
        this.cacheSize = cacheSize;
    }
    
    /**
     * @param workingDir
     * @param trimMap
     * @param filter
     */
    public FastaCasDataStoreFactory(File workingDir, CasTrimMap trimMap,
            DataStoreFilter filter, int cacheSize) {
        super(workingDir, trimMap, filter);
        this.cacheSize = cacheSize;
    }
    @Override
    public NucleotideSequenceDataStore getNucleotideDataStoreFor(File pathToDataStore, DataStoreFilter filter) throws CasDataStoreFactoryException {  
        try {
			return DataStoreUtil.createNewCachedDataStore(NucleotideSequenceDataStore.class, 
			             FastaRecordDataStoreAdapter.adapt(NucleotideSequenceDataStore.class, 
			            		 new NucleotideSequenceFastaFileDataStoreBuilder(pathToDataStore)
			             				.hint(DataStoreProviderHint.ITERATION_ONLY)
			             				.build()),
			             cacheSize);
		} catch (IOException e) {
			throw new CasDataStoreFactoryException("could not create nucleotide sequence datastore for "+ pathToDataStore.getAbsolutePath(), e);
		}            
    }
    @Override
    public QualitySequenceDataStore getQualityDataStoreFor(
            File fastaFile,DataStoreFilter filter) throws CasDataStoreFactoryException { 
        try {
			return DataStoreUtil.createNewCachedDataStore(QualitySequenceDataStore.class, 
					FastaRecordDataStoreAdapter.adapt(QualitySequenceDataStore.class, 
							new QualitySequenceFastaFileDataStoreBuilder(fastaFile)
									.hint(DataStoreProviderHint.ITERATION_ONLY)
									.build()),
			        cacheSize);
		} catch (IOException e) {
			throw new CasDataStoreFactoryException("error creating quality datastore for "+fastaFile.getAbsolutePath(),e);
		}  
        
    }

    
}
