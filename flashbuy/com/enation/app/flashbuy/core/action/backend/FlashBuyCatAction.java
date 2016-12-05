package com.enation.app.flashbuy.core.action.backend;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.flashbuy.core.model.FlashBuyCat;
import com.enation.app.flashbuy.core.service.IFlashBuyCatManager;
import com.enation.framework.action.WWAction;

/**
 * 
 * @ClassName: FlashBuyCatAction 
 * @Description: 限时抢购分类Action 
 * @author TALON 
 * @date 2015-7-31 上午1:33:16 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/flashbuy/cat/cat_list.html"),
	 @Result(name="add",type="freemarker", location="/flashbuy/cat/cat_add.html"),
	 @Result(name="edit",type="freemarker", location="/flashbuy/cat/cat_edit.html")
})

@Action("flashBuyCat")
public class FlashBuyCatAction extends WWAction {
	
	private int  cat_order   ;
	private String  cat_name    ;      
	private String  cat_path    ;      
	private Integer[] catid;
	private FlashBuyCat  flashBuyCat;
	private List catList;
	private IFlashBuyCatManager flashBuyCatManager;
	/**
	 * 跳转至限时抢购分类列表	
	 * @return
	 */
	public String list(){
		return "list";
	}
	
	/**
	 * 获取限时抢购分类分页列表Json
	 * @param webpage 限时抢购分页列表
	 * @return
	 */
	public String list_json(){
		this.webpage=this.flashBuyCatManager.list(this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 跳转至限时抢购分类添加页
	 * @return
	 */
	public String add(){
		return "add";
	}
	
	/**
	 * 保存添加限时抢购分类
	 * @param flashBuyCat 限时抢购分类
	 * @param cat_name 分类名称
	 * @param cat_order 分类排序
	 * @return
	 */
	public String saveAdd(){
		try {
			FlashBuyCat flashBuyCat = new FlashBuyCat();
			flashBuyCat.setCat_name(cat_name);
			flashBuyCat.setCat_order(cat_order);
			this.flashBuyCatManager.add(flashBuyCat);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败"+e.getMessage());
			this.logger.error("添加失败", e);
		}
		
		return this.JSON_MESSAGE;
	}
	/**
	 * 跳转至限时抢购分类修改页
	 * @param flashBuyCat 限时抢购分类
	 * @param catid 限时抢购分类数组
	 * @return
	 */
	public String edit(){
		this.flashBuyCat = this.flashBuyCatManager.get(catid[0]);
		return "edit";
	}
	/**
	 * @Title: saveEdit
	 * @Description:  保存修改限时抢购分类
	 * @param catid 限时抢购分类数组 
	 * @param cat_name 分类名称
	 * @param cat_order 分类排序
	 * @return json 1为成功.0为失败.
	 */
	public String saveEdit(){

		try {
			FlashBuyCat flashBuyCat = new FlashBuyCat();
			flashBuyCat.setCatid(catid[0]);
			flashBuyCat.setCat_name(cat_name);
			flashBuyCat.setCat_order(cat_order);
			this.flashBuyCatManager.update(flashBuyCat);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败"+e.getMessage());
			this.logger.error("修改失败", e);
		}
		
		return this.JSON_MESSAGE;
	}
	/**
	 * 批量删除限时抢购分类
	 * @param catid 限时抢购分类数组
	 * @return
	 */
	public String batchDelete(){
		try {
			this.flashBuyCatManager.delete(catid);
			this.showSuccessJson("删除改成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败"+e.getMessage());
			this.logger.error("删除失败", e);
		}
		return this.JSON_MESSAGE;
	}


	public IFlashBuyCatManager getFlashBuyCatManager() {
		return flashBuyCatManager;
	}


	public void setFlashBuyCatManager(IFlashBuyCatManager flashBuyCatManager) {
		this.flashBuyCatManager = flashBuyCatManager;
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


	public FlashBuyCat getFlashBuyCat() {
		return flashBuyCat;
	}


	public void setFlashBuyCat(FlashBuyCat flashBuyCat) {
		this.flashBuyCat = flashBuyCat;
	}


	public Integer[] getCatid() {
		return catid;
	}


	public void setCatid(Integer[] catid) {
		this.catid = catid;
	}

 
	

}
