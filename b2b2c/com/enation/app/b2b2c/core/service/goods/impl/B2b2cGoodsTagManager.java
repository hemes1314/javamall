package com.enation.app.b2b2c.core.service.goods.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.goods.IB2b2cGoodsTagManager;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
@Component
public class B2b2cGoodsTagManager extends BaseSupport implements IB2b2cGoodsTagManager {
	private IGoodsCatManager goodsCatManager;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IB2b2cGoodsTagManager#list(int, int)
	 */
	@Override
	public Page list(int pageNo, int pageSize) {

		return this.daoSupport.queryForPage("select * from es_tags where store_id is NULL order by tag_id", pageNo, pageSize);
	}
	/**
	 * 获取列表Sql
	 * @param tag_id
	 * @return
	 */
	private String getListSql(int tag_id) {
		String 	sql = "select g.*,b.name as brand_name ,t.name as type_name,c.name as cat_name  from es_goods g left join es_goods_cat c on g.cat_id=c.cat_id left join es_brand  b on g.brand_id = b.brand_id and b.disabled=0 left join es_goods_type";
				sql+= " t on g.type_id =t.type_id  where g.goods_type = 'normal' and g.disabled=0 )";
		return sql;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IB2b2cGoodsTagManager#groupBuyList(int, java.util.Map, int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public Page groupBuyList(int tagid, Map goodsMap, int page, int pageSize,
			String sort, String order) {
		String sql = getListSql(tagid);
		Integer brandid = (Integer) goodsMap.get("brandid");
		Integer catid = (Integer) goodsMap.get("catid");
		String name = (String) goodsMap.get("name");
		String sn = (String) goodsMap.get("sn");
		Integer stype = (Integer) goodsMap.get("stype");
		String keyword = (String) goodsMap.get("keyword");
		
		if (brandid != null && brandid != 0) {
			sql += " and g.brand_id = " + brandid + " ";
		}
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql+=" and ( g.name like '%"+keyword+"%'";
				sql+=" or g.sn like '%"+keyword+"%')";
			}
		}
		
		if (name != null && !name.equals("")) {
			name = name.trim();
			String[] keys = name.split("\\s");
			for (String key : keys) {
				sql += (" and g.name like '%");
				sql += (key);
				sql += ("%'");
			}
		}

		if (sn != null && !sn.equals("")) {
			sql += "   and g.sn like '%" + sn + "%'";
		}


		if (catid != null && catid!=0) {
			Cat cat = this.goodsCatManager.getById(catid);
			sql += " and  g.cat_id in(";
			sql += "select c.cat_id from " + this.getTableName("goods_cat")
					+ " c where c.cat_path like '" + cat.getCat_path()
					+ "%')  ";
		}
		//System.out.println(sql);
		return this.daoSupport.queryForPage(sql, page, pageSize );
	}
	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}
	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

}
