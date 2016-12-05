package com.enation.app.shop.core.tag.sommelier;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Sommelier;
import com.enation.app.shop.core.service.impl.SommelierManager;
import com.enation.eop.SystemSetting;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 品酒师列表
 * @author lin
 *2013-8-20下午7:58:00
 */
@Component
@Scope("prototype")
public class SommelierListTag3 extends BaseFreeMarkerTag {
	private SommelierManager sommelierManager;
	/**
	 * @param 不需要输出参数，
	 * @return 返回所有品酒师的列表 ，List<Sommelier>型
	 * {@link Sommelier}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
	    HttpServletRequest request = getRequest();
        Integer pageNo =  NumberUtils.toInt(request.getParameter("page"), 1);
		Page page = sommelierManager.list(null, pageNo, 12);
        if (page != null) {
        	page.setCurrentPageNo(pageNo);
        	List<Map<String, Object>> sommelierList = (List<Map<String, Object>>) page.getResult();
        	String statis = SystemSetting.getStatic_server_domain();
        	// 替换图片路径
        	if (sommelierList != null) {
        		String imgUrl = null;
        		String introduce = null;
        		for (Map<String, Object> map : sommelierList) {
        			imgUrl = (String) map.get("img_url");
        			if (imgUrl == null) {
        				map.put("img_url", "");
        			} else {
        				map.put("img_url", imgUrl.replaceAll("fs:", statis));
        			}
        			introduce = (String) map.get("introduce");
					if (introduce != null) {
						map.put("introduce", introduce.replaceAll("<p>", ""));
						map.put("introduce", introduce.replaceAll("</p>", ""));
					}
        		}
        	}
        }
		return page;
	}
    
    public SommelierManager getSommelierManager() {
        return sommelierManager;
    }
    
    public void setSommelierManager(SommelierManager sommelierManager) {
        this.sommelierManager = sommelierManager;
    }
	
}
