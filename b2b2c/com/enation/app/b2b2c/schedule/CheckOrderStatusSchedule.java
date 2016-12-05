package com.enation.app.b2b2c.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IOrderManager;

/**
 * 检测订单状态
 * @author Jeffrey
 *
 */
@Component
public class CheckOrderStatusSchedule extends QuartzJobBean {

    private Log log = LogFactory.getLog(CheckOrderStatusSchedule.class);
    
    @Autowired
    private IOrderManager orderManager;

    protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
        
        //超过24小时 
        //超过30小时
        //超过15天
        log.info("dennis test execute schedule start ");
        try {
            orderManager.updateStatus();
        } catch(Exception e) {
            e.printStackTrace();
            log.error("execute schedule error:" + e.getMessage());
        }
        log.info("dennis test exxcute schedule end");
    }

    
}
