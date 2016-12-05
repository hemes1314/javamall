package com.enation.app.b2b2c.core.service.member;

import com.enation.app.b2b2c.core.model.member.StoreMember;

/**
 * 多用户版会员管理类
 * @author LiFenLong
 *
 */
public interface IStoreMemberManager {
	public static final String CURRENT_STORE_MEMBER_KEY="curr_store_member";
	/**
	 * 修改会员信息
	 * @param member
	 */
	public void edit(StoreMember member);
	
	/**
	 * 获取店铺会员
	 * @param member_id
	 * @return StoreMember
	 */
	public StoreMember getMember(long member_id);
	/**
	 * 获取店铺会员
	 * @param member_name
	 * @return
	 */
	public StoreMember getMember(String member_name);
	
	/**
	 * 获取当前登录的会员
	 * @author LiFenLong
	 * @return StoreMember
	 */
	public StoreMember getStoreMember();
	
	
}
