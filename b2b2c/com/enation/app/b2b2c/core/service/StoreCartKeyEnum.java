package com.enation.app.b2b2c.core.service;

import java.util.HashMap;
import java.util.Map;

import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;

/**
 * 店铺购物车Map Key枚举<br>
 * 此类表明了，在获取店铺购物车列表中的Map的结构
 * @author kingapex
 * 2015年8月31日下午8:21:14
 * @see IStoreCartManager#storeListGoods(String)
 */
public enum StoreCartKeyEnum {
	
	//店铺id
	store_id("store_id"),
	
	//店铺名称
	store_name("store_name"),
	
	//购物列表
	goodslist("goodslist"),
	
	//店铺价格
	storeprice("storeprice"),
	
	//配送方式id
	shiptypeid("shiptypeid"),
	
	//配送方式列表
	shiptype_list("shiplist");
	
	
	private String key;
	StoreCartKeyEnum(String _key){
		this.key=_key;
	}
	
	
	public String toString(){
		return this.key;
	}
	
	public static void main(String[] args) {
	 
		Map map = new HashMap();
		map.put("store_id", "a");
		System.out.println(map.get(StoreCartKeyEnum.store_id.toString()));
	}
}
