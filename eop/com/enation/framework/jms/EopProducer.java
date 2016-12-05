package com.enation.framework.jms;

import javax.jms.Queue;

import org.springframework.jms.core.JmsTemplate;

public class EopProducer {

	private JmsTemplate template;

	private Queue destination;

	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	public void setDestination(Queue destination) {
		this.destination = destination;
	}

	public void send(IEopJmsMessage eopJmsMessage) {
		
		if(eopJmsMessage instanceof ITaskView){
			TaskContainer.pushTask((ITaskView) eopJmsMessage);
		}
		
		template.convertAndSend(this.destination, eopJmsMessage);
		
	}
}
