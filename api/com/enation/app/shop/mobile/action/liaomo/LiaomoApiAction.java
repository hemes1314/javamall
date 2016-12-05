package com.enation.app.shop.mobile.action.liaomo;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.impl.MemberManager;
import com.enation.app.shop.core.service.impl.MemberVitemManager;
import com.enation.app.shop.mobile.model.LiaomoCircleReply;
import com.enation.app.shop.mobile.model.LiaomoFriend;
import com.enation.app.shop.mobile.model.LiaomoGroup;
import com.enation.app.shop.mobile.service.impl.LiaomoCircleManager;
import com.enation.app.shop.mobile.service.impl.LiaomoCircleReplyManager;
import com.enation.app.shop.mobile.service.impl.LiaomoFriendsManager;
import com.enation.app.shop.mobile.service.impl.LiaomoGroupManager;
import com.enation.app.shop.mobile.service.impl.LiaomoGroupMemberManager;
import com.enation.app.shop.mobile.service.impl.LiaomoMessageManager;
import com.enation.app.shop.mobile.service.impl.VitemBonusManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonMessageUtil;

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/api/mobile")
@Action("liaomo")
public class LiaomoApiAction extends WWAction {

    private static final long serialVersionUID = -5605924217547526205L;
    
    private Long memberId;
    private String token;
    private Long friendId;
    private String mobile;
    private Long sender;
    private Integer contentType;
    private String content;
    private String length;
    private Long groupId;
    private Long receiver;
    private Long creator;
    private String name;
    private File avator;
    private String avatorFileName;
    private String description;
    private String memberIds;
    private File[] circleImage;
    private String[] circleImageFileName;
    private Integer circleId;
    private String reply;
    private File contentFile;
    private String contentFileFileName;
    private Long messageId;
    private Long itemId;
    private Integer sex;
    private String nickname;
    private String birthday;
    private String hometown;
    private String hobby;
    private String email;
    private String keywords;
    private Long itemCount;
    private Long bonusCount;
    private Long vitemBonusId;
    private Integer mode;

    private boolean agreed;
    private LiaomoFriendsManager liaomoFriendsManager;
    private LiaomoGroupManager liaomoGroupManager;
    private LiaomoGroupMemberManager liaomoGroupMemberManager;
    private MemberManager memberManager;
    private LiaomoCircleManager liaomoCircleManager;
    private LiaomoCircleReplyManager liaomoCircleReplyManager;
    private LiaomoMessageManager liaomoMessageManager;
    private MemberVitemManager memberVitemManager;
    private VitemBonusManager vitemBonusManager;
    
    /**
     * 绑定信鸽账号
     * 
     */
    public String setXingeAccount() {
        try {
            memberManager.setXingeAccount(memberId, token);
            this.showPlainSuccessJson("信鸽账号绑定成功");
        } catch (Exception e) {
            e.printStackTrace();
            this.showPlainErrorJson("信鸽账号绑定失败");
        }
        return this.JSON_MESSAGE;
    }
    
    /**
     * 获取好友列表
     * 
     */
    @SuppressWarnings("rawtypes")
    public String friends() {
        try {
            Map<String, Object> friends = liaomoFriendsManager.getFriends(memberId);
            Map<String, Object> groups = liaomoGroupManager.getByMemberId(memberId);
            
            Map<String, Map> returnMap = new HashMap<String, Map>();
            returnMap.put("friends", friends);
            returnMap.put("groups", groups);
            this.json = JsonMessageUtil.getMobileObjectJson(returnMap);
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("获取朋友列表出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 搜索会员列表
     * 
     */
    public String searchFriends() {
        try {
            List<Map> memberList = memberManager.searchByMobile(mobile);
            this.json = JsonMessageUtil.getMobileListJson(memberList);
        } catch (Exception e) {
            e.printStackTrace();
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 查询会员详细信息
     * 
     */
    @SuppressWarnings("rawtypes")
    public String getMember() {
        try {
            Map member = memberManager.getLiaomoMember(memberId, friendId);
            this.json = JsonMessageUtil.getMobileObjectJson(member);
        } catch (Exception e) {
            e.printStackTrace();
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 修改会员信息
     * 
     */
    public String updateMember() {
        try {
           memberManager.updateLiaomoMember(memberId, sex, nickname, birthday, hometown, 
                   hobby, email, avator, avatorFileName);
           this.showPlainSuccessJson("会员修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            this.showPlainErrorJson("会员修改失败");
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 添加好友
     * 
     */
    public String addFriend() {
        try {
            if (memberId != null && memberId.equals(friendId)) {
                this.showPlainErrorJson("不可以添加自己为好友");
            } else {
                List<LiaomoFriend> list = liaomoFriendsManager.getByMemberAndFriend(memberId, friendId);
                if (list != null && !list.isEmpty() && list.get(0).getStatus() == LiaomoFriend.STATUS_OK) {
                    this.showPlainErrorJson("好友已存在");
                } else {
                    liaomoFriendsManager.addFriend(memberId, friendId);
                    this.showPlainSuccessJson("添加成功");
                }
            }

        } catch (NullPointerException npe) {
            this.showPlainErrorJson("找不到该用户");
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("添加好友出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }

    /**
     * 确认或者拒绝好友
     *
     */
    public String confirmFriend() {
        try {
            if (memberId != null && memberId.equals(friendId)) {
                this.showPlainErrorJson("不可以添加自己为好友");
            } else {
                liaomoFriendsManager.confirmFriend(memberId, friendId, agreed);
                if (agreed) {
                    this.showPlainErrorJson("同意添加好友成功");
                } else {
                    this.showPlainSuccessJson("拒绝添加好友成功");
                }
            }

        } catch (NullPointerException npe) {
            this.showPlainErrorJson("找不到该用户");
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("确认好友出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }


    /**
     * 删除好友
     * 
     */
    public String delFriend() {
        try {
            liaomoFriendsManager.delFriend(memberId, friendId);
            this.showPlainSuccessJson("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("删除好友出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 添加分组
     * 
     */
    public String addGroup() {
        try {
            String uploadAvator = null;
            
            if (avator != null) {
                uploadAvator = UploadUtil.upload(avator, avatorFileName, "liaomo");
            }
            
            long groupId = liaomoGroupManager.addGroup(name, uploadAvator, description, creator);
            Map<String, String> map = new HashMap<String, String>();
            map.put("group_id", String.valueOf(groupId));
            this.json = JsonMessageUtil.getMobileObjectJson(map);
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("删除好友出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 分组加入朋友
     * 
     */
    public String addGroupFriend() {
        try {
            liaomoGroupMemberManager.addGroupMember(groupId, memberIds);
            this.showPlainSuccessJson("加入组成功");
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("加入组出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 退出分组
     * 
     */
    public String delGroupFriend() {
        try {
            liaomoGroupMemberManager.delGroupMember(groupId, memberId);
            this.showPlainSuccessJson("退出组成功");
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("退出组出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 获取分组详细信息
     * 
     */
    public String getGroup() {
        try {
            LiaomoGroup liaomoGroup = liaomoGroupManager.get(memberId, groupId);
            this.json = JsonMessageUtil.getMobileObjectJson(liaomoGroup);
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("退出组出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 修改分组信息
     * 
     */
    public String updateGroup() {
        try {
            String uploadAvator = null;
            if (avator != null) {
                uploadAvator = UploadUtil.upload(avator, avatorFileName, "liaomo");
            }
            
            LiaomoGroup liaomoGroup = liaomoGroupManager.get(memberId, groupId);
            liaomoGroup.setName(name);
            liaomoGroup.setAvator(uploadAvator);
            liaomoGroup.setDescription(description);
            liaomoGroupManager.update(liaomoGroup);
            
            if (liaomoGroup.getAvator() != null) {
                liaomoGroup.setAvator(UploadUtil.replacePath(liaomoGroup.getAvator()));
            }
            
            this.json = JsonMessageUtil.getMobileObjectJson(liaomoGroup);
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("修改组出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 获取分组朋友
     * 
     */
    @SuppressWarnings("rawtypes")
    public String getGroupFriend() {
        try {
            List<Map> list = liaomoGroupMemberManager.getByGroupId(groupId);
            this.json = JsonMessageUtil.getMobileListJson(list);
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("获取朋友圈朋友出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 搜索分组
     * 
     */
    public String searchGroup() {
        try {
            List<LiaomoGroup> list = liaomoGroupManager.search(keywords);
            this.json = JsonMessageUtil.getMobileListJson(list);
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("获取朋友圈朋友出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 发送沫圈信息
     * 
     */
    public String sendCircle() {
        try {
            liaomoCircleManager.addCircle(memberId, content, circleImage, circleImageFileName);
            this.showPlainSuccessJson("发送沫圈成功");
        } catch (Exception e) {
            e.printStackTrace();
            this.showPlainErrorJson("发送沫圈失败");
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 查询沫圈信息
     * 
     */
    @SuppressWarnings({ "unchecked" })
    public String getCircle() {
        try {
            Page pages = liaomoCircleManager.getList(memberId, circleId, page, pageSize);
            List<Map<String, Object>> returnList = (List<Map<String, Object>>) pages.getResult();
            this.json = JsonMessageUtil.getMobileListJson(returnList);
        } catch (Exception e) {
            e.printStackTrace();
            this.showPlainErrorJson("获取沫圈消息失败");
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 发送沫圈回复信息
     * 
     */
    public String sendCircleReply() {
        try {
            List<LiaomoCircleReply> list = liaomoCircleReplyManager.sendReply(circleId, reply);
            this.json = JsonMessageUtil.getMobileListJson(list);
        } catch (Exception e) {
            e.printStackTrace();
            this.showPlainErrorJson("回复失败");
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 发送消息
     * 
     */
    public String sendMessage() {
        try {
            int messageId =  liaomoMessageManager.sendMessage(mode, sender, contentType, content,
                    contentFile, contentFileFileName, groupId, receiver, length);
            Map<String, Integer> returnMap = new HashMap<String, Integer>();
            returnMap.put("id", messageId);
            this.json = JsonMessageUtil.getMobileObjectJson(returnMap);
        } catch (Exception e) {
            e.printStackTrace();
            this.showPlainErrorJson("发送消息失败");
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 设置已读消息
     * 
     */
    public String setReadMessage() {
        try {
            liaomoMessageManager.setReadMessage(sender, receiver, groupId, messageId);
            this.showPlainSuccessJson("设置已读消息成功");
        } catch (Exception e) {
            e.printStackTrace();
            this.showPlainErrorJson("设置已读消息失败");
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 获取未读消息
     * 
     */
    @SuppressWarnings("rawtypes")
    public String getUnreadMessage() {
        try {
            List<Map> list = liaomoMessageManager.getUnreadMessage(memberId, friendId);
            this.json = JsonMessageUtil.getMobileListJson(list);
        } catch (Exception e) {
            e.printStackTrace();
            this.showPlainErrorJson("获取未读消息失败");
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 获取历史消息
     * 
     */
    public String getHistoryMessage() {
        try {
            List<Map<String, String>> list = liaomoMessageManager.getHistoryMessage(messageId, memberId, friendId, groupId);
            this.json = JsonMessageUtil.getMobileListJson(list);
        } catch(Exception e) {
            e.printStackTrace();
            this.showPlainErrorJson("历史消息失败");
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 获取虚拟物品
     * 
     */
    @SuppressWarnings("rawtypes")
    public String getVitem() {
        try {
            List<Map> list = memberVitemManager.getByMemberId(memberId);
            this.json = JsonMessageUtil.getMobileListJson(list);
        } catch(Exception e) {
            e.printStackTrace();
            this.showPlainErrorJson("获取虚拟物品失败");
        }
        return this.JSON_MESSAGE;
    }
    
    
    /**
     * 赠送虚拟物品
     * 
     */
    @SuppressWarnings("rawtypes")
    public String giveVitem() {
        try {
            memberVitemManager.give(Integer.valueOf(sender.toString()), Integer.valueOf(receiver.toString()), 
                    Integer.valueOf(itemId.toString()));
            List<Map> list = memberVitemManager.getByMemberId(sender);
            this.json = JsonMessageUtil.getMobileListJson(list);
        } catch(Exception e) {
            e.printStackTrace();
            this.showPlainErrorJson("赠送虚拟物品失败");
        }
        return this.JSON_MESSAGE;
    }
    
    /**
     * 获取用户头像
     * 
     */
    public String getFace() {
        try {
            Member member = memberManager.get(NumberUtils.toInt(memberId.toString()));
            String face = member.getFace();
            
            if (face != null && !face.isEmpty()) {
                face = UploadUtil.replacePath(face);
            }
            
            Map<String, String> faceMap = new HashMap<String, String>();
            faceMap.put("face", face);
            this.json = JsonMessageUtil.getMobileListJson(faceMap);
        } catch (Exception e) {
            e.printStackTrace();
            this.showPlainErrorJson("获取用户头像失败");
        }
        
        return this.JSON_MESSAGE;
    }
    
    /**
     * 最近聊天消息接口
     * 
     */
    @SuppressWarnings("rawtypes")
    public String getRecent() {
        try {
            List<Map<String, Object>> list = liaomoMessageManager.getRecentMessage(memberId);
            this.json = JsonMessageUtil.getMobileListJson(list);
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("获取最近消息接口出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }
    
    /**
     * 发送虚拟物品红包
     * 
     */
    public String sendBonus() {
        try {
            if (itemCount < bonusCount) {
                this.showPlainErrorJson("虚拟物品数量必须大于红包数量");
                return this.JSON_MESSAGE;
            }
            
            Map<String, Long> sendBonusMap = vitemBonusManager.addVitemBonus(mode, sender, itemId, groupId, itemCount, bonusCount);
            
            if (sendBonusMap.get("add_bonus") == -1) {
                this.showPlainErrorJson("虚拟物品数量不足");
                return this.JSON_MESSAGE;
            }
            
            this.json = JsonMessageUtil.getMobileObjectJson(sendBonusMap);
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("发送红包出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }
    
    /**
     * 抢红包
     * 
     */
    @SuppressWarnings("rawtypes")
    public String grabBonus() {
        try {
            int grabValue = vitemBonusManager.grabVitemBonus(vitemBonusId, memberId);
            
            if (grabValue == 1) {
                this.showPlainSuccessJson("抢红包成功");
            } else if (grabValue == 0) {
                this.showPlainSuccessJson("红包已抢完");
            } else {
                this.showPlainSuccessJson("已抢过红包");
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("抢红包出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }
    
    /**
     * 获取红包抢的记录
     * 
     */
    @SuppressWarnings("rawtypes")
    public String getBonus() {
        try {
            this.json = JsonMessageUtil.getMobileListJson(vitemBonusManager.getVitemBonusLog(vitemBonusId));
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("获取红包记录出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return this.JSON_MESSAGE;
    }
    
    public Long getMemberId() {
        return memberId;
    }
    
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
    
    public String getToken() {
        return token;
    }

    
    public void setToken(String token) {
        this.token = token;
    }

    public Long getFriendId() {
        return friendId;
    }
    
    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }
    
    public String getMobile() {
        return mobile;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public Long getSender() {
        return sender;
    }
    
    public void setSender(Long sender) {
        this.sender = sender;
    }
    
    public Integer getContentType() {
        return contentType;
    }
    
    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getGroupId() {
        return groupId;
    }
    
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    
    public Long getReceiver() {
        return receiver;
    }
    
    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }
    
    public Long getCreator() {
        return creator;
    }
    
    public void setCreator(Long creator) {
        this.creator = creator;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public File getAvator() {
        return avator;
    }
    
    public void setAvator(File avator) {
        this.avator = avator;
    }
   
    public String getAvatorFileName() {
        return avatorFileName;
    }
    
    public void setAvatorFileName(String avatorFileName) {
        this.avatorFileName = avatorFileName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getMemberIds() {
        return memberIds;
    }

    
    public void setMemberIds(String memberIds) {
        this.memberIds = memberIds;
    }
    
    public File[] getCircleImage() {
        return circleImage;
    }
    
    public void setCircleImage(File[] circleImage) {
        this.circleImage = circleImage;
    }
    
    public String[] getCircleImageFileName() {
        return circleImageFileName;
    }
    
    public void setCircleImageFileName(String[] circleImageFileName) {
        this.circleImageFileName = circleImageFileName;
    }
    
    public Integer getCircleId() {
        return circleId;
    }
    
    public void setCircleId(Integer circleId) {
        this.circleId = circleId;
    }
    
    public String getReply() {
        return reply;
    }
    
    public void setReply(String reply) {
        this.reply = reply;
    }
    
    public File getContentFile() {
        return contentFile;
    }
    
    public void setContentFile(File contentFile) {
        this.contentFile = contentFile;
    }
    
    public String getContentFileFileName() {
        return contentFileFileName;
    }
    
    public void setContentFileFileName(String contentFileFileName) {
        this.contentFileFileName = contentFileFileName;
    }
    
    public Long getMessageId() {
        return messageId;
    }
    
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
    
    public Long getItemId() {
        return itemId;
    }

    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public LiaomoFriendsManager getLiaomoFriendsManager() {
        return liaomoFriendsManager;
    }
    
    public void setLiaomoFriendsManager(LiaomoFriendsManager liaomoFriendsManager) {
        this.liaomoFriendsManager = liaomoFriendsManager;
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
    
    public LiaomoCircleManager getLiaomoCircleManager() {
        return liaomoCircleManager;
    }
    
    public void setLiaomoCircleManager(LiaomoCircleManager liaomoCircleManager) {
        this.liaomoCircleManager = liaomoCircleManager;
    }
    
    public LiaomoCircleReplyManager getLiaomoCircleReplyManager() {
        return liaomoCircleReplyManager;
    }
    
    public void setLiaomoCircleReplyManager(LiaomoCircleReplyManager liaomoCircleReplyManager) {
        this.liaomoCircleReplyManager = liaomoCircleReplyManager;
    }
    
    public LiaomoMessageManager getLiaomoMessageManager() {
        return liaomoMessageManager;
    }
    
    public void setLiaomoMessageManager(LiaomoMessageManager liaomoMessageManager) {
        this.liaomoMessageManager = liaomoMessageManager;
    }
    
    public MemberVitemManager getMemberVitemManager() {
        return memberVitemManager;
    }
    
    public void setMemberVitemManager(MemberVitemManager memberVitemManager) {
        this.memberVitemManager = memberVitemManager;
    }
    
    public Integer getSex() {
        return sex;
    }
    
    public void setSex(Integer sex) {
        this.sex = sex;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getBirthday() {
        return birthday;
    }
    
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    
    public String getHometown() {
        return hometown;
    }
    
    public void setHometown(String hometown) {
        this.hometown = hometown;
    }
    
    public String getHobby() {
        return hobby;
    }
    
    public void setHobby(String hobby) {
        this.hobby = hobby;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getKeywords() {
        return keywords;
    }
    
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
    
    public String getLength() {
        return length;
    }
    
    public void setLength(String length) {
        this.length = length;
    }
    
    public VitemBonusManager getVitemBonusManager() {
        return vitemBonusManager;
    }
    
    public void setVitemBonusManager(VitemBonusManager vitemBonusManager) {
        this.vitemBonusManager = vitemBonusManager;
    }

    public boolean isAgreed() {
        return agreed;
    }

    public void setAgreed(boolean agreed) {
        this.agreed = agreed;
    }

    public Long getItemCount() {
        return itemCount;
    }

    
    public void setItemCount(Long itemCount) {
        this.itemCount = itemCount;
    }

    
    public Long getBonusCount() {
        return bonusCount;
    }

    
    public void setBonusCount(Long bonusCount) {
        this.bonusCount = bonusCount;
    }

    
    public Long getVitemBonusId() {
        return vitemBonusId;
    }
    
    public void setVitemBonusId(Long vitemBonusId) {
        this.vitemBonusId = vitemBonusId;
    }
    
    public Integer getMode() {
        return mode;
    }
    
    public void setMode(Integer mode) {
        this.mode = mode;
    }
    
}
