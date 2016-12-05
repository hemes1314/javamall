package com.enation.app.shop.core.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
public class DemoTag extends BaseFreeMarkerTag {

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		List list = new ArrayList();
		list.add("aa");
		list.add("bb");
		list.add("cc");
		return list;
	}

}
