package com.enation.app.b2b2cFlashbuy.core.tag.flashbuy;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2cFlashbuy.core.service.impl.IStoreFlashBuyManager;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商家中心用的我的限时抢购列表
 * @author kingapex
 *2015-1-8下午6:06:06
 */
@Component
@Scope("prototype")
public class MyFlashBuyListTag extends BaseFreeMarkerTag {
	private IStoreFlashBuyManager storeFlashBuyManager;
	private IStoreMemberManager storeMemberManager;
	@Override
	protected Object exec(Map arg0) throws TemplateModelException {
		StoreMember storeMember  = storeMemberManager.getStoreMember();
		if(storeMember==null ) {
			 return null;
		}
		int page = this.getPage();
		int pageSize = this.getPageSize();
		
		HttpServletRequest request =this.getRequest();
		
		Map params = new HashMap();
		params.put("gb_name", request.getParameter("gb_name"));
		params.put("gb_status", request.getParameter("gb_status"));
		Page webpage = storeFlashBuyManager.listByStoreId(page, pageSize, storeMember.getStore_id(), params);
		return webpage;
		
	}
	public IStoreFlashBuyManager getStoreFlashBuyManager() {
		return storeFlashBuyManager;
	}

	public void setStoreFlashBuyManager(IStoreFlashBuyManager storeFlashBuyManager) {
		this.storeFlashBuyManager = storeFlashBuyManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	
	

}
