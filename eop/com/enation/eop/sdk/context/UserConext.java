package com.enation.eop.sdk.context;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.enation.app.base.core.model.Member;
import com.enation.eop.resource.model.AdminUser;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.context.webcontext.WebSessionContext;
import com.gomecellar.workflow.utils.HttpClientUtils;

import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 用户上下文
 * @author kingapex
 */
@SuppressWarnings("unchecked")
public abstract class UserConext{
	public static final String CURRENT_MEMBER_KEY="curr_member";
	public static final String CURRENT_ADMINUSER_KEY="curr_adminuser";
	
	private static String cookieUserUrl;
    private static String UAT_cookieUserUrl = "http://service.uatcellar.com/api/user/getUserInfo";
    private static String LIVE_cookieUserUrl = "http://service.celler.api/user/getUserInfo";
    private static String getCookieUrlUrl() {
    	if (cookieUserUrl == null) {
    		String env = System.getenv("env");
        	if (env == null) {
        		env = System.getProperty("env");
        		if (env == null) env = "uat";
        	}
        	if ("live".equalsIgnoreCase(env)) {
        		cookieUserUrl = LIVE_cookieUserUrl;
        	} else {
        		cookieUserUrl = UAT_cookieUserUrl;
        	}
    	}
    	return cookieUserUrl;
    }
    
    /**
	 * 获取当前登录的会员
	 * @return 如果没有登录返回null
	 */
	public static Member getCurrentMember() {
		WebSessionContext<Member> sessonContext = ThreadContextHolder.getSessionContext();
		Member member = sessonContext.getAttribute(UserConext.CURRENT_MEMBER_KEY);
		if (member == null) {
			String cookie = ThreadContextHolder.getHttpRequest().getHeader("Cookie");
			String result = HttpClientUtils.get(getCookieUrlUrl(), cookie, Collections.emptyMap());
			JSONObject jsonObject = JSONObject.parseObject(result);
			if (jsonObject != null) {
		        boolean ok = jsonObject.getBoolean("success");
		        if (ok) {
		            JSONObject map = jsonObject.getJSONObject("data");
		            member = new Member();
					member.setMember_id(NumberUtils.toLong(map.getString("userId")));
					member.setUname(map.getString("loginName"));
					member.setNickname(map.getString("nikeName"));
					if (StringUtils.isBlank(member.getNickname())) {
						member.setNickname(member.getUname());
					}
					member.setName(member.getNickname());
					member.setMobile(map.getString("mobile"));
					member.setEmail(map.getString("email"));
					member.setFace(map.getString("memberIcon"));
					sessonContext.setAttribute(UserConext.CURRENT_MEMBER_KEY, member);
		        }
			}
		}
		return member;
	}
	
	/**
	 * 获取当前登录的管理员
	 * @return 如果没有登录返回null
	 */
	public static AdminUser getCurrentAdminUser(){
		WebSessionContext<AdminUser> sessonContext = ThreadContextHolder.getSessionContext();
		return sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
	}
}
