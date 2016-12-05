package com.enation.app.shop.core.plugin.timer;


import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.bill.Bill;
import com.enation.app.b2b2c.core.service.bill.IBillManager;
import com.enation.app.base.core.plugin.job.IEveryMinutesExecuteEvent;
import com.enation.app.base.core.plugin.job.IEveryMonthExecuteEvent;
import com.enation.app.shop.core.service.impl.AuctionManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.util.DateUtil;
/**
 * 结算每月执行插件
 * @author fenlongli
 *
 */
@Component
public class TimerPlugin extends AutoRegisterPlugin implements IEveryMinutesExecuteEvent {
	private IDaoSupport daoSupport;
	private AuctionManager auctionManager;
	/**
	 */
	@Override
	public void everyMinutes() {
	    System.out.println("test ");
		try {
		    System.out.println("test ");
			//this.auctionManager.list();
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
	
}
