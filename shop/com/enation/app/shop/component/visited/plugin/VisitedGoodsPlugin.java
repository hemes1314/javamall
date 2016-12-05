package com.enation.app.shop.component.visited.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.plugin.goods.IGoodsVisitEvent;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.context.webcontext.WebSessionContext;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 
 * 商品被浏览后浏览数加1（同一session周期内不重复加），
 * 并保存到Session中，调整浏览顺序，最后浏览的放在最前
 * @author lzf
 * 2012-4-17上午9:58:03
 */
@Component
public class VisitedGoodsPlugin extends AutoRegisterPlugin implements
	IGoodsVisitEvent {
	
	private IGoodsManager goodsManager;

	@Override
	public void onVisit(Map goods) {
		/**
		 * 逻辑：判断当前session中的viewedGoods变量是否包含了当前goods，如未，则
		 * 向变量中加入goods,，并对相应的goods的view_count做+1处理
		 */
		WebSessionContext sessionContext = ThreadContextHolder.getSessionContext();
		List<Map> visitedGoods = (List<Map>)sessionContext.getAttribute("visitedGoods");
		Integer goods_id=Integer.valueOf(goods.get("goods_id").toString());
		boolean visited = false;
		if(visitedGoods==null) visitedGoods = new ArrayList<Map>();
		for(Map map:visitedGoods){
			if(map.get("goods_id").toString().equals(StringUtil.toString(goods_id))){//说明当前session访问过此商品
				visitedGoods.remove(map);
				visited = true;
				break;
			}
		}
		String  thumbnail =(String) goods.get("thumbnail");
		if(StringUtil.isEmpty(thumbnail)){
			String default_img_url = SystemSetting.getDefault_img_url();
			thumbnail=default_img_url;
		}else{
			thumbnail=UploadUtil.replacePath(thumbnail);
		}
		Map newmap = new HashMap();
		newmap.put("goods_id", goods_id);
		newmap.put("thumbnail", thumbnail);
		newmap.put("name", goods.get("name"));
		newmap.put("price", goods.get("price"));
		visitedGoods.add(0, newmap);
		sessionContext.setAttribute("visitedGoods", visitedGoods);
		if(!visited){
			goodsManager.incViewCount(goods_id);
		}
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

}
