package com.enation.app.b2b2c.component.plugin.member;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.plugin.member.IMemberLogoutEvent;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
/**
 * b2b2c店铺会员退出插件
 * @author lina
 *
 */
@Component
public class B2b2cMemberLogoutPlugin extends AutoRegisterPlugin implements IMemberLogoutEvent{

	@Override
	public void onLogout(Member member) {
		ThreadContextHolder.getSessionContext().removeAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY);		
	}

}
