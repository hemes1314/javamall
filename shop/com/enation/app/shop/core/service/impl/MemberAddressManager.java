package com.enation.app.shop.core.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.base.core.model.Regions;
import com.enation.app.shop.core.plugin.member.MemberPluginBundle;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;
import com.enation.framework.util.ip.IPSeeker;



/**
 * 会员中心-收货地址
 * 
 * @author lzf<br/>
 *         2010-3-17 下午03:03:56<br/>
 *         version 1.0<br/>
 */
public class MemberAddressManager extends BaseSupport<MemberAddress> implements
		IMemberAddressManager {

	
	private MemberPluginBundle memberPluginBundle;
	private static final String USER_REGION_ID_KEY="user_region_id_key";
	@Transactional(propagation = Propagation.REQUIRED)
	public int addAddress(MemberAddress address) {
		Member member = UserConext.getCurrentMember();
		address.setMember_id(member.getMember_id());
		MemberAddress defAddr = this.getMemberDefault(member.getMember_id());
		
		//如果没有默认地址，则此地址置为默认地址
		if(defAddr==null){
			address.setDef_addr(1);
		}else{
			//不是第一个，且设置为默认地址了，则更新其它地址为非默认地址
			if(address.getDef_addr()==1){
				daoSupport.execute( "update es_member_address set def_addr = 0 where member_id = ?",member.getMember_id());

			}
		}
		
		
		this.baseDaoSupport.insert("member_address", address);
		int addressid  = this.baseDaoSupport.getLastId("member_address");
		address.setAddr_id(addressid);
		memberPluginBundle.onAddressAdd(address);
		return addressid;
	}

	
	public void deleteAddress(int addr_id) {
		this.baseDaoSupport.execute(
				"delete from member_address where addr_id = ?", addr_id);
	}

	
	public MemberAddress getAddress(int addr_id) {
		MemberAddress address = this.baseDaoSupport.queryForObject(
				"select * from member_address where addr_id = ?",
				MemberAddress.class, addr_id);
		return address;
	}

	
	public List<MemberAddress> listAddress() {
		Member member = UserConext.getCurrentMember();
		List<MemberAddress> list = this.baseDaoSupport.queryForList(
				"select * from member_address where member_id = ?", MemberAddress.class,  member.getMember_id());
		return list;
	}

	
	public void updateAddress(MemberAddress address) {
		if(address.getDef_addr()==1){
			Member member = UserConext.getCurrentMember();
			this.baseDaoSupport.execute(
					"update member_address set def_addr = 0 where member_id = ?", member.getMember_id());
	
			}
		this.baseDaoSupport.update("member_address", address, "addr_id="+ address.getAddr_id());
	}


	@Override
	public void updateAddressDefult() {
		Member member = UserConext.getCurrentMember();
		this.baseDaoSupport.execute(
				"update member_address set def_addr = 0 where member_id = ?", member.getMember_id());
	}


	@Override
	public void addressDefult(String addr_id) {
		this.baseDaoSupport.execute(
				"update member_address set def_addr = 1 where addr_id = ?",addr_id);
	}
	
	public MemberPluginBundle getMemberPluginBundle() {
		return memberPluginBundle;
	}


	public void setMemberPluginBundle(MemberPluginBundle memberPluginBundle) {
		this.memberPluginBundle = memberPluginBundle;
	}


	@Override
	public int addressCount(long member_id) {
		return baseDaoSupport.queryForInt("select count(*) from member_address where member_id=?", member_id);
	}


	@Override
	public MemberAddress getMemberDefault(long memberid) {
		String sql = "select * from es_member_address where member_id=? and def_addr=1";
		List<MemberAddress> addressList  = this.baseDaoSupport.queryForList(sql, MemberAddress.class, memberid);
		MemberAddress address=null;
		if(!addressList.isEmpty()){
			address=addressList.get(0);
		}
		return address;
	}
	
	 
	@SuppressWarnings("unchecked")
	@Override
	public int getMemberDefaultRegionId() {
		
		Integer regionid =(Integer)ThreadContextHolder.getSessionContext().getAttribute(USER_REGION_ID_KEY);
		
		if(regionid!=null) {
			return regionid;
		}
		
		
		
		Member member =UserConext.getCurrentMember();
		if(member!=null){
			MemberAddress address=this.getMemberDefault(member.getMember_id());
			
			//会员没有默认地址用ip获取
			if(address==null){
				regionid=this.getRegionIdByIp();
			}else{
				regionid =address.getRegion_id();
			}
		}else{
			
			//会员没登陆用获取
			regionid =this.getRegionIdByIp();
		}
		//将地区id压入session
		ThreadContextHolder.getSessionContext().setAttribute(USER_REGION_ID_KEY, regionid);
		return regionid;
	}
	
	
	/**
	 * 根据ip得到省的id
	 * @return 如果根据ip没找到，则默认是北京
	 */
	@SuppressWarnings("rawtypes")
	private int getRegionIdByIp(){
		String ip = getRemoteHost();
		IPSeeker ipSeeker=  IPSeeker.getInstance(); 
		String country=  ipSeeker.getIPLocation(ip).getCountry();
		country=this.getDiqu(country);
		List list = this.baseDaoSupport.queryForList("select * from regions where local_name like '%"+country+"%' order by region_path asc", Regions.class  );
		
		if(list== null || list.isEmpty()) {
			 return 1;
		}
		Regions region= (Regions)list.get(0);
		return region.getRegion_id();
		
	}

	
	/**
	 * 从地区出提取出省
	 * @param country
	 * @return
	 */
	private  String getDiqu(String  country){
		
		int pos = country.indexOf("省");
		if(pos==-1){
			pos = country.indexOf("市");
		}
		
		if(pos==-1){
			pos = country.indexOf("区");
		}
		if(pos==-1){
			return "北京";
		}
		country = country.substring(0,  pos );
		
		//默认为北京
		if(StringUtil.isEmpty(country)){
			country="北京";
		}
		return country;
	}
	
 
	
	private String getRemoteHost(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String ip = request.getHeader("x-forwarded-for");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getRemoteAddr();
		}
		return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
		}
	
	@Override
	public void updateMemberAddress(long memberid,Integer addr_id) {
		this.baseDaoSupport.execute("update es_member_address set def_addr=0 where member_id=?", memberid);
		this.baseDaoSupport.execute("update es_member_address set def_addr=1 where addr_id=?", addr_id);
	}



}
