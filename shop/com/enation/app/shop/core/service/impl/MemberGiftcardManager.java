package com.enation.app.shop.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.AdvanceLogs;
import com.enation.app.shop.core.model.MemberGiftcard;
import com.enation.app.shop.core.service.IMemberGiftcardManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.sms.SmsSender;
import com.enation.framework.util.DateUtil;

/**
 * 礼品卡查询
 * @author humaodong
 *
 */
@SuppressWarnings("rawtypes")
@Component
public class MemberGiftcardManager extends BaseSupport implements IMemberGiftcardManager {
   
    @Autowired
    IMemberManager memberManager;
    
	@Override
	public Page getGiftcardList(int pageNo,int pageSize,long memberid) {

        String sql = "select * from es_member_giftcard where member_id="+memberid+" and card_pw is not null order by card_id DESC";
		Page webPage = this.daoSupport.queryForPage(sql, pageNo, pageSize);
		return webPage;
	}
	
	@Override
    public MemberGiftcard get(Integer cardId) {
	    String sql = "select * from es_member_giftcard where card_id=?";
        MemberGiftcard giftcard = (MemberGiftcard)this.daoSupport.queryForObject(sql, MemberGiftcard.class, cardId);
        return giftcard;
    }
	
    @Override
    public MemberGiftcard get(String card_sn) {
        String sql = "select * from es_member_giftcard where card_sn=?";
        MemberGiftcard giftcard = (MemberGiftcard)this.daoSupport.queryForObject(sql, MemberGiftcard.class, card_sn);
        return giftcard;
    }

    @Override
    public void create(MemberGiftcard card) {
        this.baseDaoSupport.insert("es_member_giftcard", card);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String topup(String card_sn, String card_pw, long member_id) {
        MemberGiftcard giftcard = this.get(card_sn);
        if (giftcard == null) return "无效的礼品卡序列号";
        if (giftcard.getUsed() == 1) return "对不起，该礼品卡已被充值。";
        if (!card_pw.equals(giftcard.getCard_pw())) return "礼品卡验证不成功。";
        this.daoSupport.execute("update es_member_giftcard set used=1,used_time=?,used_member_id=? where card_sn=? and card_pw=?", DateUtil.getDateline(), member_id, card_sn, card_pw);
        
        AdvanceLogs log = memberManager.topup(member_id, 0.0d, (double)giftcard.getMoney(), card_sn, "礼品卡充值");
        if (log != null) {    
            //发送短信
            Member member = memberManager.get(member_id);
            String usedMemberName = member.getName();
            String mobile = member.getMobile();
            if (mobile != null && mobile.length() > 0) {
                String content = "您已使用礼品卡成功充值"+giftcard.getMoney()+"元，充值后 虚拟余额为："+member.getVirtual()+"元";
                try {
                    SmsSender.sendSms(mobile, content);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            
            if (member_id != giftcard.getMember_id()) {
                member = memberManager.get(giftcard.getMember_id());
                mobile = member.getMobile();
                if (mobile != null && mobile.length() > 0) {
                    String content = "您在国美酒业账户中的礼品卡"+giftcard.getCard_sn()+"刚刚被会员"+usedMemberName+"使用和充值，如有疑问请立即致电客服人员";
                    try {
                        SmsSender.sendSms(mobile, content);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "ok";
    }
	
    @Override
    public void updateCardPwd(int cardId, String pwd) {
        String sql = "update es_member_giftcard set card_pw=? where card_id=?";
        this.daoSupport.execute(sql, pwd, cardId);
    }

    
}
