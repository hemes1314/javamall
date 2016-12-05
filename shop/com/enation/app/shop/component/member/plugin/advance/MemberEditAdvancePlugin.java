package com.enation.app.shop.component.member.plugin.advance;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.AdvanceLogs;
import com.enation.app.shop.core.plugin.member.IMemberTabShowEvent;
import com.enation.app.shop.core.service.IAdvanceLogsManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.plugin.IAjaxExecuteEnable;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 会员预存款插件
 * 
 * @author lzf<br/>
 *         2012-4-5下午04:13:12<br/>
 * 
 *         xulipeng b2c产品去掉预存款功能(去掉struts2注解)
 *         2015年04月15日11:09:49
 * 
 */

@Component
public class MemberEditAdvancePlugin extends AutoRegisterPlugin implements IMemberTabShowEvent, IAjaxExecuteEnable {

	private IMemberManager memberManager;
	private IAdvanceLogsManager advanceLogsManager;

	@Override
	public boolean canBeExecute(Member member) {
		return true;
	}

	@Override
	public int getOrder() {
		return 15;
	}

	@Override
	public String getTabName(Member member) {
		return "他的预存款";
	}

	@Override
	public String onShowMemberDetailHtml(Member member) {
		return doHtml(member.getMember_id());
	}
	
	@SuppressWarnings("rawtypes")
	protected String doHtml(long memberid) {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.putData("memberid", memberid);
		if(freeMarkerPaser.getData("member")==null) {
			freeMarkerPaser.putData("member", this.memberManager.get(memberid));
		}
		List listAdvanceLogs = this.advanceLogsManager.listAdvanceLogsByMemberId(memberid);
		freeMarkerPaser.putData("listAdvanceLogs", listAdvanceLogs);
		freeMarkerPaser.setPageName("advance");
		return freeMarkerPaser.proessPageContent();
	}
	
	protected String doUpdate(HttpServletRequest request) {
		double modify_advance = StringUtil.toDouble(request.getParameter("modify_advance"), true);
		if (modify_advance == 0) return JsonMessageUtil.getErrorJson("修改无效");
		
		String modify_memo = request.getParameter("modify_memo");
		int memberid = StringUtil.toInt(request.getParameter("memberid"), true);
		Member member = this.memberManager.get(memberid);
		AdvanceLogs log = new AdvanceLogs();
        
        if (modify_advance > 0) {
            member.setVirtual(member.getVirtual()+modify_advance);
            log.setImport_virtual(modify_advance);
        } else {
            Double adv = member.getAdvance();
            Double vir = member.getVirtual();
            if (adv+vir+modify_advance < 0) {
                return JsonMessageUtil.getErrorJson("账户余额不足");
            }
            if (adv+modify_advance >= 0) {
                member.setAdvance(adv+modify_advance);
                log.setExport_advance(-modify_advance);
            } else {
                member.setAdvance(0.0);
                member.setVirtual(adv+vir+modify_advance);
                log.setExport_advance(adv);
                log.setExport_virtual(-modify_advance-adv);
            }
        }
        
        log.setMember_id(memberid);
        log.setDisabled("false");
        log.setMtime(DateUtil.getDateline());
        log.setMember_advance(member.getAdvance());
        log.setMember_virtual(member.getVirtual());
        log.setShop_advance(member.getAdvance());// 此字段很难理解
        log.setMoney(modify_advance);
        log.setMessage(modify_memo);
        AdminUser user = (AdminUser)ThreadContextHolder.getSessionContext().getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
        log.setMemo(user.getUsername() + (modify_advance > 0?"代充值":"代扣款"));
		try {
			memberManager.edit(member);
			advanceLogsManager.add(log);
			return JsonMessageUtil.getSuccessJson("会员预存款修改成功");
		} catch (Exception e) {
			this.logger.error("会员预存款修改失败", e);
			return JsonMessageUtil.getErrorJson("修改失败");
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String execute() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		if("yes".equals(request.getParameter("showHtml"))) {
			int memberid = StringUtil.toInt(request.getParameter("memberid"), true);
			return doHtml(memberid);
		} else {
			return doUpdate(request);
		}
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IAdvanceLogsManager getAdvanceLogsManager() {
		return advanceLogsManager;
	}

	public void setAdvanceLogsManager(IAdvanceLogsManager advanceLogsManager) {
		this.advanceLogsManager = advanceLogsManager;
	}

}
