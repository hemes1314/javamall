package com.enation.framework.jms;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * jms处理器
 * @author kingapex
 *
 */
public interface IJmsProcessor {
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void process(Object data);
	
	
}
