package com.enation.app.b2b2c.component.plugin.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.plugin.member.IMemberRegisterEvent;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
@Component
/**
 * 会员注册后标示为未申请店铺
 * @author LiFenLong
 *
 */
public class B2b2cMemberRegisterPlugin extends AutoRegisterPlugin implements IMemberRegisterEvent{
	private IDaoSupport daoSupport;
	private IStoreMemberManager storeMemberManager;
	@Override
	public void onRegister(Member member) {
		Map map=new HashMap();
		map.put("is_store", 0);
		daoSupport.update("es_member",map , "member_id="+member.getMember_id());
		//登陆店铺会员
		ThreadContextHolder.getSessionContext().setAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY, storeMemberManager.getMember(member.getMember_id()));
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
