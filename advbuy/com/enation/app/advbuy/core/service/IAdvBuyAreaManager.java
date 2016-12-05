package com.enation.app.advbuy.core.service;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.advbuy.core.model.AdvBuyArea;
import com.enation.framework.database.Page;

/**
 * 
 * @ClassName: IAdvBuyAreaManager 
 * @Description: 预售地区管理 
 * @author TALON 
 * @date 2015-7-31 上午1:43:11 
 *
 */
public interface IAdvBuyAreaManager {

	/**
	 * 获取地区列表
	 * @param pageNo 列表分页页码
	 * @param pageSize 每页显示数量
	 * @return Page 预售地区分页列表
	 */
	public Page list(int pageNo,int pageSize);
	
	
	/**
	 * 获取所有地区列表，不分页
	 * @return List<AdvBuyArea> 预售地区列表
	 */
	public List<AdvBuyArea> listAll();
	
	
	/**
	 * 获取某个预售地区
	 * @param areaid 预售地区Id
	 * @return AdvBuyArea 预售地区
	 */
	public AdvBuyArea get(int areaid);
	
	
	
	/**
	 * 添加预售地区
	 * @param advBuyArea 预售地区
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(AdvBuyArea advBuyArea);
	
	/**
	 * 更新预售地区
	 * @param advBuyArea 预售地区
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(AdvBuyArea advBuyArea);

	
	/**
	 * 删除预售地区
	 * @param areaid 预售地区ID数组
	 */
	public void delete(Integer[] areaid);
}
