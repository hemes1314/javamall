package com.enation.app.shop.component.giftcard.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.component.giftcard.model.GiftcardType;
import com.enation.app.shop.component.giftcard.service.IGiftcardTypeManager;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;


/**
 * 礼品卡类型管理
 * @author kingapex
 *2013-8-13下午3:10:21
 */
@Component
public class GiftcardTypeManager extends BaseSupport  implements IGiftcardTypeManager {

	@Override
	public void add(GiftcardType giftcardType) {
		this.baseDaoSupport.insert("giftcard_type", giftcardType);

	}

	@Override
	public void update(GiftcardType giftcardType) {
		this.baseDaoSupport.update("giftcard_type", giftcardType," type_id="+giftcardType.getType_id());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void delete(Integer[] giftcardTypeId) {
		
		for(int typeid:giftcardTypeId){
			//this.baseDaoSupport.execute("delete from member_giftcard where type_id=?", typeid);
			this.baseDaoSupport.execute("delete from giftcard_type where type_id=?",typeid);
		}
	}

	@Override
	public Page list(int page, int pageSize) {
		String sql ="select * from giftcard_type order by type_id desc";
		return this.baseDaoSupport.queryForPage(sql, page, pageSize, GiftcardType.class);
	}

	@Override
	public GiftcardType get(int typeid) {
		String sql ="select * from giftcard_type  where type_id =?";
		return (GiftcardType) this.baseDaoSupport.queryForObject(sql, GiftcardType.class, typeid);
	}

    @Override
    public List list() {
        String sql = "select * from giftcard_type order by money";
        return this.baseDaoSupport.queryForList(sql, GiftcardType.class);
    }

}
