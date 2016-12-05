package com.enation.app.shop.core.service;

import com.enation.app.shop.core.model.MemberOrderItem;
import com.enation.framework.database.Page;

public interface IMemberOrderItemManager {

	/**
	 * 添加记录
	 * @param memberOrderItem
	 */
	public void add(MemberOrderItem memberOrderItem);
	
	/**
	 * 根据条件查询个数
	 * @param member_id
	 * @param goods_id
	 * @return
	 */
	public int count(long member_id, int goods_id,int commented);
	
	/**
	 * 根据条件查询个数
	 * @param member_id
	 * @param goods_id
	 * @return
	 */
	public int count(long member_id, int goods_id);
	
	/**
	 * 查询一条记录
	 * @param member_id
	 * @param goods_id
	 * @param commented
	 * @return
	 */
	public MemberOrderItem get(long member_id, int goods_id,int commented);
	
	/**
	 * 更新记录
	 * @param memberOrderItem
	 */
	public void update(MemberOrderItem memberOrderItem);
	
	/**
	 * 获取一个会员的待评论或已经评论过的商品列表
	 * @param member_id
	 * @param commented
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page getGoodsList(long member_id,int commented, int pageNo, int pageSize);
	/**
     * 获取一个会员的待评论或已经评论过的商品列表 For App
     * @param member_id
     * @param commented
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Page getGoodsListForApp(long member_id, int commented, int pageNo, int pageSize);

   
}
