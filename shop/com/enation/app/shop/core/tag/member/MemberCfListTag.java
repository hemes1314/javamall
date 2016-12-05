package com.enation.app.shop.core.tag.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberCfManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 会员众筹列表标签
 * @author humaodong
 *2015-12-9下午15:13:00
 */

@Component
@Scope("prototype")
public class MemberCfListTag extends BaseFreeMarkerTag{

	private IMemberCfManager memberCfManager;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();

		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[MemberCfListTag]");
		}
		Map result = new HashMap();
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		int pageSize = 10;
		String status = request.getParameter("status");
		
		Page cfPage = memberCfManager.page(Integer.valueOf(page), pageSize, status);
		Long totalCount = cfPage.getTotalCount();
		
		List cfList = (List) cfPage.getResult();
		cfList = cfList == null ? new ArrayList() : cfList;

		result.put("totalCount", totalCount);
		result.put("pageSize", pageSize);
		result.put("pageNo", page);
		result.put("cfList", cfList);

		if(status!=null){
			result.put("status",status);
		}
		
		return result;
	}

	public IMemberCfManager getMemberCfManager() {
		return memberCfManager;
	}
	public void setMemberCfManager(IMemberCfManager memberCfManager) {
		this.memberCfManager = memberCfManager;
	}
}
