package com.enation.app.base.core.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Smtp;
import com.enation.app.base.core.service.ISmtpManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.StringUtil;

/**
 * smtp管理 
 * @author kingapex
 * @date 2011-11-1 下午12:27:51 
 * @version V1.0
 * @author LiFenLong 2014-4-1;4.0版本改造
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("smtp")
@Results({
	@Result(name="add", type="freemarker", location="/core/admin/smtp/add.html"), 
	@Result(name="edit", type="freemarker", location="/core/admin/smtp/edit.html"),
	@Result(name="list", type="freemarker", location="/core/admin/smtp/list.html")
})
public class SmtpAction extends WWAction {
	
	private ISmtpManager smtpManager;
	private Smtp smtp;
	private Integer[] id;
	private int smtpId;
	private int isedit ;
	private List<Smtp> smtpList;
	
	public String add(){
		isedit=0;
		return "add";
	}

	public String edit(){
		isedit=1;
		this.smtp = this.smtpManager.get(smtpId);
		return "edit";
	}
	public String saveAdd(){
		
		
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson(EopSetting.DEMO_SITE_TIP);
			return this.JSON_MESSAGE;
		}
		try{
			this.smtpManager.add(smtp);
			this.showSuccessJson("smtp添加成功");
		}catch(RuntimeException e){
			this.showErrorJson("smtp添加失败");
			logger.error("smtp添加失败",e);
		}
		return this.JSON_MESSAGE;
	}
	
	
	public String saveEdit(){
		
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson(EopSetting.DEMO_SITE_TIP);
			return this.JSON_MESSAGE;
		}
		try{
			if( StringUtil.isEmpty(smtp.getPassword()) ) {
				smtp.setPassword(this.smtpManager.get(smtp.getId()).getPassword()) ;
			}
			this.smtpManager.edit(smtp);
			this.showSuccessJson("smtp修改成功");
		}catch(RuntimeException e){
			this.showErrorJson("smtp修改失败");
			logger.error("smtp修改失败", e);
		}
		return this.JSON_MESSAGE;
	}
	  
	
	public String list(){
		 smtpList = this.smtpManager.list();		
		return "list";
	}
	/**
	 * @author LiFenLong
	 * @return
	 */
	public String listJson(){
		 smtpList = this.smtpManager.list();	
		 this.showGridJson(smtpList);
		 return this.JSON_MESSAGE;
	}

	public String delete(){
		
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson(EopSetting.DEMO_SITE_TIP);
			return this.JSON_MESSAGE;
		}
		
		
		try{
			this.smtpManager.delete(id);
			this.showSuccessJson("smtp删除成功");
		}catch(RuntimeException e){
			this.showErrorJson("smtp删除失败");
			this.logger.error("smtp删除失败", e);
		}
		return this.JSON_MESSAGE;
	}

	public ISmtpManager getSmtpManager() {
		return smtpManager;
	}

	public void setSmtpManager(ISmtpManager smtpManager) {
		this.smtpManager = smtpManager;
	}

	public Smtp getSmtp() {
		return smtp;
	}

	public void setSmtp(Smtp smtp) {
		this.smtp = smtp;
	}
	public Integer[] getId() {
		return id;
	}

	public void setId(Integer[] id) {
		this.id = id;
	}

	
	public int getSmtpId() {
		return smtpId;
	}

	public void setSmtpId(int smtpId) {
		this.smtpId = smtpId;
	}

	public int getIsedit() {
		return isedit;
	}

	public void setIsedit(int isedit) {
		this.isedit = isedit;
	}

	public List<Smtp> getSmtpList() {
		return smtpList;
	}

	public void setSmtpList(List<Smtp> smtpList) {
		this.smtpList = smtpList;
	}
	
}
