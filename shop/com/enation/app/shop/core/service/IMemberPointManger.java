package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.shop.core.model.FreezePoint;

/**
 * 会员积分管理接口
 * @author kingapex
 *
 */
public interface IMemberPointManger {

	/**
	 * 获取积分项常量
	 */
	String TYPE_REGISTER= "register";
	String TYPE_EMIAL_CHECK="email_check";
	String TYPE_FINISH_PROFILE = "finish_profile";
	String TYPE_BUYGOODS ="buygoods";
	String TYPE_ONLINEPAY="onlinepay";
	String TYPE_LOGIN="login";
	String TYPE_COMMENT="comment";
	String TYPE_COMMENT_IMG="comment_img";
	String TYPE_FIRST_COMMENT = "first_comment";
	String TYPE_REGISTER_LINK="register_link";
	String TYPE_GOODS_POINT="goods_point";
	
	/**
	 * 解冻积分
	 * @param freezePoint
	 */
	public void thaw(FreezePoint freezePoint,boolean ismanual);
	
	
	/**
	 * 为某个订单解冻积分
	 * @param orderId
	 */
	public void thaw(Integer orderId);
	
	
	/**
	 * 读取由当前日期起多少天前的冻结积分
	 * @param beforeDayNum 天数
	 * @return 冻结积分列表
	 */
	public List<FreezePoint> listByBeforeDay(int beforeDayNum); 
	
	
	/**
	 * 增加某个会员的冻结积分 
	 * @param freezePoint
	 */
	public void addFreezePoint(FreezePoint freezePoint,String memberName);
	
	
	
	
	
	/**
	 * 查询某个会员的消费冻结积分 
	 * @param memberid
	 * @return
	 */
	public int getFreezeMpByMemberId(long memberid);
	
	
	
	/**
	 * 查询某个会员的登记冻结积分
	 * @param memberid
	 * @return
	 */
	public int getFreezePointByMemberId(long memberid);
	
	
	/**
	 * 检测某项是否获取积分
	 * @param itemname
	 * @return
	 */
	public boolean checkIsOpen(String itemname);
	
	/**
	 * 获取某项的获取积分值
	 * @param itemname
	 * @return
	 */
	public int getItemPoint(String itemname);
	
	/**
	 * 为会员增加积分
	 * @param point
	 * @param itemname
	 */
	public void add(long memberid,int point,String itemname,Integer relatedId, int mp);
	
	/**
	 * 根据订单 删除冻结积分
	 * @param orderId
	 */
	public void deleteByOrderId(Integer orderId);
	

	/**
	 * 使用消费积分 
	 * @param memberid
	 * @param point
	 * @param reson
	 * @param relatedId
	 */
	public void useMarketPoint(long memberid,int point,String reson,Integer relatedId);
	
	
	/**
	 * 得到积分价格
	 * @param point
	 * @return
	 */
	public Double pointToPrice(int point);
	
	
	/**
	 * 将价格兑换为积分
	 * @param price
	 * @return
	 */
	public int priceToPoint(Double price);
	
}
