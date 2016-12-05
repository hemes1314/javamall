package com.enation.app.shop.core.action.backend;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.framework.action.WWAction;

/**
 * 支付配置action
 * @author kingapex
 *2010-4-13下午05:58:35
 *@author LiFenLong 2014-4-2;2.0改版
 */
@SuppressWarnings({ "rawtypes", "serial" })
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("payCfg")
@Results({
	@Result(name="add", type="freemarker", location="/shop/admin/payment/payment_add.html"),
	@Result(name="edit", type="freemarker", location="/shop/admin/payment/payment_edit.html"),
	@Result(name="list", type="freemarker", location="/shop/admin/payment/payment_list.html") 
})
public class PayCfgAction extends WWAction {
	private IPaymentManager paymentManager ;
	private List list;
	private List pluginList;
	private Integer paymentId;
	private String pluginId;
	private Integer[] id; //删除用
	private String name; 
	private String type;
	private String biref;
	
	/**
	 * 跳转至付款方式列表
	 * @return 付款方式列表
	 */
	public String list(){
		return "list";
	}
	/**
	 * @author LiFenLong
	 * @param list 付款方式列表 
	 * @return 付款方式列表Json
	 */
	public String listJson(){
		this.showGridJson(list = this.paymentManager.listAll());
		return JSON_MESSAGE;
	}
	
	
	/**
	 * 到添加页 
	 * @param pluginList 支付方式插件列表
	 * @return 支付添加页
	 */
	public String add(){	
		this.pluginList = this.paymentManager.listAvailablePlugins();
		return "add";
	}
	/**
	 * 获取支付插件的Html
	 * @param pluginId 插件Id
	 * @param paymentId 支付方式Id
	 * @return json
	 */
	public String getPluginHtml(){
		try{
			this.json = this.paymentManager.getPluginInstallHtml(pluginId, paymentId);
		}catch(RuntimeException e){
			this.json = e.getMessage();
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 到修改页面
	 * @param pluginList 支付方式插件列表,List
	 * @param paymentId 支付方式Id,Integer
	 * @param name 支付方式名称,String
	 * @param type 支付方式类型,Integer
	 * @param biref 支付方式介绍,String
	 * @return 修改页面
	 */
	public String edit(){
		this.pluginList = this.paymentManager.listAvailablePlugins();
		PayCfg cfg  = this.paymentManager.get(paymentId);
		this.name= cfg.getName();
		this.type= cfg.getType();
		this.biref= cfg.getBiref();
		return "edit";
	}
	
	
	/**
	 * 保存添加
	 * @param name 支付方式名称,String
	 * @param type 支付方式类型,Integer
	 * @param biref 支付方式介绍,String
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String saveAdd(){
		try{
			HttpServletRequest  request = this.getRequest();
			Enumeration<String> names = request.getParameterNames();
			Map<String,String> params = new HashMap<String, String>();
			while(names.hasMoreElements()){
				String name= names.nextElement();
				
				if("name".equals(name)) continue;
				if("type".equals(name)) continue;
				if("biref".equals(name)) continue;
				if("paymentId".equals(name)) continue;
				if("submit".equals(name)) continue;
				String value  = request.getParameter(name);
				params.put(name, value);
			}
			
			long id = this.paymentManager.add(name, type, biref, params);
			this.showSuccessJson("支付方式添加成功",id);
		}catch(RuntimeException e){
			this.showErrorJson("支付方式添加失败");
			logger.error("支付方式添加失败", e);
		} 		
		return JSON_MESSAGE;
	}
	
	/**
	 * 保存
	 * @param paymentId 支付方式Id,Integer
	 * @param saveAdd(),添加
	 * @param saveEdit(),修改
	 * @return
	 */
	public String save(){
		
		if(paymentId==null || "".equals(paymentId)){
			return this.saveAdd();
		}else{
			return this.saveEdit();
		}
		
	}
	
	
	/**
	 * 保存修改 
	 * @param paymentId 支付方式Id,Integer
	 * @param name 支付方式名称,String
	 * @param type 支付方式类型,Integer
	 * @param biref 支付方式介绍,String
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String saveEdit(){
		try{
			HttpServletRequest  request = this.getRequest();
			Enumeration<String> names = request.getParameterNames();
			Map<String,String> params = new HashMap<String, String>();
			while(names.hasMoreElements()){
				String name= names.nextElement();
				
				if("name".equals(name)) continue;
				if("type".equals(name)) continue;
				if("biref".equals(name)) continue;
				if("paymentId".equals(name)) continue;
				if("submit".equals(name)) continue;
				String value  = request.getParameter(name);
				params.put(name, value);
			}
			
			this.paymentManager.edit(paymentId,name,type, biref, params);
			this.showSuccessJson("支付方式修改成功");
		}catch(RuntimeException e){
			this.showErrorJson("支付方式修改失败");
			logger.error("支付方式修改失败", e);
		} 		
		return JSON_MESSAGE;
	}
	
	
	/**
	 * 删除
	 * @param id 支付方式Id数组,Integer[]
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String delete(){
		try{
			this.paymentManager.delete(id);
			this.showSuccessJson("支付方式删除成功");
		}catch(RuntimeException e){
			this.showErrorJson("支付方式删除失败");
			logger.error("支付方式删除失败", e);
		}
		return JSON_MESSAGE;
	}
 
	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}

	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	public Integer[] getId() {
		return id;
	}

	public void setId(Integer[] id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBiref() {
		return biref;
	}

	public void setBiref(String biref) {
		this.biref = biref;
	}


	public List getPluginList() {
		return pluginList;
	}


	public void setPluginList(List pluginList) {
		this.pluginList = pluginList;
	}


	public String getPluginId() {
		return pluginId;
	}


	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
	
	
	
}
