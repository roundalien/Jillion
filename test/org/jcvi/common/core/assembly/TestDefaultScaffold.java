/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jan 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.DefaultPlacedContig;
import org.jcvi.common.core.assembly.DefaultScaffold;
import org.jcvi.common.core.assembly.PlacedContig;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import java.util.HashSet;
import java.util.NoSuchElementException;

public class TestDefaultScaffold {

    DefaultScaffold scaffold;
    Set<PlacedContig> placedContigs;

    long scaffoldLength;

    @Before
    public void setUp() {
        placedContigs = new HashSet<PlacedContig>(5);
        placedContigs.add(new DefaultPlacedContig("contig1", Range.create(28,100), Direction.FORWARD));
        placedContigs.add(new DefaultPlacedContig("contig2", Range.create(250,375), Direction.REVERSE));
        placedContigs.add(new DefaultPlacedContig("contig3", Range.create(320,383), Direction.REVERSE));
        placedContigs.add(new DefaultPlacedContig("contig4", Range.create(390,500), Direction.REVERSE));
        placedContigs.add(new DefaultPlacedContig("contig5", Range.create(628,707), Direction.FORWARD));
        scaffoldLength = 707-28+1;


        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        for ( PlacedContig contig : placedContigs ) {
            builder.add(contig.getContigId(),contig.getValidRange(), contig.getDirection());
        }
        scaffold = builder.build();
    }

    @Test
    public void shiftingContigsShouldAdjustCoordinatesToStartAtZero(){
    	 ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
    	 builder.shiftContigs(true);
    	 for ( PlacedContig contig : placedContigs ) {
    		 builder.add(contig);    
    	 }
    	 Scaffold shiftedScaffold = builder.build();
    	 long shiftOffset = getFirstCoveredRange(scaffold).getBegin();
    	 for(PlacedContig shiftedContig : shiftedScaffold.getPlacedContigs()){
    		 assertShiftedCoorrectly(scaffold.getPlacedContig(shiftedContig.getContigId()), shiftedContig,shiftOffset);
    	 }
    }
    private Range getFirstCoveredRange(DefaultScaffold scaffold) {
		for(CoverageRegion<?> region :scaffold.getContigCoverageMap()){
			if(region.getCoverageDepth()>0){
				return region.asRange();
			}
		}
		throw new IllegalStateException("scaffold is empty");
	}

	private void assertShiftedCoorrectly(PlacedContig unshifted,
			PlacedContig shifted, long shiftOffset) {
		assertEquals(unshifted.getContigId(), shifted.getContigId());
		assertEquals(unshifted.getDirection(), shifted.getDirection());
		assertEquals(unshifted.asRange().shiftLeft(shiftOffset), shifted.asRange());
	}

	@Test
    public void testGetPlacedContig() {
        PlacedContig contig = placedContigs.iterator().next();
        assertEquals(scaffold.getPlacedContig(contig.getContigId()),contig);
    }

    @Test
    public void testGetPlacedContigs() {
        assertEquals(scaffold.getPlacedContigs(),placedContigs);
    }

    @Test
    public void testGetScaffoldLength() {
        assertEquals(scaffold.getLength(),scaffoldLength);
    }

    @Test
    public void testGetNumberOfContigs() {
        assertEquals(scaffold.getNumberOfContigs(),placedContigs.size());
    }



    @Test(expected= NoSuchElementException.class)
    public void testUnkonwnContigCoordinateConversionTest() {
        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        DefaultScaffold scaffold = builder.build();
        
        scaffold.convertContigRangeToScaffoldRange("nonexistantContig",Range.create(10,48));
    }

    @Test(expected= IllegalArgumentException.class)
    public void testInvalidContigRangeCoordinateConversionTest() {
        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        builder.add("contig1", Range.create(0,100), Direction.FORWARD);
        DefaultScaffold scaffold = builder.build();

        scaffold.convertContigRangeToScaffoldRange("contig1",Range.create(50,150));
    }

    @Test(expected= IllegalArgumentException.class)
    public void testInvalidContigDirectionCoordinateConversionTest() {
        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        builder.add("contig1", Range.create(0,100), Direction.UNKNOWN);
        DefaultScaffold scaffold = builder.build();

        scaffold.convertContigRangeToScaffoldRange("contig1",Range.create(20,50));
    }

    @Test(expected= IllegalArgumentException.class)
    public void testInvalidContigDirectionCoordinateConversionTest2() {
        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        builder.add("contig1", Range.create(0,100), Direction.NONE);
        DefaultScaffold scaffold = builder.build();

        scaffold.convertContigRangeToScaffoldRange("contig1",Range.create(20,50));
    }
    
    @Test
    public void testSingleForwardContigCoordinateConversionTest() {
        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        builder.add("contig1", Range.create(37,164), Direction.FORWARD);
        DefaultScaffold scaffold = builder.build();

        Range contigRange = Range.create(10,48);
        Range expectedRange = Range.create(47,85);
        Range convertedRange = scaffold.convertContigRangeToScaffoldRange("contig1",contigRange);

        Assert.assertEquals(expectedRange,convertedRange);
    }

    @Test
    public void testSingleReverseContigCoordinateConversionTest() {
        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        builder.add("contig1", Range.create(37,164), Direction.REVERSE);
        DefaultScaffold scaffold = builder.build();

        Range contigRange = Range.create(10,48);
        Range expectedRange = Range.create(116,154);
        Range convertedRange = scaffold.convertContigRangeToScaffoldRange("contig1",contigRange);

        Assert.assertEquals(expectedRange,convertedRange);
    }

    @Test
    public void testMultipleForwardContigCoordinateConversionTest() {
        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        builder.add("contig1", Range.create(37,164), Direction.FORWARD);
        builder.add("contig2", Range.create(293,568), Direction.FORWARD);
        DefaultScaffold scaffold = builder.build();

        Range contigRange = Range.create(31,56);
        Range expectedRange = Range.create(324,349);
        Range convertedRange = scaffold.convertContigRangeToScaffoldRange("contig2",contigRange);

        Assert.assertEquals(expectedRange,convertedRange);
    }

    @Test
    public void testMultipleReverseContigCoordinateConversionTest() {
        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        builder.add("contig1", Range.create(37,164), Direction.FORWARD);
        builder.add("contig2", Range.create(293,568), Direction.REVERSE);
        DefaultScaffold scaffold = builder.build();

        Range contigRange = Range.create(20,35);
        Range expectedRange = Range.create(533,548);
        Range convertedRange = scaffold.convertContigRangeToScaffoldRange("contig2",contigRange);

        Assert.assertEquals(expectedRange,convertedRange);
    }

}
