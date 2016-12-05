package com.enation.app.shop.core.service.impl;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Gnotify;
import com.enation.app.shop.core.service.IGnotifyManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;

@SuppressWarnings("rawtypes")
public class GnotifyManager extends BaseSupport implements IGnotifyManager {
	 
	
	public Page pageGnotify(int pageNo, int pageSize) {
		Member member = UserConext.getCurrentMember();

		String sql = "select a.*,b.sn, b.thumbnail image,b.store store, b.name name, b.price price, b.mktprice mktprice,b.cat_id cat_id from "+ this.getTableName("gnotify")+" a inner join "+ this.getTableName("goods")+" b on b.goods_id = a.goods_id ";

		sql += " and a.member_id = " + member.getMember_id();
		
		sql+=" order by a.create_time desc";
 		Page webpage = this.daoSupport.queryForPage(sql, pageNo, pageSize);
		return webpage;
	}
 
	
	public void deleteGnotify(int gnotify_id) {
		this.baseDaoSupport.execute("delete from gnotify where gnotify_id = ?", gnotify_id);
	}
	
	public void addGnotify(int goodsid){
		
		String sql = "select count(0) from gnotify where goods_id=?";
		int count = this.baseDaoSupport.queryForInt(sql, goodsid);
		if(count>0) return ;
		
		
		Member member = UserConext.getCurrentMember();
		Gnotify gnotify = new Gnotify();
		gnotify.setCreate_time(System.currentTimeMillis());
		gnotify.setGoods_id(goodsid);
		if(member!=null){
			gnotify.setMember_id(member.getMember_id());
			gnotify.setEmail(member.getEmail());
		}
		this.baseDaoSupport.insert("gnotify", gnotify);
		
	}

}
