package com.enation.app.b2b2c.core.action.backend.store;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.store.StoreLevel;
import com.enation.app.b2b2c.core.service.store.IStoreLevelManager;
import com.enation.framework.action.WWAction;
@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="storelevel",type="freemarker", location="/b2b2c/admin/level/store_level_list.html"),
	 @Result(name="info",type="freemarker", location="/b2b2c/admin/level/store_level_info.html")
})
@Action("storelevel")
public class StoreLevelAction extends WWAction{

	private IStoreLevelManager storeLevelManager;
	private Integer level_id;
	private StoreLevel storeLevel;
	private String level_name;
	/**
	 * 店铺等级列表
	 * @return
	 */
	public String storeLevel(){
		return "storelevel";
	}
	public String storeLevelJson(){
		this.showGridJson(storeLevelManager.storeLevelList());
		return this.JSON_MESSAGE;
	}
	/**
	 * 跳转到店铺等级详细页
	 * @return
	 */
	public String add(){
		return "info";
	}
	public String edit(){
		storeLevel=storeLevelManager.getStoreLevel(level_id);
		return "info";
	}
	/**
	 * 删除店铺等级
	 * @return
	 */
	public String delStoreLevel(){
		try {
			storeLevelManager.delStoreLevel(level_id);
			this.showSuccessJson("删除店铺等级成功");
		} catch (Exception e) {
			this.showErrorJson("删除店铺等级失败");
			this.logger.error("删除店铺等级失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 添加店铺等级 
	 * @return
	 */
	public String addStoreLevel(){
		try {
			storeLevelManager.addStoreLevel(level_name);
			this.showSuccessJson("添加店铺等级成功");
		} catch (Exception e) {
			this.showErrorJson("添加店铺等级失败");
			this.logger.error("添加店铺等级失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 修改店铺等级
	 * @return
	 */
	public String editStoreLevel(){
		try {
			storeLevelManager.editStoreLevel(storeLevel.getLevel_name(), storeLevel.getLevel_id());
			this.showSuccessJson("修改店铺等级成功");
		} catch (Exception e) {
			this.showErrorJson("修改店铺等级失败");
			this.logger.error("修改点评等级失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	public IStoreLevelManager getStoreLevelManager() {
		return storeLevelManager;
	}
	public void setStoreLevelManager(IStoreLevelManager storeLevelManager) {
		this.storeLevelManager = storeLevelManager;
	}
	public StoreLevel getStoreLevel() {
		return storeLevel;
	}
	public void setStoreLevel(StoreLevel storeLevel) {
		this.storeLevel = storeLevel;
	}
	public String getLevel_name() {
		return level_name;
	}
	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}
	public Integer getLevel_id() {
		return level_id;
	}
	public void setLevel_id(Integer level_id) {
		this.level_id = level_id;
	}
}
