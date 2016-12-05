package com.enation.app.shop.mobile.service.impl;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.impl.MemberManager;
import com.tencent.xinge.ClickAction;
import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created by ken on 16/3/11.
 */
@Component
public class PushManager {
    public static final long accessId = 2200185285L;
    public static final String secretKey = "2ebca3d523a731f158dd670a3837cde2";
    public static final long androidAccessId = 2100185229L;
    public static final String androidSecretKey = "298bb9ef295b83d7e11e9048725b4e26";

    /**
     * 聊天消息
     */
    public static final int TYPE_MESSAGE = 1;

    /**
     * 群聊天消息
     */
    public static final int TYPE_GROUP_MESSAGE = 2;

    /**
     * 添加好友的消息
     */
    public static final int TYPE_FRIEND_ADD = 3;

    /**
     * 确认的消息
     */
    public static final int TYPE_FRIEND_ACCEPT = 4;

    /**
     * 拒绝的消息
     */
    public static final int TYPE_FRIEND_REJECT = 5;

    /**
     * 删除的消息
     */
    public static final int TYPE_FRIEND_DELETE = 6;

    /**
     * 加入群的消息
     */
    public static final int TYPE_GROUP_ADD = 7;

    /**
     * 退群的时候的消息
     */
    public static final int TYPE_GROUP_DELETE = 8;


    private MemberManager memberManager;
    private LiaomoGroupMemberManager liaomoGroupMemberManager;
    private LiaomoGroupManager liaomoGroupManager;
    private LiaomoFriendsManager liaomoFriendsManager;

    /**
     * 专门处理推送的,添加好友,删除好友,同意添加
     *
     * @param content
     * @param custom
     * @param groupId 群相关的没有了
     */
    @Transactional(readOnly = true)
    public void doPush(long sender, long receiver, String content, Map<String, Object> custom, String androidActivity, Long groupId) {
        if (sender == receiver) {
            throw new RuntimeException("不可以给自己发消息");
        }
        //IOS推送
        //*
        MessageIOS mess = new MessageIOS();
        mess.setAlert(content);
        mess.setBadge(1);
        mess.setSound("beep.wav");
        mess.setCustom(custom);
        XingeApp push = new XingeApp(PushManager.accessId, PushManager.secretKey);

        //android推送
        Message messAndroid = new Message();
        messAndroid.setExpireTime(86400);
        messAndroid.setContent(content);
        messAndroid.setType(Message.TYPE_NOTIFICATION);
        messAndroid.setCustom(custom);

        if (StringUtils.isNotBlank(androidActivity)) {
            ClickAction action = new ClickAction();
            action.setActionType(ClickAction.TYPE_ACTIVITY);
            action.setActivity(androidActivity);
            messAndroid.setAction(action);
        }

        XingeApp androidPush = new XingeApp(PushManager.androidAccessId, PushManager.androidSecretKey);
        //*/
        if (groupId != null) {
            custom.put("group_id", String.valueOf(groupId));


        } else {
            Member receiverMember = memberManager.get((int)receiver);

            if (receiverMember != null && receiverMember.getXinge_account() != null) {
                String xingeAccount = receiverMember.getXinge_account();
                String[] splitArray = xingeAccount.split("_");

                //过滤xingeAccount是2_的数据，这样的数据会报错
                if (splitArray.length == 2) {
                    if (splitArray[0].equals("1")) {
                        int returnCode = push.pushSingleDevice(splitArray[1], mess, XingeApp.IOSENV_PROD).getInt("ret_code");
                        //System.out.println("tension--iosPushSingleDevice---" + returnCode);
                    } else {
                        int returnCode = androidPush.pushSingleDevice(splitArray[1], messAndroid).getInt("ret_code");
                        //System.out.println("tension--androidPushSingleDevice---" + returnCode);
                    }
                }
            }


        }


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
