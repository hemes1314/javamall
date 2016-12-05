package com.enation.framework.jms;

import com.enation.eop.sdk.context.EopContext;


/**
 * Jms消息
 * @author kingapex
 *
 */
public interface IEopJmsMessage {
	
	
	public Object getData();
	
	
	
	
	public String getProcessorBeanId();
	
	
	
	
 
	
	
	
	
	/**
	 * 获取EOP上下文
	 * @return
	 */
	public EopContext getEopContext();
	
}
