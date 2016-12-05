package com.enation.app.b2b2c.component.plugin.store;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.store.Store;

/**
 * 申请店铺后执行插件
 * @author LiFenLong
 *
 */
public interface IAfterStoreApplyEvent {

	@Transactional(propagation = Propagation.REQUIRED)  
	public void IAfterStoreApply(Store store);
}
