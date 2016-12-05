package com.enation.app.advbuy.core.service;


import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.advbuy.core.model.AdvBuy;
import com.enation.framework.database.Page;
/**
 * 
 * @ClassName: IAdvBuyManager 
 * @Description: 预售商品管理类 
 * @author TALON 
 * @date 2015-7-31 上午1:51:19 
 *
 */
public interface IAdvBuyManager {
	/**
	 * 创建预售
	 * @param advBuy
	 * @return 创建成功返回创建的预售id
	 *         创建失败返回0
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public int add(AdvBuy advBuy);
	/**
	 * 修改预售信息
	 * @param advBuy
	 * @return 创建成功返回创建的预售id
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(AdvBuy advBuy);
	
	/**
	 * 删除预售
	 * @param gbid 预售id
	 */
	public void delete(int gbid);
	
	public void deleteByGoodsId(int goodsId);

	/**
	 * 审核
	 * @param gbid 预售id
	 * @param status 状态
	 */
	public void auth(int gbid,int status);
	

	/**
	 * 查询某活动下的预售
	 * @param page 页码
	 * @param pageSize 分页每页显示数量
	 * @param actid 活动id
	 * @param status 状态
	 * @return 预售分页列表
	 */
	public Page listByActId(int page ,int pageSize,int actid,Integer status);
	
	/**
	 * 前台显示预售
	 * @param page 页数
	 * @param pageSize 每页显示数量
	 * @param catid 分类Id
	 * @param minprice 最小金额
	 * @param maxprice 最大金额
	 * @param sort_key 排序类型
	 * @param sort_type 正序还是倒叙
	 * @param search_type 搜索类型
	 * @param area_id 预售地区Id
	 * @return 预售分页列表
	 */
	public Page search(int page,int pageSize,Integer catid,Double minprice,Double maxprice,Integer sort_key,Integer sort_type,Integer search_type,Integer area_id);
	/**
	 * 获取某个预售信息
	 * @param gbid 预售id
	 * @return AdvBuy 预售商品
	 */
	public AdvBuy get(int gbid);
	
	/**
	 * 根据商品Id获取预售商品
	 * @param goodsId 商品ID
	 * @return AdvBuy 预售商品
	 */
	public AdvBuy getBuyGoodsId(int goodsId);
}
