package com.enation.app.shop.core.action.backend;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;

/**
 * 销售统计
 * @author xulipeng
 */

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("salesStatis")
@Results({
	@Result(name="order_statis", type="freemarker", location="/shop/admin/statistics/order_statistics.html")
})
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class SalesStatisticsAction extends WWAction{
	
	private OrderPluginBundle orderPluginBundle;
	private Map<Integer,String> pluginTabs;
	private Map<Integer,String> pluginHtmls;
	private Integer order_status;
	private Integer cycle_type;
	private Integer year;
	private Integer month;
	
	
	
	public String orderStatis(){
		
		Map map  = new HashMap();
		map.put("order_status", order_status);
		map.put("cycle_type", cycle_type);
		map.put("year", year);
		map.put("month", month);
		
		this.pluginTabs = this.orderPluginBundle.getStatisTabList();
		this.pluginHtmls = this.orderPluginBundle.getStatisDetailHtml(map);
		
		return "order_statis";
	}
	
	public String orderStatisJson(){
		return null;
	}
	
	public static void main(String[] args) {
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//		//String data = format.format(new Date());
//		String data ="2015-05-14";
//		long l = DateUtil.getDateline(data,"yyyy-MM");
//		System.out.println(data+"___"+l);
		
		Calendar cal = Calendar.getInstance();
		int	year = cal.get(Calendar.YEAR);
		int	month = cal.get(Calendar.MONTH)+1;
		int	day = cal.get(Calendar.DAY_OF_MONTH);
		System.out.println(year+"-"+month+"-"+day);
	}
	
	
	// set get
	
	public OrderPluginBundle getOrderPluginBundle() {
		return orderPluginBundle;
	}

	public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
		this.orderPluginBundle = orderPluginBundle;
	}

	public Map<Integer, String> getPluginTabs() {
		return pluginTabs;
	}

	public void setPluginTabs(Map<Integer, String> pluginTabs) {
		this.pluginTabs = pluginTabs;
	}

	public Map<Integer, String> getPluginHtmls() {
		return pluginHtmls;
	}

	public void setPluginHtmls(Map<Integer, String> pluginHtmls) {
		this.pluginHtmls = pluginHtmls;
	}

	public Integer getOrder_status() {
		return order_status;
	}

	public void setOrder_status(Integer order_status) {
		this.order_status = order_status;
	}

	public Integer getCycle_type() {
		return cycle_type;
	}

	public void setCycle_type(Integer cycle_type) {
		this.cycle_type = cycle_type;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}
	
}
