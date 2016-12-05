package com.enation.app.b2b2c.component.plugin.timer;


import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.bill.Bill;
import com.enation.app.b2b2c.core.service.bill.IBillManager;
import com.enation.app.base.core.plugin.job.IEveryHourExecuteEvent;
import com.enation.app.base.core.plugin.job.IEveryMinutesExecuteEvent;
import com.enation.app.base.core.plugin.job.IEveryMonthExecuteEvent;
import com.enation.app.shop.core.service.impl.AuctionManager;
import com.enation.app.shop.core.service.impl.CfManager;
import com.enation.app.shop.core.service.impl.OrderFlowManager;
import com.enation.app.shop.core.service.impl.YuemoManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.util.DateUtil;
/**
 * 对约沫，拍卖，众筹等信息进行定期的状态检查，更新状态
 * @author fenlongli
 *
 */
@Component
public class TimerPlugin extends AutoRegisterPlugin implements IEveryHourExecuteEvent {
	private IDaoSupport daoSupport;
	private AuctionManager auctionManager;
	private CfManager cfManager;
	private YuemoManager yuemoManager;
	private OrderFlowManager orderFlowManager;
	/**
	 */
	@Override
	public void everyHour() {
		try {
		    //更新拍卖的状态
			this.auctionManager.updateStatus();
			//更新众筹状态
			this.cfManager.updateStatus();
			//更新约沫  
			this.yuemoManager.updateStatus();
			//更新物流，发送未提交的物流订单给快递100
			this.orderFlowManager.updateLogiStatus();
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
   
    public AuctionManager getAuctionManager() {
        return auctionManager;
    }
 
    public void setAuctionManager(AuctionManager auctionManager) {
        this.auctionManager = auctionManager;
    }

    public CfManager getCfManager() {
        return cfManager;
    }
 
    public void setCfManager(CfManager cfManager) {
        this.cfManager = cfManager;
    }
 
    public YuemoManager getYuemoManager() {
        return yuemoManager;
    }
  
    public void setYuemoManager(YuemoManager yuemoManager) {
        this.yuemoManager = yuemoManager;
    }


    
    public OrderFlowManager getOrderFlowManager() {
        return orderFlowManager;
    }


    
    public void setOrderFlowManager(OrderFlowManager orderFlowManager) {
        this.orderFlowManager = orderFlowManager;
    }
	  
}
