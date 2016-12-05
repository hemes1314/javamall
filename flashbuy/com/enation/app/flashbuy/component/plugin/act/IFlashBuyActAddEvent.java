package com.enation.app.flashbuy.component.plugin.act;

import com.enation.app.flashbuy.core.model.FlashBuyActive;


/**
 * 
 * @ClassName: IFlashBuyActAddEvent 
 * @Description: 添加限时抢购活动事件
 * @author humaodong 
 * @date 2015-7-31 上午10:38:36 
 *
 */
public interface IFlashBuyActAddEvent {
	/**
	 * 
	 * @Title: onAddFlashBuyAct
	 * @Description: 添加限时抢购活动
	 * @param @param flashBuyActive 限时抢购活动
	 * @return void    
	 */
	public void onAddFlashBuyAct(FlashBuyActive flashBuyActive);
}
