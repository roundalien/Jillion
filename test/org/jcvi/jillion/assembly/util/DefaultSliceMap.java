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
/*
 * Created on Jun 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

public class DefaultSliceMap extends AbstractSliceMap{
	
	private final Slice[] slices;
    protected PhredQuality defaultQuality;
	    
    public static <R extends AssembledRead, C extends Contig<R>> SliceMap create(C contig, QualitySequenceDataStore qualityDataStore,
                        GapQualityValueStrategy qualityValueStrategy) throws DataStoreException{
        return new DefaultSliceMap(contig, qualityDataStore, qualityValueStrategy);
    }
    
    public static <PR extends AssembledRead> DefaultSliceMap create(CoverageMap<PR> coverageMap,QualitySequenceDataStore qualityDataStore,GapQualityValueStrategy qualityValueStrategy){
        return new DefaultSliceMap(coverageMap, qualityDataStore, qualityValueStrategy);
    }

   
    
    
    private DefaultSliceMap(CoverageMap<? extends AssembledRead> coverageMap, 
                        QualitySequenceDataStore qualityDataStore,
                        GapQualityValueStrategy qualityValueStrategy){
        this(coverageMap,qualityDataStore, qualityValueStrategy,null);
    }
    
    private <PR extends AssembledRead,C extends Contig<PR>>  DefaultSliceMap(
            C contig, QualitySequenceDataStore qualityDataStore,GapQualityValueStrategy qualityValueStrategy) throws DataStoreException {
    	DefaultSlice.Builder builders[] = new DefaultSlice.Builder[(int)contig.getConsensusSequence().getLength()];
    	Iterator<Nucleotide> consensusIter = contig.getConsensusSequence().iterator();
    	for(int i=0; consensusIter.hasNext(); i++){
    		builders[i] = new DefaultSlice.Builder().setConsensus(consensusIter.next());
    	}
    	
    	StreamingIterator<PR> readIter = null;
    	try{
    		readIter = contig.getReadIterator();
    		while(readIter.hasNext()){
    			PR read = readIter.next();
    			int start = (int)read.getGappedStartOffset();
    			String id =read.getId();
    			Direction dir = read.getDirection();
    			
    			QualitySequence fullQualities = qualityDataStore.get(id);
    			QualitySequence gappedValidRangeQualities = qualityValueStrategy.getGappedValidRangeQualitySequenceFor(read, fullQualities);
    			
    			Iterator<Nucleotide> baseIter = read.getNucleotideSequence().iterator();
    			Iterator<PhredQuality> qualIter = gappedValidRangeQualities.iterator();
    			int i=0;
    			while(baseIter.hasNext()){
    				Nucleotide base = baseIter.next();
    				PhredQuality quality = qualIter.next();
    				builders[start+i].add(id, base, quality, dir);
    				i++;
    			}
    			
    		}
    		//done building
    		this.slices = new Slice[builders.length];
    		for(int i=0; i<slices.length; i++){
    			slices[i]= builders[i].build();
    		}
    	}finally{
    		IOUtil.closeAndIgnoreErrors(readIter);
    	}
    }
    protected DefaultSliceMap(CoverageMap<? extends AssembledRead> coverageMap, 
            QualitySequenceDataStore qualityDataStore,
            GapQualityValueStrategy qualityValueStrategy, PhredQuality defaultQuality){
    	int lengthOfMap = (int)getLastCoveredOffsetIn(coverageMap)+1;
    	this.slices = new Slice[lengthOfMap];
        this.defaultQuality = defaultQuality;
        for(CoverageRegion<?  extends AssembledRead> region : coverageMap){
        	Range range = region.asRange();
            for(long i=range.getBegin(); i<=range.getEnd(); i++ ){
                List<SliceElement> sliceElements = createSliceElementsFor(region, i, qualityDataStore, qualityValueStrategy);
                slices[(int)i] =new DefaultSlice.Builder()
                                            .addAll(sliceElements)
                                            .build();
            
            }
        }
    }

    private static long getLastCoveredOffsetIn(CoverageMap<?> coverageMap){
        if(coverageMap.isEmpty()){
            return -1L;
        }
        return coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).asRange().getEnd();
}
    
    /**
     * @return the defaultQuality
     */
    protected PhredQuality getDefaultQuality() {
        return defaultQuality;
    }

    public DefaultSliceMap(List<Slice> slices){
    	this.slices = new Slice[slices.size()];
        for(int i=0; i< this.slices.length; i++){
        	this.slices[i] = slices.get(i);
        }
    }
    @Override
    public Slice getSlice(long offset) {
        return slices[(int)offset];
    }
    @Override
    public long getSize() {
        return slices.length;
    }
    @Override
    public Iterator<Slice> iterator() {
        return Arrays.<Slice>asList(slices).iterator();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(slices);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SliceMap)) {
			return false;
		}
		SliceMap other = (SliceMap) obj;
		Iterator<Slice> iter = iterator();
		Iterator<Slice> otherIter = other.iterator();
		while(iter.hasNext()){
			if(!otherIter.hasNext()){
				return false;
			}
			if(!iter.next().equals(otherIter.next())){
				return false;
			}
		}
		if(otherIter.hasNext()){
			return false;
		}
		
		return true;
	}

    
   
}
