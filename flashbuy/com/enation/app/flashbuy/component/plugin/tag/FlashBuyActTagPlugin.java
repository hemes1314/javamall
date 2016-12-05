package com.enation.app.flashbuy.component.plugin.tag;

import org.springframework.stereotype.Component;

import com.enation.app.flashbuy.component.plugin.act.IFlashBuyActAddEvent;
import com.enation.app.flashbuy.component.plugin.act.IFlashBuyActDeleteEvent;
import com.enation.app.flashbuy.core.model.FlashBuyActive;
import com.enation.app.flashbuy.core.model.FlashBuyTag;
import com.enation.app.flashbuy.core.service.IFlashBuyActiveManager;
import com.enation.app.flashbuy.core.service.IFlashGoodsTagManager;
import com.enation.app.shop.core.model.Tag;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
/**
 * 
 * @ClassName: FlashBuyActTagPlugin 
 * @Description: 限时抢购活动标签 插件 
 * @author TALON 
 * @date 2015-7-31 上午10:44:27 
 *
 */
@Component
public class FlashBuyActTagPlugin extends AutoRegisterPlugin implements IFlashBuyActAddEvent,IFlashBuyActDeleteEvent{
	private IDaoSupport daoSupport;
	private IFlashGoodsTagManager flashGoodsTagManager;
	private IFlashBuyActiveManager flashBuyActiveManager;
	/**
	 * 当活动开启添加商品标签
	 */
	@Override
	public void onAddFlashBuyAct(FlashBuyActive flashBuyActive) {
		Tag tag=new Tag();
		FlashBuyTag storeTag=new FlashBuyTag();
		storeTag.setIs_flashbuy(flashBuyActive.getAct_id());
		storeTag.setTag_name(flashBuyActive.getAct_name()+"  热门抢购");
		
		flashGoodsTagManager.add(storeTag);
		//将标签id传递给活动
		this.daoSupport.execute("update es_flashbuy_active set act_tag_id=? where act_id=?", this.daoSupport.getLastId("es_tags"),flashBuyActive.getAct_id());
		
	}
	/**
	 * 当删除限时抢购活动删除限时抢购商品标签
	 */
	@Override
	public void onDeleteFlashBuyAct(Integer act_id) {
		FlashBuyActive flashBuyActive= flashBuyActiveManager.get(act_id);
		this.daoSupport.execute("delete from es_tag_rel where tag_id=(select tag_id from es_tags where is_flashbuy=?)",act_id);
        this.daoSupport.execute("delete from es_tags where is_flashbuy=?",act_id);
	}
	public IFlashBuyActiveManager getFlashBuyActiveManager() {
		return flashBuyActiveManager;
	}
	public void setFlashBuyActiveManager(
			IFlashBuyActiveManager flashBuyActiveManager) {
		this.flashBuyActiveManager = flashBuyActiveManager;
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IFlashGoodsTagManager getFlashGoodsTagManager() {
		return flashGoodsTagManager;
	}
	public void setFlashGoodsTagManager(IFlashGoodsTagManager flashGoodsTagManager) {
		this.flashGoodsTagManager = flashGoodsTagManager;
	}
	
}
