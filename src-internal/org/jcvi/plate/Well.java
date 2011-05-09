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

package org.jcvi.plate;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code Well} is a class that represents a single well
 * in a reaction plate.
 * @author dkatzel
 *
 *
 */
public final class Well implements Comparable<Well>{
    
    private static final Pattern wellPattern = Pattern.compile("([A-P])(\\d+)");
    /**
     * Create a new Well instance for the given well name
     * as a string.
     * @param wellName a well name for 96 or 384 well plates.
     * @return
     */
    public static Well create(String wellName){
        if(wellName ==null){
            throw new IllegalArgumentException("input can not be null");
        }
        Matcher m = wellPattern.matcher(wellName);

        if(m.find()){
            char row = m.group(1).charAt(0);
            byte col = Byte.parseByte(m.group(2));
            if(col >24){
                throw new IllegalArgumentException("invalid column " + col);
            }
            return new Well(row, col);
        }
        throw new IllegalArgumentException(
                "string does not contain a parseable Well : "+ wellName);
    }
    
    /**
     * Compute the Well for a given index for a 384 well plate.
     * If an index >= 384 is given, then this method will "rollover"
     * the index to make it under 384.
     *
     * @param i the index of the well to get
     * @param order the {@link IndexOrder} to use for the given index.
     * @return a {@link Well} representing the <code>i</code>th index 
     * in the given IndexOrder.
     * @throws NullPointerException if order is null.
     * @throws IllegalArgumentException if i <0.
     */
    public static Well compute384Well(int i,IndexOrder order) {
        return computeWell(PlateFormat._384,i, order);
       
    }
    /**
     * Compute the Well for a given index for a 96 well plate.
     * If an index >= 96 is given, then this method will "rollover"
     * the index to make it under 96.
     *
     * @param i the index of the well to get
     * @param order the {@link IndexOrder} to use for the given index.
     * @return a {@link Well} representing the <code>i</code>th index 
     * in the given IndexOrder.
     * @throws NullPointerException if order is null.
     * @throws IllegalArgumentException if i <0.
     */
    public static Well compute96Well(int i,IndexOrder order) {
        return computeWell(PlateFormat._96,i, order);
       
    }
    /**
     * Compute the Well for the given index for the given plate format.
     * This method will "rollover" the index to make it under the max number
     * of wells per plate.
     * @param format the {@link PlateFormat} to use.
     *@param i the index of the well to get
     * @param order the {@link IndexOrder} to use for the given index.
     * @return a {@link Well} representing the <code>i</code>th index 
     * in the given IndexOrder.
     * @throws NullPointerException if order or format is null.
     * @throws IllegalArgumentException if i <0.
     */
    public static Well computeWell(PlateFormat format, int i, IndexOrder order){
        if(format ==null){
            throw new NullPointerException("format can not be null");
        }
        return order.getWell(i, format);
    }
   
    /**
     * the row of this well.
     */
    private char row;
    /**
     * the column of this well.
     */
    private byte column;
    /**
     * Constructor.
     * @param row the row of this well
     * @param column the column of this well
     */
    private Well(char row, int column){
        if(row >'P' || row <'A'){
            throw new IllegalArgumentException("invalid row "+ row);
        }
        if(column <1 || column >24){
            throw new IllegalArgumentException("invalid column "+ column);
        }
        this.row = row;
        this.column = (byte)column;
    }

    /**
     * @return the row
     */
    public char getRow() {
        return row;
    }

    /**
     * @return the column
     */
    public byte getColumn() {
        return column;
    }
    
    public int get96WellIndex(){
        return get96WellIndex(IndexOrder.ROW_MAJOR);
    }
    public int get384WellIndex(){
        return get384WellIndex(IndexOrder.ROW_MAJOR);
    }

    public int get96WellIndex(IndexOrder order){
        return getWellIndex(PlateFormat._96,order);
    }
    public int get384WellIndex(IndexOrder order){
        return getWellIndex(PlateFormat._384,order);
    }
    
    public int getWellIndex(PlateFormat format, IndexOrder order){
        return order.getIndex(this,format);
    }
    /**
     * Returns the hash code value for this object.
     * @return the hash code for this object.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + row;
        result = prime * result + column;
        return result;
    }

    /**
     * Compares two {@link Well}s for equality.
     * The result is <code>true</code> if and only
     * if the argument is not <code>null</code>
     * and is a {@link Well} object that
     * has the similar row and column as this object.
     * @param obj the {@link Object} to compare with.
     * @return <code>true</code> if the objects are
     *  the same; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if(!(obj instanceof Well)){
            return false;
        }
        final Well other = (Well) obj;
        return this.row == other.row &&
            this.column == other.column;
    }
  
    /**
     * delegates to {@link #toZeroPaddedString()}.
     */
    @Override
    public String toString() {
        return toZeroPaddedString();
    }
    /**
     * Converts this Well into a String of the form:
     * <pre>
     *&lt;row&gt;&lt;column&gt;
     * </pre>
     * .
     * @return the {@link String} representation of this object.
     */
    public String toUnpaddedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(row);
        sb.append(String.format("%d", column));

        return sb.toString();
    }
    /**
     * Converts this Well into a String of the form:
     * <pre>
     *&lt;row&gt;&lt;0-padded column&gt;
     * </pre>
     * .
     * @return the {@link String} representation of this object.
     */
    public String toZeroPaddedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(row);
        sb.append(String.format("%02d", column));

        return sb.toString();
    }
    
    /**
     * Wells are compared based on their String values returned by
     * {@link #toZeroPaddedString()}.
     */
     @Override
     public int compareTo(Well o) {
         return this.toZeroPaddedString().compareTo(o.toZeroPaddedString());
     }
    
    /**
     * {@code IndexOrder} defines the well order of indexes
     * into a plate.
     * @author dkatzel
     *
     *
     */
    public static enum IndexOrder{
        /**
         * Each row is filled first, once the row is full,
         * then the next row starts to get populated.
         * <p>
         * Ex: A01, A02, A03...B01, B02, B03...
         */
        ROW_MAJOR{
            @Override
            int getIndex(Well well, PlateFormat type){
                return (well.getRow() -'A') * type.getNumberOfColumns() +well.getColumn() -1;
            }

            @Override
            Well getWell(int index, PlateFormat type) {
                if(index <0){
                    throw new IllegalArgumentException("index can not be <0");
                }
                int modIndex = index%type.getNumberOfWells();
                int column =  (modIndex % type.getNumberOfColumns())+1;
                char row = (char)( 'A'+(modIndex %type.getNumberOfWells())/ type.getNumberOfColumns());
                return new Well(row,column);
            }
            
        },
        /**
         * Each column is filled first, once the column is full,
         * then the next column starts to get populated.
         * <p>
         * Ex: A01, B01, C01...A02, B02, C02...
         */
        COLUMN_MAJOR{
            int getIndex(Well well, PlateFormat type){
                 return ((well.getColumn()-1) * type.getNumberOfRows()) + (well.getRow() -'A');
            }
            @Override
            Well getWell(int index, PlateFormat type) { 
                if(index <0){
                    throw new IllegalArgumentException("index can not be <0");
                }
                int modIndex = index%type.getNumberOfWells();
                char row = (char)( 'A'+(modIndex %type.getNumberOfRows()));
                int column =  (modIndex / type.getNumberOfRows()) +1;
                
                return new Well(row,column);
            }
        },
        /**
         * Each column is filled first, once the column is full,
         * then the next column starts to get populated.
         * <p>
         * Ex: A01, C01, E01...B01, D01,...A02, C02, E02...
         */
        HAMILTON_OPTIMIZED_COLUMN_MAJOR{
            int getIndex(Well well, PlateFormat type){
                 return ((well.getColumn()-1) * type.getNumberOfRows()) + (well.getRow() -'A');
            }
            @Override
            Well getWell(int index, PlateFormat type) { 
                if(index <0){
                    throw new IllegalArgumentException("index can not be <0");
                }
                int modIndex = index%type.getNumberOfWells();
                char row = (char)( 'A'+((modIndex %type.getNumberOfRows())/2 + modIndex%2));
                int column =  (modIndex / type.getNumberOfRows()) +1;
                
                return new Well(row,column);
            }
        },
        /**
         * Well order for an Applied Biosystems
         * 3130 machine using 16 capillaries at a time.
         * Only {@link WellType#_96} is supported.
         * <p>
         * Ex: A01, A02, B01, B02,...H02, A03, A04, B03, B04....
         */
        ABI_3130_16_CAPILLARIES{
            int getIndex(Well well, PlateFormat type){
                if(type != PlateFormat._96){
                    throw new IllegalArgumentException("only 96 well plates supported");
                }
                int rowIndex =well.getRow() -'A';
                int colIndex =well.getColumn()-1;
                int capilaryIndex =colIndex /2;
                return capilaryIndex*16 + rowIndex*2 + colIndex%2;
                
           }
           @Override
           Well getWell(int index, PlateFormat type) { 
               if(index <0){
                   throw new IllegalArgumentException("index can not be <0");
               }
               if(type != PlateFormat._96){
                   throw new IllegalArgumentException("only 96 well plates supported");
               }
               int modIndex = index%type.getNumberOfWells();
               int capilaryIndex = modIndex/16;
               int i = modIndex % 16;
               
               char row = (char)( 'A'+(i /2));
              
               int column =   (modIndex%2) +capilaryIndex*2+1;
               
               return new Well(row,column);
           }
        }
        
        ;
        
        abstract  int getIndex(Well well, PlateFormat type);
        
        abstract Well getWell(int index, PlateFormat type);
        /**
         * Create a new {@link Comparator} instance
         * that compares wells using this IndexOrder's
         * index for 96 well plates.
         * @return a new Comparator instance.
         */
        public Comparator<Well> create96WellComparator(){
            return createWellComparator(PlateFormat._96);
        }
        /**
         * Create a new {@link Comparator} instance
         * that compares wells using this IndexOrder's
         * index for 384 well plates.
         * @return a new Comparator instance.
         */
        public Comparator<Well> create384WellComparator(){
            return createWellComparator(PlateFormat._384);
        }
        
        public Comparator<Well> createWellComparator(PlateFormat format){
            return new IndexOrderComparator(format, this);
        }
        
    }
    
    private static final class IndexOrderComparator implements Comparator<Well>{
        private final PlateFormat type;
        private final IndexOrder order;
        

        private IndexOrderComparator(PlateFormat type, IndexOrder order) {
            this.type = type;
            this.order = order;
        }


        @Override
        public int compare(Well o1, Well o2) {
            final int o1Index, o2Index;
            switch(type){
                case _384 : 
                    o1Index = o1.get384WellIndex(order);
                    o2Index = o2.get384WellIndex(order);
                    break;
                case _96 : 
                    o1Index = o1.get96WellIndex(order);
                    o2Index = o2.get96WellIndex(order);
                    break;
                default: 
                    //impossible
                    throw new IllegalStateException("invalid type");
            }
            return Integer.valueOf(o1Index).compareTo(o2Index);
        }
        
        
    }

}
