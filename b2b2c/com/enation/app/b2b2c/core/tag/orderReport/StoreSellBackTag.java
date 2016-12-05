package com.enation.app.b2b2c.core.tag.orderReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.SellBackList;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 退货申请标签
 * @author fenlongli
 *
 */
@Component
public class StoreSellBackTag extends BaseFreeMarkerTag {
	private ISellBackManager sellBackManager;
	private IOrderManager orderManager;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map map = new HashMap();
		Integer id = NumberUtils.toInt(params.get("id").toString());
		SellBackList sellBackList = this.sellBackManager.get(id);
		Order orderInfo = orderManager.get(sellBackList.getOrdersn());
		List goodsList = this.sellBackManager.getGoodsList(id,sellBackList.getOrdersn());
		List return_child_list = new ArrayList();
		
		for (int i = 0; i < goodsList.size(); i++) {
			Map mapTemp = (Map) goodsList.get(i);
			if(mapTemp.get("is_pack")==null){
				mapTemp.put("is_pack", "0");
			}
			int isPack = NumberUtils.toInt(mapTemp.get("is_pack").toString());
			if(isPack == 1){
				List list = this.sellBackManager.getSellbackChilds(orderInfo.getOrder_id(), NumberUtils.toInt(mapTemp.get("goodsId").toString()));
				if (list != null) {
					return_child_list.addAll(list);
				}
			}
		}

		map.put("sellBack", sellBackList);  //退货详细
		map.put("orderInfo",orderInfo);//订单详细
		map.put("goodsList",goodsList);//退货商品列表
		map.put("childGoodsList", return_child_list);//子商品详情列表
		return map;
	}
	public ISellBackManager getSellBackManager() {
		return sellBackManager;
	}
	public void setSellBackManager(ISellBackManager sellBackManager) {
		this.sellBackManager = sellBackManager;
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
}
