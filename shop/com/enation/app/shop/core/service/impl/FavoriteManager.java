/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.  
 * 描述：我的收藏 Manager  
 * 修改人：Sylow  
 * 修改时间：2015-09-15  
 * 修改内容：增加b2b2c app需要的api  
 */
package com.enation.app.shop.core.service.impl;

import java.util.List;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Favorite;
import com.enation.app.shop.core.service.IFavoriteManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;

/**
 * 我的收藏
 * 
 * @author lzf<br/>
 *         2010-3-24 下午02:54:26<br/>
 *         version 1.0<br/>
 * @version v1.1 2015-09-14
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class FavoriteManager extends BaseSupport implements IFavoriteManager {

	
	public Page list(int pageNo, int pageSize) {
		Member member = UserConext.getCurrentMember();
		String sql = "select g.*, f.favorite_id from " + this.getTableName("favorite")
				+ " f left join " + this.getTableName("goods")
				+ " g on g.goods_id = f.goods_id";
		sql += " where f.member_id = ?";
		Page page = this.daoSupport.queryForPage(sql, pageNo, pageSize, member
				.getMember_id());
		return page;
	}

	/*
	 * linyang modify
	 * 修改收藏bug ,修改sql语句，左连接改为内连接，因为如果商品删除，商品的信息检索不出来，
	 * 页面报错，暂时修改为已经删除的商品，收藏不显示
	 */
	@Override
	public Page list(long memberid, int pageNo, int pageSize) {
		String sql = "select g.*, f.favorite_id from " + this.getTableName("favorite")
				+ " f," + this.getTableName("goods")
				+ " g where g.goods_id = f.goods_id";
		sql += " and f.member_id = ? order by f.favorite_id";
		Page page = this.daoSupport.queryForPage(sql, pageNo, pageSize,memberid);
		return page;
	}
	
	
	
	public void delete(int favorite_id) {
		this.baseDaoSupport.execute("delete from favorite where favorite_id = ?", favorite_id);
	}

	
	public void add(Integer goodsid) {
		Member member = UserConext.getCurrentMember();
		Favorite favorite = new Favorite();
		favorite.setGoods_id(goodsid);
		favorite.setMember_id(member.getMember_id());
		this.baseDaoSupport.insert("favorite", favorite);
	}
	
	/**
	 * 根据商品ID和用户ID获取一个收藏
	 */
	public int getCount(Integer goodsid, long memeberid){
		return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM favorite WHERE goods_id=? AND member_id=?", goodsid,memeberid);
	}
	
	public int getCount(long memberId){
		return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM favorite WHERE  member_id=?", memberId);
	}

	
	public List list( ) {
		Member member = UserConext.getCurrentMember();
		
		return this.baseDaoSupport.queryForList("select * from favorite where member_id=?", Favorite.class, member.getMember_id());
	}


	@Override
    public Favorite get(int favorite_id) {
        String sql = "SELECT * FROM favorite WHERE favorite_id=?";
        return (Favorite) baseDaoSupport.queryForObject(sql, Favorite.class, favorite_id);
    }

	@Override
    public void delete(int goodsid, long memberid){
        baseDaoSupport.execute("DELETE FROM favorite WHERE goods_id=? AND member_id=?", goodsid, memberid);
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
    public boolean isFavorited(int goodsid, long memeberid){
        return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM favorite WHERE goods_id=? AND member_id=?", goodsid,memeberid) > 0;
    }
	
	@Override
	public List listGoods(){
		Member member = UserConext.getCurrentMember();
		String sql = "select g.*, f.favorite_id from " + this.getTableName("favorite")
				+ " f left join " + this.getTableName("goods")
				+ " g on g.goods_id = f.goods_id";
		sql += " where f.member_id = ? order by f.favorite_id";
		return this.baseDaoSupport.queryForList(sql,  member.getMember_id());
	}

}
