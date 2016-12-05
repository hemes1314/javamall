package com.enation.app.flashbuy.component.plugin.act;

/**
 * 
 * @ClassName: IFlashBuyActStartEvent 
 * @Description: 限时抢购活动开启事件
 * @author TALON 
 * @date 2015-7-31 上午10:41:01 
 *
 */
public interface IFlashBuyActStartEvent {

	/**
	 * 
	 * @Title: onFlashBuyStart
	 * @Description: 开启限时抢购活动
	 * @param @param act_id 限时抢购活动Id
	 * @return void 
	 */
	public void onFlashBuyStart(Integer act_id);
}
