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
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fastX.fasta.seq;

import java.util.Arrays;
import java.util.List;

import org.jcvi.fastX.fasta.FastaUtil;
import org.jcvi.fastX.fasta.qual.DefaultQualityFastaRecord;
import org.jcvi.fastX.fasta.seq.DefaultNucleotideEncodedSequenceFastaRecord;
import org.jcvi.glyph.nuc.DefaultNucleotideSequence;
import org.jcvi.glyph.nuc.NucleotideSequence;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestDefaultEncodedNuclotideFastaRecord {

    private String id = "1234";
    private String comment = "comment";
    String bases = "ACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGT-N";
    List<NucleotideGlyph> glyphs = NucleotideGlyph.getGlyphsFor(bases);
    NucleotideSequence encodedGlyphs = new DefaultNucleotideSequence(glyphs);

    DefaultNucleotideEncodedSequenceFastaRecord sut = new DefaultNucleotideEncodedSequenceFastaRecord(id, comment, bases);
    @Test
    public void constructor(){
        assertEquals(id, sut.getId());
        assertEquals(comment, sut.getComment());
        assertEquals(encodedGlyphs, sut.getValue());
        assertEquals(FastaUtil.calculateCheckSum(bases), sut.getChecksum());
        assertEquals(buildExpectedToString(comment), sut.toString());
    }
    @Test
    public void intConstructor(){
        DefaultNucleotideEncodedSequenceFastaRecord fasta = new DefaultNucleotideEncodedSequenceFastaRecord(1234,comment, bases);
        
        assertEquals(id, fasta.getId());
        assertEquals(comment, fasta.getComment());
        assertEquals(encodedGlyphs, fasta.getValue());
        assertEquals(FastaUtil.calculateCheckSum(bases), fasta.getChecksum());
        assertEquals(buildExpectedToString(comment), fasta.toString());
    }
    @Test
    public void constructorWithoutComment(){
        DefaultNucleotideEncodedSequenceFastaRecord fasta = new DefaultNucleotideEncodedSequenceFastaRecord(id, bases);
        
        assertEquals(id, fasta.getId());
        assertNull(fasta.getComment());
        assertEquals(encodedGlyphs, fasta.getValue());
        assertEquals(FastaUtil.calculateCheckSum(bases), fasta.getChecksum());
        assertEquals(buildExpectedToString(null), fasta.toString());
    }
    @Test
    public void intConstructorWithoutComment(){
        DefaultNucleotideEncodedSequenceFastaRecord fasta = new DefaultNucleotideEncodedSequenceFastaRecord(1234, bases);
        
        assertEquals(id, fasta.getId());
        assertNull(fasta.getComment());
        assertEquals(encodedGlyphs, fasta.getValue());
        assertEquals(FastaUtil.calculateCheckSum(bases), fasta.getChecksum());
        assertEquals(buildExpectedToString(null), fasta.toString());
    }
    @Test
    public void nullIdThrowsIllegalArgumentException(){
        try{
            new DefaultNucleotideEncodedSequenceFastaRecord(null, bases);
            fail("null id should throw IllegalArgumentException");
        }catch(IllegalArgumentException e){
            assertEquals("identifier can not be null", e.getMessage());
        }
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void equalsSameValues(){
        DefaultNucleotideEncodedSequenceFastaRecord sameValues = new DefaultNucleotideEncodedSequenceFastaRecord(id, 
                comment, bases);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);        
    }
    @Test
    public void equalsDifferentComment(){
        DefaultNucleotideEncodedSequenceFastaRecord sameValues = new DefaultNucleotideEncodedSequenceFastaRecord(id, 
                null, bases);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);        
    }
    @Test
    public void notEqualsDifferentBases(){
        DefaultNucleotideEncodedSequenceFastaRecord differentBasesAndChecksum = new DefaultNucleotideEncodedSequenceFastaRecord(id, 
                comment, bases.substring(2));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentBasesAndChecksum);        
    }
    
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsNotANucleotideFasta(){
        assertFalse(sut.equals(createMock(DefaultQualityFastaRecord.class)));
    }
    
    
    private String buildExpectedToString(String comment){
        StringBuilder builder = new StringBuilder();
        builder.append(">")
            .append(id);
        if(comment !=null){
            builder.append(' ').append(comment);
        }
        builder.append(FastaUtil.CR);
        builder.append(formatBasecalls());
        builder.append(FastaUtil.CR);
        return builder.toString();
    }

    private String formatBasecalls() {
        return bases.replaceAll("(.{60})", "$1"+FastaUtil.CR);
    }
    
    @Test
    public void whenFastaSequenceEndsAtEndOfLineShouldNotMakeAdditionalBlankLine(){
        char[] bases = new char[60];
        Arrays.fill(bases, 'A');
        String sixtyBases= new String(bases);
        DefaultNucleotideEncodedSequenceFastaRecord record = new DefaultNucleotideEncodedSequenceFastaRecord(id, 
                null, sixtyBases);
        String expectedStringRecord = ">"+id+"\n"+sixtyBases+"\n";
        assertEquals(expectedStringRecord, record.toString());
        
    }
}
