package com.enation.app.shop.core.tag.member;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 收货人详细标签
 * @author kingapex
 *2013-7-26下午2:26:12
 */
@Component
@Scope("prototype")
public class ConsigneeDetailTag extends BaseFreeMarkerTag {
	private IMemberAddressManager memberAddressManager;
	
	/**
	 * 读取某个收货人的详细信息
	 * @param addressid：收货地址id,int型，必填项
	 * @return 会员地址，即MemberAddress
	 * {@link MemberAddress}
	 */
	@Override
	public Object exec(Map arg) throws TemplateModelException {

		Integer addressid = NumberUtils.toInt((String) arg.get("addressid"));
		if(addressid == null){
			throw new TemplateModelException("必须提供收货地址id参数");
		}
		MemberAddress address = memberAddressManager.getAddress( addressid);
		if(address==null){
			 return "0";
		}
		return memberAddressManager.getAddress( addressid);
	}
	
	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}
	
	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}

} 
