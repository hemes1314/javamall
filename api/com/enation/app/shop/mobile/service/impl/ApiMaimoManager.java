package com.enation.app.shop.mobile.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import com.enation.app.shop.core.model.Maimo;
import com.enation.app.shop.mobile.model.ApiOrderList;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;


@Component
public class ApiMaimoManager extends BaseSupport<Maimo> {
    public List<Maimo> list() {
        String sql = "select es_maimo.*,es_member.face,es_member.uname, es_member.nickname from es_maimo,es_member where es_member.member_id = es_maimo.member_id order by id desc";
        List<Maimo> maimolist = this.baseDaoSupport.queryForList(sql, Maimo.class);
        return maimolist;
    }
    
    public Page listPage(int pageNo, int pageSize) {
      String sql = "select es_maimo.*,es_member.face,es_member.uname, es_member.nickname from es_maimo,es_member where es_member.member_id = es_maimo.member_id order by id desc";
      Page page = this.baseDaoSupport.queryForPage(sql, pageNo,pageSize, Maimo.class);
      return page;
    }
    
    public boolean add(Maimo maimo)
    {  
        this.daoSupport.insert("ES_MAIMO", maimo);
        return true;
    }
}
