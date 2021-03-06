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
package org.jcvi.jillion.experimental.assembly.agp;

import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.jillion.assembly.PlacedContig;
import org.jcvi.jillion.assembly.Scaffold;
import org.jcvi.jillion.assembly.util.CoverageMap;
import org.jcvi.jillion.assembly.util.CoverageRegion;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.io.IOUtil;

public final class AgpWriter {

	private AgpWriter(){
		//no-op
	}
	public static void writeScaffold(Scaffold scaffold, OutputStream out) throws IOException{
		CoverageMap<PlacedContig> coverageMap =scaffold.getContigCoverageMap();
		String scaffoldId = scaffold.getId();
		int partNumber =1;
		for(CoverageRegion<PlacedContig> region : coverageMap){
			if(region.getCoverageDepth()==0){
				writeNs(scaffoldId, partNumber, region.asRange(), out);
				partNumber++;
			}else{
				for(PlacedContig contig : region){
					writeContig(scaffoldId, partNumber, contig, out);	
					partNumber++;
				}
			}
		}
	}

	private static void writeContig(String scaffoldId, int partNumber,
			PlacedContig contig, OutputStream out) throws IOException {
		Range range =contig.asRange();
		// chrY	1	3043	1	W	AADB02037551.1	1	3043	+
		String line = String.format("%s\t%d\t%d\t%d\tW\t%s\t1\t%d\t+%n",
				scaffoldId,
				range.getBegin(CoordinateSystem.RESIDUE_BASED),
				range.getEnd(CoordinateSystem.RESIDUE_BASED),
				partNumber,
				contig.getContigId(),
				range.getLength()
				);
		out.write(line.getBytes(IOUtil.UTF_8));
	}

	private static void writeNs(String scaffoldId,int partNumber, Range range,
			OutputStream out) throws IOException {
		//chrY	3044	53043	2	N	50000	contig	no	
		String line = String.format("%s\t%d\t%d\t%d\tN\t%d\tcontig\tno\tna%n",
				scaffoldId,
				range.getBegin(CoordinateSystem.RESIDUE_BASED),
				range.getEnd(CoordinateSystem.RESIDUE_BASED),
				partNumber,
				range.getLength()
				);
		out.write(line.getBytes(IOUtil.UTF_8));
		
	}
}
