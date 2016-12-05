package com.enation.app.b2b2c.core.tag.store;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreMemberAddressManager;
import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 查询默认会员地址
 * @author xulipeng
 * 2014年12月13日16:33:53
 *
 */
@Component
public class MemberAdressRegionTag extends BaseFreeMarkerTag {

	private IStoreMemberManager storeMemberManager;
	private IStoreMemberAddressManager storeMemberAddressManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
	    
	    MemberAddress memberAddress =  StoreCartContainer.getUserSelectedAddress();
		StoreMember storeMember = storeMemberManager.getStoreMember();
		Integer regionId = 0;
		if(memberAddress == null)
		{
			if (storeMember != null) {
				regionId = storeMemberAddressManager.getRegionid(storeMember.getMember_id());
			}
		}else
		{
			regionId = memberAddress.getRegion_id();
		}

		return regionId == null ? 0 : regionId;
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
