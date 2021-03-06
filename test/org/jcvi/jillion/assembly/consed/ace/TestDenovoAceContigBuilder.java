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
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.jcvi.jillion.assembly.util.consensus.MostFrequentBasecallConsensusCaller;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
public class TestDenovoAceContigBuilder {
	private final Date phdDate = new Date();
	@Test
	public void oneRead(){
		AceContigBuilder builder = new AceContigBuilder("contigId", MostFrequentBasecallConsensusCaller.INSTANCE);
		
		NucleotideSequence readSequence = new NucleotideSequenceBuilder("ACGT").build();
		builder.addRead("read1", 
						readSequence, 
						0, 
						Direction.FORWARD,
						Range.of(2,5), new PhdInfo("read1", "read1.phd.1", phdDate),
						7);
		
		AceContig contig = builder.build();
		assertEquals(1, contig.getNumberOfReads());
		assertEquals(readSequence, contig.getConsensusSequence());
		assertEquals(new QualitySequenceBuilder(new byte[]{30,30,30,30}).build(), contig.getConsensusQualitySequence());
	}
	
	@Test
	public void twoReadsSameLocation(){
		AceContigBuilder builder = new AceContigBuilder("contigId", MostFrequentBasecallConsensusCaller.INSTANCE);
		
		NucleotideSequence readSequence = new NucleotideSequenceBuilder("ACGT").build();
		builder.addRead("read1", 
						readSequence, 
						0, 
						Direction.FORWARD,
						Range.of(2,5), new PhdInfo("read1", "read1.phd.1", phdDate),
						7);
		builder.addRead("read2", 
				readSequence, 
				0, 
				Direction.FORWARD,
				Range.of(2,5), new PhdInfo("read2", "read2.phd.1", phdDate),
				7);
		
		AceContig contig = builder.build();
		assertEquals(2, contig.getNumberOfReads());
		assertEquals(readSequence, contig.getConsensusSequence());
		assertEquals(new QualitySequenceBuilder(new byte[]{30,30,30,30}).build(), contig.getConsensusQualitySequence());
	}
	
	@Test
	public void twoReadsTiledAcross(){
		AceContigBuilder builder = new AceContigBuilder("contigId", MostFrequentBasecallConsensusCaller.INSTANCE);
		
		NucleotideSequence consensus = new NucleotideSequenceBuilder("ACGTACGT").build();
		builder.addRead("read1", 
				new NucleotideSequenceBuilder("ACGT").build(), 
						0, 
						Direction.FORWARD,
						Range.of(2,5), new PhdInfo("read1", "read1.phd.1", phdDate),
						7);
		builder.addRead("read2", 
				new NucleotideSequenceBuilder("TACGT").build(), 
				3, 
				Direction.FORWARD,
				Range.of(2,6), new PhdInfo("read2", "read2.phd.1", phdDate),
				7);
		
		AceContig contig = builder.build();
		assertEquals(2, contig.getNumberOfReads());
		assertEquals(consensus, contig.getConsensusSequence());
		assertEquals(new QualitySequenceBuilder(new byte[]{30,30,30,30,30,30,30,30}).build(), contig.getConsensusQualitySequence());
	}
	
	@Test
	public void threeReadsTiledAcross(){
		AceContigBuilder builder = new AceContigBuilder("contigId", MostFrequentBasecallConsensusCaller.INSTANCE);
		
		NucleotideSequence consensus = new NucleotideSequenceBuilder("ACGTACGT").build();
		builder.addRead("read1", 
				new NucleotideSequenceBuilder("ACGT").build(), 
						0, 
						Direction.FORWARD,
						Range.of(2,5), new PhdInfo("read1", "read1.phd.1", phdDate),
						7);
		builder.addRead("read2", 
				new NucleotideSequenceBuilder("TACGT").build(), 
				3, 
				Direction.FORWARD,
				Range.of(2,6), new PhdInfo("read2", "read2.phd.1", phdDate),
				7);
		
		builder.addRead("read3", 
				new NucleotideSequenceBuilder("ACGT").build(), 
				4, 
				Direction.FORWARD,
				Range.of(2,5), new PhdInfo("read3", "read3.phd.1", phdDate),
				7);
		
		AceContig contig = builder.build();
		assertEquals(3, contig.getNumberOfReads());
		assertEquals(consensus, contig.getConsensusSequence());
		assertEquals(new QualitySequenceBuilder(new byte[]{30,30,30,30,30,30,30,30}).build(), contig.getConsensusQualitySequence());
	}
	
	@Test
	public void abuttingReadsWithNoOverlapButAllBasesCovered(){
		AceContigBuilder builder = new AceContigBuilder("contigId", MostFrequentBasecallConsensusCaller.INSTANCE);
		
		NucleotideSequence consensus = new NucleotideSequenceBuilder("ACGTACGT").build();
		builder.addRead("read1", 
				new NucleotideSequenceBuilder("ACGT").build(), 
						0, 
						Direction.FORWARD,
						Range.of(2,5), new PhdInfo("read1", "read1.phd.1", phdDate),
						7);
		builder.addRead("read2", 
				new NucleotideSequenceBuilder("ACGT").build(), 
				4, 
				Direction.FORWARD,
				Range.of(2,5), new PhdInfo("read2", "read2.phd.1", phdDate),
				7);
		
		AceContig contig = builder.build();
		assertEquals(2, contig.getNumberOfReads());
		assertEquals(consensus, contig.getConsensusSequence());
		assertEquals(new QualitySequenceBuilder(new byte[]{30,30,30,30,30,30,30,30}).build(), contig.getConsensusQualitySequence());
	}
	
	@Test
	public void zeroCoverageRegionInMiddle(){
		AceContigBuilder builder = new AceContigBuilder("contigId", MostFrequentBasecallConsensusCaller.INSTANCE);
		
		NucleotideSequence consensus = new NucleotideSequenceBuilder("ACGTNNNACGT").build();
		builder.addRead("read1", 
				new NucleotideSequenceBuilder("ACGT").build(), 
						0, 
						Direction.FORWARD,
						Range.of(2,5), new PhdInfo("read1", "read1.phd.1", phdDate),
						7);
		builder.addRead("read2", 
				new NucleotideSequenceBuilder("ACGT").build(), 
				7, 
				Direction.FORWARD,
				Range.of(2,5), new PhdInfo("read2", "read2.phd.1", phdDate),
				7);
		
		AceContig contig = builder.build();
		assertEquals(2, contig.getNumberOfReads());
		assertEquals(consensus, contig.getConsensusSequence());
		assertEquals(new QualitySequenceBuilder(new byte[]{30,30,30,30,30,30,30,30,30,30,30}).build(), contig.getConsensusQualitySequence());
	}
	
	@Test
	public void zeroCoverageAtBeginningShouldShiftAllReadsSoFirstReadStartsAtNewZero(){
	AceContigBuilder builder = new AceContigBuilder("contigId", MostFrequentBasecallConsensusCaller.INSTANCE);
		
		NucleotideSequence consensus = new NucleotideSequenceBuilder("ACGTACGT").build();
		builder.addRead("read1", 
				new NucleotideSequenceBuilder("ACGT").build(), 
						10, 
						Direction.FORWARD,
						Range.of(2,5), new PhdInfo("read1", "read1.phd.1", phdDate),
						7);
		builder.addRead("read2", 
				new NucleotideSequenceBuilder("TACGT").build(), 
				13, 
				Direction.FORWARD,
				Range.of(2,6), new PhdInfo("read2", "read2.phd.1", phdDate),
				7);
		
		AceContig contig = builder.build();
		assertEquals(2, contig.getNumberOfReads());
		assertEquals(consensus, contig.getConsensusSequence());
		assertEquals(new QualitySequenceBuilder(new byte[]{30,30,30,30,30,30,30,30}).build(), contig.getConsensusQualitySequence());
		assertEquals(0, contig.getRead("read1").getGappedStartOffset());
	}
	
	@Test
	public void splitDenovoContig(){
		AceContigBuilder builder = new AceContigBuilder("contigId", MostFrequentBasecallConsensusCaller.INSTANCE);
		
		
		builder.addRead("read1", 
				new NucleotideSequenceBuilder("ACGT").build(), 
						0, 
						Direction.FORWARD,
						Range.of(2,5), new PhdInfo("read1", "read1.phd.1", phdDate),
						7);
		builder.addRead("read2", 
				new NucleotideSequenceBuilder("ACGT").build(), 
				7, 
				Direction.FORWARD,
				Range.of(2,5), new PhdInfo("read2", "read2.phd.1", phdDate),
				7);
		//before split consensus would be ACGTNNNACGT
		Range leftRange = Range.of(0,3);
		Range rightRange = Range.of(7,10);
		Map<Range, AceContigBuilder> splitMap =builder.split(Arrays.asList(leftRange, rightRange));
		assertEquals(2, splitMap.size());
		AceContig leftContig = splitMap.get(leftRange).build();
		assertEquals(1, leftContig.getNumberOfReads());
		assertEquals("ACGT", leftContig.getConsensusSequence().toString());
		assertEquals(new QualitySequenceBuilder(new byte[]{30,30,30,30}).build(), leftContig.getConsensusQualitySequence());
		
		assertTrue(leftContig.containsRead("read1"));
		assertFalse(leftContig.containsRead("read2"));
		
		AceContig rightContig = splitMap.get(rightRange).build();
		
		assertEquals(1, rightContig.getNumberOfReads());
		assertEquals("ACGT", rightContig.getConsensusSequence().toString());
		assertEquals(new QualitySequenceBuilder(new byte[]{30,30,30,30}).build(), rightContig.getConsensusQualitySequence());
		
		assertTrue(rightContig.containsRead("read2"));
		assertFalse(rightContig.containsRead("read1"));
		
	}
}
