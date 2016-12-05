package com.enation.app.b2b2ccostdown.core.tag;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2ccostdown.core.model.StoreCostDown;
import com.enation.app.b2b2ccostdown.core.service.StoreCostDownManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 店铺直降
 * 
 * @author Jeffrey
 */
@Component
public class StoreCostDownTag extends BaseFreeMarkerTag {

    @Autowired
    private StoreCostDownManager storeCostDownManager;

    @Override
    protected Object exec(Map params) throws TemplateModelException {
        if(params.get("gbid") != null) {
            StoreCostDown scd = storeCostDownManager.get((Integer) params.get("gbid"));
            return scd;
        }
        Integer goodsid = (Integer) params.get("goodsid");
        Integer act_id = (Integer) params.get("act_id");
        StoreCostDown scd = storeCostDownManager.getBuyGoodsId(goodsid, act_id, 0);
        return scd == null ? "" : scd;
    }

}
