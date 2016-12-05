package com.enation.app.shop.core.tag.sommelier;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.model.Sommelier;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.app.shop.core.service.impl.SommelierManager;
import com.enation.eop.SystemSetting;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 品酒师列表
 * @author lin
 *2013-8-20下午7:58:00
 */
@Component
@Scope("prototype")
public class SommelierListTag extends BaseFreeMarkerTag {
	private SommelierManager sommelierManager;
	/**
	 * @param 不需要输出参数，
	 * @return 返回所有品酒师的列表 ，List<Sommelier>型
	 * {@link Sommelier}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
	    String from =(String) params.get("from");
	    String to = (String)params.get("to");
		List<Sommelier> sommelierList  =sommelierManager.list(from,to);
		String statis = SystemSetting.getStatic_server_domain();
        //替换图片路径
        for(Sommelier a :sommelierList)
        {
           if(a.getImg_url() != null)
           {
             a.setImg_url(a.getImg_url().replaceAll("fs:",statis)); 
           }else
           {
               a.setImg_url("");   
           }
        }
		return sommelierList;
	}
    
    public SommelierManager getSommelierManager() {
        return sommelierManager;
    }
    
    public void setSommelierManager(SommelierManager sommelierManager) {
        this.sommelierManager = sommelierManager;
    }
	
}
