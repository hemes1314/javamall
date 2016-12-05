package com.enation.app.shop.core.plugin.member;

import com.enation.app.shop.core.model.MemberComment;

public interface IMemberCommentEvent {
	public void onMemberComment(MemberComment comment);
}
