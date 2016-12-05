package com.enation.app.advbuy.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.advbuy.core.model.AdvBuyArea;
import com.enation.app.advbuy.core.service.IAdvBuyAreaManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 预售地区管理
 * @author kingapex
 *2015-1-3下午2:34:33
 */
@Component
public class AdvBuyAreaManager implements IAdvBuyAreaManager {

	private IDaoSupport daoSupport;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyAreaManager#list(int, int)
	 */
	@Override
	public Page list(int pageNo, int pageSize) {
		String sql =" select * from es_advbuy_area order by area_order ";
		return this.daoSupport.queryForPage(sql,pageNo, pageSize,AdvBuyArea.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyAreaManager#listAll()
	 */
	@Override
	public List<AdvBuyArea> listAll() {
		String sql =" select * from es_advbuy_area order by area_order ";
		return this.daoSupport.queryForList(sql,AdvBuyArea.class);
	}

	
	@Override
	public AdvBuyArea get(int area_id) {
		 
		return (AdvBuyArea)this.daoSupport.queryForObject("select * from es_advbuy_area where area_id=?",  AdvBuyArea.class,area_id);
	}


	@Override
	public void add(AdvBuyArea advBuyArea) {
		this.daoSupport.insert("es_advbuy_area", advBuyArea);

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyAreaManager#update(com.enation.app.shop.component.advbuy.model.AdvBuyArea)
	 */
	@Override
	public void update(AdvBuyArea advBuyArea) {
		this.daoSupport.update("es_advbuy_area", advBuyArea,"area_id="+advBuyArea.getArea_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyAreaManager#delete(java.lang.Integer[])
	 */
	@Override
	public void delete(Integer[] area_id) {
		if(area_id==null || area_id.length==0) return;
		String id_str = StringUtil.arrayToString(area_id, ",");
		this.daoSupport.execute("delete from es_advbuy_area where area_id in ("+id_str+")");
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}


}
