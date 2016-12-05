package com.enation.app.base.core.service.impl;

import com.enation.app.base.core.plugin.job.JobExecutePluginsBundle;
import com.enation.app.base.core.service.IJobExecuter;
import com.enation.app.shop.core.model.Auction;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.impl.AuctionManager;
import com.enation.app.shop.core.service.impl.OrderManager;
import com.enation.eop.sdk.database.BaseSupport;

/**
 * 任务执行器
 * @author kingapex
 *
 */ 
public class JobExecuter extends BaseSupport<Auction> implements IJobExecuter  {
	private JobExecutePluginsBundle jobExecutePluginsBundle;
	private OrderManager orderManager;
	private Order order;
	private AuctionManager auctionManager;

	public JobExecuter(){
	    auctionManager = new AuctionManager();
	}
	
	@Override
	public void everyMinutes() {
	    try{
    	    jobExecutePluginsBundle.everyMinutesExcecute();
    		//auctionSchedule();
	    }catch(Exception e){
            e.printStackTrace();
        }
	}

	
	@Override
	public void everyHour() {
	    try{
	        jobExecutePluginsBundle.everyHourExcecute();
	    }catch(Exception e){
            e.printStackTrace();
        }
	}

	@Override 
	public void everyDay() {
		try{
			this.jobExecutePluginsBundle.everyDayExcecute();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void everyMonth() {
	    try {
	        jobExecutePluginsBundle.everyMonthExcecute();
	    }catch(Exception e){
            e.printStackTrace();
        }
	}
	
	@Override
    public void everyMonthFifteen() {
	    try {
            jobExecutePluginsBundle.everyMonthFifteenExcecute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

	public JobExecutePluginsBundle getJobExecutePluginsBundle() {
		return jobExecutePluginsBundle;
	}

	public void setJobExecutePluginsBundle(
			JobExecutePluginsBundle jobExecutePluginsBundle) {
		this.jobExecutePluginsBundle = jobExecutePluginsBundle;
	}
	
/*	public void  auctionSchedule()
	{        
            Date date=new Date();
            DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String cutime=format.format(date); 
            String sql = "select * from es_auction  order by id";
            System.out.println(sql);
            List<Auction> auctionlist = this.baseDaoSupport.queryForList(sql, Auction.class);
            for(Auction cur:auctionlist)
            {
                String overtime = cur.getTime();
                System.out.println("cutime"+cutime);

                SimpleDateFormat dtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long otime = 0;
                long ctime = 0;
                try {
                    java.util.Date dateover = dtime.parse(overtime);
                    java.util.Date datecur = dtime.parse(cutime);
                    otime = dateover.getTime();
                    ctime = datecur.getTime();
                } catch(ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
                //如果当前时间大于截止时间
                if(ctime>otime)
                {
                    String a = cur.getTitle()+":拍卖结束"+",当前价格："+cur.getCprice();
                    System.out.println(a);
                    cur.setStatus("2");
                    String sqlup = "update es_auction set status='2' where id="+cur.getId();
                    this.baseDaoSupport.execute(sqlup);
                    Order order = new Order();
                    order.setAddress_id(13);
                    order.setMember_id(36);
                    order.setSn("1511246153");
                    //order.setOrderprice(orderPrice);
                    //orderManager.add(order);
                    System.out.println(a);
                }
            }
        }*/
	
        public OrderManager getOrderManager() {
            return orderManager;
        }
    
        public void setOrderManager(OrderManager orderManager) {
            this.orderManager = orderManager;
        }

        public Order getOrder() {
            return order;
        }

        public void setOrder(Order order) {
            this.order = order;
        }
}
