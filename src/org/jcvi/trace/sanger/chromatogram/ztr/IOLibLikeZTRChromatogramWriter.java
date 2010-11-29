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

import java.io.OutputStream;

import org.jcvi.trace.TraceEncoderException;
import org.jcvi.trace.sanger.chromatogram.Chromatogram;
import org.jcvi.trace.sanger.chromatogram.ztr.DefaultZTRChromatogramWriter.DefaultZTRChromatogramWriterBuilder;
import org.jcvi.trace.sanger.chromatogram.ztr.data.DeltaEncodedData;
import org.jcvi.trace.sanger.chromatogram.ztr.data.ShrinkToEightBitData;
import org.jcvi.trace.sanger.chromatogram.ztr.data.DeltaEncodedData.Level;
/**
 * {@code IOLibLikeZTRChromatogramWriter} is a {@link ZTRChromatogramWriter}
 * implementation that performs the same encoding operations in the same order
 * as the staden IO_Lib C module.  Experiments have shown that 
 *  IOLibLikeZTRChromatogramWriter
 * will encode valid ZTR files that have about a 5% larger filesize.
 * This is probably due to the standard Java implementation of zip does not allow
 * changing the "windowbits" size which could result in better
 * compression.
 * @author dkatzel
 *
 */
public enum IOLibLikeZTRChromatogramWriter implements ZTRChromatogramWriter{
	/**
	 * Singleton instance of {@link IOLibLikeZTRChromatogramWriter}.
	 */
	INSTANCE;
	/**
	 * This is the guard value that IO_Lib uses for run length
	 * encoding its confidence values, I guess
	 * it assumes no traces will ever
	 * get a quality value of 77.
	 */
	public static final byte IO_LIB_CONFIDENCE_RUN_LENGTH_GUARD_VALUE = (byte)77;
	private final ZTRChromatogramWriter writer;
	
	{
		//these are the same encoders with the same parameters
		//with in the same order as the stadden IO_Lib C library's
		//ZTR 1.2 writer.
		DefaultZTRChromatogramWriterBuilder builder = new DefaultZTRChromatogramWriterBuilder();
		builder.forBasecallChunkEncoder()
	        .addZLibEncoder();
		builder.forPositionsChunkEncoder()
			.addDeltaEncoder(DeltaEncodedData.SHORT, Level.DELTA_LEVEL_3)
			.addShrinkEncoder(ShrinkToEightBitData.SHORT_TO_BYTE)
			.addFollowEncoder()
			.addRunLengthEncoder()
			.addZLibEncoder();
		builder.forConfidenceChunkEncoder()
			.addDeltaEncoder(DeltaEncodedData.BYTE, Level.DELTA_LEVEL_1)
			.addRunLengthEncoder(IO_LIB_CONFIDENCE_RUN_LENGTH_GUARD_VALUE)
			.addZLibEncoder();
		builder.forPeaksChunkEncoder()
			.addDeltaEncoder(DeltaEncodedData.INTEGER, Level.DELTA_LEVEL_1)
			.addShrinkEncoder(ShrinkToEightBitData.INTEGER_TO_BYTE)
			.addZLibEncoder();
		builder.forCommentsChunkEncoder()
			.addZLibEncoder();
			
		writer = builder.build();
	}
	@Override
	public void write(Chromatogram chromatogram, OutputStream out)
			throws TraceEncoderException {
		writer.write(chromatogram, out);
		
	}

}
