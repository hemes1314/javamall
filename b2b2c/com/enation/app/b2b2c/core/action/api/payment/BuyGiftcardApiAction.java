package com.enation.app.b2b2c.core.action.api.payment;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.shop.component.giftcard.model.GiftcardType;
import com.enation.app.shop.component.giftcard.service.IGiftcardTypeManager;
import com.enation.app.shop.core.model.MemberGiftcard;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.service.IMemberGiftcardManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;
/**
 * 店铺支付API
 * @author LiFenLong
 *
 */
@SuppressWarnings("serial")
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("buyGiftcardApi")
public class BuyGiftcardApiAction extends WWAction{  
    
    @Autowired
    private IPaymentManager paymentManager;
    
    @Autowired
    private IGiftcardTypeManager giftcardTypeManager;
    
    @Autowired
    private IMemberGiftcardManager memberGiftcardManager;
	
	/**
	 * 跳转到第三方支付页面
	 * @param money 充值金额
	 * @param paymentId 支付方式Id,Integer
	 * @param payCfg 支付方式,PayCfg
	 * @return html和脚本
	 */
	public String execute(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		StoreMember member=(StoreMember) ThreadContextHolder.getSessionContext().getAttribute("curr_store_member");
		if (member == null) {
		    this.json=("请先登录");
            return JSON_MESSAGE;
		}
		
		//支付方式id参数
		Integer paymentId=  StringUtil.toInt( request.getParameter("paymentId") ,null);
		if(paymentId == null ){
            this.json=("必须传递paymentId参数");
            return JSON_MESSAGE;
        }
		
		
	    Integer giftcardTypeId = StringUtil.toInt( request.getParameter("giftcardTypeId") ,null);
        if(giftcardTypeId == null ){
            this.json=("必须传递giftcardTypeId参数");
            return JSON_MESSAGE;
        }
        
        GiftcardType giftcardType = giftcardTypeManager.get(giftcardTypeId);
        if (giftcardType == null) {
            this.json=("无效的礼品卡类型");
            return JSON_MESSAGE;
        }
   
        MemberGiftcard card = new MemberGiftcard();
        card.setMember_id(member.getMember_id());
        card.setType_name(giftcardType.getType_name());
        card.setType_image(giftcardType.getType_image());
        card.setMoney(giftcardType.getMoney());
        card.setPrice(giftcardType.getPrice());
        String sn = member.getMember_id()+"G"+DateUtil.getDateline()+""+getRandom();
        card.setCard_sn(sn);
        card.setCreate_time(DateUtil.getDateline());
        card.setPayment_id(paymentId);
        memberGiftcardManager.create(card);
                
        PayCfg payCfg = this.paymentManager.get(paymentId);
        IPaymentEvent paymentPlugin = SpringContextHolder.getBean(payCfg.getType());
        String payhtml = paymentPlugin.onPay(payCfg, card);

        this.json=(payhtml);
		return JSON_MESSAGE;
	}
	
	public void pay() {
	    HttpServletRequest request = ThreadContextHolder.getHttpRequest();
	    Integer cardId = StringUtil.toInt( request.getParameter("id") ,null);
	    MemberGiftcard card = memberGiftcardManager.get(cardId);
	    
	    PayCfg payCfg = this.paymentManager.get(card.getPayment_id());
        IPaymentEvent paymentPlugin = SpringContextHolder.getBean(payCfg.getType());
        String payhtml = paymentPlugin.onPay(payCfg, card);
        this.renderHtml(payhtml);
	}
	
	
	
	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}
	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}
	/**
     * 获取随机数
     * @return
     */
    public  int getRandom(){
        Random random=new Random();
        int num=Math.abs(random.nextInt())%100;
        if(num<10){
            num=getRandom();
        }
        return num;
    }
}
