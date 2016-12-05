package com.enation.app.shop.mobile.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Favorite;
import com.enation.app.shop.mobile.service.IApiFavoriteManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;

/**
 * Created by Dawei on 4/29/15.
 */
@Component
public class ApiFavoriteManager extends BaseSupport<Favorite> implements IApiFavoriteManager {

   
	@Override
    public Favorite get(int favorite_id) {
        String sql = "SELECT * FROM favorite WHERE favorite_id=?";
        return baseDaoSupport.queryForObject(sql, Favorite.class, favorite_id);
    }

    
	@Override
    public Favorite get(int goodsid, long memberid){
        String sql = "SELECT * FROM favorite WHERE goods_id=? AND member_id=?";
        List<Favorite> favoriteList = baseDaoSupport.queryForList(sql, Favorite.class, goodsid, memberid);
        if(favoriteList.size() > 0){
            return favoriteList.get(0);
        }
        return null;
    }

   
	@Override
    public void delete(int goodsid, long memberid){
        baseDaoSupport.execute("DELETE FROM favorite WHERE goods_id=? AND member_id=?", goodsid, memberid);
    }


   
	@Override
    public boolean isFavorited(int goodsid, long memeberid){
        return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM favorite WHERE goods_id=? AND member_id=?", goodsid,memeberid) > 0;
    }
    
	@Override
    public void add(Integer goodsid, long memberid) {
        String sql = "INSERT INTO favorite(member_id,goods_id,favorite_time) VALUES(?,?,?)";
        baseDaoSupport.execute(sql, memberid, goodsid, com.enation.framework.util.DateUtil.getDateline());
    }
	
	@Override
	public Page list(long memberid, int pageNo, int pageSize) {
		String sql = "select g.name,g.goods_id,g.price,g.sn,g.thumbnail,g.buy_count,g.comment_num, f.favorite_id from " + this.getTableName("favorite")
				+ " f left join " + this.getTableName("goods")
				+ " g on g.goods_id = f.goods_id";
		sql += " where g.disabled = 0 and f.member_id = ? order by f.favorite_id";
		Page page = this.daoSupport.queryForPage(sql, pageNo, pageSize,memberid);
		return page;
	}
	
	
	@Override
	public void delete(int favorite_id) {
		this.baseDaoSupport.execute("delete from favorite where favorite_id = ?", favorite_id);
	}

	@Override
	public void add(Integer goodsid) {
		Member member = UserConext.getCurrentMember();
		Favorite favorite = new Favorite();
		favorite.setGoods_id(goodsid);
		favorite.setMember_id(member.getMember_id());
		this.baseDaoSupport.insert("favorite", favorite);
	}


	@Override
	public int getFavoriteCount(int goods_id) {

		return this.baseDaoSupport.queryForInt("select count(0) from es_favorite where goods_id=?",goods_id);
	}


	@Override
	public void addStore(Integer store_id, long member_id) {
		 String sql = "INSERT INTO ES_MEMBER_COLLECT(MEMBER_ID,STORE_ID,CREATE_TIME) VALUES(?,?,?)";
	        baseDaoSupport.execute(sql, member_id, store_id, com.enation.framework.util.DateUtil.getDateline());
	  
	}


	@Override
	public void deleteForApp(String favorite_id) {
		this.baseDaoSupport.execute("delete from ES_FAVORITE where favorite_id in("+favorite_id+")");

		
	}


	@Override
	public Page listForApp(long member_id, int page, int PAGE_SIZE) {
		String sql =" select s.store_name ,s.attr,s.tel,s.store_level,s.member_name,s.store_logo,s.description,f.* from es_store s "
				+ " left join es_member_collect f on f.store_id = s.store_id where f.member_id=? ";
		return this.baseDaoSupport.queryForPage(sql, page, PAGE_SIZE,member_id);
	}


}
