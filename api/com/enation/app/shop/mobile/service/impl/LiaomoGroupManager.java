package com.enation.app.shop.mobile.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.impl.MemberManager;
import com.enation.app.shop.mobile.model.LiaomoGroup;
import com.enation.app.shop.mobile.model.LiaomoGroupMember;
import com.enation.app.shop.mobile.util.ChineseUtils;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;

@Component
public class LiaomoGroupManager extends BaseSupport<LiaomoGroup> {
    
    private LiaomoGroupMemberManager liaomoGroupMemberManager;
    
    private MemberManager memberManager;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map<String, Object> getByMemberId(long memberId) {
        String sql = "select lg.id as group_id, lg.name as group_name, lg.avator as group_image from es_liaomo_group lg "
                + "left join es_liaomo_group_member lgm on lg.id=lgm.group_id where lgm.member_id="+ memberId 
                +" order by nlssort(group_name, 'NLS_SORT = SCHINESE_PINYIN_M')";
        List<Map> list =  this.baseDaoSupport.queryForList(sql);
        List<Map> letterList = new ArrayList<Map>();        //相同首字符拼音集合
        Map<String, Object> returnMap = new HashMap<String, Object>();
        String prevInitial = null;          //上一个用户首字符拼音
        
        for (int i = 0; i < list.size(); i++) {
            Map map = list.get(i);
            
            if (map.get("group_image") != null) {
                map.put("group_image", UploadUtil.replacePath(map.get("group_image").toString()));
            }

            String groupName = map.get("group_name") == null ? "" : map.get("group_name").toString();
            char first = groupName.charAt(0);
            Set<String> pinyinSet = ChineseUtils.getCharPinyinPermutation(first, false, true, false);
            String initial = null;
            
            if (pinyinSet == null) {
                initial = String.valueOf(first);
            } else {
                initial = pinyinSet.toArray(new String[0])[0];
            }
            
            if (prevInitial == null) {
                prevInitial = initial;
            }
            
            if (!prevInitial.equalsIgnoreCase(initial)) {
                returnMap.put(prevInitial, letterList);
                letterList = new ArrayList<Map>();
                
                letterList.add(map);
                prevInitial = initial;
            } else {
                letterList.add(map);
            }
            
            if (i == (list.size() - 1)) {
                returnMap.put(prevInitial, letterList);
            }
            
        }
        
        return returnMap;
    }
    
    @Transactional
    public Long addGroup(String name, String avator, String description, Long creator) {
        LiaomoGroup liaomoGroup = new LiaomoGroup();
        liaomoGroup.setName(name);
        liaomoGroup.setAvator(avator);
        liaomoGroup.setDescription(description);
        liaomoGroup.setCreator(creator);
        liaomoGroup.setCount(0L);
        liaomoGroup.setSn(String.valueOf(new Date().getTime()));
        this.baseDaoSupport.insert("es_liaomo_group", liaomoGroup);
        int groupId = this.baseDaoSupport.getLastId("es_liaomo_group");
        
        liaomoGroupMemberManager.addGroupMember(groupId, String.valueOf(creator));
        
        return Long.valueOf(groupId);
    }
    
    public LiaomoGroup get(Long memberId, long groupId) {
        String sql = "select * from es_liaomo_group where id="+ groupId;
        LiaomoGroup liaomoGroup = this.baseDaoSupport.queryForObject(sql, LiaomoGroup.class);
        LiaomoGroupMember liaomoGroupMember = liaomoGroupMemberManager.getByMemberGroup(memberId, groupId);
        Member member = memberManager.get(Integer.valueOf(liaomoGroup.getCreator().toString()));
        liaomoGroup.setCreator_name(member.getNickname() == null ? member.getName() : member.getNickname());
        liaomoGroup.setCaeator_face(UploadUtil.replacePath(member.getFace()));
        
        
        if (liaomoGroupMember == null) {
            liaomoGroup.setIn_group(0);
        } else {
            liaomoGroup.setIn_group(1);
        }
        
        if (liaomoGroup.getAvator() != null) {
            liaomoGroup.setAvator(UploadUtil.replacePath(liaomoGroup.getAvator()));
        }
        return liaomoGroup;
    }
    
    public List<LiaomoGroup> search(String keywords) {
        String sql = "select * from es_liaomo_group where sn='"+ keywords +"' or name='"+ keywords +"'";
        List<LiaomoGroup> list = this.baseDaoSupport.queryForList(sql, LiaomoGroup.class);
        
        for (LiaomoGroup liaomoGroup: list) {
            liaomoGroup.setAvator(UploadUtil.replacePath(liaomoGroup.getAvator()));
        }
        
        return list;
    }
    
    public LiaomoGroup getByMemberAndGroup(long groupId, long memberId) {
        String sql = "select * from es_liaomo_group_member where group_id=? and member_id=?";
        return this.baseDaoSupport.queryForObject(sql, LiaomoGroup.class, groupId, memberId);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void update(LiaomoGroup liaomoGroup) {
        Map map = new HashMap();
        map.put("id", liaomoGroup.getId());
        this.baseDaoSupport.update("es_liaomo_group", liaomoGroup, map);
    }
    
    public LiaomoGroup getById(long groupId) {
        String sql = "select * from es_liaomo_group where id="+ groupId;
        return this.baseDaoSupport.queryForObject(sql, LiaomoGroup.class);
    }
    
    public void changeGroupCount(long groupId, int count) {
        String sql = "update es_liaomo_group set count="+ count +" where id="+ groupId;
        this.baseDaoSupport.execute(sql);
    }
    
    public void delete(long groupId) {
        String sql = "delete from es_liaomo_group where id="+ groupId;
        this.baseDaoSupport.execute(sql);
    }
    
    public LiaomoGroupMemberManager getLiaomoGroupMemberManager() {
        return liaomoGroupMemberManager;
    }
    
    public void setLiaomoGroupMemberManager(LiaomoGroupMemberManager liaomoGroupMemberManager) {
        this.liaomoGroupMemberManager = liaomoGroupMemberManager;
    }
    
    public MemberManager getMemberManager() {
        return memberManager;
    }
    
    public void setMemberManager(MemberManager memberManager) {
        this.memberManager = memberManager;
    }
    
}
