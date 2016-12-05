package com.enation.app.b2b2c.core.action.backend.goods;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.goods.IB2b2cGoodsTagManager;
import com.enation.framework.action.WWAction;
/**
 * 商品标签设置action
 * @author LiFenLong
 *
 */
@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/b2b2c/admin/tags/taglist.html")
})
@Action("b2b2cGoodsShow")
public class StoreGoodsShowAction extends WWAction{
	private IB2b2cGoodsTagManager b2b2cGoodsTagManager;
	public String list(){
		return "list";
	}
	public String listJson(){
		this.webpage=b2b2cGoodsTagManager.list(this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}
	public IB2b2cGoodsTagManager getB2b2cGoodsTagManager() {
		return b2b2cGoodsTagManager;
	}
	public void setB2b2cGoodsTagManager(IB2b2cGoodsTagManager b2b2cGoodsTagManager) {
		this.b2b2cGoodsTagManager = b2b2cGoodsTagManager;
	}
}
