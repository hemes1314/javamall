package com.enation.app.b2b2c.core.service.impl;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.MemberCollect;
import com.enation.app.b2b2c.core.service.IStoreCollectManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;

/**
 * 收藏店铺	manager
 * @author xulipeng
 *
 */
@SuppressWarnings("rawtypes")
@Component
public class StoreCollectManager extends BaseSupport implements IStoreCollectManager {

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.IStoreCollectManager#addCollect(com.enation.app.b2b2c.core.model.MemberCollect)
	 */
	@Override
	public void addCollect(MemberCollect collect) {
		Integer num = this.baseDaoSupport.queryForInt("select count(0) from es_member_collect where member_id=? and store_id=?", collect.getMember_id(),collect.getStore_id());
		if(num!=0){
			throw new RuntimeException("店铺已收藏！");
		}else{
			collect.setCreate_time(DateUtil.getDateline());
			this.baseDaoSupport.insert("es_member_collect", collect);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.IStoreCollectManager#delCollect(java.lang.Integer)
	 */
	@Override
	public void delCollect(Integer collect_id) {
		this.baseDaoSupport.execute("delete from es_member_collect where id=?",collect_id);
	}

	/*
	 * modify by linyang
	 * 修改bug 改左连接为内连接，解决出现两个相同收藏记录的bug
	 * modify by lin yang 2015 10 28 增加 as celloct_id 解决删除问题
	 */
	@Override
	public Page getList(long memberid,int page,int pageSize) {
		String sql = "select s.STORE_NAME,s.STORE_ID,s.STORE_LOGO,s.STORE_TYPE,s.STORE_PROVINCEID,s.STORE_CITYID,s.STORE_REGIONID,s.ATTR,s.ZIP,s.TEL,s.COMMUNITY,s.LONGITUDE,m.id,m.create_time from es_store s , es_member_collect m where s.store_id=m.store_id  and m.member_id = ?";
		//Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize, memberid);
		Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize, memberid);
		return webpage;
	}
	
	@Override
	public boolean isCollect(long memberId, int storeId){
		return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM es_member_collect WHERE store_id=? AND member_id=?", storeId,memberId) > 0;
	} 

}
