package com.enation.app.b2b2c.core.service;

import java.util.List;

import com.enation.app.b2b2c.core.model.StoreDlyCenter;

public interface IStoreDlyCenterManager {
	
	/**
	 * 查询店铺发货地址列表
	 * @param store_id
	 * @return
	 */
	List getDlyCenterList(Integer store_id);
	
	
	/**
	 * 添加店铺发货地址
	 * @param dlyCenter
	 */
	void addDlyCenter(StoreDlyCenter dlyCenter);
	
	
	/**
	 * 修改店铺发货地址
	 * @param dlyCenter
	 */
	void editDlyCenter(StoreDlyCenter dlyCenter);
	
	
	/**
	 * 查询店铺发货地址
	 * @param store_id
	 * @param dlyid
	 * @return
	 */
	StoreDlyCenter getDlyCenter(Integer store_id,Integer dlyid);
	
	/**
	 * 删除
	 * @param dly_id
	 */
	void delete(Integer dly_id);
	
	/**
	 * 设置默认发货地址
	 * @param dly_id
	 */
	void site_default(Integer dly_id,Integer store_id);
}
