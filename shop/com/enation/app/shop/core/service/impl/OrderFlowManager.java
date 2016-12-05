package com.enation.app.shop.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.enation.app.b2b2c.component.plugin.bill.ConfirmOrderPlugin;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.component.express.plugin.HttpRequest;
import com.enation.app.shop.component.express.plugin.JacksonHelper;
import com.enation.app.shop.component.express.pojo.TaskRequest;
import com.enation.app.shop.component.express.pojo.TaskResponse;
import com.enation.app.shop.core.model.Allocation;
import com.enation.app.shop.core.model.AllocationItem;
import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.DeliveryItem;
import com.enation.app.shop.core.model.Logi;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.OrderLog;
import com.enation.app.shop.core.model.PaymentDetail;
import com.enation.app.shop.core.model.PaymentLog;
import com.enation.app.shop.core.model.PaymentLogType;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.RefundLog;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.ILogiManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.app.shop.core.service.IPointHistoryManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.mobile.service.impl.ErpManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;
import com.enation.framework.cache.redis.SystemConstants;
import com.enation.framework.sms.SmsSender;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;

/**
 * 订单业务流程
 * @author kingapex
 *2010-4-8上午10:19:42
 *@author LiFenLong 2014-4-17;4.0改版修改pay方法
 */
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class OrderFlowManager extends BaseSupport implements IOrderFlowManager {
	
	private IOrderManager orderManager;
	private IMemberManager memberManager;
	private IPointHistoryManager pointHistoryManager;


	private IMemberPointManger memberPointManger;
	private OrderPluginBundle orderPluginBundle;
	private ConfirmOrderPlugin confirmOrderPlugin;
	private ICache iCache;
	
    public ConfirmOrderPlugin getConfirmOrderPlugin() {
        return confirmOrderPlugin;
    }

	public ICache getiCache() {
		return iCache;
	}

	public void setiCache(ICache iCache) {
		this.iCache = iCache;
	}

	public void setConfirmOrderPlugin(ConfirmOrderPlugin confirmOrderPlugin) {
        this.confirmOrderPlugin = confirmOrderPlugin;
    }

    private ILogiManager logiManager;
	private IAdminUserManager adminUserManager;
	private IPromotionManager promotionManager;
	
	private IOrderReportManager orderReportManager;

	private ErpManager erpManager;

    @Value("#{configProperties['address.kuaibackurl']}")
    private String kuaibackurl;
    @Value("#{configProperties['address.kuaiposturl']}")
    private String kuaiposturl;
    @Value("#{configProperties['code.kuaidicode']}")
    private String kuaidicode;
    
    @Value("#{configProperties['order.ship.sms.on']}")
    private boolean smsOn;
    
	/**
	 * 支付
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean pay(Integer paymentId,Integer orderId,double payMoney,String userName) {
		
		Order order = this.orderManager.get(orderId);
		PaymentDetail paymentdetail=new PaymentDetail();
		PaymentLog payment= orderReportManager.getPayment(paymentId);
		double paidmoney=this.orderReportManager.getPayMoney(paymentId);//其它金额中已结算的金额
			if(CurrencyUtil.add(payMoney, paidmoney)>payment.getMoney()){
				//添加过多
				return false;
			}
				//添加支付详细对象
				paymentdetail.setAdmin_user(userName);
				paymentdetail.setPay_date(DateUtil.getDateline());
				paymentdetail.setPay_money(payMoney);
				paymentdetail.setPayment_id(paymentId);
				orderReportManager.addPayMentDetail(paymentdetail);
				
			if(CurrencyUtil.add(payMoney, paidmoney)<payment.getMoney()){
				//修改订单状态为部分付款
				
				this.daoSupport.execute("update es_order set pay_status=?,paymoney=paymoney+? where order_id=?",OrderStatus.PAY_PARTIAL_PAYED,payMoney,order.getOrder_id());
				this.daoSupport.execute("update es_payment_logs set status=?,paymoney=paymoney+? where payment_id=?",2,payMoney,payment.getPayment_id());
				return true;	
			}else{
				//修改订单状态为已付款付款
				this.daoSupport.execute("update es_payment_logs set status=? ,paymoney=paymoney+? where payment_id=?",OrderStatus.PAY_CONFIRM,payMoney,payment.getPayment_id());
				//更新订单的已付金额
				this.daoSupport.execute("update es_order set paymoney=?,status=?,pay_status=? where order_id=?",payMoney,OrderStatus.ORDER_PAY_CONFIRM,OrderStatus.PAY_CONFIRM,order.getOrder_id());
				
				payConfirm(orderId);
				return true;
		}
			
	}
	
	/**
	 * 退款
	 */
	@Transactional(propagation = Propagation.REQUIRED)	
	public void refund(RefundLog refund) {
		if(refund== null ) throw new  IllegalArgumentException("param paymentLog is NULL");
		 
		if(refund.getMoney()== null ) throw new  IllegalArgumentException("param PaymentLog's money is NULL");
		Order order = this.orderManager.get(refund.getOrder_id());
		checkDisabled(order);
		if(order.getPay_status().intValue() == OrderStatus.PAY_CANCEL ){
			if(logger.isDebugEnabled()){
				logger.debug("订单["+order.getSn()+"]支付状态为[已经退款]，不能再对其进行退款操作");
			}
			throw new IllegalStateException("订单["+order.getSn()+"]支付状态为[已经退款]，不能再对其进行退款操作");
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("订单:"+order.getOrder_id()+"退款");
		}
		
		
		double m =order.getOrder_amount();// 订单总额
		double nm = refund.getMoney();// 当前付款金额
		double om = order.getPaymoney();// 已收金额
		

		int payStatus = 0;
		if (nm == om)
			payStatus = OrderStatus.PAY_CANCEL;// 已退款
		if (nm < om)
			payStatus = OrderStatus.PAY_PARTIAL_REFUND;// 部分退款
		if (nm > om) {
			if(logger.isDebugEnabled()){
				logger.debug("退款金额["+nm+"]超过订单支付金额["+m+"]");
			}			
			throw new RuntimeException("退款金额["+nm+"]超过订单支付金额["+om+"]");
		}
		refund.setOrder_sn(order.getSn());
		refund.setCreate_time(System.currentTimeMillis());
		refund.setMember_id(order.getMember_id());
		this.baseDaoSupport.insert("refund_logs", refund);
		
 
			
		if(logger.isDebugEnabled()){
			logger.debug("更新订单状态["+OrderStatus.ORDER_CANCEL_PAY+"],支付状态["+payStatus+ "]");
		}		
		baseDaoSupport.execute("update order set status=?,pay_status=?,paymoney=paymoney-? where order_id=?", OrderStatus.ORDER_CANCEL_PAY,payStatus,refund.getMoney(),order.getOrder_id());
		
		this.log(order.getOrder_id(), "订单退款，金额"+ refund.getMoney());
		
	}
	
	/**
	 * 后台 记录订单操作日志
	 * @param order_id
	 * @param message
	 * @param op_id
	 * @param op_name
	 */
	private void log(Integer order_id,String message){
		AdminUser adminUser = UserConext.getCurrentAdminUser();
		OrderLog orderLog = new OrderLog();
		orderLog.setMessage(message);
		if(UserConext.getCurrentMember()!=null){
		    orderLog.setOp_id(0L);
            orderLog.setOp_name(UserConext.getCurrentMember().getUname());
		}else if(adminUser==null){
			orderLog.setOp_id(0L);
			orderLog.setOp_name("系统检测");
		}else{
			orderLog.setOp_id(adminUser.getUserid());
			orderLog.setOp_name(adminUser.getUsername());
		}
		orderLog.setOp_time(DateUtil.getDateline());
		orderLog.setOrder_id(order_id);
		this.baseDaoSupport.insert("order_log", orderLog);
	}
	
	
 	private void log(Integer order_id,String message,Long op_id,String op_name){
		OrderLog orderLog = new OrderLog();
		orderLog.setMessage(message);
		orderLog.setOp_id(op_id);
		orderLog.setOp_name( op_name );
		orderLog.setOp_time(DateUtil.getDateline());
		orderLog.setOrder_id(order_id);
		this.baseDaoSupport.insert("order_log", orderLog);
	}
	
	
	
	/**
	 * 配货
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void allocation(Allocation allocation ){
	
		List<AllocationItem>  itemList  = allocation.getItemList();
		int orderid = allocation.getOrderid();
		
		for(AllocationItem item:itemList){
			item.setOrderid(orderid);
			//激发"订单项"的发货事件
			this.orderPluginBundle.onAllocationItem(item); //处理每个配货项
			
			this.baseDaoSupport.insert("allocation_item", item);
			
		} 
		
		//更新订单的发货点，及订单状态
		this.baseDaoSupport.execute("update order set depotid=?,status=?,ship_status=?,allocation_time=? where order_id=?", allocation.getShipDepotId() ,OrderStatus.ORDER_ALLOCATION,OrderStatus.SHIP_ALLOCATION_YES,DateUtil.getDateline(),allocation.getOrderid());
		
		//激发"订单"的发货事件
		orderPluginBundle.onAllocation(allocation);
		
		if(logger.isDebugEnabled()){
			logger.debug("订单["+allocation.getOrderid()+"]配货成功");
		}
	 
		
	}

	@Override
	public String getAllocationHtml(int itemid) {
		OrderItem  item= this.orderManager.getItem(itemid);
		String html = this.orderPluginBundle.getAllocationHtml(item);
		return html;
	}
	
 
	@Override
	public String getAllocationViewHtml(int itemid) {
		OrderItem  item= this.orderManager.getItem(itemid);
		String html = this.orderPluginBundle.getAllocationViewHtml(item);
		return html;
	}

	

/*	public void shipping(Delivery delivery, List<DeliveryItem> itemList,List<DeliveryItem> giftItemList) {
		baseDaoSupport.insert("delivery", delivery);
		throw new RuntimeException("我的异常 ");
	}
	*/
	
	/**
	 * 发货
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void shipping(Delivery delivery, List<DeliveryItem> itemList) {
		
		if(delivery== null) throw new  IllegalArgumentException("param delivery is NULL");
		if(itemList== null) throw new  IllegalArgumentException("param itemList is NULL");
		if(delivery.getOrder_id()== null )  throw new IllegalArgumentException("param order id is null");
		
		if(delivery.getMoney()== null ) delivery.setMoney(0D);
		if(delivery.getProtect_price()==null) delivery.setProtect_price(0D);
		
		if(logger.isDebugEnabled()){
			logger.debug("订单["+delivery.getOrder_id()+"]发货");
		}
		
		Integer orderId = delivery.getOrder_id() ;
		Order order = this.orderManager.get(orderId);
		delivery.setOrder(order);
		
		checkDisabled(order);
		if(order.getShip_status().intValue() ==  OrderStatus.SHIP_YES){
			if(logger.isDebugEnabled()){
				logger.debug("订单["+order.getSn()+"]状态已经为【已发货】，不能再对其进行发货操作");
			}			
			throw new IllegalStateException("订单["+order.getSn()+"]状态已经为【已发货】，不能再对其进行发货操作");
		}
		
		if(delivery.getLogi_id()!=null && delivery.getLogi_id()!=0){
			Logi logi = this.logiManager.getLogiById(delivery.getLogi_id());
			delivery.setLogi_code(logi.getCode());
			delivery.setLogi_name(logi.getName());
		}
		
		delivery.setType(1);
		delivery.setMember_id(order.getMember_id());
		delivery.setCreate_time(System.currentTimeMillis());
		baseDaoSupport.insert("delivery", delivery);
		Integer delivery_id = baseDaoSupport.getLastId("delivery");//产生的货运单id
		
		int shipStatus = OrderStatus.SHIP_YES;
		
		//处理商品发货项
		int goodsShipStatus = this.processGoodsShipItem(orderId, delivery_id, itemList);
		shipStatus=goodsShipStatus;
	 
		
		//如果发货状态不为全部发货即为部分发货
		shipStatus=shipStatus==OrderStatus.SHIP_YES?OrderStatus.SHIP_YES :OrderStatus.SHIP_PARTIAL_SHIPED;
		

		
		/**
		 * -----------------
		 * 激发订单项发货事件
		 * -----------------
		 */
		for(DeliveryItem  deliverItem:itemList){
			List<AllocationItem> alloitemList = this.getAllocationList(orderId, deliverItem.getOrder_itemid());
			for( AllocationItem alloItem:alloitemList){
				this.orderPluginBundle.onItemShip(order,deliverItem, alloItem);
			}
		} 
		
		
		
		/**
		 * -----------------
		 * 激发发货事件
		 * -----------------
		 */
		this.orderPluginBundle.onShip(delivery, itemList);
		
		

		if(logger.isDebugEnabled()){
			logger.debug("更新订单["+ orderId+"]状态["+OrderStatus.ORDER_SHIP+"]，发货状态["+shipStatus+"]");
		}			
		//更新订单状态为已发货
		//2014-8-17 @author LiFenLong 增加增加发货时间
		baseDaoSupport.execute("update order set status=?,ship_status=?,sale_cmpl_time=? where order_id=?", OrderStatus.ORDER_SHIP,shipStatus,DateUtil.getDateline(),orderId);
		this.log(delivery.getOrder_id(), "订单发货，物流公司："+delivery.getLogi_name()+"，物流单据号："+ delivery.getLogi_no());
		
		//add by linyang  物流发货
		//orderId
		//发送物流公司和物流单号到快递100
		String logiNo = delivery.getLogi_no();
		Integer logiId = delivery.getLogi_id();
		String sql = "select * from es_logi_company where id = ?";
		List codeList = baseDaoSupport.queryForList(sql, logiId);
		String logiCode = "";
		if(codeList.size()>0)
		{
		    HashMap a = (HashMap) codeList.get(0);
		    logiCode = (String) a.get("code");
		}
		
		//add by linyang
	    String sqls = "select * from es_logi_status where com=? and nu=?";
	    ArrayList<Map> list = (ArrayList) baseDaoSupport.queryForList(sqls, logiCode,logiNo);
	    if(list.size() == 0)
	    {
    	    String sqli = "insert into es_logi_status(com,nu,orderid,status) values(?,?,?,?)";
    	    baseDaoSupport.execute(sqli,logiCode,logiNo,orderId,0);
	    }
	    if (smsOn) {
		    try {
				// 发送短信
				StringBuilder content = new StringBuilder();
				content.append("【国美国际酒业】亲爱的会员，您购买的宝贝").append(order.getSn());
				content.append("已经发货啦！电话畅通才能保证及时签收哦！（*∩_∩*）");
				logger.debug("短信内容: " + content);
				Member member = memberManager.get(order.getMember_id());
				if (member != null && StringUtils.isNotBlank(member.getMobile())) {
					SmsSender.sendSms(member.getMobile(), content.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    } else {
	    	logger.debug("未开启订单发货后短信通知功能");
	    }
	}
	
	
	//add by lin 
	public boolean updateLogiStatus()
	{
	    String sqls = "select * from  es_logi_status";
        ArrayList<Map> resultList = (ArrayList) baseDaoSupport.queryForList(sqls);
        String  com = "";
        String  nu = "";
        long orderid = 0;
        int status = 0;;
        for(Map result:resultList)
        {
            com = (String) result.get("com");
            nu = (String) result.get("nu");
            status = (int) result.get("status");
            if(status == 0)
            {
                TaskRequest req = new TaskRequest();
                //设置快递公司代码
                req.setCompany(com);
                //快递单号 
                req.setNumber(nu);
                //回调地址
                req.getParameters().put("callbackurl", kuaibackurl);
                req.setKey(kuaidicode);
                
                HashMap<String, String> p = new HashMap<String, String>(); 
                
                p.put("schema", "json");
                p.put("param", JSON.toJSONString(req));
                try {
                    String ret = HttpRequest.postData(kuaiposturl, p, "UTF-8");
                    TaskResponse resp = JacksonHelper.fromJSON(ret, TaskResponse.class);
                    if(resp.getResult()==true){
                        String sqlu = "update es_logi_status set status=1 where com = ? and nu = ?";
                        baseDaoSupport.execute(sqlu, com, nu);
                        System.out.println("订阅成功");
                    }else{
                        System.out.println("订阅失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
	    return true;
	}
	
	/**
	 * 读取某个订单、某个货物项的配货列表
	 * @param orderid
	 * @param itemid
	 * @return
	 */
	private List<AllocationItem> getAllocationList(int orderid,int itemid){
		String sql  ="select a.*,g.cat_id from "+ this.getTableName("allocation_item")+"  a ,"+ this.getTableName("goods")+" g where a.orderid=? and a.itemid=? and a.goodsid=g.goods_id ";
		return this.daoSupport.queryForList(sql, AllocationItem.class,orderid,itemid);
	}
	
	
	
	/**
	 * 处理商品退货
	 * @param orderId 订单id
	 * @param delivery_id 发货单id
	 * @param orderItemList 订单商品货物 {@link IOrderManager#listGoodsItems(Integer)}
	 * @param itemList 由用户表单读取的退货数据
	 * @return 商品退货状态
	 */
	private int processGoodsReturnItem(Integer orderId,Integer delivery_id,List<DeliveryItem> itemList){
		//this.fillAdjItem(orderId, itemList);
		List<OrderItem> orderItemList = this.orderManager.listGoodsItems(orderId);//此订单的相关商品货品
		int shipStatus = OrderStatus.SHIP_CANCEL;
		for(DeliveryItem item: itemList){
			
			if(item.getGoods_id() == null) throw new IllegalArgumentException(item.getName()+" goods id is  NULL");
			if(item.getProduct_id() == null) throw new IllegalArgumentException(item.getName()+" product id is  NULL");
			if(item.getNum()== null) throw new IllegalArgumentException(item.getName()+" num id is  NULL");
			
			if(logger.isDebugEnabled()){
				logger.debug("检测item["+item.getName()+"]退货数量是否合法");
			}			
			//检查退货数量是否合法，并得到这项的退货状态
			int itemStatus = this.checkGoodsReturnNum(orderItemList, item);
			
			//全部为退货完成则订单的发货状态为退货完成
			shipStatus=( shipStatus== OrderStatus.SHIP_CANCEL && itemStatus==OrderStatus.SHIP_CANCEL)?OrderStatus.SHIP_CANCEL:itemStatus;
			
			item.setDelivery_id(delivery_id);
			//记录退货明细
			this.baseDaoSupport.insert("delivery_item", item);
			
			//更新退货量
			baseDaoSupport.execute("update order_items set ship_num=ship_num-? where order_id=? and product_id=?", item.getNum(),orderId,item.getProduct_id());
			
			//更新库存量
			baseDaoSupport.execute("update goods set store=store+? where goods_id=?", item.getNum(),item.getGoods_id());
			baseDaoSupport.execute("update product set store=store+? where product_id=?", item.getNum(),item.getProduct_id());
			
		}		
		return shipStatus;
	}
	

	
	/**
	 * 处理商品发货项
	 * @param orderId
	 * @param delivery_id
	 * @param itemList
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	private int processGoodsShipItem(Integer orderId,Integer delivery_id, List<DeliveryItem> itemList){
		

		//此订单的相关货品
		List<Product> productList  =  listProductbyOrderId(orderId );
		List<OrderItem> orderItemList = this.orderManager.listGoodsItems(orderId);
	//	this.fillAdjItem(orderId, itemList);
		int shipStatus = OrderStatus.SHIP_YES;
		
		for(DeliveryItem item: itemList){
			
			if(item.getGoods_id() == null) throw new IllegalArgumentException(item.getName()+" goods id is  NULL");
			if(item.getProduct_id() == null) throw new IllegalArgumentException(item.getName()+" product id is  NULL");
			if(item.getNum()== null) throw new IllegalArgumentException(item.getName()+" num id is  NULL");
			
			if(logger.isDebugEnabled()){
				logger.debug("检测item["+item.getName()+"]发货数量是否合法");
			}			
			//检查发货数量是否合法，并得到这项的发货状态
			int itemStatus = this.checkGoodsShipNum(orderItemList, item);
			
			//全部为发货完成则订单的发货状态为发货完成
			shipStatus=( shipStatus== OrderStatus.SHIP_YES && itemStatus==OrderStatus.SHIP_YES)?OrderStatus.SHIP_YES:itemStatus;
			
			
			if(logger.isDebugEnabled()){
				logger.debug("检测item["+item.getName()+"]库存");
			}				
//			//检查库存
//			checkGoodsStore(orderId,productList,item);
//			
			item.setDelivery_id(delivery_id);
			//记录发货明细
			this.baseDaoSupport.insert("delivery_item", item);
	 
			 
			//更新发货量
			baseDaoSupport.execute("update order_items set ship_num=ship_num+? where order_id=? and product_id=?", item.getNum(),orderId,item.getProduct_id());
			
			
			//更新库存量
			
			
			baseDaoSupport.execute("update goods set store=store-? where goods_id=?", item.getNum(),item.getGoods_id());
			baseDaoSupport.execute("update product set store=store-? where product_id=?", item.getNum(),item.getProduct_id());
			baseDaoSupport.execute("update product_store set store=store-? where goodsid=? and productid=? and depotid=?",item.getNum(),item.getGoods_id(),item.getProduct_id(),item.getDepotId());
			
			if(this.logger.isDebugEnabled()){
				this.logger.debug("更新"+ item.getName()+"["+ item.getProduct_id()+","+item.getGoods_id()+"-["+item.getNum()+"]");
			} 
		}
		
//		this.updateDepotStore(orderId);
		return shipStatus;
	}

	


	/**
	 * 退货
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void returned(Delivery delivery, List<DeliveryItem> itemList,List<DeliveryItem> giftItemList) {
		if(delivery== null) throw new  IllegalArgumentException("param delivery is NULL");
		if(itemList== null) throw new  IllegalArgumentException("param itemList is NULL");
		if(delivery.getOrder_id()== null )  throw new IllegalArgumentException("param order id is null");
		
		if(delivery.getMoney()== null ) delivery.setMoney(0D);
		if(delivery.getProtect_price()==null) delivery.setProtect_price(0D);
		
		if(logger.isDebugEnabled()){
			logger.debug("订单["+delivery.getOrder_id()+"]退货");
		}
		
		Integer orderId = delivery.getOrder_id() ;
		Order order = this.orderManager.get(orderId);
		checkDisabled(order);
		if(order.getShip_status().intValue() ==  OrderStatus.SHIP_CANCEL){
			if(logger.isDebugEnabled()){
				logger.debug("订单["+order.getSn()+"]状态已经为【已退货】，不能再对其进行退货操作");
			}			
			throw new IllegalStateException("订单["+order.getSn()+"]状态已经为【已退货】，不能再对其进行退货操作");
		}
		
		if(delivery.getLogi_id()!=null){
			Logi logi = this.logiManager.getLogiById(delivery.getLogi_id());
			delivery.setLogi_code(logi.getCode());
			delivery.setLogi_name(logi.getName());
		}
		
		delivery.setType(2);
		delivery.setMember_id(order.getMember_id());
		delivery.setCreate_time(System.currentTimeMillis());
		baseDaoSupport.insert("delivery", delivery);
		Integer delivery_id = baseDaoSupport.getLastId("delivery");//产生的货运单id
		
	
		
		//处理退货状态
		int shipStatus = OrderStatus.SHIP_CANCEL;
		int goodsShipStatus = this.processGoodsReturnItem(orderId, delivery_id, itemList);
		shipStatus= goodsShipStatus;
	
		//如果退货完成则 退货状态为退货完成，否则为部分退货
		shipStatus = shipStatus==OrderStatus.SHIP_CANCEL ?OrderStatus.SHIP_CANCEL:OrderStatus.SHIP_PARTIAL_CANCEL;
		
		
		/**
		 * -----------------
		 * 激发退货事件
		 * -----------------
		 */
		this.orderPluginBundle.onReturned(delivery, itemList);
		
		
		if(logger.isDebugEnabled()){
			logger.debug("更新订单["+ orderId+"]状态["+OrderStatus.ORDER_CANCEL_SHIP+"]，发货状态["+shipStatus+"]");
		}			
		//更新订单状态为已退货
		baseDaoSupport.execute("update order set status=?,ship_status=? where order_id=?", OrderStatus.ORDER_CANCEL_SHIP,shipStatus,orderId);
		//baseDaoSupport.execute("update returns_order set state=4  where orderid=?",  orderId);
		
		this.log(delivery.getOrder_id(), "订单退货，物流单据号："+ delivery.getLogi_no());		
	}
	
	

	
	public void change(Delivery delivery,List<DeliveryItem> itemList,List<DeliveryItem> gifitemList){
		if(delivery== null) throw new  IllegalArgumentException("param delivery is NULL");
		if(itemList== null) throw new  IllegalArgumentException("param itemList is NULL");
		if(delivery.getOrder_id()== null )  throw new IllegalArgumentException("param order id is null");
		
		if(delivery.getMoney()== null ) delivery.setMoney(0D);
		if(delivery.getProtect_price()==null) delivery.setProtect_price(0D);
		
		if(logger.isDebugEnabled()){
			logger.debug("订单["+delivery.getOrder_id()+"]换货");
		}
		
		Integer orderId = delivery.getOrder_id() ;
		Order order = this.orderManager.get(orderId);
		checkDisabled(order);
 
		
		delivery.setType(3);
		delivery.setMember_id(order.getMember_id());
		delivery.setCreate_time(System.currentTimeMillis());
		baseDaoSupport.insert("delivery", delivery);
		
		if(logger.isDebugEnabled()){
			logger.debug("更新订单["+ orderId+"]状态["+OrderStatus.ORDER_CHANGED+"]");
		}			
		//更新订单状态为已换货
		baseDaoSupport.execute("update order set status=?, ship_status=? where order_id=?", OrderStatus.ORDER_CHANGED,OrderStatus.SHIP_CHANED,orderId);
		this.log(delivery.getOrder_id(), "订单换货，物流单据号："+ delivery.getLogi_no());
		
	}
	

	/**
	 * 检查退货量是否合法，并且计算退货状态
	 * @param orderItemList 购买的订单货物信息 @see {@link IOrderManager#listGoodsItems(Integer)}
	 * @param item 某一个发货项
	 * @return  
	 */
	private int checkGoodsReturnNum(List<OrderItem> orderItemList ,DeliveryItem item){
		
		int status =OrderStatus.SHIP_YES;
		for(OrderItem orderItem:orderItemList){
			
			if( orderItem.getProduct_id().compareTo(item.getProduct_id()) == 0){
			
				Integer shipNum = orderItem.getShip_num(); //已经发货量
				if(shipNum < item.getNum() ){ //已发货量小于本次 退货量
					if(logger.isDebugEnabled()){
						logger.debug("商品["+item.getName()+"]本次发货量["+item.getNum() +"]超出已发货量["+shipNum+"]");
					}						
					throw new RuntimeException("商品["+item.getName()+"]本次发货量["+item.getNum() +"]超出已发货量["+shipNum+"]");
				}
				if(shipNum.compareTo( item.getNum())==0 ){ //已发货量等于本次退货量，状态为已退货
					status= OrderStatus.SHIP_CANCEL;
				}
				
				if(shipNum >item.getNum()){ //已发货量大于本次退货量,状态为部分退货
					status= OrderStatus.SHIP_PARTIAL_CANCEL;
				}	
			}
		}
		return status;
	}
	

	/**
	 * 检测赠品退货量是否合法
	 * @param orderItemList 订单赠品货物列表 {@link IOrderManager#listGoodsItems(Integer)}
	 * @param item 由用户表单读取到的赠品退货数据
	 * @return
	 */
	private int checkGiftReturnNum(List<Map> orderItemList ,DeliveryItem item){
		
		int status =OrderStatus.SHIP_YES;
		for(Map orderItem:orderItemList){
			
			if( Integer.valueOf( orderItem.get("gift_id").toString()).compareTo(item.getGoods_id()) == 0){
				
				Integer shipNum = Integer .valueOf( orderItem.get("shipnum").toString() ); //已经发货量
				if(shipNum < item.getNum() ){ //已发货量小于本次 退货量
					if(logger.isDebugEnabled()){
						logger.debug("赠品["+item.getName()+"]本次发货量["+item.getNum() +"]超出已发货量["+shipNum+"]");
					}						
					throw new RuntimeException("赠品["+item.getName()+"]本次发货量["+item.getNum() +"]超出已发货量["+shipNum+"]");
				}
				if(shipNum.compareTo( item.getNum())==0 ){ //已发货量等于本次退货量，状态为已退货
					status= OrderStatus.SHIP_CANCEL;
				}
				
				if(shipNum >item.getNum()){ //已发货量大于本次退货量,状态为部分退货
					status= OrderStatus.SHIP_PARTIAL_CANCEL;
				}	
			}
		}
		return status;
		
	}

	
	/**
	 * 完成
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void complete(Integer orderId) {
		if(orderId== null ) throw new  IllegalArgumentException("param orderId is NULL");
		this.baseDaoSupport.execute("update order set status=? where order_id=?", OrderStatus.ORDER_COMPLETE,orderId);
		this.baseDaoSupport.execute("update order set complete_time=? where order_id=?", DateUtil.getDateline(),orderId);
		this.log(orderId, "订单完成");
	}

	/**
	 * 订单确认
	 */
	public void confirmOrder(Integer orderId) {
		if(orderId== null ) throw new  IllegalArgumentException("param orderId is NULL");
		Order order = orderManager.get(orderId);
		Member member = this.memberManager.get(order.getMember_id());
		
		this.daoSupport.execute("update es_order set status=?  where order_id=?", OrderStatus.ORDER_NOT_PAY ,orderId);
		
		
		//添加一条应收记录 20131110新增
		this.addPaymentIn(member, order);	
		//this.log(orderId, "订单确认");
		if(order.getNeedPayMoney()==0){
			this.payConfirm(order.getOrder_id());
		}
	}
	/**
	 * 添加应收记录
	 */
	private void addPaymentIn(Member member,Order order){
		 
        PaymentLog paymentLog =  new PaymentLog();
		 
		if(member!=null){
			paymentLog.setMember_id(member.getMember_id());
			paymentLog.setPay_user(member.getUname());
		}else{
			paymentLog.setPay_user("匿名购买者");
		}
//			paymentLog.setPay_date(com.enation.framework.util.DateUtil.getDateline());
		paymentLog.setMoney( order.getNeed_pay_money());		
		paymentLog.setOrder_sn(order.getSn());
		paymentLog.setSn("");
		paymentLog.setPay_method(order.getPayment_name());
		paymentLog.setOrder_id(order.getOrder_id());
		paymentLog.setType(PaymentLogType.receivable.getValue()); //应收
		paymentLog.setStatus(0);
		paymentLog.setCreate_time(DateUtil.getDateline());
		AdminUser adminUser  = UserConext.getCurrentAdminUser();
		if(adminUser!=null){
			paymentLog.setAdmin_user(adminUser.getRealname()+"["+adminUser.getUsername()+"]");
		}else if(member!=null){
			paymentLog.setAdmin_user(member.getName());
		}
		
		this.baseDaoSupport.insert("payment_logs", paymentLog);
	}
	
	public void addCodPaymentLog(Order order){
		if(order.getIsCod()){
           PaymentLog paymentLog =  new PaymentLog();
		Member member=null;
		if(order.getMember_id()!=null)
			member=memberManager.get(order.getMember_id());
			if(member!=null){
				paymentLog.setMember_id(member.getMember_id());
				paymentLog.setPay_user(member.getUname());
			}else{
				paymentLog.setPay_user("匿名购买者");
			}
			paymentLog.setPay_date(DateUtil.getDateline());
			paymentLog.setMoney( order.getOrder_amount());		
			paymentLog.setOrder_sn(order.getSn());
			paymentLog.setSn(orderManager.createSn());
			paymentLog.setPay_method("货到付款");
			paymentLog.setOrder_id(order.getOrder_id());
			paymentLog.setType(PaymentLogType.receivable.getValue());
			paymentLog.setStatus(0);
			paymentLog.setCreate_time(DateUtil.getDateline());
			this.baseDaoSupport.insert("payment_logs", paymentLog);
			}
	}

	/**
	 * 读取未发货的商品
	 */
	public List<OrderItem> listNotShipGoodsItem(Integer orderId) {
		String sql ="select oi.*,p.store,p.sn from "+this.getTableName("order_items")+"  oi left join "+this.getTableName("product")+" p on oi.product_id = p.product_id";
		sql += "  where order_id=? and oi.ship_num<oi.num";
		List<OrderItem> itemList  =this.daoSupport.queryForList(sql, OrderItem.class,orderId);
		this.orderPluginBundle.onFilter(orderId, itemList);
		return itemList;
	}
	
	
	public List<OrderItem > listNotAllocationGoodsItem(Integer orderid){
		String sql ="select oi.*,p.store,p.sn from "+this.getTableName("order_items")+"  oi  left join "+this.getTableName("product")+" p on oi.product_id = p.product_id";
		sql += " left join on "+this.getTableName("order")+" o on oi.order_id = o.order_id  where o.order_id=?";
		List<OrderItem> itemList  =this.daoSupport.queryForList(sql, OrderItem.class,orderid); 
		this.orderPluginBundle.onFilter(orderid, itemList);
		return itemList;
		
	} 
	
	
	public List<OrderItem> listShipGoodsItem(Integer orderId) {
		String sql ="select oi.*,p.store,p.sn from "+this.getTableName("order_items")+"  oi left join "+this.getTableName("product")+" p on oi.product_id = p.product_id";
		sql += "  where order_id=? and ship_num!=0";
		List<OrderItem> itemList  =this.daoSupport.queryForList(sql, OrderItem.class,orderId);
		this.orderPluginBundle.onFilter(orderId, itemList);
		return itemList;
	}
	
	
	
 
	
	
	
	/**
	 * 检查发货量是否合法，并且计发货状态
	 * @param orderItemList 购买的订单货物信息
	 * @param item 某一个发货项
	 * @return  
	 */
	private int checkGoodsShipNum(List<OrderItem> orderItemList ,DeliveryItem item){
		
		int status =OrderStatus.SHIP_NO;
		for(OrderItem orderItem:orderItemList){
			
			if( orderItem.getItem_id().compareTo(item.getOrder_itemid())==0){
				
				Integer num = orderItem.getNum(); //总购买量
				Integer shipNum = orderItem.getShip_num();//已经发货量
				if(num<item.getNum() + shipNum){ //总购买量小于总发货量
					if(logger.isDebugEnabled()){
						logger.debug("商品["+item.getName()+"]本次发货量["+item.getNum() +"]超出用户购买量["+num+"],此商品已经发货["+shipNum+"]");
					}						
					throw new RuntimeException("商品["+item.getName()+"]本次发货量["+item.getNum() +"]超出用户购买量["+num+"],此商品已经发货["+shipNum+"]");
				}
				if(num.compareTo(item.getNum() + shipNum)==0){ //总购买量等于总发货量
					status= OrderStatus.SHIP_YES;
				}
				
				if(num>item.getNum() + shipNum){ //总购买量大于总发货量
					status= OrderStatus.SHIP_PARTIAL_SHIPED;
				}	
			}
		}
		return status;
		
	}
	
	

	/**
	 * 检查赠品发货量是否合法，并且计算发货状态
	 * @param orderItemList 购买的订单赠品货物信息，对应order_gift表
	 * @param item 某一个发货项
	 * @return  
	 */
	private int checkGiftShipNum(List<Map> orderItemList ,DeliveryItem item){
		
		int status =OrderStatus.SHIP_NO;
		for(Map orderItem:orderItemList){
			
			if( Integer.valueOf( orderItem.get("gift_id").toString()).compareTo(item.getGoods_id())==0){
				
				Integer num = Integer .valueOf( orderItem.get("num").toString() ); //总购买量
				Integer shipNum = Integer .valueOf( orderItem.get("shipnum").toString() ); //已经发货量
				if(num<item.getNum() + shipNum){ //总购买量小于总发货量
					if(logger.isDebugEnabled()){
						logger.debug("赠品["+item.getName()+"]本次发货量["+item.getNum() +"]后超出用户购买量["+num+"],此商品已经发货["+shipNum+"]");
					}						
					throw new RuntimeException("赠品["+item.getName()+"]本次发货量["+item.getNum() +"]后超出用户购买量["+num+"],此商品已经发货["+shipNum+"]");
				}
				if(num.compareTo(item.getNum() + shipNum)==0){ //总购买量等于总购买量
					status= OrderStatus.SHIP_YES;
				}
				
				if(num>item.getNum() + shipNum){ //总购买量大于总购买量
					status= OrderStatus.SHIP_PARTIAL_SHIPED;
				}	
			}
		}
		return status;
		
	}
	
	
	
	
	
	
	/**
	 * 检查要发货商品的库存
	 */
	private void checkGoodsStore(Integer orderId,List<Product> productList ,DeliveryItem item){
		
		for(Product product : productList){
			if(product.getProduct_id().compareTo(item.getProduct_id())==0){
				if( product.getStore().compareTo(item.getNum())<0){ //库存小于发货量
					if(logger.isDebugEnabled()){
						logger.debug("商品["+item.getName()+"]库存["+ product.getStore()+"]不足->发货量["+item.getNum()+"]");
					}
					
					throw new RuntimeException("商品["+item.getName()+"]库存["+ product.getStore()+"]不足->发货量["+item.getNum()+"]");
				}
			}
		}
	}
	
	
	

	/**
	 * 读取某个订单的货品
	 * @param orderId
	 * @return
	 */
	private List<Product> listProductbyOrderId(Integer orderId){
		String sql = "select * from "+ this.getTableName("product")
		+" where product_id in (select product_id from "
		+ this.getTableName("order_items")+" where order_id=?)";
		List<Product> productList =  this.daoSupport.queryForList(sql, Product.class, orderId);
		return productList;
	}
	
	
	/**
	 * 检查订单状态是否在可操作状态
	 * @param order
	 * @throws IllegalStateException 如果订单是完成或作废状态
	 */
	private void checkDisabled(Order order){
		if(order.getStatus().intValue() ==  OrderStatus.ORDER_COMPLETE || order.getStatus().intValue() ==  OrderStatus.ORDER_CANCELLATION)
			throw new IllegalStateException("订单已经完成或作废，不能完成操作");
	}
	/**
	 * 更新配货完成
	 * @param allocationId 配货明细标识
	 * @param orderId
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateAllocation(int allocationId,int orderId){
//		String sql = "select (select count(allocationid)   from "+this.getTableName("allocation_item")+" where orderid ="+orderId+")=(select  sum(iscmpl)    from "+this.getTableName("allocation_item")+" where orderid ="+orderId+")";
//		int result = this.daoSupport.queryForInt(sql);
//		if(result==1){
//			sql = "update "+this.getTableName("order")+" set status="+OrderStatus.ORDER_ALLOCATION_YES+" where order_id="+orderId;
//			this.daoSupport.execute(sql);
//		}
		String sql = "update "+this.getTableName("allocation_item")+" set iscmpl=1 where allocationid="+allocationId;
		this.daoSupport.execute(sql);
		sql = "update  "+this.getTableName("order")+" set status="+OrderStatus.ORDER_ALLOCATION_YES+",ship_status="+OrderStatus.SHIP_NO+" where order_id="+orderId+" and (select count(allocationid) from "+this.getTableName("allocation_item")+" where orderid ="+orderId+")=(select sum(iscmpl) from  "+this.getTableName("allocation_item")+" where  orderid ="+orderId+")";
		this.daoSupport.execute(sql);
		
	}
	


	/**
	 * 确认付款
	 * @param orderId 订单标识
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Order payConfirm(int orderId){
		Order order = this.orderManager.get(orderId);
		
	
		double needPayMoney =order.getNeedPayMoney();// 订单应付
 
		
		/**
		 * 计算订单的状态，如果累计支付金额等于订单金额，则为已确认付款
		 */
		int 	payStatus = OrderStatus.PAY_CONFIRM;// 已付款
		int orderStatus = OrderStatus.ORDER_PAY_CONFIRM; 
		
		//判断是否已收货，如果已经收货，则更新订单状态为已完成
		if(order.getShip_status() == OrderStatus.SHIP_ROG){
			orderStatus =OrderStatus.ORDER_COMPLETE;
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("更新订单状态["+orderStatus+"],支付状态["+payStatus+ "]");
		}
		
	 
		String  sql = "update "+this.getTableName("order")+" set status="+orderStatus+",pay_status="+payStatus+"  where order_id=?";
		 this.daoSupport.execute(sql, order.getOrder_id());
		 
		 AdminUser adminUser = UserConext.getCurrentAdminUser();
		
		 String opuser = "系统";
		 if(adminUser!=null){
			 opuser  = adminUser.getUsername()+"["+adminUser.getRealname()+"]";
		 }
		 
		 sql="update payment_logs set status=1,pay_date=?,admin_user=? where order_id=?";//核销应收
		 this.baseDaoSupport.execute(sql,DateUtil.getDateline(),opuser,order.getOrder_id()); 
	
		if(adminUser!=null){
			this.log(orderId, "确认付款");
		}else{
			this.log(orderId, "确认付款", null, "系统");
		}
			
			
		order.setStatus( orderStatus);
		order.setPay_status( payStatus );
		this.orderPluginBundle.onConfirmPay(order);
		
		
		return order;
		
	}
	
	
	/**
	 * 确认收货
	 * @param orderId 订单标识
	 * @param op_id 操作员标识
	 * @param op_name 操作员名称
	 * @param sign_name 签收人
	 * @param sign_time 签收时间
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void rogConfirm(int orderId,long op_id,String op_name,String sign_name,Long sign_time){
		final Order order = this.orderManager.get(orderId);
		if(order.getStatus() != OrderStatus.ORDER_SHIP && order.getStatus() != OrderStatus.JSBJ_STATUS){
			//写入订单日志表
			this.log(orderId, "确认收货失败",op_id,op_name);
            return;
        }
		this.orderPluginBundle.onRogconfirm(order);
		//int orderstatus =OrderStatus.ORDER_ROG; //默认是收货状态 
		int orderstatus = OrderStatus.ORDER_SHIP;
		//sale_cmpl=1 销售生效
		String sql = "update order set status="+orderstatus +",ship_status="+OrderStatus.SHIP_ROG+",the_sign='"+sign_name+"',signing_time="+sign_time+" , sale_cmpl=1  where order_id="+orderId;
		this.baseDaoSupport.execute(sql);
		//写入订单日志表
		this.log(orderId, "确认收货成功",op_id,op_name);
		if(order.getMember_id()!=null){
			//写入会员购买商品表
			List<Map> itemList = this.orderManager.getItemsByOrderid(orderId);
			for (Map map : itemList) {
				this.baseDaoSupport.execute("INSERT INTO member_order_item(member_id,goods_id,order_id,item_id,commented,comment_time) VALUES(?,?,?,?,0,0)",
						order.getMember_id(),
						map.get("goods_id").toString(),
						map.get("order_id").toString(),
						map.get("item_id").toString());	
					
			}
//			if(order.getPay_status() == OrderStatus.PAY_CONFIRM){
//				//2014年10月17日，改为确认收货立即解冻积分
//				this.memberPointManger.thaw(orderId);
//			}
		}
		
		//起线程通知OMS
        new Thread(){
            @Override
            public void run(){
                erpManager.notifyOmsForDefault(order.getSn());
            }
        }.start();
	}
	
	/**
     * 确认收货
     * @param orderId 订单标识
     * @param op_id 操作员标识
     * @param op_name 操作员名称
     * @param sign_name 签收人
     * @param sign_time 签收时间
     */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void rogConfirmtg(int orderId, long op_id, String op_name,
			String sign_name, long sign_time, String status) {
        final Order order = this.orderManager.get(orderId);
        
        // 添加订单日志
        OrderLog orderLog = new OrderLog();
		orderLog.setMessage("订单["+orderId+"]状态已完成");
		orderLog.setOp_id(op_id);
		orderLog.setOp_name(op_name);
		orderLog.setOp_time(System.currentTimeMillis());
		orderLog.setOrder_id(orderId);
		this.baseDaoSupport.insert("order_log", orderLog);
        
        this.logger.info("确认结算：OrderFlowManager");
        //this.orderPluginBundle.onRogconfirm(order);
        this.confirmOrderPlugin.confirm(order.getOrder_id(), order.getOrder_amount());
        String sql = "update order set status=?, ship_status=?, the_sign=?, signing_time=?, complete_time=?, sale_cmpl=1 where order_id=?";
        this.baseDaoSupport.execute(sql, OrderStatus.ORDER_COMPLETE, OrderStatus.SHIP_ROG, sign_name, sign_time, sign_time, orderId);

         //写入会员购买商品表
        List<Map> itemList = this.orderManager.getItemsByOrderid(orderId);
        for (Map map : itemList) {
            this.baseDaoSupport.execute("INSERT INTO member_order_item(member_id,goods_id,order_id,item_id,commented,comment_time) VALUES(?,?,?,?,0,0)",
                    order.getMember_id(),
                    map.get("goods_id").toString(),
                    map.get("order_id").toString(),
                    map.get("item_id").toString());
			String sql2 ="update es_goods set buy_count=buy_count + "+map.get("num").toString()+" where goods_id =" +map.get("goods_id").toString() ;
			this.baseDaoSupport.execute(sql2);
			//删除缓存
			iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
			iCache.remove(String.valueOf(map.get("goods_id")));
        }
        //起线程通知OMS
        new Thread(){
            @Override
            public void run(){
                erpManager.notifyOmsForDefault(order.getSn());
            }
        }.start();
    }
    
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void autoRogConfirmtg(List<Integer> orderIds) {
		OrderLog orderLog = null;
		for (Integer orderid : orderIds) {
			orderLog = new OrderLog();
			orderLog.setMessage("系统检测到订单["+orderid+"]为完成状态");
			orderLog.setOp_id(0L);
			orderLog.setOp_name("系统检测");
			orderLog.setOp_time(System.currentTimeMillis());
			orderLog.setOrder_id(orderid);
			this.baseDaoSupport.insert("order_log", orderLog);
			this.rogConfirmtg(orderid, 0L, "系统检测", "系统", DateUtil.getDateline(), null);
		}
		
	}
	
	/**
	 * 订单自动作废.
	 * 
	 * @param orderId 订单ID
	 * @param cancel_reason 取消原因
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void cancel(Integer orderId,String cancel_reason) {
		if (orderId == null) throw new IllegalArgumentException("param orderId is NULL");
		Order order = this.orderManager.get(orderId);
		if (order == null || order.getStatus() == null || order.getStatus() != 0) return;
		if (order.getIs_erp_process() == 1) return;
		String sql = "update order set status=?,cancel_reason=? where order_id=? and status=0";
		this.baseDaoSupport.execute(sql, OrderStatus.ORDER_CANCELLATION, cancel_reason, orderId);
		// 如果所有子订单均已取消则将主订单取消
		if (order.getParent_id() != null) {
	        sql = "select count(1) from es_order where parent_id=? and status=0";
	        int count = this.baseDaoSupport.queryForInt(sql, order.getParent_id());
            if (count == 0) {
                //cancel main order
                String sql1 = "update es_order set status=? where order_id=?";
                this.daoSupport.execute(sql1, OrderStatus.ORDER_CANCELLATION, order.getParent_id());
            }
		} else {
		    //cancel children order
            sql = "update es_order set status=? where parent_id=?";
            this.daoSupport.execute(sql, OrderStatus.ORDER_CANCELLATION, order.getOrder_id());
		}
		if (order.getAdvance_pay() > 0 || order.getVirtual_pay() > 0) {
		    memberManager.topup(order.getMember_id(), order.getAdvance_pay(), order.getVirtual_pay(), order.getSn(), "自动取消订单");
		}
		this.log(orderId, "订单作废");
		
		this.orderPluginBundle.onCanel(order);
	}

	/**
	 * 订单取消
	 * @param sn 订单号
	 * @param reason 取消理由
	 */
	@Transactional(propagation = Propagation.REQUIRED)
    public void cancel(String sn,String reason){
		Order order = orderManager.get(sn);
		if(order == null){
			throw new RuntimeException("对不起，此订单不存在！");
		}
		if(order.getStatus() == null || order.getStatus().intValue() != 0){
			throw new RuntimeException("对不起，此订单不能取消！");
		}
		/*
		  add by lxl 如果订单未到erp ,可以取消订单，
		 */
		if(order.getIs_erp_process().intValue() == 1 ){
		    throw new RuntimeException("对不起，此订单不能取消！");
		}
	
		Member member = UserConext.getCurrentMember();
		if(order.getMember_id().intValue() !=member.getMember_id().intValue()){
			throw new RuntimeException("对不起，您没有权限进行此项操作！");
		}
		order.setStatus(OrderStatus.ORDER_CANCELLATION);
		order.setCancel_reason(reason);
		orderManager.edit(order);
		
		/************ 2015/10/16 ****************/
		// 如果所有子订单均已取消则将主订单取消
		if(order.getParent_id() != null) {
	        String sql = "select count(1) from es_order where parent_id=? and status=0";
	        int count = this.baseDaoSupport.queryForInt(sql, order.getParent_id());
            if (count == 0) {
                //cancel main order
                String sql1 = "update es_order set status=8 where order_id=?";
                this.daoSupport.execute(sql1, order.getParent_id());
            }
		} else {
		    //cancel children order
            String sql = "update es_order set status=8 where parent_id=?";
            this.daoSupport.execute(sql, order.getOrder_id());
		}
		
		// 将paymoney充到个人余额
	/*	if (order.getAdvance_pay() > 0 || order.getVirtual_pay() > 0 || order.getPaymoney() > 0) {
		    double total_advance =CurrencyUtil.add(order.getAdvance_pay(),order.getPaymoney());
            memberManager.topup(order.getMember_id(), total_advance, order.getVirtual_pay(), order.getSn(), "取消订单");
        }*/
		if (order.getAdvance_pay() > 0 || order.getVirtual_pay() > 0) {
		    memberManager.topup(order.getMember_id(), order.getAdvance_pay(), order.getVirtual_pay(), order.getSn(), "取消订单");
		}
		/****************************************/
		
		//记录日志
		if(member==null){
			this.log(order.getOrder_id(), "取消订单", null, "游客");
		}else{
			this.log(order.getOrder_id(), "取消订单", member.getMember_id(), member.getUname());
		}

		this.orderPluginBundle.onCanel(order);
	}
	
	/**
	 * 订单还原
	 * @param sn 订单号
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void restore(String sn) {
		Order order = orderManager.get(sn);
		if(order == null){
			throw new RuntimeException("对不起，此订单不存在！");
		}
		if(order.getStatus() == null || order.getStatus().intValue() != 8){
			throw new RuntimeException("对不起，此订单不能还原！");
		}
		
		Member member = UserConext.getCurrentMember();
		
		if(order.getMember_id().intValue() != member.getMember_id().intValue()){
			throw new RuntimeException("对不起，您没有权限进行此项操作！");
		}
		order.setStatus(0);
		order.setCancel_reason("");
		orderManager.edit(order);
		
		this.orderPluginBundle.onRestore(order);
	}
	
 
	
	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IPointHistoryManager getPointHistoryManager() {
		return pointHistoryManager;
	}

	public void setPointHistoryManager(IPointHistoryManager pointHistoryManager) {
		this.pointHistoryManager = pointHistoryManager;
	}



	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}


	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}


	public OrderPluginBundle getOrderPluginBundle() {
		return orderPluginBundle;
	}


	public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
		this.orderPluginBundle = orderPluginBundle;
	}


	public ILogiManager getLogiManager() {
		return logiManager;
	}


	public void setLogiManager(ILogiManager logiManager) {
		this.logiManager = logiManager;
	}


	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}


	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}


	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}


	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}

	public IOrderReportManager getOrderReportManager() {
		return orderReportManager;
	}


	public void setOrderReportManager(IOrderReportManager orderReportManager) {
		this.orderReportManager = orderReportManager;
	}

	public ErpManager getErpManager() {
		return erpManager;
	}

	public void setErpManager(ErpManager erpManager) {
		this.erpManager = erpManager;
	}




    
}
