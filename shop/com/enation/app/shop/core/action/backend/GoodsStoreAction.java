package com.enation.app.shop.core.action.backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.sf.json.JSONArray;

import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.IGoodsStoreManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;

/**
 * 商品库存管理
 * 
 * @author kingapex
 * 
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("goodsStore")
@Results({
	@Result(name="dialog_html", type="freemarker", location="/shop/admin/goodsstore/dialog.html"),
	@Result(name="goodsstore_list", type="freemarker", location="/shop/admin/goodsstore/goodsstore_list.html") 
})
public class GoodsStoreAction extends WWAction {

	private IGoodsStoreManager goodsStoreManager;
	private IDepotManager depotManager;
	private int goodsid;
	private String html;
	private Integer stype;
	private String keyword;
	private String name;
	private String sn;
	private Map goodsStoreMap;
	private List goodsStoreList;
	private String optype; // 操作类型
	private Integer depot_id;
	
	/**
	 * 跳转至商品库存管理页面
	 * @param goodsStoreList 仓库列表,List
	 * @return 商品库存管理页面
	 */
	public String listGoodsStore(){
		return "goodsstore_list";
	}
	/**
	 * 获取商品库存管理列表Json
	 * @param stype 搜索类型,INteger
	 * @param keyword 搜索关键字,String
	 * @param name 商品名称,String
	 * @param sn 商品编号,String
	 * @param depot_id 库房Id,Integer
	 * @return 商品库存管理列表Json
	 */
	@SuppressWarnings("unchecked")
	public String listGoodsStoreJson(){
		Map storeMap = new HashMap();
		storeMap.put("stype", stype);
		storeMap.put("keyword", keyword);
		storeMap.put("name", name);
		storeMap.put("sn", sn);
		depot_id = depot_id==null?0:depot_id;
		storeMap.put("depotid", depot_id);
		
		Page page=this.goodsStoreManager.listGoodsStore(storeMap, this.getPage(), this.getPageSize(), null,this.getSort(),this.getOrder());
		this.showGridJson(page);
		return JSON_MESSAGE;
	}
	/**
	 * 获取所有的仓库Json
	 * @return 所有的仓库Json
	 */
	@SuppressWarnings("rawtypes")
	public String listStoreJson(){
		List list = this.goodsStoreManager.getStoreList();
		String s = JSONArray.fromObject(list).toString();
		this.json = s.replace("name", "text");
		return JSON_MESSAGE;
	}

	/**
	 * 获取库存维护对话框页面html;
	 * @param goodsid 商品Id,Integer
	 * @return
	 */
	public String getStoreDialogHtml() {
		html = goodsStoreManager.getStoreHtml(goodsid);
		return "dialog_html";
	}

	/**
	 * 获取进货对话框页面html
	 * @param goodsid 商品Id,Integer
	 */
	public String getStockDialogHtml() {
		html = this.goodsStoreManager.getStockHtml(goodsid);
		return "dialog_html";
	}

	/**
	 * 获取出货对话框页面html
	 * @param goodsid 商品Id,Integer
	 * @return
	 */
	public String getShipDialogHtml() {
		html = this.goodsStoreManager.getShipHtml(goodsid);
		return "dialog_html";
	}

	/**
	 * 保存库存维护
	 * @param goodsid 商品Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String saveStore() {
		try {
			goodsStoreManager.saveStore(goodsid);
			this.showSuccessJson("保存商品库存成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("保存商品库存出错", e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 保存进货
	 * @param goodsid 商品Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String saveStock() {
		try {
			goodsStoreManager.saveStock(goodsid);
			this.showSuccessJson("保存进货成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("保存进货出错", e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 获取报警设置对话框页面html
	 * @param goodsid 商品Id
	 */
	public String getWarnDialogHtml() {
		html = this.goodsStoreManager.getWarnHtml(goodsid);
		return "dialog_html";
	}

	/**
	 * 保存报警
	 * @param goodsid 商品Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String saveWarn() {
		try {
			goodsStoreManager.saveWarn(goodsid);
			this.showSuccessJson("保存报警成功");
		} catch (RuntimeException e) {
			this.logger.error("保存报警出错", e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 保存出货
	 * @param goodsid 商品Id
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String saveShip() {
		try {
			goodsStoreManager.saveShip(goodsid);
			this.showSuccessJson("保存出货成功");
		} catch (RuntimeException e) {
			this.logger.error("保存出货出错", e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	public IGoodsStoreManager getGoodsStoreManager() {
		return goodsStoreManager;
	}

	public void setGoodsStoreManager(IGoodsStoreManager goodsStoreManager) {
		this.goodsStoreManager = goodsStoreManager;
	}

	public int getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(int goodsid) {
		this.goodsid = goodsid;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}


	public Integer getStype() {
		return stype;
	}

	public void setStype(Integer stype) {
		this.stype = stype;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Map getGoodsStoreMap() {
		return goodsStoreMap;
	}

	public void setGoodsStoreMap(Map goodsStoreMap) {
		this.goodsStoreMap = goodsStoreMap;
	}

	public IDepotManager getDepotManager() {
		return depotManager;
	}

	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}

	public List getGoodsStoreList() {
		return goodsStoreList;
	}

	public void setGoodsStoreList(List goodsStoreList) {
		this.goodsStoreList = goodsStoreList;
	}


	public String getOptype() {
		return optype;
	}

	public void setOptype(String optype) {
		this.optype = optype;
	}

	public Integer getDepot_id() {
		return depot_id;
	}

	public void setDepot_id(Integer depot_id) {
		this.depot_id = depot_id;
	}



}
