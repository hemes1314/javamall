package com.enation.app.b2b2c.core.action.backend.bill;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.bill.BillAccount;
import com.enation.app.b2b2c.core.model.bill.BillDetail;
import com.enation.app.b2b2c.core.model.bill.BillStatusEnum;
import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.bill.IBillManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.shop.component.express.plugin.JacksonHelper;
import com.enation.framework.action.WWAction;

/**
 * 结算管理
 * @author fenlongli
 *
 */
@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/b2b2c/admin/bill/list.html"),
	 @Result(name="detail_list",type="freemarker", location="/b2b2c/admin/bill/detail_list.html"),
	 @Result(name="detail",type="freemarker", location="/b2b2c/admin/bill/detail.html"),
     //添加商家打款信息 chenzhongwei add
     @Result(name = "add", type = "freemarker", location = "/b2b2c/admin/bill/detail_add.html")
})
@Action("storeBill")
public class StoreBillAction extends WWAction{
	private Integer bill_id;
	private String sn;
	private Store store;
	private Integer status;
	private BillDetail billDetail;
	private IBillManager billManager;
	private IStoreManager storeManager;
	private String keyword;
	private Integer orderId;
	private String billAccountJSON; //待打款对象 chenzhongwei add
	private BillAccount billAccount; //待打款对象 chenzhongwei add
	
	/**
	 * 结算单列表
	 * @return
	 */
	public String list(){
		return "list";
	}
	/**
	 * 获取结算列表JSON
	 * @return
	 */
	public String list_json(){
		this.webpage=billManager.bill_list(this.getPage(), this.getPageSize(),keyword);
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	/**
	 * 结算详细列表
	 * @return 结算详细列表页
	 */
	public String detail_list(){
		return "detail_list";
	}
	/**
	 * 获取结算详细列表JSON
	 * @param bill_id 结算单Id
	 * @param keyword 店铺名称关键字
	 * @param webpage 结算详细列表
	 * @return 结算详细列表页JSON
	 */
	public String detail_list_json(){
		this.webpage=billManager.bill_detail_list(this.getPage(), this.getPageSize(), bill_id, keyword);
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	/**
	 * 获取结算单详细
	 * @param id 结算详细单Id
	 * @param billDetail 结算详细单
	 * @param store 店铺信息
	 * @return
	 */
	public String detail(){
		billDetail=this.billManager.get_bill_detail(bill_id);
		store=storeManager.getStore(billDetail.getStore_id());
		String sn = billDetail.getSn();
		//本期应收：￥0.00 = ￥0.00 (订单金额不含红包) - ￥0.00 (佣金金额) - ￥0.00 (退单金额) + ￥0.00 (退还佣金) + ￥212.50 (红包) - ￥93.30 (退还红包)
		//本期应收

		Double paymoney = billManager.getPaymoney(sn);
		billDetail.setBill_price(paymoney);//订单金额 实际付款金额

		Double commiPrice = billManager.getCommiPrice(sn);
		billDetail.setCommi_price(commiPrice);//佣金金额

		Double returnedPrice = 0.0;//billManager.getReturnedPrice(sn);//退单金额
		billDetail.setReturned_price(returnedPrice);

		Double returnedCommiPrice = 0.0;//billManager.getReturnedCommiPrice(sn);//退还佣金
		billDetail.setReturned_commi_price(returnedCommiPrice);

		Double redPacketsPrice = billManager.getRedPacketsPrice(sn);
		billDetail.setRed_packets_price(redPacketsPrice); //正向红包

		Double returnedRedPacketsPrice = 0.0;//billManager.getReturnedRedPacketsPrice(sn);
		billDetail.setReturned_red_packets_price(returnedRedPacketsPrice); //退还红包

		//￥0.00 = ￥0.00 (订单金额) - ￥0.00 (佣金金额) - ￥0.00 (退单金额) + ￥0.00 (退还佣金) + ￥0 (红包) - ￥0 (退还红包)
		billDetail.setPrice(paymoney - commiPrice - returnedPrice - returnedCommiPrice + redPacketsPrice -  returnedRedPacketsPrice);
		
		billAccount=billManager.getBillAccountInfo(bill_id);
		return "detail";
	}

	/**
	 * 修改结算详细状态
	 * @param status 结算单详细状态
	 * @param bill_id 结算详细单状态
	 * @return
	 */
	public String edit_bill_detail(){
	    BillAccount billAccount=null;
        try {
            //更新账单状态时 校验状态 chenzhongwei add
            BillDetail billDetail = billManager.get_bill_detail(bill_id);
            if(status == BillStatusEnum.COMPLETE.getIndex() && status == billDetail.getStatus()) {
                status = BillStatusEnum.PASS.getIndex();
                billManager.edit_billdetail_status(bill_id, status);
                this.showSuccessJson("更改状态成功");
            } else if(status == BillStatusEnum.PASS.getIndex() && status == billDetail.getStatus()) {
                if(StringUtils.isNotEmpty(billAccountJSON)) {
                    billAccount = JacksonHelper.fromJSON(billAccountJSON, BillAccount.class);
                    billAccount.setBill_id(bill_id);
                    billManager.saveBillAccountInfo(billAccount);
                }
                status = BillStatusEnum.PAY.getIndex();
                billManager.edit_billdetail_status(bill_id, status);
                this.showSuccessJson("更改状态成功");
            }else{
                if(status == BillStatusEnum.COMPLETE.getIndex()) {
                    this.showErrorJson("已经审核,请不要重复审核");
                } else if(status == BillStatusEnum.PASS.getIndex()) {
                    this.showErrorJson("已经结算,请不要重复审核");
                }
            }
        } catch (Exception e) {
			this.showErrorJson("更改状态失败，请重试");
			this.logger.error("更改结算单状态失败",e);
		}
		return this.JSON_MESSAGE;
		
	}
	/**
	 * 订单结算列表JSON
	 * @param page 分页页码
	 * @param pageSize 分页
	 * @param sn 结算单编号
	 * @return
	 */
	public String bill_order_list_json(){
		this.webpage=billManager.bill_order_list(this.getPage(), this.getPageSize(), sn);
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	/**
	 * 结算退货结算列表JSON
	 * @param page 分页页码
	 * @param pageSize 分页
	 * @param sn 结算单编号
	 */
	public String bill_sellback_list_json(){
		this.webpage=billManager.bill_sell_back_list(this.getPage(), this.getPageSize(), sn);
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}

	/**
	 * 订单结算列表中的佣金
	 * @return
     */
	public String bill_order_commission(){
		try{
			System.out.println(orderId);

			Double commission = 0D;
			//TODO MONSOON
			this.json="{\"result\":1,\"commission\":\""+commission+"\"}";
			//this.showSuccessJson("保存成功");
		}catch(RuntimeException e){
			this.logger.error(e.getMessage(), e);
			this.showErrorJson("保存失败");
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 订单结算列表中的红包
	 * @return
	 */
	public String bill_order_red_packets(){

		try{
			Double red_packets = 0D;
			//TODO MONSOON
			this.json="{\"result\":1,\"red_packets\":\""+red_packets+"\"}";
			//this.showSuccessJson("保存成功");
		}catch(RuntimeException e){
			this.logger.error(e.getMessage(), e);
			this.showErrorJson("保存失败");
		}
		return this.JSON_MESSAGE;
	}

    /**
     * 添加商家打款信息
     * 
     * @author chenzhongwei
     * @return 添加页面
     */
    public String add() {
        return "add";
    }

	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getBill_id() {
		return bill_id;
	}
	public void setBill_id(Integer bill_id) {
		this.bill_id = bill_id;
	}
	public IBillManager getBillManager() {
		return billManager;
	}
	public void setBillManager(IBillManager billManager) {
		this.billManager = billManager;
	}
	public BillDetail getBillDetail() {
		return billDetail;
	}
	public void setBillDetail(BillDetail billDetail) {
		this.billDetail = billDetail;
	}
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}
	public IStoreManager getStoreManager() {
		return storeManager;
	}
	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Integer getOrderId() {return orderId;}
	public void setOrderId(Integer orderId) {this.orderId = orderId;}
    
    public String getBillAccountJSON() {
        return billAccountJSON;
    }
    
    public void setBillAccountJSON(String billAccountJSON) {
        this.billAccountJSON = billAccountJSON;
    }
    
    public BillAccount getBillAccount() {
        return billAccount;
    }
    
    public void setBillAccount(BillAccount billAccount) {
        this.billAccount = billAccount;
    }
	
}
