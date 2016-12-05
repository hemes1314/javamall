package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.shop.core.model.DlyCenter;

/**
 * 发货中心
 * 
 * @author lzf<br/>
 *         2010-4-30上午10:12:50<br/>
 *         version 1.0
 */
public interface IDlyCenterManager {
	/**
	 * 获取发货中心列表
	 * @return 发货中心列表
	 */
	public List<DlyCenter> list();
	/**
	 * 根据发货中心Id获取发货中心
	 * @param dly_center_id 发货中心Id
	 * @return 发货中心
	 */
	public DlyCenter get(Integer dly_center_id);
	/**
	 * 添加发货中心
	 * @param dlyCenter 发货中心
	 */
	public void add(DlyCenter dlyCenter);
	/**
	 * 修改发货中心
	 * @param dlyCenter 发货中心
	 */
	public void edit(DlyCenter dlyCenter);
	/**
	 * 删除发货中心
	 * @param id 发货中心Id数组
	 */
	public void delete(Integer[] id);

}
