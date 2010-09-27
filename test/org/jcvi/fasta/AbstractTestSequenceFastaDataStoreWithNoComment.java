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
 * Created on Feb 3, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.io.File;
import java.io.IOException;

import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.io.fileServer.ResourceFileServer;

public  abstract class AbstractTestSequenceFastaDataStoreWithNoComment {
    static final String FASTA_FILE_PATH = "files/noComment.fasta";
    ResourceFileServer RESOURCES = new ResourceFileServer(AbstractTestSequenceFastaMapWithNoComment.class);
    
DefaultEncodedNucleotideFastaRecord hrv_61 = new DefaultEncodedNucleotideFastaRecord("hrv-61",null,
        "TTAAAACTGGGTCTGGGTTGCTCCCACCCAGACCACCCATGTGGTGTTGTACACTGTTAT" +
        "TCCGGTAACTTTGTACGCCAGTTTTGAACTCCCCTACCCCTTTTGTAACTTAGAAGCTAA" +
        "ACACATCGACCAATAGCAGGCAATCACCCAGATTGCTTATGGTCAAGTACTTCTGTTTCC" +
        "CCGGTCTCCTCTGATATGCTCTACCAGGGCAAAAACAGAGTAGATCGTTATCCGCAAGAT" +
        "GCCTACGCAACGCCTAGTAACACTTTGAAGTGTTTTTGGTTGGTCGCTCAGTTACAACCC" +
        "CAGTAATAGACCTGGCAGATGAGGCTAGATATCCCCCACTGGCGACAGTGATCTAGCCTG" +
        "CGTGGCTGCCTGCACACCCTTTATGGGTGTGAAGCCAAACATATGACAAGGTGCGAAGAG" +
        "CCACGTGTGCTCATCTTGAGTCCTCCGGCCCCTGAATGCGGCTAACCTCAACCCTGCAGC" +
        "CATTGCTCACAAGCCAGTGGGTATGTGGTCGTAATGAGAAATTGCGGGACGGGACCGACT" +
        "ACTTTGGGTGTCCGTGTTTCACTTTTTTACCTTATTTTGCTTATGGTGACAATATATATA" +
        "CAGATATATATTGACACCATGGGCGCTCAGGTATCCAGACAAAACGTTGGTACTCATTCT" +
        "ACTCAGAATTCTGTTTCAAATGGTTCTAGTTTAAACTATTTTAACATTAATTACTTTAAA" +
        "GATGCGGCTTCAAGTGGAGCATCCAAATTGGAATTTTCACAAGACCCCAGCAAATTTACA" +
        "GATCCAGTCAAGGATGTTTTAGAGAAGGGAATACCAACTCTACAATCACCAACTGTTGAA" +
        "GCATGTGGTTATTCAGATAGAATCATCCAAATTACAAGAGGTGACTCCACAATTACCTCA" +
        "CAAGATGTTGCTAATGCTGTAGTTGGGTATGGAGTTTGGCCACATTACTTAACACCACAA" +
        "GATGCCACTGCTATAGATAAACCTTCTAAGCCTGACACATCTTCTAATAGATTTTACACA" +
        "TTAGAAAGCCAGACCTGGACCAATGAATCCAAGGGCTGGTGGTGGAAATTACCAGATGCT" +
        "TTGAAAGACATGGGTATTTTTGGAGAAAACATGTTTTACCATTTTCTAGGCAGAAGTGGA" +
        "TACACGGTTCATGTCCAGTGCAACGCCAGCAAGTTTCATCAGGGAACTCTTCTGGTAGCA" +
        "ATGATACCAGAGCACCAGCTAGCTACCGCTGTAGGCGGCAGTGTATCTGCAGGATACAAT" +
        "TACACACACCCTGGTGAAAAGGGTAGGGATGTTGGAAAATTAAACCGTGAATCCAATCCA" +
        "AGACAACCTAGTGATGACAATTGGTTAAATTTTGATGGCACATTATTAGGAAACTTGACA" +
        "ATATTTCCTCACCAATTCATTAACCTTAGAAGTAACAACTCGGCTACTATTATAGTACCA" +
        "TATGTTAATGCTGTCCCAATGGATTCAATGCTAAGGCACAATAATTGGAGTTTAGTCATC" +
        "ATACCCATTTGTGAGCTTAGAGGTAGCGGTGTTGATCTCACCATCCCTATTACAATTTCC" +
        "ATTAGTCCTATGTGTGCTGAATTCTCTGGAGCAAGAGCAAACAGCCAGGGGTTGCCGGTC" +
        "TTACTAACACCCGGTTCTGGCCAATTCATGACAACAGATGATTTTCAATCTCCCAGTGCT" +
        "CTACCCTGGTACCATCCTACCAAGGAAATTTCAATACCTGGACAGGTTAGAAATCTAGTG" +
        "GAATTATGCCAAGTAGATACCTTGATACCAATTAACAACACAGAAGCCAACATCAGAAAT" +
        "AAAAATATGTACACCGTACAACTTGGTGGTGAACCTGACCCTACAACCCCGGTTTTCACA" +
        "ATTAGGGTTGATATTGCATCACAACCATTAGCCACTACACTTATAGGTGAAGTTTCCAGC" +
        "TACTACACACATTGGACTGGGAGTGTTAGATTTAGTTTTATGTTCTGTGGATCAGCACTT" +
        "ACAACACTAAAGTTATTGATTGCATATACCCCACCTGGTATTAGAGTTCCAAGAAATAGA" +
        "AAAGAAGCAATGCTTGGTACACATCTAATATGGGACGTTGGTCTACAGTCCACAGTGTCA" +
        "ATGGTGGTTCCCTGGGTCAGTGCTAGTCATTATAGAAACACAACCCCAGATACATATTCA" +
        "ATAGCTGGCTTCATTACATGCTGGTATCAAACAAAACTGGTAGTTCCCCCAAACACAGCC" +
        "TCCACAGCTGATATGCTGTGCTTTGTTTCAGGGTGCAAAGATTTCTGCCTACGCATGGCA" +
        "AGAGATACTAACTTACACAAGCAAAGTGGACCTATTGAGCAAAACCCTGTGGAAAGATAT" +
        "GTAGATGAAGTTTTAAATGAAGTGCTTGTAGTCCCAAACATTAATCAGAGCAACCCTACA" +
        "ACATCCAACTCAGCACCAGTCTTAGACGCTGCTGAAACAGGTCATACCAGCAATGTTCAA" +
        "CCGGAGGACATGATTGAAACACGATATGTTCAAACCTCACAGACAAGAGATGAAATGAGT" +
        "GTAGAAAGTTTCTTGGGTAGATCAGGGTGCATACATATGTCAACATTAAATATAAACTAT" +
        "GATAACTATGATGATTCTATTGAAAACTTCAAGGTGTGGAAAATAAACCTGCAAGAGATG" +
        "GCACAAATACGTAGAAAATTTGAGTTGTTCACATATGCTAGATTTGATTCAGAGATTACA" +
        "ATTGTACCTTGTGTTGCTGGGCAAGGTGGTGACATTGGACACGTGGTCATGCAATACATG" +
        "TATGTTCCACCTGGTGCACCTACACCTGAGAAAAGAAATGATTTCACATGGCAATCAGGC" +
        "ACAAATGCATCTGTTTTCTGGCAACATGGTCAAGCTTATCCCAGATTTTCATTACCATTC" +
        "CTAAGTATTGCATCTGCATATTATATGTTTTATGATGGTTATGATGGAGATTCTGAAATA" +
        "ACGCGCTATGGAACATCAGTGACAAATGATATGGGTGCATTGTGCTTTAGAATAGTAACT" +
        "GAACAGCATACAAATCAAGTTAAAATCACAACTAGGATTTACCATAAAGCTAAACATGTT" +
        "AAAGTCTGGTGTCCTAGACCCCCCAGAGCAGTGGAATATACTAATGTGCATTTGACCAAT" +
        "TACAAGCCCAAAGATAGTGAAAAACAAGTTACCACTTTCATCAAACCTAGAGCTAACTTA" +
        "AGAGAGATTAGAACATTTGGGCCCAGTGACATGTATGTGCATGTCGGAAATTTAATATAT" +
        "AGAAATCTGCACTTATTTAATTCAGAGGCACATGATTCTGTATTAGTATCCTACTCATCA" +
        "GATTTAGTCATTTACCGCACAAACACCGTAGGTGATGACTTTATACCAACATGTGATTGC" +
        "ACACAAGCTACATATTATTGTAAGCACAAAAACAGGTACTTCCCAATCACAGTCACTAGT" +
        "CATGATTGGTACGAAATCCAAGAGAGTGAATATTATCCAAAACATATTCAATATAATCTA" +
        "TTAATTGGTGAGGGTCCTTGTGAACCAGGTGATTGTGGAGGGAAGTTACTGTGTAAACAT" +
        "GGAGTCATTGGCATTATAACTGCTGGTGGTGAAGGGCATGTTGCCTTCATAGATTTAAGA" +
        "CATTTCCTGTGTGCTGAAGAGCAAGGAGTAACAGATTATATCCACATGTTAGGTGAGGCT" +
        "TTTGGAAATGGCTTTGTAGATAGTGTTAAAGAACATGTGAATGCTATAAATCCTGTAAAT" +
        "AATATTAGTAAAAAAGTAATTAAGTGGTTGTTGAGAATTGTATCTGCTATGGTTATAATA" +
        "ATCAGAAACTCATCTGATCCCCAAACCGTTGTTGCCACTTTAACATTAATTGGATGTTCT" +
        "GGGTCACCATGGAGATTCTTAAAAGAAAAATTCTGTAAATGGACCCAACTAACTTACATT" +
        "CATAAAGAATCTGATTCTTGGCTCAAAAAATTCACCGAAATGTGCAATGCAGCAAGAGGA" +
        "CTTGAATGGATAGGGAATAAAATATCAAAATTTATAGAATGGATGAAATCAATGTTGCCA" +
        "CAAGCACAGCTTAAAGTTAAATATTTAAATGAGTTGAAAAAATTAAACCTTCTTGAAAAG" +
        "CAGATTGAGAACCTGAGAAGTGCTGATTCTAAAACTCAAGAAAGGATTAAGGTTGAGATA" +
        "GACACATTACATGATTTGTCCTGTAAATTTCTTCCACTTTATGCTAGTGAAGCCAAGAGG" +
        "ATCAAGGTGATACACAATAAATGTAATACTATCATAAAACAAAAGAAAAGGAGTGAACCG" +
        "GTTGCAGTGATGATACATGGTTCACCAGGTACTGGAAAATCCATAACAACAAATTTTCTG" +
        "GCAAGAATGATAACAAATGAGAGTGACATATATTCATTACCACCTGATCCTAAATATTTT" +
        "GATGGGTACGATCAGCAGTCTGTTGTCATAATGGATGATATCATGCAAAATCCAGATGGT" +
        "GAAGACATGGCACTATTTTGTCAGATGGTCTCTAGTGTAACTTTTATACCCCCAATGGCT" +
        "GATTTACCTGATAAAGGAAAACCCTTTGACTCCAGATTTGTTCTTTGCAGTACTAACCAC" +
        "TCCATGTTGGCCCCTCCAACAATTACATCCCTTCAGGCAATGAATAGACGCTTCTTTCTT" +
        "GATTTAGATATTGTGGTACATGATAATTACAAAGATTCACAAGGGAAACTAAATGTGTCT" +
        "AAAGCTTTTAAACCATGTGATGTTGGAACAAAAATAGGTAACGCACGTTGTTGTCCATTC" +
        "ATTTGTGGCAAGGCAGTTACATTCAAAGATCGCAACACGTGCTTGAGCTACCCATTGAGT" +
        "CAAATTTACAACCTAATTCTCCAAGAGGACAAGAGGCGTACTCATGTTGTAGATGTTATG" +
        "TCTGCTATATTTCAGGGACCAATCTCTATGGAAGTACCTCCTCCTCCTGCAATAACTGAT" +
        "TTGCTCCGATCTGTTAAGACACCTGAAGTTATTAAATATTGTGAAGACAATAAATGGACC" +
        "ATTCCAGCTGATTGTAAGATTGAGAGGGACTTAATTCTTGCTAACAACATTATTACAATT" +
        "ATAGCAAACATAATTAGCATAGCAGGTATTATCTACATTATATATAAACTATTTTGCTCA" +
        "TTTCAAGGACCATACTCTGGTGAACCCAAACCTAAAACAAAAATTCCTGAGAGGAGAGTT" +
        "GTGACACAGGGTCCTGAGGAAGAGTTTGGTCGCTCCCTGATCAAACATAACACTTGTGTG" +
        "GTGACCACTGATAATGGAAAATTTACTGGATTGGGCATTTATGACAAACTCATGATTTTA" +
        "CCCACCCATGCTGACCCTGGAAAAGAAATTTACATCAATGGGATTGCAACAAGGGTCAGT" +
        "GATTCATATGATATGTACAATAAACAAGGAATTAAACTAGAGATTACAGCTGTACTATTG" +
        "GATAGGAATGAAAAATTTAGAGACATTAGGAGATATATACCAGAGAGGGAAGATGATTAC" +
        "CCTGAGTGTAACCTAGCACTGGTAGCAAATCAGCCTGAACCAACTATCATAAGTGTGGGT" +
        "GATGTCATTTCTTACGGCAATATATTGCTTAGTGGCAACCAAACAGCACGCATGCTTAAA" +
        "TACAACTATCCCACAAAATCTGGGTACTGTGGTGGAGTCTTGTACAAAATAGGGCAAATT" +
        "ATAGGGATACATGTGGGTGGAAATGGTAGAGATGGATTTTCAGCAATGTTACTTAGGTCC" +
        "TATTTTAGTGAAACTCAGGGTGAAATCATTACATCAAAGAAAGTTCATGAATGTGGGTAT" +
        "CCAACTATACACACCCCTGCAAAAACAAAGTTACAGCCTAGTGTTTTCTTTGATGTGTTT" +
        "GAAGGTTCAAAAGAACCAGCAGTGCTCACAGAAAAGGATCCCCGTTTAACAACTGATTTT" +
        "AATCAAGCCTTATTTTCCAAATACAAAGGAAATGTTGAATGCAACATGTCTGAACACATG" +
        "AAGGTTGCCATATCACATTATTCTGCTCAATTAATGACTTTAGATATAGACCCTAGCAAT" +
        "ATAACCCTAGAGGAGAGTGTGTTTGGCACTGAAGGTTTGGAAGCTCTCGATCTAAATACT" +
        "AGTGCAGGTTTCCCATACATTAGCATGGGTATCCGAAAGAGAGATTTAATAAACAATAGT" +
        "ACAAAAGATATCACCAAGTTAAAAGTGGCCCTTGACAAGTATGGAGTTGATTTACCTATG" +
        "GTAACATTCCTTAAAGATGAACTCAGAAAGAAAGGAAAAATCATGGCTGGCAAAACAAGA" +
        "GTCATTGAAGCAAGCAGTATCAACGATACAGTTGCTTTCAGAATGACATATGGTAAACTC" +
        "TTCTCTAGTTTCCATAAAAACCCAGGGATCATCACAGGTTCAGCGGTAGGATGTGATCCC" +
        "GAAACATTTTGGTCAAAAATCCCAGTTATGCTCGATGGGGAGTGCATAATGGCATTTGAT" +
        "TACACTAATTATGATGGTAGTATACATCCCATCTGGTTTGAAGCACTCAAACAAGTTCTC" +
        "ATAAATTTATCCTTTGAGCATAGGTTAATAGACAGGTTATGTAAATCAAAACACATATTC" +
        "AAGGACACTTACTATGAAGTTGAAGGGGGGGTACCGTCAGGATGTTCTGGCACTAGTATT" +
        "TTTAATACAATGATTAACAATGTAATAATAAGAACTCTAGTCCTTGATGCATACAAATAT" +
        "ATAGATTTAGATAAGCTTAAAATAATAGCATATGGTGATGATGTCATCTTCTCTTACAAA" +
        "TACCCTCTGGATATGGAAGCAATTGCTGCAGAAGGAAACAAGTATGGTTTAACAATCACA" +
        "CCAGCTGACAAGTCTGATACCTTCAAGAAGCTTGATTACAGCAGTGTTACATTCTTGAAG" +
        "AGAGGTTTCAAACAAGATGACAAATATTCTTTTTTGATTCATCCTACTTTTCCAATTTCT" +
        "GAAATACATGAATCAATTAGGTGGACTAAGAAGCCCTCACAAATGCAGGAACACGTACTA" +
        "TCCTTGTGTCACTTAATGTGGCATAATGGCCGGGATGTGTATAAAGAATTCGAAAGGAAA" +
        "ATACGCAGTGTTAGCGCTGGACGTGCACTGTATATTCCTCCTTACGATCTCCTGTTGCAT" +
        "GAGTGGTATGAAAAATTTTAATATATAGAAATAATAAACAATTAGTTTCTTAGTTTTAT" );



protected abstract DataStore<NucleotideSequenceFastaRecord> buildMap(File file) throws IOException;
}
