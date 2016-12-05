package com.enation.app.shop.core.service;

import java.math.BigDecimal;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.AdvanceLogs;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.plugin.payment.IPaySuccessProcessor;
import com.enation.framework.sms.SmsSender;

import freemarker.template.utility.StringUtil;
/**
 * 余额充值支付成功处理器
 * @author 2015/10/17 humaodong
 *
 */
@Component
public class TopupPaySuccessProcessor implements IPaySuccessProcessor {

	private IMemberManager memberManager;
	private IAdvanceLogsManager advanceLogsManager;
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.payment.IPaySuccessProcessor#paySuccess(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void paySuccess(String sn, String tradeno, String ordertype,BigDecimal totalFee) {
		String[] a = StringUtil.split(sn, 'M');
		if (a.length != 2) return;
		int memberId = NumberUtils.toInt(a[0]);
		String[] b = StringUtil.split(a[1], 'N');
		int cent = NumberUtils.toInt(b[0]);
		Double money = (double)cent/100.0d;
		
		if (advanceLogsManager.exists(memberId, tradeno)) return;
		
		AdvanceLogs log = memberManager.topup(memberId, money, 0.0d, tradeno, "现金充值");
		if (log != null) {    
		    //发送短信
		    Member member = memberManager.get(memberId);
		    String mobile = member.getMobile();
		    if (mobile != null && mobile.length() > 0) {
		        String content = "您已成功充值"+money+"元，充值后现金余额为："+member.getAdvance()+"元";
		        try {
                    SmsSender.sendSms(mobile, content);
                } catch(Exception e) {
                    e.printStackTrace();
                }
		    }
		}
	}

    @Override
	public void refundSuccess(Order order, BigDecimal refundFee) {
	}

	public IAdvanceLogsManager getAdvanceLogsManager() {
        return advanceLogsManager;
    }
    
    public void setAdvanceLogsManager(IAdvanceLogsManager advanceLogsManager) {
        this.advanceLogsManager = advanceLogsManager;
    }

    public IMemberManager getMemberManager() {
        return memberManager;
    }
    
    public void setMemberManager(IMemberManager memberManager) {
        this.memberManager = memberManager;
    }
}
