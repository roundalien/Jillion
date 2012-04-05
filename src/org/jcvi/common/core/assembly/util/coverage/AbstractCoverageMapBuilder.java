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
 * Created on Jun 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.util.coverage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.jcvi.common.core.Placed;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.util.Builder;


public abstract class AbstractCoverageMapBuilder<P extends Placed, R extends CoverageRegion<P>> implements Builder<CoverageMap<R>> {

    private P enteringObject;
    private P leavingObject;
    private Queue<P> coveringObjects;
    private Iterator<P> enteringIterator;
    private Iterator<P> leavingIterator;
    private List<CoverageRegionBuilder<P>> coverageRegionBuilders;
    private final Integer maxAllowedCoverage;
    protected abstract Iterator<P> createEnteringIterator();
    protected abstract Iterator<P> createLeavingIterator();
    
    protected abstract CoverageMap<R> build(List<CoverageRegionBuilder<P>> coverageRegionBuilders);
    
    public AbstractCoverageMapBuilder(){
        coveringObjects =  new ArrayDeque<P>();
        this.maxAllowedCoverage =null;
    }
    public AbstractCoverageMapBuilder(int maxAllowedCoverage) {
        coveringObjects =  new ArrayBlockingQueue<P>(maxAllowedCoverage);
        this.maxAllowedCoverage = maxAllowedCoverage;
    }
    @Override
    public CoverageMap<R> build() {
        initialize();
        createListOfRegionBuilders();
        return build(coverageRegionBuilders);
    }
    private void initialize() {
        enteringIterator = createEnteringIterator();
        leavingIterator = createLeavingIterator();

        enteringObject = getNextObject(enteringIterator);
        leavingObject = getNextObject(leavingIterator);
        coverageRegionBuilders = new ArrayList<CoverageRegionBuilder<P>>();
    }

    private void createListOfRegionBuilders() {
        createAllRegionBuilders();
        if (anyRegionBuildersCreated()) {
            removeLastRegionBuilder();
            removeAnyBuildersWithEmptyRanges();
            combineConsecutiveRegionsWithSameCoveringObjects();
        }
    }
    /**
     * If we restrict the max coverage
     * then we could have adjacent coverageRegions
     * that actually have the same covering objects
     * but different start and end coordinates.
     * (These would have different coverage depths
     * but we didn't add the missing read since it would
     * put us over the limit)
     */
    private void combineConsecutiveRegionsWithSameCoveringObjects() {
    	//iterate backwards to avoid concurrent modification errors
    	CoverageRegionBuilder<P> previousBuilder=null;
        for (int i = coverageRegionBuilders.size() - 1; i >= 0; i--) {
            CoverageRegionBuilder<P> builder = coverageRegionBuilders.get(i);
            if(previousBuilder!=null && previousBuilder.getElements().equals(builder.getElements())){
            	//merge region
            	builder.end(previousBuilder.end());
            	//remove previous
            	coverageRegionBuilders.remove(i+1);
            }   
            previousBuilder=builder;
        }
		
	}
	private void removeAnyBuildersWithEmptyRanges() {
        //iterate backwards to avoid concurrent modification errors
        for (int i = coverageRegionBuilders.size() - 1; i >= 0; i--) {
            CoverageRegionBuilder<P> builder = coverageRegionBuilders.get(i);
            Range range = Range.create(builder.start(), builder.end());
            if (range.isEmpty()) {
                coverageRegionBuilders.remove(i);
            }
        }

    }

    private boolean anyRegionBuildersCreated() {
        return !coverageRegionBuilders.isEmpty();
    }

    private void removeLastRegionBuilder() {
        // last is invalid, not only should it be empty,
        // but it doesn't have an end set so just chop it off.
        coverageRegionBuilders.remove(coverageRegionBuilders.size() - 1);
    }

    private void createAllRegionBuilders() {
        computeRegionsForAllEnteringObjects();
        computeRemainingRegions();
    }

    private void computeRegionsForAllEnteringObjects() {
        while (stillHaveEnteringObjects()) {
            if (isEntering()) {
                handleEnteringObject();
            } else if (isAbutment()) {
                removeAndAdvanceLeavingObject();
            } else {
                handleLeavingObject();
            }
        }
    }

    private boolean stillHaveEnteringObjects() {
        return enteringObject != null;
    }

    private void computeRemainingRegions() {
        while (stillHaveLeavingObjects()) {
            createNewRegionWithoutCurrentLeavingObject();
            skipAllLeavingObjectsWithSameEndCoordinate();
        }
    }

    private boolean stillHaveLeavingObjects() {
        return leavingObject != null;
    }

    private void skipAllLeavingObjectsWithSameEndCoordinate() {
        long endCoord = leavingObject.getEnd();
        leavingObject = getNextObject(leavingIterator);
        while (stillHaveLeavingObjects()
                && currentLeavingObjectHasEndCoordinate(endCoord)) {
            removeLeavingObjectFromPreviousRegionBuilder();
            removeAndAdvanceLeavingObject();
        }
    }

    private void removeLeavingObjectFromPreviousRegionBuilder() {
        getPreviousRegion().remove(leavingObject);
    }

    private boolean currentLeavingObjectHasEndCoordinate(long endCoord) {
        return leavingObject.getEnd() == endCoord;
    }

    private void handleEnteringObject() {
        long startCoord = enteringObject.getBegin();
        createNewRegionWithEnteringAmplicon();
        enteringObject = getNextObject(enteringIterator);
        handleAmpliconsWithSameStartCoord(startCoord);
    }

    private void handleLeavingObject() {
        createNewRegionWithoutCurrentLeavingObject();
        skipAllLeavingObjectsWithSameEndCoordinate();
    }

    private void removeAndAdvanceLeavingObject() {
        coveringObjects.remove(leavingObject);
        leavingObject = getNextObject(leavingIterator);
    }

    private boolean isAbutment() {
        return leavingObject.getEnd() == enteringObject.getBegin() - 1;

    }

    private void handleAmpliconsWithSameStartCoord(long regionStart) {
        while (stillHaveEnteringObjects()
                && enteringObject.getBegin() == regionStart) {
            // next amplicon also starts here, add this to current region
            addEnteringObjectToPreviousRegionBuilder();
            addAndAdvanceEnteringObject();
        }
    }

    private void addEnteringObjectToPreviousRegionBuilder() {
        CoverageRegionBuilder<P> builder =getPreviousRegion();
        if(this.maxAllowedCoverage !=null && builder.getElements().size()>=maxAllowedCoverage.intValue()){
            return;
        }
        getPreviousRegion().offer(enteringObject);
    }

    private void addAndAdvanceEnteringObject() {
        coveringObjects.offer(enteringObject);
        enteringObject = getNextObject(enteringIterator);
    }

    private boolean isEntering() {
        return enteringObject.getBegin() <= leavingObject.getEnd() ;
    }

    private void createNewRegionWithoutCurrentLeavingObject() {
        coveringObjects.remove(leavingObject);
        final long endCoordinate = leavingObject.getEnd();

        setEndCoordinateOfPreviousRegion(endCoordinate);
        coverageRegionBuilders.add(createNewCoverageRegionBuilder(coveringObjects, leavingObject.getEnd() + 1, maxAllowedCoverage ));

    }

    private void setEndCoordinateOfPreviousRegion(final long endCoordinate) {
        getPreviousRegion().end(endCoordinate);
    }

    private void createNewRegionWithEnteringAmplicon() {
        if (coverageRegionBuilders.size() > 0) {
            final long endCoordinate = enteringObject.getBegin() - 1;
            setEndCoordinateOfPreviousRegion(endCoordinate);
        }
        coveringObjects.offer(enteringObject);
        coverageRegionBuilders.add(createNewCoverageRegionBuilder(coveringObjects, enteringObject.getBegin(), maxAllowedCoverage ));

    }

    protected abstract CoverageRegionBuilder<P> createNewCoverageRegionBuilder(
            Collection<P> elements, long start, Integer maxAllowedCoverage);
    
    private CoverageRegionBuilder<P> getPreviousRegion() {
        return coverageRegionBuilders.get(coverageRegionBuilders.size() - 1);
    }

    private P getNextObject(Iterator<P> iterator) {
        return iterator.hasNext() ? iterator.next() : null;
    }

    
}
