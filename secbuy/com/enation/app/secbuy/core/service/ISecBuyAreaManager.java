package com.enation.app.secbuy.core.service;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.secbuy.core.model.SecBuyArea;
import com.enation.framework.database.Page;

/**
 * 
 * @ClassName: ISecBuyAreaManager 
 * @Description: 秒拍地区管理 
 * @author TALON 
 * @date 2015-7-31 上午1:43:11 
 *
 */
public interface ISecBuyAreaManager {

	/**
	 * 获取地区列表
	 * @param pageNo 列表分页页码
	 * @param pageSize 每页显示数量
	 * @return Page 秒拍地区分页列表
	 */
	public Page list(int pageNo,int pageSize);
	
	
	/**
	 * 获取所有地区列表，不分页
	 * @return List<SecBuyArea> 秒拍地区列表
	 */
	public List<SecBuyArea> listAll();
	
	
	/**
	 * 获取某个秒拍地区
	 * @param areaid 秒拍地区Id
	 * @return SecBuyArea 秒拍地区
	 */
	public SecBuyArea get(int areaid);
	
	
	
	/**
	 * 添加秒拍地区
	 * @param secBuyArea 秒拍地区
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(SecBuyArea secBuyArea);
	
	/**
	 * 更新秒拍地区
	 * @param secBuyArea 秒拍地区
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(SecBuyArea secBuyArea);

	
	/**
	 * 删除秒拍地区
	 * @param areaid 秒拍地区ID数组
	 */
	public void delete(Integer[] areaid);
}
