package com.enation.app.shop.core.action.backend;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Logi;
import com.enation.app.shop.core.service.ILogiManager;
import com.enation.framework.action.WWAction;
/**
 * 物流公司Action
 * @author LiFenLong 2014-4-2;4.0改版
 *
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("logi")
@Results({
	@Result(name="add_logi", type="freemarker", location="/shop/admin/setting/logi_add.html"),
	@Result(name="edit_logi", type="freemarker", location="/shop/admin/setting/logi_edit.html"),
	@Result(name="list_logi", type="freemarker", location="/shop/admin/setting/logi_list.html") 
})
public class LogiAction extends WWAction {
	
	private ILogiManager logiManager;
	private String name;
	private Integer cid;
	private Integer[] id;
	private String order;
	private Logi logi;
	private String code;
	/**
	 * 跳转至物流公司添加页面
	 * @return 物流公司添加页面
	 */
	public String add_logi(){
		return "add_logi";
	}
	/**
	 * 跳转至物流公司修改页面
	 * @return 流公司修改页面
	 */
	public String edit_logi(){
		this.logi = this.logiManager.getLogiById(cid);
		return "edit_logi";
	}
	/**
	 * 跳转至物流公司列表
	 * @return 物流公司列表
	 */
	public String list_logi(){
		return "list_logi";
	}
	/**
	 * 获取物流公司列表Json
	 * @author LiFenLong
	 * @param order 排序,String
	 * @return 物流公司列表Json
	 */
	public String list_logiJson(){
		this.webpage = this.logiManager.pageLogi(order, this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return  this.JSON_MESSAGE;
	}
	/**
	 * 删除物流公司
	 * @param id,物流公司Id
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String delete(){
		try {
			this.logiManager.delete(id);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("快递公司删除失败");
			logger.error("物流公司删除失败", e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 添加物流公司
	 * @param code 物流公司代码,String
	 * @param name 物流公司名称,String
	 * @param logi 物流公司,Logi
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String saveAdd(){
		try {
			Logi logi = new Logi();
			logi.setCode(code);
			logi.setName(name);
			logiManager.saveAdd(logi);
		this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("物流公司添加失败");
			logger.error("物流公司添加失败", e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 修改物流公司
	 * @param cid 物流公司Id,Integer
	 * @param code 物流公司代码,String
	 * @param name 物流公司名称,String
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String saveEdit(){
		
		try {
			Logi logi = new Logi();
			logi.setId(cid);
			logi.setCode(code);
			logi.setName(name);
			this.logiManager.saveEdit(logi);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("物流公司修改失败");
			logger.error("物流公司修改失败", e);
		}
		return this.JSON_MESSAGE;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	 
	public ILogiManager getLogiManager() {
		return logiManager;
	}

	public void setLogiManager(ILogiManager logiManager) {
		this.logiManager = logiManager;
	}

	public Integer getCid() {
		return cid;
	}
	public void setCid(Integer cid) {
		this.cid = cid;
	}

	public Integer[] getId() {
		return id;
	}

	public void setId(Integer[] id) {
		this.id = id;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Logi getLogi() {
		return logi;
	}

	public void setLogi(Logi logi) {
		this.logi = logi;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

 
	
	
}
