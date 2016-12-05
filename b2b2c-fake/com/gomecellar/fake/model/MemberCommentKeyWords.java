package com.gomecellar.fake.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;


/**
 * 商品评论关键字模型
 * @author wangli-tri
 *
 */
public class MemberCommentKeyWords implements Serializable{

    private static final long serialVersionUID = 1L;
    private String id;
    private Integer cat_id;
    
    public Integer getCat_id() {
        return cat_id;
    }
    
    public void setCat_id(Integer cat_id) {
        this.cat_id = cat_id;
    }
    
    public String getCat_name() {
        return cat_name;
    }
    
    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }
    
    public String getKey_words() {
        return key_words;
    }
    
    public void setKey_words(String key_words) {
        this.key_words = key_words;
    }
    
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    private String cat_name;
    private Integer status;
    private String key_words; 
}
