package com.enation.app.shop.mobile.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.shop.mobile.model.LiaomoCircleReply;
import com.enation.eop.sdk.database.BaseSupport;

@Component
public class LiaomoCircleReplyManager extends BaseSupport<LiaomoCircleReply> {

    public List<LiaomoCircleReply> sendReply(long circleId, String reply) {
        LiaomoCircleReply liaomoCircleReply = new LiaomoCircleReply();
        liaomoCircleReply.setCircle_id(circleId);
        liaomoCircleReply.setReply(reply);
        baseDaoSupport.insert("es_liaomo_circle_reply", liaomoCircleReply);
        return this.getByCircleId(circleId);
    }
    
    public List<LiaomoCircleReply> getByCircleId(long circleId) {
        String sql = "select * from es_liaomo_circle_reply where circle_id="+ circleId +" order by id asc";
        return this.baseDaoSupport.queryForList(sql, LiaomoCircleReply.class);
    }
    
}
