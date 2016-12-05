package com.enation.app.advbuy.component.plugin.act;

/**
 * 
 * @ClassName: IAdvBuyActStartEvent 
 * @Description: 预售活动开启事件
 * @author TALON 
 * @date 2015-7-31 上午10:41:01 
 *
 */
public interface IAdvBuyActStartEvent {

	/**
	 * 
	 * @Title: onAdvBuyStart
	 * @Description: 开启预售活动
	 * @param @param act_id 预售活动Id
	 * @return void 
	 */
	public void onAdvBuyStart(Integer act_id);
}
