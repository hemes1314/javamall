package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

public class ActivityProduct implements Serializable {

    private static final long serialVersionUID = -6762046777676039316L;
    
    private int id;
    private int activityId;
    private int productId;
    
    @PrimaryKeyField
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getActivityId() {
        return activityId;
    }
    
    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    

}
