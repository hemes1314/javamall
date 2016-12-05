package com.enation.app.b2b2ccostdown.component.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2ccostdown.core.model.CostDownActive;
import com.enation.app.b2b2ccostdown.core.service.CostDownActiveManager;
import com.enation.app.flashbuy.component.plugin.act.IFlashBuyActAddEvent;
import com.enation.app.flashbuy.component.plugin.act.IFlashBuyActDeleteEvent;
import com.enation.app.flashbuy.core.model.FlashBuyActive;
import com.enation.app.flashbuy.core.model.FlashBuyTag;
import com.enation.app.flashbuy.core.service.IFlashBuyActiveManager;
import com.enation.app.flashbuy.core.service.IFlashGoodsTagManager;
import com.enation.app.shop.core.model.Tag;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;

@Component
public class CostDownActTagPlugin extends AutoRegisterPlugin implements IFlashBuyActAddEvent, IFlashBuyActDeleteEvent {

    @Autowired
    private IDaoSupport<?> daoSupport;

    @Autowired
    private CostDownActiveManager costDownActiveManager;

    /**
     * 当活动开启添加商品标签
     */
    @Override
    public void onAddFlashBuyAct(FlashBuyActive flashBuyActive) {
    }

    /**
     * 当删除限时抢购活动删除限时抢购商品标签
     */
    @Override
    public void onDeleteFlashBuyAct(Integer act_id) {
    }

}
