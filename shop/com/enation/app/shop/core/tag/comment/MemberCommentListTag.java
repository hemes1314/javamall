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
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 会员我的评论列表标签
 * @author
 *  member 当前登录会员
 *  commentsPage 会员评论分页列表
 */
@Component
@Scope("prototype")
public class MemberCommentListTag extends BaseFreeMarkerTag {
	private IMemberCommentManager memberCommentManager;
	
	private IMemberOrderItemManager memberOrderItemManager;

	private String action;

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[MemberCommentListTag]");
		}
		
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		int pageSize = 5;

		Map result = new HashMap();

		Page commentsPage = memberCommentManager.getMemberComments(Integer.valueOf(page), pageSize,1,member.getMember_id());
		Long totalCount = commentsPage.getTotalCount();

		List<Map> commentsList = (List) commentsPage.getResult();
		commentsList = commentsList == null ? new ArrayList() : commentsList;
		
		for(Map map :commentsList){
			map.put("img", UploadUtil.replacePath(map.get("img")+""));
		}

		result.put("totalCount", totalCount);
		result.put("pageSize", pageSize);
		result.put("page", page);
		result.put("commentsList", commentsList);
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

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
