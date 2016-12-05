package com.enation.app.b2b2c.core.service.goods;

import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.goods.StoreTag;
import com.enation.framework.database.Page;

/**
 * 店铺商品标签管理类
 * @author LiFenLong
 *
 */
public interface IStoreGoodsTagManager {
	/**
	 * 获取标签列表
	 * @param store_id
	 * @return
	 */
	public List list(Integer store_id);
	/**
	 * 添加店铺商品标签
	 * @param tag
	 */
	public void add(StoreTag tag);
	/**
	 * 删除店铺商品标签引用
	 * @param tagId
	 * @param reg_id
	 */
	public void deleteRel(Integer tagId,Integer[] reg_id);
	/**
	 * 添加标签引用
	 * @param tagId
	 * @param reg_id
	 */
	public void addRels(Integer tagId,Integer[] reg_id);
	/**
	 * 保存排序
	 * @param tagId
	 * @param reg_id
	 * @param ordernum
	 */
	public void saveSort(Integer tagId,Integer[] reg_id,Integer[] ordernum);
	/**
	 * 获取标签商品列表
	 * @param tagid
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page getGoodsList(int tagid, int page, int pageSize);
	
	public Page getGoodsList(Map map, int page, int pageSize);
}
