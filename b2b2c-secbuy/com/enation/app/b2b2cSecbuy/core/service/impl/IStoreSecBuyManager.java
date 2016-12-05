package com.enation.app.b2b2cSecbuy.core.service.impl;

import java.util.Map;

import com.enation.app.b2b2cSecbuy.core.model.StoreSecBuy;
import com.enation.framework.database.Page;

/**
 * 秒拍管理类
 * @author kingapex
 *2015-1-8下午6:08:24
 */
public interface IStoreSecBuyManager {
	
	
	/**
	 * 搜索某店铺的秒拍
	 * @param page 页码
	 * @param pageSize  分页数
	 * @param storeid 店铺id
	 * @param params 其它参数
	 * @return 分页结果集
	 */
	public Page listByStoreId(int page,int pageSize,int storeid,Map params);
	/**
	 * 获取秒拍商品
	 * @param goodsId
	 * @return
	 */
	public StoreSecBuy getBuyGoodsId(int goodsId,int act_id);
	/**
	 * 
	 * @Title: get
	 * @Description: 获取秒拍商品
	 * @param @param gbid
	 * @param @return
	 * @return StoreSecBuy    返回类型
	 */
	public StoreSecBuy get(int gbid);
}
