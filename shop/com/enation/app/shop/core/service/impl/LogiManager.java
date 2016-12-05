package com.enation.app.shop.core.service.impl;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.Logi;
import com.enation.app.shop.core.service.ILogiManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
 
/**
 * 物流公司管理类
 * @author LiFenLong 2014-4-2;4.0改版修改delete方法参数为integer[]
 *
 */
public class LogiManager extends BaseSupport<Logi> implements ILogiManager{
	
	public void delete(Integer[] logi_id) {
		
		String id =StringUtil.implode(",", logi_id);
		if(id==null || id.equals("")){return ;}
		String sql = "delete from logi_company where id in (" + id + ")";
		this.baseDaoSupport.execute(sql);
	}

	
	public Logi getLogiById(Integer id) {
		String sql  = "select * from logi_company where id=?";
		Logi a =  this.baseDaoSupport.queryForObject(sql, Logi.class, id);
		return a;
	}

	
	public Page pageLogi(String order, Integer page, Integer pageSize) {
		order = order == null ? " id desc" : order;
		String sql = "select * from logi_company";
		sql += " order by  " + order;
		Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}

	
	public void saveAdd(Logi logi) {
		this.baseDaoSupport.insert("logi_company", logi);
	}

	
	public void saveEdit(Logi logi ) {
		this.baseDaoSupport.update("logi_company", logi, "id="+logi.getId());
	}
	
	@SuppressWarnings("rawtypes")
	public List<Map> list() {
		return this.baseDaoSupport.queryForList("select * from logi_company");
	}

	
}
