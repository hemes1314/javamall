package com.enation.app.shop.component.giftcard.service;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.MemberGiftcard;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.plugin.payment.IPaySuccessProcessor;
import com.enation.app.shop.core.service.impl.MemberGiftcardManager;
import com.enation.framework.sms.SmsSender;

import freemarker.template.utility.StringUtil;
/**
 * 礼品卡购买支付成功处理器
 * @author 2015/10/16 humaodong
 *
 */
@Component
public class GiftcardPaySuccessProcessor implements IPaySuccessProcessor {

	private MemberGiftcardManager memberGiftcardManager;
	
    public MemberGiftcardManager getMemberGiftcardManager() {
        return memberGiftcardManager;
    }

    
    public void setMemberGiftcardManager(MemberGiftcardManager memberGiftcardManager) {
        this.memberGiftcardManager = memberGiftcardManager;
    }

    
    public IMemberManager getMemberManager() {
        return memberManager;
    }

    
    public void setMemberManager(IMemberManager memberManager) {
        this.memberManager = memberManager;
    }

    private IMemberManager memberManager;
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.payment.IPaySuccessProcessor#paySuccess(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void paySuccess(String cardsn, String tradeno, String ordertype, BigDecimal totalFee) {
		MemberGiftcard card = memberGiftcardManager.get(cardsn);
		if (card == null) return;
		
		//生成密码
		String pwd = card.getCard_pw();
		if (pwd == null || pwd.length() == 0) { //未支付
		    Random random=new Random();
	        int num=Math.abs(random.nextInt())%1000000;
		    pwd = Integer.toString(num);
		    StringUtil.leftPad(pwd, 6, '0');
		    card.setCard_pw(pwd);
		    memberGiftcardManager.updateCardPwd(card.getCard_id(), pwd);
		    
		    //发送短信
		    Member member = memberManager.get(card.getMember_id());
		    String mobile = member.getMobile();
		    if (mobile != null && mobile.length() > 0) {
		        String content = "您已成功购买"+card.getMoney()+"元面值的"+card.getType_name()+",卡号 "+card.getCard_sn()+" 密码 "+pwd+", 请注意查收";
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
}
