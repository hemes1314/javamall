package com.enation.app.shop.core.service.impl;


import com.enation.app.shop.core.model.StoreLog;
import com.enation.app.shop.core.service.IStoreLogManager;
import com.enation.eop.sdk.database.BaseSupport;

/**
 * @author kingapex
 *2012-3-30上午9:13:44
 */
public class StoreLogManager extends BaseSupport<StoreLog> implements IStoreLogManager {

	@Override
	public void add(StoreLog storeLog) {
		this.baseDaoSupport.insert("store_log", storeLog);

	}

}
