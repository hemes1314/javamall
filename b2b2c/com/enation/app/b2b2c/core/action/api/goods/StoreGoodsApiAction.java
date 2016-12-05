package com.enation.app.b2b2c.core.action.api.goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.enation.app.b2b2c.core.model.goods.StoreGoods;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.IStoreTemplateManager;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.b2b2c.core.test.GoodsGgalleryTest;
import com.enation.app.shop.component.pagecreator.plugin.IndexCreatorUtil;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 店铺商品管理API
 * @author LiFenLong
 *2014-9-15
 */
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("goods")
public class StoreGoodsApiAction extends WWAction{
    private IDaoSupport daoSupport;
	private IGoodsManager goodsManager;
	private IOrderManager orderManager;
	private IStoreGoodsManager storeGoodsManager;
	private IStoreTemplateManager storeTemplateManager;
	private IStoreMemberManager storeMemberManager;
	private IStoreManager storeManager;
	private IProductManager productManager;
	
	private StoreGoods storeGoods;
	private Integer[] goods_id;
	private Integer productid;
	private Integer store;
	private Integer storeid;
	private Integer[] productIds;
	private Integer[] storeIds;
	private Integer[] storeNum;
	private GoodsGgalleryTest goodsGgalleryTest;
	private String sn;
	private Integer goodsid;
	
	
	public GoodsGgalleryTest getGoodsGgalleryTest() {
		return goodsGgalleryTest;
	}

	public void setGoodsGgalleryTest(GoodsGgalleryTest goodsGgalleryTest) {
		this.goodsGgalleryTest = goodsGgalleryTest;
	}

	public String kai(){
		goodsGgalleryTest.test();
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 发布商品
	 * @param storeMember	店铺会员,StoreMember
	 * @param storeGoods	店铺商品,StoreGoods
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	@SuppressWarnings("static-access")
	public String add(){
		try {
			StoreMember storeMember = storeMemberManager.getStoreMember();
			int storeid =storeMember.getStore_id();
			
			storeGoods.setStore_id(storeid);
			
			if(storeGoods.getGoods_transfee_charge()==0){
				Integer tempid = this.storeTemplateManager.getDefTempid(storeMember.getStore_id());
				if(tempid==null){
					this.showErrorJson("店铺未设置默认配送模板，不能设置【买家承担运费】。");
					return JSON_MESSAGE;
				}
			}
			
			//2015-05-20新增店铺名称字段-by kingaepx
			Store store  = this.storeManager.getStore(storeid);
			storeGoods.setStore_name(store.getStore_name());
			
			if (goodsManager.getGoodBySn(storeGoods.getSn()) != null) {
			    this.showErrorJson("商品sn已存在。");
                return JSON_MESSAGE;
			}
			
			goodsManager.add(storeGoods);
			this.showSuccessJson("商品添加成功");
		} catch (Exception e) {
			this.showErrorJson("商品添加失败:"+e.getMessage());
			this.logger.error(e);
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 编辑商品信息
	 * @param  storeGoods	店铺商品,StoreGoods
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String edit(){
		try {
			StoreMember storeMember = storeMemberManager.getStoreMember();
			if(storeGoods.getGoods_transfee_charge()==0){
				Integer tempid = this.storeTemplateManager.getDefTempid(storeMember.getStore_id());
				if(tempid==null){
					this.showErrorJson("店铺未设置默认配送模板，不能设置【买家承担运费】。");
					return JSON_MESSAGE;
				}
			}
			
			if (goodsManager.getGoodBySn(storeGoods.getSn(), storeGoods.getGoods_id()) != null) {
			    this.showErrorJson("商品sn已存在。");
                return JSON_MESSAGE;
			}
			
			goodsManager.edit(storeGoods);
			
			this.showSuccessJson("商品修改成功");
		} catch (Exception e) {
			this.showErrorJson("商品修改失败:"+e.getMessage());
			this.logger.error(e);
		}
		try {
            if(storeGoods.getMarket_enable()==0){
                //如果商品下架，判断是否首页标签商品，那么重新生成首页静态页 
                isTagGoods(storeGoods.getGoods_id()); 
            }
        } catch(Exception e) {
            logger.error("商品id：【"+storeGoods.getGoods_id()+"】:首页标签关联商品下架，生成静态页异常："+e.getMessage());
        }
		return this.JSON_MESSAGE; 
	}
	
	
	private void isTagGoods(Integer goods_id){
	    //判断是否有商品在首页标签
	    int count = this.daoSupport.queryForInt("select count(0) from ES_TAGS t INNER JOIN ES_TAG_REL r ON r.TAG_ID = t.TAG_ID where t.STORE_ID IS NULL and t.TAG_NAME like('%F%') AND r.REL_ID = ?", goods_id);
	    if(count!=0){
	        this.daoSupport.execute("delete from es_tag_rel where rel_id = ?",goods_id);
	        IndexCreatorUtil.createIndexPage();
	    }
	}
	
	
	/**
	 * 删除商品(将商品添加至回收站)
	 * @param goods_id 商品Id,Integer[]型
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String deleteGoods(){
		try {
			if(goods_id!=null){
				goodsManager.delete(goods_id);
				this.showSuccessJson("商品添加至回收站成功");
			}else{
				this.showErrorJson("请选择商品");
			}
		} catch (Exception e) {
			this.showErrorJson("商品添加至回收站失败");
			this.logger.error(e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 清除商品
	 * @param goods_id 商品Id,Integer[]型
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String cleanGoods(){
		try {
			if(goods_id!=null){
				goodsManager.clean(goods_id);
				this.showSuccessJson("清除商品成功");
			}else{
				this.showErrorJson("请选择商品");
			}
		} catch (Exception e) {
			this.showErrorJson("清除商品失败");
			this.logger.error(e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 检验是否有订单购买过此商品
	 * @param productid	货品Id,Integer型
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String checkProInOrder(){
		boolean isinorder = this.orderManager.checkProInOrder(productid);
		if (isinorder) {
			this.showErrorJson("此货品已经有顾客购买，如果删除此订单将不能配货发货，请谨慎操作!\n点击确定删除此货品，点击取消保留此货品。");
		}else{
			this.showSuccessJson("删除吧");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 还原商品
	 * @param goods_id 商品Id,Integer[]型
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 * 
	 */
	public String revertGoods() {
		try {
			if(goods_id!=null){
				this.goodsManager.revert(goods_id);
				this.showSuccessJson("还原成功");
			}else{
				this.showErrorJson("请选择商品");
			}
		} catch (Exception e) {
			this.showErrorJson("还原失败");
			logger.error("商品还原失败", e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 修改商品库存
	 * @param goods_id 商品Id,Integer[]型
	 * @param store 商品库存,Integer型
	 * @param storeid	店铺Id,Integer型
	 * @return	返回json串
	 * result 	为1表示调用成功0表示失败
	 * 
	 */
	public String saveGoodsStore(){
		try {
			if(productIds!=null && productIds.length>0){
				this.storeGoodsManager.saveGoodsSpecStore(storeIds, goods_id[0], storeNum, productIds);
			}else{
				this.storeGoodsManager.saveGoodsStore(storeid,goods_id[0],store);
			}
			this.showSuccessJson("保存库存成功");
		} catch (Exception e) {
			this.showErrorJson("保存库存失败");
			this.logger.error("保存库存失败:"+e);
		}
		return this.JSON_MESSAGE;
	}

	
	
	/**
	 * 店铺内搜索商品（商家）
	 * @param keyword:搜索关键字,String，可为空
	 * @param store_catid 店铺分类id,int ，如果为0，则搜索全部
	 * @param is_groupbuy 是否已经为团购商品.
	 * @return 商品列表， List<Map> 型的json，Map中存的是goods
	 */
	public String search(){
		try {
			HttpServletRequest request  = this.getRequest();
			String keyword = request.getParameter("keyword");
			String store_catid = request.getParameter("store_catid");
			StoreMember storeMember = storeMemberManager.getStoreMember();
			if(storeMember==null ) {
				this.showErrorJson("尚未登陆，不能使用此API");
				return this.JSON_MESSAGE;
			}
			
			
			Map params = new HashMap();
			params.put("keyword", keyword);
			params.put("store_catid", store_catid);
			params.put("market_enable", 1);
		    List<Map> goodsList  = this.storeGoodsManager.storeGoodsList(storeMember.getStore_id(),params);
			this.json =JsonMessageUtil.getListJson(goodsList);
			
		} catch (Exception e) {
			 this.showErrorJson("api调用失败"+e.getMessage());
			 this.logger.error("商品搜索出错",e);
		}
		
		
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 
	 * 判断商品货号是否存在
	 * @author xulipeng
	 * @param sn：货号
	 * 2015年08月31日22:59:49
	 * @return
	 */
	public String snIsExist(){
		StoreMember member = this.storeMemberManager.getStoreMember();
		int count =  this.productManager.getSnIsExist(sn,goodsid,member.getStore_id());
		if(count==1){
			this.showErrorJson("货号已存在！");
		}else{
			this.showSuccessJson("ok");
		}
		return JSON_MESSAGE;
	}
	

	// set get 
	public StoreGoods getStoreGoods() {
		return storeGoods;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public void setStoreGoods(StoreGoods storeGoods) {
		this.storeGoods = storeGoods;
	}
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}
	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}
	public Integer[] getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Integer[] goods_id) {
		this.goods_id = goods_id;
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	public Integer getProductid() {
		return productid;
	}
	public void setProductid(Integer productid) {
		this.productid = productid;
	}
	public IStoreGoodsManager getStoreGoodsManager() {
		return storeGoodsManager;
	}
	public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
		this.storeGoodsManager = storeGoodsManager;
	}
	public Integer getStore() {
		return store;
	}
	public void setStore(Integer store) {
		this.store = store;
	}
	public Integer getStoreid() {
		return storeid;
	}
	public void setStoreid(Integer storeid) {
		this.storeid = storeid;
	}
	public IStoreTemplateManager getStoreTemplateManager() {
		return storeTemplateManager;
	}
	public void setStoreTemplateManager(IStoreTemplateManager storeTemplateManager) {
		this.storeTemplateManager = storeTemplateManager;
	}

	public Integer[] getProductIds() {
		return productIds;
	}

	public void setProductIds(Integer[] productIds) {
		this.productIds = productIds;
	}

	public Integer[] getStoreIds() {
		return storeIds;
	}

	public void setStoreIds(Integer[] storeIds) {
		this.storeIds = storeIds;
	}

	public Integer[] getStoreNum() {
		return storeNum;
	}

	public void setStoreNum(Integer[] storeNum) {
		this.storeNum = storeNum;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Integer getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(Integer goodsid) {
		this.goodsid = goodsid;
	}

	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

    
    public IDaoSupport getDaoSupport() {
        return daoSupport;
    }

    
    public void setDaoSupport(IDaoSupport daoSupport) {
        this.daoSupport = daoSupport;
    }
	
	
}
