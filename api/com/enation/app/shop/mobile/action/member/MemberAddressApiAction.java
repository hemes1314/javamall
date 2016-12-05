/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：会员地址api
 * 修改人：Sylow  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.base.core.model.Regions;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.mobile.util.ValidateUtils;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;
import com.enation.framework.util.TestUtil;

/**
 * 会员地址api
 * @author Sylow
 * @version v1.0 , 2015-08-24
 * @since v1.0
 */
@SuppressWarnings("serial")
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("address")
public class MemberAddressApiAction extends WWAction {

    private IMemberAddressManager memberAddressManager;
    private IRegionsManager regionsManager;
    private IMemberManager memberManager;

    
    /**
     * 根据地址id 获得一个地址详情
     * @param id 必填
     * @return 
     */
    public String get(){
    	try {
    		 HttpServletRequest request = ThreadContextHolder.getHttpRequest();
    		 String addrId = request.getParameter("addr_id");
            MemberAddress address = memberAddressManager.getAddress(NumberUtils.toInt(addrId));
            this.json = JsonMessageUtil.getMobileObjectJson(address);
        } catch (RuntimeException e) {
    		this.logger.error("获取账户收货地址出错", e);
			this.showPlainErrorJson("获取账户收货地址出错[" + e.getMessage() + "]");
    	}
    	return WWAction.JSON_MESSAGE;
    }
    
    
    /**
     * 获取会员的默认收货地址
     *
     * @param 无
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public String defaultAddress() {

		Member member = UserConext.getCurrentMember();
		try {

			if (member == null) {
			    this.json = JsonMessageUtil.expireSession();
				return JSON_MESSAGE;
			}
			List<MemberAddress> addressList = memberAddressManager
					.listAddress();
			if (addressList == null || addressList.size() == 0) {
				this.showPlainSuccessJson("您还没有添加地址！");
				return JSON_MESSAGE;
			}

			MemberAddress defaultAddress = null;
			for (MemberAddress address : addressList) {
				if (address.getDef_addr() != null
						&& address.getDef_addr().intValue() == 1) {
					defaultAddress = address;
					break;
				}
			}
			if (defaultAddress == null) {
				defaultAddress = addressList.get(0);
			}
			Map data = new HashMap();
			data.put("defaultAddress", defaultAddress);
			this.json = JsonMessageUtil.getMobileObjectJson(data);

		} catch (RuntimeException e) {
			TestUtil.print(e);
			this.logger.error("获取账户默认收货地址出错", e);
			this.showPlainErrorJson("获取账户默认收货地址出错[" + e.getMessage() + "]");
		}
       
        return WWAction.JSON_MESSAGE;
    }

    /**
     * 获取会员地址
     *
     * @param 无
     * @return json字串
     * result  为1表示调用正确，0表示失败 ，int型
     * data: 地址列表
     * <p/>
     * @link com.enation.app.base.core.model.MemberAddress
     * 如果没有登陆返回空数组
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public String list() {
		try {

			Member member = UserConext.getCurrentMember();
			if (member == null) {
			    this.json = JsonMessageUtil.expireSession();
				return WWAction.JSON_MESSAGE;
			}
			Map data = new HashMap();
			data.put("addressList", memberAddressManager.listAddress());
			this.json = JsonMessageUtil.getMobileObjectJson(data);

		} catch (RuntimeException e) {
			
			this.logger.error("获取收货地址出错", e);
			this.showPlainErrorJson("获取收货地址出错[" + e.getMessage() + "]");		
		}

		return WWAction.JSON_MESSAGE;
    }

    /**
     * 添加一会员地址
     *
     * @param name：收货人姓名,String型，必填
     * @param province_id:所在省id,int型，参见：{@link com.enation.app.base.core.model.Regions.region_id}，必填
     * @param city_id:                         所在城市id,int型，参见：{@link com.enation.app.base.core.model.Regions.region_id}，必填
     * @param region_id:                       所在地区id,int型，参见：{@link com.enation.app.base.core.model.Regions.region_id}	，必填
     * @param addr：详细地址,String型                ，必填
     * @param mobile：手机,String型                ，手机，必填
     * @param addressId：地址ID
     * @return json字串
     * result  为1表示添加成功，0表示失败 ，int型
     * message 为提示信息 ，String型
     * {@link MemberAddress}
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public String add() {
        Member member =UserConext.getCurrentMember();

        if (member == null) {
            this.json = JsonMessageUtil.expireSession();
            return JSON_MESSAGE;
        }

        if (memberAddressManager.addressCount(member.getMember_id()) >= 10) {
            this.showPlainErrorJson("添加失败，您最多可以维护10个收货地址！");
            return JSON_MESSAGE;
        }
        MemberAddress address = new MemberAddress();
        try {
            address = this.fillAddressFromReq(address);
            int addrId = memberAddressManager.addAddress(address);
            Map data = new HashMap();
            data.put("address", memberAddressManager.getAddress(addrId));
            data.put("addressId", addrId);
            data.put("message", "添加成功");
            this.json = JsonMessageUtil.getMobileObjectJson(data);
            this.showPlainSuccessJsonZyh(json);
        }catch(RuntimeException e){

            this.showPlainErrorJson(e.getMessage());

        } catch (Exception e) {
            if (this.logger.isDebugEnabled()) {
                logger.error(e.getStackTrace());
            }
            this.showPlainErrorJson("添加失败[" + e.getMessage() + "]");
        }
        return WWAction.JSON_MESSAGE;
    }

    /**
     * 修改收货地址
     *
     * @param addr_id：要修改的收货地址id,int型，必填
     * @param name：收货人姓名,String型，必填
     * @param province_id:所在省id,int型，参见：{@link Regions.region_id}，必填
     * @param city_id:                         所在城市id,int型，参见：{@link Regions.region_id}，必填
     * @param region_id:                       所在地区id,int型，参见：{@link Regions.region_id}	，必填
     * @param addr：详细地址,String型                ，必填
     * @param mobile：手机,String型                ，手机，必填
     * @return json字串
     * result  为1表示添加成功，0表示失败 ，int型
     * message 为提示信息 ，String型
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public String edit() {
        Member member =UserConext.getCurrentMember();

        if (member == null) {
            this.json = JsonMessageUtil.expireSession();
            return JSON_MESSAGE;
        }

        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        int addr_id = NumberUtils.toInt(request.getParameter("addr_id"), 0);

        MemberAddress address = memberAddressManager.getAddress(addr_id);
        if(address == null || !address.getMember_id().equals(member.getMember_id())){
            this.showPlainErrorJson("您没有权限进行此项操作！");
            return JSON_MESSAGE;
        }

        try {
            address = this.fillAddressFromReq(address);
            memberAddressManager.updateAddress(address);

            Map data = new HashMap();
            data.put("address", address);
            this.showPlainSuccessJson("修改成功");
          //  this.json = JsonMessageUtil.getMobileObjectJson(data);
        } catch (Exception e) {
            if (this.logger.isDebugEnabled()) {
                logger.error(e.getStackTrace());
            }
            this.showPlainErrorJson("修改失败[" + e.getMessage() + "]");
        }
        return JSON_MESSAGE;
    }

    /**
     * 设置当前地址为默认地址
     */
    public String isdefaddr() {
        try {
            Member member =UserConext.getCurrentMember();

            if (member == null) {
                this.json = JsonMessageUtil.expireSession();
                return JSON_MESSAGE;
            }

            HttpServletRequest request = ThreadContextHolder.getHttpRequest();
            int addr_id = NumberUtils.toInt(request.getParameter("addr_id"), 0);
            MemberAddress memberAddress = memberAddressManager.getAddress(addr_id);
            if(memberAddress == null || !memberAddress.getMember_id().equals(member.getMember_id())){
                this.showPlainErrorJson("您没有权限进行此项操作！");
                return JSON_MESSAGE;
            }

            memberAddressManager.updateAddressDefult();
            memberAddressManager.addressDefult(""+addr_id);
            this.showPlainSuccessJson("设置为默认地址成功！");
        } catch (Exception e) {
            if (this.logger.isDebugEnabled()) {
                logger.error(e.getStackTrace());
            }
            this.showPlainErrorJson("设置为默认地址失败！");
        }
        return JSON_MESSAGE;
    }

    /**
     * 删除一个收货地址
     *
     * @param addr_id ：要删除的收货地址id,int型
     *                result  为1表示添加成功，0表示失败 ，int型
     *                message 为提示信息 ，String型
     */
    public String delete() {
    	 try {
    		 
	        Member member =UserConext.getCurrentMember();
	
	        if (member == null) {
	            this.showPlainErrorJson("您没有登录或登录过期！");
	            return JSON_MESSAGE;
	        }
	
	        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
	        int addr_id = NumberUtils.toInt(request.getParameter("addr_id"), 0);
	
	        MemberAddress memberAddress = memberAddressManager.getAddress(addr_id);
	        if(memberAddress == null || !memberAddress.getMember_id().equals(member.getMember_id())){
	            this.showPlainErrorJson("您没有权限进行此项操作！");
	            return JSON_MESSAGE;
	        }
            memberAddressManager.deleteAddress(addr_id);
            this.showPlainSuccessJson("删除收货地址成功！");
        } catch (RuntimeException e) {
            if (this.logger.isDebugEnabled()) {
                logger.error(e.getStackTrace());
            }
            this.showPlainErrorJson("删除收货地址失败！");
        }
        return JSON_MESSAGE;
    }

    /**
     * 从request中填充address信息
     *
     * @param address
     * @return
     */
    private MemberAddress fillAddressFromReq(MemberAddress address) {
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();

        String name = request.getParameter("name");
        if (StringUtil.isEmpty(name)) {
            throw new RuntimeException("收货人姓名不能为空！");
        }
        address.setName(name);
        Pattern p = Pattern.compile("^[0-9A-Za-z一-龥]{0,20}$");
        Matcher m = p.matcher(name);

        if (!m.matches()) {
            throw new RuntimeException("收货人姓名格式不正确！");
        }

        address.setTel("");

        String mobile = request.getParameter("mobile");
        address.setMobile(mobile);
        if (StringUtil.isEmpty(mobile) || !ValidateUtils.checkMobile(mobile)) {
        	 throw new RuntimeException("请输入正确的手机号码！");
        }
//        if (StringUtil.isEmpty(mobile) || !isMobile(mobile) == false) {
//            throw new RuntimeException("请输入正确的手机号码！");
//        }

        String province_id = request.getParameter("province_id");
        if (province_id == null || province_id.equals("")) {
            throw new RuntimeException("请选择所在地区中的省！");
        }
        address.setProvince_id(Integer.valueOf(province_id));

        Regions province = regionsManager.get(address.getProvince_id());
        if (province == null)
            throw new RuntimeException("系统参数错误！");
        address.setProvince(province.getLocal_name());

        String city_id = request.getParameter("city_id");
        if (city_id == null || city_id.equals("")) {
            throw new RuntimeException("请选择所在地区中的市！");
        }
        address.setCity_id(Integer.valueOf(city_id));

        Regions city = regionsManager.get(address.getCity_id());
        if (city == null) {
            throw new RuntimeException("系统参数错误！");
        }
        address.setCity(city.getLocal_name());

        String region_id = request.getParameter("region_id");
        if (region_id == null || region_id.equals("")) {
            throw new RuntimeException("请选择所在地区中的县！");
        }
        address.setRegion_id(Integer.valueOf(region_id));

        Regions region = regionsManager.get(address.getRegion_id());
        if (region == null) {
            throw new RuntimeException("系统参数错误！");
        }
        address.setRegion(region.getLocal_name());

        String addr = request.getParameter("addr");
        if (addr == null || addr.equals("")) {
            throw new RuntimeException("详细地址不能为空！");
        }
        String zip = request.getParameter("zip");
        Integer def_addr = NumberUtils.toInt( request.getParameter("def_addr"));
        address.setAddr(addr);
        address.setZip(zip);
        address.setDef_addr(def_addr);        
        return address;
    }

   /* private static boolean isPhone(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }*/

    @SuppressWarnings("unused")
	private static boolean isMobile(String str) {
        Pattern p1 = null, p2 = null;
        Matcher m = null;
        boolean b = false;
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if (str.length() > 9) {
            m = p1.matcher(str);
            b = m.matches();
        } else {
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }


    public IMemberAddressManager getMemberAddressManager() {
        return memberAddressManager;
    }

    public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
        this.memberAddressManager = memberAddressManager;
    }

    public IRegionsManager getRegionsManager() {
        return regionsManager;
    }

    public void setRegionsManager(IRegionsManager regionsManager) {
        this.regionsManager = regionsManager;
    }

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
}
