package com.enation.app.b2b2c.core.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.StoreBonus;
import com.enation.app.b2b2c.core.service.IStoreBonusManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 店铺优惠卷查询
 * @author xulipeng
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
public class StoreBonusManager extends BaseSupport implements IStoreBonusManager {

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.IStoreBonusManager#getBonusList(java.lang.Integer)
	 */
	@Override
	public List getBonusList(Integer store_id) {
		return this.baseDaoSupport.queryForList("select * from es_bonus_type where store_id=?", store_id);
	}

	@Override
	public List<Map> getMemberBonusList(long memberid, Integer store_id,Double min_goods_amount) {
		long current = DateUtil.getDateline();
		StringBuffer sql = new StringBuffer("select * from es_member_bonus m left join es_bonus_type b on b.type_id = m.bonus_type_id  where m.used=0 ");
		sql.append(" and m.member_id="+memberid);
		if (store_id != null && store_id > 0) sql.append(" and b.store_id="+store_id);
		else sql.append(" and (b.store_id=0 OR b.store_id IS NULL)");
		sql.append(" and b.min_goods_amount<="+min_goods_amount);
		sql.append(" and b.use_end_date>=" + current);
		sql.append(" and b.use_start_date<=" + current);
		sql.append(" order by  b.type_money asc ");
		return this.daoSupport.queryForList(sql.toString());
	}

	@Override
	public StoreBonus get(Integer bonusid) {
		String sql ="select * from es_bonus_type where type_id=?";
		StoreBonus bonus = (StoreBonus) this.daoSupport.queryForObject(sql, StoreBonus.class, bonusid);
		return bonus;
	}

	@Override
	public Page getBonusListBymemberid(int pageNo,int pageSize,long memberid, boolean isStore) { 

		
		String sql = "select m.*,b.type_id,b.type_money,b.send_type,b.min_amount,b.max_amount,b.send_start_date,b.send_end_date,b.use_start_date,"
				+"b.use_end_date,b.min_goods_amount,b.use_num,b.create_num,b.recognition"
				+",b.store_id, s.store_name from es_member_bonus m "
				+ " inner join es_bonus_type b on b.type_id = m.bonus_type_id"
				+ " left join es_store s on b.store_id=s.store_id "
				+ "where m.member_id="+memberid
				+ " and "+(isStore?"b.store_id>0":"(b.store_id=0 or b.store_id is null)")
				+ " order by m.used_time desc, m.bonus_id desc";
		Page webPage = this.daoSupport.queryForPage(sql, pageNo, pageSize);
		return webPage;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void setBonusUsed(Integer bonus_id,long member_id) {
		this.daoSupport.execute("update es_member_bonus set used=1 where bonus_type_id="+bonus_id+" and member_id="+member_id);
		this.daoSupport.execute("update es_bonus_type set use_num=(use_num+1) where type_id="+bonus_id);
	}
	
	
	@Override
	public Page getConditionBonusList(Integer pageNo, Integer pageSize,Integer store_id,Map map){
		String keyword=String.valueOf(map.get("keyword"));
		String add_time_from=String.valueOf(map.get("add_time_from"));
		String add_time_to=String.valueOf(map.get("add_time_to"));
		StringBuffer sql =new StringBuffer("select * from es_bonus_type where store_id= "+ store_id);
		
		if(!StringUtil.isEmpty(keyword)&&!keyword.equals("null")){
			sql.append(" AND type_name like '%" + keyword + "%'");
		}
		
		if(!StringUtil.isEmpty(add_time_from)&&!add_time_from.equals("null")){
			sql.append(" AND use_start_date >="+DateUtil.getDateline(add_time_from));
		}
		if(!StringUtil.isEmpty(add_time_to)&&!add_time_to.equals("null")){
		    add_time_to += " 23:59:59";
			sql.append(" AND use_end_date <="+DateUtil.getDatelineTime(add_time_to));
		}
		sql.append(" AND use_end_date>"+DateUtil.getDateline()); // 2015/12/29 humaodong filter expired.
		Page rpage = this.daoSupport.queryForPage(sql.toString(),pageNo, pageSize, StoreBonus.class);
		 
		return rpage;
		
	}

}
