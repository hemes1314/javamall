package com.enation.app.advbuy.component.plugin.act;

import com.enation.app.advbuy.core.model.AdvBuyActive;


/**
 * 
 * @ClassName: IAdvBuyActAddEvent 
 * @Description: 添加预售活动事件
 * @author TALON 
 * @date 2015-7-31 上午10:38:36 
 *
 */
public interface IAdvBuyActAddEvent {
	/**
	 * 
	 * @Title: onAddAdvBuyAct
	 * @Description: 添加预售活动
	 * @param @param advBuyActive 预售活动
	 * @return void    
	 */
	public void onAddAdvBuyAct(AdvBuyActive advBuyActive);
}
