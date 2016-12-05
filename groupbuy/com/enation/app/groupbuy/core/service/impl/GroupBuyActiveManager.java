package com.enation.app.groupbuy.core.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.groupbuy.component.plugin.GroupbuyPluginBundle;
import com.enation.app.groupbuy.core.model.GroupBuyActive;
import com.enation.app.groupbuy.core.service.IGroupBuyActiveManager;
import com.enation.app.shop.core.service.ITagManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;
@Component
public class GroupBuyActiveManager implements IGroupBuyActiveManager {
	private IDaoSupport daoSupport;
	private ITagManager tagManager;
	private GroupbuyPluginBundle groupbuyPluginBundle;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.groupbuy.service.IGroupBuyActiveManager#groupBuyActive(java.lang.Integer, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Page groupBuyActive(Integer pageNo, Integer pageSize, Map map) {
		String sql ="select * from es_groupbuy_active order by add_time desc";
		return this.daoSupport.queryForPage(sql, pageNo, pageSize,  GroupBuyActive.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.groupbuy.service.IGroupBuyActiveManager#add(com.enation.app.shop.component.groupbuy.model.GroupBuyActive)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(GroupBuyActive groupBuyActive) {

		groupBuyActive.setAdd_time(DateUtil.getDateline());
		
		//为了演示添加的代码：如果团购活动开启时间超过当前时间则开启团购.
		if(groupBuyActive.getEnd_time()<DateUtil.getDateline()){
			groupBuyActive.setAct_status(2);
		}else{
			if(groupBuyActive.getStart_time()<DateUtil.getDateline()){
				groupBuyActive.setAct_status(1);
			}else{
				groupBuyActive.setAct_status(0);
			}
		}
		this.daoSupport.insert("es_groupbuy_active", groupBuyActive);
		//判断是否开启团购活动
		groupBuyActive.setAct_id(this.daoSupport.getLastId("es_groupbuy_active"));
		this.groupbuyPluginBundle.onGroupBuyAdd(groupBuyActive);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.groupbuy.service.IGroupBuyActiveManager#update(com.enation.app.shop.component.groupbuy.model.GroupBuyActive)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(GroupBuyActive groupBuyActive) {
		this.daoSupport.update("es_groupbuy_active", groupBuyActive, "act_id="+groupBuyActive.getAct_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.groupbuy.service.IGroupBuyActiveManager#delete(java.lang.Integer[])
	 */
	@Override
	public void delete(Integer[] ids) {
		String idstr = StringUtil.arrayToString(ids, ",");
		this.daoSupport.execute("delete from es_groupbuy_active where act_id in ("+idstr+")");
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.groupbuy.service.IGroupBuyActiveManager#delete(int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(int id) {
		this.groupbuyPluginBundle.onGroupBuyEnd(id);
		this.groupbuyPluginBundle.onGroupBuyDelete(id);
		this.daoSupport.execute("delete from es_groupbuy_active where act_id=?", id);
		

        //删除标签引用
        this.daoSupport.execute("delete from es_tag_rel where tag_id in (select tag_id from es_tags where is_groupbuy=?)", id);
        
        //删除团购标签
        this.daoSupport.execute("delete from es_tags where is_groupbuy=?", id);
        
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.groupbuy.service.IGroupBuyActiveManager#get(int)
	 */
	@Override
	public GroupBuyActive get(int id) {
		return (GroupBuyActive)this.daoSupport.queryForObject("select * from es_groupbuy_active where act_id=?", GroupBuyActive.class, id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.groupbuy.service.IGroupBuyActiveManager#get()
	 */
	@Override
	public GroupBuyActive get() {
		return (GroupBuyActive)this.daoSupport.queryForObject("select * from es_groupbuy_active where end_time>? and act_status=1", GroupBuyActive.class, DateUtil.getDateline());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.groupbuy.service.IGroupBuyActiveManager#getLastEndTime()
	 */
	@Override
	public Long getLastEndTime() {
		return this.daoSupport.queryForLong("SELECT max(end_time) from es_groupbuy_active");
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.groupbuy.service.IGroupBuyActiveManager#listEnable()
	 */
	@Override
	public List<GroupBuyActive> listEnable() {
		String sql ="select * from es_groupbuy_active where end_time>=? order by add_time desc";
		long now = DateUtil.getDateline();
		return this.daoSupport.queryForList(sql, GroupBuyActive.class,now);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.groupbuy.service.IGroupBuyActiveManager#listJoinEnable()
	 */
	@Override
	public List<GroupBuyActive> listJoinEnable() {
		String sql ="select * from es_groupbuy_active where join_end_time>=? order by add_time desc";
		long now = DateUtil.getDateline();
		return this.daoSupport.queryForList(sql, GroupBuyActive.class,now);
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

	public GroupbuyPluginBundle getGroupbuyPluginBundle() {
		return groupbuyPluginBundle;
	}

	public void setGroupbuyPluginBundle(GroupbuyPluginBundle groupbuyPluginBundle) {
		this.groupbuyPluginBundle = groupbuyPluginBundle;
	}
}
