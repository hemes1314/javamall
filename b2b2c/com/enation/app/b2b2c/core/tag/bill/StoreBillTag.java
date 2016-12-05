package com.enation.app.b2b2c.core.tag.bill;

import java.util.Calendar;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.bill.BillDetail;
import com.enation.app.b2b2c.core.service.bill.IBillManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 结算单详细
 * @author fenlongli
 * @date 2015-5-21 下午4:04:10
 */
@Component
public class StoreBillTag extends BaseFreeMarkerTag{
	private IBillManager billManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer id = NumberUtils.toInt(params.get("id").toString());
		
		BillDetail bd =  billManager.get_bill_detail(id);
		//测试说不需要判断 chenzhongwei update
		/*Integer day = Calendar.getInstance().DAY_OF_MONTH;
		//如果日期非 1到 5号。那么写入状态为0 ，意义为 非0-5号不能申请结算
		 if(0<day&&day<=5){
			 bd.setStatus(0);
		 }*/

		String sn = bd.getSn();
		//本期应收：￥0.00 = ￥0.00 (订单金额不含红包) - ￥0.00 (佣金金额) - ￥0.00 (退单金额) + ￥0.00 (退还佣金) + ￥212.50 (红包) - ￥93.30 (退还红包)
		//本期应收

		Double paymoney = billManager.getPaymoney(sn);
		bd.setBill_price(paymoney);//订单金额 实际付款金额

		Double commiPrice = billManager.getCommiPrice(sn);
		bd.setCommi_price(commiPrice);//佣金金额

		Double returnedPrice = 0.0;//billManager.getReturnedPrice(sn);//退单金额
		bd.setReturned_price(0.0);//returnedPrice

		Double returnedCommiPrice = 0.0;//billManager.getReturnedCommiPrice(sn);//退还佣金
		bd.setReturned_commi_price(0.0);//returnedCommiPrice

		Double redPacketsPrice = billManager.getRedPacketsPrice(sn);
		bd.setRed_packets_price(redPacketsPrice); //正向红包

		Double returnedRedPacketsPrice = 0.0;//billManager.getReturnedRedPacketsPrice(sn);
		bd.setReturned_red_packets_price(0.0); //退还红包 returnedRedPacketsPrice
		if(returnedPrice==null)returnedPrice=0.0;
		//￥0.00 = ￥0.00 (订单金额) - ￥0.00 (佣金金额) - ￥0.00 (退单金额) + ￥0.00 (退还佣金) + ￥0 (红包) - ￥0 (退还红包)
		bd.setPrice(paymoney - commiPrice - returnedPrice - returnedCommiPrice + redPacketsPrice -  returnedRedPacketsPrice);
		return bd;
	}
	public IBillManager getBillManager() {
		return billManager;
	}
	public void setBillManager(IBillManager billManager) {
		this.billManager = billManager;
	}
}
