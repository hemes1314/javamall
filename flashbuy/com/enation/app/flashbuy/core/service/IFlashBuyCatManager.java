package com.enation.app.flashbuy.core.service;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.flashbuy.core.model.FlashBuyCat;
import com.enation.framework.database.Page;

/**
 * 
 * @ClassName: IFlashBuyCatManager 
 * @Description: 限时抢购分类管理 
 * @author TALON 
 * @date 2015-7-31 上午1:46:44 
 *
 */
public interface IFlashBuyCatManager {
	
	/**
	 * 获取限时抢购分类列表
	 * @param pageNo 页码
	 * @param pageSize 每页显示数量
	 * @return Page 限时抢购分类分页列表
	 */
	public Page list(int pageNo,int pageSize);
	
	
	/**
	 * 获取所有分类列表，不分页
	 * @return List<FlashBuyCat> 限时抢购分页列表
	 */
	public List<FlashBuyCat> listAll();
	
	
	/**
	 * 获取某个限时抢购
	 * @param catid 限时抢购分类Id
	 * @return FlashBuyCat 限时抢购分类
	 */
	public FlashBuyCat get(int catid);
	
	
	
	/**
	 * 添加限时抢购分类
	 * @param flashBuyCat 限时抢购分类
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(FlashBuyCat flashBuyCat);
	
	/**
	 * 更新限时抢购分类
	 * @param flashBuyCat 限时抢购分类
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(FlashBuyCat flashBuyCat);

	
	/**
	 * 删除限时抢购分类
	 * @param catid 限时抢购分类ID
	 */
	public void delete(Integer[] catid);
}
