package com.enation.app.advbuy.component.plugin.act;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.job.IEveryDayExecuteEvent;
import com.enation.app.base.core.plugin.job.IEveryHourExecuteEvent;
import com.enation.app.base.core.plugin.job.IEveryMinutesExecuteEvent;
import com.enation.app.advbuy.component.plugin.AdvbuyPluginBundle;
import com.enation.app.advbuy.core.service.IAdvBuyActiveManager;
import com.enation.app.advbuy.core.service.IAdvBuyManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;
/**
 * 
 * @ClassName: AdvBuyActPlugin 
 * @Description: 预售活动插件 
 * @author TALON 
 * @date 2015-7-31 上午10:37:41 
 *
 */
@Component
public class AdvBuyActPlugin extends AutoRegisterPlugin implements IEveryMinutesExecuteEvent{
	private IDaoSupport daoSupport;
	private IAdvBuyManager advBuyManager;
	private IAdvBuyActiveManager advBuyActiveManager;
	private AdvbuyPluginBundle advbuyPluginBundle;
	/**
	 * 当预售达到结束时间就关闭预售
	 * 当预售达到开始时间就开启预售
	 */
	@Override
	public void everyMinutes() {
	 // 2015/11/4 humaodong
        //查询是否有需要开启的
        try {
            String sql = "SELECT act_id FROM es_advbuy_active WHERE act_status=0 AND start_time<? AND rownum<=1";
            Integer actId = this.daoSupport.queryForInt(sql, DateUtil.getDateline());
            //开启
            System.out.println("start advbuy act..."+actId);
            sql="UPDATE es_advbuy_active SET act_status=1  WHERE act_id=?";
            this.daoSupport.execute(sql, actId);
            advbuyPluginBundle.onAdvBuyStart(actId);
        } catch (Exception e) {
        }
        //查询是否有需要关闭的
        try {
            String sql = "SELECT act_id FROM es_advbuy_active WHERE act_status=1 AND end_time<? AND rownum<=1";
            Integer actId = this.daoSupport.queryForInt(sql, DateUtil.getDateline());
            //关闭
            System.out.println("end advbuy act..."+actId);
            sql="UPDATE es_advbuy_active SET act_status=2  WHERE act_id=?";
            this.daoSupport.execute(sql, actId);
            advbuyPluginBundle.onAdvBuyEnd(actId);
        } catch (Exception e) {
        }
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IAdvBuyManager getAdvBuyManager() {
		return advBuyManager;
	}
	public void setAdvBuyManager(IAdvBuyManager advBuyManager) {
		this.advBuyManager = advBuyManager;
	}
	public IAdvBuyActiveManager getAdvBuyActiveManager() {
		return advBuyActiveManager;
	}
	public void setAdvBuyActiveManager(
			IAdvBuyActiveManager advBuyActiveManager) {
		this.advBuyActiveManager = advBuyActiveManager;
	}
	public AdvbuyPluginBundle getAdvbuyPluginBundle() {
		return advbuyPluginBundle;
	}
	public void setAdvbuyPluginBundle(AdvbuyPluginBundle advbuyPluginBundle) {
		this.advbuyPluginBundle = advbuyPluginBundle;
	}
}
