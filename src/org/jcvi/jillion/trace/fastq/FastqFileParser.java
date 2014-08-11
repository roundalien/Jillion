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
package org.jcvi.jillion.trace.fastq;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback.FastqVisitorMemento;
/**
 * {@code FastqFileParser}  will parse a single 
 * fastq encoded file and call the appropriate
 * visitXXX methods on the given {@link FastqVisitor}.
 * @author dkatzel
 *
 */
public abstract class FastqFileParser implements FastqParser{

	private static final Pattern CASAVA_1_8_DEFLINE_PATTERN = Pattern.compile("^@(\\S+\\s+\\d:[N|Y]:\\d+:\\S+)\\s*$");
	
	private final NucleotideSequenceBuilder sequenceBuilder = new NucleotideSequenceBuilder(2000);
	private final StringBuilder qualityBuilder = new StringBuilder(2000);
	/**
	 * Create a new {@link FastqFileParser} instance
	 * that will parse the given fastq encoded
	 * file.
	 * @param fastqFile the file to parse.
	 * @throws IOException  if the file does not exist or can not be read.
	 * @throws NullPointerException if fastqFile is null.
	 */
	public static FastqParser create(File fastqFile) throws IOException{
		return new FileBasedFastqFileParser(fastqFile);
	}
	/**
	 * Create a new {@link FastqFileParser} instance
	 * that will parse the given a compressed fastq file. This factory method should be used in preference
	 * to {@link #create(InputStream)} if the file needs to be parsed
	 * multiple times.  
	 * @param fastqFile the file to parse.
	 * @param toInputStream {@link Function} to convert the given {@link File}
	 * into a <strong>new</strong> raw {@link InputStream}.  This allows the parser to handle compressed
	 * files.  A new InputStream should be created each time the function is called.  Can not be null.
	 * 
	 * @apiNote 
	 * For example if you wanted to parse a gzipped fastq file:
	 * <pre>
	 * {@code
	 * Function &lt;File, InputStream&gt; toGzipInputStream = f -&gt; {
	 * 	try {
	 * 		return new GZIPInputStream(new FileInputStream(f));
	 * 	} catch (IOException e) {
	 * 		throw new UncheckedIOException(e);
	 * 	}
	 * };
	 * 
	 * FastqFileParser parser = FastqFileParser.create(gzippedFfastqFile,toGzipInputStream);
	 * </pre>
	 * 
	 * @implNote The performance of random accessing records in this fastq file
	 * is dependent on {@link InputStream#skip(long)} implementation returned by the function.
	 * 
	 * @throws IOException  if the file does not exist or can not be read.
	 * @throws NullPointerException if any parameters are null or if the function returns null.
	 */
	public static FastqParser create(File fastqFile, Function<File, InputStream> toInputStream) throws IOException{
		return new FileBasedFastqFileParser(fastqFile, toInputStream);
	}
	/**
	 * Create a new {@link FastqFileParser} instance
	 * that will parse the given fastq encoded
	 * inputStream.  Please Note that inputStream implementations
	 * of the FastqFileParser can not create {@link FastqVisitorMemento}s
	 * or use {@link #accept(FastqVisitor, FastqVisitorMemento)}
	 * method.
	 * @param in the fastq encoded inputstream to parse.
	 * @throws NullPointerException if inputstream is null.
	 */
	public static FastqParser create(InputStream in){
		return new InputStreamFastqFileParser(in);
	}
	private FastqFileParser(){
		//can not instantiate outside of this class file.
	}
	
	protected void parseFastqFile(FastqVisitor visitor, TextLineParser parser) throws IOException{
		ParserState parserState = new ParserState(parser.getPosition());
		while(parserState.keepParsing() && parser.hasNextLine()){
			parserState=parseNextRecord(visitor, parser, parserState);
		}
		if(parserState.keepParsing()){
			visitor.visitEnd();
		}else{
			visitor.halted();
		}
	}
	
	private ParserState parseNextRecord(FastqVisitor visitor, TextLineParser parser, ParserState parserState) throws IOException{
		String deflineText = parser.nextLine();
		Defline defline = Defline.parse(deflineText);
		AbstractFastqVisitorCallback callback = createCallback(parserState);
        FastqRecordVisitor recordVisitor= visitor.visitDefline(callback, defline.getId(), defline.getComment());
        if(!parserState.keepParsing()){
        	return parserState;
        }
        return parseRecordBody(parser,recordVisitor,parserState);		
        
	}
	
	private ParserState parseRecordBody(TextLineParser parser,
			FastqRecordVisitor recordVisitor, ParserState parserState) throws IOException {
		//if we aren't visiting this read
		//we shouldn't spend any time parsing the
		//bases or qualities	
		if(recordVisitor ==null){
			skipCurrentRecord(parser);
			//set new end position for mementos to work
			return parserState.setOffset(parser.getPosition());
		}
		boolean inBasecallBlock;
		//default to 2000 bp since most sequences are only that much anyway
        //builder will grow if we get too big
        
        String line = parser.nextLine();
    	sequenceBuilder.append(line);
        do{
        	line = parser.nextLine();
        	inBasecallBlock = notQualityDefLine(line);
        	if(inBasecallBlock){
        		sequenceBuilder.append(line);
        	}
        }while(inBasecallBlock);
        
        NucleotideSequence sequence = sequenceBuilder
							        		.turnOffDataCompression(parserState.turnOffDataCompression())
							        		.build();
        	recordVisitor.visitNucleotides(sequence);
        
        if(!parserState.keepParsing()){
            recordVisitor.halted();
        	return parserState.setOffset(parser.getPosition());
        }
        //now parse the qualities
        int expectedQualities =  (int)sequence.getLength();
		sequenceBuilder.clear();
        
        qualityBuilder.setLength(0);
        //needs to be a do-while loop
        //to cover the case where the read is empty
        //(contains 0 bases) we still need to read a quality line
        do{    	
    		line = parser.nextLine();
    		qualityBuilder.append(line.trim());
    	}while(qualityBuilder.length() < expectedQualities);
    	if(qualityBuilder.length()> expectedQualities){
    		throw new IOException(
    				String.format("too many quality values for current record: expected %d but was %d", expectedQualities, qualityBuilder.length()));
    	}
    	recordVisitor.visitEncodedQualities(qualityBuilder.toString());
    	
		ParserState endParserState = parserState.setOffset(parser.getPosition());
		if (endParserState.keepParsing()){
			recordVisitor.visitEnd();
		}else{
			recordVisitor.halted();
		}

		return endParserState;
	}
	private void skipCurrentRecord(TextLineParser parser) throws IOException {
        
		String line = parser.nextLine();
		int numberOfBasesSeen=0;
     	
		while(notQualityDefLine(line)){
			//still in bases 
			numberOfBasesSeen += line.trim().length();
			line = parser.nextLine();
		}
		
		//handle special case of empty read
		if(numberOfBasesSeen==0){
			//skip blank line
			parser.nextLine();
			return;
		}
		int numberOfQualitiesLeft= numberOfBasesSeen;
		while(numberOfQualitiesLeft>0){
			line = parser.nextLine();
			numberOfQualitiesLeft -= line.trim().length();
		}
		//be consistent with errors if too many 
		//qualities
		if(numberOfQualitiesLeft< 0 ){
    		throw new IOException(
    				String.format("too many quality values for current record: expected %d but was %d", 
    						numberOfBasesSeen, 
    						numberOfBasesSeen - numberOfQualitiesLeft));
    	}
		
	}
	private boolean notQualityDefLine(String line) {
		return line.charAt(0) !='+';
	}

	protected abstract AbstractFastqVisitorCallback createCallback(ParserState parserState);
	
	
	
	
	private static final class Defline{
		private final String id,comment;

		private Defline(String id, String comment) {
			this.id = id;
			this.comment = comment;
		}
		
		public static Defline parse(String fastqDefline){
			Matcher casava18Matcher = CASAVA_1_8_DEFLINE_PATTERN.matcher(fastqDefline);
			if(casava18Matcher.matches()){
				return new Defline(casava18Matcher.group(1),null);
			}
			Matcher beginSeqMatcher =FastqUtil.SEQ_DEFLINE_PATTERN.matcher(fastqDefline);
	        if(!beginSeqMatcher.find()){
	            throw new IllegalStateException(String.format("invalid fastq file, could not parse seq id from '%s'",fastqDefline));
	        }
	        return new Defline(beginSeqMatcher.group(1), beginSeqMatcher.group(3));
		}
		public String getId() {
			return id;
		}

		public String getComment() {
			return comment;
		}

	}
	
	private abstract static class AbstractFastqVisitorCallback implements FastqVisitorCallback{
		private final ParserState parserState;
		
		
		public AbstractFastqVisitorCallback(ParserState parserState) {
			this.parserState = parserState;
		}

		@Override
		public void haltParsing() {
			parserState.stopParsing();
			
		}

		final ParserState getParserState() {
			return parserState;
		}

		@Override
		public void turnOffDataCompression(boolean turnOffDataCompression) {
			parserState.turnOffDataCompression(turnOffDataCompression);
			
		}
		
		
		
	}
	
	private static class NoMementoCallback extends AbstractFastqVisitorCallback{

		
		
		public NoMementoCallback(ParserState parserState) {
			super(parserState);
		}

		@Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public FastqVisitorMemento createMemento() {
			throw new UnsupportedOperationException("can not create memento");
		}
		
	}
	
	private static class MementoCallback extends AbstractFastqVisitorCallback{
		
		public MementoCallback(ParserState parserState){
			super(parserState);
		}

		@Override
		public boolean canCreateMemento() {
			return true;
		}

		@Override
		public FastqVisitorMemento createMemento() {
			return OffsetMemento.valueOf(getParserState().getCurrentOffset());
			//return new LongOffsetMemento(getParserState().getCurrentOffset());
		}
		
	}

	
	
	private static class ParserState{
		private final long currentOffset;
		private final AtomicBoolean keepParsing;
		//we probably don't care 
		//about concurrency here.
		//if the client ever turns off compression
		//it will probably be only once or twice over the lifetime
		//of the parser.
		private volatile boolean turnOffDataCompression = false;
		
		ParserState(long startOffset){
			this(startOffset, new AtomicBoolean(true));
		}
		
		public void turnOffDataCompression(boolean turnOffDataCompression) {
			this.turnOffDataCompression = turnOffDataCompression;
			
		}
		
		public boolean turnOffDataCompression(){
			return turnOffDataCompression;
		}

		public final long getCurrentOffset() {
			return currentOffset;
		}
		private ParserState(long startOffset, AtomicBoolean keepParsing){
			this.currentOffset = startOffset;
			this.keepParsing = keepParsing;
		}
		
		void stopParsing(){
			keepParsing.set(false);
		}
		
		public boolean keepParsing(){
			return keepParsing.get();
		}
		
		ParserState setOffset(long newOffset){
			return new ParserState(newOffset, keepParsing);
		}
	}
	
	private static class FileBasedFastqFileParser extends FastqFileParser{
		private final File fastqFile;
		private final Function<File, InputStream> toInputStream;
		
		public FileBasedFastqFileParser(File fastqFile) throws IOException {
			IOUtil.verifyIsReadable(fastqFile);
			
			this.fastqFile = fastqFile;
			toInputStream =null;
		}
		public FileBasedFastqFileParser(File fastqFile, Function<File, InputStream> toInputStream) throws IOException {
			Objects.requireNonNull(toInputStream);
			IOUtil.verifyIsReadable(fastqFile);
			
			this.fastqFile = fastqFile;
			this.toInputStream =toInputStream;
		}


		@Override
		public boolean canCreateMemento() {
			return true;
		}


		@Override
		public boolean isReadOnceOnly() {
			return false;
		}


		@Override
		public boolean canAccept() {
			return true;
		}


		@Override
		protected AbstractFastqVisitorCallback createCallback(
				ParserState parserState) {
			return new MementoCallback(parserState);
		}


		@Override
		public void parse(FastqVisitor visitor) throws IOException {
			if(visitor ==null){
				throw new NullPointerException("visitor can not be null");
			}
			
			try(InputStream in = toInputStream ==null ?
											new BufferedInputStream(new FileInputStream(fastqFile))
									:	toInputStream.apply(fastqFile);
				){
				TextLineParser parser = new TextLineParser(in);
				parseFastqFile(visitor, parser);			
			}
		}


		@Override
		public void parse(FastqVisitor visitor, FastqVisitorMemento memento)
				throws IOException {
			if(!(memento instanceof OffsetMemento)){
				throw new IllegalArgumentException("unknown memento type, instance must be generated by this parser");
			}
			if(visitor ==null){
				throw new NullPointerException("visitor can not be null");
			}
			long startOffset = ((OffsetMemento)memento).getValue();
			if(toInputStream ==null){
				try(InputStream in = new RandomAccessFileInputStream(fastqFile, startOffset)){
					TextLineParser parser = new TextLineParser(in, startOffset);
					parseFastqFile(visitor, parser);	
				}
			}else{
				try(InputStream in = toInputStream.apply(fastqFile)){
					//skip to offset
					in.skip(startOffset);
					TextLineParser parser = new TextLineParser(in, startOffset);
					parseFastqFile(visitor, parser);
				}
			}
		}		
	}
	
	private static class InputStreamFastqFileParser extends FastqFileParser{
		private final OpenAwareInputStream in;
		
		public InputStreamFastqFileParser(InputStream in) {
			if(in==null){
				throw new NullPointerException("inputstream can not be null");
			}
			this.in = new OpenAwareInputStream(in);
		}

		
		@Override
		public boolean canCreateMemento() {
			return false;
		}


		@Override
		public boolean isReadOnceOnly() {
			return true;
		}


		@Override
		public synchronized void parse(FastqVisitor visitor) throws IOException {
			if(!canAccept()){
				throw new IllegalStateException("can not accept, inputStream closed");
			}
			//synchronized to only let in one visitor at a time since they will
			//all share the inputstream...
			if(visitor ==null){
				throw new NullPointerException("visitor can not be null");
			}
			try{
				TextLineParser parser = new TextLineParser(in);
				parseFastqFile(visitor, parser);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
			
		}

		@Override
		public boolean canAccept() {
			return in.isOpen();
		}

		@Override
		public void parse(FastqVisitor visitor, FastqVisitorMemento memento)
				throws IOException {
			throw new UnsupportedOperationException("mementos not supported");
			
		}

		@Override
		protected AbstractFastqVisitorCallback createCallback(
				ParserState parserState) {
			return new NoMementoCallback(parserState);
		}
		
	}
	
	public abstract static class OffsetMemento implements FastqVisitorMemento {
		private static final long UNSIGNED_MAX_BYTE = 0xFF;
		private static final long UNSIGNED_MAX_SHORT = 0xFFFF;
		//need the "L" at the end to make the value a long otherwise it's an int with value -1 !
		private static final long UNSIGNED_MAX_INT = 0xFFFFFFFFL;
		/**
		 * Create a new instance of a {@link OffsetMemento}
		 * which will wrap the given value but use
		 * as few bytes as possible.
		 * @param value the value to wrap; may
		 * be negative.
		 * @return a n{@link OffsetMemento} instance that
		 * wraps the given value in as few bytes as possible.
		 */
		public static OffsetMemento valueOf(long value){
			//TODO: should we do caching to return 
			//already created instances (flyweight)?
			//This is probably going to be used mostly
			//for file offsets. If we wrap
			//several fastq files, each of which have
			//the same number of bases we might get a lot of
			//duplicate instances...
			
			if(value <0){
				throw new IllegalArgumentException("can not have negative offset");
			}
			if(value <=UNSIGNED_MAX_BYTE){
				return new ByteWidthOffsetMemento(value);
			}else if(value <=UNSIGNED_MAX_SHORT){
				return new ShortWidthOffsetMemento(value);
			}
			else if(value <=UNSIGNED_MAX_INT){
				return new IntWidthOffsetMemento(value);
			}
			return new LongWidthOffsetMemento(value);
		}
		/**
		 * Get the wrapped value as a long.
		 * @return the value; may be negative.
		 */
		public abstract long getValue();
		
		@Override
		public String toString() {
			return Long.toString(getValue());
		}
		@Override
		public boolean equals(Object obj){
			if(obj ==null){
				return false;
			}
			if(obj instanceof OffsetMemento){
				return getValue()==((OffsetMemento)obj).getValue();
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			long value = getValue();
			return prime * (int) (value ^ (value >>> 32));
		}
		
		
	}
	
	private static class ByteWidthOffsetMemento extends OffsetMemento{
		
		private final byte value;

		public ByteWidthOffsetMemento(long value) {
			this.value = IOUtil.toSignedByte((int)value);
		}

		@Override
		public long getValue() {
			return IOUtil.toUnsignedByte(value);
		}
		
	}
	
	private static class ShortWidthOffsetMemento extends OffsetMemento{
		
		private final short value;

		public ShortWidthOffsetMemento(long value) {
			this.value = IOUtil.toSignedShort((int)value);
		}

		@Override
		public long getValue() {
			return IOUtil.toUnsignedShort(value);
		}
		
	}
	
	private static class IntWidthOffsetMemento extends OffsetMemento{
		
		private final int value;

		public IntWidthOffsetMemento(long value) {
			this.value = IOUtil.toSignedInt(value);
		}

		@Override
		public long getValue() {
			return IOUtil.toUnsignedInt(value);
		}
		
	}
	private static class LongWidthOffsetMemento extends OffsetMemento{
		
		private final long value;

		public LongWidthOffsetMemento(long value) {
			this.value = value;
		}

		@Override
		public long getValue() {
			return value;
		}	
	}
}
