package com.enation.app.shop.component.giftcard.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.shop.component.giftcard.service.IGiftcardTypeManager;
import com.enation.app.shop.core.service.IMemberGiftcardManager;
import com.enation.app.shop.core.service.impl.VirtualProductManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 查询虚拟物品类型
 * @author humaodong
 *
 */
@Component
public class VitemTypeListTag extends BaseFreeMarkerTag {

	@Autowired
	private VirtualProductManager virtualProductManager;
	
	@SuppressWarnings("unchecked")
	@Override
	protected Object exec(Map param) throws TemplateModelException {
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		Map result = new HashMap();
		
		
		
		List typeList = this.virtualProductManager.list();
		return typeList;
	}
	
}
