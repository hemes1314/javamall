package com.enation.app.b2b2c.core.action.api.store;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.StoreDlyCenter;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreDlyCenterManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 店铺发货地址 API
 * @author XuLiPeng
 *
 */
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("storeDlyCenter")
public class StoreDlyCenterApiAction extends WWAction {
	
	private IStoreDlyCenterManager storeDlyCenterManager;
	private IStoreMemberManager storeMemberManager;
	private Integer dly_center_id;
	private String name;
	private String uname;
	private String address;
	private String zip;
	private String phone;
	/**
	 * 添加发货地址
	 * @param dlyCenter 店铺发货地址,StoreDlyCenter
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String add(){
		
		try {
			StoreDlyCenter dlyCenter = this.createDlyType();
			dlyCenter.setChoose("false");
			this.storeDlyCenterManager.addDlyCenter(dlyCenter);
			this.showSuccessJson("保存成功！");
		} catch (Exception e) {
			this.showErrorJson("保存失败！");
		}
		return JSON_MESSAGE;
	}
	/**
	 * 修改发货地址
	 * @param dlyCenter 店铺发货地址,StoreDlyCenter
	 * @param dly_center_id 店铺发货地址Id,Integer
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String edit(){
		try {
			StoreDlyCenter dlyCenter = this.createDlyType();
			dlyCenter.setDly_center_id(dly_center_id);
			this.storeDlyCenterManager.editDlyCenter(dlyCenter);
			this.showSuccessJson("保存成功！");
		} catch (Exception e) {
			this.showErrorJson("保存失败！");
		}
		return JSON_MESSAGE;
	}
	/**
	 * 创建发货地址
	 * @param member 店铺会员,StoreMember
	 * @param province 省,String
	 * @param city 城市,String
	 * @param region 地区,String
	 * @param province_id 省Id,String
	 * @param city_id 城市,String
	 * @param region_id 地区Id,String
	 * @param name  名称,String
	 * @param uname 发货人姓名,String
	 * @param address 地址,String
	 * @param zip 邮编,String
	 * @param phone 电话,String
	 * @param cellphone 手机,String
	 * @param store_id 店铺Id,Integer
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	private StoreDlyCenter createDlyType(){
		StoreMember member=storeMemberManager.getStoreMember();
		
		StoreDlyCenter dlyCenter = new StoreDlyCenter();
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String province = request.getParameter("province");
		String city = request.getParameter("city");
		String region = request.getParameter("region");
		
		String province_id = request.getParameter("province_id");
		String city_id = request.getParameter("city_id");
		String region_id = request.getParameter("region_id");
		
		dlyCenter.setName(name);
		dlyCenter.setUname(uname);
		dlyCenter.setAddress(address);
		dlyCenter.setZip(zip);
		dlyCenter.setPhone(phone);
		dlyCenter.setProvince(province);
		dlyCenter.setCity(city);
		dlyCenter.setRegion(region);
		dlyCenter.setProvince_id(NumberUtils.toInt(province_id));
		dlyCenter.setCity_id(NumberUtils.toInt(city_id));
		dlyCenter.setRegion_id(NumberUtils.toInt(region_id));
		dlyCenter.setStore_id(member.getStore_id());
		
		return dlyCenter;
	}
	/**
	 * 删除发货地址
	 * @param dly_center_id 发货地址Id
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String delete(){

		try {
			this.storeDlyCenterManager.delete(dly_center_id);
			this.showSuccessJson("删除成功！");
		} catch (Exception e) {
			this.showErrorJson("删除失败！");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 设置默认发货地址
	 * @param dly_center_id 发货地址Id,Integer
	 * @param member 店铺会员,StoreMember
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String siteDefault(){
		StoreMember member=storeMemberManager.getStoreMember();
		try {
			this.storeDlyCenterManager.site_default(dly_center_id,member.getStore_id());
			this.showSuccessJson("设置成功！");
		} catch (Exception e) {
			this.showErrorJson("设置失败！");
		}
		return JSON_MESSAGE;
	}
	
	
	// set get 
	public IStoreDlyCenterManager getStoreDlyCenterManager() {
		return storeDlyCenterManager;
	}

	public void setStoreDlyCenterManager(
			IStoreDlyCenterManager storeDlyCenterManager) {
		this.storeDlyCenterManager = storeDlyCenterManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public Integer getDly_center_id() {
		return dly_center_id;
	}

	public void setDly_center_id(Integer dly_center_id) {
		this.dly_center_id = dly_center_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
