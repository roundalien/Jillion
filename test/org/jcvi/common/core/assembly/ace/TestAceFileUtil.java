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

package org.jcvi.common.core.assembly.ace;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.jcvi.common.core.assembly.ace.AceFileUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestAceFileUtil {

    @Test
    public void parseChromatogramMadeAroundMidnight() throws ParseException{
        String dateAsString = "Fri Jan 7 00:40:59 2011";
        Date date =AceFileUtil.CHROMAT_DATE_TIME_FORMATTER.parse(dateAsString);
        Calendar calendar =Calendar.getInstance();
		calendar.setTime(date);
        assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(40, calendar.get(Calendar.MINUTE));
        assertEquals(59, calendar.get(Calendar.SECOND));
    }
}
