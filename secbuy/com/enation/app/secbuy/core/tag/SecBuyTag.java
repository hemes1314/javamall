package com.enation.app.secbuy.core.tag;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.secbuy.core.model.SecBuy;
import com.enation.app.secbuy.core.service.ISecBuyManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.RequestUtil;

import freemarker.template.TemplateModelException;

/**
 * 
 * @ClassName: SecBuyTag 
 * @Description: 秒拍标签 
 * @author TALON 
 * @date 2015-7-31 上午10:49:12 
 *
 */
@Component
@Scope("prototype")
public class SecBuyTag extends BaseFreeMarkerTag {
	
	private ISecBuyManager secBuyManager;
	
	/**
	 * @param gbid 秒拍Id
	 * @return 秒拍实体SecBuy
	 * {@link SecBuy}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
	/*  whj 2015-05-26  暂时取消商品is_secbuy（是否是秒拍商品的判断），因为商品参加秒拍is_secbuy都是1，没有判断的必要吧。
	 * 否则商品详细在IF处报错。
		if(params.get("is_secbuy").toString().equals("0")){
			return "";
		}else{
			Integer goodsid =(Integer) params.get("goodsid");
			SecBuy  secBuy=secBuyManager.getBuyGoodsId(goodsid);
			return secBuy;
		}
	*/	
		
		Integer goodsid =(Integer) params.get("goodsid");
		SecBuy  secBuy=secBuyManager.getBuyGoodsId(goodsid);
		return secBuy;
		
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
	public ISecBuyManager getSecBuyManager() {
		return secBuyManager;
	}
	public void setSecBuyManager(ISecBuyManager secBuyManager) {
		this.secBuyManager = secBuyManager;
	}

	
	
}
