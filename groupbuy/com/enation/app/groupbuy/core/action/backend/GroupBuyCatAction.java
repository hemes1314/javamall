package com.enation.app.groupbuy.core.action.backend;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.groupbuy.core.model.GroupBuyCat;
import com.enation.app.groupbuy.core.service.IGroupBuyCatManager;
import com.enation.framework.action.WWAction;

/**
 * 
 * @ClassName: GroupBuyCatAction 
 * @Description: 团购分类Action 
 * @author TALON 
 * @date 2015-7-31 上午1:33:16 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/groupbuy/cat/cat_list.html"),
	 @Result(name="add",type="freemarker", location="/groupbuy/cat/cat_add.html"),
	 @Result(name="edit",type="freemarker", location="/groupbuy/cat/cat_edit.html")
})

@Action("groupBuyCat")
public class GroupBuyCatAction extends WWAction {
	
	private int  cat_order   ;
	private String  cat_name    ;      
	private String  cat_path    ;      
	private Integer[] catid;
	private GroupBuyCat  groupBuyCat;
	private List catList;
	private IGroupBuyCatManager groupBuyCatManager;
	/**
	 * 跳转至团购分类列表	
	 * @return
	 */
	public String list(){
		return "list";
	}
	
	/**
	 * 获取团购分类分页列表Json
	 * @param webpage 团购分页列表
	 * @return
	 */
	public String list_json(){
		this.webpage=this.groupBuyCatManager.list(this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 跳转至团购分类添加页
	 * @return
	 */
	public String add(){
		return "add";
	}
	
	/**
	 * 保存添加团购分类
	 * @param groupBuyCat 团购分类
	 * @param cat_name 分类名称
	 * @param cat_order 分类排序
	 * @return
	 */
	public String saveAdd(){
		try {
			GroupBuyCat groupBuyCat = new GroupBuyCat();
			groupBuyCat.setCat_name(cat_name);
			groupBuyCat.setCat_order(cat_order);
			this.groupBuyCatManager.add(groupBuyCat);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败"+e.getMessage());
			this.logger.error("添加失败", e);
		}
		
		return this.JSON_MESSAGE;
	}
	/**
	 * 跳转至团购分类修改页
	 * @param groupBuyCat 团购分类
	 * @param catid 团购分类数组
	 * @return
	 */
	public String edit(){
		this.groupBuyCat = this.groupBuyCatManager.get(catid[0]);
		return "edit";
	}
	/**
	 * @Title: saveEdit
	 * @Description:  保存修改团购分类
	 * @param catid 团购分类数组 
	 * @param cat_name 分类名称
	 * @param cat_order 分类排序
	 * @return json 1为成功.0为失败.
	 */
	public String saveEdit(){

		try {
			GroupBuyCat groupBuyCat = new GroupBuyCat();
			groupBuyCat.setCatid(catid[0]);
			groupBuyCat.setCat_name(cat_name);
			groupBuyCat.setCat_order(cat_order);
			this.groupBuyCatManager.update(groupBuyCat);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败"+e.getMessage());
			this.logger.error("修改失败", e);
		}
		
		return this.JSON_MESSAGE;
	}
	/**
	 * 批量删除团购分类
	 * @param catid 团购分类数组
	 * @return
	 */
	public String batchDelete(){
		try {
			this.groupBuyCatManager.delete(catid);
			this.showSuccessJson("删除改成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败"+e.getMessage());
			this.logger.error("删除失败", e);
		}
		return this.JSON_MESSAGE;
	}


	public IGroupBuyCatManager getGroupBuyCatManager() {
		return groupBuyCatManager;
	}


	public void setGroupBuyCatManager(IGroupBuyCatManager groupBuyCatManager) {
		this.groupBuyCatManager = groupBuyCatManager;
	}


	public List getCatList() {
		return catList;
	}


	public void setCatList(List catList) {
		this.catList = catList;
	}


	public String getCat_name() {
		return cat_name;
	}


	public void setCat_name(String cat_name) {
		this.cat_name = cat_name;
	}


	public String getCat_path() {
		return cat_path;
	}


	public void setCat_path(String cat_path) {
		this.cat_path = cat_path;
	}


	public int getCat_order() {
		return cat_order;
	}


	public void setCat_order(int cat_order) {
		this.cat_order = cat_order;
	}


	public GroupBuyCat getGroupBuyCat() {
		return groupBuyCat;
	}


	public void setGroupBuyCat(GroupBuyCat groupBuyCat) {
		this.groupBuyCat = groupBuyCat;
	}


	public Integer[] getCatid() {
		return catid;
	}


	public void setCatid(Integer[] catid) {
		this.catid = catid;
	}

 
	

}
