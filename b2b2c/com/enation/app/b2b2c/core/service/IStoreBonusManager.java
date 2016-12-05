package com.enation.app.b2b2c.core.service;

import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.StoreBonus;
import com.enation.framework.database.Page;

@SuppressWarnings("rawtypes")
public interface IStoreBonusManager {
	/**
	 * 查询店铺优惠券类表
	 * @return
	 */
	List getBonusList(Integer store_id);
	
	
	/**
	 * 获取当前登录会员的优惠券
	 * @param memberid	当前登录会员的id
	 * @param store_id	店铺id
	 * @param min_goods_amount 最小订单金额
	 * @return
	 */
	public List<Map> getMemberBonusList(long memberid,Integer store_id,Double min_goods_amount);
	
	/**
	 * 根据id查询
	 * @param bonusid
	 * @return
	 */
	public StoreBonus get(Integer bonusid);
	
	/**
	 * 根据条件搜索优惠券    whj 2015-06-01
	 */
	
	public Page getConditionBonusList(Integer pageNo,Integer pageSize,Integer store_id,Map map);
	
	/**
	 * 查询会员的所有优惠券
	 * @param memberid
	 * @return
	 */
	public Page getBonusListBymemberid(int pageNo,int pageSize,long memberid, boolean isStore);
	
	/**
	 * 设置已使用
	 */
	public void setBonusUsed(Integer bonus_id,long member_id);
	
	
}
