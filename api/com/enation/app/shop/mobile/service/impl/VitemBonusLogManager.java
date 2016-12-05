package com.enation.app.shop.mobile.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.shop.mobile.model.VitemBonusLog;
import com.enation.eop.sdk.database.BaseSupport;

@Component
public class VitemBonusLogManager extends BaseSupport<VitemBonusLog> {

    public List<VitemBonusLog> getByItem(long vitemId) {
        String sql = "select * from es_vitem_bonus_log where vitem_bonus_id=?";
        return this.baseDaoSupport.queryForList(sql, VitemBonusLog.class, vitemId);
    }
    
    public void add(long vitemBonusId, long memberId, long itemCount) {
        VitemBonusLog vitemBonusLog = new VitemBonusLog();
        vitemBonusLog.setItem_count(itemCount);
        vitemBonusLog.setMember_id(memberId);
        vitemBonusLog.setVitem_bonus_id(vitemBonusId);
        this.baseDaoSupport.insert("es_vitem_bonus_log", vitemBonusLog);
    }
    
}
