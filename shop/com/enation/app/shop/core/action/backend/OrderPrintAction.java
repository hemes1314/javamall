package com.enation.app.shop.core.action.backend;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IOrderPrintManager;
import com.enation.framework.action.WWAction;
/**
 * 订单发货 Action
 * @author lina
 *
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("orderPrint")
@Results({
})
public class OrderPrintAction extends WWAction {
	
	private IOrderPrintManager orderPrintManager;
	private Integer[] order_id;
	private String[] expressno;//批量物流单号
	private String[] logi_id;	//物流公司Id
	private String logi_name;

	/**
	 * 订单发货Action
	 * @param order_id 订单号数组,Integer[]
	 * @return json
	 * result 1,操作成功.0,操作失败
	 */
	public String ship(){
		try{
			String is_ship= orderPrintManager.ship(order_id);
			if(is_ship.equals("true")){
				this.showSuccessJson("发货成功");
			}else{
				this.showErrorJson(is_ship);
			}
		}catch(Exception e){
			e.printStackTrace();
			this.showErrorJson(e.getMessage());
			this.logger.error("发货出错", e);
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 保存发货单号
	 * @param order_id 订单号数组,Integer[]
	 * @param expressno 物流单号数组,String[]
	 * @return json
	 * result 1,操作成功.0,操作失败
	 */
	public String saveShipNo(){
		try {
			
			String[] logiName=logi_name.toString().split(",");
			this.orderPrintManager.saveShopNos(order_id, expressno,logi_id,logiName);
			this.showSuccessJson("保存发货单号成功");
		} catch (Exception e) {
			this.showErrorJson(e.getMessage());
			this.logger.error("保存发货单号出错", e);
		}
		return this.JSON_MESSAGE;
		
	}
	/**
	 * 打印快递单
	 * @author LiFenLong
	 * @param order_id 订单号数组,Integer[]
	 * @param script 打印的script,String
	 * @return json
	 * result 1,操作成功.0,操作失败
	 * script 打印的script
	 */
	public String expressScript(){
			String script= orderPrintManager.getExpressScript(order_id);
			if(script.equals("快递单选择配送方式不同")||script.equals("请添加配送方式")||script.equals("没有此快递单模板请添加")||script.equals("请选择默认发货点")){
				this.showErrorJson(script);
			}else{
				Map map = new HashMap();
				map.put("script", script);
				map.put("result", 1);
				this.json=JSONObject.fromObject(map).toString();
			}
		return this.JSON_MESSAGE;
	}
	/**
	 * 打印发货单
	 * @author LiFenLong
	 * @param order_id 订单号数组,Integer[]
	 * @param script 打印的script,String
	 * @return 发货单的script
	 */
	public String shipScript() {
			String script= orderPrintManager.getShipScript(order_id);
			this.json=script;
		return this.JSON_MESSAGE;
	}

	public IOrderPrintManager getOrderPrintManager() {
		return orderPrintManager;
	}

	public void setOrderPrintManager(IOrderPrintManager orderPrintManager) {
		this.orderPrintManager = orderPrintManager;
	}

	public Integer[] getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer[] order_id) {
		this.order_id = order_id;
	}

	public String[] getExpressno() {
		return expressno;
	}

	public void setExpressno(String[] expressno) {
		this.expressno = expressno;
	}

	public String[] getLogi_id() {
		return logi_id;
	}

	public void setLogi_id(String[] logi_id) {
		this.logi_id = logi_id;
	}

	public String getLogi_name() {
		return logi_name;
	}

	public void setLogi_name(String logi_name) {
		this.logi_name = logi_name;
	}

}
