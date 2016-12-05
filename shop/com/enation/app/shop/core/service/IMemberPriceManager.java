package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.shop.core.model.GoodsLvPrice;

/**
 * 会员价格管理接口
 * @author kingapex
 *
 */
public interface IMemberPriceManager {
	
	/**
	 * 添加会员价格
	 * @param goodsPrice
	 */
	public void save(List<GoodsLvPrice> goodsPrice);
	
	
	/**
	 * 读取某个商品的所有规格的会员价格。
	 * @param goodsid
	 * @return
	 */
	public List<GoodsLvPrice> listPriceByGid(int goodsid);
	
	
	/**
	 * 读取某个货品的会员价格
	 * @param productid
	 * @return
	 */
	public List<GoodsLvPrice> listPriceByPid(int productid);
	
	
	
	/**
	 * 读取某会员级别的商品价格
	 * @param lvid
	 * @return
	 */
	public List<GoodsLvPrice> listPriceByLvid(int lvid);
}
