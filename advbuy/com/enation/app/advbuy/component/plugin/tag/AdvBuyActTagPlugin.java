package com.enation.app.advbuy.component.plugin.tag;

import org.springframework.stereotype.Component;

import com.enation.app.advbuy.component.plugin.act.IAdvBuyActAddEvent;
import com.enation.app.advbuy.component.plugin.act.IAdvBuyActDeleteEvent;
import com.enation.app.advbuy.core.model.AdvBuyActive;
import com.enation.app.advbuy.core.model.AdvBuyTag;
import com.enation.app.advbuy.core.service.IAdvBuyActiveManager;
import com.enation.app.advbuy.core.service.IAdvGoodsTagManager;
import com.enation.app.shop.core.model.Tag;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
/**
 * 
 * @ClassName: AdvBuyActTagPlugin 
 * @Description: 预售活动标签 插件 
 * @author TALON 
 * @date 2015-7-31 上午10:44:27 
 *
 */
@Component
public class AdvBuyActTagPlugin extends AutoRegisterPlugin implements IAdvBuyActAddEvent,IAdvBuyActDeleteEvent{
	private IDaoSupport daoSupport;
	private IAdvGoodsTagManager advGoodsTagManager;
	private IAdvBuyActiveManager advBuyActiveManager;
	/**
	 * 当预售活动开启添加预售商品标签
	 */
	@Override
	public void onAddAdvBuyAct(AdvBuyActive advBuyActive) {
		Tag tag=new Tag();
		AdvBuyTag storeTag=new AdvBuyTag();
		storeTag.setIs_advbuy(advBuyActive.getAct_id());
		storeTag.setTag_name(advBuyActive.getAct_name()+"  热门预售");
		
		advGoodsTagManager.add(storeTag);
		//将标签id传递给预售活动
		this.daoSupport.execute("update es_advbuy_active set act_tag_id=? where act_id=?", this.daoSupport.getLastId("es_advbuy_active"),advBuyActive.getAct_id());
		
	}
	/**
	 * 当删除预售活动删除预售商品标签
	 */
	@Override
	public void onDeleteAdvBuyAct(Integer act_id) {
		AdvBuyActive advBuyActive= advBuyActiveManager.get(act_id);
		this.daoSupport.execute("delete from es_tag_rel where tag_id=(select tag_id from es_tags where is_advbuy=?)",act_id);
        this.daoSupport.execute("delete from es_tags where is_advbuy=?",act_id);
	}
	public IAdvBuyActiveManager getAdvBuyActiveManager() {
		return advBuyActiveManager;
	}
	public void setAdvBuyActiveManager(
			IAdvBuyActiveManager advBuyActiveManager) {
		this.advBuyActiveManager = advBuyActiveManager;
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IAdvGoodsTagManager getAdvGoodsTagManager() {
		return advGoodsTagManager;
	}
	public void setAdvGoodsTagManager(IAdvGoodsTagManager advGoodsTagManager) {
		this.advGoodsTagManager = advGoodsTagManager;
	}
	
}
