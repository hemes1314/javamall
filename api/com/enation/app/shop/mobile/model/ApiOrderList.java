package com.enation.app.shop.mobile.model;

import java.util.List;

import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

public class ApiOrderList {
	private Integer order_id;	//订单Id

	private String sn;			//订单编号

	private Long create_time;	//创建时间
	
	private Double order_amount;		//订单价格

	
	private Integer goods_num;	//商品数量
	
	private Integer status;		//订单状态
	
	private Integer pay_status;    //付款状态

	private String ship_no; //发货单

	private String is_storage; //是否存酒
    private Integer payment_id; //付款方式Id
    private String payment_name;    //付款方式名称
	//状态显示字串
    private String payStatus;   //付款状态字符串显示
    private String orderStatus; //订单状态字符串显示
    //订单项,非数据库字段
    private OrderItem orderItem;
	@PrimaryKeyField
	public Integer getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}
	public Integer getGoods_num() {
		return goods_num;
	}

	public void setGoods_num(Integer goodsNum) {
		goods_num = goodsNum;
	}

	public String getIs_storage() {
		return is_storage;
	}

	public void setIs_storage(String is_storage) {
		this.is_storage = is_storage;
	}
	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}

	public Double getOrder_amount() {
		
		return order_amount==null?0:order_amount;
	}

	public void setOrder_amount(Double order_amount) {
		this.order_amount = order_amount;
	}

	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getShip_no() {
		return ship_no;
	}

	public void setShip_no(String ship_no) {
		this.ship_no = ship_no;
	}

    
    public Integer getPay_status() {
        return pay_status;
    }

    
    public void setPay_status(Integer pay_status) {
        this.pay_status = pay_status;
    }

    @NotDbField
    public String getOrderStatus() {
        if(status==null) return null;
        orderStatus = OrderStatus.getOrderStatusText(status);
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    @NotDbField
    public String getPayStatus() {
 
        if(pay_status==null)return null;
        this.payStatus = OrderStatus.getPayStatusText(pay_status);
        
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    
    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    
    public Integer getPayment_id() {
        return payment_id;
    }

    
    public void setPayment_id(Integer payment_id) {
        this.payment_id = payment_id;
    }

    
    public String getPayment_name() {
        return payment_name;
    }

    
    public void setPayment_name(String payment_name) {
        this.payment_name = payment_name;
    }

  
}
