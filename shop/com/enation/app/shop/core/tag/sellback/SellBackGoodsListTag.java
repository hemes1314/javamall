package com.enation.app.shop.core.tag.sellback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 退货申请商品列表
 * @author fenlongli
 *
 */
@Component
public class SellBackGoodsListTag extends BaseFreeMarkerTag {
	private ISellBackManager sellBackManager;
	private IOrderManager orderManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map<String,Object> map = new HashMap<String, Object>();
		Integer recid = NumberUtils.toInt(params.get("id").toString());
		String sn=params.get("sn").toString();
		List list = sellBackManager.getGoodsList(recid, sn);
		Order orderInfo =orderInfo = orderManager.get(sn);
		
		List return_child_list = new ArrayList();
		
		for (int i = 0; i < list.size(); i++) {
			Map mapTemp = (Map) list.get(i);
			int isPack = NumberUtils.toInt(mapTemp.get("is_pack").toString());
			if(isPack == 1){
				List listTemp = this.sellBackManager.getSellbackChilds(orderInfo.getOrder_id(), NumberUtils.toInt(mapTemp.get("goodsId").toString()));
				if (list != null) {
					return_child_list.addAll(listTemp);
				}
			}
			
		}
		map.put("goodsList", list);
		map.put("childGoodsList", return_child_list);
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
