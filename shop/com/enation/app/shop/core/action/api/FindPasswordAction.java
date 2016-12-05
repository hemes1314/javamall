package com.enation.app.shop.core.action.api;

import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.SmsMessage;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.ISmsManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.jms.EmailModel;
import com.enation.framework.jms.EmailProducer;
import com.enation.framework.sms.SmsSender;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.RequestUtil;

/**
 * 找回密码api
 * @author liuzy
 *
 */

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("findPassword")

public class FindPasswordAction extends WWAction {
	private ISmsManager smsManager;
	private IMemberManager memberManager;
	private EmailProducer mailMessageProducer;
	
	private String mobileNum;
	private String password;
	private String validcode;
	
	
	protected String createRandom(){
		Random random  = new Random();
		StringBuffer pwd=new StringBuffer();
		for(int i=0;i<6;i++){
			pwd.append(random.nextInt(9));
			 
		}
		return pwd.toString();
	}
	
	public String send(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String flag = (String) request.getSession().getAttribute("flag");
//		Long time_stamp = (Long) request.getSession().getAttribute("time_stamp");	//首次点击或者重复发送的时间
//		long current_time = DateUtil.getDateline();		//当前时间
		//判断是否是首次发送，或者重新发送 为空或者为0都为首次
		if(flag == "1"){
		    this.json = sendSmsCode();
//			//防止重复调用发送短信
//			if(CurrencyUtil.sub(current_time, time_stamp) >= 60){
//				request.getSession().setAttribute("time_stamp", DateUtil.getDateline());
//				this.json = sendSmsCode();
//			}else{
//				this.showErrorJson("请60秒后重试");
//			}
		}else{
			request.getSession().setAttribute("time_stamp", DateUtil.getDateline());
			request.getSession().setAttribute("flag","1");
			this.json = sendSmsCode();
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 检查邮箱(用户名)并发送短信验证码
	 * 需要传递mobileNum一个参数
	 * 
	 * @param mobileNum 手机号(用户名),String型
	 * 
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 为提示信息
	 */
	public String sendSmsCode() {
	    try {
	        Member member = memberManager.getMemberByMobile(mobileNum);
	        
	        if(member == null) {
	            this.showErrorJson("没有找到用户");
	        } else {
	            String code = "" + (int)((Math.random() * 9 + 1) * 100000);
	            String content = "您本次的验证码为："+ code;
	            HttpServletRequest request = ThreadContextHolder.getHttpRequest();
	            request.getSession().setAttribute("smscode", code);
	            request.getSession().setAttribute("smsnum", member.getMember_id());
	            
	            SmsSender.sendSms(mobileNum, content);          
	            this.showSuccessJson("短信发送成功");
	        }
	    } catch (Exception e) {
	        this.showErrorJson("短信发送失败");
	    }
		return json;
	}
	
	
	/**
	 * 检查用户输入的验证码
	 * 需要传入mobileNum一个参数
	 * 
	 * @param mobileNum 验证码,String型
	 *  
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 为提示信息
	 */
	public String checkSmsCode() {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String code = (String)request.getSession().getAttribute("smscode");
			Member member = memberManager.getMemberByMobile(mobileNum);
			if(member==null){
				this.showErrorJson("没有找到用户");
				return JSON_MESSAGE;
			}
			if(code.equals(validcode)) {
				request.getSession().setAttribute("smscode", "999");
				request.getSession().setAttribute("smsnum", member.getMember_id());
				this.showSuccessJson("验证成功");
			} else {
				this.showErrorJson("验证失败");
			}
		} catch (Exception e) {
			this.showErrorJson("验证失败");
		}
		
		return JSON_MESSAGE;
	}
	
	
	/**
	 * 验证通过后重置密码
	 * 需要传入mobileNum一个参数
	 * 
	 * @param mobileNum 新密码,String型
	 * 
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 为提示信息
	 */
	public String resetPassword() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		String code = (String)request.getSession().getAttribute("smscode");
		Integer memberid = (Integer)request.getSession().getAttribute("smsnum");
		if(memberid!=null && "999".equals(code)) {
			try {
				memberManager.updatePassword(memberid, password);
				request.getSession().setAttribute("smscode", null);
				this.showSuccessJson("新密码设置成功");
			} catch(Exception e) {
				this.showErrorJson("设置密码出错");
			}
			
		} else {
			this.showErrorJson("认证超时，请重新验证");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}
	
	public ISmsManager getSmsManager() {
		return smsManager;
	}

	public void setSmsManager(ISmsManager smsManager) {
		this.smsManager = smsManager;
	}
	
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public EmailProducer getMailMessageProducer() {
		return mailMessageProducer;
	}

	public void setMailMessageProducer(EmailProducer mailMessageProducer) {
		this.mailMessageProducer = mailMessageProducer;
	}

	public String getValidcode() {
		return validcode;
	}

	public void setValidcode(String validcode) {
		this.validcode = validcode;
	}
	
	public static void main(String[] args) {
		System.out.println(DateUtil.getDateline("2015-06-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		System.out.println(DateUtil.getDateline("2015-06-01 00:01:00", "yyyy-MM-dd HH:mm:ss"));
	}
	
}
