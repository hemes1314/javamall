package com.enation.app.b2b2c.core.service.OrderReport;

import org.springframework.stereotype.Component;

/**
 * 店铺单据管理
 * @author fenlongli
 *
 */
@Component
public interface IStoreOrderReportManager {
    /**
     * 审核申请
     */
    public void saveAuth(Integer status,Integer id,String seller_remark,String return_address);
    /**
     * 保存物流
     */
    public void saveKdgs(Integer id,String kddh, String wlgs);
    /**
     * 通过并审核
     */
    public void saveAuthtg(Integer status, Integer id, String seller_remark, String return_address);
    
    /**
     * 退款申请审核
     */
    public void tksaveAuthtg(Integer status, Integer id, String seller_remark, String return_address);
    
    /**
     * 用户拒收审核
     */
    public void jssaveAuthtg(Integer status, Integer id, String seller_remark, String return_address);
    
    public void savezydAuthtg(Integer status, Integer id, String seller_remark, String return_address);
}
