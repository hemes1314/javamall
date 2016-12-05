package com.enation.app.shop.core.service;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.plugin.payment.IPaySuccessProcessor;

import java.math.BigDecimal;

/**
 * 众筹支付成功处理器
 * @author 2015/12/10 humaodong
 *
 */
@Component
public class CfPaySuccessProcessor implements IPaySuccessProcessor {

	private IMemberCfManager memberCfManager;
	
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.payment.IPaySuccessProcessor#paySuccess(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void paySuccess(String sn, String tradeno, String ordertype,BigDecimal totalFee) {
	    System.out.println("crowdfunding success: "+sn);
	    memberCfManager.setStatus(sn,"2"); //已支付
	}

    @Override
	public void refundSuccess(Order order, BigDecimal refundFee) {
		
	}

	public IMemberCfManager getMemberCfManager() {
        return memberCfManager;
    }

    
    public void setMemberCfManager(IMemberCfManager memberCfManager) {
        this.memberCfManager = memberCfManager;
    }
	
}
