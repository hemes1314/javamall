package com.enation.app.shop.component.product.plugin.goods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.GoodsLvPrice;
import com.enation.app.shop.core.plugin.goods.AbstractGoodsPlugin;
import com.enation.app.shop.core.plugin.goods.IGoodsTabShowEvent;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberPriceManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/***
 * 标准的商品价格插件
 * @author kingapex
 *
 */
@Component
public class StandardGoodsPricePlugin extends AbstractGoodsPlugin implements IGoodsTabShowEvent{

	private IMemberLvManager memberLvManager;
	private IMemberPriceManager memberPriceManager;
	
	public String getAddHtml(HttpServletRequest request) {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
 
		freeMarkerPaser.setPageName("goods_price_input");
		return freeMarkerPaser.proessPageContent();
	}

	public String getEditHtml(Map goods, HttpServletRequest request) {
		
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
	 
		List lvList  = this.memberLvManager.list();
		freeMarkerPaser.putData("lvList", lvList);
 
		freeMarkerPaser.setPageName("goods_price_input");
		return freeMarkerPaser.proessPageContent();
	}
	
	public void onBeforeGoodsAdd(Map goods, HttpServletRequest request) {
		HttpServletRequest  httpRequest = ThreadContextHolder.getHttpRequest();
		goods.put("cost", httpRequest.getParameter("cost") );
		goods.put("price", httpRequest.getParameter("price") );
		goods.put("weight", httpRequest.getParameter("weight") );
		goods.put("store", httpRequest.getParameter("store") );
		
		if(StringUtil.isEmpty((String)goods.get("cost")) ){ goods.put("cost", 0);}
		if(StringUtil.isEmpty((String)goods.get("price"))){ goods.put("price", 0);}
		if(StringUtil.isEmpty((String)goods.get("weight"))){ goods.put("weight", 0);}
		if(StringUtil.isEmpty((String)goods.get("store"))){ goods.put("store", 0);}
	}
	
	public void onAfterGoodsAdd(Map goods, HttpServletRequest request)
			throws RuntimeException {
	
	}
	
	public void onAfterGoodsEdit(Map goods, HttpServletRequest request) {

	}
	
	public void onBeforeGoodsEdit(Map goods, HttpServletRequest request) {
		HttpServletRequest  httpRequest = ThreadContextHolder.getHttpRequest();
		goods.put("cost", httpRequest.getParameter("cost") );
		goods.put("price", httpRequest.getParameter("price") );
		goods.put("weight", httpRequest.getParameter("weight") );
		/* 库存编辑框已经取消，不再需要此语句了，否则会在编辑时造成库存丢失 add by liuzy
		 * goods.put("store", httpRequest.getParameter("store") );
		 */
		if(StringUtil.isEmpty((String)goods.get("cost")) ){ goods.put("cost", 0);}
		if(StringUtil.isEmpty((String)goods.get("price"))){ goods.put("price", 0);}
		if(StringUtil.isEmpty((String)goods.get("weight"))){ goods.put("weight", 0);}
		/* 库存编辑框已经取消，不再需要此语句了，否则会在编辑时造成库存丢失 add by liuzy
		 * if(StringUtil.isEmpty((String)goods.get("store"))){ goods.put("store", 0);}		
		 */
	}

	public String getAuthor() {
		return "kingapex";
	}
	
	public String getId() {
		return "goods_price";
	}
	
	public String getName() {
		return "标准商品价格插件";
	}
	
	public String getType() {
		return "goods";
	}
	
	public String getVersion() {
		return "1.0";
	}
	
	public void perform(Object... params) {

	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	public IMemberPriceManager getMemberPriceManager() {
		return memberPriceManager;
	}

	public void setMemberPriceManager(IMemberPriceManager memberPriceManager) {
		this.memberPriceManager = memberPriceManager;
	}

	@Override
	public String getTabName() {
		return "价格";
	}

	@Override
	public int getOrder() {
		return 5;
	}

}
