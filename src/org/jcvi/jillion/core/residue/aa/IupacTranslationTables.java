package org.jcvi.jillion.core.residue.aa;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.Triplet;
import org.jcvi.jillion.core.util.MapUtil;

public enum IupacTranslationTables implements TranslationTable{
	
	
	
	
	/**
	 * Default translation table used by GenBank.
	 */
	STANDARD(1),
	VERTEBRATE_MITOCHONDRIAL(2){

		@Override
		protected void updateTable(Map<Triplet, Codon2> map) {
			insertIntoTable('A', 'G', 'A', AminoAcid.STOP);
			insertIntoTable('A', 'G', 'G', AminoAcid.STOP);
			insertIntoTable('A', 'G', 'R', AminoAcid.STOP);
			
			insertIntoTable('A', 'T', 'A', AminoAcid.Methionine, true);
			insertIntoTable('A', 'T', 'R', AminoAcid.Methionine, true);
			
			insertIntoTable('T', 'G', 'A', AminoAcid.Tryptophan);
			insertIntoTable('T', 'G', 'R', AminoAcid.Tryptophan);
		}
		
	},
	YEAST_MITOCHONDRIAL(3){

		@Override
		protected void updateTable(Map<Triplet, Codon2> map) {			
			
			insertIntoTable('A', 'T', 'A', AminoAcid.Methionine, true);
			insertIntoTable('A', 'T', 'R', AminoAcid.Methionine, true);
			
			insertIntoTable('C', 'T', 'A', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'C', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'G', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'T', AminoAcid.Threonine);
			
			insertIntoTable('C', 'T', 'M', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'R', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'W', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'S', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'Y', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'K', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'V', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'H', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'D', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'B', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'N', AminoAcid.Threonine);

		}
		
	},

	MOLD_PROTOZOAN_COELENTERATE_MITOCHONDRIAL_AND_MYCOPLASMA_SPIROPLAMSA(4){

		@Override
		protected void updateTable(Map<Triplet, Codon2> map) {
			insertIntoTable('T', 'G', 'A', AminoAcid.Tryptophan);
			insertIntoTable('T', 'G', 'R', AminoAcid.Tryptophan);
			
		}
		
	},
	
	;

	private static final Map<Integer, IupacTranslationTables> TABLES;
	static{
		TABLES = new HashMap<Integer, IupacTranslationTables>(MapUtil.computeMinHashMapSizeWithoutRehashing(25));
		for(IupacTranslationTables table : values()){
			TABLES.put(Integer.valueOf(table.getTableNumber()), table);
		}
	}
	
	private final Map<Triplet, Codon2> map = new HashMap<Triplet, Codon2>(MapUtil.computeMinHashMapSizeWithoutRehashing(200));
	private final byte tableNumber;
	
	private IupacTranslationTables(int tableNumber){
		initialzeTable();
		this.tableNumber = (byte)tableNumber;
		
	}
	
	protected void removeFromTable(char base1, char base2, char base3){
		Triplet triplet = Triplet.create(base1, base2, base3);
		map.remove(triplet);
	}
	protected void  insertIntoTable(char base1, char base2, char base3, AminoAcid aa){
		insertIntoTable(base1, base2, base3, aa, false);
	}
	protected void  insertIntoTable(char base1, char base2, char base3, AminoAcid aa, boolean isStart){
		Triplet triplet = Triplet.create(base1, base2, base3);
		Codon2.Builder builder = new Codon2.Builder(triplet, aa);
		if(aa == AminoAcid.STOP){
			builder.isStop(true);
		}
		if(isStart){
			builder.isStart(true);
		}
		map.put(triplet, builder.build());
	}
	
	private void initialzeTable(){
		/*
		 * This group of Strings
		 * explains the amino acid,
		 *  if it's a start codon (M)
		 * and the triplet used to encode it.
		 * 
		 * This way of displaying the translation matrix
		 * is used by NCBI Genetic Codes page
		 * http://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi
		 * 
		 * I have made some changes, mostly adding ambiguity support
		 * for wobble bases.
		 * 
		 * Non-standard translation tables will update the
		 * Map later.
		 */
		
		char[] aas =    "FFFLLLSSSSSSSSSSSSSSSYYY***CCC*WLLLLLLLLPPPPPPPPPPPPPPPHHHQQQRRRRRRRRRRRRRRRIIIIIIIMTTTTTTTTTTTTTTTNNNKKKSSSRRRVVVVVVVVVVVVVVVAAAAAAAAAAAAAAADDDEEEGGGGGGGGGGGGGGG".toCharArray();
		char[] starts = "----M----------------------------------M-------------------------------------------M------------------------------------------------------------------------------".toCharArray();
		char[] base1 =  "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG".toCharArray();
		char[] base2 =  "TTTTTTCCCCCCCCCCCCCCCAAAAAAGGGGGTTTTTTTTCCCCCCCCCCCCCCCAAAAAAGGGGGGGGGGGGGGGTTTTTTTTCCCCCCCCCCCCCCCAAAAAAGGGGGGTTTTTTTTTTTTTTTCCCCCCCCCCCCCCCAAAAAAGGGGGGGGGGGGGGG".toCharArray();
		char[] base3 =  "TCYAGRTCAGMRWSYKVHDBNTCYAGRTCYAGTCAMWYHGTCAGMRWSYKVHDBNTCYAGRTCAGMRWSYKVHDBNTCAMWYHGTCAGMRWSYKVHDBNTCYAGRTCYAGRTCAGMRWSYKVHDBNTCAGMRWSYKVHDBNTCYAGRTCAGMRWSYKVHDBN".toCharArray();			
		
		for(int i=0; i<aas.length; i++){
			insertIntoTable(base1[i], base2[i], base3[i], 
								AminoAcid.parse(aas[i]),
								starts[i] =='M');
			
		}
	}
	

	@Override
	public AminoAcidSequence translate(NucleotideSequence sequence) {
		return translate(sequence, Frame.ZERO);
	}


	@Override
	public AminoAcidSequence translate(NucleotideSequence sequence, Frame frame) {
		if(sequence ==null){
			throw new NullPointerException("sequence can not be null");
		}
		if(frame ==null){
			throw new NullPointerException("frame can not be null");
		}
		//Brian says legacy PFGRC and (BioJava and BioPerl?)
		//don't correctly handle the 'not first starts'
		//so if translation table says codon is a start
		//and we've already seen a start, then make it not the start?
		Iterator<Nucleotide> iter = sequence.iterator();
		AminoAcidSequenceBuilder builder = new AminoAcidSequenceBuilder((int)(sequence.getLength()/3));
		handleFrame(iter, frame);
		boolean seenStart=false;
		while(iter.hasNext()){
			Triplet triplet =getNextTriplet(iter);
			if(triplet !=null){
				Codon2 codon =translate(triplet);
				if(codon.isStart() && !seenStart){
					seenStart=true;
					//hardcode an M if this is our first start
					//which may 
					//not be the amino acid returned by 
					//#getAminoAcid() depending on the translation table
					builder.append(AminoAcid.Methionine);
				}else{
					builder.append(codon.getAminoAcid());
				}
			}
		}
		return builder.build();
	}

	private Triplet getNextTriplet(Iterator<Nucleotide> iter){
		
		Nucleotide first= getNextNucleotide(iter);
		Nucleotide second= getNextNucleotide(iter);
		Nucleotide third= getNextNucleotide(iter);
		if(first==null || second ==null || third ==null){
			//no more bases
			return null;
		}
		return Triplet.create(first, second, third);
	}
	
	private Nucleotide getNextNucleotide(Iterator<Nucleotide> iter){
		if(!iter.hasNext()){
			return null;
		}
		Nucleotide n = iter.next();
		if(n.isGap()){
			throw new IllegalArgumentException("sequence can not contain gaps");
		}
		return n;
	}
	
	@SuppressWarnings("fallthrough")
	private void handleFrame(Iterator<Nucleotide> iter, Frame frame) {
		//switch uses fall through
		//so frame 2 skips first 2 bp		
		switch(frame){
			case TWO:
					if(iter.hasNext()){
						iter.next();
					}
			case ONE:
					if(iter.hasNext()){
						iter.next();
					}
					break;
			default:
					//no-op
				break;
		}
	}

	protected void updateTable(Map<Triplet, Codon2> map){
		//no-op
	}
	private Codon2 translate(Triplet triplet){
		return map.get(triplet);
	}

	public int getTableNumber(){
		return tableNumber;
	}
	
	public static TranslationTable getTableByTableNumber(int tableNumber){
		TranslationTable table = TABLES.get(tableNumber);
		if(table ==null){
			throw new IllegalArgumentException("unknown table number "+ tableNumber);
		}
		return table;
	}
}