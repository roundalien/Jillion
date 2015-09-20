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
package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriter;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriterBuilder;

public class SortFasta {

	/**
	 * @param args
	 * @throws DataStoreException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws DataStoreException, IOException {
		File inputFasta = new File("path/to/input.fasta");
		File sortedOutputFasta = new File("path/to/sorted/output.fasta");
		
		NucleotideFastaDataStore dataStore = new NucleotideFastaFileDataStoreBuilder(inputFasta)
														.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY)
														.build();
		SortedSet<String> sortedIds = new TreeSet<String>();
		StreamingIterator<String> iter=null;
		try {
			iter =dataStore.idIterator();
			while(iter.hasNext()){
				sortedIds.add(iter.next());
			}
		} finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		NucleotideFastaWriter out = new NucleotideFastaWriterBuilder(sortedOutputFasta)
												.build();
		try{
			for(String id : sortedIds){
				out.write(dataStore.get(id));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(out,dataStore);
		}
		
	}

}
