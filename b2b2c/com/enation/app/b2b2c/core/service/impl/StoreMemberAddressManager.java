package com.enation.app.b2b2c.core.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.service.IStoreMemberAddressManager;
import com.enation.eop.sdk.database.BaseSupport;


@Component
public class StoreMemberAddressManager extends BaseSupport implements IStoreMemberAddressManager {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateMemberAddress(Long memberid,Integer addr_id) {
		this.baseDaoSupport.execute("update es_member_address set def_addr=0 where member_id=?", memberid);
		this.baseDaoSupport.execute("update es_member_address set def_addr=1 where addr_id=?", addr_id);
	}

	@Override
    public Integer getRegionid(Long member_id) {
        String sql = "select region_id from es_member_address where member_id=? and def_addr=1";
        List<Map> result=this.daoSupport.queryForList(sql, member_id);
        if(result.size()==0){
            return 0;
        }else{
            return (Integer)result.get(0).get("region_id");
        }
    }
}
