package com.enation.app.b2b2c.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.StoreTemlplate;

/**
 * 运费模板接口
 * @author xulipeng
 *
 */
public interface IStoreTemplateManager {
	
	/**
	 * 添加模板
	 * @param storeTemlplate
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	Integer add(StoreTemlplate storeTemlplate);
	
	/**
	 * 取得模板list
	 * @param store_id
	 * @return
	 */
	List getTemplateList(Integer store_id);
	
	/**
	 * 最后的id
	 * @return
	 */
	Integer getLastId();
	
	/**
	 * 根据店铺id，模板id取模板
	 * @param store_id
	 * @param tempid
	 * @return
	 */
	Map getTemplae(Integer store_id,Integer tempid);
	
	/**
	 * 修改模板
	 * @param storeTemlplate
	 */
	void edit(StoreTemlplate storeTemlplate);
	
	/**
	 * 删除模板
	 * @param tempid
	 */
	void delete(Integer tempid);
	
	/**
	 * 取得默认模板
	 * @param storeid
	 * @return
	 */
	Integer getDefTempid(Integer storeid);
	
	/**
	 * 设置默认模板
	 * @param tempid
	 */
	void setDefTemp(Integer tempid,Integer storeid);
	
	/**
	 * 根据模板名称,当前登录的商家获取模板
	 * @param name
	 * @return
	 */
	Integer getStoreTemlpateByName(String name,Integer storeid);
	
	/**
	 * 检查当前模板是否为默认模板
	 * @param tempid
	 * @return
	 */
	int checkIsDef(Integer tempid);
}
