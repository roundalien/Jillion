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
 * Created on Oct 24, 2007
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Map;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.pos.Position;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.internal.trace.chromat.BasicChromatogram;
import org.jcvi.jillion.internal.trace.chromat.DefaultChannel;
import org.jcvi.jillion.internal.trace.chromat.DefaultChannelGroup;
import org.jcvi.jillion.internal.trace.chromat.scf.ScfChromatogramImpl;
import org.jcvi.jillion.internal.trace.chromat.ztr.ZTRChromatogramImpl;
import org.jcvi.jillion.trace.Trace;
import org.jcvi.jillion.trace.chromat.Channel;
import org.jcvi.jillion.trace.chromat.ChannelGroup;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.EncodedShortData;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;



/**
 * <code>ChromatogramXMLSerializer</code> serializes {@link Trace}
 * instances into <code>XML</code>.  This can be useful if {@link Trace} objects must
 * be passed to remote processes.
 * @author dkatzel
 *
 *
 */
public final class ChromatogramXMLSerializer {

    /**
     * Helper method for converting encoded Channel data into a {@link DefaultChannel} object.
     * <p>
     * Encoded data reduces the size of the XML generated by the serializer.
     * </p>
     * @param encodedBytes
     * @param encodedPositions
     * @return a newly allocated {@link DefaultChannel} object with the given confidence and position information.
     */
    public static Channel buildChannel(String encodedBytes, String encodedPositions){
        return new DefaultChannel(toByteBuffer(encodedBytes), toShortBuffer(encodedPositions));
    }

    
    /**
     * Helper method for converted a String of encoded bytes into a {@link ByteBuffer}.
     * <p>
     * Encoded data reduces the size of the XML generated by the serializer.
     * </p>
     * @param encodedBytes
     * @return a newly allocated {@link ByteBuffer} object with the given bytes.
     */
    public static QualitySequence toByteBuffer(String encodedBytes){
        return new QualitySequenceBuilder(new EncodedByteData(encodedBytes).getData())
        .build();
    }
    
    /**
     * Helper method for converted a String of encoded shorts into a {@link ShortBuffer}.
     * <p>
     * Encoded data reduces the size of the XML generated by the serializer.
     * </p>
     * @param encodedShorts
     * @return a newly allocated {@link ShortBuffer} object with the given bytes.
     */
    public static PositionSequence toShortBuffer(String encodedShorts){
        return new PositionSequenceBuilder(new EncodedShortData(encodedShorts).getData())
        				.build();
    }
   

   
    
    /**
     * <code>RangePersistenceDelegate</code> extends {@link PersistenceDelegate}
     * to convert {@link Range} objects into encoded data which can be
     * reconstructed using the buildPeaks method.
     * <p>
     *  Encoded data reduces the size of the XML generated by the serializer.
     *  </p>
     * @author dkatzel
     *
     */
     private static final class RangePersistenceDelegate extends
             PersistenceDelegate {
         @Override
         protected Expression instantiate(Object oldInstance, Encoder out) {
             Range clip = (Range)oldInstance;
                 return new Expression(clip,
                         Range.class,
                         "create",
                         new Object[]{
                         clip == null? 0:clip.getBegin(),
                         clip == null? 0:clip.getEnd()
             }      );
         }
     }
    /**
     * <code>ChannelPersistenceDelegate</code> extends {@link PersistenceDelegate}
     * to convert {@link DefaultChannel}objects into encoded data which can be
     * reconstructed using the buildChannel method.
     * <p>
     *  Encoded data reduces the size of the XML generated by the serializer.
     *  </p>
     * @author dkatzel
     *
     */
    private static final class ChannelPersistenceDelegate extends
            PersistenceDelegate {
        @Override
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Channel channel = (Channel)oldInstance;

            PositionSequence positions = channel.getPositionSequence();
            int i;
			short[] posArray = toArray(positions);
            
            QualitySequence qualities = channel.getQualitySequence();
            byte[] qualArray = new byte[(int)qualities.getLength()];
            i=0;
            for(PhredQuality qual : qualities){
            	qualArray[i]= qual.getQualityScore();
            	i++;
            }
            return new Expression(channel,
                    ChromatogramXMLSerializer.class,
                                  "buildChannel",
                                  new Object[]{
            				
               (channel.getQualitySequence()==null)? "":new EncodedByteData(qualArray).encodeData(),
              (channel.getPositionSequence()==null)? "":new EncodedShortData(posArray).encodeData(),
                      } );
        }
    }

    
     
     /**
      * <code>ChannelGroupPersistenceDelegate</code> extends {@link PersistenceDelegate}
      * to convert {@link ChannelGroup}objects into encoded data which can be
      * reconstructed using the buildPeaks method.
      * <p>
      *  Encoded data reduces the size of the XML generated by the serializer.
      *  </p>
      * @author dkatzel
      *
      */
      private static final class ChannelGroupPersistenceDelegate extends
              PersistenceDelegate {
          @Override
          protected Expression instantiate(Object oldInstance, Encoder out) {
              ChannelGroup channelGroup = (ChannelGroup)oldInstance;

              return new Expression(channelGroup,
                      DefaultChannelGroup.class,
                                    "new",
                                    new Object[]{
                  channelGroup.getAChannel(), 
                  channelGroup.getCChannel(),
                  channelGroup.getGChannel(),
                  channelGroup.getTChannel(),
                 } );
          }
      }
      public static QualitySequence buildConfidence(String encodedConfidence){
          return new QualitySequenceBuilder(new EncodedByteData(encodedConfidence).getData()).build();
      }
     
      /**
       * <code>BasicChromatogramPersistenceDelegate</code> extends {@link PersistenceDelegate}
       * to convert {@link BasicChromatogram}objects into encoded data which can be
       * reconstructed using the full BasicChromatogram constructor..
       * <p>
       *  Encoded data reduces the size of the XML generated by the serializer.
       *  </p>
       * @author dkatzel
       *
       */
       private static final class BasicChromatogramPersistenceDelegate extends
               PersistenceDelegate {
           @Override
           protected Expression instantiate(Object oldInstance, Encoder out) {
               BasicChromatogram chromatogram = (BasicChromatogram)oldInstance;

               return new Expression(chromatogram,
                       ChromatogramXMLSerializer.class,
                                     "buildBasicChromatogram",
                                     new Object[]{
                   chromatogram.getNucleotideSequence().toString(), 
                   new EncodedByteData(PhredQuality.toArray(chromatogram.getQualitySequence())).encodeData(),
                   new EncodedShortData(toArray(chromatogram.getPeakSequence())).encodeData(),
                   chromatogram.getChannelGroup(),
                   chromatogram.getComments(),
                  } );
           }
       }
       
       public static BasicChromatogram buildBasicChromatogram(String basecalls, String encodedQualities, 
               String encodedPeaks, ChannelGroup group, Map<String,String> props){
           return new
                   BasicChromatogram("id",
                           basecalls,
                           new EncodedByteData(encodedQualities).getData(),
                           new PositionSequenceBuilder(new EncodedShortData(encodedPeaks).getData()).build(),
                           group,
                           props
              );
       }
       public static ZTRChromatogramImpl buildZTRChromatogram(BasicChromatogram chromatogram, long clipStart, long clipEnd){
           return new
           ZTRChromatogramImpl(
               chromatogram,
               Range.of(clipStart, clipEnd)
              );
       }
       /**
        * <code>ZTRChromatogramPersistenceDelegate</code> extends {@link PersistenceDelegate}
        * to convert {@link ZTRChromatogramImpl}objects into encoded data which can be
        * reconstructed using the full BasicChromatogram constructor..
        * <p>
        *  Encoded data reduces the size of the XML generated by the serializer.
        *  </p>
        * @author dkatzel
        *
        */
        private static final class ZTRChromatogramPersistenceDelegate extends
                PersistenceDelegate {
            @Override
            protected Expression instantiate(Object oldInstance, Encoder out) {
                ZTRChromatogramImpl chromatogram = (ZTRChromatogramImpl)oldInstance;

                return new Expression(chromatogram,
                        ChromatogramXMLSerializer.class,
                                      "buildZTRChromatogram",
                                      new Object[]{
                    new BasicChromatogram(chromatogram), 
                    chromatogram.getClip().getBegin(), chromatogram.getClip().getEnd()
                   } );
            }
        }
        
        
        /**
         * <code>SCFChromatogramPersistenceDelegate</code> extends {@link PersistenceDelegate}
         * to convert {@link ScfChromatogram}objects into encoded data which can be
         * reconstructed using the full BasicChromatogram constructor..
         * <p>
         *  Encoded data reduces the size of the XML generated by the serializer.
         *  </p>
         * @author dkatzel
         *
         */
         private static final class SCFChromatogramPersistenceDelegate extends
                 PersistenceDelegate {
             @Override
             protected Expression instantiate(Object oldInstance, Encoder out) {
                 ScfChromatogram chromatogram = (ScfChromatogram)oldInstance;

                 return new Expression(chromatogram,
                         ScfChromatogramImpl.class,
                                       "new",
                                       new Object[]{
                     new BasicChromatogram(chromatogram), 
                     chromatogram.getSubstitutionConfidence(),
                     chromatogram.getInsertionConfidence(),
                     chromatogram.getDeletionConfidence(),
                     chromatogram.getPrivateData()
                    } );
             }
         }
    /**
     * Builds a {@link Trace} from <code>XML</code>
     * data which was generated by <code>ChromatogramXMLSerializer.toXML(...)</code>.
     *
     * @param inputStream the {@link InputStream} which contains the <code>XML</code> to read.
     * @return a {@link Trace} with the data populated by the <code>XML</code>
     *
     */
    public static Trace fromXML(InputStream inputStream){
        XMLDecoder chromatogramDecoder = new XMLDecoder(inputStream);
        return (Trace) chromatogramDecoder.readObject();
    }
    /**
     * Serializes a {@link Trace} into <code>XML</code> which is written to the provided {@link OutputStream}.
     * @param chromatogram the {@link Trace} to serialize.
     * @param outputStream the {@link OutputStream} where the <code>XML</code>
     * will be written.
     */
    public static void toXML(Chromatogram chromatogram,
            final OutputStream outputStream) {


        XMLEncoder e = new XMLEncoder(outputStream);
        e.setPersistenceDelegate(DefaultChannel.class,  new ChannelPersistenceDelegate());
        e.setPersistenceDelegate(Range.class, new RangePersistenceDelegate());
        e.setPersistenceDelegate(DefaultChannelGroup.class, new ChannelGroupPersistenceDelegate());
        e.setPersistenceDelegate(BasicChromatogram.class, new BasicChromatogramPersistenceDelegate());
        e.setPersistenceDelegate(ZTRChromatogramImpl.class, new ZTRChromatogramPersistenceDelegate());
        e.setPersistenceDelegate(ScfChromatogramImpl.class, new SCFChromatogramPersistenceDelegate());
        e.writeObject(chromatogram);
        e.close();
    }


	private static short[] toArray(PositionSequence positions) {
		short[] posArray = new short[(int)positions.getLength()];
		int i=0;
		for(Position pos : positions){
			posArray[i]= IOUtil.toSignedShort(pos.getValue());
			i++;
		}
		return posArray;
	}
}
