package com.enation.app.b2b2c.core.tag.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.shop.core.service.IMemberGiftcardManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 查询会员礼品卡集合
 * @author humaodong
 *
 */
@Component
public class B2b2cMemberGiftcardListTag extends BaseFreeMarkerTag {

	private IStoreMemberManager storeMemberManager;
	@Autowired
	private IMemberGiftcardManager giftcardManager;
	
	@SuppressWarnings("unchecked")
	@Override
	protected Object exec(Map param) throws TemplateModelException {
		StoreMember member= this.storeMemberManager.getStoreMember();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		Map result = new HashMap();
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		int pageSize = 10;
		
		Page webpage = this.giftcardManager.getGiftcardList(Integer.valueOf(page), pageSize, member.getMember_id());
		Long totalCount = webpage.getTotalCount();
		
		List cardList = (List) webpage.getResult();
		cardList = cardList == null ? new ArrayList() : cardList;

		result.put("totalCount", totalCount);
		result.put("pageSize", pageSize);
		result.put("page", page);
		result.put("cardList", cardList);
		
		return result;
	}
	
	

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}



	public IMemberGiftcardManager getGiftcardManager() {
		return giftcardManager;
	}



	public void setGiftcardManager(IMemberGiftcardManager giftcardManager) {
		this.giftcardManager = giftcardManager;
	}

}
