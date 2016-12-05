package com.enation.app.b2b2c.core.service.member;

import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.member.StoreMemberComment;
import com.enation.framework.database.Page;
/**
 * 店铺商品评论咨询管理类
 * @author LiFenLong
 *
 */
public interface IStoreMemberCommentManager {
	/**
	 * 获取店铺评论或咨询列表
	 * @param page
	 * @param pageSize
	 * @param map
	 * @param store_id
	 * @return
	 */
	public Page getAllComments(int page, int pageSize, Map map,Integer store_id);
	/**
	 * 获取店铺评论、咨询实体
	 * @param comment_id 
	 * @return
	 */
	public Map get(Integer comment_id);
	/**
	 * 更改店铺评论、咨询
	 * @param map
	 * @param comment_id
	 */
	public void edit(Map map,Integer comment_id);
	
	/**
	 * 添加评论
	 * @param memberComment
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(StoreMemberComment memberComment);
	/**
	 * 获取评论/咨询条数
	 * @param type 类型 1:评论。2咨询
	 */
	public Integer getCommentCount(Integer type,Integer store_id);
	/**
	 * 获取商品的描述度平均值
	 * @param goods_id
	 * @return
	 */
	public Double getGoodsStore_desccredit(Integer goods_id);
}
