package com.enation.app.b2b2c.core.service;

import java.util.List;

/**
 * 店铺快递公司管理类
 * @author fenlongli
 *
 */
public interface IStoreLogiCompanyManager {
	/**
	 * 获取店铺快递公司列表
	 * @return
	 */
	public List list();
	/**
	 * 获取本店的快递公司列表
	 * @return
	 */
	public List listByStore();
	
	/**
	 * 添加本店的快递
	 */
	public void addRel(Integer logi_id);
	
	/**
	 * 删除本店的快递
	 */
	public void deleteRel(Integer logi_id);
}
