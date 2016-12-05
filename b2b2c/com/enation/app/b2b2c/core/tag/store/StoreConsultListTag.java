package com.enation.app.b2b2c.core.tag.store;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberCommentManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
/**
 * 店铺商品咨询列表标签
 * @author LiFenLong
 *
 */
public class StoreConsultListTag extends BaseFreeMarkerTag{
	private IStoreMemberCommentManager storeMemberCommentManager;
	private IStoreMemberManager storeMemberManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		StoreMember member=storeMemberManager.getStoreMember();
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		int pageSize=10;
		Map map=new HashMap();
		String stype=request.getParameter("stype");
		String grade=request.getParameter("grade");
		if(request.getParameter("stype")==null){
			stype="0";
		}
		if(request.getParameter("grade")==null){
			grade="-1";
		}
		map.put("type", params.get("type").toString());
		//map.put("type", request.getParameter("type"));
		map.put("status",request.getParameter("status"));
		map.put("grade",grade);
		map.put("replyStatus", request.getParameter("replyStatus"));
		map.put("keyword", request.getParameter("keyword"));
		map.put("mname", request.getParameter("mname"));
		map.put("gname", request.getParameter("gname"));
		map.put("content", request.getParameter("content"));
		map.put("stype", stype);
		Page cmmentList = storeMemberCommentManager.getAllComments(NumberUtils.toInt(page), pageSize, map, member.getStore_id());
		//获取总记录数
		Long totalCount = cmmentList.getTotalCount();
		
		map.put("page", page);
		map.put("pageSize", pageSize);
		map.put("totalCount", totalCount);
		map.put("cmmentList", cmmentList);
		
		return map;
	}
	public IStoreMemberCommentManager getStoreMemberCommentManager() {
		return storeMemberCommentManager;
	}
	public void setStoreMemberCommentManager(
			IStoreMemberCommentManager storeMemberCommentManager) {
		this.storeMemberCommentManager = storeMemberCommentManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
