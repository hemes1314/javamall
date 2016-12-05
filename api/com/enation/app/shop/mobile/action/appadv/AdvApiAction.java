/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：商品api  
 * 修改人：  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.appadv;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.IDataAppAdvManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonMessageUtil;

/**
 *  移动端 首页轮播 API
 * 
 * @author Jeffrey
 */
@SuppressWarnings("serial")
@Component("mobileAppAdvApiAction")
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("advs")
public class AdvApiAction extends WWAction {

    @Autowired
    private IDataAppAdvManager advManager;

    @Override
    public String execute() throws Exception {
        this.json = JsonMessageUtil.getMobileListJson(advManager.getAll());
        return WWAction.JSON_MESSAGE;
    }
	
}
