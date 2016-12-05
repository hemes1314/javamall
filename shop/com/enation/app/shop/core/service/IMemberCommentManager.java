package com.enation.app.shop.core.service;

import java.util.Map;

import com.enation.app.shop.core.model.MemberComment;
import com.enation.framework.database.Page;

@SuppressWarnings("rawtypes")
public interface IMemberCommentManager {

	/**
	 * 添加一条评论
	 * @param memberComment
	 */
	public void add(MemberComment memberComment);
	
	/**
	 * 获取一个商品的评论列表
	 * @param goods_id
	 * @param page
	 * @param pageSize
	 * @param type
	 * @return
	 */
	public Page getGoodsComments(int goods_id, int page, int pageSize, int type);
	
	/**
	 * 获取一个商品的总得分
	 * @param goods_id
	 * @return
	 */
	public int getGoodsGrade(int goods_id);
	
	/**
	 * 在后台显示所有的评论列表
	 * @param page
	 * @param pageSize
	 * @param type
	 * @return
	 */
	public Page getAllComments(int page, int pageSize, int type);
	
	/**
	 * 查看所有带状态的评论或问答
	 * @param page
	 * @param pageSize
	 * @param type
	 * @param status
	 * @return
	 */
	public Page getCommentsByStatus(int page, int pageSize, int type,int status);
	
	/**
	 * 根据ID获取评论对象
	 * @param comment_id
	 * @return
	 */
	public MemberComment get(int comment_id);
	
	/**
	 * 更新一个评论对象
	 * @param memberComment
	 */
	public void update(MemberComment memberComment);
	
	/**
	 * 获取某一个商品的审核通过的评论数
	 * @return
	 */
	public int getGoodsCommentsCount(int goods_id);
	
	/**
	 * 删除评论
	 * @param comment_id
	 */
	public void delete(Integer[] comment_id);
	
	/**
	 * 删除评论、咨询
	 * @param comment_id 评论、咨询 Id
	 */
	public void deletealone(int comment_id);
	/**
	 * 取一个会员的评论列表
	 * @param page
	 * @param pageSize
	 * @param type
	 * @param member_id
	 * @return
	 */
	public Page getMemberComments(int page, int pageSize, int type,long member_id);
	
	/**
	 * 取一个会员的评论数
	 * @param member_id
	 * @param type
	 * @return
	 */
	public int getMemberCommentTotal(long member_id, int type);
	/**
	 * 评论分数列表
	 * @param goodsid 商品Id
	 * @return Map
	 */
	public Map statistics(int goodsid);

	/**
	 * 获取会员对商品的评论
	 * @param page
	 * @param pageSize
	 * @param member_id
	 * @return
	 */
	public Page getCommentsForGoods(int page, int pageSize, long member_id,int type);


	/**
	 * 根据商品id获取评论总数
	 * @param goods_id
	 * @return
	 */
	public int getCommentsCount(int goods_id);

	public int getCommentsCount(int goods_id, int grade);

    public Page getGoodsComments(Integer goods_id, int pageNo, int pageSize, Integer type, Integer grade);


	String syncOpenAPI();
}
