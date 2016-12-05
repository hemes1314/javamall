package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

public class VirtualProductLog implements Serializable {

    private static final long serialVersionUID = 2230659079633194084L;
    
    private Integer id;
    private Integer sender;
    private Integer receiver;
    private Integer virtual_product_id;
    
    @PrimaryKeyField
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getSender() {
        return sender;
    }
    
    public void setSender(Integer sender) {
        this.sender = sender;
    }
    
    public Integer getReceiver() {
        return receiver;
    }
    
    public void setReceiver(Integer receiver) {
        this.receiver = receiver;
    }
    
    public Integer getVirtual_product_id() {
        return virtual_product_id;
    }
    
    public void setVirtual_product_id(Integer virtual_product_id) {
        this.virtual_product_id = virtual_product_id;
    }

}
