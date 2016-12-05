/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：商品api  
 * 修改人：  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.advbuy;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.advbuy.core.model.AdvBuyActive;
import com.enation.app.advbuy.core.service.IAdvBuyActiveManager;
import com.enation.app.advbuy.core.service.IAdvBuyManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 预售api 获取预售列表
 * 
 * @author Sylow
 * @version v1.0 , 2015-08-24
 * @since v1.0
 */
@SuppressWarnings("serial")
@Component("mobileAdvBuyApiAction")
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("advbuy")
public class AdvBuyApiAction extends WWAction {

    @Autowired
    private IAdvBuyActiveManager advBuyActiveManager;
    @Autowired
	private IAdvBuyManager advBuyManager;

	
    public IAdvBuyActiveManager getAdvBuyActiveManager() {
        return advBuyActiveManager;
    }

    
    public void setAdvBuyActiveManager(IAdvBuyActiveManager advBuyActiveManager) {
        this.advBuyActiveManager = advBuyActiveManager;
    }

    
    public IAdvBuyManager getAdvBuyManager() {
        return advBuyManager;
    }

    
    public void setAdvBuyManager(IAdvBuyManager advBuyManager) {
        this.advBuyManager = advBuyManager;
    }

    /**
	 * 获取商品分类
	 * 
	 * @return
	 */
	public String page() {
		try {
		    AdvBuyActive advBuyAct = advBuyActiveManager.get();
	        if (advBuyAct == null) throw new RuntimeException("当前没有预售活动，敬请期待。");
	        
	        HttpServletRequest request = getRequest();
	        Integer pageNo = Integer.getInteger(request.getParameter("pageNo"));
	        Integer pageSize = Integer.getInteger(request.getParameter("pageSize"));
	        if (pageNo == null) pageNo = 1;
            if (pageSize == null) pageSize = 30;
			Page page = advBuyManager.listByActId(pageNo, pageSize, advBuyAct.getAct_id(), 1);
            List list = (List)page.getResult();
		    for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = (Map<String, Object>) list.get(i);
                if ( map != null) {
                    map.put("img_url", UploadUtil.replacePath(map.get("img_url").toString()));
                }
            }
			this.json = JsonMessageUtil.getMobileObjectJson(page);

		} catch (RuntimeException e) {
			this.logger.error("获取预售商品出错", e);
			this.showPlainErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	
	
}
