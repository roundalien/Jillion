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
package org.jcvi.jillion.assembly;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.junit.Test;
public class TestAssemblyUtil_getUngappedComplementedValidRangeQualities {

	@Test
	public void forwardReadValidRangeIsFullLengthShouldReturnEqualSequence(){
		QualitySequence quals = new QualitySequenceBuilder(new byte[]{1,2,3,4,5,6,7,8,9,10}).build();
		
		AssembledRead read = createRead(Direction.FORWARD, Range.ofLength(quals.getLength()));
		QualitySequence actual =AssemblyUtil.getUngappedComplementedValidRangeQualities(read, quals);
		assertEquals(quals, actual);
	}
	
	@Test
	public void forwardReadValidRangeIsSubRange(){
		//                                                valid range     |           |
		QualitySequence quals = new QualitySequenceBuilder(new byte[]{1,2,3,4,5,6,7,8,9,10}).build();
		
		Range validRange = Range.of(2,8);
		AssembledRead read = createRead(Direction.FORWARD, validRange);
		QualitySequence expected= new QualitySequenceBuilder(quals)
										.trim(validRange)
										.build();
		
		QualitySequence actual =AssemblyUtil.getUngappedComplementedValidRangeQualities(read, quals);
		assertEquals(expected, actual);
	}
	
	@Test
	public void reverseReadValidRangeIsFullLengthShouldReturnReverseSequence(){
		QualitySequence quals = new QualitySequenceBuilder(new byte[]{1,2,3,4,5,6,7,8,9,10}).build();
		
		AssembledRead read = createRead(Direction.REVERSE, Range.ofLength(quals.getLength()));
		
		QualitySequence expected= new QualitySequenceBuilder(quals)
									.reverse()
									.build();
		QualitySequence actual =AssemblyUtil.getUngappedComplementedValidRangeQualities(read, quals);
		assertEquals(expected, actual);
	}
	
	@Test
	public void reverseReadValidRangeIsSubRangeShouldReturnTrimmedReverseSequence(){
		//                                                valid range     |           |
		QualitySequence quals = new QualitySequenceBuilder(new byte[]{1,2,3,4,5,6,7,8,9,10}).build();
		
		Range validRange = Range.of(2,8);
		AssembledRead read = createRead(Direction.REVERSE, validRange);
		QualitySequence expected= new QualitySequenceBuilder(quals)
										.trim(validRange)
										.reverse()
										.build();
		
		QualitySequence actual =AssemblyUtil.getUngappedComplementedValidRangeQualities(read, quals);
		assertEquals(expected, actual);
	}
	
	private AssembledRead createRead(Direction dir, Range validRange){
		AssembledRead read = createMock(AssembledRead.class);
		expect(read.getDirection()).andStubReturn(dir);
		//we don't use readInfo.getFullLength() so we can make up a value
		ReadInfo info = new ReadInfo(validRange, (int)validRange.getEnd());
		expect(read.getReadInfo()).andReturn(info);
		replay(read);
		return read;
	}
}
