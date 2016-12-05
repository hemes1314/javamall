package com.enation.app.advbuy.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.advbuy.core.model.AdvBuyCat;
import com.enation.app.advbuy.core.service.IAdvBuyCatManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 预售分类管理
 * @author kingapex
 *2015-1-3下午2:34:33
 */
@Component
public class AdvBuyCatManager implements IAdvBuyCatManager {

	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyCatManager#list(int, int)
	 */
	@Override
	public Page list(int pageNo, int pageSize) {
		String sql =" select * from es_advbuy_cat order by cat_order ";
		return this.daoSupport.queryForPage(sql,pageNo, pageSize,AdvBuyCat.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyCatManager#listAll()
	 */
	@Override
	public List<AdvBuyCat> listAll() {
		String sql =" select * from es_advbuy_cat order by cat_order ";
		return this.daoSupport.queryForList(sql,AdvBuyCat.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyCatManager#get(int)
	 */
	@Override
	public AdvBuyCat get(int catid) {
		 
		return (AdvBuyCat)this.daoSupport.queryForObject("select * from es_advbuy_cat where catid=?",  AdvBuyCat.class,catid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyCatManager#add(com.enation.app.shop.component.advbuy.model.AdvBuyCat)
	 */
	@Override
	public void add(AdvBuyCat advBuyCat) {
		this.daoSupport.insert("es_advbuy_cat", advBuyCat);

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyCatManager#update(com.enation.app.shop.component.advbuy.model.AdvBuyCat)
	 */
	@Override
	public void update(AdvBuyCat advBuyCat) {
		this.daoSupport.update("es_advbuy_cat", advBuyCat,"catid="+advBuyCat.getCatid());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyCatManager#delete(java.lang.Integer[])
	 */
	@Override
	public void delete(Integer[] catid) {
		if(catid==null || catid.length==0) return;
		String id_str = StringUtil.arrayToString(catid, ",");
		this.daoSupport.execute("delete from es_advbuy_cat where catid in ("+id_str+")");
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
}
