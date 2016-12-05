package com.enation.app.b2b2c.core.action.api.payment;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.component.giftcard.model.GiftcardType;
import com.enation.app.shop.component.giftcard.service.IGiftcardTypeManager;
import com.enation.app.shop.core.model.MemberVitem;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.model.VirtualProduct;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.service.IMemberGiftcardManager;
import com.enation.app.shop.core.service.IMemberVitemManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.impl.VirtualProductManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;
/**
 * 兑换虚拟物品
 * @author humaodong
 *
 */
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("exchangeVitemApi")
public class ExchangeVitemApiAction extends WWAction{
    
    @Autowired
    private VirtualProductManager virtualProductManager;
    
    @Autowired
    private IMemberVitemManager memberVitemManager;

    @Autowired
    private IMemberManager memberManager;

    @Transactional(propagation = Propagation.REQUIRED)
    public String execute() throws Exception{
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		StoreMember member=(StoreMember) ThreadContextHolder.getSessionContext().getAttribute("curr_store_member");
		if (member == null) {
		    this.showErrorJson("请先登录");
            return this.JSON_MESSAGE;
		}
		
	    Integer typeId = StringUtil.toInt( request.getParameter("type_id") ,null);
        if(typeId == null ){
            this.showErrorJson("必须传递type_id参数");
            return this.JSON_MESSAGE;
        }
        
        MemberVitem vitem = memberVitemManager.getByTypeId(typeId, member.getMember_id());
        if (vitem == null) {
            this.showErrorJson("无效的虚拟物品");
            return this.JSON_MESSAGE;
        }
            
        Integer num = StringUtil.toInt( request.getParameter("num"), 1);
        if (num > vitem.getNum()) {
            this.showErrorJson("虚拟物品不足");
            return this.JSON_MESSAGE;
        }
        try {
            memberVitemManager.sub(typeId, num, member.getMember_id());
            memberManager.topup(member.getMember_id(), 0.0, (double)vitem.getPrice()*num, vitem.getType_name()+" x "+num, "虚拟物品");
            this.showSuccessJson("ok");
        } catch(Exception e) {
            throw e;
        }
		return this.JSON_MESSAGE;
	}
	
    
    public VirtualProductManager getVirtualProductManager() {
        return virtualProductManager;
    }

    public void setVirtualProductManager(VirtualProductManager virtualProductManager) {
        this.virtualProductManager = virtualProductManager;
    }


    
    
    public IMemberVitemManager getMemberVitemManager() {
        return memberVitemManager;
    }


    
    public void setMemberVitemManager(IMemberVitemManager memberVitemManager) {
        this.memberVitemManager = memberVitemManager;
    }


    public IMemberManager getMemberManager() {
        return memberManager;
    }


    
    public void setMemberManager(IMemberManager memberManager) {
        this.memberManager = memberManager;
    }
}
