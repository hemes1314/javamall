package com.enation.app.shop.component.goodscore.plugin.goodsbase;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.plugin.goods.AbstractGoodsPlugin;
import com.enation.app.shop.core.plugin.goods.IGoodsTabShowEvent;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.util.StringUtil;

/**
 * 商品基本信息插件
 * @author kingapex
 *
 */
@Component
public class GoodsBasePlugin extends AbstractGoodsPlugin implements IGoodsTabShowEvent {
	private IGoodsCatManager goodsCatManager;
	
	
	@Override
	public String getAddHtml(HttpServletRequest request) {
		
		//由request中读取所属分类id
		int catid = StringUtil.toInt(request.getParameter("catid"),true);
		
		//找到所有的父，以便在页面显示
		List<Cat> parentList=this.goodsCatManager.getParents(catid);
		
		//找到当前的父，以便确定商品类型id
		Cat currentCat = parentList.get(parentList.size()-1);
		
		
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setPageName("goods_add");
		freeMarkerPaser.putData("typeid",currentCat.getType_id());
		freeMarkerPaser.putData("catid",catid);
		
		freeMarkerPaser.putData("optype",request.getParameter("optype")); //操作类型
		freeMarkerPaser.putData("parentList",parentList);
		
		
		return freeMarkerPaser.proessPageContent();
		
	}
	
	
	
	@Override
	public String getEditHtml(Map goods, HttpServletRequest request) {
		
		int catid= StringUtil.toInt(goods.get("cat_id").toString(),true);
		
		//找到所有的父，以便在页面显示
		List<Cat> parentList=this.goodsCatManager.getParents(catid);
		
		//找到当前的父，以便确定商品类型id
		Cat currentCat = parentList.get(parentList.size()-1);
		
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("goodsView",goods);
		freeMarkerPaser.putData("parentList",parentList);
		freeMarkerPaser.putData("typeid",currentCat.getType_id());
		freeMarkerPaser.putData("catid",catid);
		freeMarkerPaser.putData("optype",request.getParameter("optype"));
		freeMarkerPaser.setPageName("goods_edit");
		
		return freeMarkerPaser.proessPageContent();
		
	}
    
	
	
	@Override
	public void onBeforeGoodsAdd(Map goods, HttpServletRequest request) {

	}



	@Override
	public void onAfterGoodsAdd(Map goods, HttpServletRequest request)
			throws RuntimeException {
		 

	}

	@Override
	public void onAfterGoodsEdit(Map goods, HttpServletRequest request) {
		 

	}

	@Override
	public void onBeforeGoodsEdit(Map goods, HttpServletRequest request) {
		 

	}

  

	@Override
	public String getTabName() {
		return "基本信息";
	}


	@Override
	public int getOrder() {
		return 1;
	}


	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}


	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

	
}
