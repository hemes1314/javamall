package com.enation.app.b2b2c.core.tag.store;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
@Component
/**
 * 店铺信息Tag
 * @author LiFenLong
 *
 */
public class MyStoreApplyTag extends BaseFreeMarkerTag{
	private IStoreManager storeManager;
	private IStoreMemberManager storeMemberManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String ctx = this.getRequest().getContextPath();
		HttpServletResponse response= ThreadContextHolder.getHttpResponse();
		try {
			//session中获取会员信息,判断用户是否登陆
			StoreMember member=storeMemberManager.getStoreMember();
			if(member==null){
				response.sendRedirect(ctx+"/store/login.html");
			}
			//如果已有店铺则跳转到商家中心，屏蔽店铺申请
			else if(member.getStore_id()!=null){
			    response.sendRedirect(ctx+"/store/business_center.html?menu=store_index");
			}
		} catch (IOException e) {
			throw new UrlNotFoundException();
		}
		return true;
	}
	public IStoreManager getStoreManager() {
		return storeManager;
	}
	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
