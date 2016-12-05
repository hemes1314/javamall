package com.enation.app.advbuy.core.action.backend;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.advbuy.core.model.AdvBuyCat;
import com.enation.app.advbuy.core.service.IAdvBuyCatManager;
import com.enation.framework.action.WWAction;

/**
 * 
 * @ClassName: AdvBuyCatAction 
 * @Description: 预售分类Action 
 * @author TALON 
 * @date 2015-7-31 上午1:33:16 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/advbuy/cat/cat_list.html"),
	 @Result(name="add",type="freemarker", location="/advbuy/cat/cat_add.html"),
	 @Result(name="edit",type="freemarker", location="/advbuy/cat/cat_edit.html")
})

@Action("advBuyCat")
public class AdvBuyCatAction extends WWAction {
	
	private int  cat_order   ;
	private String  cat_name    ;      
	private String  cat_path    ;      
	private Integer[] catid;
	private AdvBuyCat  advBuyCat;
	private List catList;
	private IAdvBuyCatManager advBuyCatManager;
	/**
	 * 跳转至预售分类列表	
	 * @return
	 */
	public String list(){
		return "list";
	}
	
	/**
	 * 获取预售分类分页列表Json
	 * @param webpage 预售分页列表
	 * @return
	 */
	public String list_json(){
		this.webpage=this.advBuyCatManager.list(this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 跳转至预售分类添加页
	 * @return
	 */
	public String add(){
		return "add";
	}
	
	/**
	 * 保存添加预售分类
	 * @param advBuyCat 预售分类
	 * @param cat_name 分类名称
	 * @param cat_order 分类排序
	 * @return
	 */
	public String saveAdd(){
		try {
			AdvBuyCat advBuyCat = new AdvBuyCat();
			advBuyCat.setCat_name(cat_name);
			advBuyCat.setCat_order(cat_order);
			this.advBuyCatManager.add(advBuyCat);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败"+e.getMessage());
			this.logger.error("添加失败", e);
		}
		
		return this.JSON_MESSAGE;
	}
	/**
	 * 跳转至预售分类修改页
	 * @param advBuyCat 预售分类
	 * @param catid 预售分类数组
	 * @return
	 */
	public String edit(){
		this.advBuyCat = this.advBuyCatManager.get(catid[0]);
		return "edit";
	}
	/**
	 * @Title: saveEdit
	 * @Description:  保存修改预售分类
	 * @param catid 预售分类数组 
	 * @param cat_name 分类名称
	 * @param cat_order 分类排序
	 * @return json 1为成功.0为失败.
	 */
	public String saveEdit(){

		try {
			AdvBuyCat advBuyCat = new AdvBuyCat();
			advBuyCat.setCatid(catid[0]);
			advBuyCat.setCat_name(cat_name);
			advBuyCat.setCat_order(cat_order);
			this.advBuyCatManager.update(advBuyCat);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败"+e.getMessage());
			this.logger.error("修改失败", e);
		}
		
		return this.JSON_MESSAGE;
	}
	/**
	 * 批量删除预售分类
	 * @param catid 预售分类数组
	 * @return
	 */
	public String batchDelete(){
		try {
			this.advBuyCatManager.delete(catid);
			this.showSuccessJson("删除改成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败"+e.getMessage());
			this.logger.error("删除失败", e);
		}
		return this.JSON_MESSAGE;
	}


	public IAdvBuyCatManager getAdvBuyCatManager() {
		return advBuyCatManager;
	}


	public void setAdvBuyCatManager(IAdvBuyCatManager advBuyCatManager) {
		this.advBuyCatManager = advBuyCatManager;
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


	public AdvBuyCat getAdvBuyCat() {
		return advBuyCat;
	}


	public void setAdvBuyCat(AdvBuyCat advBuyCat) {
		this.advBuyCat = advBuyCat;
	}


	public Integer[] getCatid() {
		return catid;
	}


	public void setCatid(Integer[] catid) {
		this.catid = catid;
	}

 
	

}
