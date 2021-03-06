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
package org.jcvi.jillion.trace.chromat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.io.MagicNumberInputStream;
import org.jcvi.jillion.internal.trace.chromat.abi.AbiUtil;
import org.jcvi.jillion.internal.trace.chromat.scf.SCFUtils;
import org.jcvi.jillion.internal.trace.chromat.ztr.ZTRUtil;
import org.jcvi.jillion.trace.chromat.abi.AbiChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramBuilder;
/**
 * {@code ChromatogramFactory} is a Factory object
 * that will create {@link Chromatogram}
 * objects using the given encoded
 * Chromatogram file.  This class
 * supports ZTR, SCF and AB1 encoded
 * Chromatograms so users do not 
 * have to know which encoding the file
 * uses.
 * 
 * @author dkatzel
 *
 */
public final class ChromatogramFactory {

	private ChromatogramFactory(){
		//can not instantiate
	}
	/**
	 * Create a new {@link Chromatogram} object
	 * using the given chromatogram file to populate the object data.
	 * The value returned by the {@link Chromatogram#getId()}
	 * will be the chromatogram File's name without the file
	 * extension.  For example, /path/to/foo.ztr will 
	 * get an id of "foo".
	 * 
	 * @param chromatogramFile the chromatogram file
	 * to parse and create a {@link Chromatogram}
	 * object from.
	 * @return a new {@link Chromatogram}
	 * object; will never be null.
	 * @throws IOException if there is a problem parsing
	 * the file.
	 * @throws NullPointerException if chromatogramFile is null.
	 */
	public static Chromatogram create(File chromatogramFile) throws IOException{
		if(chromatogramFile== null){
			throw new NullPointerException("file can not be null");
		}
		String id = FileUtil.getBaseName(chromatogramFile);
		return create(id,chromatogramFile);
	}
	/**
	 * Create a new {@link Chromatogram} object
	 * using the given chromatogram file to populate the object data.
	 * 
	 * The value returned by the {@link Chromatogram#getId()}
	 * will be the value passed in as the id parameter.
	 * @param id the id of this chromatogram.
	 * @param chromatogramFile the chromatogram file
	 * to parse and create a {@link Chromatogram}
	 * object from.
	 * @return a new {@link Chromatogram}
	 * object; will never be null.
	 * @throws IOException if there is a problem parsing
	 * the file.
	 * @throws NullPointerException if either id or chromatogramFile are null.
	 */
	public static Chromatogram create(String id, File chromatogramFile) throws IOException{		
		MagicNumberInputStream mIn =null;
        try{
        	mIn= new MagicNumberInputStream(chromatogramFile); 
        	return createCorrectTypeOfChromatogram(mIn, id);
        }finally{
        	IOUtil.closeAndIgnoreErrors(mIn);
        }
	}
	/**
	 * Create a new {@link Chromatogram} object
	 * using the given chromatogram file to populate the object data.
	 * Note that this method does not close the inputStream.
	 * 
	 * The value returned by the {@link Chromatogram#getId()}
	 * will be the value passed in as the id parameter.
	 * @param id the id of this chromatogram.
	 * @param in the chromatogram data given an {@link InputStream}
	 * to parse and create a {@link Chromatogram}
	 * object from; can not be null.
	 * @return a new {@link Chromatogram}
	 * object; will never be null.
	 * @throws IOException if there is a problem parsing
	 * the {@link InputStream}.
	 * @throws NullPointerException if either parameter is null.
	 */
	public static Chromatogram create(String id, InputStream in) throws IOException{
		MagicNumberInputStream mIn = new MagicNumberInputStream(in); 
        return createCorrectTypeOfChromatogram(mIn, id);
        
	}
	private static Chromatogram createCorrectTypeOfChromatogram(
			MagicNumberInputStream mIn, String id)
			throws IOException, FileNotFoundException, IOException {
		byte[] magicNumber = mIn.peekMagicNumber();
		
		if(AbiUtil.isABIMagicNumber(magicNumber)){
			return new AbiChromatogramBuilder(id, mIn).build();
		}else if(ZTRUtil.isMagicNumber(magicNumber)){
			return new ZtrChromatogramBuilder(id, mIn).build();
		}else if(SCFUtils.isMagicNumber(magicNumber)){
			return new ScfChromatogramBuilder(id, mIn).build();
		}else{
			throw new IOException("unknown chromatogram format (not ab1, scf or ztr)");
		}
	}
}
