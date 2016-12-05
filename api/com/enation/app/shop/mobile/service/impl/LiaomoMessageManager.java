package com.enation.app.shop.mobile.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enation.app.shop.mobile.model.LiaomoFriend;
import com.tencent.xinge.ClickAction;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.impl.MemberManager;
import com.enation.app.shop.mobile.model.LiaomoGroup;
import com.enation.app.shop.mobile.model.LiaomoMessage;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.util.DateUtil;
import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;

@Component
public class LiaomoMessageManager extends BaseSupport<LiaomoMessage> {


    
    MemberManager memberManager;
    LiaomoGroupMemberManager liaomoGroupMemberManager;
    LiaomoGroupManager liaomoGroupManager;
    LiaomoFriendsManager liaomoFriendsManager;

    @SuppressWarnings("rawtypes")
    @Transactional
    public int sendMessage(Integer mode, long sender, int contentType, String content, File contentFile, String contentFileFileName, 
            Long groupId, Long receiver, String length) {

        if (receiver !=null && sender == receiver) {
            throw new RuntimeException("不可以给自己发消息");
        }
        //检测是否是好友
        if (groupId == null) {
            List<LiaomoFriend> friends = liaomoFriendsManager.getByMemberAndFriend(sender, receiver);
            if (friends == null || friends.isEmpty()) {
                throw new RuntimeException("只能给好友发消息");
            }
            if (friends.get(0).getStatus() != LiaomoFriend.STATUS_OK) {
                throw new RuntimeException("只能给好友发消息, 对方尚未同意您的好友请求");
            }
        }



        if (mode == null) {
            mode = 2;
        }

        if (contentType != 1 && contentType != 4) {
            content = UploadUtil.upload(contentFile, contentFileFileName, "liaomo");
        }
        
        LiaomoMessage liaomoMessage = new LiaomoMessage();
        liaomoMessage.setSender(sender);
        liaomoMessage.setContent_type(contentType);
        liaomoMessage.setContent(content);
        liaomoMessage.setCreated_on(new Date().getTime() / 1000);
        liaomoMessage.setGroup_id(groupId);
        liaomoMessage.setReceiver(receiver);
        liaomoMessage.setContent_length(length);
        this.baseDaoSupport.insert("es_liaomo_message", liaomoMessage);
        this.baseDaoSupport.insert("es_liaomo_read_message", liaomoMessage);
        int messageId = this.baseDaoSupport.getLastId("es_liaomo_message");
        
        Map<String, Object> custom = new HashMap<String, Object>();
        custom.put("message_id", String.valueOf(messageId));
        custom.put("sender", String.valueOf(sender));
        custom.put("message_type", String.valueOf(contentType));


        if (groupId != null) {
            custom.put("group_id", String.valueOf(groupId));
            custom.put("type", PushManager.TYPE_GROUP_MESSAGE);
        } else {
            custom.put("type", PushManager.TYPE_MESSAGE);
        }
        
        //不是文本信息处理
        if (contentType != 1 && contentType != 4) {
            //custom.put("file_address", UploadUtil.replacePath(content));
            content = "收到一个文件信息";
        }
        
        //IOS推送
        MessageIOS mess = new MessageIOS();
        mess.setAlert(content);
        mess.setBadge(1);
        mess.setSound("beep.wav");
        mess.setCustom(custom);
        XingeApp push = new XingeApp(PushManager.accessId, PushManager.secretKey);
        
        //android推送
        Message messAndroid = new Message();
        messAndroid.setExpireTime(86400);
        messAndroid.setTitle(memberManager.get((int) sender).getNickname());
        messAndroid.setContent(content);
        messAndroid.setType(Message.TYPE_NOTIFICATION);
        ClickAction action = new ClickAction();
        action.setActionType(ClickAction.TYPE_ACTIVITY);
        action.setActivity("com.dlruijin.gomecellar.activity.momo_qi.liaomo.moliao.MoLiaoChatActivity_New");
        messAndroid.setAction(action);
        messAndroid.setCustom(custom);

        XingeApp androidPush = new XingeApp(PushManager.androidAccessId, PushManager.androidSecretKey);

        if (groupId != null) {
            //群组的activity
            action.setActivity("com.dlruijin.gomecellar.activity.momo_qi.liaomo.moliao.MoLiaoGroupChatActivity_New");

            messAndroid.setTitle(liaomoGroupManager.getById(groupId).getName());
            List<String> accountList = new ArrayList<String>();
            List<String> androidAccountList = new ArrayList<String>();
            List<Map> list = liaomoGroupMemberManager.getByGroupId(groupId);
            
            for (Map map: list) {
                if ((map.get("xinge_account") != null) && (NumberUtils.toLong(map.get("member_id").toString()) != sender)) {
                    String xingeAccount = map.get("xinge_account").toString();
                    //1_开头是ios的信鸽token， 2_开头的是android的token
                    String[] splitArray = xingeAccount.split("_");
                    
                    //过滤xingeAccount是2_的数据，这样的数据会报错
                    if (splitArray.length == 2) {
                        if (splitArray[0].equals("1")) {
                            accountList.add(splitArray[1]);
                        } else {
                            androidAccountList.add(splitArray[1]);
                        }
                    }
                }
            }
            
            if (accountList.size() > 0) {
                JSONObject multiPushRet = push.createMultipush(mess, mode == 1 ? XingeApp.IOSENV_DEV : XingeApp.IOSENV_PROD);
                System.out.println("tension--iosCreateMultipush---" + multiPushRet);
                int returnCode = push.pushDeviceListMultiple(multiPushRet.getJSONObject("result").getInt("push_id"), accountList).getInt("ret_code");
                System.out.println("tension--iosPushDeviceListMultiple---" + returnCode);
                
            }
            
            if (androidAccountList.size() > 0) {
                JSONObject androidMultiPushRet = androidPush.createMultipush(messAndroid);
                System.out.println("tension--androidCreateMultipush---" + androidMultiPushRet);
                int returnCode = androidPush.pushDeviceListMultiple(androidMultiPushRet.getJSONObject("result").getInt("push_id"), androidAccountList).getInt("ret_code");
                System.out.println("tension--androidPushDeviceListMultiple---" + returnCode);
            }
        } else {
            Member receiverMember = memberManager.get(Integer.valueOf(receiver.toString()));
            
            if (receiverMember.getXinge_account() != null) {
                String xingeAccount = receiverMember.getXinge_account();
                String[] splitArray = xingeAccount.split("_");
                
                //过滤xingeAccount是2_的数据，这样的数据会报错
                if (splitArray.length == 2) {
                    if (splitArray[0].equals("1")) {
                        int returnCode = push.pushSingleDevice(splitArray[1], mess, mode == 1 ? XingeApp.IOSENV_DEV : XingeApp.IOSENV_PROD).getInt("ret_code");
                        System.out.println("tension--iosPushSingleDevice---" + returnCode);
                    } else {
                        int returnCode = androidPush.pushSingleDevice(splitArray[1], messAndroid).getInt("ret_code");
                        System.out.println("tension--androidPushSingleDevice---" + returnCode);
                    }
                }
            }
        }
        
        return messageId;
        
    }
    
    public void setReadMessage(Long sender, Long receiver, Long groupId, long messageId) {
        //留最新一条记录
        
        if (receiver != null) {
            String sql = "delete from es_liaomo_message where sender=? and receiver=? and id != ?";
            this.baseDaoSupport.execute(sql, sender, receiver, messageId);
        }
        
        if (groupId != null) {
            String sql = "delete from es_liaomo_message where sender=? and group_id=? and id != ?";
            this.baseDaoSupport.execute(sql, sender, groupId, messageId);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public List<Map> getUnreadMessage(long memberId, Long friendId) {
        String sql = "";
        if (friendId != null) {
            sql = "select * from es_liaomo_message where receiver="+ memberId +" and sender="+ friendId +" order by id asc";
        } else {
            sql = "select * from es_liaomo_message where receiver="+ memberId +" order by id asc";
        }
        List<LiaomoMessage> unreadMessageList = this.baseDaoSupport.queryForList(sql, LiaomoMessage.class);
        List<Map> returnList = new ArrayList<Map>();
        
        for (int i = 0; i < unreadMessageList.size(); i++) {
            LiaomoMessage liaomoMessage = unreadMessageList.get(i);
            Long senderId = liaomoMessage.getSender();
            Long groupId = liaomoMessage.getGroup_id();
            String content = liaomoMessage.getContent();
            Integer contentType = liaomoMessage.getContent_type();
            Member member = memberManager.get(Integer.valueOf(senderId.toString()));
            LiaomoGroup liaomoGroup = groupId == null ? null : liaomoGroupManager.getById(groupId);
            
            Map<String, String> contentMap = new HashMap<String, String>();
            contentMap.put("id", String.valueOf(liaomoMessage.getId()));
            contentMap.put("sender_id", senderId.toString());
            contentMap.put("sender_name", member.getNickname());
            contentMap.put("sender_face", member.getFace() == null ? null : UploadUtil.replacePath(member.getFace()));
            contentMap.put("group_id", liaomoGroup == null ? null: String.valueOf(groupId));
            contentMap.put("group_name", liaomoGroup == null ? null: liaomoGroup.getName());
            contentMap.put("group_avator", liaomoGroup == null ? null: UploadUtil.replacePath(liaomoGroup.getAvator()));
            contentMap.put("length", liaomoMessage.getContent_length());
            //不是文本信息处理
            if (contentType != 1 && contentType != 4) {
                contentMap.put("content", UploadUtil.replacePath(content));
            }
            contentMap.put("content_type", liaomoMessage.getContent_type().toString());
            contentMap.put("created_on", DateUtil.toString(new Date(liaomoMessage.getCreated_on()),"yyyy-MM-dd HH:mm:ss"));
            
            returnList.set(i, contentMap);
        }
        
        return returnList;
        
    }
    
    public List<Map<String, String>> getHistoryMessage(Long messageId, long memberId, Long friendId, Long groupId) {
        String cond = "";
        String where = "";
        
        if (groupId != null) {
            if (cond.length() > 0) {
                cond += " and ";
            }
            cond += "group_id=" + groupId;
        }
        
        if (friendId != null) {
            if (cond.length() > 0) {
                cond += " and ";
            }
            cond += "(sender="+ memberId +" or sender="+ friendId +") and (receiver="+ memberId +" or receiver="+ friendId +")";
        }
        
        if (messageId == null) {
            where += "rownum <= 10";
        } else {
            where += "id >= " + messageId;
        }
        
        String sql = "select * from (select * from es_liaomo_read_message where @cond order by id desc) where @where order by id asc";
        sql = sql.replace("@cond", cond);
        sql = sql.replace("@where", where);
        List<LiaomoMessage> historyMessageList = this.baseDaoSupport.queryForList(sql, LiaomoMessage.class);
        List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();
        
        for (int i = 0; i < historyMessageList.size(); i++) {
            LiaomoMessage liaomoMessage = historyMessageList.get(i);
            Long senderId = liaomoMessage.getSender();
            Long dbGroupId = liaomoMessage.getGroup_id();
            String content = liaomoMessage.getContent();
            Integer contentType = liaomoMessage.getContent_type();
            Member member = memberManager.get(Integer.valueOf(senderId.toString()));
            LiaomoGroup liaomoGroup = dbGroupId == null ? null : liaomoGroupManager.getById(groupId);
            
            Map<String, String> contentMap = new HashMap<String, String>();
            contentMap.put("id", String.valueOf(liaomoMessage.getId()));
            contentMap.put("sender_id", senderId.toString());
            contentMap.put("sender_name", member.getNickname());
            contentMap.put("sender_face", member.getFace() == null ? null : UploadUtil.replacePath(member.getFace()));
            contentMap.put("group_id", liaomoGroup == null ? null : String.valueOf(groupId));
            contentMap.put("group_name", liaomoGroup == null ? null : liaomoGroup.getName());
            contentMap.put("group_avator", liaomoGroup == null ? null : UploadUtil.replacePath(liaomoGroup.getAvator()));
            contentMap.put("length", liaomoMessage.getContent_length());
            //不是文本信息处理
            if (contentType != 1 && contentType != 4) {
                contentMap.put("content", UploadUtil.replacePath(content));
            } else {
                contentMap.put("content", content);
            }
            contentMap.put("content_type", liaomoMessage.getContent_type().toString());
            contentMap.put("created_on", DateUtil.toString(new Date(liaomoMessage.getCreated_on() * 1000),"yyyy-MM-dd HH:mm:ss"));
            
            returnList.add(contentMap);
        }
        
        return returnList;
    }
    
    public List<Map<String, Object>> getRecentMessage(long memberId) {
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        //查询最近发送给此会员的所有消息
        String sql = "select * from es_liaomo_message where sender=? or receiver=? or group_id in (select group_id from "
                + "es_liaomo_group_member where member_id=?) order by created_on desc";
        List<LiaomoMessage> messageList = this.baseDaoSupport.queryForList(sql, LiaomoMessage.class, memberId, memberId,  memberId);
        //好友消息统计
        int friendCount = 0;
        //判断唯一Set
        Set<Long> receiverSet = new HashSet<Long>();
        Set<Long> groupSet = new HashSet<Long>();
        
        //取最新20个好友的第一条消息
        for (int i = 0; i < messageList.size(); i++) {
            Long receiver = null;
            LiaomoMessage liaomoMessage = messageList.get(i);
            
            if (liaomoMessage.getReceiver() != null && liaomoMessage.getSender() == memberId) {
                receiver = liaomoMessage.getReceiver();
            }
            
            if (liaomoMessage.getReceiver() != null && liaomoMessage.getSender() != memberId) {
                receiver = liaomoMessage.getSender();
            }
            
            //保留每个用户的第一条数据
            if (((receiver != null && !receiverSet.contains(receiver)) 
                    || (liaomoMessage.getGroup_id() != null && !groupSet.contains(liaomoMessage.getGroup_id())))
                    && friendCount <= 20) {
                friendCount += 1;
                String content = liaomoMessage.getContent();
                Integer contentType = liaomoMessage.getContent_type();
                
                Map<String, Object> contentMap = new HashMap<String, Object>();
                contentMap.put("id", String.valueOf(liaomoMessage.getId()));
                //不是文本信息处理
                if (contentType != 1 && contentType != 4) {
                    contentMap.put("content", UploadUtil.replacePath(content));
                    contentMap.put("length", liaomoMessage.getContent_length());
                } else {
                    contentMap.put("content", content);
                }
                //如果是分组信息
                if (liaomoMessage.getGroup_id() != null) {
                    groupSet.add(liaomoMessage.getGroup_id());
                    LiaomoGroup liaomoGroup = liaomoGroupManager.getById(liaomoMessage.getGroup_id());
                    
                    contentMap.put("group_id", liaomoMessage.getGroup_id());
                    contentMap.put("group_name", liaomoGroup.getName());
                    
                    if (liaomoMessage.getSender() == memberId) {
                        contentMap.put("unread_count", 0);
                    } else {
                        //获取未读消息数量
                        sql = "select count(*) from es_liaomo_message where sender=? and group_id=?";
                        contentMap.put("unread_count", this.baseDaoSupport.queryForInt(sql, liaomoMessage.getSender(), liaomoMessage.getGroup_id()) - 1);
                    }
                } else {
                    receiverSet.add(receiver);
                    Member member = new Member();
                    //如果本机是发送者获取接收者信息
                    if (liaomoMessage.getSender() == memberId) {
                        member = memberManager.get(NumberUtils.toInt(liaomoMessage.getReceiver().toString()));
                        contentMap.put("unread_count", 0);
                    } else {
                        member = memberManager.get(NumberUtils.toInt(liaomoMessage.getSender().toString()));
                        sql = "select count(*) from es_liaomo_message where sender=? and receiver=?";
                        contentMap.put("unread_count", this.baseDaoSupport.queryForInt(sql, liaomoMessage.getSender(), liaomoMessage.getReceiver()) - 1);
                    }
                    
                    //获取未读消息数量
                    contentMap.put("friend_id", member.getMember_id());
                    contentMap.put("friend_name", member.getNickname());
                    contentMap.put("sender_face", member.getFace() == null ? null : UploadUtil.replacePath(member.getFace()));
                }
                contentMap.put("content_type", liaomoMessage.getContent_type().toString());
                contentMap.put("created_on", DateUtil.toString(new Date(liaomoMessage.getCreated_on() * 1000),"yyyy-MM-dd HH:mm:ss"));
                
                returnList.add(contentMap);
            }
        }
        
        return returnList;
    }
    
    public MemberManager getMemberManager() {
        return memberManager;
    }
    
    public void setMemberManager(MemberManager memberManager) {
        this.memberManager = memberManager;
    }
    
    public LiaomoGroupMemberManager getLiaomoGroupMemberManager() {
        return liaomoGroupMemberManager;
    }
    
    public void setLiaomoGroupMemberManager(LiaomoGroupMemberManager liaomoGroupMemberManager) {
        this.liaomoGroupMemberManager = liaomoGroupMemberManager;
    }
    
    public LiaomoGroupManager getLiaomoGroupManager() {
        return liaomoGroupManager;
    }
    
    public void setLiaomoGroupManager(LiaomoGroupManager liaomoGroupManager) {
        this.liaomoGroupManager = liaomoGroupManager;
    }

    public LiaomoFriendsManager getLiaomoFriendsManager() {
        return liaomoFriendsManager;
    }

    public void setLiaomoFriendsManager(LiaomoFriendsManager liaomoFriendsManager) {
        this.liaomoFriendsManager = liaomoFriendsManager;
    }
}
