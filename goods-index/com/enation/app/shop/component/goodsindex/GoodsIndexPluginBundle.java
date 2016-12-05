/**
 * 
 */
package com.enation.app.shop.component.goodsindex;

import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.plugin.goods.IGoodsBeforeAddEvent;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;

/**
 * 商品索引插件桩
 * @author kingapex
 *2015-5-18
 */
@Component
public class GoodsIndexPluginBundle extends AutoRegisterPluginsBundle  {

	
	
	public void onIndex(Map goods,Document doc){
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IGoodsIndexEvent) {
					if (loger.isDebugEnabled()) {
						loger.debug("调用插件 : " + plugin.getClass() + " onIndex 开始...");
					}
					IGoodsIndexEvent event = (IGoodsIndexEvent) plugin;
					event.onIndex(goods,doc);
					if (loger.isDebugEnabled()) {
						loger.debug("调用插件 : " + plugin.getClass() + " onIndex 结束.");
					}
				} else {
					if (loger.isDebugEnabled()) {
						loger.debug(" no,skip...");
					}
				}
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.enation.framework.plugin.AutoRegisterPluginsBundle#getName()
	 */
	@Override
	public String getName() {
	 
		return "商品全文索引插件桩";
	}
	
	
	

}
