package com.enation.app.flashbuy.component.plugin.act;

import org.springframework.stereotype.Component;

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
public class FlashBuyActPlugin extends AutoRegisterPlugin implements IEveryMinutesExecuteEvent{
	private IDaoSupport daoSupport;
	private IFlashBuyManager flashBuyManager;
	private IFlashBuyActiveManager flashBuyActiveManager;
	private FlashbuyPluginBundle flashbuyPluginBundle;
	/**
	 * 当限时抢购达到结束时间就关闭
	 * 当限时抢购达到开始时间就开启
	 */
	@Override
	public void everyMinutes() {
	    // 2015/11/4 humaodong
        //查询是否有需要开启的
        try {
            String sql = "SELECT act_id FROM es_flashbuy_active WHERE act_status=0 AND start_time<? AND rownum<=1";
            Integer actId = this.daoSupport.queryForInt(sql, DateUtil.getDateline());
            //开启
            System.out.println("start flashbuy act..."+actId);
            sql="UPDATE es_flashbuy_active SET act_status=1  WHERE act_id=?";
            this.daoSupport.execute(sql, actId);
            flashbuyPluginBundle.onFlashBuyStart(actId);
        } catch (Exception e) {
        }
        //查询是否有需要关闭的
        try {
            String sql = "SELECT act_id FROM es_flashbuy_active WHERE act_status=1 AND end_time<? AND rownum<=1";
            Integer actId = this.daoSupport.queryForInt(sql, DateUtil.getDateline());
            //关闭
            System.out.println("end flashbuy act..."+actId);
            sql="UPDATE es_flashbuy_active SET act_status=2  WHERE act_id=?";
            this.daoSupport.execute(sql, actId);
            flashbuyPluginBundle.onFlashBuyEnd(actId);
        } catch (Exception e) {
        }
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IFlashBuyManager getFlashBuyManager() {
		return flashBuyManager;
	}
	public void setFlashBuyManager(IFlashBuyManager flashBuyManager) {
		this.flashBuyManager = flashBuyManager;
	}
	public IFlashBuyActiveManager getFlashBuyActiveManager() {
		return flashBuyActiveManager;
	}
	public void setFlashBuyActiveManager(
			IFlashBuyActiveManager flashBuyActiveManager) {
		this.flashBuyActiveManager = flashBuyActiveManager;
	}
	public FlashbuyPluginBundle getFlashbuyPluginBundle() {
		return flashbuyPluginBundle;
	}
	public void setFlashbuyPluginBundle(FlashbuyPluginBundle flashbuyPluginBundle) {
		this.flashbuyPluginBundle = flashbuyPluginBundle;
	}
}
