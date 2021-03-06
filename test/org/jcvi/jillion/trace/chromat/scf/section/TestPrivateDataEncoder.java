/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.section;

import java.io.IOException;

import org.jcvi.jillion.internal.trace.chromat.scf.PrivateDataImpl;
import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.internal.trace.chromat.scf.section.EncodedSection;
import org.jcvi.jillion.internal.trace.chromat.scf.section.PrivateDataCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Section;
import org.jcvi.jillion.internal.trace.chromat.scf.section.SectionEncoder;
import org.jcvi.jillion.trace.chromat.scf.PrivateData;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestPrivateDataEncoder {

    private byte[] data = new byte[]{20,30,40, -20, -67,125};
    private PrivateDataImpl privateData = new PrivateDataImpl(data);
    SectionEncoder sut = new PrivateDataCodec();
    SCFHeader mockHeader;
    ScfChromatogram c;
    @Before
    public void setup(){
        mockHeader = createMock(SCFHeader.class);
        c = createMock(ScfChromatogram.class);
    }
    @Test
    public void valid() throws IOException{
        expect(c.getPrivateData()).andStubReturn(privateData);
        mockHeader.setPrivateDataSize(data.length);
        replay(mockHeader,c);
        EncodedSection actualEncodedSection= sut.encode(c, mockHeader);
        assertEquals(Section.PRIVATE_DATA, actualEncodedSection.getSection());
        assertArrayEquals(data, actualEncodedSection.getData().array());
        verify(mockHeader,c);
    }

    @Test
    public void nullPrivateDataShouldEncodeEmptySection() throws IOException{
        expect(c.getPrivateData()).andStubReturn(null);
        assertEncodedSectionIsEmpty();
    }
   
    @Test
    public void emptyPrivateDataShouldEncodeEmptySection() throws IOException{
        expect(c.getPrivateData()).andStubReturn((PrivateData) new PrivateDataImpl(new byte[0]));
        assertEncodedSectionIsEmpty();
    }
    private void assertEncodedSectionIsEmpty() throws IOException {
        mockHeader.setPrivateDataSize(0);
        replay(mockHeader,c);
        EncodedSection actualEncodedSection= sut.encode(c, mockHeader);
        assertEquals(Section.PRIVATE_DATA, actualEncodedSection.getSection());
        assertArrayEquals(new byte[0], actualEncodedSection.getData().array());
        verify(mockHeader,c);
    }


}
