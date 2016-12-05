package com.enation.app.b2b2c.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import oracle.net.aso.i;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.DataAppAdv;
import com.enation.app.b2b2c.core.service.IDataAppAdvManager;
import com.enation.app.shop.mobile.action.appadv.AdvDTO;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 
 * @author Jeffrey
 *
 */
@Component
public class DataAppAdvManager extends BaseSupport implements IDataAppAdvManager {

	
    @Override
    public Page get(Integer pageNo, Integer pageSize, String order) {
        return this.daoSupport.queryForPage("select * from es_app_adv order by "+ order +" desc", pageNo, pageSize, DataAppAdv.class);
    }

    @Override
    public List<AdvDTO> getAll() {
        return this.baseDaoSupport.queryForList("select * from es_app_adv", AdvDTO.class);
    }

    @Override
    public DataAppAdv get(Integer id) {
        String sql ="select * from es_app_adv where aid = ?";
        Object o = this.daoSupport.queryForObject(sql, DataAppAdv.class, id);
        if (null != o)
            return  (DataAppAdv) o;
        return null;
    }

    @Override
    public void save(DataAppAdv adv) {
        this.baseDaoSupport.insert("es_app_adv", adv);
    }
    
    @Override
    public void update(DataAppAdv adv) {
        this.baseDaoSupport.update("es_app_adv", adv, " aid = " + adv.getAid());
    }

    @Override
    public void delete(Integer[] ids) {
        if (ids == null || ids.equals(""))
            return;
        String idStr = StringUtil.arrayToString(ids, ",");
        String sql = "delete from es_app_adv where aid in (" + idStr + ")";
        this.baseDaoSupport.execute(sql);
    }

}
