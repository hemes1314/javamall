package com.enation.app.b2b2c.core.tag.orderReport;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IStoreSellBackManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 退货列表
 * @author fenlongli
 *
 */
@Component
public class StoreSellBackListTag extends BaseFreeMarkerTag{
	private IStoreSellBackManager storeSellBackManager;
	private IStoreMemberManager storeMemberManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		Integer page = request.getParameter("page") == null ? 1 : NumberUtils.toInt(request.getParameter("page").toString());
		int pageSize=10;
		Map map=new HashMap();
		StoreMember storeMember=storeMemberManager.getStoreMember();
		Integer status = request.getParameter("status") != null ? NumberUtils.toInt(request.getParameter("status").toString()) : null;
		Page sellBackList= storeSellBackManager.list(page, pageSize,storeMember.getStore_id(),status,map);
		Long totalCount = sellBackList.getTotalCount();
		
		map.put("page", page);
		map.put("pageSize", pageSize);
		map.put("totalCount", totalCount);
		map.put("sellBackList", sellBackList);
		return map;
	}
	public IStoreSellBackManager getStoreSellBackManager() {
		return storeSellBackManager;
	}
	public void setStoreSellBackManager(IStoreSellBackManager storeSellBackManager) {
		this.storeSellBackManager = storeSellBackManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
