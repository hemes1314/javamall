package com.enation.app.shop.core.action.backend;

import java.util.ArrayList;
import java.util.List;

import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.model.Allocation;
import com.enation.app.shop.core.model.AllocationItem;
import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.DeliveryItem;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IGoodsStoreManager;
import com.enation.app.shop.core.service.ILogiManager;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.framework.action.WWAction;

/**
 * 收退货action
 * 
 * @author apexking
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class DeliveryAction extends WWAction {
	
	private IDepotManager depotManager;
	private ILogiManager logiManager;
	private IDlyTypeManager dlyTypeManager;
	private IOrderManager orderManager;
	private IOrderFlowManager orderFlowManager;
	private IRegionsManager regionsManager;
	private IGoodsStoreManager goodsStoreManager ;
	
	
	private Integer orderId;
	private Order ord;
	private List logiList;
	private List dlyTypeList;
	private List itemList;
	private List giftList;
	private List depotList; // 库房列表
	private String allocationHtml; //某个产品的配货html
	private Integer productid;
	private Integer depotid;
	
	private Delivery delivery;
	private String[] goods_nameArray;
	private String[] goods_snArray;
	private Integer[] goods_idArray;
	private Integer[] product_idArray;
	private Integer[] numArray;
	private Integer[] depot_idArray; // 库房id数组
	private Integer[] cat_idArray;//货物项所属分类数组
	private Integer[] item_idArray;//货物项id数组(目前配货和发货时用到了)
	
	
	private int province_id;
	private int  city_id;
	private int  region_id;
	
	
	private String province;
	private String city;
	private String region;

	private int itemid;
	
	private int id;
	
	
	
	
	/**
	 * 显示配货对话框 
	 * @return
	 */
	public String showAllocationDialog(){
		this.ord = this.orderManager.get(orderId); //当前订单 
		this.itemList = this.orderManager.listGoodsItems(this.orderId); //商品列表 
		this.depotList = this.depotManager.list(); //仓库列表
		return "allocation_dialog";
	}
	
	/**
	 * 获取某个订单商品项的配货库存情况
	 * @return
	 */
	public String getProductStore(){ 
		this.allocationHtml = this.orderFlowManager.getAllocationHtml(itemid);
		return  "product_store";
	}
	
	/**
	 * 显示配货情况的html
	 * @return
	 */
	public String viewProductStore(){
		this.allocationHtml = orderFlowManager.getAllocationViewHtml(itemid);
		return  "view_product_store";
	}
	
	
	/**
	 * 配货
	 * @return
	 */
	public String allocation(){
		
		try{
			Allocation allocation = new Allocation();
			allocation.setOrderid(this.orderId);
			allocation.setShipDepotId(this.depotid);
			
			List<AllocationItem> aitemList  = new ArrayList<AllocationItem>();
			int i =0;
			for(Integer goods_id :goods_idArray){
				
				AllocationItem item = new AllocationItem();
				item.setDepotid(this.depot_idArray[i]);
				item.setGoodsid(goods_id);
				item.setNum(numArray[i]);  
				item.setProductid(product_idArray[i]);
				item.setCat_id(cat_idArray[i]);
				item.setItemid(item_idArray[i]);
				aitemList.add(item);
				i++;
			}
			
			allocation.setItemList(aitemList);
			this.orderFlowManager.allocation(allocation);
			Order order = this.orderManager.get(orderId);
			this.json="{result:1,message:'订单["+order.getSn()+"]配货成功',orderStatus:"+order.getStatus()+",payStatus:"+order.getPay_status()+",shipStatus:"+order.getShip_status()+"}";
		}catch(RuntimeException e){
			this.logger.error("配货失败",e);
			this.showErrorJson("配货失败，错误信息："+e.getMessage());
		}
		
		return  JSON_MESSAGE;
	}
	
	/**
	 * 确认配货完成
	 * @return
	 */
	public String allocationFinish(){
		try {
			this.orderFlowManager.updateAllocation(id,orderId);
			this.json = "{result:1,message:'确认配货成功'}";
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			this.json = "{result:0,message:\"确认配货失败：" + e.getMessage() + "\"}";

		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 显示发货对话框
	 * @return
	 */
	public String showShipDialog(){
		this.fillShipData();
		//读取未发货明细
		this.itemList = this.orderFlowManager.listNotShipGoodsItem(orderId);
		
 
		 
		return "ship_dialog";
	}
	
	
	
	/**
	 * 显示退货对话框
	 * @return
	 */
	public String showReturnDialog(){
		this.fillShipData();
		//读取已发货明细
		this.itemList = this.orderFlowManager.listShipGoodsItem(orderId);		//商品已发货明细
	 
		return "return_dialog";
	}
	
	
	/**
	 * 显示换货对话框
	 * @return
	 */
	public String showChangeDialog(){
		this.fillShipData();
		//读取已发货明细
		this.itemList = this.orderFlowManager.listShipGoodsItem(orderId);		//商品已发货明细
//		giftList = this.orderFlowManager.listNotShipGiftItem(orderId);
//		hasGift= giftList==null||giftList.isEmpty()?false:true;
		return "change_dialog";
	}
	
	

	private void fillShipData(){
		logiList  = logiManager.list();
		dlyTypeList =  dlyTypeManager.list();
		this.ord = this.orderManager.get(orderId);
//		if(!StringUtil.isEmpty(this.ord.getShipping_area())){
//			Regions[] r = this.regionsManager.get(this.ord.getShipping_area());
//			this.province = r.length >0?r[0]:null;
//			this.city = r.length>1?r[1]:null;
//			this.region = r.length>2?r[2]:null;
//		}
	}


	
	public String ship(){
		try{
			
			delivery.setProvince(province);
			delivery.setCity(city);
			delivery.setRegion(region);
			
			delivery.setProvince_id(province_id);
			delivery.setCity_id(city_id);	
			delivery.setRegion_id(region_id);			
			
			List<DeliveryItem> itemList  = new ArrayList<DeliveryItem>();
			int i=0;
			for(Integer goods_id :goods_idArray){
				
				DeliveryItem item = new DeliveryItem();
				item.setGoods_id(goods_id);
				item.setName(goods_nameArray[i]);
				item.setNum(numArray[i]);  
				item.setProduct_id(product_idArray[i]);
				item.setSn(goods_snArray[i]);
				item.setOrder_itemid(item_idArray[i]); //订单货物项id
				item.setItemtype(0);
				itemList.add(item);
				i++;
			}
		
			delivery.setOrder_id(orderId);
			this.orderFlowManager.shipping(delivery, itemList);
			Order order = this.orderManager.get(orderId);
			this.json="{result:1,message:'订单["+order.getSn()+"]发货成功',orderStatus:"+order.getStatus()+",payStatus:"+order.getPay_status()+",shipStatus:"+order.getShip_status()+"}";
		}catch(RuntimeException e){
			if(logger.isDebugEnabled()){
				logger.debug(e.getStackTrace());
			this.json="{result:0,message:\"发货失败："+e.getLocalizedMessage()+"\"}";
		}
		}
		
		return JSON_MESSAGE;
	}
	
/**
 * 退货
 * @return
 */
	public String returned(){
		try{
			List<DeliveryItem> itemList  = new ArrayList<DeliveryItem>();
			int i=0;
			for(Integer goods_id :goods_idArray){
				
				DeliveryItem item = new DeliveryItem();
				item.setGoods_id(goods_id);
				item.setName(goods_nameArray[i]);
				item.setNum(numArray[i]);  
				item.setProduct_id(product_idArray[i]);
				item.setSn(goods_snArray[i]);
				itemList.add(item);
				i++;
			}
			
			i=0;
			List<DeliveryItem> giftitemList  = new ArrayList<DeliveryItem>();
 	
			delivery.setProvince(province);
			delivery.setCity(city);
			delivery.setRegion(region);
			
			delivery.setProvince_id(province_id);
			delivery.setCity_id(city_id);	
			delivery.setRegion_id(region_id);	
		
			delivery.setOrder_id(orderId);
			this.orderFlowManager.returned(delivery, itemList,giftitemList);
			Order order = this.orderManager.get(orderId);
			this.json="{result:1,message:'订单["+order.getSn()+"]退货成功',shipStatus:"+order.getShip_status()+"}";
		}catch(RuntimeException e){
			if(logger.isDebugEnabled()){
				logger.debug(e.getStackTrace());
			this.json="{result:0,message:\"退货失败："+e.getLocalizedMessage()+"\"}";
		}
		}
		
		return JSON_MESSAGE;
	}
	

	
	/**
	 * 换货
	 * @return
	 */
	public String change() {
		try {
			List<DeliveryItem> itemList = new ArrayList<DeliveryItem>();
			int i = 0;
			for (Integer goods_id : goods_idArray) {

				DeliveryItem item = new DeliveryItem();
				item.setGoods_id(goods_id);
				item.setName(goods_nameArray[i]);
				item.setNum(numArray[i]);
				item.setProduct_id(product_idArray[i]);
				item.setSn(goods_snArray[i]);
				itemList.add(item);
				i++;
			}

			delivery.setOrder_id(orderId);
			this.orderFlowManager.change(delivery, itemList, null); // 赠品退货本版本未实现
			Order order = this.orderManager.get(orderId);
			this.json = "{result:1,message:'订单[" + order.getSn()
					+ "]换货成功',shipStatus:" + order.getShip_status() + "}";
		} catch (RuntimeException e) {
	
				logger.error(e.getMessage(), e);
				
				this.json = "{result:0,message:\"换货失败："
						+ e.getLocalizedMessage() + "\"}";
			
		}

		return JSON_MESSAGE;
	}

	public ILogiManager getLogiManager() {
		return logiManager;
	}



	public void setLogiManager(ILogiManager logiManager) {
		this.logiManager = logiManager;
	}



	public IDlyTypeManager getDlyTypeManager() {
		return dlyTypeManager;
	}



	public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
		this.dlyTypeManager = dlyTypeManager;
	}



	public List getLogiList() {
		return logiList;
	}



	public void setLogiList(List logiList) {
		this.logiList = logiList;
	}



	public List getDlyTypeList() {
		return dlyTypeList;
	}



	public void setDlyTypeList(List dlyTypeList) {
		this.dlyTypeList = dlyTypeList;
	}



	public IOrderManager getOrderManager() {
		return orderManager;
	}



	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}



	public Integer getOrderId() {
		return orderId;
	}



	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}



	public Order getOrd() {
		return ord;
	}



	public void setOrd(Order ord) {
		this.ord = ord;
	}



	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}



	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}



	public List getItemList() {
		return itemList;
	}



	public void setItemList(List itemList) {
		this.itemList = itemList;
	}



	public Delivery getDelivery() {
		return delivery;
	}



	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}



	public String[] getGoods_nameArray() {
		return goods_nameArray;
	}



	public void setGoods_nameArray(String[] goodsNameArray) {
		goods_nameArray = goodsNameArray;
	}



	public Integer[] getGoods_idArray() {
		return goods_idArray;
	}



	public void setGoods_idArray(Integer[] goodsIdArray) {
		goods_idArray = goodsIdArray;
	}



	public Integer[] getProduct_idArray() {
		return product_idArray;
	}



	public void setProduct_idArray(Integer[] productIdArray) {
		product_idArray = productIdArray;
	}



	public Integer[] getNumArray() {
		return numArray;
	}



	public void setNumArray(Integer[] numArray) {
		this.numArray = numArray;
	}



	public List getGiftList() {
		return giftList;
	}



	public void setGiftList(List giftList) {
		this.giftList = giftList;
	}


	public String[] getGoods_snArray() {
		return goods_snArray;
	}



	public void setGoods_snArray(String[] goodsSnArray) {
		goods_snArray = goodsSnArray;
	}
	public IDepotManager getDepotManager() {
		return depotManager;
	}
	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}
	public List getDepotList() {
		return depotList;
	}
	public void setDepotList(List depotList) {
		this.depotList = depotList;
	}

	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}
	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}
 
	public Integer getProductid() {
		return productid;
	}

	public void setProductid(Integer productid) {
		this.productid = productid;
	}

	public IGoodsStoreManager getGoodsStoreManager() {
		return goodsStoreManager;
	}

	public void setGoodsStoreManager(IGoodsStoreManager goodsStoreManager) {
		this.goodsStoreManager = goodsStoreManager;
	}

	public Integer getDepotid() {
		return depotid;
	}


	public void setDepotid(Integer depotid) {
		this.depotid = depotid;
	}

	public Integer[] getDepot_idArray() {
		return depot_idArray;
	}

	public void setDepot_idArray(Integer[] depot_idArray) {
		this.depot_idArray = depot_idArray;
	}

	public int getItemid() {
		return itemid;
	}

	public void setItemid(int itemid) {
		this.itemid = itemid;
	}

	public Integer[] getCat_idArray() {
		return cat_idArray;
	}

	public void setCat_idArray(Integer[] cat_idArray) {
		this.cat_idArray = cat_idArray;
	}


	public String getAllocationHtml() {
		return allocationHtml;
	}

	public void setAllocationHtml(String allocationHtml) {
		this.allocationHtml = allocationHtml;
	}

	public Integer[] getItem_idArray() {
		return item_idArray;
	}

	public void setItem_idArray(Integer[] item_idArray) {
		this.item_idArray = item_idArray;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProvince_id() {
		return province_id;
	}

	public void setProvince_id(int province_id) {
		this.province_id = province_id;
	}

	public int getCity_id() {
		return city_id;
	}

	public void setCity_id(int city_id) {
		this.city_id = city_id;
	}

	public int getRegion_id() {
		return region_id;
	}

	public void setRegion_id(int region_id) {
		this.region_id = region_id;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}


}
