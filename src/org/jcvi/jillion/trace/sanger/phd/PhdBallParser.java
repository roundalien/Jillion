package org.jcvi.jillion.trace.sanger.phd;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;
import org.jcvi.jillion.trace.sanger.phd.PhdBallVisitorCallback.PhdBallVisitorMemento;

public final class PhdBallParser {

	 private static final String BEGIN_COMMENT = "BEGIN_COMMENT";
    private static final String END_SEQUENCE = "END_SEQUENCE";
    private static final String END_COMMENT = "END_COMMENT";
    
    private static final String BEGIN_DNA = "BEGIN_DNA";
    private static final String END_DNA = "END_DNA";
    
    private static final String BEGIN_TAG = "BEGIN_TAG";
    private static final String END_TAG = "END_TAG";

    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("^\\s*(\\w+):\\s+(.*?)$");
    private static final Pattern CALLED_INFO_PATTERN = Pattern.compile("^\\s*(\\w)\\s+(\\d+)\\s*(\\d+)?\\s*?");
    private static final Pattern BEGIN_SEQUENCE_PATTERN = Pattern.compile("BEGIN_SEQUENCE\\s+(\\S+)\\s*(\\d+)?\\s*$");
    private static final String BEGIN_WR = "WR{";
    private static final String END_WR = "}";

    private static final Pattern FILE_COMMENT_PATTERN = Pattern.compile("^#(.*)\\s*$");
	
	private final File phdBall;
	
	public static PhdBallParser create(File phdBall) throws FileNotFoundException{
		return new PhdBallParser(phdBall);
	}
	
	
	private PhdBallParser(File phdBall) throws FileNotFoundException{
		if(phdBall ==null){
			throw new NullPointerException("phdball can not be null");
		}
		if(!phdBall.exists()){
			throw new FileNotFoundException("phdball must exist");
		}
		this.phdBall = phdBall;
	}
	
	public void accept(PhdBallVisitor2 visitor) throws IOException{
		if(visitor==null){
			throw new NullPointerException("visitor can not be null");
		}
		TextLineParser parser =null;
		try{
			parser = new TextLineParser(new BufferedInputStream(new FileInputStream(phdBall)));
			accept(parser, visitor);
		}finally{
			IOUtil.closeAndIgnoreErrors(parser);
		}
	}
	
	
	public void accept(PhdBallVisitor2 visitor, PhdBallVisitorMemento memento) throws IOException{
		if(visitor ==null){
            throw new NullPointerException("visitor can not be null");
        }
        if(memento ==null){
            throw new NullPointerException("memento can not be null");
        }
	    if(!(memento instanceof PhdBallVisitorMementoImpl)){
	    	throw new IllegalArgumentException("unknown memento type " + memento);
	    }
	    long offset = ((PhdBallVisitorMementoImpl)memento).getOffset();
	    //TODO add check to make sure its the same parser object?
	    RandomAccessFile randomAccessFile=null;
        TextLineParser parser=null;
        try{
	        randomAccessFile= new RandomAccessFile(phdBall,"r");
	        randomAccessFile.seek(offset);
	        InputStream in = new RandomAccessFileInputStream(randomAccessFile);
	        
	        parser = new TextLineParser(in, offset);
	        accept(parser, visitor);
        }finally{
        	IOUtil.closeAndIgnoreErrors(randomAccessFile, parser);
        }
	}
	
	private void accept(TextLineParser parser, PhdBallVisitor2 visitor) throws IOException{
		ParserState parserState = new ParserState();
		boolean seenFileComment=false;
		while(parser.hasNextLine() && parserState.keepParsing()){
			
			long currentOffset = parser.getPosition();
			String line = parser.nextLine();
			Matcher beginSequenceMatcher = BEGIN_SEQUENCE_PATTERN.matcher(line);
			if(beginSequenceMatcher.matches()){
				String readId = beginSequenceMatcher.group(1);
				String optionalVersion = beginSequenceMatcher.group(2);
				PhdBallVisitorCallback callback = createCallback(parserState,currentOffset);
				final PhdVisitor2 phdVisitor;
				if(optionalVersion ==null){
					phdVisitor = visitor.visitPhd(callback, readId);
				}else{
					phdVisitor =visitor.visitPhd(callback, readId, Integer.parseInt(optionalVersion));
				}
				if(phdVisitor ==null){
					skipSequence(parser);
				}else{
					handleSequence(parserState, parser, phdVisitor);
				}
			}else{				
				if(line.startsWith(BEGIN_WR)){
					handleWholeReadTag(parserState, parser, visitor.visitReadTag());
				}else if(!seenFileComment){
					Matcher fileCommentMatcher = FILE_COMMENT_PATTERN.matcher(line);
					if(fileCommentMatcher.matches()){
						seenFileComment=true;
						visitor.visitFileComment(fileCommentMatcher.group(1));
					}
				}
			}
		}
		if(parserState.keepParsing()){
			visitor.visitEnd();
		}else{
			visitor.halted();
		}
	}
	
	private void skipSequence(TextLineParser parser) throws IOException {
		boolean entireSequenceBlockRead=false;
		while(entireSequenceBlockRead && parser.hasNextLine()){
			String line = parser.nextLine();
			entireSequenceBlockRead = line.startsWith(END_SEQUENCE);
		}
		
	}


	private PhdBallVisitorCallback createCallback(ParserState parserState, long offset) {
		return new PhdBallVisitorCallbackImpl(offset, parserState);
	}


	private void handleWholeReadTag(ParserState parserState,
			TextLineParser parser, PhdWholeReadTagVisitor visitor) throws IOException {
		while(parser.hasNextLine() && parserState.keepParsing()){
			String line = parser.nextLine();
			if(line.startsWith(END_WR)){
				if(visitor!=null){
					visitor.visitEnd();
				}
				break;
			}
			if(visitor !=null){
				visitor.visitLine(line);
			}
		}
		if(visitor !=null && !parserState.keepParsing()){
			visitor.halted();
		}
	}


	private void handleSequence(ParserState parserState, TextLineParser parser,
			PhdVisitor2 visitor) throws IOException {
		//format of each sequence is:
		//BEGIN_COMMENT
		//<comments>
		//END_COMMENT
		//BEGIN_DNA
		//<lines of base qual pos>
		//pos is now optional as of Consed 20.0 ?
		//END_DNA
		//BEGIN_TAG
		//<tag data>
		//END_TAG
		//..multiple tags allowed
		//END_SEQUENCE
		
		parseCommentBlock(parser, visitor);
		if(!parserState.keepParsing()){
			visitor.halted();
			return;
		}
		parseReadData(parserState, parser, visitor);
		
		if(!parserState.keepParsing()){
			visitor.halted();
			return;
		}
		parseTags(parserState, parser, visitor);
		if(!parserState.keepParsing()){
			visitor.halted();
			return;
		}
		visitor.visitEnd();
		//TODO : 
		//individual phd files (not phd.ball) 
		//may have read tags AFTER the END_SEQUENCE
		//should we check for that?
	}

	private void parseTags(ParserState parserState, TextLineParser parser,
			PhdVisitor2 visitor) throws IOException {
		while(parser.hasNextLine() && parserState.keepParsing()){
			String line = parser.nextLine();
			if(line.startsWith(END_SEQUENCE)){
				//no tags
				return;
			}
			if(line.startsWith(BEGIN_TAG)){
				parseSingleTag(parserState, parser, visitor.visitReadTag());
			}
		}
		
		
		
	}


	private void parseSingleTag(ParserState parserState, TextLineParser parser,
			PhdReadTagVisitor2 visitor) throws IOException {
		boolean inTag=true;
		do{
			String line = parser.nextLine();
			if(line.startsWith(END_TAG)){
				inTag=false;
			}else{
				Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(line);
				if(keyValueMatcher.matches()){
					String key = keyValueMatcher.group(1);
					String value = keyValueMatcher.group(2);
					if("TYPE".equals(key)){
						visitor.visitType(value);
					}else if("SOURCE".equals(key)){
						visitor.visitSource(value);
					}else if("UNPADDED_READ_POS".equals(key)){
						//use tokenizer instead of Scanner
						//for performance improvement
						StringTokenizer tokenizer = new StringTokenizer(value);						
						visitor.visitUngappedRange(Range.of(
								Range.CoordinateSystem.RESIDUE_BASED,
								Integer.valueOf(tokenizer.nextToken()),
								Integer.valueOf(tokenizer.nextToken())));
					}else if("DATE".equals(key)){
						try {
							visitor.visitDate(PhdUtil.parseReadTagDate(value));
						} catch (ParseException e) {
							throw new IOException("error parsing read tag date: " + value, e);
						}
					}else{
						//unrecognized key-value pair
						//could be free-form misc data that happened to be in key:value format?
						visitor.visitFreeFormData(line);
					}
				}else{
					//not a key value pair
					if(line.startsWith(BEGIN_COMMENT)){
						visitor.visitComment( parseReadTagComment(parser));
					}else{
						//free form misc data?
						visitor.visitFreeFormData(line);
					}
				}
			}
		}while(inTag && parser.hasNextLine() && parserState.keepParsing());
		if(!parserState.keepParsing()){
			visitor.halted();
		}else{
			visitor.visitEnd();
		}
	}

	private String parseReadTagComment(TextLineParser parser) throws IOException{
		boolean inCommentBlock=true;
		StringBuilder comment = new StringBuilder();
		do{
			String line = parser.nextLine();
			if(line.startsWith(END_COMMENT)){
				inCommentBlock=false;
			}else{
				comment.append(line);
			}
		}while(inCommentBlock && parser.hasNextLine());
		return comment.toString();
	}

	private void parseReadData(ParserState parserState, TextLineParser parser, PhdVisitor2 visitor) throws IOException {
		boolean inDnaBlock =false;
		while(!inDnaBlock && parser.hasNextLine()){
			String line = parser.nextLine();
			inDnaBlock = line.startsWith(BEGIN_DNA);
		}
		
		do{
			String line = parser.nextLine();
			Matcher matcher = CALLED_INFO_PATTERN.matcher(line);
			if(matcher.matches()){
				Nucleotide base = Nucleotide.parse(matcher.group(1).charAt(0));
				PhredQuality qual = PhredQuality.valueOf(Integer.parseInt(matcher.group(2)));
				if(matcher.group(3)==null){
					//no trace position
					visitor.visitBasecall(base, qual);
				}else{
					visitor.visitBasecall(base, qual, Integer.parseInt(matcher.group(3)));
				}
			}else{
				inDnaBlock = line.startsWith(END_DNA);
			}
		}while(inDnaBlock && parser.hasNextLine() && parserState.keepParsing());
	}


	private void parseCommentBlock(TextLineParser parser, PhdVisitor2 visitor) throws IOException {
		boolean inCommentBlock =false;
		while(!inCommentBlock && parser.hasNextLine()){
			String line = parser.nextLine();
			inCommentBlock = line.startsWith(BEGIN_COMMENT);
		}
		Map<String, String> comments = parseComments(parser);
		
		visitor.visitComment(comments);
	}


	private Map<String, String> parseComments(TextLineParser parser) throws IOException {
		boolean inCommentBlock=true;
		Map<String, String> comments = new LinkedHashMap<String, String>();
		do{
			String line = parser.nextLine();
			if(line.startsWith(END_COMMENT)){
				inCommentBlock=false;
			}else{
				Matcher commentMatcher = KEY_VALUE_PATTERN.matcher(line);
	            if(commentMatcher.find()){
	            	comments.put(commentMatcher.group(1), commentMatcher.group(2));
	            }
			}
		}while(inCommentBlock && parser.hasNextLine());
		return comments;
	}

	private static class ParserState{
		private final AtomicBoolean keepParsing;
		
		public ParserState(){
			keepParsing = new AtomicBoolean(true);
		}
		
		public boolean keepParsing(){
			return keepParsing.get();
		}
		
		public void haltParsing(){
			keepParsing.set(false);
		}
	}
	
	private static class PhdBallVisitorCallbackImpl implements PhdBallVisitorCallback{

		private final long byteOffset;
		private final ParserState parserState;
		
		public PhdBallVisitorCallbackImpl(long byteOffset,
				ParserState parserState) {
			this.byteOffset = byteOffset;
			this.parserState = parserState;
		}

		@Override
		public boolean canCreateMemento() {
			return true;
		}

		@Override
		public PhdBallVisitorMemento createMemento() {
			return new PhdBallVisitorMementoImpl(byteOffset);
		}

		@Override
		public void haltParsing() {
			parserState.haltParsing();			
		}
		
	}
	
	private static class PhdBallVisitorMementoImpl implements PhdBallVisitorMemento{
		private final long offset;

		
		public PhdBallVisitorMementoImpl(long offset) {
			this.offset = offset;
		}
		
		public final long getOffset() {
			return offset;
		}

	}
}
