package com.enation.app.shop.mobile.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.MemberVitem;
import com.enation.app.shop.core.model.VirtualProduct;
import com.enation.app.shop.core.service.impl.MemberManager;
import com.enation.app.shop.core.service.impl.MemberVitemManager;
import com.enation.app.shop.core.service.impl.VirtualProductManager;
import com.enation.app.shop.mobile.model.VitemBonus;
import com.enation.app.shop.mobile.model.VitemBonusLog;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;

@Component
public class VitemBonusManager extends BaseSupport<VitemBonus> {
    
    private VitemBonusLogManager vitemBonusLogManager;
    private MemberVitemManager memberVitemManager;
    private MemberManager memberManager;
    private LiaomoMessageManager liaomoMessageManager;
    private VirtualProductManager virtualProductManager;

    
    public MemberVitemManager getMemberVitemManager() {
        return memberVitemManager;
    }

    
    public void setMemberVitemManager(MemberVitemManager memberVitemManager) {
        this.memberVitemManager = memberVitemManager;
    }

    
    public LiaomoMessageManager getLiaomoMessageManager() {
        return liaomoMessageManager;
    }

    
    public void setLiaomoMessageManager(LiaomoMessageManager liaomoMessageManager) {
        this.liaomoMessageManager = liaomoMessageManager;
    }

    @Transactional
    public Map<String, Long> addVitemBonus(Integer mode, long sender, long itemId, long groupId, long itemCount, long bonusCount) {
        Map<String, Long> returnMap = new HashMap<String, Long>();
        //删除用户虚拟物品数量
        try {
            memberVitemManager.sub((int)itemId, (int)itemCount, (int)sender);
        } catch(Exception e) {
            e.printStackTrace();
            returnMap.put("add_bonus", -1L);
            return returnMap;
        }
        
        VitemBonus vitemBonus = new VitemBonus();
        vitemBonus.setSender(sender);
        vitemBonus.setVitem_id(itemId);
        vitemBonus.setGroup_id(groupId);
        vitemBonus.setItem_count(itemCount);
        vitemBonus.setBonus_count(bonusCount);
        this.baseDaoSupport.insert("es_vitem_bonus", vitemBonus);
        long vitemBonusId = this.baseDaoSupport.getLastId("es_vitem_bonus");
        //给群组发红包消息
        long messageId = liaomoMessageManager.sendMessage(mode, sender, 4, String.valueOf(vitemBonusId), null, null, groupId, null, null);
        returnMap.put("add_bonus", vitemBonusId);
        returnMap.put("message_id", messageId);
        return returnMap;
    }
    
    @Transactional
    public int grabVitemBonus(long vitemBonusId, long memberId) {
        String sql = "select * from es_vitem_bonus where id=?";
        VitemBonus vitemBonus = this.baseDaoSupport.queryForObject(sql, VitemBonus.class, vitemBonusId);
        List<VitemBonusLog> vitemBonusLogList = vitemBonusLogManager.getByItem(vitemBonusId);
        //剩余虚拟物品数量
        long vitemCount = vitemBonus.getItem_count();
        //剩余红包数量
        long bonusCount = vitemBonus.getBonus_count() - vitemBonusLogList.size();
        
        if (bonusCount == 0) {
            return 0;
        }
        
        for (VitemBonusLog vitemBonusLog: vitemBonusLogList) {
            if (vitemBonusLog.getMember_id() == memberId) {
                return -1;
            }
            
            vitemCount -= vitemBonusLog.getItem_count();
        }
        
        //随机数算法(剩余虚拟物品数量-剩余红包数量+1)确保每个红包有人抢到
        int randomItemCount = ((int)vitemCount - (int)bonusCount + 1);
        int geabItemCount = RandomUtils.nextInt(randomItemCount);
        geabItemCount = geabItemCount == 0 ? 1 : geabItemCount;
        
        //如果是最后一个抢红包的，剩余数量都给他
        if (bonusCount == 1) {
            geabItemCount = (int)vitemCount;
        }
        
        vitemBonusLogManager.add(vitemBonusId, memberId, geabItemCount);
        VirtualProduct virtualProduct = virtualProductManager.get(NumberUtils.toInt(vitemBonus.getVitem_id().toString()));
        
        try {
            memberVitemManager.add(virtualProduct, (int)geabItemCount, (int)memberId);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return 1;
    }
    
    public List<Map<String, Object>> getVitemBonusLog(long vitemBonusId) {
        List<VitemBonusLog> vitemBonusLogList = vitemBonusLogManager.getByItem(vitemBonusId);
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        
        for (VitemBonusLog vitemBonusLog: vitemBonusLogList) {
            Map<String, Object> logMap = new HashMap<String, Object>();
            logMap.put("vitem_count", vitemBonusLog.getItem_count());
            Member member = memberManager.get(NumberUtils.toInt(vitemBonusLog.getMember_id().toString()));
            logMap.put("member_id", member.getMember_id());
            logMap.put("mamber_name", member.getNickname());
            
            if (member.getFace() != null) {
                logMap.put("face", UploadUtil.replacePath(member.getFace()));
            }
            returnList.add(logMap);
        }
        return returnList;
    }
    
    public VitemBonusLogManager getVitemBonusLogManager() {
        return vitemBonusLogManager;
    }

    public void setVitemBonusLogManager(VitemBonusLogManager vitemBonusLogManager) {
        this.vitemBonusLogManager = vitemBonusLogManager;
    }
    
    public MemberManager getMemberManager() {
        return memberManager;
    }
    
    public void setMemberManager(MemberManager memberManager) {
        this.memberManager = memberManager;
    }

    public VirtualProductManager getVirtualProductManager() {
        return virtualProductManager;
    }
    
    public void setVirtualProductManager(VirtualProductManager virtualProductManager) {
        this.virtualProductManager = virtualProductManager;
    }
    
}
