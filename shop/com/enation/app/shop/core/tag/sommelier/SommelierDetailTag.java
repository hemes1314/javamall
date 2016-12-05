package com.enation.app.shop.core.tag.sommelier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.model.ReturnsOrder;
import com.enation.app.shop.core.model.Sommelier;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.app.shop.core.service.impl.SommelierManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 品酒师列表
 * @author lin
 *2013-8-20下午7:58:00
 */
@Component
@Scope("prototype")
public class SommelierDetailTag extends BaseFreeMarkerTag {
	private SommelierManager sommelierManager;
	/**
	 * @param 不需要输出参数，
	 * @return 返回所有品酒师的详情
	 * {@link Sommelier}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
	    HttpServletRequest request = getRequest();
	    String sommelier_id = request.getParameter("sommelier_id");
	    Sommelier sommelier = this.sommelierManager.get(Integer.valueOf(sommelier_id));
	    if(sommelier.getImg_url()==null)
	        sommelier.setImg_url("");
	    if(sommelier.getTasting_count()==null)
	        sommelier.setTasting_count(0);
	    if(sommelier.getBad_comment()==null)
	        sommelier.setBad_comment(0);
        if(sommelier.getGood_comment()==null)
            sommelier.setGood_comment(0);
        
        Map result = new HashMap();
        List tastingNoteList =sommelierManager.list();
        result.put("sommelier", sommelier);
        result.put("tastingNoteList", tastingNoteList);
        return result;
	}
    
    public SommelierManager getSommelierManager() {
        return sommelierManager;
    }
    
    public void setSommelierManager(SommelierManager sommelierManager) {
        this.sommelierManager = sommelierManager;
    }
	
}
