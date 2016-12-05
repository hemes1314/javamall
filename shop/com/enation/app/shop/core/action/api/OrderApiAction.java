package com.enation.app.shop.core.action.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.enation.app.shop.core.service.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.CartPluginBundle;
import com.enation.app.shop.mobile.util.HttpUtils;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 订单api
 * @author kingapex
 *2013-7-24下午9:27:47
 */
@SuppressWarnings({ "rawtypes", "serial" })
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("order")
@Results({
	@Result(name="kuaidi", type="freemarker", location="/themes/default/member/order_kuaidi.html") 
})
public class OrderApiAction extends WWAction {
	private IOrderFlowManager orderFlowManager;
	private IOrderManager orderManager;
	private IMemberAddressManager memberAddressManager;
	private IExpressManager expressManager;
	private ICartManager cartManager;
	private CartPluginBundle cartPluginBundle;
	private String status;
	
	private Map kuaidiResult;
	
	/**
	 * 创建订单，需要购物车中有商品
	 * @param address_id:收货地址id.int型，必填项
	 * @param payment_id:支付方式id，int型，必填项
	 * @param shipping_id:配送方式id，int型，必填项
	 * @param shipDay：配送时间，String型 ，可选项
	 * @param shipTime，String型 ，可选项
	 * @param remark，String型 ，可选项
	 * 
	 * @return 返回json串
	 * result  为1表示添加成功0表示失败 ，int型
	 * message 为提示信息
	 * 
	 */
	public String create(){
		try{
			Order order  = this.createOrder();
			
			this.json = JsonMessageUtil.getObjectJson(order,"order");
			
		}catch(RuntimeException e){
			//e.printStackTrace();
			this.logger.error("创建订单出错", e);
			this.showErrorJson(e.getMessage());
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 取消订单
	 * @param sn:订单序列号.String型，必填项
	 * 
	 * @return 返回json串
	 * result  为1表示添加成功0表示失败 ，int型
	 * message 为提示信息
	 */
	
	public String cancel() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		try {
			String sn = request.getParameter("sn");
			String reason = request.getParameter("reason");
			Member member = UserConext.getCurrentMember();
			if (member == null) {
				this.showErrorJson("取消订单失败：登录超时");
			} else {
				this.orderFlowManager.cancel(sn, reason);
				this.showSuccessJson("取消订单成功");
			}
		} catch (RuntimeException re) {
			this.showErrorJson(re.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 确认收货
	 * @param orderId:订单id.String型，必填项
	 * 
	 * @return 返回json串
	 * result  为1表示添加成功0表示失败 ，int型
	 * message 为提示信息
	 */
	public String rogConfirm() {
		try {
			JSONObject result = new JSONObject();
			result.put("success", false);
    		result.put("errCode", -9);
			String orderId = ThreadContextHolder.getHttpRequest().getParameter("orderId");
			Member member = UserConext.getCurrentMember();
			if (member == null) {
				result.put("errCode", -1);
				this.showErrorJson("取消订单失败：登录超时");
			} else {
				Order order = this.orderManager.get(NumberUtils.toInt(orderId));
				if (order == null) {
					this.showErrorJson("订单不存在！");
				} else if (member.getMember_id().compareTo(order.getMember_id()) != 0) {
					this.showErrorJson("对不起，您没有权限进行此项操作！");
				} else if (order.getStatus().intValue() != OrderStatus.ORDER_SHIP) {
					this.showErrorJson("该订单状态已发生变化！");
				} else {
					this.orderFlowManager.rogConfirmtg(NumberUtils.toInt(orderId), member.getMember_id(), member.getUname(), member.getUname(), DateUtil.getDateline(), status);
					this.showSuccessJson("确认收货成功");
				}
			}
			// 添加jsonp支持
	        String callback = getRequest().getParameter("callback");
	        if (StringUtils.isNotBlank(callback)) {
	            PrintWriter writer = null;
	            try {
	            	JSONObject jsonObject = JSONObject.parseObject(json);
	            	if (jsonObject.getInteger("result") == 0) {
	            		result.put("errMsg", jsonObject.getString("message"));
	            	} else {
	            		result.put("success", true);
	            		result.put("errCode", 0);
	            	}
	                writer = ThreadContextHolder.getHttpResponse().getWriter();
	                writer.write(HttpUtils.jsonp(callback, result.toJSONString()));
	            } catch(IOException e) {
	                e.printStackTrace();
	            } finally {
	                writer.close();
	            }
	        }
		} catch (Exception e) {
			this.showErrorJson("数据库错误");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**************以下非api，不用书写文档**************/
	
	private Order createOrder(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		
		Integer shippingId = StringUtil.toInt(request.getParameter("typeId"),null);
 		if(shippingId==null ) throw new RuntimeException("配送方式不能为空");
		
		Integer paymentId = StringUtil.toInt(request.getParameter("paymentId"),0);
 		if(paymentId==0 ) throw new RuntimeException("支付方式不能为空");
		
		Order order = new Order() ;
		order.setShipping_id(shippingId); //配送方式
		order.setPayment_id(paymentId);//支付方式
		Integer addressId = StringUtil.toInt(request.getParameter("addressId"), false);
		MemberAddress address = new MemberAddress();

		address = this.createAddress();	
 
		order.setShip_provinceid(address.getProvince_id());
		order.setShip_cityid(address.getCity_id());
		order.setShip_regionid(address.getRegion_id());
		
		order.setShip_addr(address.getAddr());
		order.setShip_mobile(address.getMobile());
		order.setShip_tel(address.getTel());
		order.setShip_zip(address.getZip());
//		order.setShipping_area(address.getProvince()+"-"+ address.getCity()+"-"+ address.getRegion());
		order.setShipping_area(address.getProvince() + address.getCity() + address.getRegion());
		order.setShip_name(address.getName());
		order.setRegionid(address.getRegion_id());
		
//		if (addressId.intValue()==0) {
		//新的逻辑：只要选中了“保存地址”，就会新增一条收货地址，即使数据完全没有修改
	 	if ("yes".equals(request.getParameter("saveAddress"))) {
	 		Member member = UserConext.getCurrentMember();
			if (member != null) {
					address.setAddr_id(null);
					addressId= this.memberAddressManager.addAddress(address);
			}
		}
//		}
	 	
 	 	address.setAddr_id(addressId);
	 	order.setMemberAddress(address);
		order.setShip_day(request.getParameter("shipDay"));
		order.setShip_time(request.getParameter("shipTime"));
		order.setRemark(request.getParameter("remark"));
		order.setAddress_id(address.getAddr_id());//保存本订单的会员id
		String sessionid =request.getSession().getId();
		List<CartItem> itemList  = this.cartManager.listGoods(sessionid);
		OrderPrice orderPrice =   this.cartManager.countPrice(itemList, shippingId,address.getRegion_id()+"");
	
		//激发价格计算事件
		orderPrice  = this.cartPluginBundle.coutPrice(orderPrice);
		order.setOrderprice(orderPrice);
		return this.orderManager.add(order,itemList,sessionid, orderPrice);
		
	}
	
	private MemberAddress createAddress(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		MemberAddress address = new MemberAddress();
 

		String name = request.getParameter("shipName");
		address.setName(name);

		String tel = request.getParameter("shipTel");
		address.setTel(tel);

		String mobile = request.getParameter("shipMobile");
		address.setMobile(mobile);

		String province_id = request.getParameter("province_id");
		if(province_id!=null){
			address.setProvince_id(Integer.valueOf(province_id));
		}

		String city_id = request.getParameter("city_id");
		if(city_id!=null){
			address.setCity_id(Integer.valueOf(city_id));
		}

		String region_id = request.getParameter("region_id");
		if(region_id!=null){
			address.setRegion_id(Integer.valueOf(region_id));
		}

		String province = request.getParameter("province");
		address.setProvince(province);

		String city = request.getParameter("city");
		address.setCity(city);

		String region = request.getParameter("region");
		address.setRegion(region);

		String addr = request.getParameter("shipAddr");
		address.setAddr(addr);

		String zip = request.getParameter("shipZip");
		address.setZip(zip);
	
		return address;
	}

	public String orderKuaidi(){
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String logino = request.getParameter("logino");//物流号
			String code = request.getParameter("code");//物流公司代码
			if(logino==null || logino.length()<5){
				Map<String, String> result = new HashMap<>();
				result.put("status", "-1");
				this.showErrorJson("请输入正确的运单号");
				return "";
			}
			if(code == null || code.equals("")){
				code = "yuantong";
			}
			
			kuaidiResult = this.expressManager.getDefPlatform(code, logino);
			
		} catch (Exception e) {
			this.logger.error("查询货运状态", e);
		}
		return "kuaidi";
	}

	//set get
	public IOrderManager getOrderManager() {
		return orderManager;
	}


	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}


	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}


	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}

	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}

	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}

	public Map getKuaidiResult() {
		return kuaidiResult;
	}

	public void setKuaidiResult(Map kuaidiResult) {
		this.kuaidiResult = kuaidiResult;
	}

	public IExpressManager getExpressManager() {
		return expressManager;
	}

	public void setExpressManager(IExpressManager expressManager) {
		this.expressManager = expressManager;
	}

	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}

	public CartPluginBundle getCartPluginBundle() {
		return cartPluginBundle;
	}

	public void setCartPluginBundle(CartPluginBundle cartPluginBundle) {
		this.cartPluginBundle = cartPluginBundle;
	}

    
    public String getStatus() {
        return status;
    }

    
    public void setStatus(String status) {
        this.status = status;
    }
	
	
}
