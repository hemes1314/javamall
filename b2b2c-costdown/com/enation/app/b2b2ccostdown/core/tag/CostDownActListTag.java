package com.enation.app.b2b2ccostdown.core.tag;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2ccostdown.core.service.CostDownActiveManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 直降活动列表
 * @author Jeffrey
 *
 */
@Component
public class CostDownActListTag extends BaseFreeMarkerTag {

    @Autowired
    private CostDownActiveManager costDownActiveManager;

    @Override
    protected Object exec(Map params) throws TemplateModelException {
        return costDownActiveManager.listJoinEnable();
    }
}
