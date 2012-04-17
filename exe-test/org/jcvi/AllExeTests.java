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

package org.jcvi;

import org.jcvi.assembly.ace.AllAceExeTests;
import org.jcvi.assembly.cas.TestFilterFastqDataFromCas;
import org.jcvi.common.core.seq.fastx.fastq.TestRemoveRedundantMatePairs;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.TestChromatogram2Fasta;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.TestChromatogram2FastaMain;
import org.jcvi.fasta.TestTrimFasta;
import org.jcvi.fasta.fastq.util.TestFastQ2FastaEnd2End;
import org.jcvi.fasta.fastq.util.TestFastQFile;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author dkatzel
 *
 *
 */
@RunWith(Suite.class)
@SuiteClasses( { 
   TestFastQFile.class,
   TestFastQ2FastaEnd2End.class,
   TestTrimFasta.class,
   AllAceExeTests.class,
   TestChromatogram2Fasta.class,
   TestChromatogram2FastaMain.class,
   TestFilterFastqDataFromCas.class,
   TestRemoveRedundantMatePairs.class
}
)
public class AllExeTests {

}
