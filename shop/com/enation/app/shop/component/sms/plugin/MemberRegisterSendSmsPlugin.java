package com.enation.app.shop.component.sms.plugin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.ShortMsg;
import com.enation.app.shop.core.plugin.member.IMemberRegisterEvent;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.eop.SystemSetting;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.jms.EmailModel;
import com.enation.framework.jms.EmailProducer;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.sms.SmsSender;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.RequestUtil;

/**
 * 会员注册发送邮件插件
 * @author humaodong
 *
 */
@Component
public class MemberRegisterSendSmsPlugin extends AutoRegisterPlugin implements
		IMemberRegisterEvent {

	@Override
	public void onRegister(Member member) {
		System.out.println("onRegister: "+member.getMobile());
		int sms_isopen = SystemSetting.getSms_reg_open();
		if (sms_isopen == 1 && member.getMobile().length()>0){
			String domain =RequestUtil.getDomain();
			EopSite site  =EopSite.getInstance();
			String checkurl  = domain+"/memberemailcheck.html?s="+ EncryptionUtil1.authcode(member.getMember_id()+","+member.getRegtime(), "ENCODE","",0);
			
			ShortMsg sms = new ShortMsg();
			sms.setTarget(member.getMobile());
			sms.setContent(member.getUname()+"，恭喜您成功注册为【"+site.getSitename()+"】会员!");
			try {
				//SmsSender.sendSms(sms);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
