package com.enation.app.flashbuy.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.flashbuy.core.model.FlashBuyActive;
import com.enation.framework.database.Page;

/**
 * 
 * @ClassName: IFlashBuyActiveManager 
 * @Description: 限时抢购活动管理类 
 * @author TALON 
 * @date 2015-7-31 上午1:38:39 
 *
 */
public interface IFlashBuyActiveManager {
	/**
	 * 限时抢购活动列表
	 * @param page 分页页码
	 * @param pageSize 分页每页显示数量
	 * @param map 搜索条件
	 * @return 限时抢购活动分页列表
	 */
	public Page flashBuyActive(Integer page,Integer pageSize,Map map);
	
	/**
	 * 添加限时抢购活动
	 * @param flashBuyActive 限时抢购活动
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(FlashBuyActive flashBuyActive);
	
	/**
	 * 更新限时抢购活动
	 * @param flashBuyActive 限时抢购活动
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(FlashBuyActive flashBuyActive);
	
	/**
	 * 批量删除限时抢购活动 
	 * @param ids 限时抢购活动Id
	 */
	public void delete(Integer[] ids);
	
	/**
	 * 删除某个限时抢购活动
	 * @param id 限时抢购活动Id
	 */
	public void delete(int id);
	/**
	 * 获取某个限时抢购活动
	 * @param id 限时抢购活动Id
	 * @return FlashBuyActive 限时抢购活动
	 */
	public FlashBuyActive get(int id);
	/**
	 * 获取当前正在举办的限时抢购活动
	 * @return FlashBuyActive 限时抢购活动
	 */
	public FlashBuyActive get();
	/**
	 * 获取限时抢购最后结束日期
	 * @return Long 限时抢购活动最后结束时间
	 */
	public Long getLastEndTime();
	
	/**
	 * 读取可用的限时抢购活动（未过期的）
	 * @return List<FlashBuyActive> 限时抢购活动列表（未过期）
	 */
	public List<FlashBuyActive> listEnable();
	/**
	 * 读取可报名的限时抢购活动
	 * @return List<FlashBuyActive> 可报名限时抢购活动列表
	 */
	public List<FlashBuyActive> listJoinEnable();
}
