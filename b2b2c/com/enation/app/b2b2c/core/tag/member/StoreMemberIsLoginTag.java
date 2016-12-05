package com.enation.app.b2b2c.core.tag.member;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.RequestUtil;

import freemarker.template.TemplateModelException;

/**
 * 判断会员是否登陆标签
 * @author LiFenLong
 *
 */
@SuppressWarnings("rawtypes")
@Component
public class StoreMemberIsLoginTag extends BaseFreeMarkerTag{
	private IStoreMemberManager storeMemberManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		StoreMember member=storeMemberManager.getStoreMember();
		boolean isLogin = false;
		
		//add by lin判断 登陆后  帐号被删除  
//		if(member!=null){
//            StoreMember ismember=storeMemberManager.getMember(member.getMember_id());
//            if(ismember == null)
//            {
// 
//                        isLogin = false;
//                        return isLogin;
//
//            }
//		}
		
		if (member != null) {
			isLogin = true;
		} else {
			if("no".equals(params.get("redirect"))){
				return isLogin;
			}
			//没有登录则跳转到登陆页面
			HttpServletRequest request   = ThreadContextHolder.getHttpRequest();
			HttpServletResponse response = ThreadContextHolder.getHttpResponse();
			String curr_url = RequestUtil.getRequestUrl(request);
			String loginUrl=request.getContextPath()+"/store/login.html?forward="+curr_url;
			try {
				if(!curr_url.equals(request.getContextPath()+"/")&&!curr_url.equals(request.getContextPath()+"/store/index.html")){
					response.sendRedirect(loginUrl);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return isLogin;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
