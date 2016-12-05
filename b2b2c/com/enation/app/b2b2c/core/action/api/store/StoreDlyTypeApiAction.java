package com.enation.app.b2b2c.core.action.api.store;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.StoreDlyType;
import com.enation.app.b2b2c.core.model.StoreTemlplate;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreDlyTypeManager;
import com.enation.app.b2b2c.core.service.IStoreTemplateManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.model.support.DlyTypeConfig;
import com.enation.app.shop.core.model.support.TypeAreaConfig;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 店铺物流模板api
 * 
 * @author xulipeng
 * 
 */
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("dlyType")
@Results({ @Result(name = "edit", type = "freemarker", location = "/themes/default/b2b2c/storesite/navication_edit.html") })
public class StoreDlyTypeApiAction extends WWAction {

	private IStoreDlyTypeManager storeDlyTypeManager;
	private IStoreTemplateManager storeTemplateManager;
	private IStoreMemberManager storeMemberManager;
	private IRegionsManager regionsDbManager;
	private StoreDlyType storeDlyType;
	private DlyTypeConfig typeConfig;
	private Integer pycount;
	private Integer kdcount;
	private Integer yzcount;
	private String dlyname;
	private Integer tempid;

	/**
	 * 添加物流工具
	 * 
	 * @param storeMember
	 *            店铺会员 ,StoreMember
	 * @param store_id
	 *            店铺Id,Integer
	 * @param dlyname
	 *            模板名称,String
	 * @param tplType
	 *            类型,String[]
	 * @param templateid
	 *            模板Id,Integer
	 * @return 返回json串 result 为1表示调用成功0表示失败
	 */
	public String add() {

		StoreMember storeMember = storeMemberManager.getStoreMember();
		int i = this.storeTemplateManager.getStoreTemlpateByName(dlyname,
				storeMember.getStore_id());

		Integer store_id = storeMember.getStore_id();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String[] tplType = request.getParameterValues("tplType");

		if (tplType == null) {
			this.showErrorJson("未选择配送方式");
			return JSON_MESSAGE;
		}

		if (i == 0) {

			StoreTemlplate storeTemlplate = new StoreTemlplate();
			storeTemlplate.setName(dlyname);
			storeTemlplate.setStore_id(storeMember.getStore_id());
			storeTemlplate.setDef_temp(0);
			Integer templateid = this.storeTemplateManager.add(storeTemlplate);

			for (String tpl : tplType) {
				storeDlyType = new StoreDlyType();
				storeDlyType.setStore_id(store_id);
				storeDlyType.setTemplate_id(templateid);
				storeDlyType.setIs_same(0);

				// 平邮
				if (Integer.valueOf(tpl) == 1) {
					storeDlyType.setName("平邮");
					addType(request, "py", pycount);
				}

				// 快递
				if (Integer.valueOf(tpl) == 2) {
					storeDlyType.setName("快递");
					addType(request, "kd", kdcount);
				}

				// 邮政
				if (Integer.valueOf(tpl) == 3) {
					storeDlyType.setName("邮政");
					addType(request, "yz", yzcount);
				}
			}

			/**
			 * 20160929：此处为什么会注释掉仅有一个模板时不设为默认模板的处理.
			 */
//			// 如果只有一个模板、设置此模板为默认模板
//			Integer temp_id = this.storeTemplateManager
//					.getDefTempid(storeMember.getStore_id());
//			if (temp_id == null) {
//				//this.storeTemplateManager.setDefTemp(templateid,
//						//storeMember.getStore_id());
//			}
			this.showSuccessJson("添加成功！");
		} else {
			this.showErrorJson("添加失败,模板名称已存在!");
		}

		return JSON_MESSAGE;
	}

	/**
	 * 修改物流工具
	 * 
	 * @param storeMember
	 *            店铺会员 ,StoreMember
	 * @param store_id
	 *            店铺Id,Integer
	 * @param tplType
	 *            类型,String[]
	 * @param storeTemlplate
	 *            店铺物流工具,StoreTemlplate
	 * @param dlyname
	 *            模板名称,String
	 * @param pycount
	 * @param kdcount
	 * @param yzcount
	 * @return 返回json串 result 为1表示调用成功0表示失败
	 */
	public String update() {
		StoreMember storeMember = storeMemberManager.getStoreMember();
		Integer store_id = storeMember.getStore_id();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String[] tplType = request.getParameterValues("tplType");

		StoreTemlplate storeTemlplate = new StoreTemlplate();
		storeTemlplate.setStore_id(storeMember.getStore_id());
		storeTemlplate.setName(dlyname);
		storeTemlplate.setId(tempid);
		
		//检查当前模板是否为默认模板
		int result = this.storeTemplateManager.checkIsDef(tempid);
		
		//如果当前模板是默认模板 result==1
		if(result == 1){
		    storeTemlplate.setDef_temp(1);
		}else{
		    storeTemlplate.setDef_temp(0);
		}
		
		this.storeTemplateManager.edit(storeTemlplate);
		Integer templateid = tempid;
		this.storeDlyTypeManager.del_dlyType(templateid);

		for (String tpl : tplType) {
			storeDlyType = new StoreDlyType();
			storeDlyType.setStore_id(store_id);
			storeDlyType.setTemplate_id(templateid);
			storeDlyType.setIs_same(0);

			// 平邮
			if (Integer.valueOf(tpl) == 1) {
				storeDlyType.setName("平邮");
				addType(request, "py", pycount);
			}

			// 快递
			if (Integer.valueOf(tpl) == 2) {
				storeDlyType.setName("快递");
				addType(request, "kd", kdcount);
			}

			// 邮政
			if (Integer.valueOf(tpl) == 3) {
				storeDlyType.setName("邮政");
				addType(request, "yz", yzcount);
			}
		}
		this.showSuccessJson("修改成功");
		return JSON_MESSAGE;
	}

	/**
	 * 添加配送方式
	 * 
	 * @param tpl
	 *            类型,
	 * @param count
	 * @param firstunit
	 * @param firstmoney
	 * @param continueunit
	 * @param continuemoney
	 * @param config
	 *            DlyTypeConfig
	 * @param areaConfig
	 *            TypeAreaConfig
	 */
	private void addType(HttpServletRequest request, String tpl, Integer count) {

		String firstunit = request.getParameter("default_firstunit_" + tpl);
		String continueunit = request.getParameter("default_continueunit_"
				+ tpl);
		String firstmoney = request.getParameter("default_firstmoney_" + tpl);
		String continuemoney = request.getParameter("default_continueprice_"
				+ tpl);

		DlyTypeConfig config = new DlyTypeConfig();
		config.setFirstunit(Integer.valueOf(firstunit)); // 首重
		config.setContinueunit(Integer.valueOf(continueunit == null ? "0"
				: continueunit)); // 续重

		config.setFirstprice(Double.valueOf(firstmoney)); // 首重费用
		config.setContinueprice(Double.valueOf(continuemoney)); // 续重费用

		config.setIs_same(0); // 店铺都是指定地区的配送方式
		config.setDefAreaFee(1); // 店铺都有默认费用设置
		config.setUseexp(0); // 店铺都不启动公式

		TypeAreaConfig[] configArray = new TypeAreaConfig[count];
		// 指定地区

		for (int i = 1; i <= count; i++) {
			TypeAreaConfig areaConfig = new TypeAreaConfig();
			String firstprice = request.getParameter("express_" + tpl
					+ "_firstmoney_" + i);
			String continueprice = request.getParameter("express_" + tpl
					+ "_continuemoney_" + i);
			String areaids = request.getParameter("express_" + tpl
					+ "_areaids_" + i);
			String areanames = request.getParameter("express_" + tpl
					+ "_areanames_" + i);

			if (firstprice != null && continueprice != null && areaids != null
					&& areanames != null) {

				areaConfig.setFirstprice(Double.valueOf(firstprice)); // 首重费用
				areaConfig.setFirstunit(Integer.valueOf(firstunit)); // 首重重量

				areaConfig.setContinueprice(Double.valueOf(continueprice)); // 续重费用
				areaConfig.setContinueunit(Integer
						.valueOf(continueunit == null ? "0" : continueunit)); // 续重重量
				areaConfig.setUseexp(0);

				if (areaids != null) {
					
					if(areaids.endsWith(",")){
						areaids = areaids.substring(0,areaids.length() - 1).toString();
					}
					String[] areaid = areaids.split(",");
					//先增加2级地区
					StringBuffer areas = new StringBuffer();
					areas.append(areaids); 
					List tRegions ;
					for (String aid : areaid) {
						tRegions = new ArrayList();
						tRegions = regionsDbManager.listChildren(aid);
						for (int j = 0; j < tRegions.size(); j++) {
							areas.append(","+tRegions.get(j));
						}
					}
					areaConfig.setAreaId(areas.toString()); 
				}

				if (areanames != null ) {
					
					if(areanames.endsWith(",")){
						areaConfig.setAreaName(areanames.substring(0,areanames.length() - 1));
					}else{
						areaConfig.setAreaName(areanames);
					}
				}

				configArray[i - 1] = areaConfig;
			}
		}
		this.storeDlyTypeManager.add(storeDlyType, config, configArray);
	}

	// set get
	public IStoreDlyTypeManager getStoreDlyTypeManager() {
		return storeDlyTypeManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public void setStoreDlyTypeManager(IStoreDlyTypeManager storeDlyTypeManager) {
		this.storeDlyTypeManager = storeDlyTypeManager;
	}

	public StoreDlyType getStoreDlyType() {
		return storeDlyType;
	}

	public void setStoreDlyType(StoreDlyType storeDlyType) {
		this.storeDlyType = storeDlyType;
	}

	public DlyTypeConfig getTypeConfig() {
		return typeConfig;
	}

	public void setTypeConfig(DlyTypeConfig typeConfig) {
		this.typeConfig = typeConfig;
	}

	public Integer getPycount() {
		return pycount;
	}

	public void setPycount(Integer pycount) {
		this.pycount = pycount;
	}

	public Integer getKdcount() {
		return kdcount;
	}

	public void setKdcount(Integer kdcount) {
		this.kdcount = kdcount;
	}

	public Integer getYzcount() {
		return yzcount;
	}

	public void setYzcount(Integer yzcount) {
		this.yzcount = yzcount;
	}

	public String getDlyname() {
		return dlyname;
	}

	public void setDlyname(String dlyname) {
		this.dlyname = dlyname;
	}

	public IStoreTemplateManager getStoreTemplateManager() {
		return storeTemplateManager;
	}

	public void setStoreTemplateManager(
			IStoreTemplateManager storeTemplateManager) {
		this.storeTemplateManager = storeTemplateManager;
	}

	public Integer getTempid() {
		return tempid;
	}

	public void setTempid(Integer tempid) {
		this.tempid = tempid;
	}

	public IRegionsManager getRegionsDbManager() {
		return regionsDbManager;
	}

	public void setRegionsDbManager(IRegionsManager regionsDbManager) {
		this.regionsDbManager = regionsDbManager;
	}
 

}
