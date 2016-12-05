package com.enation.app.secbuy.core.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.secbuy.core.model.SecBuyTag;
import com.enation.app.secbuy.core.service.ISecGoodsTagManager;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.plugin.goods.GoodsDataFilterBundle;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
@Component
public class SecGoodsTagManager implements ISecGoodsTagManager{
	private IGoodsCatManager goodsCatManager;
	private IDaoSupport daoSupport;
	private GoodsDataFilterBundle goodsDataFilterBundle;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IB2b2cGoodsTagManager#list(int, int)
	 */
	@Override
	public Page list(int pageNo, int pageSize) {

		return this.daoSupport.queryForPage("select * from es_tags where store_id is NULL or is_secbuy is not NULL order by tag_id", pageNo, pageSize);
	}
	/**
	 * 获取列表Sql
	 * @param tag_id
	 * @return
	 */
	private String getListSql(int tag_id) {
		String 	sql = "select g.*,b.name as brand_name ,t.name as type_name,c.name as cat_name  from es_goods g left join es_goods_cat c on g.cat_id=c.cat_id left join es_brand  b on g.brand_id = b.brand_id and b.disabled=0 left join es_goods_type";
				sql+= " t on g.type_id =t.type_id  where g.goods_type = 'normal' and g.disabled=0 and goods_id in (select goods_id from es_secbuy_goods where act_id=(select act_id from es_secbuy_active where act_tag_id="+tag_id+"))";
		return sql;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IB2b2cGoodsTagManager#secBuyList(int, java.util.Map, int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public Page secBuyList(int tagid, Map goodsMap, int page, int pageSize,
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
			sql += "select c.cat_id from es_goods_cat"
					+ " c where c.cat_path like '" + cat.getCat_path()
					+ "%')  ";
		}
		//System.out.println(sql);
		return this.daoSupport.queryForPage(sql, page, pageSize );
	}
	@Override
	public void add(SecBuyTag tag) {
		this.daoSupport.insert("es_tags", tag);
	}
	@Override
	public List listGoods(String catid, String tagid, String goodsnum) {
		int num = 10;
		if(!StringUtil.isEmpty(goodsnum)){
			num = Integer.valueOf(goodsnum);
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("select g.*,gb.price as gb_price,gb.original_price as original_price from es_tag_rel r LEFT JOIN es_goods g ON g.goods_id=r.rel_id INNER JOIN es_secbuy_goods gb ON gb.goods_id=g.goods_id where gb.gb_status=1 and g.disabled=0 and g.market_enable=1");
		
		if(! StringUtil.isEmpty(catid) ){
			Cat cat  = this.goodsCatManager.getById(Integer.valueOf(catid));
			if(cat!=null){
				String cat_path  = cat.getCat_path();
				if (cat_path != null) {
					sql.append( " and  g.cat_id in(" ) ;
					sql.append("select c.cat_id from es_goods_cat");
					sql.append(" c where c.cat_path like '" + cat_path + "%')");
				}
			}
		}
		
		if(!StringUtil.isEmpty(tagid)){
			sql.append(" AND r.tag_id="+tagid+"");
		}
		
		sql.append(" order by r.ordernum desc");
		System.out.println(sql.toString());
		List list = this.daoSupport.queryForListPage(sql.toString(), 1,num);
		this.goodsDataFilterBundle.filterGoodsData(list);
		return list;
	}
	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}
	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public GoodsDataFilterBundle getGoodsDataFilterBundle() {
		return goodsDataFilterBundle;
	}
	public void setGoodsDataFilterBundle(GoodsDataFilterBundle goodsDataFilterBundle) {
		this.goodsDataFilterBundle = goodsDataFilterBundle;
	}
	
	
}
