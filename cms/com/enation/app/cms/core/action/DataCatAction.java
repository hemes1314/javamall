package com.enation.app.cms.core.action;

import java.util.List;

import net.sf.json.JSONArray;
import com.enation.app.cms.core.model.DataCat;
import com.enation.app.cms.core.service.ArticleCatRuntimeException;
import com.enation.app.cms.core.service.IDataCatManager;
import com.enation.app.cms.core.service.IDataModelManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.action.WWAction;

/**
 * 文章类别管理action
 * @author kingapex
 * 2010-7-5上午09:26:26
 */
public class DataCatAction extends WWAction {
	
	private IDataCatManager dataCatManager;
	private IDataModelManager dataModelManager;
	private boolean isEdit;
	private List catList;
	private List modelList;
	private DataCat cat;
	private int cat_id;
	private int[] cat_ids; //分类id 数组,用于保存排序
	private int[] cat_sorts; //分类排序值
	
	public String list(){
		return "list";
	}
	public String listJson() {
		this.catList = this.dataCatManager.listAllChildren(0);
		this.json = JSONArray.fromObject(catList).toString();
		return JSON_MESSAGE;
	}
	
	

	//到添加页面
	public String add(){
		isEdit =false;
		catList = dataCatManager.listAllChildren(0);
		modelList  = this.dataModelManager.list();
		return "add";
	}
	
	
	//保存编辑
	public String edit(){
		isEdit = true;
		catList = dataCatManager.listAllChildren(0);
		modelList  = this.dataModelManager.list();
		cat = dataCatManager.get(cat_id);
		return "edit";
	}
	
	
	//保存添加
	public String saveAdd(){
		try{
			dataCatManager.add(cat);
		   	this.showSuccessJson("文章栏目添加成功");
		}catch(ArticleCatRuntimeException ex){
			this.showErrorJson("同级文章栏目不能同名");
		}
		return this.JSON_MESSAGE;
	}
 
	
	
	//保存修改 
	public String saveEdit(){
		try{
		   this.dataCatManager.edit(cat);
		   this.showSuccessJson("文章栏目修改成功");
		}catch(ArticleCatRuntimeException ex){
			this.showErrorJson("同级文章栏目不能同名"); 
		}
		return this.JSON_MESSAGE;
	}
	
	
	//删除
	public String delete(){
		
		int r  = this.dataCatManager.delete(cat_id);
		if(r==0){
			this.showSuccessJson("删除成功");
		}else if(r==1){
			this.showErrorJson("此类别下存在子类别或者文章不能删除!");
		}		
				
		return this.JSON_MESSAGE;
	}
	
	
	public String saveSort(){
		try{
			this.dataCatManager.saveSort(cat_ids, cat_sorts);
			this.showSuccessJson("保存成功");
		}catch(RuntimeException  e){
			this.showErrorJson("保存失败");
		}
		return this.JSON_MESSAGE;
	}

	
	/**
	 * 用于异步显示分类树
	 * @return
	 */
	public String showCatTree(){
		catList = dataCatManager.listAllChildren(cat_id);
		return "cat_tree";
	}


	public IDataCatManager getDataCatManager() {
		return dataCatManager;
	}



	public void setDataCatManager(IDataCatManager dataCatManager) {
		this.dataCatManager = dataCatManager;
	}



	public IDataModelManager getDataModelManager() {
		return dataModelManager;
	}



	public void setDataModelManager(IDataModelManager dataModelManager) {
		this.dataModelManager = dataModelManager;
	}



	public List getCatList() {
		return catList;
	}



	public void setCatList(List catList) {
		this.catList = catList;
	}



	public DataCat getCat() {
		return cat;
	}



	public void setCat(DataCat cat) {
		this.cat = cat;
	}



	public int getCat_id() {
		return cat_id;
	}



	public void setCat_id(int catId) {
		cat_id = catId;
	}



	public int[] getCat_ids() {
		return cat_ids;
	}



	public void setCat_ids(int[] catIds) {
		cat_ids = catIds;
	}



	public int[] getCat_sorts() {
		return cat_sorts;
	}



	public void setCat_sorts(int[] catSorts) {
		cat_sorts = catSorts;
	}



	public List getModelList() {
		return modelList;
	}



	public void setModelList(List modelList) {
		this.modelList = modelList;
	}	
	
	public void setIsEdit(boolean isEdit){
		this.isEdit = isEdit;
	}
	
	public boolean getIsEdit(){
		return this.isEdit;
	}

}
