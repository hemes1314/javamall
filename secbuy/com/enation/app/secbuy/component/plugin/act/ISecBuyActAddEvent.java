package com.enation.app.secbuy.component.plugin.act;

import com.enation.app.secbuy.core.model.SecBuyActive;


/**
 * 
 * @ClassName: ISecBuyActAddEvent 
 * @Description: 添加秒拍活动事件
 * @author TALON 
 * @date 2015-7-31 上午10:38:36 
 *
 */
public interface ISecBuyActAddEvent {
	/**
	 * 
	 * @Title: onAddSecBuyAct
	 * @Description: 添加秒拍活动
	 * @param @param secBuyActive 秒拍活动
	 * @return void    
	 */
	public void onAddSecBuyAct(SecBuyActive secBuyActive);
}
