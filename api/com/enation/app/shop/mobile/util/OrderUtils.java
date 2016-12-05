package com.enation.app.shop.mobile.util;

import java.util.Random;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 订单工具类.
 * 
 * @author baoxiufeng
 */
public class OrderUtils {

	 /** 随机数生成器 */
    private static final Random rnd = new Random();
    /** 订单模数 */
    private static final Integer MODE = 1000;

    /**
     * 获取订单号（秒时间+随机数）
     *
     * @return 生成的订单号
     */
    public static String getOrderSn() {
        return String.valueOf(System.currentTimeMillis() / MODE * MODE + Math.abs(rnd.nextInt()) % MODE);
    }
    
    /**
     * 生成退货单号.
     * 
     * @return 退货单号
     */
    public static String getTradeNo() {
    	return DateFormatUtils.format(System.currentTimeMillis(), "yyMMddhhmmssSSS");
    }
    
    /**
     * 获取退款批次号.
     * 
     * @return 退款批次号
     */
    public static String getRefundBatchNo() {
    	return DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS");
    }
}
