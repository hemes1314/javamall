package com.enation.app.secbuy.core.tag.act;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.DateUtil;

import freemarker.template.TemplateModelException;

/**
 * 
 * @ClassName: SecBuyActSurplusTag 
 * @Description: 秒拍剩余时间标签 
 * @author TALON 
 * @date 2015-7-31 上午10:47:56 
 *
 */
@Component
public class SecBuyActSurplusTag extends  BaseFreeMarkerTag{

	@Override
	protected Object exec(Map params) throws TemplateModelException {
						
		//获取日期
		long now  = DateUtil.getDateline();
		long end = NumberUtils.toLong(params.get("end_time").toString());
		
		
		//运算的程序开始
		long cha= (end-now);
		
		Map result=new HashMap();
		result.put("cha", cha);
		Object startTime = params.get("start_time");
        if (startTime != null) {
			long start = NumberUtils.toLong(startTime.toString());
			long cha0 = (start - now);
			result.put("cha0", cha0);
		}
		
		return result;
	}
	
}
