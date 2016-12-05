package com.enation.app.secbuy.component.plugin.act;

/**
 * 
 * @ClassName: ISecBuyActEndEvent 
 * @Description: 秒拍活动关闭事件 
 * @author TALON 
 * @date 2015-7-31 上午10:40:43 
 *
 */
public interface ISecBuyActEndEvent {

	/**
	 * 
	 * @Title: onEndSecBuyEnd
	 * @Description: 关闭秒拍活动
	 * @param @param act_id 秒拍活动Id
	 * @return void 
	 */
	public void onEndSecBuyEnd(Integer act_id);
}
