package com.enation.app.b2b2cAdvbuy.core.service.impl;

import java.util.Map;

import com.enation.app.b2b2cAdvbuy.core.model.StoreAdvBuy;
import com.enation.framework.database.Page;

/**
 * 预售管理类
 * @author kingapex
 *2015-1-8下午6:08:24
 */
public interface IStoreAdvBuyManager {
	
	
	/**
	 * 搜索某店铺的预售
	 * @param page 页码
	 * @param pageSize  分页数
	 * @param storeid 店铺id
	 * @param params 其它参数
	 * @return 分页结果集
	 */
	public Page listByStoreId(int page,int pageSize,int storeid,Map params);
	/**
	 * 获取预售商品
	 * @param goodsId
	 * @return
	 */
	public StoreAdvBuy getBuyGoodsId(int goodsId,int act_id);
	/**
	 * 
	 * @Title: get
	 * @Description: 获取预售商品
	 * @param @param gbid
	 * @param @return
	 * @return StoreAdvBuy    返回类型
	 */
	public StoreAdvBuy get(int gbid);
}
