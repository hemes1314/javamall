package com.enation.app.b2b2c.component.plugin.store;

/**
 * 店铺禁用事件
 * @author Sylow
 * @version v1.0,2015-11-08
 * @since v5.1
 */
public interface IStoreDisEvent {
    
    /**
     * 禁用店铺之后函数
     * @param storeId 店铺id
     */
    public void onAfterStoreDis(int storeId);
    
}
