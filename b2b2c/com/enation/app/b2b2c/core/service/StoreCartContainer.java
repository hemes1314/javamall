package com.enation.app.b2b2c.core.service;

import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.MemberAddress;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import org.apache.commons.collections.map.HashedMap;

/**
 * 多店系统购物车列表容器<br>
 * 可通过此类获由session取多店系统中的购物车列表<br>
 * 此列表是计算了各种费用的。可能过如下key获取：<br>
 * 
 * @author kingapex
 * @version 1.0
 *2015年8月31日下午5:13:55
 */
public abstract class StoreCartContainer {
	
	//存放购物车列表的 key
	private static final String CART_SESSION_KEY ="store_cart_list";
	
	//存放收货地址的key
	private static final String ADDRESS_SESSION_KEY ="user_selected_address";
	
	/**
	 * 清空多店购物车的session
	 */
	public static void cleanSession(){
		
		ThreadContextHolder.getSessionContext().removeAttribute(CART_SESSION_KEY);
		ThreadContextHolder.getSessionContext().removeAttribute(ADDRESS_SESSION_KEY);
		
	}
	
	/**
	 * 由session中获取购物车列表
	 * @return 购物车列表<br>
	 * 如果没有进入过购物车列表会返回NULL
	 */
	public static List<Map> getStoreCartListFromSession(){
		return (List)ThreadContextHolder.getSessionContext().getAttribute(CART_SESSION_KEY);
	}
	
	
	
	/**
	 * 向session中压入购物车列表<br>
	 * 当进入到购物车列时需要调用此方法
	 * @param storeCartList 购物车列表
	 */
	public static void putStoreCartListToSession(List<Map> storeCartList){
		ThreadContextHolder.getSessionContext().setAttribute(CART_SESSION_KEY, storeCartList);
	}
	
	
	/**
	 * 由购物车列表中获取某个店铺的的数据
	 * @param storeid 店铺id
	 * @return 购物车列表形成的店铺数据
	 * @throws RuntimeException :购物车列表为空，或购物车中未找到此店铺的商品抛出此异常.</br>
	 * 可通过getMessage()来获取具体错误
	 */
	public static Map getStoreData(int storeid){
		
		List<Map> storeCartList =  getStoreCartListFromSession();
		if(storeCartList==null) {
			throw new RuntimeException("在购物车中未找到店铺id为["+storeid+"]的商品");
		}
		
		for (Map map : storeCartList) {
			Integer innerStoreid = (Integer)map.get(StoreCartKeyEnum.store_id.toString());
			if(storeid== innerStoreid){
				return map;
			}
		}
		
		throw new RuntimeException("在购物车中未找到店铺id为["+storeid+"]的商品");
		
	}
	
	
	
	/**
	 * 获取用户选中的收货地址<br>
	 * 如果没有选择过，使用默认的收货地址
	 * @return 返回当前用户选中的收货地址，如果没有选择过，也没有默认地址返回NULL
	 */
	public static MemberAddress getUserSelectedAddress(){
		
		return (MemberAddress)ThreadContextHolder.getSessionContext().getAttribute(ADDRESS_SESSION_KEY);
	}
	
	/**
	 * 向session中压入选中的收货地址<br>
	 * 当进入结算页时就应该调用此方法将默认收货地址压入
	 * @param address 要压入的收货地址
	 */
	public static void putSelectedAddress(MemberAddress address){
		ThreadContextHolder.getSessionContext().setAttribute(ADDRESS_SESSION_KEY, address);
		
	}
}
