package com.enation.app.b2b2c.core.tag.member;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 会员地址
 * @author xulipeng
 * whj 0819 09：48修改如下：
 * 如果无默认地址，则返回一个String型 “0”。
 */

@Component
public class SessionConsigneeTag extends BaseFreeMarkerTag{

	private IMemberAddressManager memberAddressManager;
	
	@SuppressWarnings({ "rawtypes" })
	@Override
	protected Object exec(Map params) throws TemplateModelException { 
		return StoreCartContainer.getUserSelectedAddress();
	}

	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}

	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}

}
