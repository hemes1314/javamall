package com.enation.app.shop.component.bonus.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.StoreBonus;
import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.gome.open.api.sdk.internal.util.StringUtil;


/**
 * 红包类型管理
 * @author kingapex
 *2013-8-13下午3:10:21
 */
@Component
public class BonusTypeManager extends BaseSupport  implements IBonusTypeManager {

	@Override
	public void add(BonusType bronusType) {
		this.baseDaoSupport.insert("bonus_type", bronusType);

	}

	@Override
	public void update(BonusType bronusType) {
		this.baseDaoSupport.update("bonus_type", bronusType," type_id="+bronusType.getType_id());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void delete(Integer[] bonusTypeId) {
		
		for(int typeid:bonusTypeId){
			this.baseDaoSupport.execute("delete from member_bonus where bonus_type_id=?", typeid);
			this.baseDaoSupport.execute("delete from bonus_type where type_id=?",typeid);
		}
	}
/*   lee.li平台后台红包列表中过滤掉优惠券    */
	@Override
	public Page list(int page, int pageSize) {
	   
		String sql =" select t.*, s.store_name from bonus_type t left join es_store s ON s.store_id=t.store_id where t.store_id is null  order by t.type_id desc";
		return this.baseDaoSupport.queryForPage(sql, page, pageSize, BonusType.class);
	}

	@Override
	public StoreBonus get(int typeid) {
		String sql ="select * from bonus_type  where type_id =?";
		return (StoreBonus) this.baseDaoSupport.queryForObject(sql, StoreBonus.class, typeid);
	}

    @Override
    public boolean isExistRecognition(String recognition) {
        String sql = "select recognition from bonus_type  where recognition =?";
        @SuppressWarnings("rawtypes")
        List result = this.baseDaoSupport.queryForList(sql, recognition);
        if(null != result && result.size() > 0) { 
            return true; 
        }
        return false;
    }

}
