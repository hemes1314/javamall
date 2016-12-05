package com.enation.app.b2b2c.core.service;

import java.util.List;

/**
 * 地区接口
 * @author xulipeng
 *
 */
public interface IStoreRegionsManager {

	/**
	 * 查询地区类表，到市级
	 * @return
	 */
	public List getRegionsToAreaList();
	
	/**
	 * 根据多个地区id，查询地区数据
	 * @param ids
	 * @return
	 */
	public List getRegionsbyids(String ids);
}
