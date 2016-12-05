package com.enation.app.b2b2cFlashbuy.core.service.impl;

import java.util.Map;

import com.enation.app.b2b2cFlashbuy.core.model.StoreFlashBuy;
import com.enation.framework.database.Page;

/**
 * 限时抢购管理类
 * @author humaodong
 *2015-1-8下午6:08:24
 */
public interface IStoreFlashBuyManager {
	
	
	/**
	 * 搜索某店铺的限时抢购
	 * @param page 页码
	 * @param pageSize  分页数
	 * @param storeid 店铺id
	 * @param params 其它参数
	 * @return 分页结果集
	 */
	public Page listByStoreId(int page,int pageSize,int storeid,Map params);
	/**
	 * 获取限时抢购商品
	 * @param goodsId
	 * @return
	 */
	public StoreFlashBuy getBuyGoodsId(int goodsId,int act_id);
	/**
	 * 
	 * @Title: get
	 * @Description: 获取限时抢购商品
	 * @param @param gbid
	 * @param @return
	 * @return StoreFlashBuy    返回类型
	 */
	public StoreFlashBuy get(int gbid);
}
