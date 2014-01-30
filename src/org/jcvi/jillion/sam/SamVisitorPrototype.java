package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamProgram;

public class SamVisitorPrototype implements SamVisitor{

	private int numberOfRecords = 0;
	
	@Override
	public void visitHeader(SamHeader header) {
		System.out.println("header");
		System.out.println(" version = " + header.getVersion());
		System.out.println(" sort order = " + header.getSortOrder());
		System.out.println("references : ");
		for(ReferenceSequence ref : header.getReferenceSequences()){
			System.out.println("\t" + ref.getName() + " length = " + ref.getLength());
		}
		System.out.println("programs:");
		for(SamProgram prog : header.getPrograms()){
			System.out.println("\t" + prog.getId() + " cmdline " + prog.getCommandLine());
		}
		System.out.println("\n\n\n\n\n==================================\n");
	}

	public int getNumberOfRecords() {
		return numberOfRecords;
	}

	@Override
	public void visitRecord(SamRecord record) {
		System.out.println("record  " + record.getQueryName());
		if(record.isPrimary()){
			System.out.println("primary");
		}else{
			System.out.println("not primary");
		}
		System.out.println("cigar = " + record.getCigar());
		System.out.println("seq = " + record.getSequence());
		numberOfRecords ++;
	}

	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) throws IOException{
		File sam = new File("/local/netapp_scratch/CORE/allow_to_del/sam_creation/temp.sam");
	
		SamFileParser parser = new SamFileParser(sam);
		SamVisitorPrototype visitor = new SamVisitorPrototype();
		parser.accept(visitor);
		System.out.println("found " + visitor.getNumberOfRecords());
	}
}