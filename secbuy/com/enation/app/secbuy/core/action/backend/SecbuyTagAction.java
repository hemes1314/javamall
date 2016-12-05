package com.enation.app.secbuy.core.action.backend;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.secbuy.core.service.ISecGoodsTagManager;
import com.enation.framework.action.WWAction;

/**
 * @ClassName: SecbuyTagAction 
 * @Description: 秒拍标签Action 
 * @author kingapex 
 * @date 2015-7-31 上午1:37:43 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/secbuy/tags/taglist.html"),
})

@Action("secBuyTag")
public class SecbuyTagAction extends WWAction{
	private ISecGoodsTagManager secGoodsTagManager;
	/**
	 * 商品标签列表
	 * @return
	 */
	public String list(){
		return "list";
	}
	/**
	 * 商品标签列表json
	 * @return
	 */
	public String listJson(){
		this.webpage=secGoodsTagManager.list(this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}
	public ISecGoodsTagManager getSecGoodsTagManager() {
		return secGoodsTagManager;
	}
	public void setSecGoodsTagManager(ISecGoodsTagManager secGoodsTagManager) {
		this.secGoodsTagManager = secGoodsTagManager;
	}
}
