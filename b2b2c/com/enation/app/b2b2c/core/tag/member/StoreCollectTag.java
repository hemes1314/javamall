package com.enation.app.b2b2c.core.tag.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreCollectManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 收藏店铺标签
 * @author xulipeng
 *	2014年12月9日17:38:55
 */

@Component
@SuppressWarnings({ "rawtypes", "unchecked" })
public class StoreCollectTag extends BaseFreeMarkerTag {

	private IStoreCollectManager storeCollectManager;
	private IStoreMemberManager storeMemberManager;

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		StoreMember storeMember =storeMemberManager.getStoreMember() ;
		Page webpage = this.storeCollectManager.getList(storeMember.getMember_id(),this.getPage(),this.getPageSize());
		
		Map result = new HashMap();
		//获取总记录数
		Long totalCount = webpage.getTotalCount();
		result.put("page", this.getPage());
		result.put("pageSize", this.getPageSize());
		result.put("totalCount", totalCount);
		result.put("storelist", webpage);
		return result;
	}
	
	public IStoreCollectManager getStoreCollectManager() {
		return storeCollectManager;
	}
	public void setStoreCollectManager(IStoreCollectManager storeCollectManager) {
		this.storeCollectManager = storeCollectManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
