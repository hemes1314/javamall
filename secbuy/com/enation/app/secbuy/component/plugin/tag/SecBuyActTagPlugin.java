package com.enation.app.secbuy.component.plugin.tag;

import org.springframework.stereotype.Component;

import com.enation.app.secbuy.component.plugin.act.ISecBuyActAddEvent;
import com.enation.app.secbuy.component.plugin.act.ISecBuyActDeleteEvent;
import com.enation.app.secbuy.core.model.SecBuyActive;
import com.enation.app.secbuy.core.model.SecBuyTag;
import com.enation.app.secbuy.core.service.ISecBuyActiveManager;
import com.enation.app.secbuy.core.service.ISecGoodsTagManager;
import com.enation.app.shop.core.model.Tag;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
/**
 * 
 * @ClassName: SecBuyActTagPlugin 
 * @Description: 秒拍活动标签 插件 
 * @author TALON 
 * @date 2015-7-31 上午10:44:27 
 *
 */
@Component
public class SecBuyActTagPlugin extends AutoRegisterPlugin implements ISecBuyActAddEvent,ISecBuyActDeleteEvent{
	private IDaoSupport daoSupport;
	private ISecGoodsTagManager secGoodsTagManager;
	private ISecBuyActiveManager secBuyActiveManager;
	/**
	 * 当秒拍活动开启添加秒拍商品标签
	 */
	@Override
	public void onAddSecBuyAct(SecBuyActive secBuyActive) {
		Tag tag=new Tag();
		SecBuyTag storeTag=new SecBuyTag();
		storeTag.setIs_secbuy(secBuyActive.getAct_id());
		storeTag.setTag_name(secBuyActive.getAct_name()+"  热门秒拍");
		
		secGoodsTagManager.add(storeTag);
		//将标签id传递给秒拍活动
		this.daoSupport.execute("update es_secbuy_active set act_tag_id=? where act_id=?", this.daoSupport.getLastId("es_secbuy_active"),secBuyActive.getAct_id());
		
	}
	/**
	 * 当删除秒拍活动删除秒拍商品标签
	 */
	@Override
	public void onDeleteSecBuyAct(Integer act_id) {
		SecBuyActive secBuyActive= secBuyActiveManager.get(act_id);
		this.daoSupport.execute("delete from es_tag_rel where tag_id=(select tag_id from es_tags where is_secbuy=?)",act_id);
        this.daoSupport.execute("delete from es_tags where is_secbuy=?",act_id);
	}
	public ISecBuyActiveManager getSecBuyActiveManager() {
		return secBuyActiveManager;
	}
	public void setSecBuyActiveManager(
			ISecBuyActiveManager secBuyActiveManager) {
		this.secBuyActiveManager = secBuyActiveManager;
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public ISecGoodsTagManager getSecGoodsTagManager() {
		return secGoodsTagManager;
	}
	public void setSecGoodsTagManager(ISecGoodsTagManager secGoodsTagManager) {
		this.secGoodsTagManager = secGoodsTagManager;
	}
	
}
