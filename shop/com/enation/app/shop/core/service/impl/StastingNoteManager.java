package com.enation.app.shop.core.service.impl;

import java.util.List;

import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.model.Sommelier;
import com.enation.app.shop.core.model.TastingNote;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

public class StastingNoteManager extends BaseSupport<TastingNote> {

	public void add(TastingNote tastingNote) {
		this.baseDaoSupport.insert("es_tasting_note", tastingNote);
	}
	
	public void edit(TastingNote tastingNote) {
		this.baseDaoSupport.update("es_tasting_note", tastingNote, "id=" + tastingNote.getId());
	}
	
	public void delete(Integer[] id) {
        if (id == null || id.equals(""))
            return;
        String id_str = StringUtil.arrayToString(id, ",");
        String sql = "delete from es_tasting_note where id in (" + id_str + ")";
        this.baseDaoSupport.execute(sql);
	}
	
	
    public TastingNote get(Integer id) {
	        if(id!=null&&id!=0){
	            String sql = "select * from es_tasting_note where id=?";
	            TastingNote tastingNote = this.baseDaoSupport.queryForObject(sql, TastingNote.class,id);
	            return tastingNote;
	        }else{
	            return null;
	        }
	 }
	
    public TastingNote getTastingNote(Integer id) {
        if(id!=null&&id!=0){
            String sql = "select * from es_tasting_note where id=?";
            TastingNote tastingNote = (TastingNote)this.baseDaoSupport.queryForObject(sql,TastingNote.class,id);
            return tastingNote;
        }else{
            return null;
        }
 }
    public Page list(String order, int page, int pageSize) {
        order = order == null ? " id desc" : order;
        String sql = "select * from es_sommelier";
        sql += " order by  " + order;
        Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
        return webpage;
    }
    
    public Page list_tast_note(String order, int page, int pageSize, int uid) {
        order = order == null ? " id desc" : order;
        String sql = "select * from es_tasting_note where userid="+uid;
        sql += " order by  " + order;
        Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
        return webpage;
    }
    
    public List list() {
        String sql = "select * from es_tasting_note order by id";
        List yuemolist = this.baseDaoSupport.queryForList(sql, Sommelier.class);
        return yuemolist;
    }
	
	
	
	
	public Page get(int pageNo, int pageSize) {
		return this.baseDaoSupport.queryForPage("select * from es_tasting_note order by id", pageNo, pageSize);
	}
	
}
