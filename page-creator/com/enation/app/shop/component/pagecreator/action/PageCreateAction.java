/**
 * 
 */
package com.enation.app.shop.component.pagecreator.action;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.shop.component.pagecreator.service.IPageCreateManager;
import com.enation.framework.action.WWAction;

/**
 * @author kingapex
 *2015-3-27
 */
@ParentPackage("eop_default")
@Namespace("/page/admin")
@Results({
	@Result(name="input", type="freemarker", location="/com/enation/app/shop/component/pagecreator/action/create.html")
})
public class PageCreateAction extends WWAction {
	
	private IPageCreateManager pageCreateManager;
	private String[] choose_pages;// 要生成的页面
	
	 
	/**
	 * 转向生成页面
	 */
	public String execute(){
		
		return this.INPUT;
	}
	
	
	
	
	/**
	 * 生成
	 * @return
	 */
	public String create(){
		
		try {
			
			boolean result = pageCreateManager.startCreate( choose_pages);
			if(result){
				this.showSuccessJson("生成成功");
			}else{
				this.showErrorJson("有静态页生成任务正在进行中，需等待本次任务完成后才能再次生成。");
			}
		} catch (Exception e) {
			this.showErrorJson("生成失败"+e.getMessage());
		}
		
		return this.JSON_MESSAGE;
	}

	


	public IPageCreateManager getPageCreateManager() {
		return pageCreateManager;
	}


	public void setPageCreateManager(IPageCreateManager pageCreateManager) {
		this.pageCreateManager = pageCreateManager;
	}

	public String[] getChoose_pages() {
		return choose_pages;
	}

	public void setChoose_pages(String[] choose_pages) {
		this.choose_pages = choose_pages;
	}
	
	
}
