package com.enation.app.b2b2c.core.action.backend.stock;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.b2b2c.core.service.goods.IStoreGoodsSpecManager;
import com.enation.app.shop.core.service.IGoodsStoreManager;
/**
 * 店铺商品库存信息
 * @author fenlongli
 *
 */
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="goodsstore_list",type="freemarker", location="/b2b2c/admin/goodsstore/goodsstore_list.html"),
	 @Result(name="dialog_html",type="freemarker", location="/b2b2c/admin/goodsstore/dialog.html")
})
@Action("storeGoodsStock")
public class StoreGoodsStockAction {
	private IStoreGoodsSpecManager storeGoodsSpecManager;
	private Integer goodsid;
	private String html;

	/**
	 * 跳转至商品库存管理页面
	 * @return 商品库存管理页面
	 */
	public String listGoodsStore(){
		return "goodsstore_list";
	}
	
	/**
	 * 获取库存维护对话框页面html;
	 * @param goodsid 商品Id,Integer
	 * @return
	 */
	public String getStockDialogHtml() {
		try {
			html = storeGoodsSpecManager.getStoreHtml(goodsid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "dialog_html";
	}

	//get set
	public IStoreGoodsSpecManager getStoreGoodsSpecManager() {
		return storeGoodsSpecManager;
	}

	public void setStoreGoodsSpecManager(
			IStoreGoodsSpecManager storeGoodsSpecManager) {
		this.storeGoodsSpecManager = storeGoodsSpecManager;
	}

	public Integer getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(Integer goodsid) {
		this.goodsid = goodsid;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

}
