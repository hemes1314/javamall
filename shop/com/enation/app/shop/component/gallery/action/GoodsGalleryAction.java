package com.enation.app.shop.component.gallery.action;

import java.io.File;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.eop.sdk.context.UserConext;


import com.enation.framework.action.WWAction;


/**
 * 商品相册action
 * @author kingapex
 *
 */

@ParentPackage("shop_default")
@Namespace("/api/shop")
@Action("goods-gallery")
public class GoodsGalleryAction extends WWAction {

	private File filedata;
	private String filedataFileName;
	private String photoName;
	private IGoodsGalleryManager goodsGalleryManager;
	
	private boolean checkAdminPerm() {
		if (UserConext.getCurrentAdminUser()==null) {
			this.showErrorJson("您无权限访问此API");
			return false;
		}
		return true;
	}
	
	public String upload(){
//		if(!this.checkAdminPerm()){
//			return this.JSON_MESSAGE;
//		}

		if (filedata != null) {
			String name = goodsGalleryManager.upload(filedata, filedataFileName);
			this.json = name;
		}
		return this.JSON_MESSAGE;
	}
	
	public String delete() {
		if (!this.checkAdminPerm()) {
			return this.JSON_MESSAGE;
		}

		this.goodsGalleryManager.delete(photoName);
		this.showSuccessJson("图片删除成功");
		return this.JSON_MESSAGE;
	}
	
	public File getFiledata() {
		return filedata;
	}

	public void setFiledata(File filedata) {
		this.filedata = filedata;
	}

	public String getFiledataFileName() {
		return filedataFileName;
	}

	public void setFiledataFileName(String filedataFileName) {
		this.filedataFileName = filedataFileName;
	}

	public String getPhotoName() {
		return photoName;
	}

	public IGoodsGalleryManager getGoodsGalleryManager() {
		return goodsGalleryManager;
	}

	public void setGoodsGalleryManager(IGoodsGalleryManager goodsGalleryManager) {
		this.goodsGalleryManager = goodsGalleryManager;
	}

	public void setPhotoName(String photoName) {
		this.photoName = photoName;
	}

}
