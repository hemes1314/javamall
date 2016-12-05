package com.enation.app.shop.core.service.impl;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.VirtualProductLog;
import com.enation.eop.sdk.database.BaseSupport;

@Component
public class VirtualProductLogManager extends BaseSupport<VirtualProductLog> {

    public void addLog(int sender, int receiver, int virtualProductId) {
        VirtualProductLog virtualProductLog = new VirtualProductLog();
        virtualProductLog.setSender(sender);
        virtualProductLog.setReceiver(receiver);
        virtualProductLog.setVirtual_product_id(virtualProductId);
        
        this.baseDaoSupport.insert("es_virtual_product_log", virtualProductLog);
    }
    
}
