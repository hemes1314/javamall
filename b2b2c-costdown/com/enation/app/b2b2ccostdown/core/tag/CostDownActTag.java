package com.enation.app.b2b2ccostdown.core.tag;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2ccostdown.core.model.CostDownActive;
import com.enation.app.b2b2ccostdown.core.service.CostDownActiveManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 直降活动标签 
 * 用户获得对应的直降活动
 * @author Jeffrey
 *
 */
@Component
@Scope("prototype")
public class CostDownActTag extends BaseFreeMarkerTag {

    @Autowired
    private CostDownActiveManager costDownActiveManager;

    /**
     * 直降
     */
    @Override
    protected Object exec(Map params) throws TemplateModelException {
        CostDownActive cda;
        if(null == params.get("act_id")) {
            cda = costDownActiveManager.get();
        } else {
            cda = costDownActiveManager.get(NumberUtils.toInt((params.get("act_id").toString())));
        }
        if(cda == null) { return ""; }
        return cda;

    }

}
