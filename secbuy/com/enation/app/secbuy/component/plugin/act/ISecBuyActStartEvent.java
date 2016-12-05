package com.enation.app.secbuy.component.plugin.act;

/**
 * 
 * @ClassName: ISecBuyActStartEvent 
 * @Description: 秒拍活动开启事件
 * @author TALON 
 * @date 2015-7-31 上午10:41:01 
 *
 */
public interface ISecBuyActStartEvent {

	/**
	 * 
	 * @Title: onSecBuyStart
	 * @Description: 开启秒拍活动
	 * @param @param act_id 秒拍活动Id
	 * @return void 
	 */
	public void onSecBuyStart(Integer act_id);
}
