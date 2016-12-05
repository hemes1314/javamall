package com.enation.app.b2b2c.core.action.api.member;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreMemberAddressManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;

import freemarker.template.TemplateModelException;

/**
 * 店铺会员收货地址 Action
 * @author XuLiPeng
 */
@Component
@SuppressWarnings("serial")
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("memberAddress")
public class StoreMemberAddressApiAction extends WWAction {
	private IMemberAddressManager memberAddressManager;
	private IStoreMemberAddressManager storeMemberAddressManager;
	private IStoreMemberManager storeMemberManager;
	private Integer memberid;
	private Integer addrid;
	/**
	 * 添加店铺会员收货地址
	 * @param address 接收地址,MemberAddress
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String addNewAddress() {
		MemberAddress address = new MemberAddress();
		try {
			address = this.createAddress();
			this.memberAddressManager.addAddress(address);
			this.showSuccessJson("添加成功");
			return JSON_MESSAGE;
		} catch (Exception e) {
			this.logger.error("前台添加地址错误", e);
		}
		this.showErrorJson("添加失败");
		return JSON_MESSAGE;
	}
	/**
	 * 创建收货地址
	 * @param shipName 收货人名称
	 * @param shipTel 收货人电话
	 * @param shipMobile 收货人手机号
	 * @param province_id 收货——省Id
	 * @param city_id 收货——城市Id
	 * @param region_id 收货——区Id
	 * @param province 收货——省
	 * @param city 收货——城市
	 * @param region 收货——区
	 * @param shipAddr 详细地址
	 * @param shipZip 收货邮编
	 * @return 收货地址
	 */
	private MemberAddress createAddress() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();

		MemberAddress address = new MemberAddress();

		String name = request.getParameter("shipName");
		address.setName(name);

		String tel = request.getParameter("shipTel");
		address.setTel(tel);

		String mobile = request.getParameter("shipMobile");
		address.setMobile(mobile);

		String province_id = request.getParameter("province_id");
		if (province_id != null) {
			address.setProvince_id(Integer.valueOf(province_id));
		}

		String city_id = request.getParameter("city_id");
		if (city_id != null) {
			address.setCity_id(Integer.valueOf(city_id));
		}

		String region_id = request.getParameter("region_id");
		if (region_id != null) {
			address.setRegion_id(Integer.valueOf(region_id));
		}

		String province = request.getParameter("province");
		address.setProvince(province);

		String city = request.getParameter("city");
		address.setCity(city);

		String region = request.getParameter("region");
		address.setRegion(region);

		String addr = request.getParameter("shipAddr");
		address.setAddr(addr);

		String zip = request.getParameter("shipZip");
		address.setZip(zip);

		return address;
	}
	/**
	 * 设置会员默认收货地址
	 * @param member 店铺会员,StoreMember
	 * @param addrid 收货地址Id,Integer
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String setDefAddress(){
		try {
			StoreMember member=storeMemberManager.getStoreMember();
			if(member==null){
				throw new TemplateModelException("未登陆不能使用此标签[ConsigneeListTag]");
			}
			this.storeMemberAddressManager.updateMemberAddress(member.getMember_id(),addrid);
			this.showSuccessJson("设置成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败");
		}
		return JSON_MESSAGE;
	}

	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}

	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}

	public IStoreMemberAddressManager getStoreMemberAddressManager() {
		return storeMemberAddressManager;
	}

	public void setStoreMemberAddressManager(
			IStoreMemberAddressManager storeMemberAddressManager) {
		this.storeMemberAddressManager = storeMemberAddressManager;
	}

	public Integer getMemberid() {
		return memberid;
	}

	public void setMemberid(Integer memberid) {
		this.memberid = memberid;
	}

	public Integer getAddrid() {
		return addrid;
	}

	public void setAddrid(Integer addrid) {
		this.addrid = addrid;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	
}
