package com.enation.app.b2b2c.core.service.goods;

import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.store.StoreCat;
import com.enation.framework.database.Page;

/**
 * 店铺分类管理类
 * @author LiFenLong
 *
 */
public interface IStoreGoodsCatManager {
    /**
     * 店铺分类列表
     * @param page
     * @param pageSize
     * @return
     */
    public List storeCatList(Integer storeId);
 
	/**
	 * 添加店铺分类
	 * @param StoreCat
	 */
	public void addStoreCat(StoreCat storeCat);
	/**
	 * 修改店铺分类
	 * @param storeCat
	 */
	public void editStoreCat(StoreCat storeCat);
	/**
	 * 删除店铺分类
	 * @param storeCatId
	 */
	public void deleteStoreCat(Integer storeCatId,Integer storeid);
	
	/**
	 * 查询店铺顶级分类列表 不包括本身
	 * @return
	 */
	List getStoreCatList(Integer catid,Integer storeId);
	
	/**
	 * 根据store_cat_pid和sort判断该分类下是否已经有此排序
	 * 返回值是0，表示无排序，
	 * @param map
	 * @return
	 */
	public Integer getStoreCatNum(Integer store_id,Integer store_cat_pid,Integer sort);
	
	
	public StoreCat getStoreCat(Map map);
	
	public Integer is_children(Integer catid);
	
	public Integer childrenCount(Integer catid);
}
