package com.enation.app.b2b2ccostdown.component.act;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2ccostdown.component.CostDownPluginBundle;
import com.enation.app.b2b2ccostdown.core.service.CostDownActiveManager;
import com.enation.app.b2b2ccostdown.core.service.CostDownManager;
import com.enation.app.base.core.plugin.job.IEveryDayExecuteEvent;
import com.enation.app.base.core.plugin.job.IEveryHourExecuteEvent;
import com.enation.app.base.core.plugin.job.IEveryMinutesExecuteEvent;
import com.enation.app.flashbuy.component.plugin.FlashbuyPluginBundle;
import com.enation.app.flashbuy.core.service.IFlashBuyActiveManager;
import com.enation.app.flashbuy.core.service.IFlashBuyManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

/**
 * 
 * @ClassName: FlashBuyActPlugin
 * @Description: 限时抢购活动插件
 * @author humaodong
 * @date 2015-7-31 上午10:37:41
 *
 */
@Component
public class CostDownActPlugin extends AutoRegisterPlugin implements IEveryMinutesExecuteEvent {

    @Autowired
    private IDaoSupport<?> daoSupport;

    @Autowired
    private CostDownManager costDownManager;

    @Autowired
    private CostDownActiveManager costDownActiveManager;

    private CostDownPluginBundle costDownPluginBundle;

    /**
     * 当限时抢购达到结束时间就关闭 当限时抢购达到开始时间就开启
     */
    @Override
    public void everyMinutes() {
        // 2015/11/4 humaodong
        //查询是否有需要开启的
        try {
            String sql = "SELECT act_id FROM es_cost_down_active WHERE act_status=0 AND start_time<? AND rownum<=1";
            Integer actId = this.daoSupport.queryForInt(sql, DateUtil.getDateline());
            //开启
            System.out.println("start flashbuy act..." + actId);
            sql = "UPDATE es_cost_down_active SET act_status=1  WHERE act_id=?";
            this.daoSupport.execute(sql, actId);
            costDownPluginBundle.onCostDownStart(actId);
        } catch(Exception e) {
        }
        //查询是否有需要关闭的
        try {
            String sql = "SELECT act_id FROM es_cost_down_active WHERE act_status=1 AND end_time<? AND rownum<=1";
            Integer actId = this.daoSupport.queryForInt(sql, DateUtil.getDateline());
            //关闭
            System.out.println("end flashbuy act..." + actId);
            sql = "UPDATE es_cost_down_active SET act_status=2  WHERE act_id=?";
            this.daoSupport.execute(sql, actId);
            costDownPluginBundle.onCostDownEnd(actId);
        } catch(Exception e) {
        }
    }
    
    public CostDownPluginBundle getCostDownPluginBundle() {
        return costDownPluginBundle;
    }
    
    public void setCostDownPluginBundle(CostDownPluginBundle costDownPluginBundle) {
        this.costDownPluginBundle = costDownPluginBundle;
    }
    
}
