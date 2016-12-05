package com.enation.app.shop.mobile.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Auction;
import com.enation.app.shop.core.model.AuctionRecord;
import com.enation.app.shop.core.model.Maimo;
import com.enation.app.shop.core.model.UtastingNote;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.Page;


@Component
public class ApiUtastingnoteManager extends BaseSupport<UtastingNote> { 
    UtastingNote utastingNote;
    public List<UtastingNote> list(String memberId) {
        String sql = "select * from es_utasting_note where member_id = "+ memberId +" order by id";
        List<UtastingNote> utastinglist = this.baseDaoSupport.queryForList(sql, UtastingNote.class);
        for(UtastingNote tast:utastinglist)
        {
            tast.setFnimagea(UploadUtil.replacePath(tast.getFnimagea()));
            tast.setFnimageb(UploadUtil.replacePath(tast.getFnimageb()));
            tast.setFnimagec(UploadUtil.replacePath(tast.getFnimagec()));
            tast.setFnimaged(UploadUtil.replacePath(tast.getFnimaged()));
            tast.setFnimagee(UploadUtil.replacePath(tast.getFnimagee()));
            tast.setFnimagef(UploadUtil.replacePath(tast.getFnimagef()));
            tast.setFnimageg(UploadUtil.replacePath(tast.getFnimageg()));
            tast.setFnimageh(UploadUtil.replacePath(tast.getFnimageh()));
            tast.setFnimagei(UploadUtil.replacePath(tast.getFnimagei()));
        }
        return utastinglist;
    }
    
    public Page listPage(int pageNo, int pageSize,String memberId) {
        String sql = "select * from es_utasting_note where member_id = "+ memberId +" order by RELEASE_TIME DESC ";
        Page page =  this.baseDaoSupport.queryForPage(sql, pageNo,pageSize,UtastingNote.class);      
        return page;
      }
    
    public boolean joinUtastingnote(UtastingNote utastingNote)
    {  
        this.baseDaoSupport.insert("es_utasting_note", utastingNote);
        return true;
    }
}
