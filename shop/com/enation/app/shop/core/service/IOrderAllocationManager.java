package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.framework.database.Page;

public interface IOrderAllocationManager {
	
	/**
	 * 列出配货信息
	 * @param depotid
	 * @param status
	 * @return
	 */
	public List listAllocation(int orderid);
	
	public void clean(Integer[] ids);
}
