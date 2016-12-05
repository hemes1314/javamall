package com.enation.app.b2b2c.core.pluin.goods;

import java.util.Map;

/**
 * 店铺商品前台tab显示事件
 * @author fenlongli
 * @date 2015-6-10 上午10:00:15
 */
public interface IStoreGoodsTabShowEvent {

	/**
	 * 
	 * @return Map 三个参数(name="tab的标题",url="tab的页面",show_type="展示状态=0为都显示，1为添加时显示，2为修改时显示。")
	 */
	public Map getTabInfo();
}
