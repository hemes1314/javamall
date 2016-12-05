package com.enation.app.shop.core.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.AdvanceLogs;
import com.enation.app.shop.core.service.IAdvanceLogsManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;

/**
 * 预存款日志
 * 
 * @author lzf<br/>
 *         2010-3-25 下午01:36:37<br/>
 *         version 1.0<br/>
 */
@SuppressWarnings("rawtypes")
public class AdvanceLogsManager extends BaseSupport implements IAdvanceLogsManager {

	private IMemberManager memberManager;
	public Page pageAdvanceLogs(int pageNo, int pageSize) {
		Member member = UserConext.getCurrentMember();
		Page page = this.baseDaoSupport.queryForPage("select * from advance_logs where member_id=? order by log_id DESC", pageNo, pageSize, member.getMember_id());
		return page;
	}
    public Page pageAdvanceLogsForApp(int pageNo, int pageSize, String start_time, String end_time) {
        Member member = UserConext.getCurrentMember();
        

        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long startTime =0;
        long endTime = 0;
        try{
            startTime = format1.parse(start_time).getTime()/1000;
            endTime = format1.parse(end_time).getTime()/1000;
    
        } catch(java.text.ParseException e) {
            e.printStackTrace();
        }
        String  sql = "select * from advance_logs where member_id=? ";
                sql += " and mtime<"+endTime+ " and mtime > "+ startTime+ " order by mtime DESC ";
        Page page = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize, member.getMember_id());
        return page;
    }
	public void add(AdvanceLogs advanceLogs) {
		this.baseDaoSupport.insert("advance_logs", advanceLogs);
	}

	public List listAdvanceLogsByMemberId(long member_id) {
		return this.baseDaoSupport.queryForList("select * from advance_logs where member_id=? order by log_id desc",	AdvanceLogs.class, member_id);
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
    @Override
    public boolean exists(long memberId, String business) {
        String sql = "select count(0) from advance_logs where member_id=? and memo=?";
        long count = this.baseDaoSupport.queryForLong(sql, memberId, business);
        return count > 0;
    }
	
	
}
