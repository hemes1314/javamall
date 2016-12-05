package com.enation.app.b2b2c.core.service.cart;

import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.cart.StoreCartItem;
import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.b2b2c.core.service.StoreCartKeyEnum;
import com.enation.app.shop.core.model.support.OrderPrice;
/**
 * 店铺购物车管理类
 * @author LiFenLong
 *
 */
public interface IStoreCartManager {
	public static final String FILTER_KEY = "cartFilter"; 
	
	/**
	 * 根据session信息计算费用<br>
	 * 计算结果要通过{@link StoreCartContainer#getStoreCartListFromSession()} 获取到
	 * @param isCountShip 是否计算费用
	 */
	public void countPrice(String isCountShip);

	/**
	 * 根据session信息计算费用<br>
	 * 计算结果要通过{@link StoreCartContainer#getStoreCartListFromSession()} 获取到
	 * @param isCountShip 是否计算费用
	 * @param onlySelected 是否只计算购物车中选中的商品
	 */
	public void countPrice(String isCountShip, boolean onlySelected);
	
	/**
	 * 获取购物车商品列表
	 * @param sessionid
	 * @return List<StoreCartItem>
	 */
	public List<StoreCartItem> listGoods(String sessionid);
	/**
	 * 用cart_id String []
	 * 代替sessionid 
	 * @param cart_id
	 * @return
	 */
	public List<StoreCartItem> listGoodsForApp(String cart_id,String sessionid);
	
	/**
	 * 获取分店铺购物车列表<br>
	 * 
	 * @param sessionid
	 * @param onlySelected
	 * @return 返回的list中map结构如下：<br>
	 * <li>key为store_id的值为店铺id</li>
	 * <li>key为store_name的值为店铺名称</li>
	 * <li>key为goodslist为此店铺的购物车列表</li>
	 * <li>key为storeprice为此店铺的价格对像 {@link OrderPrice}</li>
	 * @see StoreCartKeyEnum
	 */
	public List<Map> storeListGoods(String sessionid, boolean onlySelected);
	/**
	 * 获取分店铺购物车列表<br>
	 * 
	 * @param cart_id  String[]
	 * @return 返回的list中map结构如下：<br>
	 * <li>key为store_id的值为店铺id</li>
	 * <li>key为store_name的值为店铺名称</li>
	 * <li>key为goodslist为此店铺的购物车列表</li>
	 * <li>key为storeprice为此店铺的价格对像 {@link OrderPrice}</li>
	 * @see StoreCartKeyEnum
	 */
	public List<Map> storeListGoodsForApp(String cart_id,String sessionid);
	
	
	
	/**
	 * 清除购物车
	 * @param sessionid
	 */
	public void  clean(String sessionid);
	/**
	 * 计算购物车价格
	 * @param isCountShip
	 */
	public void countPriceForApp(String isCountShip,String cart_id);

	/**
	 * 重新计算用户购物车商品价格
	 */
	public void resetCartPrice();
	
	/**
	 * 获取当前用户购物车中选中的商品
	 */
	public List<Map<String, Object>> listSelectedGoods();

	/**
	 * 如果该商品为下架，已删除等，设置购物车中的该商品为未选中状态
	 */
	public void updateGoodsNoSelected(int goodsId);
}
