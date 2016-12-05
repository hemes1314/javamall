package com.enation.app.b2b2c.core.action.api.store;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
/**
 * 店铺管理API
 * @author LiFenLong
 *
 */
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("storeApi")
public class StoreApiAction extends WWAction{
	private Store store;
	
	private File id_img;
	private File license_img;
	private String id_imgFileName;
	private String license_imgFileName;
	private String id_number;
	
	private String fsid_img;
	private String fslicense_img;
	private String status_id_img;
	private String status_license_img;
	
	private String logo;
	private String storeName;
	
	private Integer store_id;
	private Integer store_auth;
	private Integer name_auth;
	private IStoreManager storeManager;
	private IStoreMemberManager storeMemberManager;
	private String description;
	
	/**
	 * 查看用户名是否重复
	 * @param storeName 店铺名称,String
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String checkStoreName(){
		if(this.storeManager.checkStoreName(storeName)){
			this.showErrorJson("店铺名称重复");
		}else{
			this.showSuccessJson("店铺名称可以使用");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 申请开店
	 * @param store 店铺信息,Store
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String apply(){
		try {
			if(null==storeMemberManager.getStoreMember()){
				this.showErrorJson("您没有登录不能申请开店");
			}else if(!storeManager.checkStore()){
				//添加店铺地址
				store=this.getStoreInfo();
				//暂时先将店铺等级定为默认等级，以后版本升级更改此处
				store.setStore_level(1);
				store.setStore_type(1);
				storeManager.apply(store);
				this.showSuccessJson("申请成功,请等待审核");
			}else{
				this.showErrorJson("您已经申请过了，请不要重复申请");
			}
		} catch (Exception e) {
			this.logger.error("申请失败:"+e);
			e.printStackTrace();
			this.showErrorJson("申请失败");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 重试申请开店
	 * @param store 店铺信息,Store
	 * @return
	 */
	public String reApply(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		try {
			if(null==storeMemberManager.getStoreMember()){
				this.showErrorJson("您没有登录不能申请开店");
			}else {
				//添加店铺地址
				store=this.getStoreInfo();
				//暂时先将店铺等级定为默认等级，以后版本升级更改此处
				store.setStore_id(store_id);
				store.setStore_level(1);
				storeManager.reApply(store);
				this.showSuccessJson("提交申请成功,请等待审核");
			}
		} catch (Exception e) {
			this.logger.error("申请失败:"+e);
			e.printStackTrace();
			this.showErrorJson("申请失败");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 获取店铺信息
	 * @param store
	 * @return Store
	 */
	private Store getStoreInfo(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Store store=new Store();
		store.setStore_name(request.getParameter("store_name"));		//店铺名称
		store.setZip(request.getParameter("zip"));					//邮编
		store.setTel(request.getParameter("tel"));					//联系方式
		store.setId_number(request.getParameter("id_number"));		//申请人身份证号
		store.setId_img(request.getParameter("store_id_img"));				//身份证证明
		store.setLicense_img(request.getParameter("store_license_img") );	//营业执照证明
		store.setName_auth(NumberUtils.toInt(request.getParameter("store_name_auth").toString()));    //身份证图片路径
		store.setStore_auth(NumberUtils.toInt(request.getParameter("store_store_auth").toString()));    //营业执照图片路径
		//店铺地址信息
		store.setStore_provinceid(NumberUtils.toInt(request.getParameter("store_province_id").toString()));    //店铺省ID
		store.setStore_cityid(NumberUtils.toInt(request.getParameter("store_city_id").toString()));            //店铺市ID
		store.setStore_regionid(NumberUtils.toInt(request.getParameter("store_region_id").toString()));        //店铺区ID
		
		store.setStore_province(request.getParameter("store_province"));	//店铺省
		store.setStore_city(request.getParameter("store_city"));			//店铺市
		store.setStore_region(request.getParameter("store_region"));		//店铺区
		store.setAttr(request.getParameter("attr"));						//店铺详细地址
		//店铺银行信息
		store.setBank_account_name(request.getParameter("bank_account_name")); 		//银行开户名   
		store.setBank_account_number(request.getParameter("bank_account_number")); 	//公司银行账号
		store.setBank_name(request.getParameter("bank_name")); 						//开户银行支行名称
		store.setBank_code(request.getParameter("bank_code")); 						//支行联行号

		store.setBank_provinceid(NumberUtils.toInt(request.getParameter("bank_province_id").toString())); //开户银行所在省Id
		store.setBank_cityid(NumberUtils.toInt(request.getParameter("bank_city_id").toString()));          //开户银行所在市Id
		store.setBank_regionid(NumberUtils.toInt(request.getParameter("bank_region_id").toString()));    //开户银行所在区Id
		
		store.setBank_province(request.getParameter("bank_province"));	//开户银行所在省
		store.setBank_city(request.getParameter("bank_city"));			//开户银行所在市
		store.setBank_region(request.getParameter("bank_region"));		//开户银行所在区
		
		//店铺佣金
		store.setCommission(0.0);
		
		//判断是否含有小区
		if(request.getParameter("community_id")!=null){
			store.setCommunity_id(NumberUtils.toInt(request.getParameter("community_id")));
			store.setCommunity(request.getParameter("community"));
		}
		return store;
	}
	/**
	 * 修改店铺设置
	 * @param store 店铺信息,Store
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String edit(){
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("store_id",request.getParameter("store_id"));
			map.put("attr", request.getParameter("attr"));
			map.put("zip", request.getParameter("zip"));
			map.put("tel", request.getParameter("tel"));
			map.put("qq", request.getParameter("qq"));
			map.put("description", description);              //修改详细得不到富文本编辑器得不到图片src信息。  whj 2015-06-18
			map.put("store_logo",request.getParameter("store_logo"));
			map.put("store_banner", request.getParameter("store_banner"));
			map.put("store_provinceid", NumberUtils.toInt(request.getParameter("province_id").toString()));
			map.put("store_cityid", NumberUtils.toInt(request.getParameter("city_id").toString()));
			map.put("store_regionid", NumberUtils.toInt(request.getParameter("region_id").toString()));
			map.put("store_province", request.getParameter("province"));
			map.put("store_city", request.getParameter("city"));
			map.put("store_region", request.getParameter("region"));
			this.storeManager.editStore(map);
			this.showSuccessJson("修改店铺信息成功");
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("修改店铺信息失败");
			this.logger.error("修改店铺信息失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 检测身份证
	 * @param id_number 身份证号
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String checkIdNumber(){
		int result=storeManager.checkIdNumber(id_number);
		if(result==0){
			this.showSuccessJson("身份证可以使用！");
		}else{
			this.showErrorJson("身份证已经存在！");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 修改店铺Logo
	 * @param logo Logo,String
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String editStoreLogo(){
		try {
			storeManager.editStoreOnekey("store_logo",logo);
			this.showSuccessJson("店铺Logo修改成功");
		} catch (Exception e) {
			this.logger.error("修改店铺Logo失败:"+e);
			this.showErrorJson("店铺Logo修改失败");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 提交店铺认证信息
	 * @param store_id 店铺Id,Integer
	 * @param fsid_img 身份证图片,String
	 * @param fslicense_img 营业执照图片,String
	 * @param store_auth 店铺认证,Integer
	 * @param name_auth 店主认证,Integer
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String store_auth(){
		try {

			   storeManager.saveStoreLicense(store_id, fsid_img, fslicense_img, store_auth, name_auth);
			   this.showSuccessJson("提交成功，等待审核");
		} catch (Exception e) {
			this.showErrorJson("提交失败，请重试");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 前台搜索框输入关键词的下拉
	 * @return
	 */
	public String words(){        
        try{
            String keyword = getRequest().getParameter("keyword");
            List<String> storeList = this.storeManager.getSearchWords(keyword);
            
            this.json= JsonMessageUtil.getListJson(storeList);
        }catch(Exception e){
            e.printStackTrace();
            this.showErrorJson("error");
        }
        return this.JSON_MESSAGE;
    }
	
	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getLogo() {
		return logo;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}
	public IStoreManager getStoreManager() {
		return storeManager;
	}
	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
	public File getId_img() {
		return id_img;
	}
	public void setId_img(File id_img) {
		this.id_img = id_img;
	}
	public File getLicense_img() {
		return license_img;
	}
	public void setLicense_img(File license_img) {
		this.license_img = license_img;
	}
	public String getId_imgFileName() {
		return id_imgFileName;
	}
	public void setId_imgFileName(String id_imgFileName) {
		this.id_imgFileName = id_imgFileName;
	}
	public String getLicense_imgFileName() {
		return license_imgFileName;
	}
	public void setLicense_imgFileName(String license_imgFileName) {
		this.license_imgFileName = license_imgFileName;
	}
	public String getId_number() {
		return id_number;
	}
	public void setId_number(String id_number) {
		this.id_number = id_number;
	}
	public String getFsid_img() {
		return fsid_img;
	}
	public void setFsid_img(String fsid_img) {
		this.fsid_img = fsid_img;
	}
	public String getFslicense_img() {
		return fslicense_img;
	}
	public void setFslicense_img(String fslicense_img) {
		this.fslicense_img = fslicense_img;
	}
	public String getStatus_id_img() {
		return status_id_img;
	}
	public void setStatus_id_img(String status_id_img) {
		this.status_id_img = status_id_img;
	}
	public String getStatus_license_img() {
		return status_license_img;
	}
	public void setStatus_license_img(String status_license_img) {
		this.status_license_img = status_license_img;
	}

	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}

	public Integer getStore_auth() {
		return store_auth;
	}

	public void setStore_auth(Integer store_auth) {
		this.store_auth = store_auth;
	}

	public Integer getName_auth() {
		return name_auth;
	}

	public void setName_auth(Integer name_auth) {
		this.name_auth = name_auth;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
