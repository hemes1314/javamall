package com.enation.app.groupbuy.core.action.backend;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.groupbuy.core.model.GroupBuyArea;
import com.enation.app.groupbuy.core.service.IGroupBuyAreaManager;
import com.enation.framework.action.WWAction;

/**
 * 
 * @ClassName: GroupBuyAreaAction 
 * @Description: 团购地区管理action 
 * @author TALON 
 * @date 2015-7-31 上午1:30:18 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/groupbuy/area/area_list.html"),
	 @Result(name="add",type="freemarker", location="/groupbuy/area/area_add.html"),
	 @Result(name="edit",type="freemarker", location="/groupbuy/area/area_edit.html")
})

@Action("groupBuyArea")
public class GroupBuyAreaAction extends WWAction {
	
	private IGroupBuyAreaManager groupBuyAreaManager;
	private String  area_name;      
	private String  area_path;      
	private int  area_order;
	private Integer[] area_id;
	
	private GroupBuyArea  groupBuyArea;
	
	private  List catList;
	/**
	 * 跳转至团购地区列表	
	 * @return
	 */
	public String list(){
		return "list";
	}
	
	/**
	 * 团购地区分页列表json
	 * @param webpage 团购地区分页
	 * @return
	 */
	public String list_json(){
		this.webpage=this.groupBuyAreaManager.list(this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}
	/**
	 * 跳转至团购地区
	 * @return
	 */
	public String add(){
		return "add";
	}
	
	/**
	 * 
	 * @Title: saveAdd
	 * @Description: 添加团购地区
	 * @param groupBuyArea 团购地区
	 * @param area_name 地区名称
	 * @param area_order 排序
	 * @return json 1为成功.0为失败
	 */
	public String saveAdd(){
		try {
			GroupBuyArea groupBuyArea = new GroupBuyArea();
			groupBuyArea.setArea_name(area_name);
			groupBuyArea.setArea_order(area_order);
			this.groupBuyAreaManager.add(groupBuyArea);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败"+e.getMessage());
			this.logger.error("添加失败", e);
		}
		
		return this.JSON_MESSAGE;
	}
	/**
	 * 跳转至修改团购地区
	 * @param groupBuyArea 团购地区
	 * @param area_id 团购地区Id数组
	 * @return
	 */
	public String edit(){
		this.groupBuyArea = this.groupBuyAreaManager.get(area_id[0]);
		return "edit";
	}
	/**
	 * 
	 * @Title: saveEdit
	 * @Description: 保存修改团购地区
	 * @param groupBuyArea 团购地区
	 * @param area_id 团购地区Id数组
	 * @param area_name 地区名称
	 * @param area_order 团购地区排序
	 * @return json 1为成功.0为失败
	 */
	public String saveEdit(){
		try {
			GroupBuyArea groupBuyArea = new GroupBuyArea();
			groupBuyArea.setArea_id(area_id[0]);
			groupBuyArea.setArea_name(area_name);
			groupBuyArea.setArea_order(area_order);
			this.groupBuyAreaManager.update(groupBuyArea);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败"+e.getMessage());
			this.logger.error("修改失败", e);
		}
		
		return this.JSON_MESSAGE;
	}
	/**
	 * 批量删除团购地区 
	 * @param area_id 团购地区Id数组
	 * @return
	 */
	public String batchDelete(){
		try {
			this.groupBuyAreaManager.delete(area_id);
			this.showSuccessJson("删除改成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败"+e.getMessage());
			this.logger.error("删除失败", e);
		}
		return this.JSON_MESSAGE;
	}


	public IGroupBuyAreaManager getGroupBuyAreaManager() {
		return groupBuyAreaManager;
	}


	public void setGroupBuyAreaManager(IGroupBuyAreaManager groupBuyAreaManager) {
		this.groupBuyAreaManager = groupBuyAreaManager;
	}


	public String getArea_name() {
		return area_name;
	}


	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}


	public String getArea_path() {
		return area_path;
	}


	public void setArea_path(String area_path) {
		this.area_path = area_path;
	}


	public int getArea_order() {
		return area_order;
	}


	public void setArea_order(int area_order) {
		this.area_order = area_order;
	}


	public Integer[] getArea_id() {
		return area_id;
	}


	public void setArea_id(Integer[] area_id) {
		this.area_id = area_id;
	}


	public GroupBuyArea getGroupBuyArea() {
		return groupBuyArea;
	}


	public void setGroupBuyArea(GroupBuyArea groupBuyArea) {
		this.groupBuyArea = groupBuyArea;
	}


	public List getCatList() {
		return catList;
	}


	public void setCatList(List catList) {
		this.catList = catList;
	}
 
 
	

}
