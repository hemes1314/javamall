package com.enation.app.advbuy.core.service;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.advbuy.core.model.AdvBuyCat;
import com.enation.framework.database.Page;

/**
 * 
 * @ClassName: IAdvBuyCatManager 
 * @Description: 预售分类管理 
 * @author TALON 
 * @date 2015-7-31 上午1:46:44 
 *
 */
public interface IAdvBuyCatManager {
	
	/**
	 * 获取预售分类列表
	 * @param pageNo 页码
	 * @param pageSize 每页显示数量
	 * @return Page 预售分类分页列表
	 */
	public Page list(int pageNo,int pageSize);
	
	
	/**
	 * 获取所有分类列表，不分页
	 * @return List<AdvBuyCat> 预售分页列表
	 */
	public List<AdvBuyCat> listAll();
	
	
	/**
	 * 获取某个预售
	 * @param catid 预售分类Id
	 * @return AdvBuyCat 预售分类
	 */
	public AdvBuyCat get(int catid);
	
	
	
	/**
	 * 添加预售分类
	 * @param advBuyCat 预售分类
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(AdvBuyCat advBuyCat);
	
	/**
	 * 更新预售分类
	 * @param advBuyCat 预售分类
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(AdvBuyCat advBuyCat);

	
	/**
	 * 删除预售分类
	 * @param catid 预售分类ID
	 */
	public void delete(Integer[] catid);
}
