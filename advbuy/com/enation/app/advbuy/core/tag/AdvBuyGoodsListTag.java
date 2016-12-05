package com.enation.app.advbuy.core.tag;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.advbuy.core.service.IAdvGoodsTagManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 
 * @ClassName: AdvBuyGoodsListTag 
 * @Description: 预售商品列表标签 
 * @author TALON 
 * @date 2015-7-31 上午10:48:39 
 *
 */
@Component
public class AdvBuyGoodsListTag extends BaseFreeMarkerTag{
	private IAdvGoodsTagManager advGoodsTagManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String catid =(String) params.get("catid");
		String tagid = (String)params.get("tagid");
		String goodsnum = (String)params.get("goodsnum");
	 
		if(catid == null || catid.equals("")){
			String uri  = ThreadContextHolder.getHttpRequest().getServletPath();
			//catid = UrlUtils.getParamStringValue(uri,"cat");
		}
		
		List goodsList  = advGoodsTagManager.listGoods(catid, tagid, goodsnum);
		 
		return goodsList;
	}

	public IAdvGoodsTagManager getAdvGoodsTagManager() {
		return advGoodsTagManager;
	}

	public void setAdvGoodsTagManager(IAdvGoodsTagManager advGoodsTagManager) {
		this.advGoodsTagManager = advGoodsTagManager;
	}

}
