package com.enation.app.shop.mobile.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enation.app.base.core.model.Member;
import com.tencent.xinge.ClickAction;
import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.service.impl.MemberManager;
import com.enation.app.shop.mobile.model.LiaomoFriend;
import com.enation.app.shop.mobile.util.ChineseUtils;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;

/**
 * status:如果A添加B为好友
 1:已经添加,未确认
 2:收到请求,未确认
 11:请求被拒绝
 12:已经拒绝对方请求
 13:已经忽略对方请求,备用
 101:成功添加为好友
 */
@Component
public class LiaomoFriendsManager extends BaseSupport<LiaomoFriend> {


    private PushManager pushManager;
    private MemberManager memberManager;
        
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map<String, Object> getFriends(long memberId) {
        String sql = "select lf.friend_id as user_id,lf.status as status, m.nickname as user_name, m.face as user_image, m.uname "
                + "from es_liaomo_friend lf "
                + "left join es_member m on lf.friend_id=m.member_id "
                + "where lf.member_id="+ memberId +" order by nlssort(user_name, 'NLS_SORT = SCHINESE_PINYIN_M')";
        List<Map> list = this.baseDaoSupport.queryForList(sql);
        Map<String, Object> returnMap = new HashMap<String, Object>();
        String prevInitial = null;      //上一个用户首字符拼音
        List<Map> letterList = new ArrayList<Map>();    //相同首字符拼音集合
        
        for (int i = 0; i < list.size(); i++) {
            Map map = list.get(i);
            
            if (memberManager.get(NumberUtils.toInt(map.get("user_id").toString())) == null) {
                continue;
            }
            
            if (map.get("user_image") != null) {
                map.put("user_image", UploadUtil.replacePath(map.get("user_image").toString()));
            }

            //没有nickname的,这里就是空字符串了,所以取第一个字符就异常了.
            String userName = map.get("user_name") == null ? "" : map.get("user_name").toString();
            if (userName == null || "".equals(userName.trim())) {
                userName = map.get("uname").toString();
                map.put("user_name", userName);
            }
            char first = userName.charAt(0);    //截取第一个字符
            Set<String> pinyinSet = ChineseUtils.getCharPinyinPermutation(first, false, true, false);
            String initial = null;  //第一个字符拼音
            
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
                
                if (i == (list.size() - 1)) {
                    returnMap.put(prevInitial, letterList);
                }
            } else {
                letterList.add(map);
                
                if (i == (list.size() - 1)) {
                    returnMap.put(prevInitial, letterList);
                }
            }
            
        }
        
        return returnMap;
    }
    
    @Transactional
    public void addFriend(long memberId, long friendId) {
        //因为有拒绝的关系,所以从心请求要删除之前的,可能有问题就是不断有人重试,这个客户端自己控制把,获取的列表有存在的关系,处于未确认状态,那就不再请求了.
        List<LiaomoFriend> friends = this.getByMemberAndFriend(memberId, friendId);

        boolean notify = true;
        //没有就插入,有就更新
        if (friends.isEmpty()) {
            LiaomoFriend liaomoFriend1 = new LiaomoFriend();
            liaomoFriend1.setMember_id(memberId);
            liaomoFriend1.setFriend_id(friendId);
            liaomoFriend1.setStatus(LiaomoFriend.STATUS_REQUESTED_ADD);
            this.baseDaoSupport.insert("es_liaomo_friend", liaomoFriend1);

            LiaomoFriend liaomoFriend2 = new LiaomoFriend();
            liaomoFriend2.setMember_id(friendId);
            liaomoFriend2.setFriend_id(memberId);
            liaomoFriend2.setStatus(LiaomoFriend.STATUS_RECEIVED_ADD);
            this.baseDaoSupport.insert("es_liaomo_friend", liaomoFriend2);
        } else {

            LiaomoFriend lf = friends.get(0);
            if (lf.getStatus() != LiaomoFriend.STATUS_OK) {
                String sql = "update es_liaomo_friend set status=? where member_id=? and friend_id=?";
                this.baseDaoSupport.execute(sql, LiaomoFriend.STATUS_REQUESTED_ADD, memberId, friendId);
                sql = "update es_liaomo_friend set status=? where member_id=? and friend_id=?";
                this.baseDaoSupport.execute(sql, LiaomoFriend.STATUS_RECEIVED_ADD, friendId, memberId);
            } else {
                //已经是好友,不需要通知了
                notify = false;
            }

        }

        //推送通知消息,添加好友
        if (notify) {
            Member senderMember = memberManager.get((int)memberId);
            String name1 = StringUtils.isNotBlank(senderMember.getNickname())?senderMember.getNickname():senderMember.getUname();
            String content = name1 + "请求添加您为好友";
            Map<String, Object> custom = new HashMap<>();
            custom.put("type", PushManager.TYPE_FRIEND_ADD);
            custom.put("sender", memberId);
            custom.put("receiver", friendId);
            pushManager.doPush(memberId, friendId, content, custom, "com.dlruijin.gomecellar.activity.momo_qi.liaomo.moliao.MoLiaoFriendMessagesActivity", null);
        }

    }

    /**
     * 这是和add反过来的memberId和friendId
     *
     * @param memberId
     * @param friendId
     * @param agreed
     */
    @Transactional
    public void confirmFriend(long memberId, long friendId, boolean agreed) {
        String sql = "update es_liaomo_friend set status=? where member_id=? and friend_id=?";
        if (agreed) {
            this.baseDaoSupport.execute(sql, LiaomoFriend.STATUS_OK, memberId, friendId);
            this.baseDaoSupport.execute(sql, LiaomoFriend.STATUS_OK,friendId, memberId);
        } else {
            this.baseDaoSupport.execute(sql, LiaomoFriend.STATUS_REJECTED_ADD, memberId, friendId);
            this.baseDaoSupport.execute(sql, LiaomoFriend.STATUS_FAILED_ADD,friendId, memberId);
        }


        //推送通知消息, 确认好友, 静默
        Map<String, Object> custom = new HashMap<>();
        custom.put("type", agreed?PushManager.TYPE_FRIEND_ACCEPT:PushManager.TYPE_FRIEND_REJECT);
        custom.put("sender", memberId);
        custom.put("receiver", friendId);

        Member senderMember = memberManager.get((int)memberId);
        String name1 = StringUtils.isNotBlank(senderMember.getNickname())?senderMember.getNickname():senderMember.getUname();
        String content = name1 + (agreed?"同意":"拒绝") + "添加您为好友";

        String activity = null;
        if (agreed) {
            activity = "com.dlruijin.gomecellar.activity.momo_qi.liaomo.moliao.MoLiaoChatActivity_New";
        } else {
            activity = "com.dlruijin.gomecellar.activity.momo_qi.liaomo.MoliaoActivity";
        }

        pushManager.doPush(memberId, friendId, content, custom, activity, null);
    }

    @Transactional
    public void delFriend(long memberId, long friendId) {
        String sql = "delete from es_liaomo_friend where member_id=? and friend_id=?";
        this.baseDaoSupport.execute(sql, memberId, friendId);
        this.baseDaoSupport.execute(sql, friendId, memberId);

        //推送通知消息,删除好友, 静默
        Map<String, Object> custom = new HashMap<>();
        custom.put("type", PushManager.TYPE_FRIEND_DELETE);
        custom.put("sender", memberId);
        custom.put("receiver", friendId);

        Member senderMember = memberManager.get((int)memberId);
        String name1 = StringUtils.isNotBlank(senderMember.getNickname())?senderMember.getNickname():senderMember.getUname();
        String content = name1 + "解除了和您的好友关系";

        pushManager.doPush(memberId, friendId, content, custom, "com.dlruijin.gomecellar.activity.momo_qi.liaomo.MoliaoActivity", null);
    }
    
    public List<LiaomoFriend> getByMemberAndFriend(long memberId, long friendId) {
        String sql = "select * from es_liaomo_friend where member_id="+ memberId +" and friend_id="+ friendId;
        return this.baseDaoSupport.queryForList(sql, LiaomoFriend.class);
    }



    public MemberManager getMemberManager() {
        return memberManager;
    }
    
    public void setMemberManager(MemberManager memberManager) {
        this.memberManager = memberManager;
    }

    public PushManager getPushManager() {
        return pushManager;
    }

    public void setPushManager(PushManager pushManager) {
        this.pushManager = pushManager;
    }
}
