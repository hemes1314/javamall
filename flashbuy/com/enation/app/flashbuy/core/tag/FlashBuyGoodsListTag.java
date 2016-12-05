package com.enation.app.flashbuy.core.tag;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.flashbuy.core.service.IFlashGoodsTagManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 
 * @ClassName: FlashBuyGoodsListTag 
 * @Description: 限时抢购商品列表标签 
 * @author TALON 
 * @date 2015-7-31 上午10:48:39 
 *
 */
@Component
public class FlashBuyGoodsListTag extends BaseFreeMarkerTag{
	private IFlashGoodsTagManager flashGoodsTagManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String catid =(String) params.get("catid");
		String tagid = (String)params.get("tagid");
		String goodsnum = (String)params.get("goodsnum");
	 
		if(catid == null || catid.equals("")){
			String uri  = ThreadContextHolder.getHttpRequest().getServletPath();
			//catid = UrlUtils.getParamStringValue(uri,"cat");
		}
		
		List goodsList  = flashGoodsTagManager.listGoods(catid, tagid, goodsnum);
		 
		return goodsList;
	}

	public IFlashGoodsTagManager getFlashGoodsTagManager() {
		return flashGoodsTagManager;
	}

	public void setFlashGoodsTagManager(IFlashGoodsTagManager flashGoodsTagManager) {
		this.flashGoodsTagManager = flashGoodsTagManager;
	}

}
