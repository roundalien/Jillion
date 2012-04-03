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

package org.jcvi.common.core.seq.fastx.fasta;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.seq.fastx.fasta.FastaParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaVisitor.DeflineReturnCode;
import org.jcvi.common.core.seq.fastx.fasta.FastaVisitor.EndOfBodyReturnCode;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestFastaParser {

    private static final ResourceFileServer RESOURCES = new ResourceFileServer(TestFastaParser.class);
    
    FastaVisitor mockVisitor;
    
    @Before
    public void setup(){
        mockVisitor = createMock(FastaVisitor.class);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullFileShouldThrowNPE() throws FileNotFoundException{
        FastaParser.parseFasta((File)null, mockVisitor);
    }
    @Test(expected = NullPointerException.class)
    public void nullInputStreamShouldThrowNPE(){
        FastaParser.parseFasta((InputStream)null, mockVisitor);
    }
    
    @Test
    public void parseFastaFile() throws FileNotFoundException, IOException{
        
        mockVisitor.visitFile();
        
        mockVisitor.visitLine(">IWKNA01T07A01PB2A1101R comment1\n");
        expect(mockVisitor.visitDefline(">IWKNA01T07A01PB2A1101R comment1")).andReturn(DeflineReturnCode.VISIT_CURRENT_RECORD);
        
        mockVisitor.visitBodyLine("CCTCATGTACTCTTACTTTCAATGTTTGGAGGTTGCCCGTAAGCACTTCTTCTTCCCAAT");
        mockVisitor.visitBodyLine("AACTGACGACCCACTTGTTCTTTTGAAAGTGAATCCACCAAAGCTGAAAGATGAGCTAAT");
        mockVisitor.visitBodyLine("CCTTAAGCCCATTGCTGCCTTGCATATGTCCACAGCTTGTTCCTCAGTTGGATTTTGTCG");
        mockVisitor.visitBodyLine("AAGAATGTCTACCATCCTTATTCCCCCAATCTGTGTGCTATGGCACATCTCCAATAAAGA");
        mockVisitor.visitBodyLine("TGCTAGTGGGTCTGCTGATACCGTTGCTCTTCTTACTATGTTCCTGGCAGCAATAATCAA");
        mockVisitor.visitBodyLine("GCTTTGGTCAACATCATCATTTCTCACCTCCCCTCCTGGAGTATACATTTGCTCCCAGCA");
        mockVisitor.visitBodyLine("TGTTCCCTGGGTCAAATGCAGCACTTCAATATAGACACTGCTTGTTCCACCAGCCACTGG");
        mockVisitor.visitBodyLine("GAGGAATCTCGTTTTGCGGACCAACTCTCTTTCTAGCATGTATGCAACCATTAAGGGGGC");
        mockVisitor.visitBodyLine("AATTTTGCAGTCCTGGAGTTCTTCCTTCTTCTCTTTTGTTATCGTCAGCTGTGATTCCGA");
        mockVisitor.visitBodyLine("TGTTAGTATTCTAGCTCCCACTTCATTTGGGAAAACAACTTCCATGATTACATCCTGCGC");
        mockVisitor.visitBodyLine("CTCTTTGGCACTGAGGTCTGCGTGGCCAGGGTTTATGTCAACCCTCCGTCTTATTTTAAC");
        mockVisitor.visitBodyLine("TTGATTTCTGAAGTGGACAGGGCCAAAGGTCCCGTGTTTCAATCTTTCGACTTTTTCAAA");
        mockVisitor.visitBodyLine("ATAAGTTTTATATACCTTTGGGTAGTGAACTGTACTTGTTGTTGGTCCATTCCTATTCCA");
        mockVisitor.visitBodyLine("CCATGTTACAGCTAGAGGTGATACCATTACTCGGTCTGAGCCGGCA");
        
        mockVisitor.visitLine("CCTCATGTACTCTTACTTTCAATGTTTGGAGGTTGCCCGTAAGCACTTCTTCTTCCCAAT\n");
        mockVisitor.visitLine("AACTGACGACCCACTTGTTCTTTTGAAAGTGAATCCACCAAAGCTGAAAGATGAGCTAAT\n");
        mockVisitor.visitLine("CCTTAAGCCCATTGCTGCCTTGCATATGTCCACAGCTTGTTCCTCAGTTGGATTTTGTCG\n");
        mockVisitor.visitLine("AAGAATGTCTACCATCCTTATTCCCCCAATCTGTGTGCTATGGCACATCTCCAATAAAGA\n");
        mockVisitor.visitLine("TGCTAGTGGGTCTGCTGATACCGTTGCTCTTCTTACTATGTTCCTGGCAGCAATAATCAA\n");
        mockVisitor.visitLine("GCTTTGGTCAACATCATCATTTCTCACCTCCCCTCCTGGAGTATACATTTGCTCCCAGCA\n");
        mockVisitor.visitLine("TGTTCCCTGGGTCAAATGCAGCACTTCAATATAGACACTGCTTGTTCCACCAGCCACTGG\n");
        mockVisitor.visitLine("GAGGAATCTCGTTTTGCGGACCAACTCTCTTTCTAGCATGTATGCAACCATTAAGGGGGC\n");
        mockVisitor.visitLine("AATTTTGCAGTCCTGGAGTTCTTCCTTCTTCTCTTTTGTTATCGTCAGCTGTGATTCCGA\n");
        mockVisitor.visitLine("TGTTAGTATTCTAGCTCCCACTTCATTTGGGAAAACAACTTCCATGATTACATCCTGCGC\n");
        mockVisitor.visitLine("CTCTTTGGCACTGAGGTCTGCGTGGCCAGGGTTTATGTCAACCCTCCGTCTTATTTTAAC\n");
        mockVisitor.visitLine("TTGATTTCTGAAGTGGACAGGGCCAAAGGTCCCGTGTTTCAATCTTTCGACTTTTTCAAA\n");
        mockVisitor.visitLine("ATAAGTTTTATATACCTTTGGGTAGTGAACTGTACTTGTTGTTGGTCCATTCCTATTCCA\n");
        mockVisitor.visitLine("CCATGTTACAGCTAGAGGTGATACCATTACTCGGTCTGAGCCGGCA\n");

        expect(mockVisitor.visitEndOfBody()).andReturn(EndOfBodyReturnCode.KEEP_PARSING);
        /*
        expect(mockVisitor.visitRecord("IWKNA01T07A01PB2A1101R", "comment1", 
        
                "CCTCATGTACTCTTACTTTCAATGTTTGGAGGTTGCCCGTAAGCACTTCTTCTTCCCAAT\n" +
                "AACTGACGACCCACTTGTTCTTTTGAAAGTGAATCCACCAAAGCTGAAAGATGAGCTAAT\n" +
                "CCTTAAGCCCATTGCTGCCTTGCATATGTCCACAGCTTGTTCCTCAGTTGGATTTTGTCG\n" +
                "AAGAATGTCTACCATCCTTATTCCCCCAATCTGTGTGCTATGGCACATCTCCAATAAAGA\n" +
                "TGCTAGTGGGTCTGCTGATACCGTTGCTCTTCTTACTATGTTCCTGGCAGCAATAATCAA\n" +
                "GCTTTGGTCAACATCATCATTTCTCACCTCCCCTCCTGGAGTATACATTTGCTCCCAGCA\n" +
                "TGTTCCCTGGGTCAAATGCAGCACTTCAATATAGACACTGCTTGTTCCACCAGCCACTGG\n" +
                "GAGGAATCTCGTTTTGCGGACCAACTCTCTTTCTAGCATGTATGCAACCATTAAGGGGGC\n" +
                "AATTTTGCAGTCCTGGAGTTCTTCCTTCTTCTCTTTTGTTATCGTCAGCTGTGATTCCGA\n" +
                "TGTTAGTATTCTAGCTCCCACTTCATTTGGGAAAACAACTTCCATGATTACATCCTGCGC\n" +
                "CTCTTTGGCACTGAGGTCTGCGTGGCCAGGGTTTATGTCAACCCTCCGTCTTATTTTAAC\n" +
                "TTGATTTCTGAAGTGGACAGGGCCAAAGGTCCCGTGTTTCAATCTTTCGACTTTTTCAAA\n" +
                "ATAAGTTTTATATACCTTTGGGTAGTGAACTGTACTTGTTGTTGGTCCATTCCTATTCCA\n" +
                "CCATGTTACAGCTAGAGGTGATACCATTACTCGGTCTGAGCCGGCA\n"
                )
        ).andReturn(true);
        */
        expect( mockVisitor.visitDefline(">IWKNA01T07A01PB2A1F  another comment")).andReturn(DeflineReturnCode.VISIT_CURRENT_RECORD);
        mockVisitor.visitBodyLine("ATAAAAGAACTAAGAGATCTAATGTCGCAGTCTCGCACCCGCGAGATACTAACCAAAACC");
        mockVisitor.visitBodyLine("ACTGTGGACCACATGGCCATAATCAAAAAATACACATCAGGAAGACAAGAGAAGAACCCC");
        mockVisitor.visitBodyLine("GCACTCAGGATGAAGTGGATGATGGCAATGAAATATCCAATTACAGCAGATAAAAGAATA");
        mockVisitor.visitBodyLine("ATGGAAATGATTCCCGAAAGGAATGAACAAGGACAAACCCTCTGGAGTAAAACAAACGAT");
        mockVisitor.visitBodyLine("GCCGGCTCAGACCGAGTAATGGTATCACCTCTAGCTGTAACATGGTGGAATAGGAATGGA");
        mockVisitor.visitBodyLine("CCAACAACAAGTACAGTTCACTACCCAAAGGTATATAAAACTTATTTTGAAAAAGTCGAA");
        mockVisitor.visitBodyLine("AGATTGAAACACGGGACCTTTGGCCCTGTCCACTTCAGAAATCAAGTTAAAATAAGACGG");
        mockVisitor.visitBodyLine("AGGGTTGACATAAACCCTGGCCACGCAGACCTCAGTGCCAAAGAGGCGCAGGATGTAATC");
        mockVisitor.visitBodyLine("ATGGAAGTTGTTTTCCCAAATGAAGTGGGAGCTAGAATACTAACATCGGAATCACAGCTG");
        mockVisitor.visitBodyLine("ACGATAACAAAAGAGAAGAAGGAAGAACTCCAGGACTGCAAAATTGCCCCCTTAATGGTT");
        mockVisitor.visitBodyLine("GCATACATGCTAGAAAGAGAGTTGGTCCGCAAAACGAGATTCCTCCCAGTGGCTGGTGGA");
        mockVisitor.visitBodyLine("ACAAGCAGTGTCTATATTGAAGTGCTGCATTTGACCCAGGGAACATGCTGGGAGCAAATG");
        mockVisitor.visitBodyLine("TATACTCCAGGAGGGGAGGTGAGAAATGATGATGTTGACCAAAGCTTGATTATTGCTGCC");
        mockVisitor.visitBodyLine("AGGAACATAGTAAGAAGAGCAACGGTATCAGCAGACCCACTAGCATCTCTATTGGAGATG");
        mockVisitor.visitBodyLine("TGCCATAGCACACAGATGGGGGAATAAGGATGGTAGACATTCTTCGACAAAATCCAACTG");
        mockVisitor.visitBodyLine("AGGAACAAGCTGTGGACATATGCAAGGCAGCAATGGGCTTAAGGATTAGCTCATCTTTCA");
        mockVisitor.visitBodyLine("GCTTTGGTGGATTCACT");
        
        mockVisitor.visitLine(">IWKNA01T07A01PB2A1F  another comment\n");
        mockVisitor.visitLine("ATAAAAGAACTAAGAGATCTAATGTCGCAGTCTCGCACCCGCGAGATACTAACCAAAACC\n");
        mockVisitor.visitLine("ACTGTGGACCACATGGCCATAATCAAAAAATACACATCAGGAAGACAAGAGAAGAACCCC\n");
        mockVisitor.visitLine("GCACTCAGGATGAAGTGGATGATGGCAATGAAATATCCAATTACAGCAGATAAAAGAATA\n");
        mockVisitor.visitLine("ATGGAAATGATTCCCGAAAGGAATGAACAAGGACAAACCCTCTGGAGTAAAACAAACGAT\n");
        mockVisitor.visitLine("GCCGGCTCAGACCGAGTAATGGTATCACCTCTAGCTGTAACATGGTGGAATAGGAATGGA\n");
        mockVisitor.visitLine("CCAACAACAAGTACAGTTCACTACCCAAAGGTATATAAAACTTATTTTGAAAAAGTCGAA\n");
        mockVisitor.visitLine("AGATTGAAACACGGGACCTTTGGCCCTGTCCACTTCAGAAATCAAGTTAAAATAAGACGG\n");
        mockVisitor.visitLine("AGGGTTGACATAAACCCTGGCCACGCAGACCTCAGTGCCAAAGAGGCGCAGGATGTAATC\n");
        mockVisitor.visitLine("ATGGAAGTTGTTTTCCCAAATGAAGTGGGAGCTAGAATACTAACATCGGAATCACAGCTG\n");
        mockVisitor.visitLine("ACGATAACAAAAGAGAAGAAGGAAGAACTCCAGGACTGCAAAATTGCCCCCTTAATGGTT\n");
        mockVisitor.visitLine("GCATACATGCTAGAAAGAGAGTTGGTCCGCAAAACGAGATTCCTCCCAGTGGCTGGTGGA\n");
        mockVisitor.visitLine("ACAAGCAGTGTCTATATTGAAGTGCTGCATTTGACCCAGGGAACATGCTGGGAGCAAATG\n");
        mockVisitor.visitLine("TATACTCCAGGAGGGGAGGTGAGAAATGATGATGTTGACCAAAGCTTGATTATTGCTGCC\n");
        mockVisitor.visitLine("AGGAACATAGTAAGAAGAGCAACGGTATCAGCAGACCCACTAGCATCTCTATTGGAGATG\n");
        mockVisitor.visitLine("TGCCATAGCACACAGATGGGGGAATAAGGATGGTAGACATTCTTCGACAAAATCCAACTG\n");
        mockVisitor.visitLine("AGGAACAAGCTGTGGACATATGCAAGGCAGCAATGGGCTTAAGGATTAGCTCATCTTTCA\n");
        mockVisitor.visitLine("GCTTTGGTGGATTCACT\n");
        
        expect(mockVisitor.visitEndOfBody()).andReturn(EndOfBodyReturnCode.KEEP_PARSING);
        /*
        expect(mockVisitor.visitRecord("IWKNA01T07A01PB2A1F", "another comment", 
                
                "ATAAAAGAACTAAGAGATCTAATGTCGCAGTCTCGCACCCGCGAGATACTAACCAAAACC\n" +
                "ACTGTGGACCACATGGCCATAATCAAAAAATACACATCAGGAAGACAAGAGAAGAACCCC\n" +
                "GCACTCAGGATGAAGTGGATGATGGCAATGAAATATCCAATTACAGCAGATAAAAGAATA\n" +
                "ATGGAAATGATTCCCGAAAGGAATGAACAAGGACAAACCCTCTGGAGTAAAACAAACGAT\n" +
                "GCCGGCTCAGACCGAGTAATGGTATCACCTCTAGCTGTAACATGGTGGAATAGGAATGGA\n" +
                "CCAACAACAAGTACAGTTCACTACCCAAAGGTATATAAAACTTATTTTGAAAAAGTCGAA\n" +
                "AGATTGAAACACGGGACCTTTGGCCCTGTCCACTTCAGAAATCAAGTTAAAATAAGACGG\n" +
                "AGGGTTGACATAAACCCTGGCCACGCAGACCTCAGTGCCAAAGAGGCGCAGGATGTAATC\n" +
                "ATGGAAGTTGTTTTCCCAAATGAAGTGGGAGCTAGAATACTAACATCGGAATCACAGCTG\n" +
                "ACGATAACAAAAGAGAAGAAGGAAGAACTCCAGGACTGCAAAATTGCCCCCTTAATGGTT\n" +
                "GCATACATGCTAGAAAGAGAGTTGGTCCGCAAAACGAGATTCCTCCCAGTGGCTGGTGGA\n" +
                "ACAAGCAGTGTCTATATTGAAGTGCTGCATTTGACCCAGGGAACATGCTGGGAGCAAATG\n" +
                "TATACTCCAGGAGGGGAGGTGAGAAATGATGATGTTGACCAAAGCTTGATTATTGCTGCC\n" +
                "AGGAACATAGTAAGAAGAGCAACGGTATCAGCAGACCCACTAGCATCTCTATTGGAGATG\n" +
                "TGCCATAGCACACAGATGGGGGAATAAGGATGGTAGACATTCTTCGACAAAATCCAACTG\n" +
                "AGGAACAAGCTGTGGACATATGCAAGGCAGCAATGGGCTTAAGGATTAGCTCATCTTTCA\n" +
                "GCTTTGGTGGATTCACT\n"
        )).andReturn(true);
        */
        mockVisitor.visitEndOfFile();
        
        replay(mockVisitor);
        FastaParser.parseFasta(RESOURCES.getFile("files/seqs.fasta"), mockVisitor);
        verify(mockVisitor);
    }
    
    @Test
    public void parseEmptyFile(){
        mockVisitor.visitFile();
        mockVisitor.visitEndOfFile();
        
        replay(mockVisitor);
        FastaParser.parseFasta(new ByteArrayInputStream(new byte[]{}), mockVisitor);
        verify(mockVisitor);
    }
}
