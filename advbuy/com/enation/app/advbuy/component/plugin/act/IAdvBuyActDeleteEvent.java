package com.enation.app.advbuy.component.plugin.act;

/**
 * 
 * @ClassName: IAdvBuyActDeleteEvent 
 * @Description: 预售活动删除事件 
 * @author TALON 
 * @date 2015-7-31 上午10:40:26 
 *
 */
public interface IAdvBuyActDeleteEvent {
	/**
	 * 
	 * @Title: onDeleteAdvBuyAct
	 * @Description: 删除预售活动
	 * @param @param act_id 预售活动Id
	 * @return void 
	 */
	public void onDeleteAdvBuyAct(Integer act_id);
	
}
