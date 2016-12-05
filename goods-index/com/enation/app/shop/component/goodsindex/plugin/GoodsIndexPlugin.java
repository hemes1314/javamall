/**
 * 
 */
package com.enation.app.shop.component.goodsindex.plugin;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.component.plugin.store.IStoreDisEvent;
import com.enation.app.shop.component.goodsindex.service.IGoodsIndexManager;
import com.enation.app.shop.core.plugin.goods.IGoodsAfterAddEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsAfterEditEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsDeleteEvent;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 商品索引插件
 * @author kingapex
 *2015-4-16
 */
@Component
public class GoodsIndexPlugin extends AutoRegisterPlugin implements
		IGoodsAfterAddEvent, IGoodsAfterEditEvent,IGoodsDeleteEvent,
		IStoreDisEvent {
	private IDaoSupport daoSupport;
	private IGoodsIndexManager goodsIndexManager;
	
	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.goods.IGoodsAfterEditEvent#onAfterGoodsEdit(java.util.Map, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void onAfterGoodsEdit(Map goods, HttpServletRequest arg1) {
		this.goodsIndexManager.updateIndex(goods);
	}
 
	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.goods.IGoodsAfterAddEvent#onAfterGoodsAdd(java.util.Map, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void onAfterGoodsAdd(Map goods, HttpServletRequest arg1)
			throws RuntimeException {
		this.goodsIndexManager.addIndex(goods);
	}

	@Override
    public void onAfterStoreDis(int storeId) {
       
	    try{
	        String sql = "SELECT * FROM ES_GOODS WHERE STORE_ID = ?";
	        List<Map> goodsList = this.daoSupport.queryForList(sql, storeId);
	        
	        // 遍历该商铺所有的商品 并刷新静态页
	        for(Map goods : goodsList) {
	            
                this.goodsIndexManager.updateIndex(goods);
	            
	        }
	    }catch(RuntimeException e) {
	        e.printStackTrace();
	    }
        
    }
	
	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.goods.IGoodsDeleteEvent#onGoodsDelete(java.lang.Integer[])
	 */
	@Override
	public void onGoodsDelete(Integer[] ids) {
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "select goods_id,name from es_goods  where goods_id in ("
				+ id_str + ")";
		List<Map> goodsList = this.daoSupport.queryForList(sql);
		for (Map map : goodsList) {
			this.goodsIndexManager.deleteIndex(map);
		}
	}
	
	
	public IGoodsIndexManager getGoodsIndexManager() {
		return goodsIndexManager;
	}

	public void setGoodsIndexManager(IGoodsIndexManager goodsIndexManager) {
		this.goodsIndexManager = goodsIndexManager;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	
	
}
