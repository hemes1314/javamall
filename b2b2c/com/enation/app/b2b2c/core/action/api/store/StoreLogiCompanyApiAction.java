package com.enation.app.b2b2c.core.action.api.store;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.IStoreLogiCompanyManager;
import com.enation.framework.action.WWAction;
/**
 * 店铺物流公司API
 * @author fenlongli
 *
 */
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("storeLogiCompany")
public class StoreLogiCompanyApiAction extends WWAction{
	private Integer logi_id;
	
	private IStoreLogiCompanyManager storeLogiCompanyManager;
	/**
	 * 保存
	 * @return
	 */
	public String saveReal(){
		try {
			storeLogiCompanyManager.addRel(logi_id);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 关闭
	 * @return
	 */
	public String delReal(){
		try {
			storeLogiCompanyManager.deleteRel(logi_id);
			this.showSuccessJson("关闭成功");
		} catch (Exception e) {
			this.showErrorJson("关闭失败");
		}
		return this.JSON_MESSAGE;
	}
	
	//get set
	public Integer getLogi_id() {
		return logi_id;
	}
	public void setLogi_id(Integer logi_id) {
		this.logi_id = logi_id;
	}
	public IStoreLogiCompanyManager getStoreLogiCompanyManager() {
		return storeLogiCompanyManager;
	}
	public void setStoreLogiCompanyManager(
			IStoreLogiCompanyManager storeLogiCompanyManager) {
		this.storeLogiCompanyManager = storeLogiCompanyManager;
	}
	
}
