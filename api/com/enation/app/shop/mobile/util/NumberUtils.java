package com.enation.app.shop.mobile.util;

/**
 * 数值工具类.
 * 
 * @author baoxiufeng
 */
public class NumberUtils {

	/**
	 * 包装类型到原始类型的安全转换.
	 * 
	 * @param v 待处理的值
	 * @param defaultValue 默认值
	 * @return 转换后的值
	 */
	public static <T> T toNumber(T v, T defalutValue) {
		return v == null ? defalutValue : v;
	}
	
	/**
	 * 包装类型到原始类型的安全转换.
	 * 
	 * @param v 待处理的值
	 * @return 转换后的值
	 */
	public static double toDouble(Double v) {
		return v == null ? 0d : v;
	}
	
	/**
	 * 包装类型到原始类型的安全转换.
	 * 
	 * @param v 待处理的值
	 * @return 转换后的值
	 */
	public static long toLong(Long v) {
		return v == null ? 0 : v;
	}
	
	/**
	 * 包装类型到原始类型的安全转换.
	 * 
	 * @param v 待处理的值
	 * @return 转换后的值
	 */
	public static int toInt(Integer v) {
		return v == null ? 0 : v;
	}
}
