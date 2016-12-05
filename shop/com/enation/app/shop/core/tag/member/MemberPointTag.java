package com.enation.app.shop.core.tag.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IPointHistoryManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 会员我的积分
 * @author whj
 *2014-02-17下午15:13:00
 */
@Component
@Scope("prototype")
public class MemberPointTag extends BaseFreeMarkerTag{

	private IPointHistoryManager pointHistoryManager;
	 
	private IMemberLvManager memberLvManager;
	private IMemberManager memberManager;
	private IMemberPointManger memberPointManger;
	private String  action;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		Map data = new HashMap();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();		 
		String action = request.getParameter("action");
		
		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[MemberPointTag]");
		}
		
		member = this.memberManager.get(member.getMember_id());//获取当前最新的member对象
		if(action == null || action.equals("")){
			
			//冻结积分
			data.put("freezepoint",memberPointManger.getFreezePointByMemberId(member.getMember_id()));
			data.put("freezemp",memberPointManger.getFreezeMpByMemberId(member.getMember_id()));
			data.put("member",member);
			
			//当前等级
			MemberLv memberLv = memberLvManager.get(member.getLv_id());
			data.put("memberLv",memberLv);
			
			//下一等级
			MemberLv nextLv = memberLvManager.getNextLv(member.getPoint());
			if(nextLv!=null){
				data.put("nextLv",nextLv);
			}else{
				data.put("nextLv",memberLv);
			}
			
		}else if(action.equals("list")){
			
		 
			String page = request.getParameter("page");
			page = (page == null || page.equals("")) ? "1" : page;
			int pageSize = 20;
			Page pointHistoryPage = pointHistoryManager.pagePointHistory(Integer
					.valueOf(page), pageSize);
			List pointHistoryList = (List) pointHistoryPage.getResult();
			pointHistoryList = pointHistoryList == null ? new ArrayList()
					: pointHistoryList;
	
			data.put("totalCount", pointHistoryPage.getTotalCount());
			data.put("pageSize", pageSize);
			data.put("pageNo", page);
			data.put("pointHistoryList", pointHistoryList);
			
			
			
		}else if(action.equals("freeze")){
			//冻结明细
		
			String page = request.getParameter("page");
			page = (page == null || page.equals("")) ? "1" : page;
			int pageSize = 20;
			Page pointHistoryPage = pointHistoryManager.pagePointFreeze(Integer
					.valueOf(page), pageSize);
			List pointFreezeList = (List) pointHistoryPage.getResult();
			pointFreezeList = pointFreezeList == null ? new ArrayList()
					: pointFreezeList;
	
			data.put("totalCount", pointHistoryPage.getTotalCount());
			data.put("pageSize", pageSize);
			data.put("pageNo", page);
			data.put("pointFreezeList", pointFreezeList);
			data.put("point", member.getPoint());
			
		}
		return data;
	}

	

	public IPointHistoryManager getPointHistoryManager() {
		return pointHistoryManager;
	}

	public void setPointHistoryManager(IPointHistoryManager pointHistoryManager) {
		this.pointHistoryManager = pointHistoryManager;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}

	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}


}
