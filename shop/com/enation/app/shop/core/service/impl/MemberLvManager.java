package com.enation.app.shop.core.service.impl;
import java.util.ArrayList;
import java.util.List;

import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
 

/**
 * 会员级别管理
 * @author kingapex
 *2010-4-29下午11:04:43
 * @author LiFenLong 2014-4-1; 4.0版本改造,delete方法参数String 更改为integer[]
 */
public class MemberLvManager extends BaseSupport<MemberLv> implements IMemberLvManager{

	
	public void add(MemberLv lv) {
		if(lv.getDefault_lv()==1){
			this.baseDaoSupport.execute("update es_member_lv m set m.default_lv=0");
		}
		this.baseDaoSupport.insert("member_lv", lv);
	}

	
	public void edit(MemberLv lv) {
		if(lv.getDefault_lv()==1){
			this.baseDaoSupport.execute("update es_member_lv m set m.default_lv=0");
		}
		this.baseDaoSupport.update("member_lv", lv, "lv_id=" + lv.getLv_id());
	}

	
	public Integer getDefaultLv() {
		String sql  ="select * from member_lv where default_lv=1";
		List<MemberLv> lvList  = this.baseDaoSupport.queryForList(sql, MemberLv.class);
		if(lvList==null || lvList.isEmpty()){
			return null;
		}
		
		return lvList.get(0).getLv_id();
	}

	
	public Page list(String order, int page, int pageSize) {
		order = order == null ? " lv_id desc" : order;
		String sql = "select * from member_lv ";
		sql += " order by  " + order;
		Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}

	
	public MemberLv get(Integer lvId) {
		if(lvId!=null&&lvId!=0){
			String sql = "select * from member_lv where lv_id=?";
			MemberLv lv = this.baseDaoSupport.queryForObject(sql, MemberLv.class,lvId);
			return lv;
		}else{
			return null;
		}
	}

	
	public void delete(Integer[] id) {
		if (id == null || id.equals(""))
			return;
		String id_str = StringUtil.arrayToString(id, ",");
		String sql = "delete from member_lv where lv_id in (" + id_str + ")";
		this.baseDaoSupport.execute(sql);
	}

	
	public List<MemberLv> list() {
		String sql = "select * from member_lv order by lv_id";
		List lvlist = this.baseDaoSupport.queryForList(sql, MemberLv.class);
		return lvlist;
	}

	
	public List<MemberLv> list(String ids) {
		
		if( StringUtil.isEmpty(ids)) return new ArrayList();
		
		String sql = "select * from member_lv where  lv_id in("+ids+") order by lv_id";
		List lvlist = this.baseDaoSupport.queryForList(sql, MemberLv.class);
		return lvlist;
		 
	}





	public MemberLv getByPoint(int point) {
		String sql = "select * from member_lv where  point<=? order by point desc";
		List<MemberLv> list =this.baseDaoSupport.queryForList(sql, MemberLv.class,point);
		if(list==null || list.isEmpty())
		return null;
		else
			return list.get(0);
	}
	
	public MemberLv getNextLv(int point){
		String sql = "select * from member_lv where  point>? order by point ASC";
		List<MemberLv> list =this.baseDaoSupport.queryForList(sql, MemberLv.class,point);
		if(list==null || list.isEmpty())
			return null;
		else
			return list.get(0);
	}
	
	/**
	 * 根据等级获取商品分类折扣列表
	 * @param lv_id
	 * @return
	 */
	public List getCatDiscountByLv(int lv_id){
		String sql= "select d.*,c.name from " + this.getTableName("member_lv_discount") + " d inner join " + this.getTableName("goods_cat") + " c on d.cat_id=c.cat_id where d.lv_id="+lv_id;
		return this.daoSupport.queryForList(sql);
	}
	
	/**
	 * 根据等级获取该等级有折扣的类别列表
	 * @param lv_id
	 * @return
	 */
	public List getHaveCatDiscountByLv(int lv_id){
		String sql = "select * from member_lv_discount where discount>0 and lv_id="+lv_id;
		return this.baseDaoSupport.queryForList(sql);
	}
	
	/**
	 * 保存某等级的商品类别对应折扣
	 * @param cat_ids 与 discount是一一对应关系
	 * @param discounts 与catids是一一对应关系
	 * @param lv_id
	 */
	public void saveCatDiscountByLv(int[] cat_ids,int[] discounts,int lv_id){
		if(cat_ids.length!=discounts.length){
			throw new RuntimeException("非法参数");
		}
		for (int i = 0; i < discounts.length; i++) {
			if(discounts[i]==0)
				continue;
			String sql = "update member_lv_discount set discount="+discounts[i]+" where cat_id="+cat_ids[i]+" and lv_id="+lv_id;
//			//System.out.println(sql);
			this.baseDaoSupport.execute(sql);
		}
		
		
	}
	
	/**
	 * 根据类别获取所有等级的折扣
	 * @param cat_id
	 */
	public List getHaveLvDiscountByCat(int cat_id){
		String sql = "select * from member_lv_discount where discount>0 and cat_id="+cat_id;
		return this.baseDaoSupport.queryForList(sql);
	}


	



	 

}
