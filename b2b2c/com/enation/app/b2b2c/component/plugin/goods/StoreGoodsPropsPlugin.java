package com.enation.app.b2b2c.component.plugin.goods;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.pluin.goods.IStoreGoodsTabShowEvent;
import com.enation.framework.plugin.AutoRegisterPlugin;


@Component
public class StoreGoodsPropsPlugin extends AutoRegisterPlugin implements IStoreGoodsTabShowEvent {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map getTabInfo() {
		Map map = new HashMap();
		map.put("name", "商品属性");
		map.put("url", "goods/add_props.html");
		map.put("show_type", 0);
		return map;
	}

}
