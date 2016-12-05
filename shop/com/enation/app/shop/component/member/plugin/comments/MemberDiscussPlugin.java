package com.enation.app.shop.component.member.plugin.comments;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.plugin.member.IMemberTabShowEvent;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.database.Page;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 他的评论
 * @author lzf<br/>
 * 2012-4-5下午05:15:19<br/>
 */
@Component
public class MemberDiscussPlugin extends AutoRegisterPlugin implements
		IMemberTabShowEvent {
	
	private IMemberCommentManager memberCommentManager;

	@Override
	public boolean canBeExecute(Member member) {
		return true;
	}

	@Override
	public int getOrder() {
		return 19;
	}

	@Override
	public String getTabName(Member member) {
		return "他的评论";
	}
	/**
	 * @param member 会员
	 * listComments 会员评论列表
	 */
	@Override
	public String onShowMemberDetailHtml(Member member) {
		Page page = memberCommentManager.getMemberComments(1, 100, 1, member.getMember_id());
		List listComments = new ArrayList();
		if(page != null){
			listComments = (List)page.getResult();
		}
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.putData("listComments",listComments);
		freeMarkerPaser.setPageName("comments");		
		return freeMarkerPaser.proessPageContent();
	}

	public IMemberCommentManager getMemberCommentManager() {
		return memberCommentManager;
	}

	public void setMemberCommentManager(IMemberCommentManager memberCommentManager) {
		this.memberCommentManager = memberCommentManager;
	}

}
