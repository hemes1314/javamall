package com.enation.app.shop.core.plugin.goods;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.plugin.search.IGoodsDataFilter;
import com.enation.app.shop.core.plugin.search.IGoodsDataFilter;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;

public class GoodsDataFilterBundle extends AutoRegisterPluginsBundle {

	public String getName() {
		 
		return "商品数据过滤插件桩";
	}
	
	
	public List getPluginList(){
		
		return this.getPlugins();
	}
 

	public void filterGoodsData(List<Map> goodsList){
		List<IGoodsDataFilter > filterList  = getPluginList();
		
		if(filterList== null ) return ;
		
		for(Map goods:goodsList){
			for(IGoodsDataFilter filter:filterList){
					((IGoodsDataFilter)filter).filter(goodsList);
			}
		}
	}
	 
}
