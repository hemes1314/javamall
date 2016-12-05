package com.enation.app.b2b2c.core.action.api.goods;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.store.StoreCat;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsCatManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 商品分类API
 * @author LiFenLong
 *2014-9-15
 */
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("goodsCat")
@Results({
	 @Result(name="cat_edit", type="freemarker", location="/themes/default/b2b2c/goods/goods_cat_edit.html") 
})
public class GoodsCatApiAction extends WWAction{
	private IStoreMemberManager storeMemberManager;
	private IStoreGoodsCatManager storeGoodsCatManager;
	private Integer cat_id;
	private String store_cat_name;
	private Integer store_cat_pid;
	private Integer store_sort;
	private Integer disable;
	private String catids;
	private String catnames;
	private String cat_name;
	
	private IGoodsCatManager goodsCatManager;
	/**
	 * 获取子类别
	 * @param	cat_id 	父分类Id ,Integer型
	 * @return 	返回json串
	 * result	为1表示调用成功0表示失败
	 */
	public String getStoreGoodsChildJson(){
		try {
			List list = this.goodsCatManager.listChildren(this.cat_id);
			this.json = JsonMessageUtil.getListJson(list);
			
		} catch (Exception e) {
			this.showErrorJson("加载出错");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 添加商品分类
	 * @param store_cat_name 店铺分类名称,String型
	 * @param store_cat_pid	店铺分类父Id,Integer型
	 * @param store_sort	店铺分类排序,Integer型
	 * @param disable		店铺分类状态,Integer型
	 * @param storeMember。store_id 店铺Id,Integer型
	 * @return 	返回json串
	 * result	为1表示调用成功0表示失败
	 * 
	 */
	public String addGoodsCat(){
		StoreCat storeCat = new StoreCat();
		StoreMember storeMember = storeMemberManager.getStoreMember();
		try {
			storeCat.setStore_cat_name(store_cat_name);
			storeCat.setStore_cat_pid(store_cat_pid);
			storeCat.setSort(store_sort);
			storeCat.setDisable(disable);
			storeCat.setStore_id(storeMember.getStore_id());
			
			int count = this.storeGoodsCatManager.getStoreCatNum(storeMember.getStore_id(),store_cat_pid,store_sort);
			if(count==0){
				this.storeGoodsCatManager.addStoreCat(storeCat);
				this.showSuccessJson("保存成功");
			}else{
				this.showErrorJson("此分类排序已存在");
			}
			
		} catch (Exception e) {
			this.showErrorJson("保存失败");
		}
		return JSON_MESSAGE;
	}
	/**
	 * 跳转到修改分类页面
	 * @param 无
	 * @return 修改分类页面
	 */
	public String edit(){
		return "cat_edit";
	}
	/**
	 * 修改店铺商品分类
	 * @param cat_id 	分类Id,Integer型
	 * @param store_cat_pid	店铺分类父Id,Integer型
	 * @param store_cat_name	店铺分类名称,String型
	 * @param store_sort		店铺分类排序,Integer型
	 * @param disable			店铺分类状态,Integer型
	 * @param cat_id			分类Id,Integer型
	 * @param storeCat			店铺分类对象,StoreCat
	 * @return	返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String editGoodsCat(){
		StoreCat storeCat = new StoreCat();
		StoreMember storeMember = storeMemberManager.getStoreMember();
		
		try {
			
			int pid = this.storeGoodsCatManager.is_children(cat_id);
			
			if(pid==0 && store_cat_pid!=pid){
				this.showErrorJson("顶级分类不可修改上级分类");
				return JSON_MESSAGE;
			}
			
			storeCat.setStore_cat_name(store_cat_name);
			storeCat.setStore_cat_pid(store_cat_pid);
			storeCat.setSort(store_sort);
			storeCat.setDisable(disable);
			storeCat.setStore_cat_id(cat_id);
			
			this.storeGoodsCatManager.editStoreCat(storeCat);
			this.showSuccessJson("保存成功");
			
		} catch (Exception e) {
			this.showErrorJson("保存失败");
		}
		
		return JSON_MESSAGE;
	}
	/**
	 * 删除店铺商品分类
	 * @param cat_id		分类Id,Integer型
	 * @param cat_name		分类名称,String型	
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String delete(){
		
		try {
			StoreMember member = this.storeMemberManager.getStoreMember();
			this.storeGoodsCatManager.deleteStoreCat(cat_id,member.getStore_id());
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			String str_message = e.getMessage().replaceAll("\\*", "【"+cat_name+"】");
			this.showErrorJson(str_message);
		}catch (Exception e) {
			this.showErrorJson("删除失败");
		}
		
		return JSON_MESSAGE;
	}
	/**
	 * 批量删除店铺商品分类
	 * @param catids	店铺分类,String型
	 * @param catnames	店铺分类名称,String型
	 * @return 	返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String delAll(){
		String catname=null;
		try {
			StoreMember member = this.storeMemberManager.getStoreMember();
			String[] str_catid = catids.split(",");
			String[] str_catname =catnames.split(",");
			for(int i=0;i<str_catid.length;i++){
				String catid = str_catid[i];
				catname = str_catname[i];
				this.storeGoodsCatManager.deleteStoreCat(NumberUtils.toInt(catid), member.getStore_id());
			}
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			String str_message = e.getMessage().replaceAll("\\*", "【"+catname+"】");
			this.showErrorJson(str_message);
		}catch (Exception e) {
			this.showErrorJson("删除失败");
		}
		return JSON_MESSAGE;
	}

	//set  get
	public IStoreGoodsCatManager getStoreGoodsCatManager() {
		return storeGoodsCatManager;
	}

	public void setStoreGoodsCatManager(IStoreGoodsCatManager storeGoodsCatManager) {
		this.storeGoodsCatManager = storeGoodsCatManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	public Integer getCat_id() {
		return cat_id;
	}
	public void setCat_id(Integer cat_id) {
		this.cat_id = cat_id;
	}

	public String getStore_cat_name() {
		return store_cat_name;
	}

	public void setStore_cat_name(String store_cat_name) {
		this.store_cat_name = store_cat_name;
	}

	public Integer getStore_cat_pid() {
		return store_cat_pid;
	}

	public void setStore_cat_pid(Integer store_cat_pid) {
		this.store_cat_pid = store_cat_pid;
	}

	public Integer getDisable() {
		return disable;
	}

	public void setDisable(Integer disable) {
		this.disable = disable;
	}

	public Integer getStore_sort() {
		return store_sort;
	}
 
	public void setStore_sort(Integer store_sort) {
		this.store_sort = store_sort;
	}
	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}
	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}
	public String getCatids() {
		return catids;
	}
	public void setCatids(String catids) {
		this.catids = catids;
	}
	
	public String getCatnames() {
		return catnames;
	}
	public void setCatnames(String catnames) {
		this.catnames = catnames;
	}
	public String getCat_name() {
		return cat_name;
	}
	public void setCat_name(String cat_name) {
		this.cat_name = cat_name;
	}
	
}

