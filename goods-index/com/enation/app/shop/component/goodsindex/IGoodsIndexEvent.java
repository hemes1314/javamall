/**
 * 
 */
package com.enation.app.shop.component.goodsindex;

import java.util.Map;

import org.apache.lucene.document.Document;

/**
 * 商品索引事件
 * @author kingapex
 *2015-5-18
 */
public interface IGoodsIndexEvent {
	
	
	/**
	 * 商品索引事件
	 * @param goods
	 * @param doc
	 */
	public void onIndex(Map goods, Document doc);
	
	
	
	
}
