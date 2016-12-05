package com.enation.app.b2b2c.core.service;

/**
 * 会员地址
 * @author xulipeng
 *2015年1月11日13:36:19
 */
public interface IStoreMemberAddressManager {

	/**
	 * 设置会员默认收货地址
	 * @param memberid
	 * @param addr_id
	 */
	void updateMemberAddress(Long memberid,Integer addr_id);
	
	/**
	 * 获取默认地址
	 * @param addrid
	 * @return
	 */
	Integer getRegionid(Long member_id);
}
