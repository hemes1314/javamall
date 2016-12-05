package com.enation.app.b2b2c.component.plugin.store;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
/**
 * 店铺通过审核添加店铺幻灯片
 * @author LiFenLong
 *
 */
@Component
public class StoreAddSildePlugin extends AutoRegisterPlugin implements IAfterStorePassEvent{
	private IDaoSupport daoSupport;
	@Override
	public void AfterStorePass(Store store) {
			Map map=new HashMap();
			for (int i = 0; i < 5; i++) {
				map.put("store_id", store.getStore_id());
				map.put("img","fs:/images/s_side.jpg");
				this.daoSupport.insert("es_store_silde",map);
			}
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
		
}

