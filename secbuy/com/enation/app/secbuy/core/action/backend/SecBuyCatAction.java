package com.enation.app.secbuy.core.action.backend;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.secbuy.core.model.SecBuyCat;
import com.enation.app.secbuy.core.service.ISecBuyCatManager;
import com.enation.framework.action.WWAction;

/**
 * 
 * @ClassName: SecBuyCatAction 
 * @Description: 秒拍分类Action 
 * @author TALON 
 * @date 2015-7-31 上午1:33:16 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/secbuy/cat/cat_list.html"),
	 @Result(name="add",type="freemarker", location="/secbuy/cat/cat_add.html"),
	 @Result(name="edit",type="freemarker", location="/secbuy/cat/cat_edit.html")
})

@Action("secBuyCat")
public class SecBuyCatAction extends WWAction {
	
	private int  cat_order   ;
	private String  cat_name    ;      
	private String  cat_path    ;      
	private Integer[] catid;
	private SecBuyCat  secBuyCat;
	private List catList;
	private ISecBuyCatManager secBuyCatManager;
	/**
	 * 跳转至秒拍分类列表	
	 * @return
	 */
	public String list(){
		return "list";
	}
	
	/**
	 * 获取秒拍分类分页列表Json
	 * @param webpage 秒拍分页列表
	 * @return
	 */
	public String list_json(){
		this.webpage=this.secBuyCatManager.list(this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 跳转至秒拍分类添加页
	 * @return
	 */
	public String add(){
		return "add";
	}
	
	/**
	 * 保存添加秒拍分类
	 * @param secBuyCat 秒拍分类
	 * @param cat_name 分类名称
	 * @param cat_order 分类排序
	 * @return
	 */
	public String saveAdd(){
		try {
			SecBuyCat secBuyCat = new SecBuyCat();
			secBuyCat.setCat_name(cat_name);
			secBuyCat.setCat_order(cat_order);
			this.secBuyCatManager.add(secBuyCat);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败"+e.getMessage());
			this.logger.error("添加失败", e);
		}
		
		return this.JSON_MESSAGE;
	}
	/**
	 * 跳转至秒拍分类修改页
	 * @param secBuyCat 秒拍分类
	 * @param catid 秒拍分类数组
	 * @return
	 */
	public String edit(){
		this.secBuyCat = this.secBuyCatManager.get(catid[0]);
		return "edit";
	}
	/**
	 * @Title: saveEdit
	 * @Description:  保存修改秒拍分类
	 * @param catid 秒拍分类数组 
	 * @param cat_name 分类名称
	 * @param cat_order 分类排序
	 * @return json 1为成功.0为失败.
	 */
	public String saveEdit(){

		try {
			SecBuyCat secBuyCat = new SecBuyCat();
			secBuyCat.setCatid(catid[0]);
			secBuyCat.setCat_name(cat_name);
			secBuyCat.setCat_order(cat_order);
			this.secBuyCatManager.update(secBuyCat);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败"+e.getMessage());
			this.logger.error("修改失败", e);
		}
		
		return this.JSON_MESSAGE;
	}
	/**
	 * 批量删除秒拍分类
	 * @param catid 秒拍分类数组
	 * @return
	 */
	public String batchDelete(){
		try {
			this.secBuyCatManager.delete(catid);
			this.showSuccessJson("删除改成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败"+e.getMessage());
			this.logger.error("删除失败", e);
		}
		return this.JSON_MESSAGE;
	}


	public ISecBuyCatManager getSecBuyCatManager() {
		return secBuyCatManager;
	}


	public void setSecBuyCatManager(ISecBuyCatManager secBuyCatManager) {
		this.secBuyCatManager = secBuyCatManager;
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


	public SecBuyCat getSecBuyCat() {
		return secBuyCat;
	}


	public void setSecBuyCat(SecBuyCat secBuyCat) {
		this.secBuyCat = secBuyCat;
	}


	public Integer[] getCatid() {
		return catid;
	}


	public void setCatid(Integer[] catid) {
		this.catid = catid;
	}

 
	

}
