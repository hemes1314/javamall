package com.enation.app.shop.core.action.backend;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.shop.component.spec.service.ISpecManager;
import com.enation.app.shop.component.spec.service.ISpecValueManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 商品规格操作action
 * 
 * @author kingapex 2010-3-8下午04:22:31
 */
public class GoodsSpecAction extends WWAction {
	private ISpecManager specManager;
	private IOrderManager orderManager;
	private ISpecValueManager specValueManager;
	private List specList;
	private Integer spec_id;
	private Integer value_id;
	private Map spec;
	private List valueList;

	public String execute() {
		specList = specManager.list();
		return "select";
	}

	public String getValues() {
		this.spec = this.specManager.get(spec_id);
		this.valueList = this.specValueManager.list(spec_id);
		return "values";
	}

	public String addOne() {
		spec = this.specValueManager.get(value_id);
		return "add_one";
	}
	
	public String closeSpec(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String action = request.getParameter("action");
		if ("check-pro-in-order".equals(action)) {
			int productid = StringUtil.toInt(request.getParameter("productid"),	true);
			boolean isinorder = this.orderManager.checkProInOrder(productid);
			if (isinorder) {
				this.showSuccessJson("有订单使用");
			} else {
				this.showErrorJson("无订单使用");
			}
		} else if ("check-goods-in-order".equals(action)) {
			int goodsid = StringUtil.toInt(request.getParameter("goodsid"),	true);
			boolean isinorder = this.orderManager.checkGoodsInOrder(goodsid);
			if (isinorder) {
				this.showSuccessJson("有订单使用");
			} else {
				this.showErrorJson("无订单使用");
			}
		}
		return JSON_MESSAGE;
	}
	

	public String addAll() {
		return "add_all";
	}

	public ISpecManager getSpecManager() {
		return specManager;
	}

	public void setSpecManager(ISpecManager specManager) {
		this.specManager = specManager;
	}

	public List getSpecList() {
		return specList;
	}

	public void setSpecList(List specList) {
		this.specList = specList;
	}

	public ISpecValueManager getSpecValueManager() {
		return specValueManager;
	}

	public void setSpecValueManager(ISpecValueManager specValueManager) {
		this.specValueManager = specValueManager;
	}

	public Map getSpec() {
		return spec;
	}

	public void setSpec(Map spec) {
		this.spec = spec;
	}

	public List getValueList() {
		return valueList;
	}

	public void setValueList(List valueList) {
		this.valueList = valueList;
	}

	public Integer getSpec_id() {
		return spec_id;
	}

	public void setSpec_id(Integer specId) {
		spec_id = specId;
	}

	public Integer getValue_id() {
		return value_id;
	}

	public void setValue_id(Integer valueId) {
		value_id = valueId;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	
 
 
}
