package com.enation.app.shop.mobile.model;

import java.io.Serializable;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

public class LiaomoGroup implements Serializable {

    private static final long serialVersionUID = -1429962406863713330L;

    private Long id;
    private String name;
    private String avator;
    private String description;
    private Long creator;
    private Long count;
    private String sn;
    private Integer in_group;
    
    private String creator_name;
    private String caeator_face;
    
    @PrimaryKeyField
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAvator() {
        return avator;
    }
    
    public void setAvator(String avator) {
        this.avator = avator;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getCreator() {
        return creator;
    }
    
    public void setCreator(Long creator) {
        this.creator = creator;
    }
    
    public Long getCount() {
        return count;
    }
    
    public void setCount(Long count) {
        this.count = count;
    }
    
    public String getSn() {
        return sn;
    }
    
    public void setSn(String sn) {
        this.sn = sn;
    }

    @NotDbField
    public Integer getIn_group() {
        return in_group;
    }
    
    public void setIn_group(Integer in_group) {
        this.in_group = in_group;
    }
    
    public String getCreator_name() {
        return creator_name;
    }
    
    public void setCreator_name(String creator_name) {
        this.creator_name = creator_name;
    }
    
    public String getCaeator_face() {
        return caeator_face;
    }
    
    public void setCaeator_face(String caeator_face) {
        this.caeator_face = caeator_face;
    }
    
}
