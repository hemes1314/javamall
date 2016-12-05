package com.enation.app.b2b2c.core.tag.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 店铺商品信息
 * @author LiFenLong
 *
 */
@Component
public class StoreGoodsInfoTag extends BaseFreeMarkerTag{
	private IGoodsManager goodsManager;
	private IGoodsGalleryManager goodsGalleryManager;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map result=new HashMap();
		List<GoodsGallery> galleryList = goodsGalleryManager.list(Integer.valueOf(params.get("goods_id").toString()));
		
		result.put("galleryList", galleryList);
		
		Map goods=goodsManager.get(Integer.valueOf(params.get("goods_id").toString()));
		//goods.put("original",UploadUtil.replacePath(goods.get("original").toString()));
		this.getRequest().setAttribute("goods", goods);
		result.put("goods",goods);
		return result;
	}
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}
	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}
	public IGoodsGalleryManager getGoodsGalleryManager() {
		return goodsGalleryManager;
	}
	public void setGoodsGalleryManager(IGoodsGalleryManager goodsGalleryManager) {
		this.goodsGalleryManager = goodsGalleryManager;
	}
}
