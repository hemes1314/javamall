package com.enation.app.b2b2cGroupbuy.core.service.impl;

import java.util.Map;

import com.enation.app.b2b2cGroupbuy.core.model.StoreGroupBuy;
import com.enation.framework.database.Page;

/**
 * 团购管理类
 * @author kingapex
 *2015-1-8下午6:08:24
 */
public interface IStoreGroupBuyManager {
	
	
	/**
	 * 搜索某店铺的团购
	 * @param page 页码
	 * @param pageSize  分页数
	 * @param storeid 店铺id
	 * @param params 其它参数
	 * @return 分页结果集
	 */
	public Page listByStoreId(int page,int pageSize,int storeid,Map params);
	/**
	 * 获取团购商品
	 * @param goodsId
	 * @return
	 */
	public StoreGroupBuy getBuyGoodsId(int goodsId,int act_id);
	/**
	 * 
	 * @Title: get
	 * @Description: 获取团购商品
	 * @param @param gbid
	 * @param @return
	 * @return StoreGroupBuy    返回类型
	 */
	public StoreGroupBuy get(int gbid);
}
