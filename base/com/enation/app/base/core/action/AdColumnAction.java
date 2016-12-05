package com.enation.app.base.core.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.service.IAdColumnManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;

/**
 * @author lzf
 * 2010-3-2 上午09:46:08
 * version 1.0
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("adColumn")
@Results({
	@Result(name="list", type="freemarker", location="/core/admin/adv/adc_list.html"),
	@Result(name="add", type="freemarker", location="/core/admin/adv/adc_input.html"), 
	@Result(name="edit", type="freemarker", location="/core/admin/adv/adc_edit.html") 
})
public class AdColumnAction extends WWAction {
	
	private IAdColumnManager adColumnManager;
	private AdColumn adColumn;
	private Long ac_id;
	private Integer[] acid;
	
	public String list() {
		return "list";
	}
	public String listJson() {
		this.webpage = this.adColumnManager.pageAdvPos(this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	public String detail(){
		adColumn = this.adColumnManager.getADcolumnDetail(ac_id);
		return "detail";
	}
	
	public String delete(){
		
		if(EopSetting.IS_DEMO_SITE){
			for(Integer id:acid){
				if(id<=21){
					this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
					return JSON_MESSAGE;
				}
			}
		}
		
		try {
			this.adColumnManager.delAdcs(acid);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("删除失败"+e.getMessage());
		}
		return JSON_MESSAGE;
	}
	
	public String add(){
		return "add";
	}
	
	public String addSave() {
		this.adColumnManager.addAdvc(adColumn);
		this.showSuccessJson("广告位添加成功");
		return JSON_MESSAGE;
	}
	
	public String edit(){
		adColumn = this.adColumnManager.getADcolumnDetail(ac_id);
		return "edit";
	}
	
	public String editSave(){
		this.adColumnManager.updateAdvc(adColumn);
		this.showSuccessJson("修改广告位成功");
		return JSON_MESSAGE;
	}

	public IAdColumnManager getAdColumnManager() {
		return adColumnManager;
	}

	public void setAdColumnManager(IAdColumnManager adColumnManager) {
		this.adColumnManager = adColumnManager;
	}

	public AdColumn getAdColumn() {
		return adColumn;
	}

	public void setAdColumn(AdColumn adColumn) {
		this.adColumn = adColumn;
	}

	

	public Long getAc_id() {
		return ac_id;
	}
	public void setAc_id(Long ac_id) {
		this.ac_id = ac_id;
	}
	public Integer[] getAcid() {
		return acid;
	}
	public void setAcid(Integer[] acid) {
		this.acid = acid;
	}
	
	

	
	
}
