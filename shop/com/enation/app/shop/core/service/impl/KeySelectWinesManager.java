package com.enation.app.shop.core.service.impl;

import java.util.List;

import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.model.Tag;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

public class KeySelectWinesManager extends BaseSupport<Tag> {

	public void edit(Tag tag) {
		this.baseDaoSupport.update("es_tags", tag, "tag_id="+tag.getTag_id());
	}

    
    public Page list(String order, int page, int pageSize){ 
        order = order == null ? " TAG_ID desc" : order;
        String sql = "select TAG_ID,TAG_NAME from es_tags where IS_KEY_SELECT = 1";
        sql += " order by  " + order;
        Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
        return webpage;
    }	
    
    public void delete(Integer[] id) {
        if (id == null || id.equals(""))
            return;
        String id_str = StringUtil.arrayToString(id, ",");
        String sql = "update es_tags set is_key_select = 0 where tag_id in (" + id_str + ")";
        this.baseDaoSupport.execute(sql);
    }
}
