package com.enation.app.shop.component.statistics.plugin.order;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.poi.hssf.record.chart.DatRecord;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.plugin.order.IOrderStatisDetailHtmlEvent;
import com.enation.app.shop.core.plugin.order.IOrderStatisTabShowEvent;
import com.enation.app.shop.core.service.IStatisticsManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

/**
 * 订单统计—下单金额插件
 * @author xulipeng
 * 2015年05月13日19:22:25
 */

@Component
public class OrderMoneyStatisticsPlugin extends AutoRegisterPlugin implements IOrderStatisTabShowEvent,IOrderStatisDetailHtmlEvent {

	private IStatisticsManager statisticsManager;
	
	@Override
	public String onShowOrderDetailHtml(Map map) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		
		Integer order_status = (Integer) map.get("order_status");
		Integer cycle_type = (Integer) map.get("cycle_type");
		Integer year = (Integer) map.get("year");
		Integer month = (Integer) map.get("month");
		
		if(cycle_type==null){
			cycle_type=0;
		}
		
		Calendar cal = Calendar.getInstance();
		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}
		
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setPageName("order_money");
		freeMarkerPaser.putData("typeid",12);
		
		// 按年周期统计
		if(cycle_type!=null && cycle_type.intValue()==1){
			
//			long start_time = DateUtil.getDateline(data,"yyyy");
//			long end_time = DateUtil.getDateline(year+"-12-31 23:59:59");
			
			//当前年
			//List<Map> thislist = this.statisticsManager.statisticsYear_Amount(order_status, start_time,end_time);
			//去年
			//List<Map> lastlist = this.statisticsManager.statisticsYear_Amount(order_status, start_time,end_time);
			
		}else if(cycle_type!=null && cycle_type.intValue()==0){
			int day = getDaysByYearMonth(year, month);
			long start_time = DateUtil.getDateline(year+"-"+month+"-01 00:00:00");
			long end_time = DateUtil.getDateline(year+"-"+month+"-"+day+" 23:59:59");   
			System.out.println("当前月开始时间："+start_time+",结束时间："+end_time);
			//当前月
			List<Map> this_monthlist =  this.statisticsManager.statisticsMonth_Amount(order_status,start_time,end_time);
			
			if(month.intValue()==1){
				year = year-1;
				month = 12;
			}
			int last_day = getDaysByYearMonth(year, month);
			long last_start_time = DateUtil.getDateline(year+"-"+month+"-01 00:00:00");
			long last_end_time = DateUtil.getDateline(year+"-"+month+"-"+last_day+" 23:59:59");
			System.out.println("上月开始时间："+last_start_time+",结束时间："+last_end_time);
			//上个月
			List<Map> last_monthlist = this.statisticsManager.statisticsMonth_Amount(order_status, last_start_time,last_end_time);
			
			System.out.println(JSONArray.fromObject(this_monthlist).toString());
			
			freeMarkerPaser.putData("this_monthlist",this_monthlist);
			freeMarkerPaser.putData("last_monthlist",last_monthlist);
		}
		return freeMarkerPaser.proessPageContent();
	}

	@Override
	public String getTabName() {
		return "下单金额";
	}

	@Override
	public int getOrder() {
		return 1;
	}
	
	public static int getDaysByYearMonth(int year, int month) {  
        Calendar a = Calendar.getInstance();  
        a.set(Calendar.YEAR, year);  
        a.set(Calendar.MONTH, month - 1);  
        a.set(Calendar.DATE, 1);  
        a.roll(Calendar.DATE, -1);  
        int maxDate = a.get(Calendar.DATE);  
        return maxDate;  
    }
	
	
	public static void main(String[] args) {
		int year =2015;
		int day=04;
	}
	
	public static String getYear(){
		
		return null;
	}

	public IStatisticsManager getStatisticsManager() {
		return statisticsManager;
	}

	public void setStatisticsManager(IStatisticsManager statisticsManager) {
		this.statisticsManager = statisticsManager;
	}
	

}
