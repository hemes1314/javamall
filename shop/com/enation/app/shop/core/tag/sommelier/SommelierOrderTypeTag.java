package com.enation.app.shop.core.tag.sommelier;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.model.Sommelier;
import com.enation.app.shop.core.model.SommelierOrderType;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.app.shop.core.service.impl.SommelierManager;
import com.enation.app.shop.core.service.impl.SommelierOrderTypeManager;
import com.enation.eop.SystemSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 品酒师预约侍酒
 * @author lin
 *2015-11-13下午7:58:00
 */
@Component
@Scope("prototype")
public class SommelierOrderTypeTag extends BaseFreeMarkerTag {
	private SommelierOrderTypeManager sommelierOrderTypeManager;
	/**
	 * @param 不需要输出参数，
	 * @return 返回所有品酒师的列表 ，List<Sommelier>型
	 * {@link Sommelier}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
	    String sommelierids = ThreadContextHolder.getHttpRequest().getParameter("sommelierid");
        Integer sommelierid = Integer.valueOf(sommelierids);
        Integer typeId = null;
		List<SommelierOrderType> orderTypeList  = (List<SommelierOrderType>) sommelierOrderTypeManager.getOrderType(typeId);
		String statis = SystemSetting.getStatic_server_domain();
		return orderTypeList;
	}
    
    public SommelierOrderTypeManager getSommelierOrderTypeManager() {
        return sommelierOrderTypeManager;
    }
    
    public void setSommelierOrderTypeManager(SommelierOrderTypeManager sommelierOrderTypeManager) {
        this.sommelierOrderTypeManager = sommelierOrderTypeManager;
    }

	
	
}
