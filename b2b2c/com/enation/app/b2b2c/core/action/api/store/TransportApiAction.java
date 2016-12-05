package com.enation.app.b2b2c.core.action.api.store;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreTemplateManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.action.WWAction;


@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("transport")
public class TransportApiAction extends WWAction {
	private IStoreTemplateManager storeTemplateManager;
	private IStoreMemberManager storeMemberManager;
	private Integer tempid;
	
	/**
	 * 删除
	 * @param tempid 模板名称
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String del(){
		try {			
			this.storeTemplateManager.delete(tempid);
			this.showSuccessJson("删除成功！");
		} catch (RuntimeException e) {
			this.showErrorJson(e.getMessage());
		} catch (Exception e) {
			this.showErrorJson("删除失败！");
		}
		return JSON_MESSAGE;
	}
	/**
	 * 设置默认模板
	 * @param tempid 模板名称
	 * @param member 店铺会员,StoreMember
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String setDefTemp(){
		try {
			StoreMember member=storeMemberManager.getStoreMember();
			this.storeTemplateManager.setDefTemp(tempid,member.getStore_id());
			this.showSuccessJson("设置成功！");
		} catch (Exception e) {
			this.showErrorJson("设置失败，请稍后重试！");
		}
		return JSON_MESSAGE;
	}

	public IStoreTemplateManager getStoreTemplateManager() {
		return storeTemplateManager;
	}

	public void setStoreTemplateManager(IStoreTemplateManager storeTemplateManager) {
		this.storeTemplateManager = storeTemplateManager;
	}


	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public Integer getTempid() {
		return tempid;
	}

	public void setTempid(Integer tempid) {
		this.tempid = tempid;
	}
	
}
