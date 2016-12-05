package com.enation.framework.jms;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Smtp;
import com.enation.app.base.core.service.ISmtpManager;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.FreeMarkerUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;


import freemarker.template.Configuration;
import freemarker.template.Template;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;

/**
 * 
 * @author kingapex
 *
 */
public class EmailProcessor extends BaseSupport implements IJmsProcessor {
	
	private JavaMailSender mailSender;
	private ISmtpManager smtpManager;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void process(Object data) {
		
		EmailModel emailModel = (EmailModel)data;
		int emailid =0;
		try {
			
			Smtp smtp = smtpManager.getCurrentSmtp( );
			JavaMailSenderImpl javaMailSender=(JavaMailSenderImpl)mailSender;
			javaMailSender.setHost(smtp.getHost());
			javaMailSender.setUsername(smtp.getUsername());
			javaMailSender.setPassword(smtp.getPassword());
			
			
			MimeMessage message = mailSender.createMimeMessage();	
			MimeMessageHelper helper = new MimeMessageHelper(message, true,"UTF-8");
			
			helper.setSubject(emailModel.getTitle());
			helper.setTo(emailModel.getEmail());
			helper.setFrom(smtp.getMail_from());
		 			
			Configuration cfg =FreeMarkerUtil.getCfg();
			
			String app_path= StringUtil.getRootPath();
			String pageFolder = app_path+"/themes/";
			cfg.setDirectoryForTemplateLoading(new File(pageFolder));
			
			
			Template temp = cfg.getTemplate(emailModel.getTemplate());
			ByteOutputStream stream = new ByteOutputStream();

			Writer out = new OutputStreamWriter(stream);
			temp.process(emailModel.getData(), out);

			out.flush();
			String html = stream.toString();
			emailModel.setContent(html);
			
			helper.setText(html, true);
			
			
			//向库中插入
			emailid =this.addEmailList(emailModel);
			
			//发送邮件
			javaMailSender.send(message);
			
			this.smtpManager.sendOneMail(smtp);
			
			
		} catch (Exception e) {
		    e.printStackTrace();
			
			//如果发送失败，则记录
			if(emailid!=0){
				this.baseDaoSupport.execute("update email_list set is_success=0,error_num=error_num+1 where email_id=?", emailid);
				
			}
			this.logger.error("发送邮件出错",e);
		}
	}
	
	
	/**
	 * 向数据库中插入邮件队列，并返回邮件id
	 * @param emailModel
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	private int addEmailList(EmailModel emailModel){
		emailModel.setIs_success(1); //默认假设成功
		emailModel.setLast_send(DateUtil.getDateline());
		this.baseDaoSupport.insert("email_list", emailModel);
		return this.baseDaoSupport.getLastId("email_list");
	}
	
	public JavaMailSender getMailSender() {
		return mailSender;
	}
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	public ISmtpManager getSmtpManager() {
		return smtpManager;
	}
	public void setSmtpManager(ISmtpManager smtpManager) {
		this.smtpManager = smtpManager;
	}

	
}
