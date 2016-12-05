package com.enation.app.shop.core.taglib.goodssearch;

import java.io.IOException;
import java.util.Map;

import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 商品搜索标签
 * @author kingapex
 *2013-8-20下午5:12:32
 */
public class GoodsSearchDirectiveModel implements TemplateDirectiveModel {

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Object obj = params.get("fieldname");
		if(obj==null){
			obj="goodsids";
		}
		//obj = params.get("showResult");
		
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.setPageName("goods_search");
		freeMarkerPaser.putData("fieldname",obj);
		if(obj!=null) {
			freeMarkerPaser.putData("showResult",obj);
		}
		String html=freeMarkerPaser.proessPageContent();
		env.getOut().write(html);
	}

}
