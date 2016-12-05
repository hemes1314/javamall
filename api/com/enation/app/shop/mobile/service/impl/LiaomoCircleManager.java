package com.enation.app.shop.mobile.service.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.impl.MemberManager;
import com.enation.app.shop.mobile.model.LiaomoCircle;
import com.enation.app.shop.mobile.model.LiaomoCircleImage;
import com.enation.app.shop.mobile.model.LiaomoCircleReply;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

@Component
public class LiaomoCircleManager extends BaseSupport<LiaomoCircle> {
    
    LiaomoCircleImageManager liaomoCircleImageManager;
    LiaomoCircleReplyManager liaomoCircleReplyManager;
    MemberManager memberManager;

    @Transactional
    public void addCircle(Long memberId, String content, File[] imageArr, String[] imageNameArr) {
        LiaomoCircle liaomoCircle = new LiaomoCircle();
        liaomoCircle.setMember_id(memberId);
        liaomoCircle.setContent(content);
        int createdOn = (int) (System.currentTimeMillis() / 1000);
        liaomoCircle.setCreated_on(createdOn);
        this.baseDaoSupport.insert("es_liaomo_circle", liaomoCircle);
        long circleId = this.baseDaoSupport.getLastId("es_liaomo_circle");
        
        if (imageArr != null) {
            for (int i = 0; i < imageArr.length; i++) {
                String imageUploadAddress = UploadUtil.upload(imageArr[i], imageNameArr[i], "liaomo");
                liaomoCircleImageManager.addCircleImage(circleId, imageUploadAddress);
            }
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Transactional
    public Page getList(long memberId, Integer circleId, Integer pageNo, Integer pageSize) {
        Member member = memberManager.get((int)memberId);
        
        if (circleId == null || circleId == 0L) {
          circleId = member.getCircle_num();
          
          if (circleId == null) {
              circleId = 0;
          }
        }
        
        String sql = "select * from es_liaomo_circle where id>"+ circleId +" and (member_id in ("
                + "select friend_id from es_liaomo_friend where member_id="+ memberId +") "
                + "or member_id="+ memberId +") order by id desc";
        Page page = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize);
        List<Map<String, Object>> list = (List<Map<String, Object>>) page.getResult();
        
        for (int i = 0; i < list.size(); i++) {
            Map map = list.get(i);
            Long thisCircleId = (Long)map.get("id");
            Member member_friend = memberManager.get(NumberUtils.toInt(map.get("member_id").toString()));
            List<LiaomoCircleImage> circleImageList = liaomoCircleImageManager.getByCircleId(thisCircleId);
            List<LiaomoCircleReply> circleReplyList = liaomoCircleReplyManager.getByCircleId(thisCircleId);
            map.put("circle_images", circleImageList);
            map.put("circle_reply", circleReplyList);
            map.put("member_name", member_friend.getNickname());
            map.put("face", UploadUtil.replacePath(member_friend.getFace()));
            list.set(i, map);
            
            if (i == (list.size() - 1)) {
                Integer id = NumberUtils.toInt(map.get("id").toString());
                sql = "update es_member set circle_num="+ id +" where member_id="+ memberId;
                this.baseDaoSupport.execute(sql);
            }
            
            page.setData(list);
        }
        
        return page;
    }
    
    public LiaomoCircleImageManager getLiaomoCircleImageManager() {
        return liaomoCircleImageManager;
    }
    
    public void setLiaomoCircleImageManager(LiaomoCircleImageManager liaomoCircleImageManager) {
        this.liaomoCircleImageManager = liaomoCircleImageManager;
    }
    
    public LiaomoCircleReplyManager getLiaomoCircleReplyManager() {
        return liaomoCircleReplyManager;
    }
    
    public void setLiaomoCircleReplyManager(LiaomoCircleReplyManager liaomoCircleReplyManager) {
        this.liaomoCircleReplyManager = liaomoCircleReplyManager;
    }
    
    public MemberManager getMemberManager() {
        return memberManager;
    }
    
    public void setMemberManager(MemberManager memberManager) {
        this.memberManager = memberManager;
    }
    
}
