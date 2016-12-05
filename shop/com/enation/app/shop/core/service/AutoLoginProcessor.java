package com.enation.app.shop.core.service;

import java.net.URLDecoder;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.eop.IEopProcessor;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.HttpUtil;

/**
 * 自动登录处理.
 */
public class AutoLoginProcessor implements IEopProcessor {
	private static Logger logger = LoggerFactory.getLogger(AutoLoginProcessor.class);
	
	private IMemberManager memberManager;

	@SuppressWarnings("rawtypes")
	public boolean process() {
		try {
			String url = ThreadContextHolder.getHttpRequest().getRequestURI();
			if (StringUtils.isBlank(url)) return true;
			url = StringUtils.lowerCase(url);
			if (url.endsWith("/") || url.endsWith(".html") || url.endsWith(".do")) {
				Member member = UserConext.getCurrentMember();
				if (member == null) {
					String cookieValue = HttpUtil.getCookieValue(ThreadContextHolder.getHttpRequest(), "JavaShopUser");
					if (StringUtils.isNotBlank(cookieValue)) {
						cookieValue = URLDecoder.decode(cookieValue, "UTF-8");
						cookieValue = EncryptionUtil1.authcode(cookieValue, "DECODE", "", 0);
						if (StringUtils.isNotBlank(cookieValue)) {
							Map map = (Map) JSONObject.toBean(JSONObject.fromObject(cookieValue), Map.class);
							if (map != null) {
								String username = map.get("username").toString();
								String password = map.get("password").toString();
								if (memberManager.loginWithCookie(username, password) != 1) {
									HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(), "JavaShopUser","", 0);
								}
							}
						}
					}
				} else {
					Member queryMember = memberManager.get(member.getMember_id());
					if (queryMember == null) {
						int result = memberManager.add(member);
						logger.debug("AutoLoginProcessor process memberManager.add member:"+member+" result(0：用户名已存在，1：添加成功):"+result);
					}
				}
			}
		} catch (Exception ex) {
			logger.error("AutoLoginProcessor process",ex);
		}
		return true;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
}
