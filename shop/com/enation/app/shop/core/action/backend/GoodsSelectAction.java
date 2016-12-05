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

import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.framework.action.WWAction;
/**
 * 商品选择Action
 * @author fenlongli
 *
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("goodsSelect")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/goods/goods_opt.html") 
})
public class GoodsSelectAction extends WWAction{

	private IGoodsCatManager goodsCatManager;
	private IGoodsManager goodsManager;
	private IProductManager productManager;
	private Integer catid;
	private Integer sing;
	private Map goodsMap;
	/**
	 * 跳转至商品选择页面
	 * @return 商品选择页面
	 */
	public String list(){
		return "list";
	}
	/**
	 * 获取商品分类列表
	 * @return 分类列表Json
	 */
	@SuppressWarnings("rawtypes")
	public String listJson() {
		List catList = goodsCatManager.listAllChildren(0);
		String s = JSONArray.fromObject(catList).toString();
		this.json = s.replace("name", "text").replace("cat_id", "id");
		return JSON_MESSAGE;
	}
	/**
	 * 获取商品列表
	 * @param catid 商品分类Id,Integer
	 * @param goodslist 商品列表,List
	 * @return 商品列表Json
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String listGoodsById(){
		goodsMap = new HashMap();
		goodsMap.put("catid", catid);
		List goodslist = goodsManager.searchGoods(goodsMap);
		this.showGridJson(goodslist);
		return JSON_MESSAGE;
	}

	/**
	 * 根据分类id查询货品
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String listProductByCatid(){
		List list = productManager.listProductByCatId(catid);
		this.showGridJson(list);
		return JSON_MESSAGE;
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

	public Integer getCatid() {
		return catid;
	}

	public void setCatid(Integer catid) {
		this.catid = catid;
	}

	public Integer getSing() {
		return sing;
	}

	public void setSing(Integer sing) {
		this.sing = sing;
	}

	public Map getGoodsMap() {
		return goodsMap;
	}

	public void setGoodsMap(Map goodsMap) {
		this.goodsMap = goodsMap;
	}
	public IProductManager getProductManager() {
		return productManager;
	}
	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}
	
	
}
