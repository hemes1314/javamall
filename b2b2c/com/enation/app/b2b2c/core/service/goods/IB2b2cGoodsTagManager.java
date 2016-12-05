package com.enation.app.b2b2c.core.service.goods;

import java.util.Map;

import com.enation.framework.database.Page;

/**
 * 商品标签管理类
 * @author lina
 *
 */
public interface IB2b2cGoodsTagManager {
	/**
	 * 标签商品列表
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page list(int pageNo, int pageSize);
	/**
	 * 获取团购标签商品列表
	 * @param tagid 标签Id
	 * @param page 分页
	 * @param pageSize 每页显示数量
	 * @param order 顺序
	 * @param sort 排序条件
	 * @return
	 */
	public Page groupBuyList(int tagid,Map goodsMap,int page,int pageSize,String sort,String order);
}
