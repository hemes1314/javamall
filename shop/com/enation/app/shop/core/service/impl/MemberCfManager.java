package com.enation.app.shop.core.service.impl;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberCfManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;


public class MemberCfManager extends BaseSupport implements	IMemberCfManager {

	
    public Page page(int pageNo,int pageSize, String status) {
		Member member = UserConext.getCurrentMember();
		
		String sql = "select r.message_time,c.title,g.price,r.status "
		        + "from es_cf_record r "
		        + "inner join es_cf c on c.id=r.cf_id "
		        + "inner join es_goods g on g.goods_id=r.cf_goods_id "
		        + "where r.member_id=? and c.status=? "
		        + "order by r.message_time desc";
		Page rpage = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize, member.getMember_id(), status);
		return rpage;
	}
    
    public void setStatus(String sn, String recStatus) {
        String sql = "update es_cf_record set status=? where order_id=?";
        this.daoSupport.execute(sql, recStatus, sn);
    }
}
