package com.enation.app.shop.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IWelcomeInfoManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.DoubleMapper;

public class WelcomeInfoManager extends BaseSupport implements
		IWelcomeInfoManager {

	
	public Map mapWelcomInfo() {
		Member member = UserConext.getCurrentMember();
		Map map = new HashMap();
		int oNum = this.baseDaoSupport.queryForInt("SELECT count(*) as oNum  FROM order where pay_status=0 and member_id=? and status != 4", member.getMember_id());
		map.put("oNum", oNum);
		int totalOrder = this.baseDaoSupport.queryForInt( "SELECT count(*) as totalOrder  FROM order where member_id=? and disabled=0 ", member.getMember_id() );
		map.put("totalOrder", totalOrder);
		int mNum = this.baseDaoSupport.queryForInt( "SELECT count(*) as mNum FROM message where to_id=? and unread='0' and to_type=0 and disabled='false'", member.getMember_id() );
		map.put("mNum", mNum);
		int pNum = this.baseDaoSupport.queryForInt( "SELECT sum(point) as pNum FROM member where member_id=?", member.getMember_id() );
		map.put("pNum", pNum);
		Object oaNum = this.baseDaoSupport.queryForObject( "SELECT advance FROM member where member_id=?",new DoubleMapper(), member.getMember_id() );
		Double aNum = oaNum == null ? 0L : (Double)oaNum;
		map.put("aNum", aNum);
		
		
		int couponNum = this.baseDaoSupport.queryForInt( "SELECT count(*) as couponNum FROM member_coupon where memberid=?", member.getMember_id() );
		map.put("couponNum", couponNum);
		
		
		Long curTime = (new Date()).getTime();

		
		String sql="SELECT sum(discount_price) FROM  member_coupon WHERE used=1 and memberid = ?";
		Double couponprice= (Double)this.baseDaoSupport.queryForObject(sql, new DoubleMapper(),member.getMember_id());
		map.put("couponprice", couponprice);
		
		
		int commentRNum = this.baseDaoSupport.queryForInt( "SELECT count(*) as commentRNum FROM comments where author_id=? and display='true'", member.getMember_id() );

		map.put("commentRNum", commentRNum);
		List pa = this.baseDaoSupport.queryForList( "SELECT name FROM promotion_activity where enable='1' and end_time>=? and begin_time<=?", Object.class, curTime, curTime );
		pa = pa == null ? new ArrayList() : pa;
		map.put("pa", pa);
		return map;
	}

}
