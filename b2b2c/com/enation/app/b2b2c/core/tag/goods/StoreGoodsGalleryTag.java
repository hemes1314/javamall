package com.enation.app.b2b2c.core.tag.goods;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商品相册标签
 * @author kingapex
 *2013-8-1上午10:59:02
 */
@Component
@Scope("prototype")
public class StoreGoodsGalleryTag extends BaseFreeMarkerTag {
	private IGoodsGalleryManager goodsGalleryManager;
	
	/**
	 * 商品相册标签
	 * 特殊说明：调用商品属性标签前，必须先调用商品基本信息标签
	 * @param 无
	 * @return 商品相册 ，类型：List<GoodsGallery>
	 * {@link GoodsGallery}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request  = this.getRequest();
		Integer goods_id=(Integer)params.get("goodsid");
				
		List<GoodsGallery> galleryList = this.goodsGalleryManager.list(goods_id);
		if(galleryList==null || galleryList.size()==0){
			String img  = SystemSetting.getDefault_img_url();
			GoodsGallery gallery = new GoodsGallery();
			gallery.setSmall(img);
			gallery.setBig(img);
			gallery.setThumbnail(img);
			gallery.setTiny(img);
			gallery.setOriginal(img);
			gallery.setIsdefault(1);
			galleryList.add(gallery);
		}
		return galleryList;
	}

	public IGoodsGalleryManager getGoodsGalleryManager() {
		return goodsGalleryManager;
	}

	public void setGoodsGalleryManager(IGoodsGalleryManager goodsGalleryManager) {
		this.goodsGalleryManager = goodsGalleryManager;
	}

}
