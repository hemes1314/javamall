package com.enation.app.shop.core.service.impl.cache;

import java.util.ArrayList;
import java.util.List;

import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.impl.MemberLvManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.cache.AbstractCacheProxy;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

public class MemberLvCacheProxy extends AbstractCacheProxy<List<MemberLv> > implements IMemberLvManager {

	private IMemberLvManager memberLvManager;
	
	public static final String CACHE_KEY= "member_lv";
	
	public MemberLvCacheProxy(IMemberLvManager memberLvManager) {
		super(CACHE_KEY);
		this.memberLvManager = memberLvManager;
	}
	
	private void cleanCache(){
		this.cache.remove(CACHE_KEY) ;
	}
	
	public void add(MemberLv lv) {
		memberLvManager.add(lv);
		cleanCache();
	}

	
	public void edit(MemberLv lv) {
		memberLvManager.edit(lv);
		cleanCache();
	}

	
	public Integer getDefaultLv() {
		return memberLvManager.getDefaultLv();
	}

	
	public Page list(String order, int page, int pageSize) {
		return memberLvManager.list(order, page, pageSize);
	}

	
	public MemberLv get(Integer lvId) {
		return memberLvManager.get(lvId);
	}

	
	public void delete(Integer[] id) {
		memberLvManager.delete(id);
		cleanCache();
	}

	
	public List<MemberLv> list() {
		List<MemberLv> memberLvList  = this.cache.get(CACHE_KEY);
		if(memberLvList == null){
			memberLvList  = this.memberLvManager.list();
			this.cache.put(CACHE_KEY, memberLvList);
			if(this.logger.isDebugEnabled()){
				this.logger.debug("load memberLvList from database");
			}			
		} else{
			if(this.logger.isDebugEnabled()){
				this.logger.debug("load memberLvList from cache");
			}
		}
		return memberLvList;
	}
	
	public MemberLv getNextLv(int point){
		return memberLvManager.getNextLv(point);
	}

	
	public List<MemberLv> list(String ids) {
		return memberLvManager.list(ids);		 
	}


	public MemberLv getByPoint(int point) {
		return memberLvManager.getByPoint(point);
	}
	
	/**
	 * 根据等级获取商品分类折的扣列表
	 * @param lv_id
	 * @return
	 */
	public List getCatDiscountByLv(int lv_id){
		return memberLvManager.getCatDiscountByLv(lv_id);
	}
	
	/**
	 * 根据等级获取该等级有折扣的类别列表
	 * @param lv_id
	 * @return
	 */
	public List getHaveCatDiscountByLv(int lv_id){
		return memberLvManager.getHaveCatDiscountByLv(lv_id);
	}
	
	/**
	 * 保存某等级的商品类别对应折扣
	 * @param cat_ids 与 discount是一一对应关系
	 * @param discounts 与catids是一一对应关系
	 * @param lv_id
	 */
	public void saveCatDiscountByLv(int[] cat_ids,int[] discounts,int lv_id){
		memberLvManager.saveCatDiscountByLv(cat_ids, discounts, lv_id);
	}
	/**
	 * 根据类别获取所有等级的折扣
	 * @param cat_id
	 */
	public List getHaveLvDiscountByCat(int cat_id){
		return memberLvManager.getHaveLvDiscountByCat(cat_id);
	}
}
