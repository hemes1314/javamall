package com.enation.app.secbuy.component.plugin.act;

/**
 * 
 * @ClassName: ISecBuyActDeleteEvent 
 * @Description: 秒拍活动删除事件 
 * @author TALON 
 * @date 2015-7-31 上午10:40:26 
 *
 */
public interface ISecBuyActDeleteEvent {
	/**
	 * 
	 * @Title: onDeleteSecBuyAct
	 * @Description: 删除秒拍活动
	 * @param @param act_id 秒拍活动Id
	 * @return void 
	 */
	public void onDeleteSecBuyAct(Integer act_id);
	
}
