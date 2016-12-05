package com.enation.framework.jms;

import com.enation.eop.sdk.context.EopContext;


/**
 * 
 * @author kingapex
 *
 */  
public class EopJmsMessage implements IEopJmsMessage {

	private Object data;
	private String beanid;
	private EopContext context;
	
	/*
	 * @see EopConsumer
	 */
	public EopJmsMessage(){
		
		//使持有当前上下文，将来在EopConsumer处转为当前上下文
		context= EopContext.getContext();
	}
	
	@Override
	public Object getData() {
		return data;
	}

	@Override
	public String getProcessorBeanId() {
		return beanid;
	}
	
	public void setData(Object _data){
		data = _data;
	}
	
	public void setProcessorBeanId(String _beanid){
		this.beanid= _beanid;
	}
	
 

	@Override
	public EopContext getEopContext() {
		return context;
	}
}
