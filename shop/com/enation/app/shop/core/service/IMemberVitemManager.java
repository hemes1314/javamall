package com.enation.app.shop.core.service;

import com.enation.app.shop.core.model.MemberVitem;
import com.enation.app.shop.core.model.VirtualProduct;
import com.enation.framework.database.Page;

public interface IMemberVitemManager {
	
	/**
	 * 查询会员的所有虚拟物品
	 * @param memberid
	 * @return
	 */
	public Page getList(int pageNo,int pageSize,long memberid);
	
	public MemberVitem getByTypeId(int type_id, long member_id);
	/**
	 * 转为虚拟货币
	 */
	public String topup(int type_id, int num, long member_id);
	
	public void add(VirtualProduct vp, int num, long member_id) throws Exception;
	public void sub(int type_id, int num, long member_id) throws Exception;

}
