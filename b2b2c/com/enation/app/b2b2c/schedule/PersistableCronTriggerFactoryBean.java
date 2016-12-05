package com.enation.app.b2b2c.schedule;

import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

public class PersistableCronTriggerFactoryBean extends CronTriggerFactoryBean {

    private Log log = LogFactory.getLog(PersistableCronTriggerFactoryBean.class);
    
    @Override
    public void afterPropertiesSet() {
        try {
            super.afterPropertiesSet();
        } catch(ParseException e) {
            log.error("afterPropertiesSet err", e);
        }
        System.out.println("PersistableCronTriggerFactoryBean-------------------");
        // Remove the JobDetail element
//        getJobDataMap().remove(JobDetailAwareTrigger.JOB_DETAIL_KEY);
        getJobDataMap().remove("JobDetail");
    }

}
