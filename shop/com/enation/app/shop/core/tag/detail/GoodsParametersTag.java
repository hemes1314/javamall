package com.enation.app.shop.core.tag.detail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.model.support.ParamGroup;
import com.enation.app.shop.core.service.GoodsTypeUtil;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商品参数标签
 * @author whj
 *2014-07-02下午23:13:00
 */
@Component
@Scope("prototype")
public class GoodsParametersTag extends BaseFreeMarkerTag {
    /**
     * 商品参数标签
     * 获得 商品参数的  参数组
     * 例如：goods表中params字段中有如下json。name[{"name":"基本参数","paramList":[{"name":"规格","value":"口径18cm，高8.28cm","valueList":[]},{"name":"上架时间","value":"","valueList
     * ${node.name}得到结果：“基本参数”
     * 要想得到paramList下的json中，则继续试用第二层list循环
     * html详情参考default/detail/goods_props.html.中的<div class="rer_para paramList">
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Object exec(Map params) throws TemplateModelException {
        
        HttpServletRequest request  = this.getRequest();
        Map goods =(Map)request.getAttribute("goods");
        String goodParams  =(String)goods.get("params");
        Map result = new HashMap();
        if(goodParams!=null && !goodParams.equals("")){
            ParamGroup[] paramList =GoodsTypeUtil.converFormString(goodParams);
            
            result.put("paramList", paramList);
    
            
            if(paramList!=null && paramList.length>0)
                result.put("hasParam", true);
            else
                result.put("hasParam", false);
        }else{
            result.put("hasParam", false);
        }

        return result;
        
    }

}