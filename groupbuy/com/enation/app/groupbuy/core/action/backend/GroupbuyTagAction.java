package com.enation.app.groupbuy.core.action.backend;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.groupbuy.core.service.IGroupGoodsTagManager;
import com.enation.framework.action.WWAction;

/**
 * @ClassName: GroupbuyTagAction 
 * @Description: 团购标签Action 
 * @author kingapex 
 * @date 2015-7-31 上午1:37:43 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/groupbuy/tags/taglist.html"),
})

@Action("groupBuyTag")
public class GroupbuyTagAction extends WWAction{
	private IGroupGoodsTagManager groupGoodsTagManager;
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
		this.webpage=groupGoodsTagManager.list(this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}
	public IGroupGoodsTagManager getGroupGoodsTagManager() {
		return groupGoodsTagManager;
	}
	public void setGroupGoodsTagManager(IGroupGoodsTagManager groupGoodsTagManager) {
		this.groupGoodsTagManager = groupGoodsTagManager;
	}
}
