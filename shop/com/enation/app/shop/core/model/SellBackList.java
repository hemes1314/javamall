package com.enation.app.shop.core.model;

import com.enation.framework.util.CurrencyUtil;

/**
 * 退货单
 * @author lina
 *2013-11-10上午10:16:43
 */
public class SellBackList {
    
    private Integer id;             //ID
    private String tradeno;         //退货单号
    private Integer tradestatus;    //状态 0待审核。1.审核成功代发货.6拒绝申请
    private String ordersn;         //订单号
    private String regoperator;     //操作员
    private Long regtime;           //创建时间
    private Double alltotal_pay;    //退款金额
    private String goodslist;       //商品列表
    private String seller_remark;   //客服备注
    private String warehouse_remark;//库管备注
    private String finance_remark;  //财务备注
    private Long member_id;      //退货人会员id
    private String sndto;           //退货人
    private String tel;             //电话
    private String adr;             //地址
    private String zip;             //邮编
    private Double total;           //总数
    private Integer depotid;        //仓库Id
    private String refund_way;      //退款方式
    private String return_account;  //退款账户
    private String remark;          //备注
    private Double return_price; // 退货金额
    private String return_address; // 退货地址
    private String kddh; // 快递单号
    private String wlgs; // 物流公司
    private Integer tythstatus; // 同意退货
    private Long return_time; // 审核通过日期
    private Long refund_time;	//实际退款时间
    private Integer orderid;// 订单id
 
    public Integer getOrderid() {
        return orderid;
    }

    public void setOrderid(Integer orderid) {
        this.orderid = orderid;
    }



    public Integer getTythstatus() {
        return tythstatus;
    }
    
    public void setTythstatus(Integer tythstatus) {
        this.tythstatus = tythstatus;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTradeno() {
        return tradeno;
    }
    public void setTradeno(String tradeno) {
        this.tradeno = tradeno;
    }
    public Integer getTradestatus() {
        return tradestatus;
    }
    public void setTradestatus(Integer tradestatus) {
        this.tradestatus = tradestatus;
    }
    public String getOrdersn() {
        return ordersn;
    }
    public void setOrdersn(String ordersn) {
        this.ordersn = ordersn;
    }
    public String getRegoperator() {
        return regoperator;
    }
    public void setRegoperator(String regoperator) {
        this.regoperator = regoperator;
    }
    public Long getRegtime() {
        return regtime;
    }
    public void setRegtime(Long regtime) {
        this.regtime = regtime;
    }
    public String getGoodslist() {
        return goodslist;
    }
    public void setGoodslist(String goodslist) {
        this.goodslist = goodslist;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getSeller_remark() {
        return seller_remark;
    }
    public void setSeller_remark(String seller_remark) {
        this.seller_remark = seller_remark;
    }
    public String getWarehouse_remark() {
        return warehouse_remark;
    }
    public void setWarehouse_remark(String warehouse_remark) {
        this.warehouse_remark = warehouse_remark;
    }
    public String getFinance_remark() {
        return finance_remark;
    }
    public void setFinance_remark(String finance_remark) {
        this.finance_remark = finance_remark;
    }
    public Long getMember_id() {
        return member_id;
    }
    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }
    public String getSndto() {
        return sndto;
    }
    public void setSndto(String sndto) {
        this.sndto = sndto;
    }
    public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }
    public String getAdr() {
        return adr;
    }
    public void setAdr(String adr) {
        this.adr = adr;
    }
    public String getZip() {
        return zip;
    }
    public void setZip(String zip) {
        this.zip = zip;
    }
    public Double getTotal() {
        return total;
    }
    public void setTotal(Double total) {
        this.total = total;
    }
    public Integer getDepotid() {
        return depotid;
    }
    public void setDepotid(Integer depotid) {
        this.depotid = depotid;
    }
    public String getRefund_way() {
        return refund_way;
    }
    public void setRefund_way(String refund_way) {
        this.refund_way = refund_way;
    }
    public String getReturn_account() {
        return return_account;
    }
    public void setReturn_account(String return_account) {
        this.return_account = return_account;
    }
    public Double getAlltotal_pay() {
        return alltotal_pay;
    }
    public void setAlltotal_pay(Double alltotal_pay) {
        this.alltotal_pay = alltotal_pay;
    }
    
    public String getReturn_address() {
        return return_address;
    }
    
    public void setReturn_address(String return_address) {
        this.return_address = return_address;
    }

    public Double getReturn_price() {
        if(return_price != null) {
            return_price = CurrencyUtil.round(return_price, 2);
        } else {
            return 0.00d;
        }

        return return_price;
    }

    public void setReturn_price(Double return_price) {
        this.return_price = return_price;
    }

    public String getKddh() {
        return kddh;
    }
    
    public void setKddh(String kddh) {
        this.kddh = kddh;
    }
    
    public String getWlgs() {
        return wlgs;
    }
    
    public void setWlgs(String wlgs) {
        this.wlgs = wlgs;
    }

    public Long getReturn_time() {
        return return_time;
    }

    public void setReturn_time(Long return_time) {
        this.return_time = return_time;
    }

	public Long getRefund_time() {
		return refund_time;
	}

	public void setRefund_time(Long refund_time) {
		this.refund_time = refund_time;
	}
}
