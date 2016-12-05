package com.enation.app.flashbuy.core.tag;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.flashbuy.core.model.FlashBuy;
import com.enation.app.flashbuy.core.service.IFlashBuyManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.RequestUtil;

import freemarker.template.TemplateModelException;

/**
 * 
 * @ClassName: FlashBuyTag 
 * @Description: 限时抢购标签 
 * @author TALON 
 * @date 2015-7-31 上午10:49:12 
 *
 */
@Component
@Scope("prototype")
public class FlashBuyTag extends BaseFreeMarkerTag {
	
	private IFlashBuyManager flashBuyManager;
	
	/**
	 * @param gbid 限时抢购Id
	 * @return 限时抢购实体FlashBuy
	 * {@link FlashBuy}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
	/*  whj 2015-05-26  暂时取消商品is_flashbuy（是否是限时抢购商品的判断），因为商品参加限时抢购is_flashbuy都是1，没有判断的必要吧。
	 * 否则商品详细在IF处报错。
		if(params.get("is_flashbuy").toString().equals("0")){
			return "";
		}else{
			Integer goodsid =(Integer) params.get("goodsid");
			FlashBuy  flashBuy=flashBuyManager.getBuyGoodsId(goodsid);
			return flashBuy;
		}
	*/	
		
		Integer goodsid =(Integer) params.get("goodsid");
		FlashBuy  flashBuy=flashBuyManager.getBuyGoodsId(goodsid);
		return flashBuy;
		
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
	public IFlashBuyManager getFlashBuyManager() {
		return flashBuyManager;
	}
	public void setFlashBuyManager(IFlashBuyManager flashBuyManager) {
		this.flashBuyManager = flashBuyManager;
	}

	
	
}
