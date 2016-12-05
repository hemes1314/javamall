package com.enation.app.b2b2c.core.tag.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreBonusManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 订单结算时查询店铺优惠券
 * @author xulipeng
 *
 */
@Component
public class StoreBonusTag extends BaseFreeMarkerTag {

	private IStoreBonusManager storeBonusManager;
	private IStoreMemberManager storeMemberManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer store_id = (Integer) params.get("store_id");
		Double storeprice = (Double) params.get("storeprice");
		Member member = UserConext.getCurrentMember();
		List list = this.storeBonusManager.getMemberBonusList(member.getMember_id(), store_id,storeprice);
		return list == null ? new ArrayList() : list;
	}
	
	
	public IStoreBonusManager getStoreBonusManager() {
		return storeBonusManager;
	}
	public void setStoreBonusManager(IStoreBonusManager storeBonusManager) {
		this.storeBonusManager = storeBonusManager;
	}
	
	

}
