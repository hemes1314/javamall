package com.enation.app.shop.core.tag.member;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
public class MemberLvListTag extends BaseFreeMarkerTag {

	private IMemberLvManager memberLvManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		List list = this.memberLvManager.list();
		return list;
	}
	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}
	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

}
