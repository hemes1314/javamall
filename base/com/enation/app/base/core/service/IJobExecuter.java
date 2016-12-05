package com.enation.app.base.core.service;

/**
 * 任务执行器 
 * @author kingapex
 *
 */
public interface IJobExecuter {
	
	public void everyHour();
	public void everyDay();
	public void everyMonth();
	public void everyMinutes();
	
    /**
     * 每月15号的23点59分59秒执行task
     * 
     * @author chenzhongwei
     */
    public void everyMonthFifteen();
	
	
}
