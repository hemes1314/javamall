package com.enation.app.shop.core.tag.comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.app.shop.core.service.IMemberOrderItemManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 会员中心等待评论商品列表     whj  0609-15:40 
 * @author 
 *
 */
@Component
@Scope("prototype")
public class MemberWaitCommontListTag extends BaseFreeMarkerTag{
	
	private IMemberCommentManager memberCommentManager;
	
	private IMemberOrderItemManager memberOrderItemManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
	
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[MemberWaitCommontListTag]");
		}
		int pageSize = 12;

		Map result = new HashMap();
		
		Page goodsPage = memberOrderItemManager.getGoodsList(member.getMember_id(), 0, Integer.valueOf(page), pageSize);
		
		List waitcommentsList = (List) goodsPage.getResult();
		waitcommentsList = waitcommentsList == null ? new ArrayList() : waitcommentsList;
		
		result.put("goodsPage", goodsPage);
		result.put("totalCount", goodsPage.getTotalCount());
		result.put("pageSize", pageSize);
		result.put("page", page);
		result.put("waitcommentsList", waitcommentsList);
		return result;
	}

	public IMemberCommentManager getMemberCommentManager() {
		return memberCommentManager;
	}

	public void setMemberCommentManager(IMemberCommentManager memberCommentManager) {
		this.memberCommentManager = memberCommentManager;
	}

	public IMemberOrderItemManager getMemberOrderItemManager() {
		return memberOrderItemManager;
	}

	public void setMemberOrderItemManager(
			IMemberOrderItemManager memberOrderItemManager) {
		this.memberOrderItemManager = memberOrderItemManager;
	}
	
	

}
