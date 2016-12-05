package com.enation.framework.util;

/**
 * 测试通用类
 * 提供测试开关 统一打印方法
 * @author Sylow
 * @version v1.0 , 2015-08-24
 * @since v4.0
 * 
 */
public class TestUtil {
	
	/**
	 * 打印开关
	 */
	public final static boolean TEST_OPEN = true;
	
	
	/**
	 * 打印错误信息
	 * @param e <b>RuntimeException</b>
	 */
	public static void print(RuntimeException e){
		if (TEST_OPEN) {
			e.printStackTrace();
		}
	}

}
