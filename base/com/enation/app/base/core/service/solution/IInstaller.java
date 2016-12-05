package com.enation.app.base.core.service.solution;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Node;

/**
 * 安装器接口
 * @author kingapex
 * 2010-1-20下午04:57:05
 */
public interface IInstaller {
	
	/**
	 * 根据一相应的置文件片段进行安装某product
	 * @param fragment
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void install(String productId,Node fragment);

	
	 
}
