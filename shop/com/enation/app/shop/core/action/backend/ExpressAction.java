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

import com.enation.app.base.core.model.ExpressPlatform;
import com.enation.app.shop.core.service.IExpressManager;
import com.enation.framework.action.WWAction;

/**
 * 查询快递平台action
 * @author xulipeng
 * 2015年07月21日12:06:26
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("express")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/express/express_list.html"),
	@Result(name="edit", type="freemarker", location="/shop/admin/express/express_edit.html"),
})
public class ExpressAction extends WWAction {
	
	private IExpressManager expressManager;
	private ExpressPlatform platform;
	private String code;
	private Integer id;
	private String platformHtml;
	
	/**
	 * 快递平台列表页
	 * @return
	 */
	public String list(){
		return "list";
	}
	
	/**
	 * 快递平台列表json
	 * @return
	 */
	public String listJson(){
		List list = this.expressManager.getList();
		this.showGridJson(list);
		return JSON_MESSAGE;
	}
	
	/**
	 * 修改参数页
	 * @return
	 */
	public String edit(){
		try {
			platform =  this.expressManager.getPlateform(id);
			platformHtml = this.expressManager.getPlateformHtml(code, id);
		} catch (Exception e) {
			System.out.println(e);
		}
		return "edit";
	}
	
	/**
	 * 保存修改
	 * @return
	 */
	public String saveEdit(){
		try{
			HttpServletRequest  request = this.getRequest();
			Enumeration<String> names = request.getParameterNames();
			Map<String,String> params = new HashMap<String, String>();
			while(names.hasMoreElements()){
				String name= names.nextElement();
				if("id".equals(name)) continue;
				String value = request.getParameter(name);
				params.put(name, value);
			}
			this.expressManager.setParam(id, params);
			
			this.showSuccessJson("设置成功");
		}catch(Exception e){
			this.logger.error("设置快递网关参数出错", e);
			this.showSuccessJson("设置失败");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 设置启用的快递平台
	 * @return
	 */
	public String setOpen(){
		try {
			this.expressManager.open(id);
			this.showSuccessJson("设置成功");
		} catch (Exception e) {
			this.showSuccessJson("出现错误，请稍后重试！");
		}
		return JSON_MESSAGE;
	}
	
	
	
	//set get 
	public IExpressManager getExpressManager() {
		return expressManager;
	}

	public void setExpressManager(IExpressManager expressManager) {
		this.expressManager = expressManager;
	}

	public ExpressPlatform getPlatform() {
		return platform;
	}

	public void setPlatform(ExpressPlatform platform) {
		this.platform = platform;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPlatformHtml() {
		return platformHtml;
	}

	public void setPlatformHtml(String platformHtml) {
		this.platformHtml = platformHtml;
	}
	
	
}
