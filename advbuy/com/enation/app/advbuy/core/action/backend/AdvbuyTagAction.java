package com.enation.app.advbuy.core.action.backend;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.advbuy.core.service.IAdvGoodsTagManager;
import com.enation.framework.action.WWAction;

/**
 * @ClassName: AdvbuyTagAction 
 * @Description: 预售标签Action 
 * @author kingapex 
 * @date 2015-7-31 上午1:37:43 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/advbuy/tags/taglist.html"),
})

@Action("advBuyTag")
public class AdvbuyTagAction extends WWAction{
	private IAdvGoodsTagManager advGoodsTagManager;
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
		this.webpage=advGoodsTagManager.list(this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}
	public IAdvGoodsTagManager getAdvGoodsTagManager() {
		return advGoodsTagManager;
	}
	public void setAdvGoodsTagManager(IAdvGoodsTagManager advGoodsTagManager) {
		this.advGoodsTagManager = advGoodsTagManager;
	}
}
