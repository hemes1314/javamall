package com.enation.app.shop.core.service.promotion;

import com.enation.app.shop.core.model.Promotion;


/**
 * 翻倍积分优惠方式
 * @author kingapex
 *2010-4-25下午10:31:02
 */
public interface ITimesPointBehavior extends IPromotionBehavior {
	
	public Integer countPoint(Promotion promotion,Integer point);
	
}
