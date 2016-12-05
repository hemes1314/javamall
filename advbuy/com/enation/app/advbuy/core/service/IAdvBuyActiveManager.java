package com.enation.app.advbuy.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.advbuy.core.model.AdvBuyActive;
import com.enation.framework.database.Page;

/**
 * 
 * @ClassName: IAdvBuyActiveManager 
 * @Description: 预售活动管理类 
 * @author TALON 
 * @date 2015-7-31 上午1:38:39 
 *
 */
public interface IAdvBuyActiveManager {
	/**
	 * 预售活动列表
	 * @param page 分页页码
	 * @param pageSize 分页每页显示数量
	 * @param map 搜索条件
	 * @return 预售活动分页列表
	 */
	public Page advBuyActive(Integer page,Integer pageSize,Map map);
	
	/**
	 * 添加预售活动
	 * @param advBuyActive 预售活动
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(AdvBuyActive advBuyActive);
	
	/**
	 * 更新预售活动
	 * @param advBuyActive 预售活动
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(AdvBuyActive advBuyActive);
	
	/**
	 * 批量删除预售活动 
	 * @param ids 预售活动Id
	 */
	public void delete(Integer[] ids);
	
	/**
	 * 删除某个预售活动
	 * @param id 预售活动Id
	 */
	public void delete(int id);
	/**
	 * 获取某个预售活动
	 * @param id 预售活动Id
	 * @return AdvBuyActive 预售活动
	 */
	public AdvBuyActive get(int id);
	/**
	 * 获取当前正在举办的预售活动
	 * @return AdvBuyActive 预售活动
	 */
	public AdvBuyActive get();
	/**
	 * 获取预售最后结束日期
	 * @return Long 预售活动最后结束时间
	 */
	public Long getLastEndTime();
	
	/**
	 * 读取可用的预售活动（未过期的）
	 * @return List<AdvBuyActive> 预售活动列表（未过期）
	 */
	public List<AdvBuyActive> listEnable();
	/**
	 * 读取可报名的预售活动
	 * @return List<AdvBuyActive> 可报名预售活动列表
	 */
	public List<AdvBuyActive> listJoinEnable();
}
