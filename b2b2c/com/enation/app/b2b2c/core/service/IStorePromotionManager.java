package com.enation.app.b2b2c.core.service;

import com.enation.app.b2b2c.core.model.StoreBonus;

/**
 * 店铺促销管理接口
 * @author xulipeng
 * 2015年1月12日23:07:19
 */
public interface IStorePromotionManager {

	/**
	 * 添加满减优惠
	 * @return
	 */
	public void add_FullSubtract(StoreBonus bonus);

	/**
     * 查看优惠券识别码是否重复
     * @param recognition 识别码
     * @return
     */
    public boolean checkRecognition(String recognition);
	
	/**
	 * 会员领取优惠卷
	 * @param memberid	会员id
	 * @param storeid	店铺id
	 * @param type_id	优惠卷id
	 */
	public void receive_bonus(Long memberid,Integer storeid,Integer type_id );
	
	/**
	 * 获取优惠券
	 * @param type_id
	 * @return
	 */
	public StoreBonus getBonus(Integer type_id);
	
	/**
	 * 获取会员领取的优惠券的数量
	 * @param type_id
	 * @return
	 */
	public int getmemberBonus(Integer type_id,long memberid);
	
	/**
	 * 修改优惠券
	 * @param bonus
	 */
	public void edit_FullSubtract(StoreBonus bonus);
	
	/**
	 * 删除优惠券
	 * @param bonus
	 */
	public void deleteBonus(Integer type_id);
}
