package com.enation.app.b2b2cFlashbuy.core.action.backend;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.framework.action.WWAction;
/**
 * 多商户限时抢购管理类
 * @author humaodong
 * @date 2015-9-18 下午2:17:31
 */
@ParentPackage("shop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/b2b2c/admin/flashbuy/act_list.html"),
})

@Action("flashBuy")
@Component
public class B2b2cFlashBuyAction extends WWAction{

	public String list(){
		return "list";
	}
}
