package com.enation.app.shop.core.action.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.base.core.model.Regions;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

import static javax.swing.UIManager.get;

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("memberAddress")

/**
 * 会员地址api
 * @author kingapex
 *2013-7-26上午9:47:19
 */
public class MemberAddressApiAction extends WWAction {
	private IMemberAddressManager memberAddressManager;
	private Integer addr_id;
	
	
	/**
	 * 获取会员地址
	 * @param 无
	 * 
	 * @return json字串
	 * result  为1表示调用正确，0表示失败 ，int型
	 * data: 地址列表
	 * 
	 * {@link MemberAddress}
	 * 如果没有登录返回空数组
	 */
	public String list() {
		List<MemberAddress> addressList = null;
		MemberAddress defaultAddress = null;
		Member member = UserConext.getCurrentMember();
		if (member != null) {
			// 读取此会员的收货地址列表
			addressList = memberAddressManager.listAddress();
			defaultAddress = this.getDefaultAddress(addressList);
		} else {
			addressList = new ArrayList();
		}
		
		Map data = new HashMap();
		data.put("addressList", addressList);
		data.put("defaultAddress", defaultAddress);
		this.json = JsonMessageUtil.getObjectJson(data);
		return WWAction.JSON_MESSAGE;
	}

	
	/**
	 * 添加一会员地址
	 * @param  name：收货人姓名,String型，必填
	 * @param province:所在省,String型，必填
	 * @param province_id:所在省id,int型，参见：{@link Regions.region_id}，必填
	 * @param city：所在城市,String型，必填
	 * @param city_id: 所在城市id,int型，参见：{@link Regions.region_id}，必填
	 * @param region：所在地区,String型，必填
	 * @param region_id: 所在地区id,int型，参见：{@link Regions.region_id}	，必填
	 * @param addr：详细地址,String型 ，必填
	 * @param zip：邮编,String型 ，必填
	 * @param tel：固定电话,String型 ，手机和电话必填一项
	 * @param mobile：手机,String型 ，手机和电话必填一项
	 * @param def_addr：是否是默认地址,如果传递"1"则为默认地址，如果传递"0"为非默认地址    whj 14-03-07,修改(107-111行).
	 * @param remark：备注,String型,可选项
	 * @return json字串
	 * result  为1表示添加成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 * {@link MemberAddress}
	 */
	public String add() {
		Member member = UserConext.getCurrentMember();
		if (member == null) {
			this.showErrorJson("无权访问此api[未登录或已超时]");
			return WWAction.JSON_MESSAGE;
		}

		if (memberAddressManager.addressCount(member.getMember_id()) >= 10) {
			this.showErrorJson("添加失败。原因：最多可以维护10个收货地址。");
		} else {
			MemberAddress address = new MemberAddress();
			try {
				address = this.fillAddressFromReq(address);
				HttpServletRequest request = ThreadContextHolder.getHttpRequest();
				String def_addr = request.getParameter("def_addr");
				if ("1".equals(def_addr)){
					address.setDef_addr(Integer.valueOf(def_addr));       //应该是让当钱的member_address的addr_id的def_add值是1.如果是这个意思，那么执行顺序做了，应该是先变成0，然后再执行本句，为什么这么写也对呢
					memberAddressManager.updateAddressDefult();           //让member_address的def_addr值变成0.大事上一句话是啥意思。
				}
				
				memberAddressManager.addAddress(address);
				this.showSuccessJson("添加成功");
			} catch (Exception e) {
				if (this.logger.isDebugEnabled()) {
					logger.error(e.getStackTrace());
				}
				this.showErrorJson("添加失败[" + e.getMessage() + "]");
			}
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 修改收货地址
	 * @param  addr_id：要修改的收货地址id,int型，必填
	 * @param  name：收货人姓名,String型，必填
	 * @param province:所在省,String型，必填
	 * @param province_id:所在省id,int型，参见：{@link Regions.region_id}，必填
	 * @param city：所在城市,String型，必填
	 * @param city_id: 所在城市id,int型，参见：{@link Regions.region_id}，必填
	 * @param region：所在地区,String型，必填
	 * @param region_id: 所在地区id,int型，参见：{@link Regions.region_id}	，必填
	 * @param addr：详细地址,String型 ，必填
	 * @param zip：邮编,String型 ，必填
	 * @param tel：固定电话,String型 ，手机和电话必填一项
	 * @param mobile：手机,String型 ，手机和电话必填一项
	 * @param def_addr：是否是默认地址,Integer型 ,可选项，如果传递"1"则为默认地址，如果传递"0"为非默认地址    whj 14-03-07,修改(158-161行).
	 * @param remark：备注,String型,可选项
	 * @return json字串
	 * result  为1表示添加成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	public String edit() {
		Member member = UserConext.getCurrentMember();

		if (member == null) {
			this.showErrorJson("无权访问此api[未登录或已超时]");
			return WWAction.JSON_MESSAGE;
		}
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String addr_id = request.getParameter("addr_id");
		MemberAddress address = memberAddressManager.getAddress(Integer.valueOf(addr_id));
		try {
			address = this.fillAddressFromReq(address);
			String def_addr = request.getParameter("def_addr");
			if ("1".equals(def_addr)){
				address.setDef_addr(Integer.valueOf(def_addr));
				memberAddressManager.updateAddressDefult(); 
			}
			
			if ("0".equals(def_addr)){
				address.setDef_addr(Integer.valueOf(def_addr));
			}
			
			memberAddressManager.updateAddress(address);
			StoreCartContainer.putSelectedAddress(address);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
			this.showErrorJson("修改失败[" + e.getMessage() + "]");
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 设置当前地址为默认地址
	 */
	public String isdefaddr() {
		try {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String addr_id = request.getParameter("addr_id");
		memberAddressManager.updateAddressDefult(); 
		memberAddressManager.addressDefult(addr_id); 
		this.showSuccessJson("修改成功");
		
		}
		catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
			this.showErrorJson("修改失败[" + e.getMessage() + "]");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	
	
	/**
	 * 删除一个收货地址
	 * @param addr_id ：要删除的收货地址id,int型
	 * result  为1表示添加成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	public String delete() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String addr_id = request.getParameter("addr_id");
		try {
			HttpServletResponse response= ThreadContextHolder.getHttpResponse();
			String ctx = this.getRequest().getContextPath();
			Member member = UserConext.getCurrentMember();
			if(member == null)
			{
				try {
					//未登陆跳转到登录页面
					response.sendRedirect(ctx+"/store/login.html");
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			List<MemberAddress> addresses = memberAddressManager.listAddress();
			for (int i = 0; i < addresses.size(); i++) {
				MemberAddress memberAddress =addresses.get(i);
				if(Integer.valueOf(addr_id)==memberAddress.getAddr_id()){
					memberAddressManager.deleteAddress(Integer.valueOf(addr_id));
					this.showSuccessJson("删除成功");
					break;
				}
				this.showSuccessJson("非法操作");
			}

			
			// 如果地址是默认地址则将缓存清除
			MemberAddress address = StoreCartContainer.getUserSelectedAddress();
			if(address != null && addr_id.equals(String.valueOf(address.getAddr_id()))) {
			    ThreadContextHolder.getSessionContext().removeAttribute("user_selected_address");
			}
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
			this.showErrorJson("删除失败[" + e.getMessage() + "]");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	
	
	
	/**
	 * 结算时直接添加
	 */
	
	public String addNewAddress() {
		
		Member member = UserConext.getCurrentMember();
		if (member == null) {
			this.showErrorJson("无权访问此api[未登录或已超时]");
			return JSON_MESSAGE;
		}
		if (memberAddressManager.addressCount(member.getMember_id()) >= 10) {
			this.showErrorJson("添加失败。原因：最多可以维护10个收货地址。");
			return JSON_MESSAGE;
		}
		
		MemberAddress address = new MemberAddress();
		try {
			address = this.createAddress();
			
			int addressid =this.memberAddressManager.addAddress(address);
			address.setAddr_id(addressid);
			this.json= JsonMessageUtil.getObjectJson(address);
			return JSON_MESSAGE;
		} catch (Exception e) {
			e.printStackTrace();
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
		String def_addr = request.getParameter("def_addr");
	
		MemberAddress address = new MemberAddress();
		
		//设置是否是默认地址
		if ("1".equals(def_addr)){
			address.setDef_addr(Integer.valueOf(def_addr));
		}
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
			Member member = UserConext.getCurrentMember();
			if(member==null){
				throw new TemplateModelException("未登录不能使用此标签[ConsigneeListTag]");
			}
			this.memberAddressManager.updateMemberAddress(member.getMember_id(),addr_id);
			this.showSuccessJson("设置成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败");
		}
		return JSON_MESSAGE;
	}

	/************以下方法非api，不需要书写api文档*************/
	
	
	/**
	 * 设置默认地址
	 * @param addr_id:要设置为默认收货地址的id,int型
	 * result  为1表示设置功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	public String defaddr() {
		Member member = UserConext.getCurrentMember();

		if (member == null) {
			this.showErrorJson("无权访问此api[未登录或已超时]");
			return WWAction.JSON_MESSAGE;
		}
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String addr_id = request.getParameter("addr_id");
		MemberAddress address = memberAddressManager.getAddress(Integer.valueOf(addr_id));
		address.setDef_addr(1);
		try {
			memberAddressManager.updateAddressDefult();
			memberAddressManager.updateAddress(address);
			this.showSuccessJson("设置成功");
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
			this.showErrorJson("设置失败[" + e.getMessage() + "]");
		}
		return WWAction.JSON_MESSAGE;
	}
	

	
	
	/**
	 * 获取会员的默认收货地址
	 * @param 无
	*/
	private MemberAddress getDefaultAddress(List<MemberAddress> addressList) {
		if (addressList != null && !addressList.isEmpty()) {
			for (MemberAddress address : addressList) {
				if (address.getDef_addr() != null && address.getDef_addr().intValue() == 1) {
					address.setDef_addr(1);
					return address;
				}
			}

			MemberAddress defAddress = addressList.get(0);
			defAddress.setDef_addr(1);
			return defAddress;
		}
		return null;
	}
	
	
	/**
	 * 从request中填充address信息
	 * @param address
	 * @return
	 */
	private MemberAddress fillAddressFromReq(MemberAddress address) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String def_addr = request.getParameter("def_addr");
		if ("yes".equals(def_addr)){
			address.setDef_addr(Integer.valueOf(def_addr));
		}
		String name = request.getParameter("name");
		address.setName(name);
		if (name == null || name.equals("")) {
			throw new RuntimeException("姓名不能为空！");
		}
		Pattern p = Pattern.compile("^[0-9A-Za-z一-龥]{0,20}$");
		Matcher m = p.matcher(name);

		if (!m.matches()) {
			throw new RuntimeException("收货人格式不正确！");
		}

		String tel = request.getParameter("tel");
		address.setTel(tel);

 		String mobile = request.getParameter("mobile");
		address.setMobile(mobile);
		if (StringUtil.isEmpty(tel)	&& StringUtil.isEmpty(mobile)) {
			throw new RuntimeException("联系电话和联系手机必须填写一项！");
		}else if(!StringUtil.isEmpty(tel)&& isMobile(tel)==false){
			throw new RuntimeException("电话格式不对！");
		}else if(!StringUtil.isEmpty(mobile)&& isPhone(mobile)==false){
			throw new RuntimeException("手机格式不对！");
		}

		String province_id = request.getParameter("province_id");
		if (province_id == null || province_id.equals("")) {
			throw new RuntimeException("请选择地区中的省！");
		}
		address.setProvince_id(Integer.valueOf(province_id));

		String city_id = request.getParameter("city_id");
		if (city_id == null || city_id.equals("")) {
			throw new RuntimeException("请选择地区中的市！");
		}
		address.setCity_id(Integer.valueOf(city_id));

		String region_id = request.getParameter("region_id");
		if (region_id == null || region_id.equals("")) {
			throw new RuntimeException("请选择地区中的县！");
		}
		address.setRegion_id(Integer.valueOf(region_id));

		String province = request.getParameter("province");
		address.setProvince(province);

		String city = request.getParameter("city");
		address.setCity(city);

		String region = request.getParameter("region");
		address.setRegion(region);

		String addr = request.getParameter("addr");
		if (addr == null || addr.equals("")) {
			throw new RuntimeException("地址不能为空！");
		}
		/*	Comment by Liuzy 校验导至 4-2401 即4单元2401室这样的写法不能通过		
		Pattern p1 = Pattern.compile("^[0-9A-Za-z一-]{0,50}$");
		Matcher m1 = p1.matcher(addr);
		if(!m1.matches()){
			throw new RuntimeException("地址格式不正确！");
			
		}*/
		address.setAddr(addr);

		String zip = request.getParameter("zip");
		if (zip == null || zip.equals("")) {
			throw new RuntimeException("邮编不能为空！");
		}
		address.setZip(zip);

		String remark = request.getParameter("remark");
		address.setRemark(remark);

		return address;
	}

	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}

	public void setMemberAddressManager(
			IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}
	private static boolean isPhone(String str) {   
        Pattern p = null;  
        Matcher m = null;  
        boolean b = false;   
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号  
        m = p.matcher(str);  
        b = m.matches();   
        return b;  
    }  
	private static boolean isMobile(String str) {   
        Pattern p1 = null,p2 = null;  
        Matcher m = null;  
        boolean b = false;    
        p1 = Pattern.compile("0\\d{2,3}-\\d{7,8}");       // 验证带区号的        whj  2015-05-22修改对带区号的固定电话的验证，验证格式为“0467-8888888” 
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");     // 验证没有区号的  
        if(str.length() >9)  
        {   m = p1.matcher(str);  
            b = m.matches();    
        }else{  
            m = p2.matcher(str);  
            b = m.matches();   
        }    
        return b;  
    }
	public Integer getAddr_id() {
		return addr_id;
	}

	public void setAddr_id(Integer addr_id) {
		this.addr_id = addr_id;
	}  
	
}
