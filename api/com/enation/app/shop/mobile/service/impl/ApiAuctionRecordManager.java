package com.enation.app.shop.mobile.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.model.Auction;
import com.enation.app.shop.core.model.AuctionRecord;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

@Component
public class ApiAuctionRecordManager extends BaseSupport<AuctionRecord> {

    public List<AuctionRecord> getAuctionRecord(String actionId) {
        String sql = "select * from es_auction_record where auction_id ="+ actionId +" order by id desc";
        List<AuctionRecord> auctionRecordlist = this.baseDaoSupport.queryForList(sql, AuctionRecord.class);
        return auctionRecordlist;
    }
	
}
