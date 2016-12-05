package com.enation.app.b2b2c.core.service.goods;

import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.goods.StoreGoods;
import com.enation.framework.database.Page;
public interface IStoreGoodsManager {
    /**
     * 获取店铺商品列表
     * @param pageNo
     * @param pageSize
     * @param map
     * @return
     */
    public Page storeGoodsList(Integer pageNo,Integer pageSize,Map map);
    
    /**
     * 获取店铺商品列表
     * @param pageNo
     * @param pageSize
     * @param map
     * @return
     */
    public Page storeGoodsListDels(Integer pageNo,Integer pageSize,Map map);
    
	
	/**
	 * 搜索某店铺的商品，不分页
	 * @author kingapex 
	 * add by 2015-01-07
	 * @param storeid 店铺id
	 * @param map 其它搜索参数:
	 *  store_catid:店铺分类 
	 *  keyword:关键字
	 * @return 商品列表
	 */
	public List<Map>  storeGoodsList(int storeid,Map map);
	
	
	
	/**
	 * 获取b2b2c商品列表
	 * @param pageNo
	 * @param pageSize
	 * @param keyword
	 * @return
	 */
	public Page b2b2cGoodsList(Integer pageNo,Integer pageSize,Map map);

	
	/**
	 * 店铺商品列表
	 * @param page
	 * @param pageSize
	 * @param map
	 * @return
	 */
	public Page store_searchGoodsList(Integer page,Integer pageSize,Map map);

	/***
	 * 商品成功记录
	 * @param pageNo
	 * @param pageSize
	 * @param goods_id
	 * @return
	 */
	public List transactionList(Integer pageNo,Integer pageSize,Integer goods_id);
	/**
	 * 记录个数
	 * @param goods_id
	 * @return
	 */
	public int transactionCount(Integer goods_id);
	
	/**
	 * 获取商品信息
	 * @param goods_id
	 * @return
	 */
	public StoreGoods getGoods(Integer goods_id);

	/**
	 * 保存库存
	 * @param goods_id
	 * @param storeNum
	 * @return
	 */
	public void saveGoodsStore(Integer storeid,Integer goods_id, Integer storeNum);
	
	/**
	  * 保存规格商品库存
	 * @param goods_id
	 * @param storeNum
	 * @param storeid
	 * @return
	 */
	public void saveGoodsSpecStore(Integer[] store_id,Integer goods_id, Integer[] storeNum,Integer[] product_id);
	
	/**
	 * 根据商品状态获取店铺商品数量
	 * @param struts
	 * @author LiFenLong
	 * @return
	 */
	public int getStoreGoodsNum(int struts);
	/**
	 * 获取商品库存信息
	 */
	public Map getGoodsStore(Integer goods_id);
	
	/**
	 * 获取带有规格的商品的库存信息
	 * @param goods_id
	 * @return
	 */
	public List getGoodsSpecStore(Integer goods_id);
	
	public void addStoreGoodsComment(Integer goods_id);
}
