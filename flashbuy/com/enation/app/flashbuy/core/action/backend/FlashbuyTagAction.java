package com.enation.app.flashbuy.core.action.backend;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.flashbuy.core.service.IFlashGoodsTagManager;
import com.enation.framework.action.WWAction;

/**
 * @ClassName: FlashbuyTagAction 
 * @Description: 限时抢购标签Action 
 * @author kingapex 
 * @date 2015-7-31 上午1:37:43 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/flashbuy/tags/taglist.html"),
})

@Action("flashBuyTag")
public class FlashbuyTagAction extends WWAction{
	private IFlashGoodsTagManager flashGoodsTagManager;
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
		this.webpage=flashGoodsTagManager.list(this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}
	public IFlashGoodsTagManager getFlashGoodsTagManager() {
		return flashGoodsTagManager;
	}
	public void setFlashGoodsTagManager(IFlashGoodsTagManager flashGoodsTagManager) {
		this.flashGoodsTagManager = flashGoodsTagManager;
	}
}
