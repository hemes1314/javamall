package com.enation.app.b2b2c.core.service.store;

import java.io.File;
import java.util.List;

import com.enation.app.b2b2c.core.model.store.StoreSilde;

/**
 * 订单幻灯片管理类
 * @author LiFenLong
 *
 */
public interface IStoreSildeManager {
	/**
	 * 获取店铺幻灯片列表
	 * @param store_id
	 * @return
	 */
	public List<StoreSilde> list(Integer store_id);
	/**
	 * 修改店铺幻灯片
	 * @param fsImg
	 * @param silde_id
	 * @param silde_url
	 */
	public void edit(Integer[] silde_id,String[] fsImg,String[] silde_url);
}
