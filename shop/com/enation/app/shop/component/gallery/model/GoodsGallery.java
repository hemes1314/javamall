package com.enation.app.shop.component.gallery.model;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

public class GoodsGallery {
	
	private int img_id;
	private int goods_id;
	private String thumbnail;//列表尺寸，用于各商品列表中
	private String small;//小尺寸，用于商品详细中preview
	private String big;//大尺寸
	private String original;//原尺寸
	private String tiny;//极小的，用于商品详细页中主图下的小列表图
	private int isdefault;
	private String original_gfs;
	private int disabled;
	
	@PrimaryKeyField
	public int getImg_id() {
		return img_id;
	}
	public void setImg_id(int img_id) {
		this.img_id = img_id;
	}
	public int getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(int goods_id) {
		this.goods_id = goods_id;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getSmall() {
		return small;
	}
	public void setSmall(String small) {
		this.small = small;
	}
	public String getBig() {
		return big;
	}
	public void setBig(String big) {
		this.big = big;
	}
	public String getOriginal() {
		return original;
	}
	public void setOriginal(String original) {
		this.original = original;
	}
	public String getTiny() {
		return tiny;
	}
	public void setTiny(String tiny) {
		this.tiny = tiny;
	}
	public int getIsdefault() {
		return isdefault;
	}
	public void setIsdefault(int isdefault) {
		this.isdefault = isdefault;
	}
    public String getOriginal_gfs() {
        return original_gfs;
    }
    public void setOriginal_gfs(String original_gfs) {
        this.original_gfs = original_gfs;
    }
    @NotDbField
	public int getDisabled() {
		return disabled;
	}
	public void setDisabled(int disabled) {
		this.disabled = disabled;
	}
}
