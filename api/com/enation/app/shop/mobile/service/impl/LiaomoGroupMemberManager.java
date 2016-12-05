package com.enation.app.shop.mobile.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.mobile.model.LiaomoGroup;
import com.enation.app.shop.mobile.model.LiaomoGroupMember;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;

@Component
public class LiaomoGroupMemberManager extends BaseSupport<LiaomoGroupMember> {
    
    LiaomoGroupManager liaomoGroupManager;

    @Transactional
    public boolean addGroupMember(long groupId, String memberIds) {
        boolean returnValue = true;
        String[] memberIdArr = memberIds.split(",");
        Long count = 0L;
        
        for (String memberId: memberIdArr) {
            if (liaomoGroupManager.getByMemberAndGroup(groupId, NumberUtils.toLong(memberId)) != null) {
                returnValue = false;
                continue;
            }
            
            count += 1;
            LiaomoGroupMember liaomoGroupMember = new LiaomoGroupMember();
            liaomoGroupMember.setGroup_id(groupId);
            liaomoGroupMember.setMember_id(Long.valueOf(memberId));
            this.baseDaoSupport.insert("es_liaomo_group_member", liaomoGroupMember);
        }

        LiaomoGroup liaomoGroup = liaomoGroupManager.getById(groupId);
        count = liaomoGroup.getCount() + count;
        liaomoGroupManager.changeGroupCount(groupId, NumberUtils.toInt(count.toString()));

        //TODO:加入群的消息

        return returnValue;
    }
    
    @Transactional
    public void delGroupMember(long groupId, long memberId) {
        String sql = "delete from es_liaomo_group_member where group_id="+ groupId +" and member_id="+ memberId;
        this.baseDaoSupport.execute(sql);
        
        LiaomoGroup liaomoGroup = liaomoGroupManager.getById(groupId);
        int count = (int) (liaomoGroup.getCount() - 1);
        liaomoGroupManager.changeGroupCount(groupId, count);
        
        if (count == 0) {
            liaomoGroupManager.delete(groupId);
        }

        //TODO:退出群的消息
        
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Map> getByGroupId(long groupId) {
        String sql = "select m.member_id, m.nickname, m.face, m.xinge_account, lgm.group_id "
                + "from es_liaomo_group_member lgm "
                + "left join es_member m on lgm.member_id=m.member_id "
                + "where lgm.group_id="+ groupId +" order by lgm.id asc";
        List<Map> list = this.baseDaoSupport.queryForList(sql);
        
        for (Map map: list) {
            if (map.get("face") != null) {
                map.put("face", UploadUtil.replacePath(map.get("face").toString()));
            }
        }
        
        return list;
    }
    
    
    public LiaomoGroupMember getByMemberGroup(long memberId, long groupId) {
        String sql = "select * from es_liaomo_group_member where group_id="+ groupId +" and member_id="+ memberId;
        return this.baseDaoSupport.queryForObject(sql, LiaomoGroupMember.class);
    }
    
    
    public LiaomoGroupManager getLiaomoGroupManager() {
        return liaomoGroupManager;
    }
    
    public void setLiaomoGroupManager(LiaomoGroupManager liaomoGroupManager) {
        this.liaomoGroupManager = liaomoGroupManager;
    }
    
}
