package com.enation.app.shop.core.action.backend;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Depot;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.framework.action.WWAction;
/**
 * 库房管理Action
 * @author LiFenLong 2014-4-2;4.0版本改造
 *
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("depot")
@Results({
	@Result(name="add", type="freemarker", location="/shop/admin/depot/add.html"),
	@Result(name="edit", type="freemarker", location="/shop/admin/depot/edit.html"),
	@Result(name="list", type="freemarker", location="/shop/admin/depot/list.html") 
})
public class DepotAction extends WWAction {

	private IDepotManager depotManager;
	private Depot room;
	private int id;
	private List depotList;
	/**
	 * 跳转仓库列表
	 * @return 仓库列表页面
	 */
	public String list(){
		return "list";
	}
	/**
	 * 获取仓库列表json
	 * @param depotList 仓库列表
	 * @author LiFenLong
	 * @return 仓库列表json
	 */
	public String listJson(){
		depotList = this.depotManager.list();
		this.showGridJson(depotList);
		return this.JSON_MESSAGE;
	}
	/**
	 * 跳转添加仓库页面
	 * @return 添加仓库页面
	 */
	public String add(){
		return "add";
	}
	/**
	 * 跳转修改仓库页面
	 * @param id 仓库Id,Integer
	 * @param room 仓库对象,Depot
	 * @return 修改仓库页面
	 */
	public String edit(){
		room = this.depotManager.get(id);
		return "edit";
	}
	/**
	 * 添加仓库
	 * @param room 仓库对象,Depot
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String saveAdd(){
		try {
			this.depotManager.add(room);
			this.showSuccessJson("仓库新增成功");
		} catch (Exception e) {
			this.showErrorJson("仓库新增失败"+e.getMessage());
			logger.error("仓库新增失败", e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 修改仓库
	 * @param room 仓库对象,Depot
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String saveEdit(){
		try {
			this.depotManager.update(room);
			this.showSuccessJson("修改仓库成功");
		} catch (Exception e) {
			this.showErrorJson("修改仓库失败");
			logger.error("修改仓库失败", e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 删除仓库
	 * @param id 仓库Id
	 * @param message 判断仓库是否可以删除： 如果仓库为默认仓库，不能删除
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String delete(){
		try {
			String message= this.depotManager.delete(id);
			if(message.equals("删除成功")){
				this.showSuccessJson(message);
			}else{
				this.showErrorJson(message);
			}
		} catch (Exception e) {
			this.showErrorJson("仓库删除失败");
			logger.error("仓库删除失败", e);
		}
		return this.JSON_MESSAGE;
	}

	public IDepotManager getDepotManager() {
		return depotManager;
	}

	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}

	public Depot getRoom() {
		return room;
	}

	public void setRoom(Depot room) {
		this.room = room;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List getDepotList() {
		return depotList;
	}

	public void setDepotList(List depotList) {
		this.depotList = depotList;
	}
	

}
