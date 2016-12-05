package com.enation.app.advbuy.core.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.advbuy.component.plugin.AdvbuyPluginBundle;
import com.enation.app.advbuy.core.model.AdvBuyActive;
import com.enation.app.advbuy.core.service.IAdvBuyActiveManager;
import com.enation.app.shop.core.service.ITagManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;
@Component
public class AdvBuyActiveManager implements IAdvBuyActiveManager {
	private IDaoSupport daoSupport;
	private ITagManager tagManager;
	private AdvbuyPluginBundle advbuyPluginBundle;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyActiveManager#advBuyActive(java.lang.Integer, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Page advBuyActive(Integer pageNo, Integer pageSize, Map map) {
		String sql ="select * from es_advbuy_active order by add_time desc";
		return this.daoSupport.queryForPage(sql, pageNo, pageSize,  AdvBuyActive.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyActiveManager#add(com.enation.app.shop.component.advbuy.model.AdvBuyActive)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(AdvBuyActive advBuyActive) {

		advBuyActive.setAdd_time(DateUtil.getDateline());
		
		//为了演示添加的代码：如果预售活动开启时间超过当前时间则开启预售.
		if(advBuyActive.getEnd_time()<DateUtil.getDateline()){
			advBuyActive.setAct_status(2);
		}else{
			if(advBuyActive.getStart_time()<DateUtil.getDateline()){
				advBuyActive.setAct_status(1);
			}else{
				advBuyActive.setAct_status(0);
			}
		}
		this.daoSupport.insert("es_advbuy_active", advBuyActive);
		//判断是否开启预售活动
		advBuyActive.setAct_id(this.daoSupport.getLastId("es_advbuy_active"));
		this.advbuyPluginBundle.onAdvBuyAdd(advBuyActive);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyActiveManager#update(com.enation.app.shop.component.advbuy.model.AdvBuyActive)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(AdvBuyActive advBuyActive) {
		this.daoSupport.update("es_advbuy_active", advBuyActive, "act_id="+advBuyActive.getAct_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyActiveManager#delete(java.lang.Integer[])
	 */
	@Override
	public void delete(Integer[] ids) {
		String idstr = StringUtil.arrayToString(ids, ",");
		this.daoSupport.execute("delete from es_advbuy_active where act_id in ("+idstr+")");
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyActiveManager#delete(int)
	 */
	@Override
	public void delete(int id) {
		this.advbuyPluginBundle.onAdvBuyEnd(id);
		this.advbuyPluginBundle.onAdvBuyDelete(id);
		this.daoSupport.execute("delete from es_advbuy_active where act_id=?", id);

        //删除标签引用
        this.daoSupport.execute("delete from es_tag_rel where tag_id=(select tag_id from es_tags where is_advbuy=?)", id);
        
        //删除预售标签
        this.daoSupport.execute("delete from es_tags where is_advbuy=?", id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyActiveManager#get(int)
	 */
	@Override
	public AdvBuyActive get(int id) {
		return (AdvBuyActive)this.daoSupport.queryForObject("select * from es_advbuy_active where act_id=?", AdvBuyActive.class, id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyActiveManager#get()
	 */
	@Override
	public AdvBuyActive get() {
		return (AdvBuyActive)this.daoSupport.queryForObject("select * from es_advbuy_active where end_time>? and act_status=1", AdvBuyActive.class, DateUtil.getDateline());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyActiveManager#getLastEndTime()
	 */
	@Override
	public Long getLastEndTime() {
		return this.daoSupport.queryForLong("SELECT max(end_time) from es_advbuy_active");
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyActiveManager#listEnable()
	 */
	@Override
	public List<AdvBuyActive> listEnable() {
		String sql ="select * from es_advbuy_active where end_time>=? order by add_time desc";
		long now = DateUtil.getDateline();
		return this.daoSupport.queryForList(sql, AdvBuyActive.class,now);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyActiveManager#listJoinEnable()
	 */
	@Override
	public List<AdvBuyActive> listJoinEnable() {
		String sql ="select * from es_advbuy_active where join_end_time>=? order by add_time desc";
		long now = DateUtil.getDateline();
		return this.daoSupport.queryForList(sql, AdvBuyActive.class,now);
	}
	
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	public ITagManager getTagManager() {
		return tagManager;
	}

	public void setTagManager(ITagManager tagManager) {
		this.tagManager = tagManager;
	}

	public AdvbuyPluginBundle getAdvbuyPluginBundle() {
		return advbuyPluginBundle;
	}

	public void setAdvbuyPluginBundle(AdvbuyPluginBundle advbuyPluginBundle) {
		this.advbuyPluginBundle = advbuyPluginBundle;
	}
}
