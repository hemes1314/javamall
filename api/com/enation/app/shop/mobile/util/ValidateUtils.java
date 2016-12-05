package com.enation.app.shop.mobile.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 校验工具类.
 * 
 * @author baoxiufeng
 */
public class ValidateUtils {

	/** 手机号码校验 */
	private static final String MOBILE_REGEX = "^((1[0-9]))\\d{9}$";
	
	/** 缓存校验模式集合 */
	private static final Map<String, Pattern> patternMap = new HashMap<>();
	
	/**
	 * 初略校验手机号码的格式有效性.
	 * 
	 * @param mobile 待校验的手机号码
	 * @return 校验结果（true-有效、false-无效）
	 */
	public static boolean checkMobile(String mobile) {
		Pattern p = patternMap.get(MOBILE_REGEX);
		if (p == null) {
			patternMap.put(MOBILE_REGEX, p = Pattern.compile(MOBILE_REGEX));
		}
		return p.matcher(mobile).matches();
	}
}
