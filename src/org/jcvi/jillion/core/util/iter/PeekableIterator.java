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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.util.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface PeekableIterator<T> extends Iterator<T> {
	/**
	 * Peek at the next element to be iterated
	 * over without actually iterating over it.
	 * The object returned is guaranteed to be the 
	 * same as the object returned by the next 
	 * call to {@link #next()}.
	 * Calling {@link #peek()} several times
	 * without calling {@link #next()}
	 * will always return the same object.
	 * @return T
	 * @throws NoSuchElementException if there are no more elements
	 */
	T peek();
	/**
	 * Remove is not supported.
	 * Will always throw {@link UnsupportedOperationException}.
	 * @throws UnsupportedOperationException always.
	 */
	@Override
	void remove();
}