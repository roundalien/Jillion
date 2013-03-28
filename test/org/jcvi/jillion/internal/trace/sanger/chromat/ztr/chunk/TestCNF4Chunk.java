/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.sanger.chromat.ztr.chunk;

import java.nio.ByteBuffer;

import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.trace.sanger.chromat.DefaultChannel;
import org.jcvi.jillion.internal.trace.sanger.chromat.DefaultChannelGroup;
import org.jcvi.jillion.internal.trace.sanger.chromat.ztr.chunk.Chunk;
import org.jcvi.jillion.trace.TraceDecoderException;
import org.jcvi.jillion.trace.TraceEncoderException;
import org.jcvi.jillion.trace.sanger.chromat.ChannelGroup;
import org.jcvi.jillion.trace.sanger.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.sanger.chromat.ztr.ZtrChromatogramBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestCNF4Chunk {

    private static final String bases = "ACGT-";
    private static final byte[] aconf = new byte[]{40,0,0,0,0};
    private static final byte[] cconf = new byte[]{0,30,0,0,0};
    private static final byte[] gconf = new byte[]{0,0,35,0,0};
    //- confidence is put in T
    private static final byte[] tconf = new byte[]{0,0,0,38,37};
    Chunk sut = Chunk.CONFIDENCES;
    
    private static final byte[] encodedBytes;
    static{
    	ByteBuffer buf = ByteBuffer.allocate(bases.length()*4 +1);
        buf.put((byte)0);//padding
        //called bases
        buf.put(aconf[0]);
        buf.put(cconf[1]);
        buf.put(gconf[2]);
        buf.put(tconf[3]);
        buf.put(tconf[4]);
        //uncalled confidences
        buf.put(cconf[0]);
        buf.put(gconf[0]);
        buf.put(tconf[0]);
        
        buf.put(aconf[1]);
        buf.put(gconf[1]);
        buf.put(tconf[1]);
        
        buf.put(aconf[2]);
        buf.put(cconf[2]);
        buf.put(tconf[2]);
        
        buf.put(aconf[3]);
        buf.put(cconf[3]);
        buf.put(gconf[3]);
        
        buf.put(aconf[4]);
        buf.put(cconf[4]);
        buf.put(gconf[4]);
        
        encodedBytes = buf.array();
    }
    @Test
    public void parse() throws TraceDecoderException{
        ZtrChromatogramBuilder struct = new ZtrChromatogramBuilder("id");
        struct.basecalls(new NucleotideSequenceBuilder(bases).build());
        
        sut.parseData(encodedBytes, struct);
        
        assertArrayEquals(aconf, struct.aConfidence());
        assertArrayEquals(cconf, struct.cConfidence());
        assertArrayEquals(gconf, struct.gConfidence());
        assertArrayEquals(tconf, struct.tConfidence());
    }
    @Test
    public void encode() throws TraceEncoderException{
    	ZtrChromatogram chromatogram = createMock(ZtrChromatogram.class);
    	ChannelGroup channelGroup = new DefaultChannelGroup(
    			new DefaultChannel(aconf,new short[0]), 
    			new DefaultChannel(cconf,new short[0]), 
    			new DefaultChannel(gconf,new short[0]), 
    			new DefaultChannel(tconf,new short[0]));

    	expect(chromatogram.getNucleotideSequence()).andReturn(new NucleotideSequenceBuilder(bases).build());
    	expect(chromatogram.getChannelGroup()).andReturn(channelGroup);
    
    	replay(chromatogram);
    	byte[] actual =sut.encodeChunk(chromatogram);
    	assertArrayEquals(encodedBytes, actual);
    	verify(chromatogram);
    }
    
}
