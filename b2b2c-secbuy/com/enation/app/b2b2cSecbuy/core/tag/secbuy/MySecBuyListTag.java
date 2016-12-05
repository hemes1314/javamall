package com.enation.app.b2b2cSecbuy.core.tag.secbuy;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2cSecbuy.core.service.impl.IStoreSecBuyManager;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商家中心用的我的秒拍列表
 * @author kingapex
 *2015-1-8下午6:06:06
 */
@Component
@Scope("prototype")
public class MySecBuyListTag extends BaseFreeMarkerTag {
	private IStoreSecBuyManager storeSecBuyManager;
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
		Page webpage = storeSecBuyManager.listByStoreId(page, pageSize, storeMember.getStore_id(), params);
		return webpage;
		
	}
	public IStoreSecBuyManager getStoreSecBuyManager() {
		return storeSecBuyManager;
	}

	public void setStoreSecBuyManager(IStoreSecBuyManager storeSecBuyManager) {
		this.storeSecBuyManager = storeSecBuyManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	
	

}
