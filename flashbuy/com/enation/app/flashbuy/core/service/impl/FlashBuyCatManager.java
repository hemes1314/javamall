package com.enation.app.flashbuy.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.flashbuy.core.model.FlashBuyCat;
import com.enation.app.flashbuy.core.service.IFlashBuyCatManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 限时抢购分类管理
 * @author kingapex
 *2015-1-3下午2:34:33
 */
@Component
public class FlashBuyCatManager implements IFlashBuyCatManager {

	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyCatManager#list(int, int)
	 */
	@Override
	public Page list(int pageNo, int pageSize) {
		String sql =" select * from es_flashbuy_cat order by cat_order ";
		return this.daoSupport.queryForPage(sql,pageNo, pageSize,FlashBuyCat.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyCatManager#listAll()
	 */
	@Override
	public List<FlashBuyCat> listAll() {
		String sql =" select * from es_flashbuy_cat order by cat_order ";
		return this.daoSupport.queryForList(sql,FlashBuyCat.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyCatManager#get(int)
	 */
	@Override
	public FlashBuyCat get(int catid) {
		 
		return (FlashBuyCat)this.daoSupport.queryForObject("select * from es_flashbuy_cat where catid=?",  FlashBuyCat.class,catid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyCatManager#add(com.enation.app.shop.component.flashbuy.model.FlashBuyCat)
	 */
	@Override
	public void add(FlashBuyCat flashBuyCat) {
		this.daoSupport.insert("es_flashbuy_cat", flashBuyCat);

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyCatManager#update(com.enation.app.shop.component.flashbuy.model.FlashBuyCat)
	 */
	@Override
	public void update(FlashBuyCat flashBuyCat) {
		this.daoSupport.update("es_flashbuy_cat", flashBuyCat,"catid="+flashBuyCat.getCatid());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyCatManager#delete(java.lang.Integer[])
	 */
	@Override
	public void delete(Integer[] catid) {
		if(catid==null || catid.length==0) return;
		String id_str = StringUtil.arrayToString(catid, ",");
		this.daoSupport.execute("delete from es_flashbuy_cat where catid in ("+id_str+")");
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
}
