package com.enation.app.flashbuy.component.plugin.act;

/**
 * 
 * @ClassName: IFlashBuyActDeleteEvent 
 * @Description: 限时抢购活动删除事件 
 * @author humaodong 
 * @date 2015-7-31 上午10:40:26 
 *
 */
public interface IFlashBuyActDeleteEvent {
	/**
	 * 
	 * @Title: onDeleteFlashBuyAct
	 * @Description: 删除限时抢购活动
	 * @param @param act_id 限时抢购活动Id
	 * @return void 
	 */
	public void onDeleteFlashBuyAct(Integer act_id);
	
}
