package com.enation.app.b2b2c.core.action.api.cart;

import java.util.HashMap;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.StoreProduct;
import com.enation.app.b2b2c.core.model.cart.StoreCart;
import com.enation.app.b2b2c.core.service.cart.IStoreProductManager;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 店铺购物车API
 * @author LiFenLong
 *
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/store")
@Action("storeCart")
public class StoreCartApiAction extends WWAction{

	private ICartManager cartManager;
	private IStoreProductManager storeProductManager;
	private int num;//要向购物车中活加的货品数量
	private int goodsid;
	private int showCartData;	//在向购物车添加货品时，是否在返回的json串中同时显示购物车数据。0为否,1为是
	private Integer productid;
	
	/**
	 * 添加货品的购物车
	 * @param product
	 * @return
	 */
	private boolean addProductToCart(StoreProduct product){
		String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
		
		if(product!=null){
			try{
				if(product.getStore()==null || product.getStore()<num){
					throw new RuntimeException("抱歉！您所选择的货品库存不足。");
				}
				StoreCart cart = new StoreCart();
				cart.setGoods_id(product.getGoods_id());
				cart.setProduct_id(product.getProduct_id());
				cart.setSession_id(sessionid);
				cart.setNum(num);
				cart.setItemtype(0); //0为product和产品 ，当初是为了考虑有赠品什么的，可能有别的类型。
				cart.setWeight(product.getWeight());
				cart.setPrice( product.getPrice() );
				cart.setName(product.getName());
				cart.setStore_id(product.getStore_id());
				this.cartManager.add(cart); 
				this.showSuccessJson("货品成功添加到购物车");

				//需要同时显示购物车信息
				if(showCartData==1){
					this.getCartData();
				}
				
				return true;
			}catch(RuntimeException e){
				this.logger.error("将货品添加至购物车出错",e);
				this.showErrorJson("将货品添加至购物车出错["+e.getMessage()+"]");
				return false;
			}
			
		}else{
			this.showErrorJson("该货品不存在，未能添加到购物车");
			return false;
		}
	}
	/**
	 * 获取购物车数据
	 * @param 无
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败
	 * data.count：购物车的商品总数,int 型
	 * data.total:购物车总价，int型
	 * 
	 */
	public String getCartData(){
		
		try{
			
			String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
			
			Double goodsTotal  =cartManager.countGoodsTotal( sessionid );
			int count = this.cartManager.countItemNum(sessionid);
			
			java.util.Map<String, Object> data =new HashMap();
			data.put("count", count);//购物车中的商品数量
			data.put("total", goodsTotal);//总价
			
			this.json = JsonMessageUtil.getObjectJson(data);
			
		}catch(Throwable e ){
			this.logger.error("获取购物车数据出错",e);
			this.showErrorJson("获取购物车数据出错["+e.getMessage()+"]");
		}
		
		return this.JSON_MESSAGE;
	}
	/**
	 * 将一个商品添加到购物车
	 * 需要传递goodsid 和num参数
	 * @param goodsid 商品id，int型
	 * @param num 数量，int型
	 * 
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败
	 * message 为提示信息
	 */
	public String addGoods(){
		StoreProduct product = null;
		if(productid!=null){
			product = storeProductManager.get(productid);
		}else{
			product = storeProductManager.getByGoodsId(goodsid);
		}
		this.addProductToCart(product);
		return this.JSON_MESSAGE;
	}
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getGoodsid() {
		return goodsid;
	}
	public void setGoodsid(int goodsid) {
		this.goodsid = goodsid;
	}
	public ICartManager getCartManager() {
		return cartManager;
	}
	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}
	public int getShowCartData() {
		return showCartData;
	}
	public void setShowCartData(int showCartData) {
		this.showCartData = showCartData;
	}
	public IStoreProductManager getStoreProductManager() {
		return storeProductManager;
	}
	public void setStoreProductManager(IStoreProductManager storeProductManager) {
		this.storeProductManager = storeProductManager;
	}
	public Integer getProductid() {
		return productid;
	}
	public void setProductid(Integer productid) {
		this.productid = productid;
	}
	
}
