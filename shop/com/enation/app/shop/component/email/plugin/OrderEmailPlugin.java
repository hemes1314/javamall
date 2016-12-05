package com.enation.app.shop.component.email.plugin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.b2b2c.core.model.order.StoreOrder;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.plugin.order.IAfterOrderCreateEvent;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.jms.EmailModel;
import com.enation.framework.jms.EmailProducer;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;

/**
 * 订单邮件插件
 * @author kingapex
 * @date 2011-11-6 下午4:41:06 
 * @version V1.0
 */
@Component
public class OrderEmailPlugin extends AutoRegisterPlugin implements
		IAfterOrderCreateEvent {

	
	private EmailProducer mailMessageProducer; 
	/**
	 * member.email 会员邮箱
	 * member.uname 会员名称
	 * site.name 站点名称
	 * site.logofile 站点logo
	 * order.order_id 订单ID
	 * order.sn 订单编号
	 */
	@Override
	public void onAfterOrderCreate(Order order, List<CartItem> itemList,String sessionid) {

		//对子订单不发送邮件
		if (order instanceof StoreOrder) {
			return;
		}

		EopSite site  =EopSite.getInstance();
		Member member = UserConext.getCurrentMember();
		if(member!=null){
			String email =member.getEmail();
			if(StringUtil.isEmpty(email)){
				return ;
			}
			
			
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		 
			String domain =RequestUtil.getDomain();
			
			EmailModel emailModel = new EmailModel();
			emailModel.getData().put("username", member.getUname());
			emailModel.getData().put("sn", order.getSn());
			emailModel.getData().put("createtime", DateUtil.toString(order.getCreate_time(), "yyyy-MM-dd HH:mm:ss"));
			emailModel.getData().put("sitename", site.getSitename());
			emailModel.getData().put("logo", site.getLogofile());
			emailModel.getData().put("domain", domain);
			emailModel.getData().put("orderid", order.getOrder_id());
			 
			emailModel.setTitle("订单提交成功--"+site.getSitename());
			emailModel.setEmail(member.getEmail());
			emailModel.setTemplate("order_create_email_template.html");
			emailModel.setEmail_type("新订单成功提醒");
			mailMessageProducer.send(emailModel);
		}
		
	}

	public EmailProducer getMailMessageProducer() {
		return mailMessageProducer;
	}

	public void setMailMessageProducer(EmailProducer mailMessageProducer) {
		this.mailMessageProducer = mailMessageProducer;
	}

}
