package com.enation.app.secbuy.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.secbuy.core.model.SecBuyCat;
import com.enation.app.secbuy.core.service.ISecBuyCatManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 秒拍分类管理
 * @author kingapex
 *2015-1-3下午2:34:33
 */
@Component
public class SecBuyCatManager implements ISecBuyCatManager {

	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyCatManager#list(int, int)
	 */
	@Override
	public Page list(int pageNo, int pageSize) {
		String sql =" select * from es_secbuy_cat order by cat_order ";
		return this.daoSupport.queryForPage(sql,pageNo, pageSize,SecBuyCat.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyCatManager#listAll()
	 */
	@Override
	public List<SecBuyCat> listAll() {
		String sql =" select * from es_secbuy_cat order by cat_order ";
		return this.daoSupport.queryForList(sql,SecBuyCat.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyCatManager#get(int)
	 */
	@Override
	public SecBuyCat get(int catid) {
		 
		return (SecBuyCat)this.daoSupport.queryForObject("select * from es_secbuy_cat where catid=?",  SecBuyCat.class,catid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyCatManager#add(com.enation.app.shop.component.secbuy.model.SecBuyCat)
	 */
	@Override
	public void add(SecBuyCat secBuyCat) {
		this.daoSupport.insert("es_secbuy_cat", secBuyCat);

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyCatManager#update(com.enation.app.shop.component.secbuy.model.SecBuyCat)
	 */
	@Override
	public void update(SecBuyCat secBuyCat) {
		this.daoSupport.update("es_secbuy_cat", secBuyCat,"catid="+secBuyCat.getCatid());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyCatManager#delete(java.lang.Integer[])
	 */
	@Override
	public void delete(Integer[] catid) {
		if(catid==null || catid.length==0) return;
		String id_str = StringUtil.arrayToString(catid, ",");
		this.daoSupport.execute("delete from es_secbuy_cat where catid in ("+id_str+")");
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
}
