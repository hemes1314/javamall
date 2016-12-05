package com.enation.app.b2b2c.core.service.order.impl;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderPrintManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.util.StringUtil;
@Component
public class StoreOrderPrintManager extends BaseSupport implements IStoreOrderPrintManager {
	private IStoreOrderManager storeOrderManager;
	private IStoreMemberManager storeMemberManager;
	private IStoreManager storeManager;
	@Override
	public String getShipScript(Integer order_id) {
		StoreOrder storeOrder= storeOrderManager.get(order_id);
		List<Map> itemList  = listItem(storeOrder.getOrder_id());
		int  itemCount = 0;
		for (Map item : itemList) {
			int num = (Integer)item.get("num");
			itemCount+=num;
			//获取订单商品规格
			if(item.get("addon")!=null){
				String addon=item.get("addon").toString();
				if(!StringUtil.isEmpty(addon)){
					item.put("specList", (List) JSONArray.toCollection(JSONArray.fromObject(addon),Map.class));
				}
			}
		}
		
		//发货人
		StoreMember member= storeMemberManager.getStoreMember();
		Store store=storeManager.getStore(member.getStore_id());
		FreeMarkerPaser freeMarkerPaser =  new FreeMarkerPaser();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.setPageName("user");
		freeMarkerPaser.setPageFolder("/b2b2c/admin/printtpl/ship");
		freeMarkerPaser.putData("order",storeOrder);
		freeMarkerPaser.putData("itemCount",itemCount);
		freeMarkerPaser.putData("store",store);
		
		String userHtml = freeMarkerPaser.proessPageContent();
		String itemHtml = this.createItemHtml(freeMarkerPaser, itemList);
							
		
		freeMarkerPaser.setPageName("footer");
		String footerHtml = freeMarkerPaser.proessPageContent();
		
		
		userHtml=userHtml.replaceAll("(\r\n|\r|\n|\n\r)", "");
		itemHtml=itemHtml.replaceAll("(\r\n|\r|\n|\n\r)", "");
		footerHtml=footerHtml.replaceAll("(\r\n|\r|\n|\n\r)", "");
	
		freeMarkerPaser.setPageName("script");
		freeMarkerPaser.putData("userHtml",userHtml);
		freeMarkerPaser.putData("itemHtml",itemHtml);
		freeMarkerPaser.putData("footerHtml",footerHtml);
		String script = freeMarkerPaser.proessPageContent();
		
		return script;
	}
	
	/**
	 * 创建商品列表
	 * @param freeMarkerPaser
	 * @param itemList
	 * @return
	 */
	private String createItemHtml(FreeMarkerPaser freeMarkerPaser,List itemList){
		
		StringBuffer itemHtml = new StringBuffer();
		
		int totalCount = itemList.size();//总条数
		int pageSize=15; //每页记录数
		int firstPageSize=10; //首页记录数
		 
		
		//生成第一页的商品列表
		int firstMax=totalCount> firstPageSize?firstPageSize:totalCount;
		
		List firstList  = itemList.subList(0, firstMax);
		
		
		freeMarkerPaser.setPageName("item");
		freeMarkerPaser.putData("itemList",firstList);
		freeMarkerPaser.putData("start",0);
		String firstHtml = freeMarkerPaser.proessPageContent();
		firstHtml="LODOP.ADD_PRINT_TABLE(\"60px\",\"-1\",\"200mm\",\"100%\",'"+firstHtml+"');";
		firstHtml+="LODOP.SET_PRINT_STYLEA(0,\"LinkedItem\",1);";
		itemHtml.append(firstHtml);
		
		//剩余数量
		int expessCount =totalCount-firstList.size();
		
		//计算剩下的分几页
		int  pageCount =expessCount/ pageSize;
		pageCount = expessCount/ pageSize > 0 ? pageCount + 1 : pageCount;
		
		for(int pageNo=1;pageNo<=pageCount;pageNo++){
			itemHtml.append("LODOP.NEWPAGEA();");
			int start  =  firstMax+ (pageNo-1) *pageSize;
			int end = start+pageSize;
			if(pageNo==pageCount){
				end=totalCount;
			}
				
			List subList =itemList.subList( start, end);
			freeMarkerPaser.putData("start",start);
			freeMarkerPaser.putData("itemList",subList);
			String subHtml = freeMarkerPaser.proessPageContent();
			subHtml="LODOP.ADD_PRINT_TABLE(\"0\",\"-0\",\"200mm\",\"100%\",'"+subHtml+"');";
			itemHtml.append(subHtml);
		}
		
		return itemHtml.toString();
	}
	/**
	 * 查看商品列表
	 * @param orderid
	 * @return
	 */
	private List listItem(int orderid){
		String sql ="select i.num,i.price,i.addon,g.sn,g.name,g.type_id,g.p11 p11,g.p8 p8 from es_order_items i inner join es_goods g on i.goods_id = g.goods_id where i.order_id=?";
		return this.daoSupport.queryForList(sql, orderid);
	}

	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}

	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
}
