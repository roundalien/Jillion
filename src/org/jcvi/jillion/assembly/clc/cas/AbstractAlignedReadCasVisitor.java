package org.jcvi.jillion.assembly.clc.cas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.assembly.clc.cas.align.CasAlignment;
import org.jcvi.jillion.assembly.clc.cas.align.CasAlignmentRegion;
import org.jcvi.jillion.assembly.clc.cas.align.CasAlignmentRegionType;
import org.jcvi.jillion.assembly.clc.cas.read.CasPlacedRead;
import org.jcvi.jillion.assembly.clc.cas.read.DefaultCasPlacedReadFromCasAlignmentBuilder;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.jillion.trace.Trace;
import org.jcvi.jillion.trace.TraceDataStore;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.sff.SffFileIterator;

public abstract class AbstractAlignedReadCasVisitor extends AbstractCasFileVisitor2{

	private final CasGappedReferenceDataStore gappedReferenceDataStore;

	private final File workingDir;
	
	private List<StreamingIterator<? extends Trace>> iterators = new ArrayList<StreamingIterator<? extends Trace>>();
	
	
	
	public AbstractAlignedReadCasVisitor(File casFile,
			CasGappedReferenceDataStore gappedReferenceDataStore) {
		if(gappedReferenceDataStore ==null){
			throw new NullPointerException("gapped Reference DataStore can not be null");
		}
		if(casFile ==null){
			throw new NullPointerException("cas file can not be null");
		}
		this.workingDir = casFile.getParentFile();
		this.gappedReferenceDataStore = gappedReferenceDataStore;
	}

	public final CasGappedReferenceDataStore getGappedReferenceDataStore() {
		return gappedReferenceDataStore;
	}

	@Override
	public final void visitReadFileInfo(CasFileInfo readFileInfo) {
		for(String filePath :readFileInfo.getFileNames()){
			try {
				File file = CasUtil.getFileFor(workingDir, filePath);
				
				iterators.add(createIteratorFor(file));
			} catch (Exception e) {
				for(StreamingIterator<? extends Trace> iter : iterators){
					IOUtil.closeAndIgnoreErrors(iter);
				}
				throw new IllegalStateException("error getting input read data", e);
			}           
        }
	}
	
	private StreamingIterator<? extends Trace> createIteratorFor(File file) throws DataStoreException{
        ReadFileType readType = ReadFileType.getTypeFromFile(file.getName());
           switch(readType){
	            case FASTQ: 
	            	return createFastqIterator(file);
	            case SFF:
	            	return createSffIterator(file);
	            case FASTA:
                       return createFastaIterator(file);
	            default: 
	            	throw new IllegalArgumentException("unsupported type "+ file.getName());
	            }
        
   }


    protected StreamingIterator<? extends Trace> createFastqIterator(File illuminaFile) throws DataStoreException {
		try {
			FastqDataStore datastore = new FastqFileDataStoreBuilder(illuminaFile)
											.hint(DataStoreProviderHint.ITERATION_ONLY)
											.build();
			return datastore.iterator();
		} catch (IOException e) {
			throw new IllegalStateException("fastq file no longer exists! : "+ illuminaFile.getAbsolutePath());
		}
		
    }

    protected StreamingIterator<? extends Trace> createSffIterator(File sffFile) throws DataStoreException{
        return SffFileIterator.createNewIteratorFor(sffFile);
    }

    protected StreamingIterator<? extends Trace> createFastaIterator(File fastaFile) throws DataStoreException{        
        try {
			NucleotideSequenceFastaDataStore datastore = new NucleotideSequenceFastaFileDataStoreBuilder(fastaFile)
															.hint(DataStoreProviderHint.ITERATION_ONLY)
															.build();
			
			@SuppressWarnings("unchecked")
			TraceDataStore<Trace> fakeQualities = DataStoreUtil.adapt(TraceDataStore.class, datastore, 
					new DataStoreUtil.AdapterCallback<NucleotideSequenceFastaRecord, Trace>() {

						@Override
						public Trace get(final NucleotideSequenceFastaRecord from) {
						        int numberOfQualities =(int) from.getSequence().getLength();
								byte[] qualities = new byte[numberOfQualities];
								Arrays.fill(qualities, PhredQuality.valueOf(30).getQualityScore());
						        final QualitySequence qualSequence = new QualitySequenceBuilder(qualities).build();
							return new Trace() {
								
								@Override
								public QualitySequence getQualitySequence() {
									return qualSequence;
								}
								
								@Override
								public NucleotideSequence getNucleotideSequence() {

									return from.getSequence();
								}
								
								@Override
								public String getId() {
									return from.getId();
								}
							};
						}
				
			});
			return fakeQualities.iterator();
        } catch (IOException e) {
			throw new DataStoreException("error reading fasta file "+ fastaFile.getAbsolutePath(),e);
		}
    }
	    
    protected abstract void visitUnMatched(Trace currentTrace);

    protected abstract void  visitMatch(String referenceId, CasPlacedRead read, Trace traceOfRead);
    
	@Override
	public CasMatchVisitor visitMatches(CasVisitorCallback callback) {
		
		
		return new TraceCasMatchVisitor(IteratorUtil.createChainedStreamingIterator(iterators));
		
	}
	
	private class TraceCasMatchVisitor implements CasMatchVisitor{
		private final StreamingIterator<Trace> chainedTraceIterator;
		
		public TraceCasMatchVisitor(
				StreamingIterator<Trace> chainedTraceIterator) {
			this.chainedTraceIterator = chainedTraceIterator;
		}

		@Override
		public void visitMatch(CasMatch match) {
			if(!chainedTraceIterator.hasNext()){
				closeIterator();
				throw new IllegalStateException("possible cas file corruption : no more reads in input files but cas file says there are more reads");
			}
			Trace currentTrace = chainedTraceIterator.next();
			if(match.matchReported()){
				CasAlignment alignment = match.getChosenAlignment();
				long refIndex = alignment.getReferenceIndex();
				String refId = gappedReferenceDataStore.getIdByIndex(refIndex);
				CasPlacedRead read =null;
				try {
					if(refId ==null){
						closeIterator();
						throw new IllegalStateException("could not get get gapped reference for index "+ refIndex);
					
					}
					NucleotideSequence gappedReference = gappedReferenceDataStore.get(refId);
					long ungappedStartOffset = alignment.getStartOfMatch();
			        long gappedStartOffset = gappedReference.getGappedOffsetFor((int)ungappedStartOffset);
			        
			        List<CasAlignmentRegion> regionsToConsider = new ArrayList<CasAlignmentRegion>(alignment.getAlignmentRegions());
			        int lastIndex = regionsToConsider.size()-1;
			        if(regionsToConsider.get(lastIndex).getType()==CasAlignmentRegionType.INSERT){
			            regionsToConsider.remove(lastIndex);
			        }
			        
			        NucleotideSequence sequence = currentTrace.getNucleotideSequence();
			        String readId = currentTrace.getId();
			        DefaultCasPlacedReadFromCasAlignmentBuilder readBuilder= new DefaultCasPlacedReadFromCasAlignmentBuilder(readId,
			       		 gappedReference,
			       		sequence, 
			       		alignment.readIsReversed(), gappedStartOffset,
			            null);
			        
			        readBuilder.addAlignmentRegions(regionsToConsider, gappedReference);
			        read = readBuilder.build();
			        
			        AbstractAlignedReadCasVisitor.this.visitMatch(refId, read, currentTrace);
				} catch (Throwable e) {
					closeIterator();
					throw new IllegalStateException("processing read " + read + " for reference "+ refId, e);
				
				}
			}else{
				AbstractAlignedReadCasVisitor.this.visitUnMatched(currentTrace);
			}
			
		}
		

		

		@Override
		public void visitEnd() {
			closeIterator();
		}

		@Override
		public void halted() {
			closeIterator();
		}
		
		private void closeIterator(){
			IOUtil.closeAndIgnoreErrors(chainedTraceIterator);
		}
	}
	
	
}
