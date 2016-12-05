package com.enation.app.b2b2c.core.service.goods;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface IStoreGoodsSpecManager {
	/**
	 * 查看商品库存Html
	 * @param goods
	 * @return
	 */
	public String getStoreHtml(Integer goodsid);
	
	
	@Transactional(propagation = Propagation.REQUIRED)  
	public List<Map> listGoodsStore(int goodsid);
}
