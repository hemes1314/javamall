package com.enation.app.b2b2c.core.action.backend.goods;


import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.support.GoodsEditDTO;
import com.enation.app.shop.core.plugin.goods.GoodsPluginBundle;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.framework.action.WWAction;
@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/b2b2c/admin/goods/goods_list.html"),
	 @Result(name="input", type="freemarker", location="/b2b2c/admin/goods/goods_input.html")
})
@Action("storeGoods")
public class StoreGoodsAction extends WWAction{
	private String optype="no";
	protected Boolean is_edit;
	protected Integer goodsId;
	protected String actionName;
	protected Map goodsView;
	protected List catList; // 所有商品分类
	
	protected Map<Integer, String> pluginTabs;
	protected Map<Integer, String> pluginHtmls;
	
	protected IGoodsCatManager goodsCatManager;
	protected IGoodsManager goodsManager;
	private GoodsPluginBundle goodsPluginBundle;
	
    private Integer market_enable = -1; //防止商品列表报错 chenzhongwei add
	
	
    public Integer getMarket_enable() {
        return market_enable;
    }

    
    public void setMarket_enable(Integer market_enable) {
        this.market_enable = market_enable;
    }

    /**
	 * 商品列表
	 * @param brand_id 品牌Id,Integer
	 * @param catid 商品分类Id,Integer
	 * @param name 商品名称,String
	 * @param sn 商品编号,String 
	 * @param tagids 商品标签Id,Integer[]
	 * @return 商品列表页
	 */
	public String list() {
		return "list";
	}

	/**
	 * 跳转到商品详细页
	 * @param catList 商品分类列表,List
	 * @param actionName 修改商品方法,String
	 * @param is_edit 是否为修改商品,boolean
	 * @param goodsView 商品信息,Map
	 * @param pluginTabs 商品tab标题List,List
	 * @param pluginHtmls 商品添加内容List,List
	 * @return 商品详细页
	 */
	public String edit() {
		actionName = "goods!saveEdit.do";
		is_edit = true;

		catList = goodsCatManager.listAllChildren(0);
		GoodsEditDTO editDTO = this.goodsManager.getGoodsEditData(goodsId);
		goodsView = editDTO.getGoods();

		this.pluginTabs = this.goodsPluginBundle.getTabList();
		this.pluginHtmls = editDTO.getHtmlMap();

		return this.INPUT;
	}
	public String getOptype() {
		return optype;
	}

	public void setOptype(String optype) {
		this.optype = optype;
	}

	public Boolean getIs_edit() {
		return is_edit;
	}

	public void setIs_edit(Boolean is_edit) {
		this.is_edit = is_edit;
	}

	public Integer getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}

	public Map getGoodsView() {
		return goodsView;
	}

	public void setGoodsView(Map goodsView) {
		this.goodsView = goodsView;
	}

	public List getCatList() {
		return catList;
	}

	public void setCatList(List catList) {
		this.catList = catList;
	}

	public Map<Integer, String> getPluginTabs() {
		return pluginTabs;
	}

	public void setPluginTabs(Map<Integer, String> pluginTabs) {
		this.pluginTabs = pluginTabs;
	}

	public Map<Integer, String> getPluginHtmls() {
		return pluginHtmls;
	}

	public void setPluginHtmls(Map<Integer, String> pluginHtmls) {
		this.pluginHtmls = pluginHtmls;
	}

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

	public GoodsPluginBundle getGoodsPluginBundle() {
		return goodsPluginBundle;
	}

	public void setGoodsPluginBundle(GoodsPluginBundle goodsPluginBundle) {
		this.goodsPluginBundle = goodsPluginBundle;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
}
