package com.enation.app.shop.core.tag.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IAdvanceLogsManager;
import com.enation.app.shop.core.service.IMemberOrderManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 会员充值记录标签
 * @author whj
 *2014-02-17下午15:13:00
 */
@Component
@Scope("prototype")
public class MemberAdvanceLogTag extends BaseFreeMarkerTag {

    @Autowired
    private IAdvanceLogsManager advanceLogsManager;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();

		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[MemberAdvanceLogTag]");
		}
		Map result = new HashMap();
		String pageNo = request.getParameter("page");
		pageNo = (pageNo == null || pageNo.equals("")) ? "1" : pageNo;
		int pageSize = 10;
		
		Page page = advanceLogsManager.pageAdvanceLogs(Integer.valueOf(pageNo), pageSize);
		Long totalCount = page.getTotalCount();
		
		List logList = (List) page.getResult();
		logList = logList == null ? new ArrayList() : logList;

		result.put("totalCount", totalCount);
		result.put("pageSize", pageSize);
		result.put("pageNo", pageNo);
		result.put("logList", logList);
		
		return result;
	}	

}
