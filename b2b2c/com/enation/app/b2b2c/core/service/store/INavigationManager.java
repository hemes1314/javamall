package com.enation.app.b2b2c.core.service.store;

import java.util.List;

import com.enation.app.b2b2c.core.model.Navigation;

public interface INavigationManager {
	
	/**
	 * 查询导航列表
	 * @return
	 */
	List getNavicationList(Integer storeid);
	
	/**
	 * 添加店铺导航
	 * @param navigation
	 */
	void save(Navigation navigation);
	
	/**
	 * 修改店铺导航
	 * @param navigation
	 */
	void edit(Navigation navigation);
	
	/**
	 * 删除店铺导航
	 * @param id
	 */
	void delete(Integer id);
	
	/**
	 * 查询某个导航的信息
	 * @param id
	 * @return
	 */
	Navigation getNavication(Integer id);
}
