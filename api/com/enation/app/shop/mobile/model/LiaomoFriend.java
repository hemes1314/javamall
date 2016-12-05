package com.enation.app.shop.mobile.model;

import java.io.Serializable;

public class LiaomoFriend implements Serializable {

    private static final long serialVersionUID = 4875770862169237828L;

    public static final int STATUS_REQUESTED_ADD = 1;
    public static final int STATUS_RECEIVED_ADD = 2;
    public static final int STATUS_FAILED_ADD = 11;
    public static final int STATUS_REJECTED_ADD = 12;
    public static final int STATUS_IGNORED_ADD = 13;
    public static final int STATUS_OK = 101;

    private Long id;
    private Long member_id;
    private Long friend_id;
    private Integer status;

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getMember_id() {
        return member_id;
    }
    
    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }
    
    public Long getFriend_id() {
        return friend_id;
    }
    
    public void setFriend_id(Long friend_id) {
        this.friend_id = friend_id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
