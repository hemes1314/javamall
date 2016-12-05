package com.enation.app.shop.core.action.backend;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.batchimport.IGoodsDataBatchManager;
import com.enation.framework.action.WWAction;

/**
 * 商品导入
 * @author kingapex
 *
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("goodsImport")
@Results({
	@Result(name="input", type="freemarker", location="/shop/admin/import/input.html"),
})
public class GoodsImportAction extends WWAction {
	private IGoodsDataBatchManager goodsDataBatchManager ;
	private IGoodsCatManager goodsCatManager;
	private String path;
	private List catList;
	private int imptype;
	private int catid;
	private Integer startNum;
	private Integer endNum;
	/**
	 * 跳转商品导入页面
	 * @param catList 商品分类列表,List
	 * @return 商品导入页面
	 */
	public String execute(){
		catList = goodsCatManager.listAllChildren(0);
		return "input";
	}
	/**
	 * 导入
	 * @return
	 */
	public String imported(){
		try{
			this.logger.debug("startNum["+startNum+"]endNum["+endNum+"]");
			goodsDataBatchManager.batchImport(path,imptype,catid,startNum,endNum);
			this.showSuccessJson("导入成功");
		}catch(RuntimeException e){
			
			this.logger.error("商品导入",e);
			this.json="{result:0,message:'"+e.getMessage()+"'}";
		}
		return this.JSON_MESSAGE;
	}

	public IGoodsDataBatchManager getGoodsDataBatchManager() {
		return goodsDataBatchManager;
	}

	public void setGoodsDataBatchManager(
			IGoodsDataBatchManager goodsDataBatchManager) {
		this.goodsDataBatchManager = goodsDataBatchManager;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List getCatList() {
		return catList;
	}

	public void setCatList(List catList) {
		this.catList = catList;
	}

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

	public int getImptype() {
		return imptype;
	}

	public void setImptype(int imptype) {
		this.imptype = imptype;
	}

	public int getCatid() {
		return catid;
	}

	public void setCatid(int catid) {
		this.catid = catid;
	}

	public Integer getStartNum() {
		return startNum;
	}

	public void setStartNum(Integer startNum) {
		this.startNum = startNum;
	}

	public Integer getEndNum() {
		return endNum;
	}

	public void setEndNum(Integer endNum) {
		this.endNum = endNum;
	}
	
	
}
