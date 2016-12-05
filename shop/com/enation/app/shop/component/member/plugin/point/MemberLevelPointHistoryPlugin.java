package com.enation.app.shop.component.member.plugin.point;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.plugin.member.IMemberTabShowEvent;
import com.enation.app.shop.core.service.IPointHistoryManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 会员等级积分历史插件
 * @author lzf<br/>
 * 2012-4-1下午10:34:27<br/>
 */
@Component
public class MemberLevelPointHistoryPlugin extends AutoRegisterPlugin implements
		IMemberTabShowEvent {
	
	private IPointHistoryManager pointHistoryManager;

	@Override
	public boolean canBeExecute(Member member) {
		return true;
	}

	@Override
	public int getOrder() {
		return 11;
	}

	@Override
	public String getTabName(Member member) {
		return "等级积分";
	}

	@Override
	public String onShowMemberDetailHtml(Member member) {
		List listPointHistory = pointHistoryManager.listPointHistory(member.getMember_id(),0);
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.putData("listPointHistory",listPointHistory);
		freeMarkerPaser.putData("type", 0);
		freeMarkerPaser.setPageName("point_history");		
		return freeMarkerPaser.proessPageContent();
	}

	
	public IPointHistoryManager getPointHistoryManager() {
		return pointHistoryManager;
	}

	public void setPointHistoryManager(IPointHistoryManager pointHistoryManager) {
		this.pointHistoryManager = pointHistoryManager;
	}

}
