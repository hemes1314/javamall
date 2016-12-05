package com.enation.app.advbuy.core.action.api;

import java.util.HashMap;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;

import com.enation.app.advbuy.core.model.AdvBuy;
import com.enation.app.advbuy.core.service.IAdvBuyManager;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
/**
 * 
 * @ClassName: AdvBuyCartApiAction 
 * @Description: 预售购物车API 
 * @author TALON 
 * @date 2015-7-31 上午12:48:32 
 *
 */
@ParentPackage("shop_default")
@Namespace("/api/advbuy")
@Action("cart")
@Component
public class AdvBuyCartApiAction extends WWAction{
	private IProductManager productManager;
	private IAdvBuyManager advBuyManager;
	private ICartManager cartManager;
	private Integer goodsid;
	private Integer num;
	//在向购物车添加货品时，是否在返回的json串中同时显示购物车数据。
	//0为否,1为是
	private int showCartData;
	/**
	 * 将一个商品添加到购物车
	 * 需要传递goodsid 和num参数
	 * @param goodsid 商品id，int型
	 * @param num 数量，int型
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败
	 * message 为提示信息
	 */
	public String addGoods(){
		Product product = productManager.getByGoodsId(goodsid);
		this.addProductToCart(product);
		return this.JSON_MESSAGE;
	}
	/**
	 * 添加货品的购物车
	 * @param product 货品
	 * 1.判断购买的预售货品库存是否充足
	 * 2.判断用户购买的预售商品已经超过了商品的限购数量（此步需要去完善应该去库中去查询一下是否已经添加过预售商品，应该按库中为准）
	 * 3.将预售商品放置购物车中
	 * @return 成功或失败
	 */
	private boolean addProductToCart(Product product){
		String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
		AdvBuy advbuy= advBuyManager.getBuyGoodsId(product.getGoods_id());
		if(product!=null){
			try{
				if(advbuy.getGoods_num()<num){
					throw new RuntimeException("抱歉！您所选择的货品库存不足。");
				}
				if(advbuy.getLimit_num()>num){
					throw new RuntimeException("抱歉！您所选选择的货品数量超过了此商品的限购数量。");
				}
				Cart cart = new Cart();
				cart.setGoods_id(product.getGoods_id());
				cart.setProduct_id(product.getProduct_id());
				cart.setSession_id(sessionid);
				cart.setNum(num);
				cart.setItemtype(0); //0为product和产品 ，当初是为了考虑有赠品什么的，可能有别的类型。
				cart.setWeight(product.getWeight());
				cart.setPrice(advbuy.getPrice());
				cart.setName(product.getName());
				
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
	public IProductManager getProductManager() {
		return productManager;
	}
	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}
	public IAdvBuyManager getAdvBuyManager() {
		return advBuyManager;
	}
	public void setAdvBuyManager(IAdvBuyManager advBuyManager) {
		this.advBuyManager = advBuyManager;
	}
	public ICartManager getCartManager() {
		return cartManager;
	}
	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}
	public Integer getGoodsid() {
		return goodsid;
	}
	public void setGoodsid(Integer goodsid) {
		this.goodsid = goodsid;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public int getShowCartData() {
		return showCartData;
	}
	public void setShowCartData(int showCartData) {
		this.showCartData = showCartData;
	}
}
