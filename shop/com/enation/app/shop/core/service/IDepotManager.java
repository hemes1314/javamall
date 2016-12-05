package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.shop.core.model.Depot;


/**
 * 库房管理接口
 * @author kingapex
 *
 */
public interface IDepotManager {
	
	/**
	 * 添加库房 
	 * @param room 库房
	 */
	public void add(Depot room );
	
	/**
	 * 修改库房
	 * @param room 库房
	 */
	public void update(Depot room );
	
	
	
	/**
	 * 获取库房
	 * @param roomid 库房ID
	 * @return
	 */
	public Depot get(int roomid);
	
	/**
	 * 删除库房
	 * @param roomid 库房Id
	 * @return
	 */
	public String delete(int roomid);
	
	/**
	 * 读取所的库房列表
	 * @return
	 */
	public List<Depot> list();
}
