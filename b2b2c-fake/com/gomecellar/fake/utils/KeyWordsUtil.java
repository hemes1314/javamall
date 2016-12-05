package com.gomecellar.fake.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class KeyWordsUtil {
   /**
    * 随机生成评论
 * @param keyWord 关键词@字符串
 * @param number  需要生成数据数量
 * @param baseStep 基数步长
 * @param randStep 随机步长
 * @return  评论集合
 */
public static List<String> splitCommentsKeyWords(String keyWord,int number,int baseStep,int randStep){
        
        List<String> list = new ArrayList<String>();
        String[] keyWords = keyWord.split("@");
        Random rand = new Random();
        for(int i = 0; i < number; i++) {
            StringBuffer oneRecord = new StringBuffer();
            int connectStep = rand.nextInt(randStep);
            int times = baseStep + connectStep;
            for(int z = 0; z < times; z++) {
                int j = rand.nextInt(keyWords.length);
                // 一个词
                oneRecord.append(keyWords[j]);
                if(z == (times-1)){
                    oneRecord.append("。");
                }else{
                    oneRecord.append(",");
                }
            }
            list.add(oneRecord.toString());
        }
        return list;
    }


public static long randomDate(String beginDate, String endDate,String ssuffix,String esuffix) {
    try {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = format.parse(beginDate);// 开始日期
        Date end = format.parse(endDate);// 结束日期
        if (start.getTime() >= end.getTime()) {
            return 0;
        }
        long date = random(start.getTime(), end.getTime(),ssuffix,esuffix);

        return new Date(date).getTime();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return 0;
}

private static long random(long begin, long end,String ssuffix,String esuffix) throws ParseException {
    long rtnn = begin + (long) (Math.random() * (end - begin));
    Date temp = new Date(rtnn);
    
    SimpleDateFormat apend_str = new SimpleDateFormat("yyyy-MM-dd "+ssuffix);
    SimpleDateFormat apend_end = new SimpleDateFormat("yyyy-MM-dd "+esuffix);
    SimpleDateFormat apend_change = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   
    if (rtnn == begin || rtnn == end) {
        return random(begin, end,ssuffix,esuffix);
    }
    else{
        long long_str_date =apend_change.parse(apend_str.format(temp)).getTime();
        long long_end_date =apend_change.parse(apend_end.format(temp)).getTime();
        if (long_str_date <= rtnn && rtnn<=long_end_date) {
            return rtnn;
        }else{
            return random(begin, end,ssuffix,esuffix);
        }
    }
        
}
}
