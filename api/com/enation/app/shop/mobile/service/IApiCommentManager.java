package com.enation.app.shop.mobile.service;

import org.springframework.stereotype.Component;

/**
 * 评论Manager接口
 * @author Sylow
 * @version v1.0 , 2015-08-24
 * @since v1.0
 */
@Component
public interface IApiCommentManager  {

	
	 /**
     * 获取商品评论数
     * @param goods_id
     * @return
     */
	public int getCommentsCount(int goods_id, int grade);

	
	 /**
     * 获取某个评分以上的评论数
     * @param goods_id
     * @param grade
     * @return
     */
	public int getCommentsCount(int goods_id);
	
}
