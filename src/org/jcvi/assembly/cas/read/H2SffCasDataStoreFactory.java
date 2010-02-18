/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Feb 3, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.datastore.H2NucleotideDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.datastore.H2QualityDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.sff.H2NucleotideSffDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.sff.H2QualitySffDataStore;

public class H2SffCasDataStoreFactory implements CasDataStoreFactory{

    @Override
    public NucleotideDataStore getNucleotideDataStoreFor(String pathToDataStore)
            throws CasDataStoreFactoryException {       
        if(!"sff".equals(FilenameUtils.getExtension(pathToDataStore))){
            throw new CasDataStoreFactoryException("not a sff file");
        }
        try {
            return new H2NucleotideSffDataStore(new File(pathToDataStore), 
                    new H2NucleotideDataStore(),
                    true);
        } catch (Exception e) {
           throw new CasDataStoreFactoryException("could not create H2 Sff Nucleotide DataStore for "+ pathToDataStore,e);
        } 
    }

    @Override
    public QualityDataStore getQualityDataStoreFor(String pathToDataStore)
            throws CasDataStoreFactoryException {
        if(!"sff".equals(FilenameUtils.getExtension(pathToDataStore))){
            throw new CasDataStoreFactoryException("not a sff file");
        }
        try {
            return new H2QualitySffDataStore(new File(pathToDataStore), 
                    new H2QualityDataStore(),
                    true);
        } catch (Exception e) {
           throw new CasDataStoreFactoryException("could not create H2 Sff Quality DataStore for "+ pathToDataStore,e);
        } 
    }

}
