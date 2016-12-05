/**
 * 
 */
package com.enation.app.base.core.service.impl;

import com.enation.app.base.core.service.IExampleDataCleanManager;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;
import com.enation.framework.database.IDaoSupport;

/**
 * 
 * 示例数据清除管理
 * @author kingapex
 *2015-6-3
 */
public class ExampleDataCleanManager implements IExampleDataCleanManager {
	
	private IDaoSupport  daoSupport;
 
	
	
	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IExampleDataCleanManager#clean(java.lang.String[])
	 */
	@Override
	public void clean(String[] moudels) {
		
		
		if( this.hasMoudel("goods", moudels)){
			this.cleanGoods();
			this.cleanOrder();
		}
		
		if( this.hasMoudel("order", moudels)){
			this.cleanOrder();
		}
		
		if( this.hasMoudel("member", moudels)){
			this.cleanMember();
			this.cleanOrder();
		}
		
		
		if( this.hasMoudel("goodscat", moudels)){
			this.cleanGoodsCat();
		}
		
		
		if( this.hasMoudel("goodstype", moudels)){
			this.cleanGoodsType();
			this.cleanGoodsCat();
			this.cleanGoods();
		}
		
	
		if( this.hasMoudel("brand", moudels)){
			this.cleanBrand();
		}
		
		if( this.hasMoudel("store", moudels)){
			this.cleanStore();
			this.cleanOrder();
		}
		
		
	}
 
	/**
	 * 检测某个模块是否存在
	 * @param m
	 * @param moudels
	 * @return
	 */
	private boolean hasMoudel(String m,String[] moudels){
		for (String s : moudels) {
			if(s.equals(m)){
				return true;
			}
		}
		
		return false;
	}
	
	
	
	/***
	 * 清空商品
	 */
	private void cleanGoods(){
		
		String sql ="TRUNCATE table es_goods";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_goods_gallery";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_product";
		this.daoSupport.execute(sql);
		

		sql ="TRUNCATE table es_product_store";
		this.daoSupport.execute(sql);
		
		
		sql ="TRUNCATE table es_store_log";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_tag_rel";
		this.daoSupport.execute(sql);
		
		
	}
	
	
	
	
	/**
	 * 清空订单
	 */
	private void cleanOrder(){
	
		String sql ="TRUNCATE table es_order";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_order_items";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_order_log";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_order_meta";
		this.daoSupport.execute(sql);
	}
	
	
	/**
	 * 清空商品分类
	 */
	private void cleanGoodsCat(){
		String sql ="TRUNCATE table es_goods_cat";
		this.daoSupport.execute(sql);
		ICache cache = CacheFactory.getCache("goods_cat");
		cache.remove("goods_cat_0");
	}

	
	/**
	 * 清空商品类型
	 */
	private void cleanGoodsType(){
		String sql ="TRUNCATE table es_goods_type";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_type_brand";
		this.daoSupport.execute(sql);
	}

	
	/**
	 * 清除品牌
	 */
	private void cleanBrand(){
		String sql ="TRUNCATE table es_brand";
		this.daoSupport.execute(sql);
		
	}
	
	
	
	
	/**
	 * 清除店铺
	 */
	private void cleanStore(){
		String sql ="TRUNCATE table es_store";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_store_silde";
		this.daoSupport.execute(sql);
		

		
	}
	
	/**
	 * 清空会员
	 */
	private void cleanMember(){
		String sql ="TRUNCATE table es_member";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_member_address";
		this.daoSupport.execute(sql);
		
	}
	
	
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}


	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}


 
	

}
