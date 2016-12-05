package com.enation.app.flashbuy.core.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.flashbuy.component.plugin.FlashbuyPluginBundle;
import com.enation.app.flashbuy.core.model.FlashBuyActive;
import com.enation.app.flashbuy.core.service.IFlashBuyActiveManager;
import com.enation.app.shop.core.service.ITagManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;
@Component
public class FlashBuyActiveManager implements IFlashBuyActiveManager {
	private IDaoSupport daoSupport;
	private ITagManager tagManager;
	private FlashbuyPluginBundle flashbuyPluginBundle;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyActiveManager#flashBuyActive(java.lang.Integer, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Page flashBuyActive(Integer pageNo, Integer pageSize, Map map) {
		String sql ="select * from es_flashbuy_active order by add_time desc";
		return this.daoSupport.queryForPage(sql, pageNo, pageSize,  FlashBuyActive.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyActiveManager#add(com.enation.app.shop.component.flashbuy.model.FlashBuyActive)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(FlashBuyActive flashBuyActive) {

		flashBuyActive.setAdd_time(DateUtil.getDateline());
		
		//为了演示添加的代码：如果限时抢购活动开启时间超过当前时间则开启限时抢购.
		if(flashBuyActive.getEnd_time()<DateUtil.getDateline()){
			flashBuyActive.setAct_status(2);
		}else{
			if(flashBuyActive.getStart_time()<DateUtil.getDateline()){
				flashBuyActive.setAct_status(1);
			}else{
				flashBuyActive.setAct_status(0);
			}
		}
		this.daoSupport.insert("es_flashbuy_active", flashBuyActive);
		//判断是否开启限时抢购活动
		flashBuyActive.setAct_id(this.daoSupport.getLastId("es_flashbuy_active"));
		this.flashbuyPluginBundle.onFlashBuyAdd(flashBuyActive);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyActiveManager#update(com.enation.app.shop.component.flashbuy.model.FlashBuyActive)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(FlashBuyActive flashBuyActive) {
		this.daoSupport.update("es_flashbuy_active", flashBuyActive, "act_id="+flashBuyActive.getAct_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyActiveManager#delete(java.lang.Integer[])
	 */
	@Override
	public void delete(Integer[] ids) {
		String idstr = StringUtil.arrayToString(ids, ",");
		this.daoSupport.execute("delete from es_flashbuy_active where act_id in ("+idstr+")");
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyActiveManager#delete(int)
	 */
	@Override
	public void delete(int id) {
		this.flashbuyPluginBundle.onFlashBuyEnd(id);
		this.flashbuyPluginBundle.onFlashBuyDelete(id);
		this.daoSupport.execute("delete from es_flashbuy_active where act_id=?", id);
		

        //删除标签引用
        this.daoSupport.execute("delete from es_tag_rel where tag_id=(select tag_id from es_tags where is_flashbuy=?)", id);
        
        //删除抢购标签
        this.daoSupport.execute("delete from es_tags where is_flashbuy=?", id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyActiveManager#get(int)
	 */
	@Override
	public FlashBuyActive get(int id) {
		return (FlashBuyActive)this.daoSupport.queryForObject("select * from es_flashbuy_active where act_id=?", FlashBuyActive.class, id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyActiveManager#get()
	 */
	@Override
	public FlashBuyActive get() {
		return (FlashBuyActive)this.daoSupport.queryForObject("select * from es_flashbuy_active where end_time>? and act_status=1", FlashBuyActive.class, DateUtil.getDateline());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyActiveManager#getLastEndTime()
	 */
	@Override
	public Long getLastEndTime() {
		return this.daoSupport.queryForLong("SELECT max(end_time) from es_flashbuy_active");
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyActiveManager#listEnable()
	 */
	@Override
	public List<FlashBuyActive> listEnable() {
		String sql ="select * from es_flashbuy_active where end_time>=? order by add_time desc";
		long now = DateUtil.getDateline();
		return this.daoSupport.queryForList(sql, FlashBuyActive.class,now);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.flashbuy.service.IFlashBuyActiveManager#listJoinEnable()
	 */
	@Override
	public List<FlashBuyActive> listJoinEnable() {
		String sql ="select * from es_flashbuy_active where join_end_time>=? order by add_time desc";
		long now = DateUtil.getDateline();
		return this.daoSupport.queryForList(sql, FlashBuyActive.class,now);
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

	public FlashbuyPluginBundle getFlashbuyPluginBundle() {
		return flashbuyPluginBundle;
	}

	public void setFlashbuyPluginBundle(FlashbuyPluginBundle flashbuyPluginBundle) {
		this.flashbuyPluginBundle = flashbuyPluginBundle;
	}
}
