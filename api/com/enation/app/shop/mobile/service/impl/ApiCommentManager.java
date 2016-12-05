package com.enation.app.shop.mobile.service.impl;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.MemberComment;
import com.enation.app.shop.mobile.service.IApiCommentManager;
import com.enation.eop.sdk.database.BaseSupport;

@Component
public class ApiCommentManager extends BaseSupport<MemberComment> implements IApiCommentManager {
   
	
	@Override
    public int getCommentsCount(int goods_id) {
        return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_comment WHERE goods_id=? AND status=1 AND type=1", goods_id);
    }

   
	@Override
    public int getCommentsCount(int goods_id, int grade){
        return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_comment WHERE goods_id=? AND status=1 AND type=1 AND grade >= ?", goods_id, grade);
    }


}
