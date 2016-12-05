package com.enation.app.shop.component.gallery.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.framework.action.WWAction;

/**
 * 商品相册缩略图生成action
 * @author kingapex
 *
 */

@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Results({ 
    @Result(name="input", type="freemarker", location="/com/enation/app/shop/component/gallery/action/gallery.html")
}) 
@Action 
public class GalleryRecreateAction extends WWAction {
	private IGoodsGalleryManager goodsGalleryManager;
	private int total;
	private int start;
	private int end;
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String recreate(){
		try{
			this.goodsGalleryManager.recreate(start,end);
			this.showSuccessJson("生成商品相册缩略图成功 ");
		}catch(RuntimeException e){
			this.logger.error("生成商品相册缩略图错误", e);
			this.showErrorJson("生成商品相册缩略图错误"+e.getMessage());
		}
		return  this.JSON_MESSAGE;
	}
	
	public String execute(){
		total = this.goodsGalleryManager.getTotal();
		return this.INPUT;
	}

	public IGoodsGalleryManager getGoodsGalleryManager() {
		return goodsGalleryManager;
	}

	public void setGoodsGalleryManager(IGoodsGalleryManager goodsGalleryManager) {
		this.goodsGalleryManager = goodsGalleryManager;
	}
		
 

}
