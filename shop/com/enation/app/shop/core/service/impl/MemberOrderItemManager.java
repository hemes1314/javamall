package com.enation.app.shop.core.service.impl;

import java.util.List;

import com.enation.app.shop.core.model.MemberOrderItem;
import com.enation.app.shop.core.service.IMemberOrderItemManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MemberOrderItemManager extends BaseSupport implements IMemberOrderItemManager {

	@Override
	public void add(MemberOrderItem memberOrderItem) {
		this.baseDaoSupport.execute("INSERT INTO member_order_item(member_id,goods_id,order_id,item_id,commented,comment_time) VALUES(?,?,?,?,0,0)",
				memberOrderItem.getMember_id(),memberOrderItem.getGoods_id(),memberOrderItem.getOrder_id(),memberOrderItem.getItem_id());		
	}

	@Override
	public int count(long member_id, int goods_id,int commented) {
		return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_order_item WHERE member_id=? AND goods_id=? AND commented=?", member_id, goods_id,commented);
	}

	@Override
	public MemberOrderItem get(long member_id, int goods_id, int commented) {
		List list = this.baseDaoSupport.queryForList("SELECT * FROM member_order_item WHERE member_id=? AND goods_id=? AND commented=?", MemberOrderItem.class, 
				member_id,goods_id, commented);
		if(list!=null && list.size()>0)
			return (MemberOrderItem)list.get(0);
		return null;
	}
	
	public int count(long member_id, int goods_id){
		return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_order_item WHERE member_id=? AND goods_id=?", member_id, goods_id);
	}

	@Override
	public void update(MemberOrderItem memberOrderItem) {
//		this.baseDaoSupport.execute("UPDATE member_order_item SET member_id=?,goods_id=?,order_id=?,item_id=?,commented=?,comment_time=? WHERE id=?", 
//				memberOrderItem.getMember_id(),memberOrderItem.getGoods_id(),memberOrderItem.getOrder_id(),memberOrderItem.getItem_id(),
//				memberOrderItem.getCommented(),memberOrderItem.getComment_time(),memberOrderItem.getId());
		this.baseDaoSupport.update("member_order_item", memberOrderItem, "id=" + memberOrderItem.getId());
	}
	
	public Page getGoodsList(long member_id,int commented, int pageNo, int pageSize){
		//by ken 20160303 LEFT JOIN改为inner join,否则空的也带出来了,页面报错.
		String sql = "SELECT g.* FROM " + this.getTableName("member_order_item") + " m INNER JOIN " + this.getTableName("goods") + " g ON m.goods_id=g.goods_id WHERE m.member_id=? AND m.commented=? ORDER BY m.id DESC";
		return this.daoSupport.queryForPage(sql, pageNo, pageSize, member_id, commented);
	}

    @Override
    public Page getGoodsListForApp(long member_id, int commented, int pageNo, int pageSize) {
        String sql = "SELECT g.goods_id,g.name,g.thumbnail,g.price,g.sn,g.store_name,g.buy_count,g.view_count FROM " + this.getTableName("member_order_item") + " m LEFT JOIN " + this.getTableName("goods") + " g ON m.goods_id=g.goods_id WHERE m.member_id=? AND m.commented=? ORDER BY m.id DESC";
        return this.daoSupport.queryForPage(sql, pageNo, pageSize, member_id, commented);

    }
	
	

}
