package com.enation.app.shop.component.sms.plugin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.plugin.order.IAfterOrderCreateEvent;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.sms.SmsSender;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;

/**
 * 订单邮件插件
 * @author kingapex
 * @date 2011-11-6 下午4:41:06 
 * @version V1.0
 */
@Component
public class OrderSmsPlugin extends AutoRegisterPlugin implements
		IAfterOrderCreateEvent {

	@Value("#{configProperties['order.new.sms.on']}")
    private boolean smsOn;
	
	@Override
	public void onAfterOrderCreate(Order order, List<CartItem>   itemList,String sessionid) {
		if (!smsOn) {
			logger.debug("未开启订单创建成功发送短信功能");
			return;
		}
	    if (!(order instanceof StoreOrder)) {
	       return;
	    }
		EopSite site  =EopSite.getInstance();
		Member member = UserConext.getCurrentMember();
		if(member!=null){
			String mobile =member.getMobile();
			if(StringUtil.isEmpty(mobile)){
				return ;
			}
			
			
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		 
			String domain =RequestUtil.getDomain();
			
			try {
				SmsSender.sendSms(mobile, "订单"+order.getSn()+"提交成功");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}
}
