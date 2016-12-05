package com.enation.app.shop.core.plugin.order;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.enation.app.shop.core.model.Allocation;
import com.enation.app.shop.core.model.AllocationItem;
import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.DeliveryItem;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.SellBackList;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.ICountPriceEvent;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;


/**
 * 订单插件桩
 * @author kingapex
 *
 */
public class OrderPluginBundle extends AutoRegisterPluginsBundle {

	@Override
	public String getName() {
		
		return "订单插件桩";
	}

	/**
	 * 获取订单详细页的选项卡
	 * @return
	 */
	public Map<Integer,String> getTabList(Order order){
		List<IPlugin> plugins = this.getPlugins();
		
		Map<Integer,String> tabMap = new TreeMap<Integer, String>();
		if (plugins != null) {
			
		 
			for (IPlugin plugin : plugins) {
				if(plugin instanceof IOrderTabShowEvent ){
					
					
					IOrderTabShowEvent event  = (IOrderTabShowEvent)plugin;
					
					/**
					 * 如果插件返回不执行，则跳过此插件
					 */
					if(!event.canBeExecute(order)){
						continue;
					}
					
					
					String name = event.getTabName(order);
					tabMap.put( event.getOrder(), name);
					 
					 
				}
			}
			
			 
		}
		return tabMap;
	}
	


    /**
     * 订单退货收货事件
     * @param 订单id
     * @author Chopper
     * 2015年10月28日15:05:00
     */
    public void confirm(Integer orderid,double price){
        try{
            List<IPlugin> plugins = this.getPlugins();
            //plugins 如果不为空
            if (plugins != null) {
                for (IPlugin plugin : plugins) { 
                    if (plugin instanceof IConfirmReceiptEvent) {
                        //如果 级别为debug 不输出这些日志
                        if (loger.isDebugEnabled()) {
                            loger.debug("调用插件 : " +plugin.getClass() + " onItemShip 开始...");
                        }
                        IConfirmReceiptEvent event = (IConfirmReceiptEvent) plugin;
                         event.confirm(orderid,price);
                        //如果 级别为debug 不输出这些日志
                        if (loger.isDebugEnabled()) {
                            loger.debug("调用插件 : " +plugin.getClass() + " onItemShip 结束.");
                        }
                    } 
                }
            }
        }catch(RuntimeException  e){
            if(this.loger.isErrorEnabled())
            this.loger.error("调用订单插件[onItemShip]事件错误", e);
            throw e;
        }
    }   
    
    

	/**
	 * 获取各个插件的html
	 * 
	 */
	public Map<Integer,String>   getDetailHtml(Order order) {
		 Map<Integer,String> htmlMap = new TreeMap<Integer,String>();
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("order",order);
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
			
				
				if (plugin instanceof IShowOrderDetailHtmlEvent) {
					IShowOrderDetailHtmlEvent event = (IShowOrderDetailHtmlEvent) plugin;
					freeMarkerPaser.setClz(event.getClass());
					if(plugin instanceof IOrderTabShowEvent){
						
						IOrderTabShowEvent tabEvent =  (IOrderTabShowEvent)plugin;
						
						/**
						 * 如果插件返回不执行，则跳过此插件
						 */
						if(!tabEvent.canBeExecute(order)){
							continue;
						}
						String html =event.onShowOrderDetailHtml(order);
						htmlMap.put(tabEvent.getOrder(), html);
					}
				}
			}
		}
		
		
		return htmlMap;
	}
	
	
	public void onBeforeCreate(Order order,List<CartItem>   itemList,String sessionid){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IBeforeOrderCreateEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onBeforeCreate 开始...");
						}
						IBeforeOrderCreateEvent event = (IBeforeOrderCreateEvent) plugin;
						event.onBeforeOrderCreate(order, itemList,sessionid);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onBeforeCreate 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[Before创建]事件错误", e);
			throw e;
		}
	}
	public void onAfterCreate(Order order,List<CartItem>   itemList,String sessionid){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IAfterOrderCreateEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAfterCreate 开始...");
						}
						IAfterOrderCreateEvent event = (IAfterOrderCreateEvent) plugin;
						event.onAfterOrderCreate(order, itemList,sessionid);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAfterCreate 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[After创建]事件错误", e);
			throw e;
		}
	}
	
	public void onFilter(Integer orderid,List<OrderItem> itemList){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderItemFilter) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onFilter 开始...");
						}
						IOrderItemFilter event = (IOrderItemFilter) plugin;
						event.filter(orderid,itemList);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onFilter 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[filter]事件错误", e);
			throw e;
		}
	}	
	
	
	
	
	/**
	 * 激发订单发货事件
	 * @param orderid
	 * @param itemList
	 */
	public void onShip(Delivery delivery,List<DeliveryItem> itemList){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderShipEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onShip 开始...");
						}
						IOrderShipEvent event = (IOrderShipEvent) plugin;
						event.ship(delivery, itemList);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onShip 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[ship]事件错误", e);
			throw e;
		}
	}	
	
	
	/**
	 * 激发订单项发货事件
	 * @param deliveryItem
	 * @param allocationItem
	 */
	public void onItemShip(Order order,DeliveryItem deliveryItem,AllocationItem allocationItem){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderShipEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onItemShip 开始...");
						}
						IOrderShipEvent event = (IOrderShipEvent) plugin;
						if(event.canBeExecute(allocationItem.getCat_id() )){
							event.itemShip(order,deliveryItem, allocationItem);
						}
						
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onItemShip 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[onItemShip]事件错误", e);
			throw e;
		}
	}	
	
	
	/**
	 * 订单退货
	 * @param delivery
	 * @param itemList
	 */
	public void onReturned(Delivery delivery,List<DeliveryItem> itemList){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderReturnsEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onReturned 开始...");
						}
						IOrderReturnsEvent event = (IOrderReturnsEvent) plugin;
						event.returned(delivery, itemList);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onReturned 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[returned]事件错误", e);
			
			throw e;
		}
	}	
	/**
	 * 订单退货
	 * @param delivery
	 * @param itemList
	 */
	public void onOrderSellback(SellBackList sellBackList){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderSellbackEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onReturned 开始...");
						}
						IOrderSellbackEvent event = (IOrderSellbackEvent) plugin;
						event.onOrderSellback(sellBackList);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onReturned 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[returned]事件错误", e);
			
			throw e;
		}
	}	
	
	
	/**
	 * 激发订单删除事件
	 * @param orderId
	 */
	public void onDelete(Integer[] orderId){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderDeleteEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onDelete 开始...");
						}
						IOrderDeleteEvent event = (IOrderDeleteEvent) plugin;
						event.delete(orderId);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onDelete 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[delete]事件错误", e);
			throw e;
		}
	}
	

	
	/**
	 * 激发订单项配货事件
	 * @param orderId
	 */
	public void onAllocationItem(AllocationItem allocationItem){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderAllocationItemEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAllocationItem 开始...");
						}
						IOrderAllocationItemEvent event = (IOrderAllocationItemEvent) plugin;
						if(event.canBeExecute(allocationItem.getCat_id())){
							 event.onAllocation(allocationItem);
						}
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAllocationItem 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
				this.loger.error("调用订单插件[onAllocationItem]事件错误", e);
			
			throw e;
		}
	}	
	
	
	/**
	 * 激发订单配货事件
	 * @param orderId
	 */
	public void onAllocation(Allocation allocation){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderAllocationEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAllocation 开始...");
						}
						IOrderAllocationEvent event = (IOrderAllocationEvent) plugin;
						event.onAllocation(allocation);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAllocation 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
				this.loger.error("调用订单插件[onAllocation]事件错误", e);
			
			throw e;
		}
	}		
	
	
	
	/**
	 * 获取某个订单项的各库房的库存情况
	 * @param item
	 * @return
	 */
/*	public String getAllocationHtml(OrderItem item){
		String  html = null;
		try{
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderAllocationItemEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " getAllocationHtml 开始...");
						}
						IOrderAllocationItemEvent event = (IOrderAllocationItemEvent) plugin;
						if( event.canBeExecute(item.getCat_id()) ){
							html = ((IOrderAllocationItemEvent) plugin).getAllocationStoreHtml(item);
						} 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " getAllocationHtml 结束.");
						}
					} 
				}
			}
			
			return html;
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[getAllocationHtml]事件错误", e);
			
			throw e;
		}
	} 
	*/
	/**
	 * 获取配货情况html
	 * @param item
	 * @return
	 */
/*	public String getAllocationViewHtml(OrderItem item){
		String  html = null;
		try{
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderAllocationItemEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " getAllocationViewHtml 开始...");
						}
						IOrderAllocationItemEvent event = (IOrderAllocationItemEvent) plugin;
						if( event.canBeExecute(item.getCat_id()) ){
							html = ((IOrderAllocationItemEvent) plugin).getAllocationViewHtml(item);
						} 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " getAllocationViewHtml 结束.");
						}
					} 
				}
			}
			
			return html;
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[getAllocationViewHtml]事件错误", e);
			throw e;
		}
		
	}*/
	
	
	
	/**
	 * 显示配货单时过滤相应项
	 * @param catid
	 * @param values
	 * @param rs
	 * @throws SQLException 
	 */
	public void filterAlloItem(int catid,Map values,ResultSet rs) throws SQLException{
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderAllocationItemEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " filterAlloViewItem 开始...");
						}
						IOrderAllocationItemEvent event = (IOrderAllocationItemEvent) plugin;
						if( event.canBeExecute(catid) ){
						  ((IOrderAllocationItemEvent) plugin).filterAlloViewItem(values, rs);
						} 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " filterAlloViewItem 结束.");
						}
					} 
				}
			}
			
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[filterAlloViewItem]事件错误", e);
			throw e;
		}
		
	}
 
	
 
	
	
	/**
	 * 获取某个订单项的各库房的库存情况
	 * @param item
	 * @return
	 */
	public String getAllocationHtml(OrderItem item){
		String  html = null;
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderAllocationItemEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " getAllocationHtml 开始...");
						}
						IOrderAllocationItemEvent event = (IOrderAllocationItemEvent) plugin;
						if( event.canBeExecute(item.getCat_id()) ){
							html = ((IOrderAllocationItemEvent) plugin).getAllocationStoreHtml(item);
						} 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " getAllocationHtml 结束.");
						}
					} 
				}
			}
			
			return html;
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[getAllocationHtml]事件错误", e);
			
			throw e;
		}
	} 
	
	/**
	 * 获取配货情况html
	 * @param item
	 * @return
	 */
	public String getAllocationViewHtml(OrderItem item){
		String  html = null;
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderAllocationItemEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " getAllocationViewHtml 开始...");
						}
						IOrderAllocationItemEvent event = (IOrderAllocationItemEvent) plugin;
						if( event.canBeExecute(item.getCat_id()) ){
							html = ((IOrderAllocationItemEvent) plugin).getAllocationViewHtml(item);
						} 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " getAllocationViewHtml 结束.");
						}
					} 
				}
			}
			
			return html;
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[getAllocationViewHtml]事件错误", e);
			throw e;
		}
		
	}
	
	
	

	
	
	public void onPay(Order order ,boolean isOnline){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderPayEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " pay 开始...");
						}
						    IOrderPayEvent event = (IOrderPayEvent) plugin;
						   event.pay(order, isOnline);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " pay 结束.");
						}
					} 
				}
			} 
			
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[pay]事件错误", e);
			throw e;
		}		
	}
	
	public void onCanel(Order order){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderCanelEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " canel 开始...");
						}
						    IOrderCanelEvent event = (IOrderCanelEvent) plugin;
						   event.canel(order);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " canel 结束.");
						}
					} 
				}
			} 
			
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[canel]事件错误", e);
			throw e;
		}		
	}
	
	public void onRestore(Order order){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderRestoreEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " canel 开始...");
						}
						IOrderRestoreEvent event = (IOrderRestoreEvent) plugin;
						   event.restore(order);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " canel 结束.");
						}
					} 
				}
			} 
			
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[canel]事件错误", e);
			throw e;
		}		
	}
	
	
	
	
	public void onConfirmPay(Order order){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderConfirmPayEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " confirmPay 开始...");
						}
						IOrderConfirmPayEvent event = (IOrderConfirmPayEvent) plugin;
						   event.confirmPay(order);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " confirmPay 结束.");
						}
					} 
				}
			}
			
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[confirmPay]事件错误", e);
			throw e;
		}		
	}
	/**
	 * 确认收货
	 * @param order
	 */
	public void onRogconfirm(Order order) {
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderRogconfirmEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " confirmPay 开始...");
						}
						IOrderRogconfirmEvent event = (IOrderRogconfirmEvent) plugin;
						   event.rogConfirm(order);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " confirmPay 结束.");
						}
					} 
				}
			}
			
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[confirmPay]事件错误", e);
			throw e;
		}	
	}
	
	
	public void onItemSave(Order order,OrderItem item){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderItemSaveEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onItemSave 开始...");
						}
						IOrderItemSaveEvent event = (IOrderItemSaveEvent) plugin;
						 event.onItemSave(order,item);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onItemSave 结束.");
						}
					} 
				}
			}
			
		}catch(RuntimeException  e){
			 
			this.loger.error("调用订单插件[onItemSave]事件错误", e);
			throw e;
		}	
	}
	public void onOrderChangeDepot(Order order,int newdepotid,List<OrderItem> itemList){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderChangeDepotEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onItemSave 开始...");
						}
						IOrderChangeDepotEvent event = (IOrderChangeDepotEvent) plugin;
						 event.chaneDepot(order,newdepotid,itemList);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onItemSave 结束.");
						}
					} 
				}
			}
			
		}catch(RuntimeException  e){
			 
			this.loger.error("调用订单插件[onItemSave]事件错误", e);
			throw e;
		}	
	}
	
	
	public Map<Integer,String> getStatisTabList(){
		List<IPlugin> plugins = this.getPlugins();
		Map<Integer,String> tabMap = new TreeMap<Integer, String>();
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if(plugin instanceof IOrderStatisTabShowEvent ){
					
					IOrderStatisTabShowEvent event  = (IOrderStatisTabShowEvent)plugin;
					String name = event.getTabName();
					tabMap.put( event.getOrder(), name);
				}
			}
		}
		return tabMap;
	}
	
	public Map<Integer,String>   getStatisDetailHtml(Map map) {
		
		Map<Integer,String> htmlMap = new TreeMap<Integer,String>();
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				
				if (plugin instanceof IOrderStatisDetailHtmlEvent) {
					IOrderStatisDetailHtmlEvent event = (IOrderStatisDetailHtmlEvent) plugin;
					freeMarkerPaser.setClz(event.getClass());
					
					if(plugin instanceof IOrderStatisTabShowEvent){
						IOrderStatisTabShowEvent tabEvent =  (IOrderStatisTabShowEvent)plugin;
						String html =event.onShowOrderDetailHtml(map);
						htmlMap.put(tabEvent.getOrder(), html);
					}
				}
			}
		}
		
		
		return htmlMap;
	}
	
	
	/**
	 * 激发订单价格计算事件<br>
	 * 此方法以前在cartPluginBundle中，现转移至orderPlugin
	 * 
	 * @param orderpice
	 * @param map
	 * @return
	 */
	public OrderPrice coutPrice(OrderPrice orderpice){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof ICountPriceEvent ) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ "cart.countPrice开始...");
						}
						ICountPriceEvent event = (ICountPriceEvent) plugin;
						orderpice =event.countPrice(orderpice);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " cart.countPrice结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			}
		
			
		}catch(Exception e){
			e.printStackTrace();
			 
		}
		 
 		return orderpice;
	}
	 
	
}


