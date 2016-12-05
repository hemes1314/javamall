package com.enation.app.b2b2c.core.tag.bill;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.shop.core.model.Order;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.bill.IBillManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 店铺结算订单列表
 * @author fenlongli
 * @date 2015-6-8 下午2:50:39
 */
@Component
public class StoreBillOrderListTag extends BaseFreeMarkerTag{
	private IBillManager billManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		Integer pageSize=10;
		Integer pageNo = request.getParameter("page") == null ? 1 : NumberUtils.toInt(request.getParameter("page").toString());
		String sn=request.getParameter("sn").toString();
		Map result=new HashMap();
		Page orderList=billManager.bill_order_list(pageNo, pageSize, sn);
		result.put("page", pageNo);
		result.put("pageSize", pageSize);
		result.put("totalCount", orderList.getTotalCount());
		result.put("storeOrder", orderList);
		return result;
	}
	public IBillManager getBillManager() {
		return billManager;
	}
	public void setBillManager(IBillManager billManager) {
		this.billManager = billManager;
	}
}
