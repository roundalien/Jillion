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

package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf;

import static org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFUtils.HEADER_SIZE;
import static org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFUtils.ORDER_OF_SECTIONS;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.TraceDecoderException;
import org.jcvi.common.core.seq.read.trace.sanger.SangerTrace;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.Chromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ChromatogramFileVisitor;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.header.DefaultSCFHeader;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.header.DefaultSCFHeaderCodec;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.header.SCFHeader;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.header.SCFHeaderCodec;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.DefaultSectionCodecFactory;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.EncodedSection;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.Section;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.SectionCodecFactory;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.SectionDecoder;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.SectionDecoderException;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.SectionEncoder;
/**
 * {@code SCFCodecs} contains singleton implementations
 * of various {@link SCFCodec}s to parse each 
 * file format version of SCF encoded files.
 * @author dkatzel
 *
 *
 */
public enum SCFCodecs implements SCFCodec{
    /**
     * Handles SCF 3.00 (the current version) encoded SCF files.
     */
    VERSION_3{
        @Override
        public void encode(Chromatogram c, OutputStream out) throws IOException {
            this.encode(out, (SCFChromatogram)c, 3);
        }
    },
    /**
     * Handles the legacy SCF 2.00 encoded SCF files.
     */
    VERSION_2{
        @Override
        public void encode(Chromatogram c, OutputStream out) throws IOException {
            this.encode(out, (SCFChromatogram)c, 2);
        }
    };
    private SectionCodecFactory sectionCodecFactory;
    private SCFHeaderCodec headerCodec;
    
    /**
     * 
     */
    SCFCodecs(){
    	  this.headerCodec = DefaultSCFHeaderCodec.INSTANCE;
          this.sectionCodecFactory = DefaultSectionCodecFactory.INSTANCE;
    }
    

    /**
     *
    * {@inheritDoc}
     */
    private SCFChromatogram decode(String id, InputStream in) throws SCFDecoderException{
           DataInputStream dataIn = new DataInputStream(in);
           SCFHeader header= headerCodec.decode(dataIn);
           SCFChromatogramBuilder builder = new SCFChromatogramBuilder(id);
           SortedMap<Integer, Section> sectionsByOffset = createSectionsByOffsetMap(header);
           long currentOffset =HEADER_SIZE;
           for(Entry<Integer, Section> entry: sectionsByOffset.entrySet()){
              SectionDecoder sp=sectionCodecFactory.getSectionParserFor(entry.getValue(), header);
              currentOffset = sp.decode(dataIn, currentOffset, header, builder);
           }
           return builder.build();

       
    }
    public void parse(DataInputStream dataIn,SCFHeader header, ChromatogramFileVisitor visitor) throws SectionDecoderException{
        visitor.visitNewTrace();
        SortedMap<Integer, Section> sectionsByOffset = createSectionsByOffsetMap(header);
        long currentOffset =HEADER_SIZE;
        for(Entry<Integer, Section> entry: sectionsByOffset.entrySet()){
           SectionDecoder sp=sectionCodecFactory.getSectionParserFor(entry.getValue(), header);
           currentOffset = sp.decode(dataIn, currentOffset, header, visitor);
        }
        visitor.visitEndOfTrace();
        visitor.visitEndOfFile();
    }
    
    @Override
    public void parse(InputStream in, ChromatogramFileVisitor visitor)
            throws SCFDecoderException {
        visitor.visitFile();
        
        DataInputStream dataIn = new DataInputStream(in);
        SCFHeader header= headerCodec.decode(dataIn);
        parse(dataIn,header, visitor);
        
    }
    @Override
    public void parse(File scfFile, ChromatogramFileVisitor visitor)
            throws SCFDecoderException, FileNotFoundException {
        InputStream in = null;
        try{
            in = new FileInputStream(scfFile);
            parse(in,visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
        
    }
    
    public SangerTrace decode(File sangerTrace) throws TraceDecoderException,
            FileNotFoundException {
        InputStream in = null;
        try{
            in = new FileInputStream(sangerTrace);
            return decode(sangerTrace.getName(),in);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    /**
     * Since each section does not depend on the other, parsing is made
     * easier if the sections are parsed in offset order.
     * This prevents the parser from having to go back into the stream
     * which may not be possible depending on the source.  This method
     * creates a Map of the sections which is ordered by the offset.
     * @param header the {@link SCFHeader} which contains the offsets
     * for each Section.
     * @return a {@link Map} of {@link Section}s ordered by offset.
     */
    private static SortedMap<Integer, Section> createSectionsByOffsetMap(SCFHeader header) {
        SortedMap<Integer, Section> sectionsByOffset = new TreeMap<Integer, Section>();
        sectionsByOffset.put(header.getBasesOffset(), Section.BASES);
        sectionsByOffset.put(header.getSampleOffset(), Section.SAMPLES);
        sectionsByOffset.put(header.getCommentOffset(), Section.COMMENTS);
        sectionsByOffset.put(header.getPrivateDataOffset(), Section.PRIVATE_DATA);
        return sectionsByOffset;
    }

    @Override
    public void write(Chromatogram chromatogram, OutputStream out)
            throws IOException {
        encode(chromatogram, out);
        
    }
    protected abstract void encode(Chromatogram c, OutputStream out) throws IOException;
    /**
     * Encodes the given {@link SCFChromatogram} into SCF version specific
     * format.
     * @param out the OutputStream to write the encoded {@link SCFChromatogram}.
     * @param c the {@link SCFChromatogram} to write.
     * @param version which SCF format version spec to encode.
     * @throws IOException f there are any problems encoding the chromatogram
     * or any problems writing to the {@link OutputStream}.
     */
    protected final void encode(OutputStream out, SCFChromatogram c, int version) throws IOException{
        SCFHeader header = new DefaultSCFHeader();
        header.setVersion(version);
        int currentOffset = HEADER_SIZE;
        Map<Section, EncodedSection> encodedSectionMap = new EnumMap<Section, EncodedSection>(Section.class);
        for(Section s : ORDER_OF_SECTIONS){
            setOffsetFor(s, currentOffset,header);
            SectionEncoder encoder =sectionCodecFactory.getSectionEncoderFor(s, version);
            final EncodedSection encodedSection = encoder.encode(c, header);
            encodedSectionMap.put(s, encodedSection);
            currentOffset+=encodedSection.getData().limit();
        }
        ByteBuffer result = ByteBuffer.allocate(currentOffset);
        result.put(headerCodec.encode(header));
        for(Section s : ORDER_OF_SECTIONS){
            result.put(encodedSectionMap.get(s).getData());
        }
        result.rewind();
        out.write(result.array());

    }

    private static void setOffsetFor(Section s, int currentOffset,SCFHeader header) {
        if(s==Section.SAMPLES){
            header.setSampleOffset(currentOffset);
        }
        else if(s == Section.BASES){
            header.setBasesOffset(currentOffset);
        }
        else if(s== Section.COMMENTS){
            header.setCommentOffset(currentOffset);
        }
        else if(s== Section.PRIVATE_DATA){
            header.setPrivateDataOffset(currentOffset);
        }

    }
}