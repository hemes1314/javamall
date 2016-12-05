package com.enation.app.secbuy.component.plugin.act;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.job.IEveryDayExecuteEvent;
import com.enation.app.base.core.plugin.job.IEveryMinutesExecuteEvent;
import com.enation.app.secbuy.component.plugin.SecbuyPluginBundle;
import com.enation.app.secbuy.core.service.ISecBuyActiveManager;
import com.enation.app.secbuy.core.service.ISecBuyManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;
/**
 * 
 * @ClassName: SecBuyActPlugin 
 * @Description: 秒拍活动插件 
 * @author TALON 
 * @date 2015-7-31 上午10:37:41 
 *
 */
@Component
public class SecBuyActPlugin extends AutoRegisterPlugin implements IEveryMinutesExecuteEvent{
	private IDaoSupport daoSupport;
	private ISecBuyManager secBuyManager;
	private ISecBuyActiveManager secBuyActiveManager;
	private SecbuyPluginBundle secbuyPluginBundle;
	/**
	 * 当秒拍达到结束时间就关闭秒拍
	 * 当秒拍达到开始时间就开启秒拍
	 */
	@Override
	public void everyMinutes() {
	    // 2015/11/4 humaodong
        //查询是否有需要开启的
        try {
            String sql = "SELECT act_id FROM es_secbuy_active WHERE act_status=0 AND start_time<? AND rownum<=1";
            Integer actId = this.daoSupport.queryForInt(sql, DateUtil.getDateline());
            //开启
            System.out.println("start secbuy act..."+actId);
            sql="UPDATE es_secbuy_active SET act_status=1  WHERE act_id=?";
            this.daoSupport.execute(sql, actId);
            secbuyPluginBundle.onSecBuyStart(actId);
        } catch (Exception e) {
        }
        //查询是否有需要关闭的
        try {
            String sql = "SELECT act_id FROM es_secbuy_active WHERE act_status=1 AND end_time<? AND rownum<=1";
            Integer actId = this.daoSupport.queryForInt(sql, DateUtil.getDateline());
            //关闭
            System.out.println("end secbuy act..."+actId);
            sql="UPDATE es_secbuy_active SET act_status=2  WHERE act_id=?";
            this.daoSupport.execute(sql, actId);
            secbuyPluginBundle.onSecBuyEnd(actId);
        } catch (Exception e) {
        }
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public ISecBuyManager getSecBuyManager() {
		return secBuyManager;
	}
	public void setSecBuyManager(ISecBuyManager secBuyManager) {
		this.secBuyManager = secBuyManager;
	}
	public ISecBuyActiveManager getSecBuyActiveManager() {
		return secBuyActiveManager;
	}
	public void setSecBuyActiveManager(
			ISecBuyActiveManager secBuyActiveManager) {
		this.secBuyActiveManager = secBuyActiveManager;
	}
	public SecbuyPluginBundle getSecbuyPluginBundle() {
		return secbuyPluginBundle;
	}
	public void setSecbuyPluginBundle(SecbuyPluginBundle secbuyPluginBundle) {
		this.secbuyPluginBundle = secbuyPluginBundle;
	}
}
