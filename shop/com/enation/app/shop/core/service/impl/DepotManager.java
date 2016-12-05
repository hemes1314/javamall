package com.enation.app.shop.core.service.impl;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.Depot;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.IntegerMapper;


/***
 * 库房管理实现
 * @author kingapex
 *
 */
public class DepotManager extends BaseSupport<Depot> implements IDepotManager {

	
	
	@Override
	public void add(Depot room) {
		if(room.getChoose()==1){
			this.daoSupport.execute("update es_depot set choose=0 where choose=1");
		}
		this.baseDaoSupport.insert("depot", room);
	}

	@Override
	public void update(Depot room) {
		if(room.getChoose()==1){
			this.daoSupport.execute("update es_depot set choose=0 where choose=1");
		}
		this.baseDaoSupport.update("depot", room, "id="+room.getId());
	}

	@Override
	public Depot get(int roomid) {
		return this.baseDaoSupport.queryForObject("select * from depot where id=?", Depot.class, roomid);
		 
	}

	@Override
	public List<Depot> list() {
		
		return this.baseDaoSupport.queryForList("select * from depot", Depot.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String delete(int roomid) {
		String message=this.depot_validate(roomid);
		if(message.equals("")){
			this.baseDaoSupport.execute("delete from goods_depot where depotid = ?", roomid);
			this.baseDaoSupport.execute("delete from product_store where depotid = ?", roomid);
			this.baseDaoSupport.execute("delete from depot_user where depotid = ?", roomid);
			this.baseDaoSupport.execute("delete from depot where id = ?", roomid);
			message="删除成功";
		}
		return message;
		
	}
	private String depot_validate(int roomid){
		int is_choose= baseDaoSupport.queryForInt("select choose from depot where id=?", roomid);
		if(is_choose==1){
			return "此仓库为默认仓库，不能删除";
		}
		List<Integer> numList  =(List)baseDaoSupport.queryForList("select sum(store) from product_store where depotid=? and store>0  and goodsid in (select goods_id from es_goods g where g.disabled=0 )",new IntegerMapper(), roomid);
		
		Integer  has_goods=0;
		if(numList!=null && numList.isEmpty()){
			has_goods = numList.get(0);
			if( has_goods==null) has_goods=0;
		}
		
		if(has_goods!=0){
			return "此仓库仍有商品。不能删除";
		}
		return "";
	}
}
