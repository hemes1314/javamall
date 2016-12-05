package com.enation.app.b2b2c.component.plugin.store;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
/**
 * 申请店铺后会员管理插件
 * @author LiFenLong
 *
 */
@Component
public class StoreAfterApplyPlugin extends AutoRegisterPlugin implements IAfterStoreApplyEvent{
	private IDaoSupport daoSupport;
	private IStoreMemberManager storeMemberManager;
	@Override
	/**
	 * 申请完店铺更改会员申请状态
	 * 并且刷新会员信息
	 */
	public void IAfterStoreApply(Store store) {
		String sql="update es_member set is_store=1,store_id=? where member_id=?";
		daoSupport.execute(sql, store.getStore_id(),store.getMember_id());
		ThreadContextHolder.getSessionContext().setAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY, storeMemberManager.getMember( store.getMember_id()));
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	
}
