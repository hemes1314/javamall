/**
 * 
 */
package com.enation.app.shop.component.goodsindex.b2b2c.plugin;

import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.goodsindex.IGoodsIndexEvent;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * b2b2c商品索引插件
 * @author kingapex
 *2015-5-18
 */
@Component
public class B2b2cGoodsIndexPlugin extends AutoRegisterPlugin implements IGoodsIndexEvent {

	
	/**
	 * 响应索引事件，将店铺id和店铺名称写进索引
	 */
	@Override
	public void onIndex(Map goods, Document doc) {
		Integer storeid = StringUtil.toInt( goods.get("store_id").toString(),0);
	    Field storeid_field  = new StringField("store_id", ""+storeid,Field.Store.YES);
	    doc.add(storeid_field);
	    String storename = (String) goods.get("store_name");
	    if(!StringUtil.isEmpty(storename)){
	    	Field storename_field =  new StringField("store_name", storename,Field.Store.YES);
	    	doc.add(storename_field);
	    	
	    }

	}

}
