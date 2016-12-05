package com.enation.app.shop.core.tag;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 配送方式详细标签
 * @author kingapex
 *2013-7-27下午9:10:14
 */
@Component
@Scope("prototype")
public class DlyTypeDetailTag extends BaseFreeMarkerTag {
	private IDlyTypeManager dlyTypeManager;
	
	/**
	 * 配送方式详细标签
	 * @param id:配送方式id
	 * @return 配送方式详细，DlyType型
	 * {@link DlyType} 
	 */
	@Override
	public Object exec(Map args) throws TemplateModelException {
		
		Integer type_id =  (Integer)args.get("id");
		
		if(type_id==null)throw new TemplateModelException("必须传递配送方式id参数");
		DlyType dlyType = dlyTypeManager.getDlyTypeById(type_id);
		
		return dlyType;
	}
	public IDlyTypeManager getDlyTypeManager() {
		return dlyTypeManager;
	}
	public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
		this.dlyTypeManager = dlyTypeManager;
	}

}
