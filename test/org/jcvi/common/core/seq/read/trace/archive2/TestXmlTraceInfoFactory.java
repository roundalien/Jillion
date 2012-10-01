package org.jcvi.common.core.seq.read.trace.archive2;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestXmlTraceInfoFactory {

	private final TraceArchiveInfo sut;
	private final File rootDir;
	public TestXmlTraceInfoFactory() throws IOException{
		ResourceFileServer resources = new ResourceFileServer(TestXmlTraceInfoFactory.class);
		sut = XmlTraceArchiveInfoFactory.create(resources.getFile("files/exampleTraceArchive/TRACEINFO.xml"));
		rootDir = resources.getFile("files/exampleTraceArchive");
	}
	
	@Test
	public void numRecords(){
		assertEquals(4,sut.getRecordList().size());
	}
	
	@Test
	public void fastaRecordsExist(){
		for(TraceArchiveRecord record : sut.getRecordList()){
			assertTrue("missing seq fasta",
					new File(rootDir,record.getAttribute(TraceInfoField.BASE_FILE))
						.exists());
			assertTrue("missing qual fasta",
					new File(rootDir,record.getAttribute(TraceInfoField.QUAL_FILE))
						.exists());
			assertTrue("missing pos fasta",
					new File(rootDir,record.getAttribute(TraceInfoField.PEAK_FILE))
						.exists());
			assertTrue("missing pos fasta",
					new File(rootDir,record.getAttribute(TraceInfoField.PEAK_FILE))
						.exists());
		}
	}
}