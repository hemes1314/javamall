package com.enation.app.shop.core.action.backend;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Regions;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.model.DlyArea;
import com.enation.app.shop.core.service.IAreaManager;
import com.enation.framework.action.WWAction;

/**
 * @author lzf<br/>
 * 2010-3-17 上午10:07:46<br/>
 * version 1.0<br/>
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("area")
@Results({
	@Result(name="add_area",  location="/shop/admin/setting/area_add.jsp"),
	@Result(name="edit_area",  location="/shop/admin/setting/area_edit.jsp"),
	@Result(name="list_area",  location="/shop/admin/setting/area_list.jsp"),
	@Result(name="list_regions", type="freemarker", location="/shop/admin/setting/site_area.html")
})

public class AreaAction extends WWAction {
	
	/*
	 * 属性
	 */
	private IRegionsManager regionsManager;
	private Regions regions;
	private List provinceList;
	private List cityList;
	private List regionList;
	private int province_id;
	private int city_id;
	private Integer regionid;
	/*
	 * 方法
	 */
	public String add_region(){
		return "add_region";
	}
	
	public String edit_region(){
		return "eidt_region";
	}
	
	public String list_province(){
		provinceList = regionsManager.listProvince();
		return "list_province";
	}
	
	public String list_city(){
		cityList = regionsManager.listCity(province_id);
		return "list_city";
	}
	
	public String list_region(){
		regionList = regionsManager.listRegion(city_id);
		return "list_region";
	}
	
	public String listChildren(){
		String s= this.regionsManager.getChildrenJson(regionid);
		this.json = s.replace("local_name", "text").replace("p_region_id", "id");
		return JSON_MESSAGE;
	}
	
	public String getRegionsList(){
		regionList = this.regionsManager.listChildren(regionid);
		return "list_regions";
	}
	
	/*
	 * 属性公开
	 */
	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}

	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}

	public Regions getRegions() {
		return regions;
	}

	public void setRegions(Regions regions) {
		this.regions = regions;
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

	public int getProvince_id() {
		return province_id;
	}

	public void setProvince_id(int provinceId) {
		province_id = provinceId;
	}

	public int getCity_id() {
		return city_id;
	}

	public void setCity_id(int cityId) {
		city_id = cityId;
	}

//////////////////////////////////////////////////////
	
	
	private Integer area_id;
	private String name;
	private IAreaManager areaManager;
	private String order;
	private String id;
	private DlyArea area;
	
	public String add_area(){
		return "add_area";
	}
	
	public String edit_area(){
		area = this.areaManager.getDlyAreaById(area_id);
		return "edit_area";
	}
	
	public String list(){
		this.webpage = this.areaManager.pageArea(order, this.getPage(), this.getPageSize());
		return "list_area";
	}
	
	public String delete(){
		try {
			this.areaManager.delete(id);
			this.json = "{'result':0,'message':'删除成功'}";
		} catch (RuntimeException e) {
			this.json = "{'result':1,'message':'删除失败'}";
		}
		return this.JSON_MESSAGE;
	}
	
	public String saveAdd(){
		areaManager.saveAdd(name);
		this.msgs.add("地区保存成功");
		this.urls.put("地区列表", "area!list.do" ); 
		return this.MESSAGE;
	}
	
	
	public String saveEdit(){
		areaManager.saveEdit(area_id,name);
		this.msgs.add("地区保存成功");
		this.urls.put("地区列表", "area!list.do" ); 		
		return this.MESSAGE;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	 

	public IAreaManager getAreaManager() {
		return areaManager;
	}


	public void setAreaManager(IAreaManager areaManager) {
		this.areaManager = areaManager;
	}


	public Integer getArea_id() {
		return area_id;
	}

	public void setArea_id(Integer area_id) {
		this.area_id = area_id;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DlyArea getArea() {
		return area;
	}

	public void setArea(DlyArea area) {
		this.area = area;
	}

	public Integer getRegionid() {
		return regionid;
	}

	public void setRegionid(Integer regionid) {
		this.regionid = regionid;
	}
	
}
