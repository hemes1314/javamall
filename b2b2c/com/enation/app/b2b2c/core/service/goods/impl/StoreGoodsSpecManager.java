package com.enation.app.b2b2c.core.service.goods.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.service.goods.IStoreGoodsSpecManager;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.database.BaseSupport;
@Component
public class StoreGoodsSpecManager extends BaseSupport implements IStoreGoodsSpecManager {
	private IProductManager productManager;
	
	
	@Override
	public String getStoreHtml(Integer goodsid) {
		List<String> specNameList = productManager.listSpecName(goodsid);

		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.putData("specNameList", specNameList);
		freeMarkerPaser.putData("productList", this.listGoodsStore(goodsid));
		freeMarkerPaser.setPageName("store_info");
		return  freeMarkerPaser.proessPageContent();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Map> listGoodsStore(int goodsid) {
		/**
		 * 首先取出这个商品的所有货品以及规格信息
		 */
		List<Product> prolist= productManager.list(goodsid);
		
		String sql ="select * from es_product_store where goodsid=?";
		List<Map> storeList = this.daoSupport.queryForList(sql, goodsid);
		
		List<Map> pList = new ArrayList<Map>();
		
		for(Product product:prolist){
			Map pro = new HashMap();
			
			pro.put("specList",product.getSpecList());
			pro.put("sn", product.getSn());
			pro.put("name",product.getName());
			pro.put("product_id", product.getProduct_id());
			pro.put("storeid", 0);
			pro.put("store", 0);
			for(Map store:storeList){
				
				//找到此仓库、此货品
				if(1 == ((Integer)store.get("depotid")).intValue() 
					&&  product.getProduct_id().intValue()== ((Integer)store.get("productid")).intValue() 
						
				){
					pro.put("storeid",(Integer)store.get("storeid"));
					pro.put("store", (Integer)store.get("store") );
					
				} 
				
			}
			
			pList.add(pro);
		}
		
		return pList;
	}
	public IProductManager getProductManager() {
		return productManager;
	}


	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}
	
}
