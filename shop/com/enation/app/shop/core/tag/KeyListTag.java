package com.enation.app.shop.core.tag;

import java.util.List;
import java.util.Map;
import com.enation.app.shop.core.model.Tag;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.app.shop.core.service.impl.TagManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 品牌列表标签
 * @author kingapex
 *2013-8-20下午7:58:00
 */
@Component
@Scope("prototype")
public class KeyListTag extends BaseFreeMarkerTag {
	private TagManager tagManager;
	/**
	 * @param 不需要输出参数，
	 * @return 返回所有品牌的列表 ，List<Brand>型
	 * {@link Brand}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		List<Tag> tagList  = tagManager.keyselectlist();
		return tagList;
	}
    
    public TagManager getTagManager() {
        return tagManager;
    }
    
    public void setTagManager(TagManager tagManager) {
        this.tagManager = tagManager;
    }
	
}
