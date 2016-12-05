package com.enation.app.shop.core.tag;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.mapper.GoodsListMapper;
import com.enation.app.shop.core.model.support.GoodsView;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.utils.UrlUtils;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 商品列表标签
 * @author kingapex
 *2013-7-29下午9:45:44
 */
@Component
@Scope("prototype")
public class GoodsListTag extends BaseFreeMarkerTag {
	private IGoodsCatManager goodsCatManager;
	private IGoodsManager goodsManager;
	
	/**
	 * 根据参数获取商品列表
	 * @param catid:分类id,可选项，如果为空则查询所有分类下的商品
	 * @param tagid:标签id，可选项
	 * @param goodsnum:要读取的商品数量，必填项。
	 * @return 商品列表
	 * {@link Goods}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String catid =(String) params.get("catid");
		String tagid = (String)params.get("tagid");
		if(tagid == null)
		{
		    tagid = ThreadContextHolder.getHttpRequest().getParameter("tagid");
		}
		String goodsnum = (String)params.get("goodsnum");
	 
		if(catid == null || catid.equals("")){
			String uri  = ThreadContextHolder.getHttpRequest().getServletPath();
			catid = UrlUtils.getParamStringValue(uri,"cat");
		}
		
		return goodsManager.listGoods(catid, tagid, goodsnum);
	}
	
	
	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}
	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}
	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}
	
	 
}
