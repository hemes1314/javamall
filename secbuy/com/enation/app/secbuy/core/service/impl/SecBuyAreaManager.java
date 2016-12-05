package com.enation.app.secbuy.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.secbuy.core.model.SecBuyArea;
import com.enation.app.secbuy.core.service.ISecBuyAreaManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 秒拍地区管理
 * @author kingapex
 *2015-1-3下午2:34:33
 */
@Component
public class SecBuyAreaManager implements ISecBuyAreaManager {

	private IDaoSupport daoSupport;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyAreaManager#list(int, int)
	 */
	@Override
	public Page list(int pageNo, int pageSize) {
		String sql =" select * from es_secbuy_area order by area_order ";
		return this.daoSupport.queryForPage(sql,pageNo, pageSize,SecBuyArea.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyAreaManager#listAll()
	 */
	@Override
	public List<SecBuyArea> listAll() {
		String sql =" select * from es_secbuy_area order by area_order ";
		return this.daoSupport.queryForList(sql,SecBuyArea.class);
	}

	
	@Override
	public SecBuyArea get(int area_id) {
		 
		return (SecBuyArea)this.daoSupport.queryForObject("select * from es_secbuy_area where area_id=?",  SecBuyArea.class,area_id);
	}


	@Override
	public void add(SecBuyArea secBuyArea) {
		this.daoSupport.insert("es_secbuy_area", secBuyArea);

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyAreaManager#update(com.enation.app.shop.component.secbuy.model.SecBuyArea)
	 */
	@Override
	public void update(SecBuyArea secBuyArea) {
		this.daoSupport.update("es_secbuy_area", secBuyArea,"area_id="+secBuyArea.getArea_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyAreaManager#delete(java.lang.Integer[])
	 */
	@Override
	public void delete(Integer[] area_id) {
		if(area_id==null || area_id.length==0) return;
		String id_str = StringUtil.arrayToString(area_id, ",");
		this.daoSupport.execute("delete from es_secbuy_area where area_id in ("+id_str+")");
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}


}
