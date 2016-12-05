package com.enation.app.secbuy.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.secbuy.core.model.SecBuyActive;
import com.enation.framework.database.Page;

/**
 * 
 * @ClassName: ISecBuyActiveManager 
 * @Description: 秒拍活动管理类 
 * @author TALON 
 * @date 2015-7-31 上午1:38:39 
 *
 */
public interface ISecBuyActiveManager {
	/**
	 * 秒拍活动列表
	 * @param page 分页页码
	 * @param pageSize 分页每页显示数量
	 * @param map 搜索条件
	 * @return 秒拍活动分页列表
	 */
	public Page secBuyActive(Integer page,Integer pageSize,Map map);
	
	/**
	 * 添加秒拍活动
	 * @param secBuyActive 秒拍活动
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(SecBuyActive secBuyActive);
	
	/**
	 * 更新秒拍活动
	 * @param secBuyActive 秒拍活动
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(SecBuyActive secBuyActive);
	
	/**
	 * 批量删除秒拍活动 
	 * @param ids 秒拍活动Id
	 */
	public void delete(Integer[] ids);
	
	/**
	 * 删除某个秒拍活动
	 * @param id 秒拍活动Id
	 */
	public void delete(int id);
	/**
	 * 获取某个秒拍活动
	 * @param id 秒拍活动Id
	 * @return SecBuyActive 秒拍活动
	 */
	public SecBuyActive get(int id);
	/**
	 * 获取当前正在举办的秒拍活动
	 * @return SecBuyActive 秒拍活动
	 */
	public SecBuyActive get();
	/**
	 * 获取秒拍最后结束日期
	 * @return Long 秒拍活动最后结束时间
	 */
	public Long getLastEndTime();
	
	/**
	 * 读取可用的秒拍活动（未过期的）
	 * @return List<SecBuyActive> 秒拍活动列表（未过期）
	 */
	public List<SecBuyActive> listEnable();
	/**
	 * 读取可报名的秒拍活动
	 * @return List<SecBuyActive> 可报名秒拍活动列表
	 */
	public List<SecBuyActive> listJoinEnable();
}
