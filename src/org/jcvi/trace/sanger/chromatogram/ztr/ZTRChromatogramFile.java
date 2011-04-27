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

package org.jcvi.trace.sanger.chromatogram.ztr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.QualityEncodedGlyphs;
import org.jcvi.io.IOUtil;
import org.jcvi.sequence.Peaks;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ChannelGroup;

/**
 * {@code ZTRChromatogramFile} is a {@link ZTRChromatogramFileVisitor} implementation
 * that once populated can function as a {@link ZTRChromatogram}.
 * @author dkatzel
 *
 *
 */
public final class ZTRChromatogramFile implements ZTRChromatogramFileVisitor, ZTRChromatogram{

    private ZTRChromatogram delegate;
    private ZTRChromatogramBuilder builder;
    private ZTRChromatogramFile(){
        builder = new ZTRChromatogramBuilder();
    }
    /**
     * Create a new {@link ZTRChromatogram} instance from the given
     * ZTR encoded file.
     * @param ztrFile the ZTR encoded file to parse
     * @return a new {@link ZTRChromatogram} instance containing data
     * from the given ZTR file.
     * @throws FileNotFoundException if the file does not exist
     * @throws TraceDecoderException if the file is not correctly encoded.
     */
    public static ZTRChromatogram create(File ztrFile) throws FileNotFoundException, TraceDecoderException{
        return new ZTRChromatogramFile(ztrFile);
    }
    
    /**
     * Create a new {@link ZTRChromatogram} instance from the given
     * ZTR encoded InputStream, This method will close the input stream regardless
     * if this method returns or throws an exception.
     * @param ztrInputStream the ZTR encoded input stream to parse
     * @return a new {@link ZTRChromatogram} instance containing data
     * from the given ZTR file.
     * @throws FileNotFoundException if the file does not exist
     * @throws TraceDecoderException if the file is not correctly encoded.
     */
    public static ZTRChromatogram create(InputStream ztrInputStream) throws FileNotFoundException, TraceDecoderException{
        try{
            return new ZTRChromatogramFile(ztrInputStream);
        }finally{
            IOUtil.closeAndIgnoreErrors(ztrInputStream);
        }
    }
    /**
     * Create an "unset" ZTRChromatogramFile which needs to be 
     * populated via {@link ZTRChromatogramFileVisitor}
     * method calls.  While this is still being populated
     * via visitor method calls, this object is not thread safe.
     * @return a new ZTRChromatogramFile instance that needs to be populated.
     */
    public static ZTRChromatogramFile createUnset(){
        return new ZTRChromatogramFile();
    }
    /**
     * 
     * @param ztrFile
     * @throws FileNotFoundException
     * @throws TraceDecoderException
     */
    private ZTRChromatogramFile(File ztrFile) throws FileNotFoundException, TraceDecoderException{
        this();
        ZTRChromatogramFileParser.parseZTRFile(ztrFile, this);
    }
    
    private ZTRChromatogramFile(InputStream ztrInputStream) throws TraceDecoderException{
        this();
        ZTRChromatogramFileParser.parseZTRFile(ztrInputStream, this);
    }
    
    /**
    * {@inheritDoc}
    */
    @Override
    public void visitFile() {
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfFile() {
        delegate = builder.build();
        builder =null;
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitBasecalls(String basecalls) {
        builder.basecalls(basecalls);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitPeaks(short[] peaks) {
        builder.peaks(peaks);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitClipRange(Range clipRange) {
        builder.clip(clipRange);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitComments(Map<String,String> comments) {
        builder.properties(comments);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitAPositions(short[] positions) {
        builder.aPositions(positions);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitCPositions(short[] positions) {
       builder.cPositions(positions);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitGPositions(short[] positions) {
        builder.gPositions(positions);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTPositions(short[] positions) {
        builder.tPositions(positions);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public ChannelGroup getChannelGroup() {        
        return delegate.getChannelGroup();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Map<String,String> getProperties() {
        return delegate.getProperties();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Peaks getPeaks() {
        return delegate.getPeaks();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfTracePositions() {
        return delegate.getNumberOfTracePositions();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public NucleotideEncodedGlyphs getBasecalls() {
        return delegate.getBasecalls();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public QualityEncodedGlyphs getQualities() {
        return delegate.getQualities();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range getClip() {
        return delegate.getClip();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitAConfidence(byte[] confidence) {
        builder.aConfidence(confidence);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitCConfidence(byte[] confidence) {
        builder.cConfidence(confidence);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitGConfidence(byte[] confidence) {
        builder.gConfidence(confidence);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTConfidence(byte[] confidence) {
        builder.tConfidence(confidence);
        
    }
    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public void visitNewTrace() {
        
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfTrace() {
        
    }
}
