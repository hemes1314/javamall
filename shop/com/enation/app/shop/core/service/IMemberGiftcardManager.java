package com.enation.app.shop.core.service;

import com.enation.app.shop.core.model.MemberGiftcard;
import com.enation.framework.database.Page;

public interface IMemberGiftcardManager {
	
	/**
	 * 查询会员的所有礼品卡
	 * @param memberid
	 * @return
	 */
	public Page getGiftcardList(int pageNo,int pageSize,long memberid);
	
	public MemberGiftcard get(Integer cardId);
	public MemberGiftcard get(String card_sn);
	
	/**
	 * 充值
	 */
	public String topup(String card_sn, String card_pw, long member_id);

    public void create(MemberGiftcard card);

    public void updateCardPwd(int cardId, String pwd);

}
