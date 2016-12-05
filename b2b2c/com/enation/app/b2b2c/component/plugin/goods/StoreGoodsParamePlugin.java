package com.enation.app.b2b2c.component.plugin.goods;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.enation.app.b2b2c.core.pluin.goods.IStoreGoodsTabShowEvent;
import com.enation.framework.plugin.AutoRegisterPlugin;


@SuppressWarnings({ "unchecked", "rawtypes" })
@Component
public class StoreGoodsParamePlugin extends AutoRegisterPlugin implements IStoreGoodsTabShowEvent {

	
	@Override
	public Map getTabInfo() {
		Map map = new HashMap();
		map.put("name", "商品参数");
		map.put("url", "goods/add_parame.html");
		map.put("show_type", 0);
		return map;
	}

}
