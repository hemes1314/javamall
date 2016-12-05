package com.enation.app.groupbuy.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.groupbuy.core.model.GroupBuyArea;
import com.enation.app.groupbuy.core.service.IGroupBuyAreaManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 团购地区管理
 * @author kingapex
 *2015-1-3下午2:34:33
 */
@Component
public class GroupBuyAreaManager implements IGroupBuyAreaManager {

	private IDaoSupport daoSupport;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.groupbuy.service.IGroupBuyAreaManager#list(int, int)
	 */
	@Override
	public Page list(int pageNo, int pageSize) {
		String sql =" select * from es_groupbuy_area order by area_order ";
		return this.daoSupport.queryForPage(sql,pageNo, pageSize,GroupBuyArea.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.groupbuy.service.IGroupBuyAreaManager#listAll()
	 */
	@Override
	public List<GroupBuyArea> listAll() {
		String sql =" select * from es_groupbuy_area order by area_order ";
		return this.daoSupport.queryForList(sql,GroupBuyArea.class);
	}

	
	@Override
	public GroupBuyArea get(int area_id) {
		 
		return (GroupBuyArea)this.daoSupport.queryForObject("select * from es_groupbuy_area where area_id=?",  GroupBuyArea.class,area_id);
	}


	@Override
	public void add(GroupBuyArea groupBuyArea) {
		this.daoSupport.insert("es_groupbuy_area", groupBuyArea);

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.groupbuy.service.IGroupBuyAreaManager#update(com.enation.app.shop.component.groupbuy.model.GroupBuyArea)
	 */
	@Override
	public void update(GroupBuyArea groupBuyArea) {
		this.daoSupport.update("es_groupbuy_area", groupBuyArea,"area_id="+groupBuyArea.getArea_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.groupbuy.service.IGroupBuyAreaManager#delete(java.lang.Integer[])
	 */
	@Override
	public void delete(Integer[] area_id) {
		if(area_id==null || area_id.length==0) return;
		String id_str = StringUtil.arrayToString(area_id, ",");
		this.daoSupport.execute("delete from es_groupbuy_area where area_id in ("+id_str+")");
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}


}
