package com.enation.app.shop.core.tag;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 支付列表标签
 * @author lina
 * 2012-2-19
 */
@Component
@Scope("prototype")
public class ShopPaymentListTag extends BaseFreeMarkerTag {
	private IPaymentManager paymentManager;
	/**
	 * 支付列表标签
	 * @param无
	 * @return list {@link PayCfg}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		//读取支付方式列表
		return this.paymentManager.list();
	}
	
	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}
	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

}
