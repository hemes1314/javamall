package com.enation.app.b2b2c.core.tag.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.shop.core.service.IMemberVitemManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 查询会员虚拟物品集合
 * @author humaodong
 *
 */
@Component
public class B2b2cMemberVitemListTag extends BaseFreeMarkerTag {

	private IStoreMemberManager storeMemberManager;
	@Autowired
	private IMemberVitemManager vitemManager;
	
	@SuppressWarnings("unchecked")
	@Override
	protected Object exec(Map param) throws TemplateModelException {
		StoreMember member= this.storeMemberManager.getStoreMember();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		Map result = new HashMap();
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		int pageSize = 10;
		
		Page webpage = this.vitemManager.getList(Integer.valueOf(page), pageSize, member.getMember_id());
		Long totalCount = webpage.getTotalCount();
		
		List vlist = (List) webpage.getResult();
		vlist = vlist == null ? new ArrayList() : vlist;

		result.put("totalCount", totalCount);
		result.put("pageSize", pageSize);
		result.put("page", page);
		result.put("vlist", vlist);
		
		return result;
	}
	
	

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}



	public IMemberVitemManager getVitemManager() {
		return vitemManager;
	}



	public void setVitemManager(IMemberVitemManager vitemManager) {
		this.vitemManager = vitemManager;
	}

}
