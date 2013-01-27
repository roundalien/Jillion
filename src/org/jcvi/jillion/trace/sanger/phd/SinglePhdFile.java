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
/*
 * Created on Nov 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger.phd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.sanger.PositionSequence;
import org.jcvi.jillion.trace.sanger.PositionSequenceBuilder;

public final class SinglePhdFile implements  Phd{
    private final NucleotideSequenceBuilder bases = new NucleotideSequenceBuilder();
    private final QualitySequenceBuilder qualities = new QualitySequenceBuilder();
    private final PositionSequenceBuilder positions = new PositionSequenceBuilder();
    
    private final List<PhdTag> tags = new ArrayList<PhdTag>();
    private Properties comments=null;
    private String id=null;
    private boolean inTag =false;
    private String currentTag;
    private StringBuilder currentTagValueBuilder;
    private final Phd delegatePhd;
    
    public static Phd create(File singlePhdFile) throws FileNotFoundException{
    	return new SinglePhdFile(singlePhdFile);
    }
    public static Phd create(InputStream singlePhdFileStream) throws FileNotFoundException{
    	return new SinglePhdFile(singlePhdFileStream);
    }
    private SinglePhdFile(File singlePhdFile) throws FileNotFoundException {
    	PhdParser.parsePhd(singlePhdFile, new SinglePhdFileVisitor());
    	
		this.delegatePhd = new DefaultPhd(id, 
				bases.build(),
				 qualities.build(),					
					positions.build(),
					comments,
					tags);
	}
    private SinglePhdFile(InputStream singlePhdStream) {
    	PhdParser.parsePhd(singlePhdStream, new SinglePhdFileVisitor());
    	
		this.delegatePhd = new DefaultPhd(id, 
				bases.build(),
				 qualities.build(),
				 positions.build(),
				 	comments,
					tags);
	}

  
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((delegatePhd == null) ? 0 : delegatePhd.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SinglePhdFile)) {
			return false;
		}
		SinglePhdFile other = (SinglePhdFile) obj;
		if (delegatePhd == null) {
			if (other.delegatePhd != null) {
				return false;
			}
		} else if (!delegatePhd.equals(other.delegatePhd)) {
			return false;
		}
		return true;
	}
	
	
	@Override
	public PositionSequence getPositionSequence() {
		return delegatePhd.getPositionSequence();
	}
	@Override
	public int getNumberOfTracePositions() {
		return delegatePhd.getNumberOfTracePositions();
	}
	@Override
	public NucleotideSequence getNucleotideSequence() {
		return delegatePhd.getNucleotideSequence();
	}
	@Override
	public QualitySequence getQualitySequence() {
		return delegatePhd.getQualitySequence();
	}
	@Override
	public String getId() {
		return delegatePhd.getId();
	}
	@Override
	public Properties getComments() {
		return delegatePhd.getComments();
	}
	@Override
	public List<PhdTag> getTags() {
		return delegatePhd.getTags();
	}
	
	
	/**
	 * {@code SinglePhdFileVisitor} is a private
	 * inner class that implements the {@link PhdFileVisitor}
	 * interface and sets the appropriate values 
	 * in singlePhdFile.
	 * @author dkatzel
	 *
	 *
	 */
	private class SinglePhdFileVisitor implements PhdFileVisitor{
	    private boolean firstRecord=true;
	    private boolean validRecord=false;
	    @Override
	    public synchronized void visitBeginTag(String tagName) {
	        currentTag =tagName;
	        currentTagValueBuilder = new StringBuilder();
	        inTag =true;
	        validRecord=true;
	    }

	    @Override
	    public synchronized void visitEndTag() {
	        if(!inTag){
	            throw new IllegalStateException("invalid tag");
	        }
	        tags.add(new DefaultPhdTag(currentTag, currentTagValueBuilder.toString()));
	        inTag = false;
	    }

	    
	    @Override
	    public synchronized void visitBasecall(Nucleotide base, PhredQuality quality,
	            int tracePosition) {
	       bases.append(base);
	       qualities.append(quality);
	       positions.append(tracePosition);            
	    }
	    @Override
	    public synchronized void visitBeginDna() {
	        firstRecord=false;
	    }



	    @Override
	    public synchronized void visitBeginSequence(String id) {
	        if(!firstRecord){
	            throw new IllegalStateException("found more than one record in phd file");
	        }
	        SinglePhdFile.this.id = id;
	    }



	    @Override
	    public synchronized void visitComment(Properties comments) {
	        SinglePhdFile.this.comments = comments;
	    }



	    @Override
	    public synchronized void visitEndDna() {
	    	//no-op
	    }



	    @Override
	    public synchronized void visitEndSequence() {
	    	//no-op
	    }



	    /**
        * {@inheritDoc}
        */
        @Override
        public boolean visitBeginPhd(String id) {
            return true;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean visitEndPhd() {
            return false;
        }

        @Override
	    public synchronized void visitLine(String line) {
        	//no-op
	    }



	    @Override
	    public synchronized void visitEndOfFile() {
	        if(!validRecord){
	            throw new IllegalStateException("not a valid phdfile not phd records found");
	        }
	    }



	    @Override
	    public synchronized void visitFile() {
	    	//no-op
	    }
	}

}