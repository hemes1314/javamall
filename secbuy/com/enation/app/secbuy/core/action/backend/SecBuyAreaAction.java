package com.enation.app.secbuy.core.action.backend;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.secbuy.core.model.SecBuyArea;
import com.enation.app.secbuy.core.service.ISecBuyAreaManager;
import com.enation.framework.action.WWAction;

/**
 * 
 * @ClassName: SecBuyAreaAction 
 * @Description: 秒拍地区管理action 
 * @author TALON 
 * @date 2015-7-31 上午1:30:18 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/secbuy/area/area_list.html"),
	 @Result(name="add",type="freemarker", location="/secbuy/area/area_add.html"),
	 @Result(name="edit",type="freemarker", location="/secbuy/area/area_edit.html")
})

@Action("secBuyArea")
public class SecBuyAreaAction extends WWAction {
	
	private ISecBuyAreaManager secBuyAreaManager;
	private String  area_name;      
	private String  area_path;      
	private int  area_order;
	private Integer[] area_id;
	
	private SecBuyArea  secBuyArea;
	
	private  List catList;
	/**
	 * 跳转至秒拍地区列表	
	 * @return
	 */
	public String list(){
		return "list";
	}
	
	/**
	 * 秒拍地区分页列表json
	 * @param webpage 秒拍地区分页
	 * @return
	 */
	public String list_json(){
		this.webpage=this.secBuyAreaManager.list(this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}
	/**
	 * 跳转至秒拍地区
	 * @return
	 */
	public String add(){
		return "add";
	}
	
	/**
	 * 
	 * @Title: saveAdd
	 * @Description: 添加秒拍地区
	 * @param secBuyArea 秒拍地区
	 * @param area_name 地区名称
	 * @param area_order 排序
	 * @return json 1为成功.0为失败
	 */
	public String saveAdd(){
		try {
			SecBuyArea secBuyArea = new SecBuyArea();
			secBuyArea.setArea_name(area_name);
			secBuyArea.setArea_order(area_order);
			this.secBuyAreaManager.add(secBuyArea);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败"+e.getMessage());
			this.logger.error("添加失败", e);
		}
		
		return this.JSON_MESSAGE;
	}
	/**
	 * 跳转至修改秒拍地区
	 * @param secBuyArea 秒拍地区
	 * @param area_id 秒拍地区Id数组
	 * @return
	 */
	public String edit(){
		this.secBuyArea = this.secBuyAreaManager.get(area_id[0]);
		return "edit";
	}
	/**
	 * 
	 * @Title: saveEdit
	 * @Description: 保存修改秒拍地区
	 * @param secBuyArea 秒拍地区
	 * @param area_id 秒拍地区Id数组
	 * @param area_name 地区名称
	 * @param area_order 秒拍地区排序
	 * @return json 1为成功.0为失败
	 */
	public String saveEdit(){
		try {
			SecBuyArea secBuyArea = new SecBuyArea();
			secBuyArea.setArea_id(area_id[0]);
			secBuyArea.setArea_name(area_name);
			secBuyArea.setArea_order(area_order);
			this.secBuyAreaManager.update(secBuyArea);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败"+e.getMessage());
			this.logger.error("修改失败", e);
		}
		
		return this.JSON_MESSAGE;
	}
	/**
	 * 批量删除秒拍地区 
	 * @param area_id 秒拍地区Id数组
	 * @return
	 */
	public String batchDelete(){
		try {
			this.secBuyAreaManager.delete(area_id);
			this.showSuccessJson("删除改成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败"+e.getMessage());
			this.logger.error("删除失败", e);
		}
		return this.JSON_MESSAGE;
	}


	public ISecBuyAreaManager getSecBuyAreaManager() {
		return secBuyAreaManager;
	}


	public void setSecBuyAreaManager(ISecBuyAreaManager secBuyAreaManager) {
		this.secBuyAreaManager = secBuyAreaManager;
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


	public SecBuyArea getSecBuyArea() {
		return secBuyArea;
	}


	public void setSecBuyArea(SecBuyArea secBuyArea) {
		this.secBuyArea = secBuyArea;
	}


	public List getCatList() {
		return catList;
	}


	public void setCatList(List catList) {
		this.catList = catList;
	}
 
 
	

}
