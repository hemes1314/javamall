/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：商品api  
 * 修改人：  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.flashbuy;

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

import com.enation.app.flashbuy.core.model.FlashBuyActive;
import com.enation.app.flashbuy.core.service.IFlashBuyActiveManager;
import com.enation.app.flashbuy.core.service.IFlashBuyManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;


/**
 * 限时抢购api 获取限时抢购列表
 * 
 * @author Sylow
 * @version v1.0 , 2015-08-24
 * @since v1.0
 */
@SuppressWarnings("serial")
@Component("mobileFlashBuyApiAction")
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("flashbuy")
public class FlashBuyApiAction extends WWAction {

    @Autowired
    private IFlashBuyActiveManager flashBuyActiveManager;
    @Autowired
	private IFlashBuyManager flashBuyManager;

	
    public IFlashBuyActiveManager getFlashBuyActiveManager() {
        return flashBuyActiveManager;
    }

    
    public void setFlashBuyActiveManager(IFlashBuyActiveManager flashBuyActiveManager) {
        this.flashBuyActiveManager = flashBuyActiveManager;
    }

    
    public IFlashBuyManager getFlashBuyManager() {
        return flashBuyManager;
    }

    
    public void setFlashBuyManager(IFlashBuyManager flashBuyManager) {
        this.flashBuyManager = flashBuyManager;
    }

    /**
	 * 获取商品分类
	 * 
	 * @return
	 */
	public String page() {
		try {
		    FlashBuyActive flashBuyAct = flashBuyActiveManager.get();
	        if (flashBuyAct == null) throw new RuntimeException("当前没有限时抢购活动，敬请期待。");
	        
	        HttpServletRequest request = getRequest();
	        Integer pageNo = Integer.getInteger(request.getParameter("pageNo"));
	        Integer pageSize = Integer.getInteger(request.getParameter("pageSize"));
	        if (pageNo == null) pageNo = 1;
            if (pageSize == null) pageSize = 30;
			Page page = flashBuyManager.listByActId(pageNo, pageSize, flashBuyAct.getAct_id(), 1);
			List list = (List)page.getResult();
			long nowTime = DateUtil.getDateline();
	            for (int i = 0; i < list.size(); i++) {
	                Map<String, Object> map = (Map<String, Object>) list.get(i);
	                if ( map != null) {
	                    map.put("img_url", UploadUtil.replacePath(map.get("img_url").toString()));
	                    map.put("nowTime", nowTime);
	                }
	            }
	         
			this.json = JsonMessageUtil.getMobileObjectJson(page);

		} catch (RuntimeException e) {
			this.logger.error("获取限时抢购商品出错", e);
			this.showPlainErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 获取限时抢购结束时间
	 */
	public String secBuyEndTime(){
	    
	    try{
	        FlashBuyActive flashBuyAct = flashBuyActiveManager.get();
	        if(flashBuyAct==null){
	            this.json = JsonMessageUtil.getMobileNumberJson("date", 0);
	            return WWAction.JSON_MESSAGE; 
	        }
	        Long endTime =flashBuyAct.getEnd_time();
	        HashMap <String,Object> map = new HashMap<String ,Object>();
	        map.put("nowTime", DateUtil.getDateline());
	        map.put("endTime", endTime);
 	        this.json = JsonMessageUtil.getMobileObjectJson(map);
 	        
	    }catch(RuntimeException e){
	        
	        this.showPlainErrorJson("数据库运行异常"+e);
	    }
	    
	    return WWAction.JSON_MESSAGE; 
	}
}
