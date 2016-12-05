package com.enation.app.b2b2c.core.tag.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.IStoreDlyTypeManager;
import com.enation.app.b2b2c.core.service.IStoreTemplateManager;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 店铺配送方式标签
 * @author xulipeng
 *	2015年1月13日15:46:16
 */

@Component
public class StoreDlytypeTag extends BaseFreeMarkerTag {
	
	private IDlyTypeManager dlyTypeManager;
	private IStoreDlyTypeManager storeDlyTypeManager;
	private IStoreCartManager storeCartManager;
	private IStoreGoodsManager storeGoodsManager;
	private IStoreTemplateManager storeTemplateManager;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer storeid= (Integer) params.get("storeid");
		Integer regionid = (Integer) params.get("regionid");
		String originalPrice = (String) params.get("originalPrice");
		String weight = (String) params.get("weight");
		
		List<Map> list = new ArrayList<Map>();
		if(Double.valueOf(weight)!=0d){
			Integer tempid = this.storeTemplateManager.getDefTempid(storeid);
			list = this.storeDlyTypeManager.getDlyTypeList(tempid);
			for(Map maps:list){
				Integer typeid = (Integer) maps.get("type_id");
				Double[] priceArray = this.dlyTypeManager.countPrice(typeid, Double.valueOf(weight), Double.valueOf(originalPrice), regionid+"");
				Double dlyPrice = priceArray[0];//配送费用
				maps.put("dlyPrice", dlyPrice);
			}
		}
		
		return list;
	}
	
	

	public IStoreTemplateManager getStoreTemplateManager() {
		return storeTemplateManager;
	}


	public void setStoreTemplateManager(IStoreTemplateManager storeTemplateManager) {
		this.storeTemplateManager = storeTemplateManager;
	}


	public IStoreDlyTypeManager getStoreDlyTypeManager() {
		return storeDlyTypeManager;
	}

	public void setStoreDlyTypeManager(IStoreDlyTypeManager storeDlyTypeManager) {
		this.storeDlyTypeManager = storeDlyTypeManager;
	}

	public IDlyTypeManager getDlyTypeManager() {
		return dlyTypeManager;
	}
	public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
		this.dlyTypeManager = dlyTypeManager;
	}


	public IStoreCartManager getStoreCartManager() {
		return storeCartManager;
	}


	public void setStoreCartManager(IStoreCartManager storeCartManager) {
		this.storeCartManager = storeCartManager;
	}


	public IStoreGoodsManager getStoreGoodsManager() {
		return storeGoodsManager;
	}


	public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
		this.storeGoodsManager = storeGoodsManager;
	}

}
