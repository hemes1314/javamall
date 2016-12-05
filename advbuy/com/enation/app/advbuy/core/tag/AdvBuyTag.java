package com.enation.app.advbuy.core.tag;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.advbuy.core.model.AdvBuy;
import com.enation.app.advbuy.core.service.IAdvBuyManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.RequestUtil;

import freemarker.template.TemplateModelException;

/**
 * 
 * @ClassName: AdvBuyTag 
 * @Description: 预售标签 
 * @author TALON 
 * @date 2015-7-31 上午10:49:12 
 *
 */
@Component
@Scope("prototype")
public class AdvBuyTag extends BaseFreeMarkerTag {
	
	private IAdvBuyManager advBuyManager;
	
	/**
	 * @param gbid 预售Id
	 * @return 预售实体AdvBuy
	 * {@link AdvBuy}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
	/*  whj 2015-05-26  暂时取消商品is_advbuy（是否是预售商品的判断），因为商品参加预售is_advbuy都是1，没有判断的必要吧。
	 * 否则商品详细在IF处报错。
		if(params.get("is_advbuy").toString().equals("0")){
			return "";
		}else{
			Integer goodsid =(Integer) params.get("goodsid");
			AdvBuy  advBuy=advBuyManager.getBuyGoodsId(goodsid);
			return advBuy;
		}
	*/	
		
		Integer goodsid =(Integer) params.get("goodsid");
		AdvBuy  advBuy=advBuyManager.getBuyGoodsId(goodsid);
		return advBuy;
		
	}
	private Integer getGoodsId(){
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		String url = RequestUtil.getRequestUrl(httpRequest);
		String goods_id = this.paseGoodsId(url);
		
		return Integer.valueOf(goods_id);
	}

	private  static String  paseGoodsId(String url){
		String pattern = "(-)(\\d+)";
		String value = null;
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			value=m.group(2);
		}
		return value;
	}
	public IAdvBuyManager getAdvBuyManager() {
		return advBuyManager;
	}
	public void setAdvBuyManager(IAdvBuyManager advBuyManager) {
		this.advBuyManager = advBuyManager;
	}

	
	
}
