package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.Order;
import com.enation.framework.database.Page;

/**
 * 用户中心-我的众筹
 * @author humaodong<br/>
 * 2010-3-15 上午10:21:45<br/>
 * version 1.0<br/>
 */
public interface IMemberCfManager {
	
    /**
     * 查询会员的所有众筹
     * @param memberid
     * @return
     */
    public Page page(int pageNo,int pageSize, String cfStatus);
    
    public void setStatus(String sn, String recStatus);
}
