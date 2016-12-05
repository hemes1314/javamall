package com.enation.app.b2b2c.core.service;

import java.util.List;

import com.enation.app.b2b2c.core.model.DataAppAdv;
import com.enation.app.shop.mobile.action.appadv.AdvDTO;
import com.enation.framework.database.Page;

/**
 * 
 * @author Jeffrey
 *
 */
public interface IDataAppAdvManager {

    public Page get(Integer pageNo, Integer pageSize, String order);
    
    public List<AdvDTO> getAll();

    public DataAppAdv get(Integer id);
    
    public void save(DataAppAdv adv);
    
    public void update(DataAppAdv adv);

    public void delete(Integer[] ids);

}
