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

package org.jcvi.common.core.seq.fastx.fastq;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fastx.NullFastXFilter;
import org.jcvi.common.core.seq.fastx.fasta.FastaParser;
import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideSequenceFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nuc.NucleotideSequenceFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nuc.NucleotideFastaDataStoreBuilderVisitor;
import org.jcvi.common.core.seq.fastx.fasta.qual.DefaultQualityFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualityFastaDataStore;
import org.jcvi.common.core.seq.fastx.fastq.DefaultFastQFileDataStore;
import org.jcvi.common.core.seq.fastx.fastq.FastQFileParser;
import org.jcvi.common.core.seq.fastx.fastq.FastQQualityCodec;
import org.jcvi.common.core.seq.fastx.fastq.FastQRecord;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.jcvi.fasta.fastq.util.Fastq2Fasta;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestFastQ2Fasta {

    FastQQualityCodec codec = FastQQualityCodec.ILLUMINA;
    ResourceFileServer RESOURCES = new ResourceFileServer(TestFastQ2Fasta.class);
    
    @Test
    public void NullFilter() throws FileNotFoundException, IOException, DataStoreException{
        ByteArrayOutputStream seqOut = new ByteArrayOutputStream();
        ByteArrayOutputStream qualOut = new ByteArrayOutputStream();
        
        Fastq2Fasta sut = new Fastq2Fasta(NullFastXFilter.INSTANCE,codec , seqOut, qualOut);
        int numberOfRecords =parseAndAssertFastQFile(seqOut, qualOut, sut);
        assertEquals(2, numberOfRecords);
    }

    private int parseAndAssertFastQFile(ByteArrayOutputStream seqOut,
            ByteArrayOutputStream qualOut, Fastq2Fasta sut) throws IOException,
            FileNotFoundException, DataStoreException {
        final File fastqFile = RESOURCES.getFile("files/example.fastq");
        FastQFileParser.parse(fastqFile, sut);
        DefaultFastQFileDataStore fastqDataStore = new DefaultFastQFileDataStore(codec);
        FastQFileParser.parse(fastqFile, fastqDataStore);
        
        NucleotideFastaDataStoreBuilderVisitor seqFastaDataStoreVisitor = DefaultNucleotideSequenceFastaFileDataStore.createBuilder();
        FastaParser.parseFasta(new ByteArrayInputStream(seqOut.toByteArray()), seqFastaDataStoreVisitor);
    
        QualityFastaDataStore qualFastaDataStore = DefaultQualityFastaFileDataStore.create(new ByteArrayInputStream(qualOut.toByteArray()));
        NucleotideSequenceFastaDataStore seqFastaDataStore = seqFastaDataStoreVisitor.build();
        for(FastQRecord fastQRecord : fastqDataStore){
            String id = fastQRecord.getId();
            assertEquals("qualities",fastQRecord.getQualities().asList(), qualFastaDataStore.get(id).getSequence().asList());
            assertEquals("seq",fastQRecord.getNucleotides().asList(), seqFastaDataStore.get(id).getSequence().asList());
        }
        return fastqDataStore.size();
    }
}
