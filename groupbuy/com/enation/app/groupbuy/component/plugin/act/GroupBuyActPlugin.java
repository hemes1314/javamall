package com.enation.app.groupbuy.component.plugin.act;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.job.IEveryDayExecuteEvent;
import com.enation.app.base.core.plugin.job.IEveryHourExecuteEvent;
import com.enation.app.base.core.plugin.job.IEveryMinutesExecuteEvent;
import com.enation.app.groupbuy.component.plugin.GroupbuyPluginBundle;
import com.enation.app.groupbuy.core.service.IGroupBuyActiveManager;
import com.enation.app.groupbuy.core.service.IGroupBuyManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;
/**
 * 
 * @ClassName: GroupBuyActPlugin 
 * @Description: 团购活动插件 
 * @author TALON 
 * @date 2015-7-31 上午10:37:41 
 *
 */
@Component
public class GroupBuyActPlugin extends AutoRegisterPlugin implements IEveryMinutesExecuteEvent{
	private IDaoSupport daoSupport;
	private IGroupBuyManager groupBuyManager;
	private IGroupBuyActiveManager groupBuyActiveManager;
	private GroupbuyPluginBundle groupbuyPluginBundle;
	/**
	 * 当团购达到结束时间就关闭团购
	 * 当团购达到开始时间就开启团购
	 */
	@Override
	public void everyMinutes() {
	    // 2015/11/4 humaodong
        //查询是否有需要开启的团购
	    try {
    	    String sql = "SELECT act_id FROM es_groupbuy_active WHERE act_status=0 AND start_time<? AND rownum<=1";
            Integer actId = this.daoSupport.queryForInt(sql, DateUtil.getDateline());
            //开启团购
            System.out.println("start groupbuy act..."+actId);
            sql="UPDATE es_groupbuy_active SET act_status=1  WHERE act_id=?";
            this.daoSupport.execute(sql, actId);
            groupbuyPluginBundle.onGroupBuyStart(actId);
        } catch (Exception e) {
        }
        //查询是否有需要关闭的团购
	    try {
	        String sql = "SELECT act_id FROM es_groupbuy_active WHERE act_status=1 AND end_time<? AND rownum<=1";
	        Integer actId = this.daoSupport.queryForInt(sql, DateUtil.getDateline());
            //关闭团购
            System.out.println("end groupbuy act..."+actId);
            sql="UPDATE es_groupbuy_active SET act_status=2  WHERE act_id=?";
            this.daoSupport.execute(sql, actId);
            groupbuyPluginBundle.onGroupBuyEnd(actId);
        } catch (Exception e) {
        }
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IGroupBuyManager getGroupBuyManager() {
		return groupBuyManager;
	}
	public void setGroupBuyManager(IGroupBuyManager groupBuyManager) {
		this.groupBuyManager = groupBuyManager;
	}
	public IGroupBuyActiveManager getGroupBuyActiveManager() {
		return groupBuyActiveManager;
	}
	public void setGroupBuyActiveManager(
			IGroupBuyActiveManager groupBuyActiveManager) {
		this.groupBuyActiveManager = groupBuyActiveManager;
	}
	public GroupbuyPluginBundle getGroupbuyPluginBundle() {
		return groupbuyPluginBundle;
	}
	public void setGroupbuyPluginBundle(GroupbuyPluginBundle groupbuyPluginBundle) {
		this.groupbuyPluginBundle = groupbuyPluginBundle;
	}
}
