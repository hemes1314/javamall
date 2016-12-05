package com.enation.app.b2b2c.core.tag.store;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreBonusManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 店铺优惠券标签
 * 根据店铺id查集合
 * @author xulipeng
 * 2015年1月13日15:45:55
 */
@Component
public class StoreBonusListTag extends BaseFreeMarkerTag {

	private IStoreBonusManager storeBonusManager;
	private IStoreMemberManager storeMemberManager;
	private String keyword;
	private String add_time_from;
	private String add_time_to;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		Integer store_id = (Integer) params.get("store_id");
		if(store_id==null){
			StoreMember storeMember = storeMemberManager.getStoreMember();
			store_id = storeMember.getStore_id();
		}
		
		//获得优惠券参数
		int pageSize=10;
		String page = request.getParameter("page")==null?"1": request.getParameter("page");
		//String keyword=request.getParameter("keyword");
		String add_time_from=request.getParameter("add_time_from");
		String add_time_to=request.getParameter("add_time_to");
		
		
		Map result=new HashMap();
		//result.put("keyword", keyword);
		result.put("add_time_from", add_time_from);
		result.put("add_time_to", add_time_to);

		Page bonusList = storeBonusManager.getConditionBonusList(NumberUtils.toInt(page), pageSize, store_id, result);
		//获取总记录数
		Long totalCount = bonusList.getTotalCount();
		
		result.put("page", page);
		result.put("pageSize", pageSize);
		result.put("totalCount", totalCount);
		result.put("bonusList", bonusList);
		return result;
		
		
	}
	
	public IStoreBonusManager getStoreBonusManager() {
		return storeBonusManager;
	}
	public void setStoreBonusManager(IStoreBonusManager storeBonusManager) {
		this.storeBonusManager = storeBonusManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getAdd_time_from() {
		return add_time_from;
	}

	public void setAdd_time_from(String add_time_from) {
		this.add_time_from = add_time_from;
	}

	public String getAdd_time_to() {
		return add_time_to;
	}

	public void setAdd_time_to(String add_time_to) {
		this.add_time_to = add_time_to;
	}
	

}
