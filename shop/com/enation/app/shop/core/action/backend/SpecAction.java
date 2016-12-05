package com.enation.app.shop.core.action.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.spec.service.ISpecManager;
import com.enation.app.shop.component.spec.service.ISpecValueManager;
import com.enation.app.shop.core.model.SpecValue;
import com.enation.app.shop.core.model.Specification;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 规格ation
 * @author kingapex
 *2010-3-7下午06:50:20
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("spec")
@Results({
	@Result(name="input", type="freemarker", location="/shop/admin/spec/spec_input.html"),
	@Result(name="edit", type="freemarker", location="/shop/admin/spec/spec_edit.html"),
	@Result(name="list", type="freemarker", location="/shop/admin/spec/spec_list.html") 
})
public class SpecAction extends WWAction {
	
	private ISpecManager specManager;
	private ISpecValueManager specValueManager;
	private Integer specId;
	private Map specView;
	private List specList ;
	private List valueList;
	private Specification spec;
	private String[] valueArray;
	private String[] imageArray;
	private Integer[] valueIdArray;
	
	private Integer[] spec_id;
	private int valueid;
	
	/**
	 * 检测规格是否被使用
	 * @param spec_id 规格Id数组,Integer[]
	 * @return json
	 * result 1.使用.0.未使用
	 */
	public String checkUsed(){
        try {
            if(this.specManager.checkUsed(spec_id)){
                
                //规格已被使用
                this.json=JsonMessageUtil.getNumberJson("used", 1);
            }else{
                
                //规格未被使用
                this.json=JsonMessageUtil.getNumberJson("used", 0);
            }
            
        } catch (Exception e) {
            
            this.logger.error("检测规格使用情况出错",e);
            this.showErrorJson("检测规格使用情况出错");
            
        }
		return this.JSON_MESSAGE;
	}

	
	
	/**
	 * 检测某个规格值 是否被使用
	 * @param  valueid 规格Id
	 * @return json
	 * result 1.使用.0.未使用
	 */
	public String checkValueUsed(){
		
		boolean isused = this.specManager.checkUsed(valueid);
		
		if(isused){
			this.json ="{result:1}";
		}else{
			this.json ="{result:0}";
		}
		
		return this.JSON_MESSAGE;
		
	}
	/**
	 * 跳转至规格列表页
	 * @return 规格列表页
	 */
	public String list(){
		return "list";
	}
	/**
	 * 获取规格列表Json
	 * @author LiFenLong
	 * @return 规格列表Json
	 */
	public String listJson(){
		specList = specManager.list();
		this.showGridJson(specList);
		return JSON_MESSAGE;
	}
	/**
	 * 跳转至添加规格页面
	 * @return 添加规格页面
	 */
	public String add(){
		return this.INPUT;
	}
	/**
	 * 保存规格
	 * @param spec 规格,Specification
	 * @param valueList 规格值,List
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@SuppressWarnings("unchecked")
	public String saveAdd(){
		this.fillSpecValueList();
		
		try {
			this.specManager.add(spec, valueList);
			this.showSuccessJson("规格添加成功");
		} catch (Exception e) {
			this.showErrorJson("规格添加失败");
			logger.error("规格添加失败"+ e);
		}
		return JSON_MESSAGE;
	}
	/**
	 * 跳转至修改规格页面
	 * @param specId 规格Id,Integer
	 * @param specView 规格详细,Map
	 * @param valueList 规格值,List
	 * @return 修改规格页面
	 */
	public String edit(){
		specView = this.specManager.get(specId);
		this.valueList = this.specValueManager.list(specId);
		return "edit";
	}
	/**
	 * 保存修改规格
	 * @param spec 规格,Specification
	 * @param valueList 规格值,List
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String saveEdit(){
		this.fillSpecValueList();
		try {
			this.specManager.edit(spec, valueList);
			this.showSuccessJson("规格修改成功");
		} catch (Exception e) {
			this.showErrorJson("规格修改失败");
			logger.error("规格修改失败"+e);
		}
		return JSON_MESSAGE;
	}
	/**
	 * 整理规格List
	 * @return List<SpecValue>
	 */
	private List<SpecValue> fillSpecValueList(){
		valueList = new ArrayList<SpecValue>();
		
		if(valueArray!=null ){
			for(int i=0;i<valueArray.length;i++){
				String value =valueArray[i];
	
				SpecValue specValue = new SpecValue();
				specValue.setSpec_value_id(this.valueIdArray[i]);
				specValue.setSpec_value(value);
				if( imageArray!=null){
					String image = imageArray[i];
					if(image == null || image.equals("")) image ="/shop/admin/spec/image/spec_def.gif";
					else
					image  =UploadUtil.replacePath(image);
					specValue.setSpec_image(image);
				}else{
					specValue.setSpec_image( "/shop/admin/spec/image/spec_def.gif" );
				}
				valueList.add(specValue);
			}
		}
		return valueList;
	}
	/**
	 * 删除规格
	 * @param spec_id 规格Id数组,Integer[]
	 * @return
	 */
	public String delete(){
		try {
			this.specManager.delete(spec_id);
			this.showSuccessJson("规格删除成功");
		} catch (Exception e) {
			this.showErrorJson("规格删除失败");
			logger.error("规格删除失败"+e);
		}
		return this.JSON_MESSAGE;
	}
	
	public ISpecManager getSpecManager() {
		return specManager;
	}

	public void setSpecManager(ISpecManager specManager) {
		this.specManager = specManager;
	}

	public ISpecValueManager getSpecValueManager() {
		return specValueManager;
	}

	public void setSpecValueManager(ISpecValueManager specValueManager) {
		this.specValueManager = specValueManager;
	}

	public List getSpecList() {
		return specList;
	}

	public void setSpecList(List specList) {
		this.specList = specList;
	}

	public Specification getSpec() {
		return spec;
	}

	public void setSpec(Specification spec) {
		this.spec = spec;
	}

	public String[] getValueArray() {
		return valueArray;
	}

	public void setValueArray(String[] valueArray) {
		this.valueArray = valueArray;
	}

	public String[] getImageArray() {
		return imageArray;
	}

	public void setImageArray(String[] imageArray) {
		this.imageArray = imageArray;
	}

	

	public Map getSpecView() {
		return specView;
	}

	public void setSpecView(Map specView) {
		this.specView = specView;
	}

	public List getValueList() {
		return valueList;
	}

	public void setValueList(List valueList) {
		this.valueList = valueList;
	}

	

	public Integer getSpecId() {
		return specId;
	}



	public void setSpecId(Integer specId) {
		this.specId = specId;
	}



	public Integer[] getSpec_id() {
		return spec_id;
	}



	public void setSpec_id(Integer[] spec_id) {
		this.spec_id = spec_id;
	}



	public Integer[] getValueIdArray() {
		return valueIdArray;
	}

	public void setValueIdArray(Integer[] valueIdArray) {
		this.valueIdArray = valueIdArray;
	}



	public int getValueid() {
		return valueid;
	}



	public void setValueid(int valueid) {
		this.valueid = valueid;
	}
	
	
	
	
}
