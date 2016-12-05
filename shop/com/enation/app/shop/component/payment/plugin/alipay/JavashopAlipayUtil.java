package com.enation.app.shop.component.payment.plugin.alipay;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.enation.app.shop.component.payment.plugin.alipay.sdk33.util.AlipayNotify;
import com.enation.framework.context.webcontext.ThreadContextHolder;


/**
 * 支付宝工具
 * 提供验证方法
 * @author kingapex
 * @version 1.0
 *2015年9月24日下午1:47:42
 */
public abstract class JavashopAlipayUtil {
    private static Log log = LogFactory.getLog(JavashopAlipayUtil.class);
    
	public static boolean verify(String param_encoding) {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			Map<String, String> params = new HashMap<String, String>();
			Map requestParams = request.getParameterMap();
			log.info("循环开始");
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
				}
				// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
//				if(!StringUtil.isEmpty(param_encoding)){
//					 valueStr = new String(valueStr.getBytes("ISO-8859-1"), param_encoding);
//				}
				params.put(name, valueStr);

			}
			log.info("循环结束");
			return (AlipayNotify.verify(params));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
