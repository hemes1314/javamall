package com.enation.app.shop.core.plugin.goods;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.framework.plugin.IPlugin;

/**
 * 获取商品添加html事件
 * @author kingapex
 *
 */
public  interface IGetGoodsAddHtmlEvent {
	 
	 public String getAddHtml(HttpServletRequest request);
	
	
} 
