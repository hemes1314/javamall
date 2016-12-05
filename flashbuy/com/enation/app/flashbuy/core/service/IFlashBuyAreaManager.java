package com.enation.app.flashbuy.core.service;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.flashbuy.core.model.FlashBuyArea;
import com.enation.framework.database.Page;

/**
 * 
 * @ClassName: IFlashBuyAreaManager 
 * @Description: 限时抢购地区管理 
 * @author TALON 
 * @date 2015-7-31 上午1:43:11 
 *
 */
public interface IFlashBuyAreaManager {

	/**
	 * 获取地区列表
	 * @param pageNo 列表分页页码
	 * @param pageSize 每页显示数量
	 * @return Page 限时抢购地区分页列表
	 */
	public Page list(int pageNo,int pageSize);
	
	
	/**
	 * 获取所有地区列表，不分页
	 * @return List<FlashBuyArea> 限时抢购地区列表
	 */
	public List<FlashBuyArea> listAll();
	
	
	/**
	 * 获取某个限时抢购地区
	 * @param areaid 限时抢购地区Id
	 * @return FlashBuyArea 限时抢购地区
	 */
	public FlashBuyArea get(int areaid);
	
	
	
	/**
	 * 添加限时抢购地区
	 * @param flashBuyArea 限时抢购地区
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(FlashBuyArea flashBuyArea);
	
	/**
	 * 更新限时抢购地区
	 * @param flashBuyArea 限时抢购地区
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(FlashBuyArea flashBuyArea);

	
	/**
	 * 删除限时抢购地区
	 * @param areaid 限时抢购地区ID数组
	 */
	public void delete(Integer[] areaid);
}
