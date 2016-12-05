package com.enation.app.secbuy.core.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.secbuy.component.plugin.SecbuyPluginBundle;
import com.enation.app.secbuy.core.model.SecBuyActive;
import com.enation.app.secbuy.core.service.ISecBuyActiveManager;
import com.enation.app.shop.core.service.ITagManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;
@Component
public class SecBuyActiveManager implements ISecBuyActiveManager {
	private IDaoSupport daoSupport;
	private ITagManager tagManager;
	private SecbuyPluginBundle secbuyPluginBundle;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyActiveManager#secBuyActive(java.lang.Integer, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Page secBuyActive(Integer pageNo, Integer pageSize, Map map) {
		String sql ="select * from es_secbuy_active order by add_time desc";
		return this.daoSupport.queryForPage(sql, pageNo, pageSize,  SecBuyActive.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyActiveManager#add(com.enation.app.shop.component.secbuy.model.SecBuyActive)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(SecBuyActive secBuyActive) {

		secBuyActive.setAdd_time(DateUtil.getDateline());
		
		//为了演示添加的代码：如果秒拍活动开启时间超过当前时间则开启秒拍.
		if(secBuyActive.getEnd_time()<DateUtil.getDateline()){
			secBuyActive.setAct_status(2);
		}else{
			if(secBuyActive.getStart_time()<DateUtil.getDateline()){
				secBuyActive.setAct_status(1);
			}else{
				secBuyActive.setAct_status(0);
			}
		}
		this.daoSupport.insert("es_secbuy_active", secBuyActive);
		//判断是否开启秒拍活动
		secBuyActive.setAct_id(this.daoSupport.getLastId("es_secbuy_active"));
		this.secbuyPluginBundle.onSecBuyAdd(secBuyActive);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyActiveManager#update(com.enation.app.shop.component.secbuy.model.SecBuyActive)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(SecBuyActive secBuyActive) {
		this.daoSupport.update("es_secbuy_active", secBuyActive, "act_id="+secBuyActive.getAct_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyActiveManager#delete(java.lang.Integer[])
	 */
	@Override
	public void delete(Integer[] ids) {
		String idstr = StringUtil.arrayToString(ids, ",");
		this.daoSupport.execute("delete from es_secbuy_active where act_id in ("+idstr+")");
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyActiveManager#delete(int)
	 */
	@Override
	public void delete(int id) {
		this.secbuyPluginBundle.onSecBuyEnd(id);
		this.secbuyPluginBundle.onSecBuyDelete(id);
		this.daoSupport.execute("delete from es_secbuy_active where act_id=?", id);
		

        //删除标签引用
        this.daoSupport.execute("delete from es_tag_rel where tag_id=(select tag_id from es_tags where is_secbuy=?)", id);
        
        //删除秒拍标签
        this.daoSupport.execute("delete from es_tags where is_secbuy=?", id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyActiveManager#get(int)
	 */
	@Override
	public SecBuyActive get(int id) {
		return (SecBuyActive)this.daoSupport.queryForObject("select * from es_secbuy_active where act_id=?", SecBuyActive.class, id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyActiveManager#get()
	 */
	@Override
	public SecBuyActive get() {
		return (SecBuyActive)this.daoSupport.queryForObject("select * from es_secbuy_active where end_time>? and act_status=1", SecBuyActive.class, DateUtil.getDateline());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyActiveManager#getLastEndTime()
	 */
	@Override
	public Long getLastEndTime() {
		return this.daoSupport.queryForLong("SELECT max(end_time) from es_secbuy_active");
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyActiveManager#listEnable()
	 */
	@Override
	public List<SecBuyActive> listEnable() {
		String sql ="select * from es_secbuy_active where end_time>=? order by add_time desc";
		long now = DateUtil.getDateline();
		return this.daoSupport.queryForList(sql, SecBuyActive.class,now);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyActiveManager#listJoinEnable()
	 */
	@Override
	public List<SecBuyActive> listJoinEnable() {
		String sql ="select * from es_secbuy_active where join_end_time>=? order by add_time desc";
		long now = DateUtil.getDateline();
		return this.daoSupport.queryForList(sql, SecBuyActive.class,now);
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

	public SecbuyPluginBundle getSecbuyPluginBundle() {
		return secbuyPluginBundle;
	}

	public void setSecbuyPluginBundle(SecbuyPluginBundle secbuyPluginBundle) {
		this.secbuyPluginBundle = secbuyPluginBundle;
	}
}
