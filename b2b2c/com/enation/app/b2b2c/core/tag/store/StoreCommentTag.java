package com.enation.app.b2b2c.core.tag.store;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.member.IStoreMemberCommentManager;
import com.enation.app.shop.core.model.MemberComment;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 店铺咨询评论标签
 * @author LiFenLong
 *
 */
@Component
public class StoreCommentTag extends BaseFreeMarkerTag{
	private IStoreMemberCommentManager storeMemberCommentManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		return storeMemberCommentManager.get(NumberUtils.toInt(request.getParameter("comment_id")));
	}
	public IStoreMemberCommentManager getStoreMemberCommentManager() {
		return storeMemberCommentManager;
	}
	public void setStoreMemberCommentManager(
			IStoreMemberCommentManager storeMemberCommentManager) {
		this.storeMemberCommentManager = storeMemberCommentManager;
	}
}
