/**
 * 
 */
package com.enation.app.shop.component.pagecreator.service;

/**
 * 页面生成管理接口
 * @author kingapex
 *2015-3-26
 */
public interface IPageCreateManager {
	
	/**
	 * 生成静态页
	 * @return 
	 * 真：正常下达生成任务<br>
	 * 假：有任务正在生成，此次生成任务未能下达
	 */
	public boolean startCreate(String[] choose_pages);
	
	
}
