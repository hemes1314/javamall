package com.enation.app.b2b2ccostdown.core.tag;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.advbuy.core.model.AdvBuy;
import com.enation.app.advbuy.core.service.impl.AdvBuyManager;
import com.enation.app.b2b2ccostdown.core.model.CostDown;
import com.enation.app.b2b2ccostdown.core.service.CostDownManager;
import com.enation.app.groupbuy.core.model.GroupBuy;
import com.enation.app.groupbuy.core.service.impl.GroupBuyManager;
import com.enation.app.utils.PriceUtils;
import com.enation.app.utils.PriceUtils.IActivity;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 获得最低价格的活动
 * 
 * @author Jeffrey
 *
 */
@Component
public class MinimumPriceActivityTag extends BaseFreeMarkerTag {

    @Autowired
    private CostDownManager costDownManager;

    @Autowired
    private GroupBuyManager groupBuyManager;

    @Autowired
    private AdvBuyManager advBuyManager;

    @Override
    protected Object exec(Map params) throws TemplateModelException {
        if (null == params.get("goodsId"))
            return "";
        Integer goodsId = (Integer) params.get("goodsId");
        GroupBuy gb = null;
        CostDown cd = null;
        AdvBuy ab = null;
        if (null != params.get("isGroupbuy") && (Integer) params.get("isGroupbuy") == 1)
            gb = groupBuyManager.getBuyGoodsId(goodsId);
        if (null != params.get("isCostDown") && (Integer) params.get("isCostDown") == 1)
            cd = costDownManager.getBuyGoodsId(goodsId);
        if (null != params.get("isAdvbuy") && (Integer) params.get("isAdvbuy") == 1)
            ab = advBuyManager.getBuyGoodsId(goodsId);
        IActivity a = PriceUtils.getMinimumPriceActivity(gb, cd, ab);
        if (null == a)
            return "";
        return a;
    }
}
