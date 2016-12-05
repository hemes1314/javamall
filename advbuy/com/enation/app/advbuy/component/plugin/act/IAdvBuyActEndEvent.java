package com.enation.app.advbuy.component.plugin.act;

/**
 * 
 * @ClassName: IAdvBuyActEndEvent 
 * @Description: 预售活动关闭事件 
 * @author TALON 
 * @date 2015-7-31 上午10:40:43 
 *
 */
public interface IAdvBuyActEndEvent {

	/**
	 * 
	 * @Title: onEndAdvBuyEnd
	 * @Description: 关闭预售活动
	 * @param @param act_id 预售活动Id
	 * @return void 
	 */
	public void onEndAdvBuyEnd(Integer act_id);
}
