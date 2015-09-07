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
package org.jcvi.jillion.sam.attribute;

import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamReadGroup;

/**
 * {@code ReservedSamAttributeKeys}
 * are the {@link SamAttributeKey}s that the 
 * SAM specification has reserved to mean
 * specific things.
 * @author dkatzel
 *
 */
public enum ReservedSamAttributeKeys{
	/**
	 * The smallest template-independent mapping
	 * quality of segments in the rest.
	 */
	SMALLEST_MAPPING_QUAL('A','M', SamAttributeType.SIGNED_INT),
	/**
	 * The Alignment score generated by the aligner.
	 */
	ALIGNMENT_SCORE('A','S', SamAttributeType.SIGNED_INT),
	/**
	 * Barcode sequence.
	 */
	BARCODE_SEQUENCE('B','C', SamAttributeType.STRING),
	/**
	 * Offset to base alignment quality (BAQ), of the same length as the read sequence. 
	 * At the i-th read base, BAQ<sub>i</sub> = Q<sub>i</sub> - (BQ<sub>i</sub> - 64) 
	 * where Q<sub>i</sub> is the i-th base quality.
	 */
	BASE_ALIGNMENT_QUAL_OFFSET('B','Q', SamAttributeType.STRING),
	/**
	 * Reference name of the next hit;
	 * '=' for the same chromosome.
	 */
	NEXT_HIT_REF_NAME('C','C', SamAttributeType.STRING),
	/**
	 * Edit distance between the color sequence and the color reference (see also {@link #EDIT_DISTANCE})
	 */
	COLOR_EDIT_DISTANCE('C','M', SamAttributeType.SIGNED_INT),
	/**
	 * Free Text comments.
	 */
	COMMENTS('C','O',SamAttributeType.STRING),
	/**
	 * Leftmost coordinate of the next hit
	 */
	LEFT_COORD_NEXT_HIT('C','P',SamAttributeType.SIGNED_INT),
	/**
	 * Color read quality on the original strand of the read.
	 *  Same encoding as QUAL; same length as {@link #COLOR_SEQUENCE}.
	 */
	COLOR_READ_QUAL('C','Q', SamAttributeType.STRING),
	/**
	 * Color read sequence on the original strand of the read. 
	 * The primer base must be included.
	 */
	COLOR_SEQUENCE('C','S', SamAttributeType.STRING),
	/**
	 * Complete read annotation tag, used for consensus annotation dummy features.
	 * <p>
	 * This key is intended primarily for annotation dummy reads, and consists of a strand, type and zero or more
key=value pairs, each separated with semicolons. The strand field has four values as in GFF3, and supplements
 {@link SamRecordFlags#REVERSE_COMPLEMENTED} to allow unstranded (`.'), and stranded but unknown strand (`?') annotation. For these and annotation on the
forward strand (strand set to `+'), do not set {@link SamRecordFlags#REVERSE_COMPLEMENTED}. For annotation on the reverse strand, set the strand to `-'
and set {@link SamRecordFlags#REVERSE_COMPLEMENTED}. The type and any keys and their optional values are all percent encoded according to RFC3986
to escape meta-characters `=', `%', `;', `|' or non-printable characters not matched by the isprint() macro (with the C
locale). For example a percent sign becomes `%2C'. The  record matches: \strand ;type (;key (=value ))*".
	 * </p>
	 */
	COMPLETE_READ_ANNOTATION('C','T', SamAttributeType.STRING),
	/**
	 * The 2nd most likely base calls. Same encoding and same length as QUAL.
	 */
	SECOND_MOSTLY_LIKELY_BASECALLS('E','2', SamAttributeType.STRING),
	
	/**
	 * The index of segment in the template.
	 */
	TEMPLATE_INDEX('F','I', SamAttributeType.SIGNED_INT),
	/**
	 * Segment suffix.
	 */
	SEGMENT_SUFFIX('F','S', SamAttributeType.STRING),
	/**
	 * Flow signal intensities on the original strand of the read, stored as (uint16 t)
round(value * 100.0).
	 */
	FLOW_SIG('F','Z', SamAttributeType.UNSIGNED_SHORT_ARRAY),
	/**
	 * Library. Value to be consistent with the header
	 * {@link org.jcvi.jillion.sam.header.ReadGroup#getLibrary()}
	 *  if this record belong to a Read Group.
	 */
	LIBRARY('L','B', SamAttributeType.STRING){

		@Override
		public void validate(SamHeader header, Object value) throws InvalidAttributeException {
			String libId = getType().getString(value);
			boolean found=false;
			for(SamReadGroup readGroup : header.getReadGroups()){
				//equals order allows for readGroup's getLibrary() to return null
				if(libId.equals(readGroup.getLibrary())){
					found = true;
					break;
				}
			}
			if(!found){
				throw new InvalidAttributeException("header must have read group with id "+ value);
			}
		}
		
	},
	/**
	 * Number of perfect hits
	 */
	NUM_PERFECT_HITS('H','0', SamAttributeType.SIGNED_INT),
	/**
	 * Number of 1-difference hits (see also {@link #EDIT_DISTANCE})
	 */
	ONE_DIFF_HITS('H','1', SamAttributeType.SIGNED_INT),
	/**
	 * Number of 2-difference hits (see also {@link #EDIT_DISTANCE})
	 */
	TWO_DIFF_HITS('H','2', SamAttributeType.SIGNED_INT),
	/**
	 * Query hit index, indicating the alignment record 
	 * is the i-th one stored in SAM.
	 */
	QUERY_HIT_INDEX('H','I', SamAttributeType.SIGNED_INT),
	/**
	 * Number of stored alignments in SAM that contains 
	 * the query in the current record.
	 */
	NUM_ALIGNMENTS('I','H', SamAttributeType.SIGNED_INT),
	/**
	 * String for mismatching positions. Regex : [0-9]+(([A-Z]|\^[A-Z]+)[0-9]+)*.
	 *<p>
	 *This field aims to achieve SNP/indel calling without looking at the reference.
	 * For example, a string `10A5^AC6' means from the leftmost
	 *  reference base in the alignment, there are 10 matches 
	 *  followed by an A on the reference which is 
	 *  different from the aligned read base; 
	 *  the next 5 reference bases are matches followed
	 *   by a 2bp deletion from the reference; 
	 *   the deleted sequence is AC; 
	 *   the last 6 bases are matches.
	 *   This field ought to match the CIGAR string.
	 *</p>
	 */
	MISMATCHING_POSITIONS('M','D', SamAttributeType.STRING),
	/**
	 * Mapping quality of the mate/next segment.
	 */
	MATE_OR_NEXT_MAPPING_QUALITY('M','Q', SamAttributeType.SIGNED_INT),
	/**
	 * Number of reported alignments that contains the query in the current record.
	 */
	NUM_REPORTED_ALIGNEMENTS('N','H', SamAttributeType.SIGNED_INT),
	/**
	 * Edit distance to the reference, including ambiguous bases but excluding clipping.
	 */
	EDIT_DISTANCE('N','M', SamAttributeType.SIGNED_INT),
	/**
	 * Original base quality (usually before recalibration). Same encoding as QUAL.
	 */
	ORIGINAL_QUALTY('O','Q', SamAttributeType.STRING),
	/**
	 * Original mapping position (usually before realignment).
	 */
	ORIGINAL_MAPPING_POSITION('O','P', SamAttributeType.SIGNED_INT),
	/**
	 * Original CIGAR (usually before realignment).
	 */
	ORIGINAL_CIGAR('O','C', SamAttributeType.STRING),
	/**
	 * SamProgram, the value must match 
	 * a {@link org.jcvi.jillion.sam.header.SamProgram#getId()}
	 * in the header.
	 */
	PROGRAM('P','G', SamAttributeType.STRING){

		@Override
		void validate(SamHeader header, Object value)
				throws InvalidAttributeException {
			if(!header.hasSamProgram(getType().getString(value))){
				throw new InvalidAttributeException("header does not contain program " + value);
			}
		}
		
	},
	/**
	 * Phred likelihood of the template, conditional on both the mapping being correct
	 */
	PHRED_LIKELIHOOD_OF_TEMPLATE('P','Q', SamAttributeType.SIGNED_INT),
	/**
	 * Read annotations for parts of the padded read sequence.
	 * <p>
	 * This key's value has the format of a series of tags separated by |, each
	 * annotating a sub-region of the read. Each tag consists of start, end,
	 * strand, type and zero or more key=value pairs, each separated with
	 * semicolons. Start and end are 1-based positions between one and the sum
	 * of the M/I/D/P/S/=/X CIGAR operators, i.e. SEQ length plus any pads. Note
	 * any editing of the CIGAR string may require updating the `PT' tag
	 * coordinates, or even invalidate them. As in GFF3, strand is one of `+'
	 * for forward strand tags, `-' for reverse strand, `.' for unstranded or
	 * `?' for stranded but unknown strand. The type and any keys and their
	 * optional values are all percent encoded as in the CT tag. Formally the
	 * entire PT record matches: \start ;end ;strand ;type (;key (=value
	 * ))*(\|start ;end ;strand ;type (;key (=value ))*)*".
	 * </p>
	 */
	PADDED_ANNOTATIONS('P','T', SamAttributeType.STRING),
	/**
	 * Platform unit of this record, the value must be consistent with 
	 * a {@link org.jcvi.jillion.sam.header.ReadGroup#getPlatformUnit()}
	 * in the header..
	 */
	PLATFORMT_UNIT('P','U', SamAttributeType.STRING){
		@Override
		void validate(SamHeader header, Object value)
				throws InvalidAttributeException {
			String actualPlatformUnit = getType().getString(value);
			//don't have access to which read group
			//this read is in
			//so try them all?
			//this could have false positives if the
			//read uses a platform unit from
			//a different read group.
			boolean found=false;
			for(SamReadGroup group : header.getReadGroups()){
				String platformUnit = group.getPlatformUnit();
				//equals order is to handle null platformUnits
				if(actualPlatformUnit.equals(platformUnit)){
					found=true;
					break;
				}
			}
			if(!found){
				throw new InvalidAttributeException("header does not contain platform unit " + value);
			}
		}
	},
	/**
	 * Phred quality of the barcode sequence in the {@link #BARCODE_SEQUENCE} (or {@link #READ_TAG}) tag. Same encoding as QUAL.
	 */
	BARCODE_QUALITY('Q','T', SamAttributeType.STRING),
	/**
	 * Phred quality of the mate/next segment sequence in the {@link #SEQUENCE_OF_NEXT_OR_MATE}.
	 *  Same encoding as QUAL.
	 */
	QUALITY_OF_NEXT_OR_MATE('Q','2', SamAttributeType.STRING),
	
	/**
	 * Sequence of the mate/next segment in the template.
	 */
	SEQUENCE_OF_NEXT_OR_MATE('R','2', SamAttributeType.STRING),
	/**
	 * Read group this record belongs to, the value must match a {@link org.jcvi.jillion.sam.header.ReadGroup}
	 * in the header.
	 */
	READ_GROUP('R','G', SamAttributeType.STRING){
		@Override
		void validate(SamHeader header, Object value)
				throws InvalidAttributeException {
			if(!header.hasReadGroup(getType().getString(value))){
				throw new InvalidAttributeException("header does not contain program " + value);
			}
		}
	},
	/**
	 * Deprecated alternative to {@link #BARCODE_SEQUENCE} tag originally used at Sanger.
	 */
	@Deprecated
	READ_TAG('R','T', SamAttributeType.STRING),
	
	/**
	 * Other canonical alignments in a chimeric alignment, in the format of:
	 * (rname,pos,strand,CIGAR,mapQ,NM;)+. Each element in the semi-colon delim-
	 * ited list represents a part of the chimeric alignment. Conventionally, at
	 * a supplementary line, the first element points to the primary line.
	 */
	OTHER_ALIGNMENT('S','A', SamAttributeType.STRING),
	/**
	 * Template-independent mapping quality.
	 */
	MAPPING_QUALITY('S','M', SamAttributeType.SIGNED_INT),
	/**
	 * The number of segments in the template.
	 */
	NUM_SEGS_IN_TEMPLATE('T','C', SamAttributeType.SIGNED_INT),
	/**
	 * Phred probility of the 2nd call being wrong conditional on the best being
	 * wrong. The same encoding as QUAL.
	 */
	QUAL_SECOND_CALL_WRONG_TOO('Q','2', SamAttributeType.STRING),
	/**
	 * Phred likelihood of the segment, conditional on the mapping being correct.
	 */
	QUAL_OF_THE_SEGMENT('U','Q', SamAttributeType.SIGNED_INT)
	;
	
	private static final ReservedSamAttributeKeys[][] CACHE;
	static{
		//cache is wasteful since we don't
		//need to store every 2 letter combination
		//but it makes for very fast look ups/
		//43 because 0-Z
		CACHE = new ReservedSamAttributeKeys[43][43];
		for(ReservedSamAttributeKeys reserved : values()){
			SamAttributeKey key = reserved.getKey();
			CACHE[getCacheIndex(key.getFirstChar())][getCacheIndex(key.getSecondChar())] = reserved;
		}
	}
	
	private static int getCacheIndex(char c){
		return c -'0';
	}
	private SamAttributeKey key;
	private SamAttributeType type;
	
	ReservedSamAttributeKeys(char c1, char c2, SamAttributeType type){
		this.key = SamAttributeKeyFactory.getKey(c1,c2);
		this.type = type;
	}

	public SamAttributeKey getKey() {
		return key;
	}
	
	void validate(SamHeader header, Object value) throws InvalidAttributeException{
		//no-op by default
	}
	
	public SamAttributeType getType() {
		return type;
	}
	public static ReservedSamAttributeKeys parseKey(SamAttributeKey key){
		return parseKey(key.getFirstChar(), key.getSecondChar());
	}
	public static ReservedSamAttributeKeys parseKey(char c1, char c2){
		return CACHE[getCacheIndex(c1)][getCacheIndex(c2)];
	}
	public static ReservedSamAttributeKeys parseKey(String key){
		if(key.length() !=2){
			throw new IllegalArgumentException("key string must be 2 chars long " + key);
		}
		return parseKey(key.charAt(0), key.charAt(1));
	}
	
}
