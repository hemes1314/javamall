package com.enation.app.flashbuy.component.plugin.act;

/**
 * 
 * @ClassName: IFlashBuyActEndEvent 
 * @Description: 限时抢购活动关闭事件 
 * @author TALON 
 * @date 2015-7-31 上午10:40:43 
 *
 */
public interface IFlashBuyActEndEvent {

	/**
	 * 
	 * @Title: onEndFlashBuyEnd
	 * @Description: 关闭限时抢购活动
	 * @param @param act_id 限时抢购活动Id
	 * @return void 
	 */
	public void onEndFlashBuyEnd(Integer act_id);
}
