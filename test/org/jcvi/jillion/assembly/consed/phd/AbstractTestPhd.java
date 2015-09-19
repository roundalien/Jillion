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
 * Created on Nov 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.phd;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;

public abstract class AbstractTestPhd {
    protected static final String PHD_FILE = "files/1095595674585.phd.1";
    protected final String expectedId= "1095595674585";
    protected final String expectedBasecalls = "GCTTGCGGTTTGGGAGACTTCAGCATAAACCGGTCACGGCGGGGTCTCCCCTGAGCTGAGGGACAGCAGTTCGTTCACGGTGCATGCCTCACTCTGGAGCCAGGCATCCAAAGGCTTCGTAGCGAGGTGATCCAGAAGCCCGCTGAAAGCCCGGAGATGCTCGTCCCGGCGCCCCTCTTCCACGGCGGTGCGGGAACCGAAGAGCGCAAAGGGCGGCAGGGGACGCATATGGCACAGGGTTGCAGTCTGCTCCAGGGGGATCAGCAACTCCCGGAGGCTGTGATGGTTATACCCCGAGGCGTCATAGGCGGCGCCGGGGCCGCCGGCGCTGCAGGCACAGATAAAATATTTGCCGGCCAGGGCCGTACCCTCCCGGCCGTAGGCGAAGCCATACTCCAGCACCAGGTCCTGCCATTCCTTGAGCAGTGCCGGGGTGGAGTACCAATACAGGGGGAACAAAAACACGATGACATCATGCTCGCAGAGTCGCTGCTGCTCCCTGTCGATATCCACACGATAGTTCGGGTACTCCGCGTAGAGATCCACGAAGCTTGCGTAGGGGTGTTCCCGGGCCAGGGACGCGAGATGCCGGTTAATCTCAGAACGGGCGGGTGAGGGATGGGCAAACAGCACCAGTATGCGTCGGGCACTCATGGCATCAGGCGTCTTCGGGAAGGGGGATGAACTCCTCGTCGCCGGGCACCCCCGGGAACTTGCCACTTCGCCAGTCGTCCTTGGCCTGATCGATGCGCGCGGTGCTGTCGCTGACAAAATTCCAGTACATGTGGCGCTCGCCCAGGGGGTCCCCGCCAATCATGAGCACCCGCGCTGTTTCGGAAGCGCTGAGCATCACGCTGTCATCACCGTCGAGCACTACCATCACTCCCGCGGCTACAGCCTCCCCATCCACCTGCACGGCGCCCTCGGCTACATAAAGGGCATGCTCGTTCTCGTTCAGCGTGATGGAGGTCTTCCCACCGGCCGTCACCTTGAGATCTGCATACACGGTGCGAGAGTAGGTTTTT";
    protected final PositionSequence expectedPositions = new PositionSequenceBuilder(
             new short[]{5 ,19 ,33 ,50 ,71 ,78 ,88 ,97 ,110 ,117 ,123 ,131 ,151 ,162 ,176 ,177 ,195 ,198 ,208 ,222 ,234 ,250 ,262 ,279 ,292 ,308 ,319 ,331 ,341 ,351 ,363 ,377 ,394 ,407 ,420 ,428 ,437 ,450 ,464 ,479 ,490 ,506 ,518 ,531 ,543 ,555 ,566 ,578 ,589 ,600 ,611 ,624 ,637 ,652 ,663 ,675 ,687 ,700 ,712 ,723 ,
                     737 ,749 ,762 ,770 ,779 ,791 ,804 ,815 ,827 ,839 ,852 ,864 ,878 ,889 ,902 ,914 ,923 ,933 ,946 ,959 ,972 ,984 ,996 ,1007 ,1017 ,1030 ,1042 ,1054 ,1066 ,1079 ,1088 ,1099 ,1112 ,1124 ,1137 ,1149 ,1163 ,1176 ,1186 ,1198 ,1209 ,1219 ,1231 ,1244 ,1255 ,1267 ,1277 ,1289 ,1301 ,1311 ,1322 ,1333 ,1345 ,1358 ,1370 ,1382 ,1394 ,1406 ,1418 ,1431 ,
                     1442 ,1453 ,1465 ,1479 ,1490 ,1501 ,1514 ,1526 ,1538 ,1550 ,1560 ,1571 ,1584 ,1594 ,1607 ,1619 ,1631 ,1643 ,1654 ,1666 ,1677 ,1690 ,1702 ,1715 ,1727 ,1740 ,1751 ,1762 ,1773 ,1784 ,1796 ,1808 ,1820 ,1833 ,1847 ,1857 ,1870 ,1880 ,1892 ,1903 ,1916 ,1928 ,1940 ,1953 ,1964 ,1976 ,1987 ,2000 ,2013 ,2025 ,2036 ,2049 ,2060 ,2072 ,2083 ,2096 ,2108 ,2121 ,2133 ,2144 ,
                     2155 ,2165 ,2176 ,2188 ,2201 ,2214 ,2225 ,2238 ,2251 ,2262 ,2274 ,2286 ,2299 ,2312 ,2325 ,2336 ,2346 ,2358 ,2370 ,2383 ,2395 ,2406 ,2419 ,2430 ,2442 ,2455 ,2466 ,2477 ,2488 ,2500 ,2511 ,2524 ,2537 ,2548 ,2561 ,2574 ,2585 ,2597 ,2608 ,2620 ,2632 ,2645 ,2659 ,2668 ,2681 ,2692 ,2704 ,2715 ,2727 ,2738 ,2750 ,2764 ,2776 ,2787 ,2798 ,2809 ,2820 ,2833 ,2845 ,2858 ,
                     2870 ,2882 ,2894 ,2905 ,2917 ,2929 ,2941 ,2953 ,2965 ,2977 ,2989 ,3001 ,3013 ,3023 ,3034 ,3047 ,3058 ,3071 ,3084 ,3097 ,3106 ,3118 ,3129 ,3141 ,3153 ,3165 ,3176 ,3188 ,3200 ,3212 ,3224 ,3236 ,3248 ,3261 ,3274 ,3284 ,3296 ,3308 ,3320 ,3332 ,3344 ,3355 ,3368 ,3379 ,3391 ,3404 ,3415 ,3427 ,3438 ,3449 ,3460 ,3471 ,3484 ,3496 ,3508 ,3520 ,3533 ,3544 ,3557 ,3569 ,
                     3581 ,3593 ,3605 ,3616 ,3627 ,3639 ,3651 ,3664 ,3676 ,3688 ,3701 ,3712 ,3724 ,3737 ,3749 ,3760 ,3773 ,3786 ,3798 ,3810 ,3822 ,3834 ,3846 ,3858 ,3870 ,3882 ,3895 ,3906 ,3919 ,3931 ,3942 ,3954 ,3966 ,3977 ,3990 ,4001 ,4013 ,4024 ,4035 ,4047 ,4060 ,4071 ,4083 ,4094 ,4106 ,4118 ,4129 ,4140 ,4151 ,4164 ,4176 ,4189 ,4201 ,4213 ,4225 ,4238 ,4249 ,4262 ,4273 ,4284 ,
                     4296 ,4308 ,4321 ,4333 ,4345 ,4357 ,4369 ,4380 ,4392 ,4404 ,4417 ,4428 ,4440 ,4452 ,4464 ,4477 ,4488 ,4501 ,4512 ,4525 ,4536 ,4547 ,4560 ,4572 ,4584 ,4596 ,4608 ,4619 ,4631 ,4643 ,4654 ,4666 ,4677 ,4689 ,4701 ,4714 ,4726 ,4737 ,4749 ,4761 ,4773 ,4784 ,4796 ,4807 ,4819 ,4832 ,4844 ,4856 ,4868 ,4880 ,4892 ,4905 ,4917 ,4928 ,4939 ,4952 ,4964 ,4976 ,4988 ,5000 ,
                     5012 ,5025 ,5036 ,5048 ,5059 ,5071 ,5083 ,5095 ,5107 ,5119 ,5131 ,5144 ,5156 ,5168 ,5181 ,5192 ,5205 ,5218 ,5229 ,5241 ,5252 ,5263 ,5275 ,5286 ,5298 ,5310 ,5322 ,5334 ,5345 ,5358 ,5371 ,5382 ,5395 ,5408 ,5421 ,5433 ,5444 ,5455 ,5467 ,5479 ,5491 ,5503 ,5514 ,5525 ,5537 ,5550 ,5563 ,5574 ,5586 ,5599 ,5610 ,5622 ,5633 ,5646 ,5657 ,5669 ,5681 ,5694 ,5707 ,5719 ,
                     5731 ,5743 ,5755 ,5766 ,5779 ,5790 ,5803 ,5814 ,5827 ,5838 ,5851 ,5863 ,5875 ,5888 ,5900 ,5912 ,5924 ,5936 ,5948 ,5960 ,5972 ,5983 ,5997 ,6008 ,6020 ,6033 ,6044 ,6055 ,6066 ,6078 ,6091 ,6101 ,6113 ,6125 ,6136 ,6149 ,6162 ,6174 ,6186 ,6198 ,6210 ,6222 ,6234 ,6247 ,6260 ,6272 ,6284 ,6295 ,6306 ,6319 ,6331 ,6343 ,6355 ,6368 ,6380 ,6393 ,6404 ,6415 ,6429 ,6440 ,
                     6453 ,6463 ,6475 ,6488 ,6499 ,6510 ,6522 ,6536 ,6548 ,6559 ,6572 ,6584 ,6596 ,6609 ,6620 ,6633 ,6645 ,6657 ,6668 ,6681 ,6694 ,6706 ,6718 ,6730 ,6742 ,6754 ,6766 ,6777 ,6790 ,6802 ,6815 ,6828 ,6839 ,6852 ,6863 ,6874 ,6887 ,6899 ,6913 ,6924 ,6936 ,6948 ,6960 ,6974 ,6985 ,6998 ,7009 ,7021 ,7033 ,7045 ,7058 ,7070 ,7082 ,7095 ,7106 ,7118 ,7129 ,7142 ,7155 ,7166 ,
                     7178 ,7189 ,7202 ,7215 ,7226 ,7238 ,7251 ,7263 ,7276 ,7288 ,7301 ,7313 ,7325 ,7337 ,7350 ,7360 ,7374 ,7386 ,7399 ,7409 ,7422 ,7434 ,7447 ,7459 ,7471 ,7482 ,7495 ,7507 ,7518 ,7530 ,7543 ,7554 ,7566 ,7578 ,7590 ,7602 ,7615 ,7627 ,7638 ,7651 ,7663 ,7676 ,7689 ,7701 ,7713 ,7726 ,7738 ,7750 ,7762 ,7773 ,7786 ,7798 ,7810 ,7821 ,7833 ,7846 ,7859 ,7870 ,7882 ,7894 ,
                     7906 ,7918 ,7931 ,7944 ,7956 ,7969 ,7980 ,7992 ,8005 ,8017 ,8028 ,8042 ,8054 ,8067 ,8079 ,8089 ,8102 ,8115 ,8127 ,8139 ,8152 ,8162 ,8175 ,8187 ,8199 ,8211 ,8224 ,8236 ,8249 ,8261 ,8273 ,8286 ,8298 ,8310 ,8322 ,8334 ,8347 ,8357 ,8371 ,8385 ,8396 ,8408 ,8418 ,8430 ,8442 ,8455 ,8468 ,8479 ,8493 ,8506 ,8519 ,8530 ,8541 ,8553 ,8566 ,8578 ,8591 ,8603 ,8614 ,8625 ,
                     8638 ,8651 ,8663 ,8675 ,8687 ,8700 ,8712 ,8723 ,8737 ,8749 ,8761 ,8774 ,8785 ,8798 ,8809 ,8822 ,8834 ,8847 ,8859 ,8871 ,8884 ,8896 ,8909 ,8920 ,8932 ,8945 ,8958 ,8969 ,8981 ,8993 ,9006 ,9019 ,9031 ,9043 ,9056 ,9069 ,9081 ,9093 ,9105 ,9117 ,9130 ,9143 ,9154 ,9167 ,9179 ,9192 ,9204 ,9221 ,9228 ,9240 ,9252 ,9264 ,9276 ,9286 ,9300 ,9312 ,9325 ,9335 ,9348 ,9361 ,
                     9373 ,9385 ,9397 ,9408 ,9421 ,9434 ,9446 ,9460 ,9472 ,9485 ,9497 ,9510 ,9522 ,9535 ,9546 ,9558 ,9571 ,9583 ,9594 ,9608 ,9620 ,9631 ,9645 ,9662 ,9670 ,9682 ,9693 ,9706 ,9719 ,9731 ,9744 ,9755 ,9772 ,9780 ,9793 ,9805 ,9812 ,9830 ,9844 ,9855 ,9867 ,9880 ,9890 ,9902 ,9915 ,9929 ,9941 ,9954 ,9967 ,9980 ,9992 ,10007 ,10021 ,10032 ,10042 ,10053 ,10067 ,10077 ,10093 ,10105 ,
                     10116 ,10127 ,10141 ,10154 ,10167 ,10183 ,10190 ,10203 ,10219 ,10227 ,10240 ,10248 ,10263 ,10276 ,10289 ,10303 ,10315 ,10329 ,10340 ,10349 ,10363 ,10378 ,10386 ,10399 ,10413 ,10421 ,10437 ,10452 ,10460 ,10481 ,10488 ,10503 ,10511 ,10526 ,10539 ,10555 ,10562 ,10575 ,10585 ,10602 ,10613 ,10625 ,10637 ,10652 ,10663 ,10675 ,10684 ,10700 ,10713 ,10729 ,10739 ,10753 ,10763 ,10778 ,10789 ,10802 ,10814 ,10825 ,10839 ,10856 ,
                     10864 ,10875 ,10892 ,10903 ,10914 ,10930 ,10938 ,10951 ,10959 ,10974 ,10991 ,11001 ,11014 ,11028 ,11041 ,11051 ,11073 ,11081 ,11092 ,11101 ,11113 ,11124 ,11145 ,11154 ,11168 ,11183 ,11193 ,11207 ,11223 ,11238 ,11244 ,11255 ,11272 ,11285 ,11293 ,11309 ,11319 ,11330 ,11348 ,11359 ,11369 ,11381 ,11402 ,11412 ,11427 ,11435 ,11447 ,11457 ,11479 ,11489 ,11503 ,11514 ,11525 ,11543 ,11553 ,11566 ,11575 ,11591 ,11604 ,11618 ,
                     11628 ,11642 ,11654 ,11665 ,11678 ,11699 ,11709 ,11728 ,11739 ,11750 ,11761 ,11772 ,11793 ,11802 ,11815 ,11828 ,11836 ,11850 ,11862 ,11880 ,11892 ,11912 ,11921 ,11931 ,11944 ,11959 ,11968 ,11985 ,11994 ,12011 ,12027 ,12034 ,12056 ,12070 ,12074 ,12098 ,12108 ,12113 ,12135 ,12142 ,12155 ,12168 ,12186 ,12199 ,12211 ,12226 ,12240 ,12253 ,12269 ,12273 ,12295 ,12309 ,12325 ,12336 ,12347 ,12367 ,12374 ,12389 ,12409 ,12415 ,
                     12423 ,12435 ,12452 ,12460 ,12475}).build();
     
    protected QualitySequence expectedQualities = new QualitySequenceBuilder(
             new byte[]{
                     9 , 7 , 8 , 6 , 9 , 9 , 9 , 11 , 9 , 9 , 9 , 10 , 6 , 6 , 9 , 6 , 6 , 6 , 8 , 10 , 9 , 7 , 19 , 20 , 19 , 19 , 21 , 27 , 34 , 23 , 23 , 26 , 17 , 17 , 16 , 28 , 22 , 24 , 22 , 34 , 31 , 31 , 32 , 31 , 36 , 36 , 38 , 38 , 44 , 36 , 24 , 25 , 25 , 30 , 35 , 24 , 36 , 29 , 29 , 35 , 
                     19 , 18 , 24 , 16 , 19 , 16 , 19 , 41 , 36 , 29 , 29 , 27 , 34 , 21 , 31 , 22 , 32 , 34 , 32 , 44 , 47 , 44 , 40 , 40 , 40 , 44 , 44 , 34 , 31 , 26 , 26 , 34 , 34 , 44 , 41 , 36 , 35 , 35 , 42 , 40 , 44 , 40 , 40 , 33 , 40 , 33 , 45 , 44 , 45 , 40 , 40 , 40 , 44 , 47 , 49 , 49 , 47 , 47 , 41 , 41 , 
                     40 , 40 , 40 , 35 , 38 , 38 , 47 , 40 , 33 , 44 , 44 , 33 , 40 , 38 , 26 , 40 , 44 , 45 , 47 , 47 , 41 , 47 , 47 , 47 , 41 , 47 , 47 , 47 , 47 , 47 , 47 , 40 , 35 , 35 , 35 , 35 , 35 , 44 , 44 , 40 , 47 , 36 , 47 , 44 , 47 , 47 , 41 , 41 , 41 , 47 , 41 , 47 , 41 , 47 , 47 , 41 , 47 , 36 , 44 , 44 , 
                     40 , 44 , 44 , 40 , 44 , 41 , 47 , 41 , 47 , 47 , 47 , 36 , 36 , 40 , 44 , 44 , 44 , 44 , 33 , 36 , 47 , 47 , 47 , 44 , 47 , 44 , 47 , 47 , 47 , 47 , 36 , 47 , 36 , 47 , 41 , 36 , 47 , 36 , 47 , 40 , 32 , 31 , 31 , 31 , 26 , 31 , 47 , 45 , 44 , 44 , 40 , 44 , 35 , 44 , 47 , 47 , 36 , 36 , 47 , 38 , 
                     47 , 47 , 47 , 47 , 47 , 47 , 49 , 49 , 45 , 49 , 35 , 44 , 33 , 40 , 40 , 44 , 41 , 34 , 26 , 31 , 31 , 34 , 35 , 45 , 47 , 47 , 45 , 38 , 47 , 49 , 49 , 38 , 44 , 33 , 40 , 44 , 40 , 44 , 47 , 47 , 47 , 41 , 41 , 47 , 47 , 47 , 47 , 47 , 47 , 47 , 41 , 41 , 47 , 47 , 47 , 47 , 41 , 36 , 47 , 47 , 
                     44 , 47 , 47 , 47 , 44 , 41 , 47 , 47 , 47 , 47 , 47 , 36 , 36 , 41 , 36 , 41 , 44 , 36 , 47 , 49 , 49 , 49 , 47 , 49 , 44 , 41 , 36 , 47 , 41 , 47 , 41 , 36 , 47 , 47 , 47 , 41 , 47 , 47 , 47 , 41 , 47 , 47 , 47 , 45 , 47 , 47 , 47 , 41 , 44 , 47 , 47 , 49 , 47 , 47 , 47 , 47 , 36 , 47 , 44 , 47 , 
                     41 , 47 , 45 , 49 , 47 , 47 , 47 , 47 , 47 , 47 , 36 , 47 , 47 , 47 , 47 , 41 , 41 , 47 , 41 , 36 , 47 , 47 , 47 , 36 , 47 , 45 , 47 , 47 , 47 , 47 , 47 , 45 , 41 , 47 , 41 , 38 , 47 , 36 , 36 , 47 , 47 , 47 , 47 , 47 , 47 , 47 , 45 , 47 , 49 , 49 , 41 , 41 , 47 , 47 , 47 , 47 , 47 , 45 , 49 , 47 , 
                     47 , 47 , 47 , 47 , 45 , 41 , 47 , 49 , 49 , 49 , 47 , 49 , 47 , 47 , 47 , 41 , 47 , 41 , 47 , 47 , 47 , 47 , 47 , 47 , 47 , 45 , 47 , 47 , 47 , 47 , 47 , 47 , 41 , 47 , 47 , 47 , 47 , 47 , 47 , 47 , 47 , 47 , 47 , 44 , 47 , 44 , 36 , 47 , 47 , 47 , 47 , 47 , 44 , 47 , 47 , 47 , 41 , 49 , 47 , 49 , 
                     47 , 47 , 47 , 47 , 47 , 47 , 44 , 44 , 47 , 44 , 47 , 47 , 47 , 49 , 49 , 49 , 47 , 49 , 47 , 40 , 44 , 38 , 35 , 44 , 40 , 47 , 36 , 45 , 44 , 38 , 38 , 35 , 44 , 38 , 47 , 47 , 47 , 41 , 41 , 41 , 45 , 49 , 49 , 45 , 47 , 47 , 47 , 47 , 47 , 36 , 47 , 49 , 49 , 41 , 47 , 40 , 44 , 38 , 35 , 35 , 
                     33 , 40 , 38 , 44 , 44 , 44 , 40 , 44 , 44 , 34 , 40 , 45 , 44 , 36 , 47 , 47 , 47 , 47 , 44 , 47 , 47 , 49 , 49 , 49 , 47 , 47 , 47 , 47 , 47 , 47 , 47 , 44 , 47 , 47 , 47 , 40 , 45 , 44 , 45 , 44 , 35 , 38 , 44 , 35 , 44 , 44 , 47 , 44 , 38 , 49 , 38 , 47 , 38 , 47 , 47 , 47 , 47 , 47 , 36 , 47 , 
                     44 , 36 , 39 , 39 , 40 , 47 , 47 , 49 , 41 , 45 , 49 , 49 , 44 , 35 , 29 , 34 , 35 , 35 , 35 , 44 , 40 , 44 , 47 , 47 , 47 , 47 , 44 , 36 , 47 , 44 , 47 , 47 , 47 , 38 , 49 , 47 , 47 , 47 , 47 , 47 , 47 , 49 , 45 , 38 , 45 , 49 , 47 , 47 , 47 , 47 , 47 , 47 , 41 , 47 , 47 , 47 , 47 , 36 , 45 , 45 , 
                     45 , 36 , 49 , 36 , 44 , 47 , 47 , 40 , 44 , 38 , 38 , 35 , 30 , 28 , 38 , 44 , 44 , 44 , 44 , 38 , 38 , 33 , 44 , 44 , 45 , 49 , 41 , 49 , 45 , 49 , 49 , 38 , 47 , 49 , 34 , 34 , 34 , 35 , 35 , 29 , 33 , 34 , 38 , 44 , 44 , 45 , 44 , 44 , 44 , 44 , 40 , 47 , 47 , 47 , 47 , 47 , 47 , 41 , 47 , 47 , 
                     47 , 40 , 49 , 49 , 47 , 35 , 34 , 40 , 44 , 35 , 45 , 44 , 47 , 38 , 47 , 47 , 47 , 49 , 36 , 49 , 47 , 47 , 36 , 47 , 47 , 47 , 47 , 47 , 47 , 47 , 49 , 49 , 49 , 49 , 47 , 49 , 49 , 49 , 49 , 47 , 45 , 47 , 47 , 47 , 20 , 15 , 9 , 9 , 14 , 16 , 28 , 35 , 35 , 30 , 33 , 27 , 27 , 34 , 31 , 44 , 
                     38 , 40 , 40 , 47 , 44 , 44 , 47 , 47 , 47 , 47 , 49 , 47 , 47 , 36 , 47 , 47 , 38 , 44 , 29 , 45 , 20 , 21 , 11 , 9 , 16 , 16 , 23 , 40 , 40 , 16 , 16 , 9 , 9 , 17 , 11 , 13 , 6 , 10 , 12 , 12 , 33 , 44 , 33 , 33 , 30 , 40 , 45 , 40 , 30 , 32 , 27 , 21 , 19 , 22 , 30 , 16 , 16 , 9 , 9 , 14 , 
                     19 , 29 , 16 , 15 , 9 , 9 , 16 , 11 , 9 , 18 , 18 , 16 , 13 , 15 , 26 , 38 , 26 , 24 , 24 , 16 , 20 , 17 , 13 , 21 , 18 , 12 , 10 , 10 , 6 , 9 , 13 , 13 , 9 , 10 , 10 , 9 , 14 , 13 , 12 , 20 , 24 , 22 , 25 , 19 , 20 , 23 , 16 , 14 , 17 , 19 , 28 , 25 , 14 , 15 , 24 , 22 , 18 , 16 , 13 , 11 , 
                     16 , 10 , 12 , 16 , 14 , 12 , 16 , 16 , 11 , 10 , 9 , 19 , 19 , 13 , 12 , 9 , 10 , 13 , 13 , 12 , 11 , 10 , 9 , 10 , 10 , 15 , 16 , 9 , 9 , 10 , 13 , 10 , 9 , 10 , 9 , 9 , 13 , 10 , 10 , 12 , 10 , 9 , 7 , 10 , 10 , 14 , 10 , 9 , 9 , 13 , 18 , 18 , 9 , 12 , 15 , 11 , 10 , 9 , 13 , 16 , 
                     17 , 18 , 11 , 9 , 9 , 9 , 11 , 11 , 14 , 10 , 10 , 9 , 9 , 13 , 16 , 20 , 14 , 13 , 12 , 11 , 11 , 11 , 18 , 12 , 13 , 12 , 10 , 11 , 10 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 10 , 9 , 11 , 10 , 11 , 9 , 9 , 9 , 6 , 9 , 9 , 9 , 9 , 9 , 9 , 10 , 9 , 10 , 9 , 10 , 10 , 10 , 13 , 
                     10 , 10 , 9 , 10 , 10 }
             ).build();
    protected Map<String,String> expectedProperties;
    
    protected static final ResourceHelper RESOURCE = new ResourceHelper(AbstractTestPhd.class);
    
     
    protected List<PhdReadTag> expectedReadTags;
    
     @Before
     public void setup() throws ParseException{
         expectedProperties = new LinkedHashMap<String,String>();
         expectedProperties.put("TIME", "Tue Dec 18 10:50:48 2007");
         expectedProperties.put("CHEM", "term");
         expectedProperties.put("DYE", "big");
         
         expectedReadTags = new ArrayList<PhdReadTag>(3);
         expectedReadTags.add(new DefaultPhdReadTag("polymorphism", "consed", 
        		 Range.of(CoordinateSystem.RESIDUE_BASED,130,134), 
        		 PhdUtil.parseReadTagDate("10/07/02 12:37:23"),
        		 null, null));
         
         expectedReadTags.add(new DefaultPhdReadTag("polymorphism", "fasta2PhdBall.perl", 
        		 Range.of(CoordinateSystem.RESIDUE_BASED,76,76), 
        		 PhdUtil.parseReadTagDate("10/07/02 13:35:03"),
        		 "chrpos: 27,868,168", null));
         
         expectedReadTags.add(new DefaultPhdReadTag("polymorphism", "fasta2PhdBall.perl", 
        		 Range.of(CoordinateSystem.RESIDUE_BASED,76,76), 
        		 PhdUtil.parseReadTagDate("10/07/02 13:35:03"),
        		 null, "line1 of misc data\nline2 of misc data"));
     }
     
     
}
