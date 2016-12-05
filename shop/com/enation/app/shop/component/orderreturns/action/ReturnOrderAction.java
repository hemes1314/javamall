package com.enation.app.shop.component.orderreturns.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager;
import com.enation.app.shop.component.orderreturns.service.ReturnsOrderStatus;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderLog;
import com.enation.app.shop.core.model.ReturnsOrder;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.OrderItemStatus;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 		<!-- 退换货订单 -->
		<action name="returnOrder" class="returnOrderAction">
			<result name="return_list">order/return_list.jsp</result>
			<result name="return_detail">order/return_detail.jsp</result>	
		 
			<result name="changeitem">order/dialog/item_change.jsp</result>
		</action>
 */
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Results({ 
	 @Result(name="return_list", type="freemarker", location="/com/enation/app/shop/component/orderreturns/action/return_list.html") ,
	 @Result(name="return_detail", type="freemarker", location="/com/enation/app/shop/component/orderreturns/action/return_detail.html") ,
	 @Result(name="changeitem", type="freemarker", location="/com/enation/app/shop/component/orderreturns/action/item_change.html")
})  
@Action("returnOrder") 
public class ReturnOrderAction extends WWAction {
	private IAdminUserManager adminUserManager;
	private IReturnsOrderManager returnsOrderManager;
	private IGoodsManager goodsManager;
	private Integer returnOrderId;
	private ReturnsOrder rorder;
	private List itemList;
	private IOrderManager orderManager;
	private List goodIdS;// 退货单ID数组
	private String refuse_reason; // 拒绝原因
	private IRegionsManager regionsManager;
	private Order ord;
	private List provinceList;
	private Integer[] itemid_array;
	private String[] targetsn_array;
	private Integer state; // 根据退换货状态过滤
	private String ctx;
	
	public IAdminUserManager getAdminUserManager() {
		
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String[] getTargetsn_array() {
		return targetsn_array;
	}

	public void setTargetsn_array(String[] targetsn_array) {
		this.targetsn_array = targetsn_array;
	}

	public String getRefuse_reason() {
		return refuse_reason;
	}

	public Integer[] getItemid_array() {
		return itemid_array;
	}

	public void setItemid_array(Integer[] itemid_array) {
		this.itemid_array = itemid_array;
	}

	public void setRefuse_reason(String refuse_reason) {
		this.refuse_reason = refuse_reason;
	}

	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}

	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}

	public Order getOrd() {
		return ord;
	}

	public void setOrd(Order ord) {
		this.ord = ord;
	}

	public List getProvinceList() {
		return provinceList;
	}

	public void setProvinceList(List provinceList) {
		this.provinceList = provinceList;
	}

	public ReturnsOrder getRorder() {
		return rorder;
	}

	public void setRorder(ReturnsOrder rorder) {
		this.rorder = rorder;
	}

	public Integer getReturnOrderId() {
		return returnOrderId;
	}

	public void setReturnOrderId(Integer returnOrderId) {
		this.returnOrderId = returnOrderId;
	}

	public IReturnsOrderManager getReturnsOrderManager() {
		return returnsOrderManager;
	}

	public void setReturnsOrderManager(IReturnsOrderManager returnsOrderManager) {
		this.returnsOrderManager = returnsOrderManager;
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

	public List getItemList() {
		return itemList;
	}

	public void setItemList(List itemList) {
		this.itemList = itemList;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public List getGoodIdS() {
		return goodIdS;
	}

	public void setGoodIdS(List goodIdS) {
		this.goodIdS = goodIdS;
	}

	/**
	 * 拒绝退货，订单状态改为：已拒绝2注意此处的returnorderId为退换货订单表的id
	 * 
	 * @return
	 */
	public String refuseReturn() {
		try {
			this.returnsOrderManager.refuse(returnOrderId, this.refuse_reason,
					ReturnsOrderStatus.APPLY_REFUSE);
			int orderid = returnsOrderManager
					.queryOrderidByReturnorderid(returnOrderId);
			this.returnsOrderManager.updateItemStatusByOrderidAndStatus(
					OrderItemStatus.REFUSE_RETURN,
					OrderItemStatus.APPLY_RETURN, orderid);
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			OrderLog log = new OrderLog();
			log.setMessage("管理员" + adminUser.getUsername() + "拒绝退货");
			log.setOp_id(adminUser.getUserid());
			log.setOp_name(adminUser.getUsername());
			log.setOrder_id(this.returnsOrderManager
					.getOrderidByReturnid(returnOrderId));
			this.returnsOrderManager.addLog(log);
			this.showSuccessJson("");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error(e.fillInStackTrace());
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 同意退货
	 * 
	 * @return
	 */
	public String agreeReturn() {
		Integer orderid = this.returnsOrderManager	.getOrderidByReturnid(returnOrderId);		
		try {
			this.returnsOrderManager.updateState(returnOrderId,
					ReturnsOrderStatus.APPLY_PASSED);
			if (itemid_array != null && itemid_array.length > 0) {
				for (int i = 0; i < itemid_array.length; i++) {
					this.returnsOrderManager.updateOrderItemsState(
							itemid_array[i], OrderItemStatus.RETURN_PASSED);
				}
				this.returnsOrderManager.updateItemsState(orderid, OrderItemStatus.REFUSE_RETURN, OrderItemStatus.APPLY_RETURN);
			}
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			OrderLog log = new OrderLog();
			log.setMessage("管理员" + adminUser.getUsername() + "同意退货");
			log.setOp_id(adminUser.getUserid());
			log.setOp_name(adminUser.getUsername());
			log.setOrder_id(orderid);
			this.returnsOrderManager.addLog(log);
			this.showSuccessJson("");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error(e.fillInStackTrace());
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 确认退货收到
	 * 
	 * @return
	 */
	public String confirmReturnReceive() {
		try {
			this.returnsOrderManager.updateState(returnOrderId,
					ReturnsOrderStatus.GOODS_REC);
			int orderid = returnsOrderManager
					.queryOrderidByReturnorderid(returnOrderId);
			this.returnsOrderManager.updateItemStatusByOrderidAndStatus(
					OrderItemStatus.RETURN_REC, OrderItemStatus.RETURN_PASSED,
					orderid);
			OrderLog log = new OrderLog();
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			log.setMessage("管理员" + adminUser.getUsername() + "已收到退货,正在执行退款");
			log.setOp_id(adminUser.getUserid());
			log.setOp_name(adminUser.getUsername());
			log.setOrder_id(this.returnsOrderManager
					.getOrderidByReturnid(returnOrderId));
			this.returnsOrderManager.addLog(log);
			this.showSuccessJson("");
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e.fillInStackTrace());
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 已退货
	 */
	public String returned() {
		try {
			this.returnsOrderManager.updateState(returnOrderId,
					ReturnsOrderStatus.APPLY_END);// 退货完成
			int orderid = returnsOrderManager
					.queryOrderidByReturnorderid(returnOrderId);
			double prices = this.returnsOrderManager.queryItemPrice(orderid,
					OrderItemStatus.RETURN_REC).doubleValue();
			this.returnsOrderManager.updateItemStatusByOrderidAndStatus(
					OrderItemStatus.RETURN_END, OrderItemStatus.RETURN_REC,
					orderid);
			this.orderManager.updateOrderPrice(prices, orderid);
			OrderLog log = new OrderLog();
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			log.setMessage("管理员" + adminUser.getUsername() + "完成退货");
			log.setOp_id(adminUser.getUserid());
			log.setOp_name(adminUser.getUsername());
			log.setOrder_id(this.returnsOrderManager
					.getOrderidByReturnid(returnOrderId));
			this.returnsOrderManager.addLog(log);
			this.showSuccessJson("");
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e.fillInStackTrace());
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 拒绝换货
	 */
	public String refuseChange() {
		try {
			this.returnsOrderManager.refuse(returnOrderId, this.refuse_reason,
					ReturnsOrderStatus.APPLY_REFUSE);
			int orderid = returnsOrderManager
					.queryOrderidByReturnorderid(returnOrderId);
			this.returnsOrderManager.updateItemStatusByOrderidAndStatus(
					OrderItemStatus.REFUSE_CHANGE,
					OrderItemStatus.APPLY_CHANGE, orderid);
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			OrderLog log = new OrderLog();
			log.setMessage("管理员" + adminUser.getUsername() + "拒绝换货");
			log.setOp_id(adminUser.getUserid());
			log.setOp_name(adminUser.getUsername());
			log.setOrder_id(this.returnsOrderManager
					.getOrderidByReturnid(returnOrderId));
			this.returnsOrderManager.addLog(log);
			this.showSuccessJson("");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error(e.fillInStackTrace());
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 同意换货
	 */
	public String agreeChange() {
		Integer orderid = this.returnsOrderManager	.getOrderidByReturnid(returnOrderId);
		try {
			this.returnsOrderManager.updateState(returnOrderId,
					ReturnsOrderStatus.APPLY_PASSED);
			if (itemid_array != null && itemid_array.length > 0) {
				for (int i = 0; i < itemid_array.length; i++) {
					this.returnsOrderManager.updateOrderItemsState(
							itemid_array[i], OrderItemStatus.CHANGE_PASSED);
				}
				this.returnsOrderManager.updateItemsState(orderid, OrderItemStatus.REFUSE_CHANGE, OrderItemStatus.APPLY_CHANGE);
			}
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			OrderLog log = new OrderLog();
			log.setMessage("管理员" + adminUser.getUsername() + "同意换货");
			log.setOp_id(adminUser.getUserid());
			log.setOp_name(adminUser.getUsername());
			log.setOrder_id(orderid);
			this.returnsOrderManager.addLog(log);
			this.showSuccessJson("");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error(e.fillInStackTrace());
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 确认收到换货,并发货
	 */
	public String confirmChangeReceive() {
		int orderid = returnsOrderManager
				.queryOrderidByReturnorderid(returnOrderId);
		double prices = this.returnsOrderManager.queryItemPrice(orderid,
				OrderItemStatus.CHANGE_PASSED).doubleValue();// 原货物价格和
		try {
			if (targetsn_array != null && targetsn_array.length > 0) {
				targetsn_array = dealwithhyphen(targetsn_array);
				for (int i = 0; i < targetsn_array.length; i++) {
					Goods g = this.goodsManager.getGoodBySn(targetsn_array[i]);
					if (g == null) {
						this.showErrorJson("目标货号必须都存在！");
						return this.JSON_MESSAGE;
					}
				}
				for (int i = 0; i < targetsn_array.length; i++) {
					targetsn_array = dealwithhyphen(targetsn_array);
					double temp_price = this.goodsManager
							.getGoodBySn(targetsn_array[i]).getPrice()
							.doubleValue();
					this.returnsOrderManager.updatePriceByItemid(
							itemid_array[i], temp_price);
				}
			}
			if (targetsn_array != null && targetsn_array.length > 0) {
				for (int i = 0; i < targetsn_array.length; i++) {
					targetsn_array = dealwithhyphen(targetsn_array);
					Goods g = this.goodsManager.getGoodBySn(targetsn_array[i]);
					prices -= g.getPrice().doubleValue();
					this.returnsOrderManager.updateItemChange(g.getName(),
							g.getGoods_id(), itemid_array[i]);
				}
			}
			this.orderManager.updateOrderPrice(prices, orderid);
			this.returnsOrderManager.updateState(returnOrderId,
					ReturnsOrderStatus.GOODS_REC);
			this.returnsOrderManager.updateItemStatusByOrderidAndStatus(
					OrderItemStatus.CHANGE_REC, OrderItemStatus.CHANGE_PASSED,
					orderid);
			OrderLog log = new OrderLog();
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			log.setMessage("管理员" + adminUser.getUsername() + "已收到换货,换货已发出");
			log.setOp_id(adminUser.getUserid());
			log.setOp_name(adminUser.getUsername());
			log.setOrder_id(this.returnsOrderManager
					.getOrderidByReturnid(returnOrderId));
			this.returnsOrderManager.addLog(log);
			this.showSuccessJson("");
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e.fillInStackTrace());
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 更新退换货表状态为完成5
	 */
	public String changed() {
		try {
			this.returnsOrderManager.updateState(returnOrderId,
					ReturnsOrderStatus.APPLY_END);// 退货完成
			int orderid = returnsOrderManager
					.queryOrderidByReturnorderid(returnOrderId);
			this.returnsOrderManager.updateItemStatusByOrderidAndStatus(
					OrderItemStatus.CHANGE_END, OrderItemStatus.CHANGE_REC,
					orderid);
			OrderLog log = new OrderLog();
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			log.setMessage("管理员" + adminUser.getUsername() + "完成换货");
			log.setOp_id(adminUser.getUserid());
			log.setOp_name(adminUser.getUsername());
			log.setOrder_id(this.returnsOrderManager
					.getOrderidByReturnid(returnOrderId));
			this.returnsOrderManager.addLog(log);
			this.showSuccessJson("");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error(e.fillInStackTrace());
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 退换货列表查询
	 * 
	 * @return
	 */
	public String returnsList() {
		return "return_list";
	}
	
	public String returnsListJson() {
		this.webpage = this.returnsOrderManager.listAll(this.getPage(),
				this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}

	/**
	 * 退换货申请查询
	 * 
	 * @return
	 */
	public String returnsApplyList() {
		this.webpage = this.returnsOrderManager.listAll(this.getPage(),
				this.getPageSize(), this.state);
		return "return_list";
	}

	/**
	 * 退货详细
	 * 
	 * @return
	 */
	public String returnDetail() {
		rorder = returnsOrderManager.get(returnOrderId);
		itemList = orderManager.listGoodsItems(orderManager.get(
				rorder.getOrdersn()).getOrder_id());
		String goodsSn = returnsOrderManager
				.getSnByOrderSn(rorder.getOrdersn());
		String[] goodSn = null;
		goodIdS = new ArrayList();
		if (goodsSn != null && !goodsSn.equals("")) {
			goodSn = StringUtils.split(goodsSn,",");
			goodSn = dealwithhyphen(goodSn);
			for (int i = 0; i < goodSn.length; i++) {
				goodIdS.add(goodsManager.getGoodBySn(goodSn[i]).getGoods_id());
			}
		}
		this.ctx= ThreadContextHolder.getHttpRequest().getContextPath();
		if(ctx!=null && !ctx.endsWith("/")){
			ctx=ctx+"/";
		}
		return "return_detail";
	}

	public String returnD() {
		rorder = returnsOrderManager.get(returnOrderId);
		itemList = orderManager.listGoodsItems(orderManager.get(
				rorder.getOrdersn()).getOrder_id());
		return "changeitem";
	}

	public String[] dealwithhyphen(String[] targetsn_array){
		for (int j = 0; j < targetsn_array.length; j++) {
			if(targetsn_array[j].indexOf("-") != -1){
				targetsn_array[j]=targetsn_array[j].substring(0, targetsn_array[j].indexOf("-"));
			}
		}
		return targetsn_array;
	}

	public String getCtx() {
		return ctx;
	}

	public void setCtx(String ctx) {
		this.ctx = ctx;
	}

	
}
