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
package org.jcvi.jillion.core.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.jcvi.jillion.core.util.GenomeStatistics.GenomeStatisticsBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestNG90Computations {

	
	@Parameters
	public static List<Object[]> params(){
		return Arrays.asList(
					new Object[]{ 70, 100, new int[]{80,70,50,40,30,20}},
					new Object[]{ 70, 100, new int[]{80,70,50,40,30,20, 10, 5}}
				
				);
	}
	
	
	private final int expectedAnswer;
	private final int genomeLength;
	
	private final int[] values;
	
	
	public TestNG90Computations(int expectedAnswer, int genomeLength, int[] values) {
		this.expectedAnswer = expectedAnswer;
		this.genomeLength = genomeLength;
		this.values = values;
	}
	
	
	
	
	@Test
	public void intBuilder(){
		GenomeStatisticsBuilder builder = GenomeStatistics.ng90Builder(genomeLength);
		for(int i : values){
			builder.add(i);
		}
		
		assertEquals(expectedAnswer, builder.build().getAsInt());
	}
	
	@Test
	public void longBuilder(){
		GenomeStatisticsBuilder builder = GenomeStatistics.ng90Builder(genomeLength);
		for(int i : values){
			builder.add( (long) i);
		}
		
		assertEquals(expectedAnswer, builder.build().getAsInt());
	}
	
	@Test
	public void intStream(){
		IntStream stream = IntStream.of(values);
		
		assertEquals(expectedAnswer, GenomeStatistics.ng90(stream, genomeLength).getAsInt());
	}
	
	@Test
	public void intXStream(){
		IntStream stream = IntStream.of(values);
		
		assertEquals(expectedAnswer, GenomeStatistics.ngX(stream, .9D, genomeLength).getAsInt());
	}
	
	@Test
	public void parallelIntStream(){
		IntStream stream = IntStream.of(values).parallel();
		
		assertEquals(expectedAnswer, GenomeStatistics.ng90(stream,genomeLength).getAsInt());
	}
	
	@Test
	public void longStream(){
		LongStream stream = IntStream.of(values).asLongStream();
		
		assertEquals(expectedAnswer, GenomeStatistics.ng90(stream, genomeLength).getAsInt());
	}
	
	@Test
	public void longXStream(){
		LongStream stream = IntStream.of(values).asLongStream();
		
		assertEquals(expectedAnswer, GenomeStatistics.ngX(stream,  .9D, genomeLength).getAsInt());
	}
	
	@Test
	public void IntegerStream(){
		int actual = IntStream.of(values)
									.mapToObj(Integer::valueOf)
									.collect(GenomeStatistics.ng90Collector(genomeLength))
									.getAsInt();
		
		assertEquals(expectedAnswer, actual);
	}
	
	@Test
	public void LongStream(){
		int actual = IntStream.of(values)
									.mapToObj(Long::valueOf)
									.collect(GenomeStatistics.ng90Collector(genomeLength))
									.getAsInt();
		
		assertEquals(expectedAnswer, actual);
	}
	
	@Test
	public void XStream(){
		int actual = IntStream.of(values)
									.mapToObj(Long::valueOf)
									.collect(GenomeStatistics.ngXCollector(genomeLength, .9D))
									.getAsInt();
		
		assertEquals(expectedAnswer, actual);
	}
	
}
