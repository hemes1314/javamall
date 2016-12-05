package com.enation.app.b2b2c.core.action.backend.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IStoreLevelManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.model.Member;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.StringUtil;
@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="store_list",type="freemarker", location="/b2b2c/admin/store/store_list.html"),
	 @Result(name="audit_list",type="freemarker", location="/b2b2c/admin/store/audit_list.html"),
	 @Result(name="license_list",type="freemarker", location="/b2b2c/admin/store/license_list.html"),
	 @Result(name="disStore_list",type="freemarker", location="/b2b2c/admin/store/disStore_list.html"),
	 @Result(name="edit",type="freemarker", location="/b2b2c/admin/store/store_edit.html"),
	 @Result(name="add",type="freemarker", location="/b2b2c/admin/store/store_add.html"),
	 @Result(name="opt",type="freemarker", location="/b2b2c/admin/store/opt_member.html"),
	 @Result(name="pass",type="freemarker", location="/b2b2c/admin/store/pass.html"),
	 @Result(name="auth_list",type="freemarker", location="/b2b2c/admin/store/auth_list.html")
})
@Action("store")
/**
 * 店铺管理
 * @author LiFenLong
 *
 */
public class StoreAction extends WWAction{
	private IStoreLevelManager storeLevelManager;
	private IStoreManager storeManager;
	private IStoreMemberManager storeMemberManager;
	private Map other;
	private Integer disabled; 
	private Integer storeId;
	private Store store;
	private List level_list;
	
	private Long member_id;
	private Integer pass;
	private Integer name_auth;
	private Integer store_auth;
	private String storeName;
	
	private String uname;
	private String password;
	private Integer assign_password;
	
	private Double commission;

	private Double wine_commission;//葡萄酒佣金比例
	private Double chinese_spirits_commission;//白酒佣金比例
	private Double foreign_spirits_commission;//洋酒佣金比例
	private Double beer_commission;//啤酒佣金比例
	private Double other_spirits_commission;//黄酒/养生酒佣金比例oreign
	private Double around_commission;//酒周边佣金比例
	
	private Integer type; // 1.审核店铺 2.审核列表 chenzhongwei add
	
    public Double getAround_commission() {
        return around_commission;
    }
    
    public void setAround_commission(Double around_commission) {
        this.around_commission = around_commission;
    }
    /**
	 * 店铺列表
	 * @return
	 */
	public String store_list(){
		return "store_list";
	}
	/**
	 * 开店申请
	 * @return
	 */
	public String audit_list(){
		return "audit_list";
	}
	/**
	 * 店铺认证审核列表
	 * @return
	 */
	public String license_list(){
		return "license_list";
	}
	/**
	 * 禁用店铺列表
	 * @return
	 */
	public String disStore_list(){
		return "disStore_list";
	}
	/**
	 * 审核店铺
	 * @return
	 */
	public String pass(){
		store= this.storeManager.getStore(storeId);
		if(store.getName_auth()==2 ||store.getName_auth()==0){
			store.setId_img(UploadUtil.replacePath(store.getId_img()));
		}
		if(store.getStore_auth()==2 ||store.getStore_auth()==0){
			store.setLicense_img( UploadUtil.replacePath(store.getLicense_img()));
		}
		other=new HashMap();
        other.put("store", store);
        other.put("type", type);
		return "pass";
	}
	public String store_listJson(){
		other=new HashMap();
		other.put("disabled", disabled);
		other.put("name", storeName);
		this.showGridJson(storeManager.store_list(other,disabled,this.getPage(),this.getPageSize()));
		return this.JSON_MESSAGE;
	}
	/**
	 * 审核通过
	 * @return
	 */
	public String audit_pass(){
		try {
			storeManager.audit_pass(member_id, storeId, pass, name_auth, store_auth,commission,wine_commission,chinese_spirits_commission,foreign_spirits_commission,beer_commission,other_spirits_commission,around_commission) ;
			this.showSuccessJson("操作成功");
		} catch (Exception e) {
			this.showErrorJson("审核失败");
			this.logger.error("操作失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 禁用店铺
	 * @return
	 */
	public String disStore(){
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson(EopSetting.DEMO_SITE_TIP);
			return this.JSON_MESSAGE;
		}
		
		try {
			storeManager.disStore(storeId);
			this.showSuccessJson("店铺禁用成功");
		} catch (Exception e) {
			this.showErrorJson("店铺禁用失败");
			this.logger.error("店铺禁用失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 店铺恢复使用
	 * @return
	 */
	public String useStore(){
		try {
			storeManager.useStore(storeId);
			this.showSuccessJson("店铺恢复使用成功");
		} catch (Exception e) {
			this.showErrorJson("店铺恢复使用失败");
			this.logger.error("店铺恢复使用失败"+e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 添加店铺
	 * @return
	 */
	public String save(){
		try {
			store=new Store();
			store = this.assign();
			this.storeManager.save(store);
			this.showSuccessJson("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("保存失败");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 注册店铺
	 * @return
	 */
	public String registStore() {
		try {
		    HttpServletRequest request = this.getRequest();
			store = new Store();
			List<Store> checkStore = storeManager.getStoreByMemberName(request.getParameter("member_name"));
			
			if (checkStore.size() > 0) {
			    this.showErrorJson("该用户已有店铺");
			    return JSON_MESSAGE;
			}
			
			store = this.assign(); // 设置商店信息
			Member member = this.assignMem(); // 设置会员信息
			member.setPassword("123456"); // 会员默认密码
			
			// 注册商店,同时注册会员
			this.storeManager.registStore(store, member);
			
			this.showSuccessJson("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("保存失败");
		}
		return JSON_MESSAGE;
	}
	
	//修改店铺
	public String edit(){
		store = this.storeManager.getStore(storeId);
		/*
		 * add by linyang 判断图片是否为null 修正页面无法呈现问题
		 */
		if(store.getLicense_img() == null)
		    store.setLicense_img("");
		if(store.getId_img()==null)
		    store.setId_img("");    
		level_list=storeLevelManager.storeLevelList();
		
		return "edit";
	}
	/**
	 * 修改店铺信息
	 * @return
	 */
	public String saveEdit(){
		try {
		   
			store = this.storeManager.getStore(storeId);
			//通过新店铺名称查询店铺记录 chenzhongwei add
			HttpServletRequest request = this.getRequest();
			List<Store> list = this.storeManager.getStoreByStorename(request.getParameter("store_name"));
            if(list != null && list.size() > 0) {
                //防止同一个店铺再次修改被拦截  chenzhongwei add
                Store store = list.get(0);
                if(store.getStore_id() != storeId) {
                    this.showErrorJson("该店名已经被使用!");
                    return JSON_MESSAGE;
                }
            }
			Integer disable= store.getDisabled();
			store = this.assign();
			//判断店铺状态 更改店铺状态
			if(disable!=store.getDisabled()){
				if(store.getDisabled()==1){
					storeManager.useStore(storeId);
				}else{
					storeManager.disStore(storeId);
				}
			}
			this.storeManager.editStoreInfo(store);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败，请稍后重试！");
		}
		return JSON_MESSAGE;
	}
	/**
	 * 获取会员账号信息
	 * @return
	 */
	private Member assignMem(){
		HttpServletRequest request = this.getRequest();
		Member member = new Member();
		member.setUname(request.getParameter("member_name"));
	    member.setNickname(request.getParameter("member_name"));
	    member.setName(request.getParameter("member_name"));
		member.setPassword(request.getParameter("mem_pwd"));
		return member;
	}
	/**
	 * 获取店铺信息
	 * @return
	 */
	private Store assign(){
		HttpServletRequest request = this.getRequest();

		store.setMember_name(request.getParameter("member_name"));
		store.setId_number(request.getParameter("id_number"));
		store.setStore_name(request.getParameter("store_name"));
		store.setStore_type(NumberUtils.toInt((String)request.getParameter("store_type")));
		
		//店铺地址信息
		store.setStore_provinceid(NumberUtils.toInt((String)request.getParameter("store_province_id")));	//店铺省ID
		store.setStore_cityid(NumberUtils.toInt((String)request.getParameter("store_city_id")));			//店铺市ID
		store.setStore_regionid(NumberUtils.toInt((String)request.getParameter("store_region_id")));		//店铺区ID
		
		store.setStore_province(request.getParameter("store_province"));	//店铺省
		store.setStore_city(request.getParameter("store_city"));			//店铺市
		store.setStore_region(request.getParameter("store_region"));		//店铺区
		store.setAttr(request.getParameter("attr"));						//店铺详细地址
		//店铺银行信息
		store.setBank_account_name(request.getParameter("bank_account_name")); 		//银行开户名   
		store.setBank_account_number(request.getParameter("bank_account_number")); 	//公司银行账号
		store.setBank_name(request.getParameter("bank_name")); 						//开户银行支行名称
		store.setBank_code(request.getParameter("bank_code")); 						//支行联行号
		
		store.setBank_provinceid(NumberUtils.toInt((String)request.getParameter("bank_province_id"))); //开户银行所在省Id
		store.setBank_cityid(NumberUtils.toInt((String)request.getParameter("bank_city_id")));		  //开户银行所在市Id
		store.setBank_regionid(NumberUtils.toInt((String)request.getParameter("bank_region_id")));    //开户银行所在区Id
		
		store.setBank_province(request.getParameter("bank_province"));	//开户银行所在省
		store.setBank_city(request.getParameter("bank_city"));			//开户银行所在市
		store.setBank_region(request.getParameter("bank_region"));		//开户银行所在区
 		
		store.setAttr(request.getParameter("attr"));
		store.setZip(request.getParameter("zip"));
		store.setTel(request.getParameter("tel"));
		
		store.setCommission(commission);
		store.setWine_commission(wine_commission);
		store.setChinese_spirits_commission(chinese_spirits_commission);
		store.setForeign_spirits_commission(foreign_spirits_commission);
		store.setBeer_commission(beer_commission);
		store.setOther_spirits_commission(other_spirits_commission);
		store.setAround_commission(around_commission);

		store.setStore_level(1);
		store.setDisabled(NumberUtils.toInt((String)request.getParameter("disabled"),2));
		
		//add by linyang 只有上传了图片，才改变图片
		if(!request.getParameter("id_img").equals(""))
		{
		   store.setId_img(request.getParameter("id_img"));				// 身份证照片
		}
		if(!request.getParameter("license_img").equals(""))
		{
		   store.setLicense_img(request.getParameter("license_img"));		// 营业执照照片
		}
		
		
		//判断是否含有小区
		if(request.getParameter("community_id")!=null){
			store.setCommunity_id(NumberUtils.toInt(request.getParameter("community_id")));
			store.setCommunity(request.getParameter("community"));
		}
		return store;
	}
	
	/**
	 * 新增店铺验证用户
	 * @return
	 */
	public String opt(){
		return "opt";
	}
	/**
	 * 验证用户 
	 * @param uname 会员名称
	 * @param password 密码
	 * @param assign_password 是否验证密码 
	 * @return
	 */
	public String optMember(){
		try {
			StoreMember storeMember= storeMemberManager.getMember(uname);
			//检测是否为新添加的会员
			if(storeMember.getIs_store()==null){
				this.showSuccessJson(uname);
				return this.JSON_MESSAGE;
			}
			//判断用户是否已经拥有店铺
			if(storeMember.getIs_store()==1){
				this.showErrorJson("会员已拥有店铺");
				return this.JSON_MESSAGE;
			}
			//验证会员密码
			if(assign_password!=null&&assign_password==1){
				if(!storeMember.getPassword().equals(StringUtil.md5(password))){
					this.showErrorJson("密码不正确");
					return this.JSON_MESSAGE;
				}
			}
			if(storeMember.getIs_store()==-1){
				this.showSuccessJson(uname);
			}else{
				this.showSuccessJson("2");
			}
		} catch (Exception e) {
			this.showErrorJson("没有此用户");
		}
		return this.JSON_MESSAGE;
		
	}
	public String add(){
		level_list=storeLevelManager.storeLevelList();
		return "add";
	}
	/**
	 * 跳转到申请信息页面
	 * @return
	 */
	public String auth_list(){
		return "auth_list";
	}
	public String auth_listJson(){
		this.showGridJson(storeManager.auth_list(other, disabled, this.getPage(), this.getPageSize()));
	    //this.showGridJson(storeManager.list(this.getPage(), this.getPageSize()));
		return this.JSON_MESSAGE;
	}
	/**
	 * 审核店铺认证
	 * @param storeId 店铺Id
	 * @param name_auth 店主认证
	 * @param store_auth 店铺认证
	 */
	public String auth_pass(){
		try{
			storeManager.auth_pass(storeId, name_auth, store_auth);
			this.showSuccessJson("操作成功");
		}catch(Exception e){
			this.showErrorJson("操作失败");
			this.logger.error("审核店铺认证失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	public IStoreLevelManager getStoreLevelManager() {
		return storeLevelManager;
	}
	public void setStoreLevelManager(IStoreLevelManager storeLevelManager) {
		this.storeLevelManager = storeLevelManager;
	}
	public IStoreManager getStoreManager() {
		return storeManager;
	}
	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
	public Map getOther() {
		return other;
	}
	public void setOther(Map other) {
		this.other = other;
	}
	public Integer getDisabled() {
		return disabled;
	}
	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}
	public Integer getStoreId() {
		return storeId;
	}
	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}
	public List getLevel_list() {
		return level_list;
	}
	public void setLevel_list(List level_list) {
		this.level_list = level_list;
	}
	public Long getMember_id() {
		return member_id;
	}
	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}
	public Integer getPass() {
		return pass;
	}
	public void setPass(Integer pass) {
		this.pass = pass;
	}
	public Integer getName_auth() {
		return name_auth;
	}
	public void setName_auth(Integer name_auth) {
		this.name_auth = name_auth;
	}
	public Integer getStore_auth() {
		return store_auth;
	}
	public void setStore_auth(Integer store_auth) {
		this.store_auth = store_auth;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getAssign_password() {
		return assign_password;
	}
	public void setAssign_password(Integer assign_password) {
		this.assign_password = assign_password;
	}
	public Double getCommission() {
		return commission;
	}
	public void setCommission(Double commission) {
		this.commission = commission;
	}

	public Double getWine_commission() {
		return wine_commission;
	}

	public void setWine_commission(Double wine_commission) {
		this.wine_commission = wine_commission;
	}

	public Double getChinese_spirits_commission() {
		return chinese_spirits_commission;
	}

	public void setChinese_spirits_commission(Double chinese_spirits_commission) {
		this.chinese_spirits_commission = chinese_spirits_commission;
	}

	public Double getForeign_spirits_commission() {
		return foreign_spirits_commission;
	}

	public void setForeign_spirits_commission(Double foreign_spirits_commission) {
		this.foreign_spirits_commission = foreign_spirits_commission;
	}

	public Double getBeer_commission() {
		return beer_commission;
	}

	public void setBeer_commission(Double beer_commission) {
		this.beer_commission = beer_commission;
	}

	public Double getOther_spirits_commission() {
		return other_spirits_commission;
	}

	public void setOther_spirits_commission(Double other_spirits_commission) {
		this.other_spirits_commission = other_spirits_commission;
	}

    public Integer getType() {
        return type;
    }

    
    public void setType(Integer type) {
        this.type = type;
    }
    
}
