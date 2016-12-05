package com.enation.app.b2b2c.component.plugin.member;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.plugin.member.IMemberLoginEvent;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
@Component
/**
 * b2b2c 会员登录向Session中传入StoreMember对象
 * @author LiFenLong
 *
 */
public class B2b2cMemberLoginPlugin extends AutoRegisterPlugin implements IMemberLoginEvent{
	private IStoreMemberManager storeMemberManager;
	@Override
	public void onLogin(Member member, Long upLogintime) {
		ThreadContextHolder.getSessionContext().setAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY, storeMemberManager.getMember(member.getMember_id()));
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
