package com.enation.app.base.core.plugin.job;
/**
 * 任务每小时执行事件
 * @author Fenlongli
 *
 */
public interface IEveryMinutesExecuteEvent {
	/**
	 * 每隔一分钟会激发此事件 
	 */
	public void everyMinutes();
	
}
