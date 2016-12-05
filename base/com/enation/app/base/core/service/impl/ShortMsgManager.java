package com.enation.app.base.core.service.impl;

import java.util.List;

import com.enation.app.base.core.model.ShortMsg;
import com.enation.app.base.core.plugin.shortmsg.ShortMsgPluginBundle;
import com.enation.app.base.core.service.IShortMsgManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.DateUtil;

/**
 * 短消息管理
 * @author kingapex
 *
 */
public class ShortMsgManager extends BaseSupport<ShortMsg> implements IShortMsgManager {

	private ShortMsgPluginBundle shortMsgPluginBundle;

	@Override
	public List<ShortMsg> listNotReadMessage() {
		
		return shortMsgPluginBundle.getMessageList();
	}

	public ShortMsgPluginBundle getShortMsgPluginBundle() {
		return shortMsgPluginBundle;
	}

	public void setShortMsgPluginBundle(ShortMsgPluginBundle shortMsgPluginBundle) {
		this.shortMsgPluginBundle = shortMsgPluginBundle;
	}

	
	 

	
}
