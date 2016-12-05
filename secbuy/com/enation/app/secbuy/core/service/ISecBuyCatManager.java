package com.enation.app.secbuy.core.service;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.secbuy.core.model.SecBuyCat;
import com.enation.framework.database.Page;

/**
 * 
 * @ClassName: ISecBuyCatManager 
 * @Description: 秒拍分类管理 
 * @author TALON 
 * @date 2015-7-31 上午1:46:44 
 *
 */
public interface ISecBuyCatManager {
	
	/**
	 * 获取秒拍分类列表
	 * @param pageNo 页码
	 * @param pageSize 每页显示数量
	 * @return Page 秒拍分类分页列表
	 */
	public Page list(int pageNo,int pageSize);
	
	
	/**
	 * 获取所有分类列表，不分页
	 * @return List<SecBuyCat> 秒拍分页列表
	 */
	public List<SecBuyCat> listAll();
	
	
	/**
	 * 获取某个秒拍
	 * @param catid 秒拍分类Id
	 * @return SecBuyCat 秒拍分类
	 */
	public SecBuyCat get(int catid);
	
	
	
	/**
	 * 添加秒拍分类
	 * @param secBuyCat 秒拍分类
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(SecBuyCat secBuyCat);
	
	/**
	 * 更新秒拍分类
	 * @param secBuyCat 秒拍分类
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(SecBuyCat secBuyCat);

	
	/**
	 * 删除秒拍分类
	 * @param catid 秒拍分类ID
	 */
	public void delete(Integer[] catid);
}
