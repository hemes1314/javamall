package com.enation.app.shop.core.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Auction;
import com.enation.app.shop.core.model.AuctionRecord;
import com.enation.app.shop.core.model.Cf;
import com.enation.app.shop.core.model.CfRecord;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

public class AuctionRecordManager extends BaseSupport {
    	
    public List<AuctionRecord> getRecordlist(int auid) {
        String sql = "select a.*,m.address,m.uname,m.name,m.tel,m.mobile from es_auction_record a left join es_member  m on a.userid = m.member_id  where auction_id = ? order by a.id desc";
        List<AuctionRecord> auRecordlist = this.baseDaoSupport.queryForList(sql,AuctionRecord.class,auid);
  /*      List<Member> mlist = new ArrayList();
        for(AuctionRecord re:auRecordlist)
        {
            String memid = re.getUserid();
            String sqlmem = "select * from es_member where member_id = ?";
            Member m = (Member) this.baseDaoSupport.queryForObject(sqlmem, Member.class, memid);
            mlist.add(m);
        }*/
        return auRecordlist;
    }
    
	
}
