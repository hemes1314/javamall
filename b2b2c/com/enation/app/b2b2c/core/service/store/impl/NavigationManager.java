package com.enation.app.b2b2c.core.service.store.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.Navigation;
import com.enation.app.b2b2c.core.service.store.INavigationManager;
import com.enation.eop.sdk.database.BaseSupport;


@Component
public class NavigationManager extends BaseSupport implements INavigationManager {

	@Override
	public List getNavicationList(Integer storeid) {
		String sql ="select * from es_navigation where store_id="+storeid;
		List list = this.baseDaoSupport.queryForList(sql);
		return list;
	}

	@Override
	public void save(Navigation navigation) {
		this.baseDaoSupport.insert("es_navigation", navigation);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void edit(Navigation navigation) {
		this.baseDaoSupport.update("es_navigation", navigation, " id="+navigation.getId()+" and store_id="+navigation.getStore_id());
	}

	@Override
	public void delete(Integer id) {
		this.baseDaoSupport.execute("delete from es_navigation where id="+id);
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Navigation getNavication(Integer id) {
		String sql = "select * from es_navigation where id="+id;
		List<Navigation> list = this.baseDaoSupport.queryForList(sql,Navigation.class);
		Navigation navigation = (Navigation) list.get(0);
		return navigation;
	}

}
