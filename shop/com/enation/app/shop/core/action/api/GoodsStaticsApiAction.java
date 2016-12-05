package com.enation.app.shop.core.action.api;



import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.framework.action.WWAction;

/**
 * 商品静态页标签
 * @author fenlongli
 *
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("goodsStatics")
public class GoodsStaticsApiAction extends WWAction{
	private IProductManager productManager;
	private IGoodsManager goodsManager;
	private Integer productid;	//货品Id
	private Integer goods_id;	//商品Id
	/**
	 * 根据商品Id获取货品库存
	 * @return
	 */
	public String getGoodsStore(){
		this.showSuccessJson(productManager.getByGoodsId(goods_id).getEnable_store()+"");
//		System.out.println(json);
		return this.JSON_MESSAGE;
	}
	/**
	 * 根据货品Id获取货品库存
	 * @param productid 货品Id
	 * @return
	 */
	public String getProductStore(){
		this.showSuccessJson(productManager.get(productid).getEnable_store()+"");
		return this.JSON_MESSAGE;
	}
	public IProductManager getProductManager() {
		return productManager;
	}
	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}
	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}
	public Integer getProductid() {
		return productid;
	}
	public void setProductid(Integer productid) {
		this.productid = productid;
	}
	public Integer getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
	}
	
}
