package com.enation.app.b2b2c.core.action.api.bill;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.bill.BillDetail;
import com.enation.app.b2b2c.core.model.bill.BillStatusEnum;
import com.enation.app.b2b2c.core.service.bill.IBillManager;
import com.enation.framework.action.WWAction;

/**
 * 结算API
 * @author fenlongli
 * @date 2015-6-7 下午4:05:25
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/bill")
@Action("storeBill")
public class BillApiAction extends WWAction{
	private Integer id;
	private IBillManager billManager;
	/**
	 * 确认结算
	 * @return
	 */
	public String confirm(){
		try {
            //当状态等于1的时候再更新 chenzhongwei add
            BillDetail billDetail = billManager.get_bill_detail(id);
            if(1 == billDetail.getStatus()) {
                billManager.edit_billdetail_status(id, BillStatusEnum.COMPLETE.getIndex());
                this.showSuccessJson("确认结算单成功");
            } else {
                this.showErrorJson("已经确认,请不要重复确认");
            }
		} catch (Exception e) {
			this.showErrorJson("确认结算单失败,请重试");
			this.logger.error("确定结算失败",e);
		}
		return this.JSON_MESSAGE;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public IBillManager getBillManager() {
		return billManager;
	}
	public void setBillManager(IBillManager billManager) {
		this.billManager = billManager;
	}
}
