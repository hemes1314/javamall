package com.enation.app.flashbuy.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.flashbuy.core.model.FlashBuyArea;
import com.enation.app.flashbuy.core.service.IFlashBuyAreaManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 限时抢购地区管理
 * @author kingapex
 *2015-1-3下午2:34:33
 */
@Component
public class FlashBuyAreaManager implements IFlashBuyAreaManager {

	private IDaoSupport daoSupport;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyAreaManager#list(int, int)
	 */
	@Override
	public Page list(int pageNo, int pageSize) {
		String sql =" select * from es_flashbuy_area order by area_order ";
		return this.daoSupport.queryForPage(sql,pageNo, pageSize,FlashBuyArea.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyAreaManager#listAll()
	 */
	@Override
	public List<FlashBuyArea> listAll() {
		String sql =" select * from es_flashbuy_area order by area_order ";
		return this.daoSupport.queryForList(sql,FlashBuyArea.class);
	}

	
	@Override
	public FlashBuyArea get(int area_id) {
		 
		return (FlashBuyArea)this.daoSupport.queryForObject("select * from es_flashbuy_area where area_id=?",  FlashBuyArea.class,area_id);
	}


	@Override
	public void add(FlashBuyArea flashBuyArea) {
		this.daoSupport.insert("es_flashbuy_area", flashBuyArea);

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyAreaManager#update(com.enation.app.shop.component.flashbuy.model.FlashBuyArea)
	 */
	@Override
	public void update(FlashBuyArea flashBuyArea) {
		this.daoSupport.update("es_flashbuy_area", flashBuyArea,"area_id="+flashBuyArea.getArea_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyAreaManager#delete(java.lang.Integer[])
	 */
	@Override
	public void delete(Integer[] area_id) {
		if(area_id==null || area_id.length==0) return;
		String id_str = StringUtil.arrayToString(area_id, ",");
		this.daoSupport.execute("delete from es_flashbuy_area where area_id in ("+id_str+")");
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}


}
