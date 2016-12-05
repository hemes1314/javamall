/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：商品新增评论事件
 *  修改人：Sylow
 *  修改时间：2015-11-05
 *  修改内容：制定初版
 */
package com.enation.app.b2b2c.component.plugin.goods;

import com.enation.app.b2b2c.core.model.member.StoreMemberComment;

/**
 * 商品新增评论事件
 * @author Sylow
 * @version v1.0,2015-11-05
 * @since v5.2
 */
public interface IStoreGoodsCommentsAddEvent  {
	
	/**
	 * 增加前事件
	 * @param memberComment 评论对象
	 */
	public void beforeAdd(StoreMemberComment storeMemberComment);
	
	
	/**
	 * 增加之后事件
	 * @param memberComment 评论对象
	 */
	public void afterAdd(StoreMemberComment storeMemberComment);
	
}
