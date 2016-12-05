package com.enation.app.shop.core.plugin.goods;


/**
 * 商品输入选项卡显示事件
 * @author kingapex
 *
 */
public interface IGoodsTabShowEvent {
	
	/**
	 * 返回选项卡的名称
	 * @return
	 */
	public String getTabName();
	
	
	/**
	 * 返回排序
	 * @return
	 */
	public int getOrder();
	
}
