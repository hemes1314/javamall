package com.enation.app.b2b2c.core.tag.member;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreMemberAddressManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 读取当前会员收货地址列表
 * @author kingapex
 *2013-7-26下午2:48:02
 */
@Component
@Scope("prototype")
public class MemberAddressTag extends BaseFreeMarkerTag {
	private IMemberAddressManager memberAddressManager;
	private IStoreMemberAddressManager storeMemberAddressManager;
	private IStoreMemberManager storeMemberManager;
	
	/**
	 * 读取当前会员收货地址列表
	 * @param 无
	 * @return 收货地址列表
	 * {@link MemberAddress}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		StoreMember member=storeMemberManager.getStoreMember();
		if(member==null){
			throw new TemplateModelException("未登陆不能使用此标签[ConsigneeListTag]");
		}
		return memberAddressManager.listAddress();
	}
	
	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}
	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}

	public IStoreMemberAddressManager getStoreMemberAddressManager() {
		return storeMemberAddressManager;
	}

	public void setStoreMemberAddressManager(
			IStoreMemberAddressManager storeMemberAddressManager) {
		this.storeMemberAddressManager = storeMemberAddressManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}


}
