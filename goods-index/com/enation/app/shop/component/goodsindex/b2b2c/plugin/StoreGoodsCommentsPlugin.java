/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：商品评论插件
 *  修改人：Sylow
 *  修改时间：2015-11-05
 *  修改内容：制定初版
 */
package com.enation.app.shop.component.goodsindex.b2b2c.plugin;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.component.plugin.goods.IStoreGoodsCommentsAddEvent;
import com.enation.app.b2b2c.core.model.member.StoreMemberComment;
import com.enation.app.shop.component.goodsindex.service.IGoodsIndexManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.framework.plugin.AutoRegisterPlugin;


/**
 * 商品评论插件
 * @author Sylow
 * @version v1.0,2015-11-05
 * @since v5.2
 */
@Component
public class StoreGoodsCommentsPlugin extends AutoRegisterPlugin implements IStoreGoodsCommentsAddEvent {

	private IGoodsManager goodsManager;
	private IGoodsIndexManager goodsIndexManager;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.goods.IGoodsCommentsAddEvent#beforeAdd(com.enation.app.shop.core.model.Comments)
	 */
	@Override
	public void beforeAdd(StoreMemberComment storeMemberComment) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.goods.IGoodsCommentsAddEvent#afterAdd(com.enation.app.shop.core.model.Comments)
	 */
	@Override
	public void afterAdd(StoreMemberComment storeMemberComment) {
		// TODO Auto-generated method stub
		int goodsId = storeMemberComment.getGoods_id();
		Map<String, Object> goods = this.goodsManager.get(goodsId);
		this.goodsIndexManager.updateIndex(goods);
		
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

	public IGoodsIndexManager getGoodsIndexManager() {
		return goodsIndexManager;
	}

	public void setGoodsIndexManager(IGoodsIndexManager goodsIndexManager) {
		this.goodsIndexManager = goodsIndexManager;
	}

}
