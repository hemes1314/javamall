package com.enation.app.secbuy.core.tag;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.secbuy.core.service.ISecGoodsTagManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 
 * @ClassName: SecBuyGoodsListTag 
 * @Description: 秒拍商品列表标签 
 * @author TALON 
 * @date 2015-7-31 上午10:48:39 
 *
 */
@Component
public class SecBuyGoodsListTag extends BaseFreeMarkerTag{
	private ISecGoodsTagManager secGoodsTagManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String catid =(String) params.get("catid");
		String tagid = (String)params.get("tagid");
		String goodsnum = (String)params.get("goodsnum");
	 
		if(catid == null || catid.equals("")){
			String uri  = ThreadContextHolder.getHttpRequest().getServletPath();
			//catid = UrlUtils.getParamStringValue(uri,"cat");
		}
		
		List goodsList  = secGoodsTagManager.listGoods(catid, tagid, goodsnum);
		 
		return goodsList;
	}

	public ISecGoodsTagManager getSecGoodsTagManager() {
		return secGoodsTagManager;
	}

	public void setSecGoodsTagManager(ISecGoodsTagManager secGoodsTagManager) {
		this.secGoodsTagManager = secGoodsTagManager;
	}

}
