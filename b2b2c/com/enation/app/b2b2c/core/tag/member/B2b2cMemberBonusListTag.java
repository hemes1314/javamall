package com.enation.app.b2b2c.core.tag.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreBonusManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 多用户版查询会员优惠券集合
 * @author xulipeng
 *
 */
@Component
public class B2b2cMemberBonusListTag extends BaseFreeMarkerTag {

	
	private IStoreMemberManager storeMemberManager;
	private IStoreBonusManager storeBonusManager;
	
	@SuppressWarnings("unchecked")
	@Override
	protected Object exec(Map param) throws TemplateModelException {
		StoreMember member= this.storeMemberManager.getStoreMember();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		Map result = new HashMap();
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		int pageSize = 1000;
		String store = request.getParameter("store");
		boolean isStore = store != null && store.equals("1");
	
		Page webpage = this.storeBonusManager.getBonusListBymemberid(Integer.valueOf(page), pageSize, member.getMember_id(), isStore);
		Long totalCount = webpage.getTotalCount();
		
		List bonusList = (List) webpage.getResult();
		bonusList = bonusList == null ? new ArrayList() : bonusList;

		result.put("totalCount", totalCount);
		result.put("pageSize", pageSize);
		result.put("page", page);
		result.put("bonusList", bonusList);
		result.put("store",store);
		
		return result;
	}
	
	

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}



	public IStoreBonusManager getStoreBonusManager() {
		return storeBonusManager;
	}



	public void setStoreBonusManager(IStoreBonusManager storeBonusManager) {
		this.storeBonusManager = storeBonusManager;
	}

}
