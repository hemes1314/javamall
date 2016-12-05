package com.enation.app.b2b2c.core.service;

import com.enation.app.b2b2c.core.model.MemberCollect;
import com.enation.framework.database.Page;

/**
 * 店铺收藏接口
 * @author xulipeng
 *	2015年1月12日14:16:43
 */

public interface IStoreCollectManager {
	
	/**
	 * 添加店铺
	 * @param collect
	 */
	public void addCollect(MemberCollect collect);
	
	/**
	 * 删除收藏的店铺
	 * @param collect_id
	 */
	public void delCollect(Integer collect_id);
	
	/**
	 * 获取收藏店铺的集合
	 * @param memberid
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page getList(long memberid,int page,int pageSize);

	/**
	 * 是否已经收藏
	 * @param memberId 会员id
	 * @param storeId 店铺id
	 * @return
	 */
	public boolean isCollect(long memberId, int storeId);
}
