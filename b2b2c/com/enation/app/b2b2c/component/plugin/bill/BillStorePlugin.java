package com.enation.app.b2b2c.component.plugin.bill;


import java.util.Date;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.bill.IBillManager;
import com.enation.app.base.core.plugin.job.IEveryMonthFifteenExecuteEvent;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

/**
 * 结算每月执行插件
 *
 * @author fenlongli
 */
@Component
public class BillStorePlugin extends AutoRegisterPlugin implements IEveryMonthFifteenExecuteEvent {
    private IDaoSupport daoSupport;
    private IBillManager billManager;

    /**
     * 每月触发一次结算
     * 修改结算单详情
     */
    @Override
    public void everyMonthFifteen() {
        try {
            //所有上月订单修改状态
            Long start_time = DateUtil.getMonthFirstDay();
            daoSupport.execute("update es_bill_detail set status = 1 where status = 0 and start_time < ?", start_time);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public IDaoSupport getDaoSupport() {
        return daoSupport;
    }

    public void setDaoSupport(IDaoSupport daoSupport) {
        this.daoSupport = daoSupport;
    }

    public IBillManager getBillManager() {
        return billManager;
    }

    public void setBillManager(IBillManager billManager) {
        this.billManager = billManager;
    }

}
