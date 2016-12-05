package com.enation.framework.jms;




public class EmailProducer {
	
	private EopProducer eopProducer;
	
	public void send(EmailModel emailModel) {
		
		EopJmsMessage jmsMessage = new EopJmsMessage();
		jmsMessage.setData(emailModel);
		jmsMessage.setProcessorBeanId("emailProcessor");
		eopProducer.send(jmsMessage);
		
	}
	
	public EopProducer getEopProducer() {
		return eopProducer;
	}
	public void setEopProducer(EopProducer eopProducer) {
		this.eopProducer = eopProducer;
	}
	
	
	
}
