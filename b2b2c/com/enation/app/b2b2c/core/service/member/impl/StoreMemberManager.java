package com.enation.app.b2b2c.core.service.member.impl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.model.Member;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.context.webcontext.ThreadContextHolder;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Component
public class StoreMemberManager extends BaseSupport implements IStoreMemberManager{

	public void edit(StoreMember member) {
		this.baseDaoSupport.update("member", member, "member_id=" + member.getMember_id());
		ThreadContextHolder.getSessionContext().setAttribute(CURRENT_STORE_MEMBER_KEY, member);
	}

	@Override
	public StoreMember getMember(long member_id) {
		String sql="select * from es_member where member_id=?";
		return (StoreMember) this.daoSupport.queryForObject(sql, StoreMember.class, member_id);
	}

	@Override
	public StoreMember getStoreMember() {
		StoreMember member=(StoreMember) ThreadContextHolder.getSessionContext().getAttribute(CURRENT_STORE_MEMBER_KEY);
		if (member == null) {
			Member memberTemp = UserConext.getCurrentMember();
			if (memberTemp != null) {
				member = this.getMember(memberTemp.getMember_id());
				ThreadContextHolder.getSessionContext().setAttribute(CURRENT_STORE_MEMBER_KEY, member);
			}
		}
		return member;
	}

	@Override
	public StoreMember getMember(String member_name) {
		String sql="select * from es_member where uname=?";
		return (StoreMember) this.daoSupport.queryForObject(sql, StoreMember.class,member_name );
	}
}

