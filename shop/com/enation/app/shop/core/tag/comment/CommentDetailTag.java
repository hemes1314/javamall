package com.enation.app.shop.core.tag.comment;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.MemberComment;
import com.enation.app.shop.core.service.impl.MemberCommentManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 评论详细标签
 * @author fenlongli
 *
 */
@Component
@Scope("prototype")
public class CommentDetailTag extends BaseFreeMarkerTag {
     private MemberCommentManager memberCommentManager;
	/**
	 * @param comment_id 评论Id
	 */
    @Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer conmmentid = (Integer)params.get("comment_id");
		MemberComment memberComment=memberCommentManager.get(conmmentid);
		return memberComment;
	}
	public MemberCommentManager getMemberCommentManager() {
		return memberCommentManager;
	}
	public void setMemberCommentManager(MemberCommentManager memberCommentManager) {
		this.memberCommentManager = memberCommentManager;
	}
    
}
