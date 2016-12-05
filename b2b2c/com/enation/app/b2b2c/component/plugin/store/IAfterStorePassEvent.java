package com.enation.app.b2b2c.component.plugin.store;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.store.Store;

/**
 * 店铺通过后执行插件
 * @author LiFenLong
 *
 */
public interface IAfterStorePassEvent {
	@Transactional(propagation = Propagation.REQUIRED)  
	public void AfterStorePass(Store store);
}
