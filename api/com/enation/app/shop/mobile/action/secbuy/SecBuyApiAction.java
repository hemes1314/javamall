/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：商品api  
 * 修改人：  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.secbuy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.secbuy.core.model.SecBuy;
import com.enation.app.secbuy.core.model.SecBuyActive;
import com.enation.app.secbuy.core.service.ISecBuyActiveManager;
import com.enation.app.secbuy.core.service.ISecBuyManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 秒拍api 获取秒拍列表
 * 
 * @author Sylow
 * @version v1.0 , 2015-08-24
 * @since v1.0
 */
@SuppressWarnings("serial")
@Component("mobileSecBuyApiAction")
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("secbuy")
public class SecBuyApiAction extends WWAction {

    @Autowired
    private ISecBuyActiveManager secBuyActiveManager;
    @Autowired
	private ISecBuyManager secBuyManager;

	
    public ISecBuyActiveManager getSecBuyActiveManager() {
        return secBuyActiveManager;
    }

    
    public void setSecBuyActiveManager(ISecBuyActiveManager secBuyActiveManager) {
        this.secBuyActiveManager = secBuyActiveManager;
    }

    
    public ISecBuyManager getSecBuyManager() {
        return secBuyManager;
    }

    
    public void setSecBuyManager(ISecBuyManager secBuyManager) {
        this.secBuyManager = secBuyManager;
    }

    /**
	 * 获取商品分类
	 * 
	 * @return
	 */
	public String page() {
		try {
		    SecBuyActive secBuyAct = secBuyActiveManager.get();
	        if (secBuyAct == null) throw new RuntimeException("当前没有秒拍活动，敬请期待。");
	        
	        HttpServletRequest request = getRequest();
	        Integer pageNo = Integer.getInteger(request.getParameter("pageNo"));
	        Integer pageSize = Integer.getInteger(request.getParameter("pageSize"));
	        if (pageNo == null) pageNo = 1;
	        if (pageSize == null) pageSize = 30;
			Page page = secBuyManager.listByActId(pageNo, pageSize, secBuyAct.getAct_id(),1);
			List list = (List)page.getResult();
	        for (int i = 0; i < list.size(); i++) {
	            long nowTime = DateUtil.getDateline();
	            Map<String, Object> map = (Map<String, Object>) list.get(i);
	            if ( map != null) {
	                map.put("img_url", UploadUtil.replacePath(map.get("img_url").toString()));
	                map.put("nowTime",nowTime);
	            }
	        }
			this.json = JsonMessageUtil.getMobileObjectJson(page);

		} catch (RuntimeException e) {
		    e.printStackTrace();
			this.logger.error("获取秒拍商品出错", e);
			this.showPlainErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	   public String firstPage() {
	        try {
	            SecBuyActive secBuyAct = secBuyActiveManager.get();
	            if (secBuyAct == null){
	                this.showPlainSuccessJson("没有秒拍活动");
	                return WWAction.JSON_MESSAGE;
	                
	            }
	            Page page = secBuyManager.listByActId(1, 4, secBuyAct.getAct_id(),null);
	            this.json = JsonMessageUtil.getMobileObjectJson(page);

	        } catch (RuntimeException e) {
	            e.printStackTrace();
	            this.logger.error("获取秒拍商品出错", e);
	            this.showPlainErrorJson(e.getMessage());
	        }
	        return WWAction.JSON_MESSAGE;
	    }
	
}
