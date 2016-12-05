package com.enation.app.shop.core.service.promotion;

/**
 * 优惠行为-送优惠券
 * @author kingapex
 *2010-4-15下午04:49:32
 */
public interface IGiveCouponBehavior  extends IPromotionBehavior{
	
	/**
	 * 送出优惠券
	 */
	public void giveCoupon();
	
	
}
