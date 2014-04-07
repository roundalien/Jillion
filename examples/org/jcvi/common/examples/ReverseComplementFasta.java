/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecordWriter;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecordWriterBuilder;

public class ReverseComplementFasta {

	public static void main(String[] args) throws DataStoreException, IOException {
		File inputFasta = new File("path/to/input/fasta");
		File reverseComplimentOutputFasta = new File("/path/to/output.sorted.fasta");
		
		NucleotideFastaDataStore dataStore = new NucleotideFastaFileDataStoreBuilder(inputFasta)
														.hint(DataStoreProviderHint.ITERATION_ONLY)
														.build();
		NucleotideFastaRecordWriter out = new NucleotideFastaRecordWriterBuilder(reverseComplimentOutputFasta)
															.build();

		StreamingIterator<NucleotideFastaRecord> iter=null;
		try {
			iter =dataStore.iterator();
			while(iter.hasNext()){
				NucleotideFastaRecord record =iter.next();
				NucleotideSequence reverseSequence = new NucleotideSequenceBuilder(record.getSequence())
															.reverseComplement()
															.build();
				out.write(record.getId(), reverseSequence, record.getComment());
			}
		} finally{
			IOUtil.closeAndIgnoreErrors(iter,out);
		}		
	}
}
