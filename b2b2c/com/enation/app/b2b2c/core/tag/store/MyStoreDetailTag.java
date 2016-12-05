package com.enation.app.b2b2c.core.tag.store;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
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
@SuppressWarnings("rawtypes")
public class MyStoreDetailTag extends BaseFreeMarkerTag{
	private IStoreManager storeManager;
	private IStoreMemberManager storeMemberManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String ctx = this.getRequest().getContextPath();
		HttpServletResponse response= ThreadContextHolder.getHttpResponse();
		Store store=new Store();
		try {
			if(params.get("type")!=null){
				store = storeManager.getStore(NumberUtils.toInt(params.get("store_id").toString()));
			}else{
				//session中获取会员信息,判断用户是否登陆
				StoreMember member=storeMemberManager.getStoreMember();
				if(member==null){
					response.sendRedirect(ctx+"/store/login.html");
				}
				//重新申请店铺时使用
				else if(member.getStore_id()==null){
					store=storeManager.getStoreByMember(member.getMember_id());
				}else{
					 store=storeManager.getStore(member.getStore_id());
				}
			}
			if(store.getDisabled()==2){
				response.sendRedirect(ctx+"/store/dis_store.html");
			}
		} catch (IOException e) {
			throw new UrlNotFoundException();
		}
		
		return store;
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
