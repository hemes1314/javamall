package com.enation.app.shop.mobile.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.MaxWordSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.Word;

public class ChineseUtils {

  private static Logger logger = LoggerFactory.getLogger(ChineseUtils.class.getSimpleName());

  private static Dictionary dic = null;
  private static HanyuPinyinOutputFormat pinyin = null;
  private static Seg segCom = new ComplexSeg(getDictionaryInstance());
  private static Seg segMax = new MaxWordSeg(getDictionaryInstance());

  /**
   * 获取汉字字符的拼音排列(包括多音)
   *
   * @param ch
   *          汉字字符，如"长"
   * @return [c, ch, chang, z, zh, zhang]
   * humaodong 11/03/2010
   */
  public static Set<String> getCharPinyinPermutation(char ch, boolean bFullPinyin, boolean bFirst, boolean bYunmu/*韵母sh,zh,ch*/) {
    final HashSet<String> result = new HashSet<String>();
    if (!isHanZi(ch)) return null;
    try {
      String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch, getPinYinInstance());
      if (pinyinArray == null) {
        logger.error("Unrecognize char: {}", String.valueOf(ch));
        return null;
      }
      for (final String pinyin : pinyinArray) {
        final String head = getInitials(pinyin);
        if (bFirst) result.add(String.valueOf(head.charAt(0)));
        if (bYunmu && head.length() > 1) result.add(head);
        if (bFullPinyin) result.add(pinyin);
      }
      return result;
    } catch (final BadHanyuPinyinOutputFormatCombination e) {
      logger.error("{}: {}", String.valueOf(ch), e.toString());
    } catch (Exception e) {
      logger.error("{}: {}", String.valueOf(ch), e.toString());
    }

    return null;
  }

  public static String getInitials(String pinyin) {
    if (StringUtils.isBlank(pinyin)) return null;
    final char first = pinyin.charAt(0);
    if (first != 's' && first != 'c' && first != 'z') return String.valueOf(first);
    if (pinyin.length() >= 2 && pinyin.charAt(1) == 'h') return pinyin.substring(0, 2);
    return String.valueOf(first);
  }

  /**
   * 获取汉语词组所有单字的全拼集合 如: 长江七号 chang zhang jiang qi hao
   *
   * humaodong 11/03/2010
   */
  public static Set<String> getWordFullPinyin(String word) {
    final char[] CharArray = word.toCharArray();
    final HashSet<String> result = new HashSet<String>();
    for (final char ch : CharArray) {
      Set<String> chPinyinSet = getCharPinyinPermutation(ch, true, false, false);
      if (chPinyinSet != null) result.addAll(chPinyinSet);
    }
    return result;
  }

  /**
   * 获取汉语词组的可能的拼音组合 如: 长江 cj cjiang chj chjiang changj changjiang... 如: 共和国 ghg
   * gheg gheguo gonghg gonghguo...
   *
   * humaodong 11/03/2010
   */
  public static Set<String> getWordPinyinCombination(String word, boolean bFullPinyin, boolean bFirst, boolean bYunmu) {
    final char[] CharArray = word.toCharArray();
    final ArrayList<Set<String>> SetArray = new ArrayList<Set<String>>();
    for (final char ch : CharArray) {
      Set<String> chPinyinSet = getCharPinyinPermutation(ch, bFullPinyin, bFirst, bYunmu);
      if (chPinyinSet != null) SetArray.add(chPinyinSet);
    }
    final HashSet<String> combinationResult = new HashSet<String>();
    combinePinyinSet(combinationResult, "", SetArray, 0);
    return combinationResult;
  }

  public static boolean isHanZi(char ch) {
    return ch >= 0x4E00 && ch <= 0x9FA5;
  }

  public static boolean allHanZi(String word) {
    final char[] CharArray = word.toCharArray();
    for (final char ch : CharArray) {
      if (!isHanZi(ch)) return false;
    }
    return true;
  }

  public static String trimHanZi(String word) {
    return word.replace(" ", "").replace("　", "");
  }

//  public static void main(String[] args) throws IOException, BadHanyuPinyinOutputFormatCombination {
//    System.out.println(ChineseUtils.class.getResource("/").getFile());
//    System.out.println(ChineseUtils.allHanZi("2008"));
//    System.out.println(getCharPinyinPermutation('长', true, true, true));
//    System.out.println(getWordPinyinCombination("长江", true, true, true));
//    System.out.println(getCharPinyinPermutation('和', true, true, true));
//    System.out.println(getWordPinyinCombination("共和国", true, true, true));
//    System.out.println(toWords("长江七号爱地球"));
//    System.out.println(toWords("叶问2宗师传奇(高清)"));
//    System.out.println(toPinyin("长江七号爱地球"));
//
//    @SuppressWarnings("unchecked")
//    final List<String> list = FileUtils.readLines(new File(
//        "/Users/mamba/Documents/Projects/itvi/codes/matrix/trunk/dependent/doc/db/hanzhi.txt"), "UTF-8");
//
//    for (final String zhi : list) {
//      final Set<String> set = toPinyin(zhi);
//      System.out.print(zhi + set);
//      for (final String pinyin : set) {
//        System.out.print(getInitials(pinyin) + ",");
//      }
//      System.out.println("");
//    }
//  }

  public static void reloadDictionary() {
    if (dic != null) dic.reload();
  }

  /**
   * 获取拼音集合
   */
  public static Set<String> toPinyin(String src) {
    if (src != null && !src.trim().equalsIgnoreCase("")) {
      char[] srcChar;
      srcChar = src.toCharArray();
      final String[][] temp = new String[src.length()][];
      for (int i = 0; i < srcChar.length; i++) {
        final char c = srcChar[i];
        //是中文或者a-z或者A-Z转换拼音(我的需求，是保留中文或者a-z或者A-Z)
        if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {
          try {
            temp[i] = PinyinHelper.toHanyuPinyinStringArray(srcChar[i], getPinYinInstance());
          } catch (final BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
          }
        } else if (c >= 65 && c <= 90 || c >= 97 && c <= 122) {
          temp[i] = new String[] { String.valueOf(srcChar[i]) };
        } else {
          temp[i] = new String[] { "" };
        }
        final Set<String> set1 = new HashSet<String>();
        for (final String element : temp[i]) {
          set1.add(element);
        }
      }

      final String[] pingyinArray = exchange(temp);
      final Set<String> pinyinSet = new HashSet<String>();
      for (final String element : pingyinArray) {
        pinyinSet.add(element);
      }

      return pinyinSet;
    }

    return null;
  }

  public static Set<String> toWords(String line) {
    final Set<String> words = new HashSet<String>();
    Reader input = new StringReader(line);

    MMSeg mmSeg = new MMSeg(input, segCom);
    Word word = null;
    try {
      while ((word = mmSeg.next()) != null) {
        final String w = trimHanZi(word.getString());
        if (w.length() < 2 || !allHanZi(w)) continue;
        words.add(w);
        logger.debug("Complex mode input: " + line);
        logger.debug("Complex mode result: " + w);
      }

      IOUtils.closeQuietly(input);

      input = new StringReader(line);
      mmSeg = new MMSeg(input, segMax);

      while ((word = mmSeg.next()) != null) {
        final String w = trimHanZi(word.getString());
        if (w.length() < 2 || !allHanZi(w)) continue;
        words.add(w);
        logger.debug("MaxWord mode input: " + line);
        logger.debug("MaxWord mode result: " + w);
      }
    } catch (final IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(input);
    }

    return words;
  }

  /**
   * 递归组合拼音集合
   *
   * humaodong 11/03/2010
   */
  private static void combinePinyinSet(Set<String> result, String head, ArrayList<Set<String>> setArray, int level) {
    final Set<String> cur_set = setArray.get(level);
    if (level == setArray.size() - 1) {
      for (final String str : cur_set)
        result.add(head + str);
    } else {
      for (final String str : cur_set)
        combinePinyinSet(result, head + str, setArray, level + 1);
    }
  }

  /**
   * 递归
   */
  private static String[][] doExchange(String[][] strJaggedArray) {
    final int len = strJaggedArray.length;
    if (len >= 2) {
      final int len1 = strJaggedArray[0].length;
      final int len2 = strJaggedArray[1].length;
      final int newlen = len1 * len2;
      final String[] temp = new String[newlen];
      int Index = 0;
      for (int i = 0; i < len1; i++) {
        for (int j = 0; j < len2; j++) {
          temp[Index] = strJaggedArray[0][i] + strJaggedArray[1][j];
          Index++;
        }
      }
      final String[][] newArray = new String[len - 1][];
      for (int i = 2; i < len; i++) {
        newArray[i - 1] = strJaggedArray[i];
      }
      newArray[0] = temp;
      return doExchange(newArray);
    } else {
      return strJaggedArray;
    }
  }

  /**
   * 递归
   */
  private static String[] exchange(String[][] strJaggedArray) {
    final String[][] temp = doExchange(strJaggedArray);
    return temp[0];
  }

  private static Dictionary getDictionaryInstance() {
    if (dic == null) {
      dic = Dictionary.getInstance(ChineseUtils.class.getResource("/").getFile() + "dics/");
    }

    return dic;
  }

  private static HanyuPinyinOutputFormat getPinYinInstance() {
    if (pinyin == null) {
      pinyin = new HanyuPinyinOutputFormat();
      //输出设置，大小写，音标方式等
      pinyin.setCaseType(HanyuPinyinCaseType.LOWERCASE);
      pinyin.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
      pinyin.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    return pinyin;
  }

  /**如果type等于1,生成全拼,简拼,首字母拼音;如果类型等于2生成全拼,简拼;如果类型等于3只生成简拼
   *
   * @param text
   * @param type
   * @return
   */
  public static String makePinyin(String text, Integer type) {
    text = cutText(text);
    final StringBuilder sb = new StringBuilder(text);
    for(int i = 0; i < sb.length(); i++) {
      if(!isHanZi(sb.charAt(i))) sb.setCharAt(i, ' ');
    }
    final String[] textSegs = StringUtils.split(sb.toString(), ' ');

    final HashSet<String> allPinyin = new HashSet<String>();
    for(final String seg : textSegs) {
      if (type == 1 || type == null) {
        final Set<String> pinyin = getWordPinyinCombination(seg, true, true, false);
        allPinyin.addAll(pinyin);
      }
      if (type == 2) {
        final Set<String> pinyin = getWordPinyinCombination(seg, true, false, false);
        pinyin.addAll(getWordPinyinCombination(seg, false, true, false));
        allPinyin.addAll(pinyin);
      }
      if (type == 3) {
        final Set<String> pinyin = getWordPinyinCombination(seg, false, true, false);
        allPinyin.addAll(pinyin);
      }
    }
    String pinyin = StringUtils.join(allPinyin.toArray(), ' ');
    if(textSegs != null && textSegs.length > 1) {
      //System.out.println(String.format("%s ==> %s", text, StringUtils.join(textSegs, ' ')));
      //System.out.println(pinyin);
    }
    //if (pinyin.length() > 65535)  pinyin = pinyin.substring(0, 65535);
    return pinyin;
  }

  private static String cutText(String text) {
    String cutTitle = null;
    int chineseCount = 0;
    for(int i = 0; i < text.length(); i++) {
      cutTitle += text.charAt(i);
      if (ChineseUtils.isHanZi(text.charAt(i))) chineseCount += 1;
      if (chineseCount == 8)  break;
    }
    return cutTitle;
  }

}
