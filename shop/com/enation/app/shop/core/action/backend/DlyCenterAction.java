package com.enation.app.shop.core.action.backend;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.model.DlyCenter;
import com.enation.app.shop.core.service.IDlyCenterManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;
/**
 * 发货信息 Action
 * @author LiFenLong 2014-4-1;4.0版本改造   
 *
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("dlyCenter")
@Results({
	@Result(name="edit", type="freemarker", location="/shop/admin/dlyCenter/edit.html"),
	@Result(name="add", type="freemarker", location="/shop/admin/dlyCenter/add.html"),
	@Result(name="list", type="freemarker", location="/shop/admin/dlyCenter/list.html") 
})
public class DlyCenterAction extends WWAction {
	
	private IDlyCenterManager dlyCenterManager;
	private IRegionsManager regionsManager;
	
	private DlyCenter dlyCenter;
	private Integer dlyCenterId;
	private Integer[] dly_center_id;
	private List<DlyCenter> list;
	private List provinceList;
	private List cityList;
	private List regionList;
	
	/**
	 * 显示发货信息添加页
	 * @author xulipeng
	 * @param provinceList 省列表,List
	 * @return 发货信息添加页
	 * 2014年4月1日17:29:02
	 */
	public String add(){
		provinceList = this.regionsManager.listProvince();
		return "add";
	}
	/**
	 * 显示发货信息修改页
	 * @param dlyCenterId 发货信息Id,Integer
	 * @param listProvince 省列表,List
	 * @param cityList 城市列表,List
	 * @param regionList 地区列表,List
	 * @return 发货信息修改页
	 */
	public String edit(){
		dlyCenter = dlyCenterManager.get(dlyCenterId);
		provinceList = this.regionsManager.listProvince();
		if (dlyCenter.getProvince_id() != null) {
			cityList = this.regionsManager.listCity(dlyCenter.getProvince_id());
		}
		if (dlyCenter.getCity_id() != null) {
			regionList = this.regionsManager.listRegion(dlyCenter.getCity_id());
		}
		return "edit";
	}
	/**
	 * 跳转发货信息列表页
	 * @return 发货信息列表页
	 */
	public String list(){
		return "list";
	}
	/**
	 * 获取发货信息列表json
	 * @param list 发货信息列表
	 * @return 获取发货信息列表json
	 */
	public String listJson(){
		list = dlyCenterManager.list();
		this.showGridJson(list);
		return JSON_MESSAGE;
	}
	/**
	 * 删除发货信息
	 * @param dly_center_id 发货信息Id
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	public String delete(){
		try {
			this.dlyCenterManager.delete(dly_center_id);
			this.showSuccessJson("发货信息删除成功");
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			this.showErrorJson("发货信息删除失败"+e.getMessage());

		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 添加发货信息
	 * @author xulipeng
	 * 2014年4月1日17:28:35
	 * @param province 省,String
	 * @param city 城市,String
	 * @param region 地区,String
	 * @param province_id 省Id,Integer
	 * @param city_id 城市Id,Integer
	 * @param region_id 地区Id,Integer
	 * @param dlyCenter 发货信息对象,DlyCenter
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	public String saveAdd(){
		try{
			
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			String province = request.getParameter("province");
			String city = request.getParameter("city");
			String region = request.getParameter("region");
			
			String province_id = request.getParameter("province_id");
			String city_id = request.getParameter("city_id");
			String region_id = request.getParameter("region_id");
			
			
			dlyCenter.setProvince(province);
			dlyCenter.setCity(city);
			dlyCenter.setRegion(region);
			
			if(!StringUtil.isEmpty(province_id)){
				dlyCenter.setProvince_id( StringUtil.toInt(province_id,true));
			}
			
			if(!StringUtil.isEmpty(city_id)){
				dlyCenter.setCity_id(StringUtil.toInt(city_id,true));
			}
			
			if(!StringUtil.isEmpty(province_id)){
				dlyCenter.setRegion_id(StringUtil.toInt(region_id,true));
			}
			
			dlyCenterManager.add(dlyCenter);
			this.showSuccessJson("发货信息添加成功");
			
		}catch(Exception e){
			e.printStackTrace();
			this.showErrorJson("发货信息添加失败");
			logger.error("发货信息添加失败", e);
		}
		return JSON_MESSAGE;
	}
	/**
	 * 修改发货信息
	 * @author xulipeng
	 * @param province 省,String
	 * @param city 城市,String
	 * @param region 地区,String
	 * @param province_id 省Id,Integer
	 * @param city_id 城市Id,Integer
	 * @param region_id 地区Id,Integer
	 * @param dlyCenter 发货信息对象,DlyCenter
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	public String saveEdit(){
		try{
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			String province = request.getParameter("province");
			String city = request.getParameter("city");
			String region = request.getParameter("region");
			
			String province_id = request.getParameter("province_id");
			String city_id = request.getParameter("city_id");
			String region_id = request.getParameter("region_id");
			
			
			dlyCenter.setProvince(province);
			dlyCenter.setCity(city);
			dlyCenter.setRegion(region);
			
			if(!StringUtil.isEmpty(province_id)){
				dlyCenter.setProvince_id( StringUtil.toInt(province_id,true));
			}
			
			if(!StringUtil.isEmpty(city_id)){
				dlyCenter.setCity_id(StringUtil.toInt(city_id,true));
			}
			
			if(!StringUtil.isEmpty(province_id)){
				dlyCenter.setRegion_id(StringUtil.toInt(region_id,true));
			}
			
			dlyCenterManager.edit(dlyCenter);
			this.showSuccessJson("发货信息修改成功");
			
		}catch(Exception e){
			e.printStackTrace();
			this.showSuccessJson("发货信息修改失败");
			logger.error("发货信息修改失败", e);
		}
		return JSON_MESSAGE;
	}

	public IDlyCenterManager getDlyCenterManager() {
		return dlyCenterManager;
	}

	public void setDlyCenterManager(IDlyCenterManager dlyCenterManager) {
		this.dlyCenterManager = dlyCenterManager;
	}

	public DlyCenter getDlyCenter() {
		return dlyCenter;
	}

	public void setDlyCenter(DlyCenter dlyCenter) {
		this.dlyCenter = dlyCenter;
	}

	public Integer getDlyCenterId() {
		return dlyCenterId;
	}

	public void setDlyCenterId(Integer dlyCenterId) {
		this.dlyCenterId = dlyCenterId;
	}

	public Integer[] getDly_center_id() {
		return dly_center_id;
	}

	public void setDly_center_id(Integer[] dly_center_id) {
		this.dly_center_id = dly_center_id;
	}

	public List<DlyCenter> getList() {
		return list;
	}

	public void setList(List<DlyCenter> list) {
		this.list = list;
	}

	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}

	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}

	public List getProvinceList() {
		return provinceList;
	}

	public void setProvinceList(List provinceList) {
		this.provinceList = provinceList;
	}

	public List getCityList() {
		return cityList;
	}

	public void setCityList(List cityList) {
		this.cityList = cityList;
	}

	public List getRegionList() {
		return regionList;
	}

	public void setRegionList(List regionList) {
		this.regionList = regionList;
	}

}
