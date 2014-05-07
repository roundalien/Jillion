/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam;

import java.io.IOException;

import org.jcvi.jillion.sam.header.SamHeader;
/**
 * {@code SamParser}
 * is an interface that can parse
 * SAM or BAM files and call the appropriate
 * methods on the given {@link SamVisitor}.
 * @author dkatzel
 *
 */
public interface SamParser {
	/**
	 * 
	 * @return
	 */
	boolean canAccept();
	/**
	 * Parse the given {@link SamVisitor}
	 * and call the appropriate visit methods
	 * on the given visitor.
	 * @param visitor the {@link SamVisitor}
	 * to call the visit methods on;
	 * can not be null.
	 * @throws IOException
	 * @throws NullPointerException if visitor is null.
	 */
	void accept(SamVisitor visitor) throws IOException;
	/**
	 * Get the {@link SamHeader}
	 * for this SAM or BAM file.
	 * @return
	 */
	SamHeader getHeader() throws IOException;
}
