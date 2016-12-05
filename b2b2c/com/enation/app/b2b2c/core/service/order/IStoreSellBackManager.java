package com.enation.app.b2b2c.core.service.order;

import java.util.Map;

import com.enation.framework.database.Page;
/**
 * 店铺退货申请
 * @author fenlongli
 *
 */
public interface IStoreSellBackManager {
	/**
	 * 分页显示退货单列表
	 * @param status
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page list(int page,int pageSize,Integer status,Integer store_id,Map map);

}
