/**
 * 
 */
package com.enation.app.shop.component.goodsindex.service;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.component.goodsindex.model.GoodsWords;
import com.enation.app.shop.core.plugin.search.SearchSelector;
import com.enation.framework.database.Page;

/**
 * 商品索引管理接口
 * @author kingapex
 *2015-4-16
 */
public interface IGoodsIndexManager {
	
	/**
	 * 将某个商品加入索引<br>
	 * @param goods
	 */
	public void addIndex(Map goods);
	
	/**
	 * 更新某个商品的索引
	 * @param goods
	 */
	public void updateIndex(Map goods);
	
	
	/**
	 * 更新
	 * @param goods
	 */
	public void deleteIndex(Map goods);
	
	
	/**
	 * 将所有商品加入索引
	 */
	public void addAallIndex();
	
	
	/**
	 * 通过关键字获取商品分词索引
	 * @param keyword
	 * @return
	 */
	public List<GoodsWords> getGoodsWords(String keyword);
	
	
	
	/**
	 * 商品搜索
	 * @param pageNo 页码
	 * @param pageSize 分页大小
	 * @return
	 */
	public Page search(int pageNo,int pageSize);
	
	
	
	/**
	 * 生成搜索的选择器
	 * @return key为selected为是已经选中的选择器
	 */
	public Map<String,Object> createSelector();
	
	
 
	
}
