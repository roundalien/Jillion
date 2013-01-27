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
package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.nt.DefaultNucleotideSequenceFastaFileDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;

/**
 * @author dkatzel
 *
 *
 */
public class TestStreamingDefaultSequenceDataStore extends TestDefaultSequenceFastaDataStore{

    @Override
    protected NucleotideSequenceFastaDataStore parseFile(File file)
            throws IOException {
        InputStream in =null;
        try{
            in = new FileInputStream(file);
            return DefaultNucleotideSequenceFastaFileDataStore.create(in);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }

}