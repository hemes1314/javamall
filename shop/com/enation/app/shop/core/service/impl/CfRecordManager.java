package com.enation.app.shop.core.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Auction;
import com.enation.app.shop.core.model.Cf;
import com.enation.app.shop.core.model.CfRecord;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

public class CfRecordManager extends BaseSupport {
    	
    public List<Member> getRecordlist(int cfid) {
        String sql = "select * from es_cf_record where cf_id = ? order by id desc";
        List<CfRecord> cfRecordlist = this.baseDaoSupport.queryForList(sql,CfRecord.class,cfid);
        List<Member> mlist = new ArrayList();
        for(CfRecord re:cfRecordlist)
        {
            String memid = re.getMember_id(); 
            String sqlmem = "select * from es_member where member_id = ?";
            Member m = (Member) this.baseDaoSupport.queryForObject(sqlmem, Member.class, memid);
            mlist.add(m);
        }
        return mlist;
    }
    
	
}
