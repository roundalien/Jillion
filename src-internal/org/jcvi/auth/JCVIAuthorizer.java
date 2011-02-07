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
 * Created on Sep 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.auth;

import java.io.Closeable;
/**
 * {@code JCVIAuthorizer} is a interface
 * to handle username/password data.
 * @author dkatzel
 *
 *
 */
public interface JCVIAuthorizer extends Closeable{
    /**
     * Get the username for this authorization.
     * @return the username as a String.
     */
    String getUsername();
    /**
     * Get the password for this authorization as a char array.
     * @return an array of chars.
     */
    char[] getPassword();
    /**
     * Cleans up any resources created.  
     * It is recommended that the password
     * is cleared out for security.
     */
    void close();
    /**
     * Checks to see if this Authorizer is closed.
     * @return {@code true} if closed; {@code false} otherwise.
     */
    boolean isClosed();
}
