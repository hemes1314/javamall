package com.enation.app.shop.core.action.api;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.jms.EmailModel;
import com.enation.framework.jms.EmailProducer;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * @author LiFenLong
 * 会员找回密码
 */

@Component
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("findPasswordbyEmail")
public class MemberFindPasswordAction extends WWAction{
	private IMemberManager memberManager;
	private EmailProducer mailMessageProducer;
	private String email;
	private Integer memberid; 	//会员Id
	private String password;	//密码
	private String conpasswd;	//确认密码
	private String s;			//s码
	private Integer choose;		//选择找回方式
	
	/**
	 * 找回密码
	 * @param choose 找回方式.0为邮箱1.用户名
	 * @param email 邮箱或者用户名
	 * @param email
	 */
	public String find(){
		//获取站点信息
		EopSite  site  =EopSite.getInstance();
		//验证邮箱
		
		//获取用户信息By Email
		Member member =	new Member();
		if(choose==0){
			if(email==null || !StringUtil.validEmail(email)){
				this.showErrorJson("请输入正确的邮箱地址");
				return this.JSON_MESSAGE;
			}
			member=this.memberManager.getMemberByEmail(email);
		}else{
			member=this.memberManager.getMemberByUname(email);
		}	
		if(member==null){
			this.showErrorJson("["+ email +"]不存在!");
			return this.JSON_MESSAGE;
		}
		if(member.getEmail()==null){
		    this.showErrorJson("["+ email +"] 没有填写邮箱!");
            return this.JSON_MESSAGE;
		}
		String domain =RequestUtil.getDomain();
		String initCode = member.getMember_id()+","+member.getRegtime();
		String edit_url  =domain+ "/findPassword.html?s="+ EncryptionUtil1.authcode(initCode, "ENCODE","",0);
		
		//编辑邮件信息发送邮件
		EmailModel emailModel = new EmailModel();
		emailModel.getData().put("logo", site.getLogofile());
		emailModel.getData().put("sitename", site.getSitename());
		emailModel.getData().put("username", member.getUname());
		emailModel.getData().put("checkurl", edit_url);
		emailModel.setTitle("找回您的登录密码--"+site.getSitename());
		emailModel.setEmail(member.getEmail());
		emailModel.setTemplate("findpass_email_template.html");
		emailModel.setEmail_type("找回密码");
		mailMessageProducer.send(emailModel);
		
		this.memberManager.updateFindCode(member.getMember_id(),DateUtil.getDateline()+"");
		this.showSuccessJson("请登录"+ member.getEmail()+"查收邮件并完成密码修改。");
		return this.JSON_MESSAGE;
	}
	/**
	 * 修改密码
	 * @param password
	 * @param conpasswd
	 * @param s
	 */
	public String modify(){
		if(email==null || !StringUtil.validEmail(email)){
			this.showErrorJson("邮箱错误,请重试");
			return this.JSON_MESSAGE;
		}
		if(s==null){
			this.showErrorJson("非法链接地址,请重新找回");
			return this.JSON_MESSAGE;
		}
		String str = EncryptionUtil1.authcode(s, "DECODE","",0);
		String[] array = StringUtils.split(str,",");
		if(array.length!=2){
			this.showErrorJson("验证字串不正确,请重新找回");
			return this.JSON_MESSAGE;
		}
		int memberid  = Integer.valueOf(array[0]);
		long regtime = Long.valueOf(array[1]);
		
		Member member = this.memberManager.get(memberid);
		if(member==null || member.getRegtime() != regtime){
			this.showErrorJson("验证字串不正确,请重新找回");
			return this.JSON_MESSAGE;
		}
		if(member.getFind_code()==null||"".equals(member.getFind_code())||member.getFind_code().length()!=10){
			this.showErrorJson("地址已经过期,请重新找回");
			return this.JSON_MESSAGE;
		}
		int time = NumberUtils.toInt(member.getFind_code()) + 60 * 60;
		if(DateUtil.getDateline()>time){
			this.showErrorJson("地址已经过期,请重新找回");
			return this.JSON_MESSAGE;
		}
		if(!password.equals(conpasswd)){
			this.showErrorJson("密码不同");
			return this.JSON_MESSAGE;
		}
		this.memberManager.updatePassword(memberid, password);
		this.memberManager.updateFindCode(memberid, "");
		this.memberManager.login(member.getUname(), password);
		this.showSuccessJson("修改密码成功");
		return this.JSON_MESSAGE;
	}
	
	
	
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
	public EmailProducer getMailMessageProducer() {
		return mailMessageProducer;
	}
	public void setMailMessageProducer(EmailProducer mailMessageProducer) {
		this.mailMessageProducer = mailMessageProducer;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getMemberid() {
		return memberid;
	}
	public void setMemberid(Integer memberid) {
		this.memberid = memberid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConpasswd() {
		return conpasswd;
	}
	public void setConpasswd(String conpasswd) {
		this.conpasswd = conpasswd;
	}
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}
	public Integer getChoose() {
		return choose;
	}
	public void setChoose(Integer choose) {
		this.choose = choose;
	}
}
