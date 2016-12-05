package com.enation.app.b2b2c.core.service.goods.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.store.StoreCat;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsCatManager;
import com.enation.app.shop.core.openapi.service.impl.ShopOpenApiManager;
import com.enation.eop.sdk.database.BaseSupport;


@Component
public class StoreGoodsCatManager extends BaseSupport implements IStoreGoodsCatManager{

    // 店铺信息管理OpenApi接口
    private ShopOpenApiManager shopOpenApiManager;
	
	@Override
	public List storeCatList(Integer storeId) {
		String sql = "select * from es_store_cat where store_id="+storeId+" order by sort asc ";
		List<Map> list = this.baseDaoSupport.queryForList(sql);
		
		for (Map map: list) {
		    int storeCatId = NumberUtils.toInt(map.get("store_cat_id").toString());
		    int childrenCount = this.childrenCount(storeCatId);
		    
		    if (childrenCount > 0) {
		        map.put("has_child", "1");
		    } else {
		        map.put("has_child", "0");
		    }
		}
		return list;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void addStoreCat(StoreCat storeCat) {
		if (storeCat.getStore_cat_pid() == null) {
			storeCat.setStore_cat_pid(0);
		}
		if (storeCat.getSort() == null) {
		    storeCat.setSort(0);
		}
		this.baseDaoSupport.insert("es_store_cat", storeCat);
		
		// 同步调用OpenApi
        try {
            shopOpenApiManager.addCat(storeCat);
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void editStoreCat(StoreCat storeCat) {
	    if (storeCat.getSort() == null) {
            storeCat.setSort(0);
        }
		this.baseDaoSupport.update("es_store_cat", storeCat, " store_cat_id="+storeCat.getStore_cat_id());
		
		// 同步调用OpenApi
        try {
            shopOpenApiManager.updateCat(storeCat);
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void deleteStoreCat(Integer storeCatId,Integer storeid) {
		Integer num = this.baseDaoSupport.queryForInt("select count(0) from es_store_cat s where s.store_cat_pid=? and store_id=?",storeCatId,storeid);
		if(num>=1){
			throw new RuntimeException("删除失败，分类*有下级分类！");
		}
		
		int goodsnum = this.baseDaoSupport.queryForInt("select count(0) from es_goods where store_cat_id=? and store_id=?", storeCatId,storeid);
		if(goodsnum>=1){
			throw new RuntimeException("删除失败，请删除此分类*下所有商品(包括商品回收站)！");
		}
		
		String sql="delete from es_store_cat where store_cat_id=? and store_id=?";
		this.baseDaoSupport.execute(sql,storeCatId,storeid);
		
		// 同步调用OpenApi
        try {
            shopOpenApiManager.deleteCat(storeCatId);
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
	}

	@Override
	public List getStoreCatList(Integer catid,Integer storeId) {
		String sql = "select * from es_store_cat where store_cat_pid=0 and store_id="+storeId;
		if(catid!=null){
			sql+=" and store_cat_id!="+catid;
		}
		sql+="  order by sort asc";
		return this.baseDaoSupport.queryForList(sql);
	}

	@Override
	public StoreCat getStoreCat(Map map) {
		Integer storeid = (Integer) map.get("storeid");
		Integer strore_catid = (Integer) map.get("store_catid");
		String sql = "select * from es_store_cat where store_id=? and store_cat_id=?";
		List<StoreCat> list = this.baseDaoSupport.queryForList(sql,StoreCat.class,storeid,strore_catid);
		StoreCat storeCat =new StoreCat();
		if(!list.isEmpty()){
			storeCat= (StoreCat) list.get(0);
		}
		return storeCat;
	}

	@Override
	public Integer is_children(Integer catid) {
		String sql ="select store_cat_pid from es_store_cat where store_cat_id=?";
		Integer pid=this.baseDaoSupport.queryForInt(sql, catid);
		return pid;
	}
	
	@Override
	public Integer getStoreCatNum(Integer store_id,Integer store_cat_pid,Integer sort) {
		String sql = "select count(0) from store_cat where store_id =? and store_cat_pid=? and sort=?";
		return this.baseDaoSupport.queryForInt(sql, store_id,store_cat_pid,sort);
	}

    @Override
    public Integer childrenCount(Integer catid) {
        String sql = "select count(*) from es_store_cat where store_cat_pid=?";
        return this.baseDaoSupport.queryForInt(sql, catid);
    }
    
    public void setShopOpenApiManager(ShopOpenApiManager shopOpenApiManager) {
        this.shopOpenApiManager = shopOpenApiManager;
    }
}
