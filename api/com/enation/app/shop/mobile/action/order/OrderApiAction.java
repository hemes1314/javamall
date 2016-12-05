/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：订单Api
 * 修改人：  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.order;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.enation.app.b2b2c.core.action.backend.order.StoreOrderAction;
import com.enation.app.b2b2c.core.pluin.order.StoreCartPluginBundle;
import com.enation.app.b2b2c.core.service.IStoreBonusManager;
import com.enation.app.b2b2c.core.service.IStoreDlyTypeManager;
import com.enation.app.b2b2c.core.service.IStoreMemberAddressManager;
import com.enation.app.b2b2c.core.service.IStoreTemplateManager;
import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.b2b2c.core.service.StoreCartKeyEnum;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.component.receipt.service.IReceiptManager;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IMemberOrderManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.mobile.model.ApiOrderList;
import com.enation.app.shop.mobile.util.HttpUtils;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;
import com.enation.framework.util.TestUtil;

/**
 * 订单Api
 * 提供
 * @author Sylow
 * @version v1.0
 * @since v1.0
 */
@SuppressWarnings({"rawtypes", "serial", "unchecked"})
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("order")
public class OrderApiAction extends WWAction {

    private IOrderManager orderManager;
    private IMemberAddressManager memberAddressManager;
    private IPaymentManager paymentManager;
    private ICartManager cartManager;
    private IDlyTypeManager dlyTypeManager;
    private IMemberOrderManager memberOrderManager;
    private IReceiptManager receiptManager;
    private IOrderFlowManager orderFlowManager;
    private IMemberManager memberManager;
    private IStoreCartManager storeCartManager;
    private IStoreMemberAddressManager storeMemberAddressManager;
    private IStoreTemplateManager storeTemplateManager;
    private IStoreDlyTypeManager storeDlyTypeManager;
    private IStoreOrderManager storeOrderManager;
    private IBonusManager bonusManager;
    private CartPluginBundle cartPluginBundle;
    private IProductManager productManager;
    private IStoreBonusManager storeBonusManager;
    private IGoodsManager goodsManager;
    private IRegionsManager regionsManager;
    private IDaoSupport daoSupport;
    private IMemberPointManger memberPointManger;

    private final int PAGE_SIZE = 20;
    //支付
    private String order_sn;
    private Double money;
    private String mark;
    
    //余额支付
    private Double advancePay;
    //erp
    private String order_state;
    private String status;
    private String paystatus;
    private String shipstatus;
    private String shipping_type;
    // 红包
    private Integer order_id;
    private Integer bonus_id;
    private Integer regionid;
    //店铺优惠券
    private Integer storeBonusId;
    private Integer storeId;
    
    // app端传传过来计算价格用的店铺ID列表.以分号分割（1;2;3）
    private String storeIdsForApp; 
//    保存配送方式时参数（格式是以店铺ID和配送方式ID为简直对的列表 如：1:3;2:4）
    private String saveShippingGroupParam;
    private StoreOrderAction storeOrderAction;

    //余额支付
    private int useBalance;
	/**
     * 获取支付及配送方式
     * @return
     */
    public Map payment(){
        //支付方式
        

        //配送方式
        /*HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
        String sessionid =  request.getSession().getId();
        Double orderPrice = cartManager.countGoodsTotal(sessionid);
        Double weight = cartManager.countGoodsWeight(sessionid);
        int regionId = NumberUtils.toInt(request.getParameter("regionid"), 0);
        List<DlyType> dlyTypeList = this.dlyTypeManager.list(weight, orderPrice,"" + regionId);*/

		List<PayCfg> paymentList  = this.paymentManager.list();
		Map data = new HashMap();
		for(PayCfg pay:paymentList){
			pay.setBiref("");
			pay.setConfig("");
			pay.setType("");
		}

		data.put("payment", paymentList);

		return data;
    }
    
    /**
     * 获取购物车所有商品、商品所对应的商家、商家的配送方式等信息
     * @return
     */
    public String storeCartGoods(){
		Member member = UserConext.getCurrentMember();
		if (member == null) {
		    this.json = JsonMessageUtil.expireSession();
			return WWAction.JSON_MESSAGE;
		}
    	try {
    		String region =null;
    		//传递支付方式
    		Map payment =this.payment();
    		
    		//更新购物车
    		String cart_id= this.updateNum();
    		//清空优惠券和红包
    		BonusSession.cleanAll();;
			List<Map> list = StoreCartContainer.getStoreCartListFromSession();
			
			MemberAddress address =StoreCartContainer.getUserSelectedAddress();
			Map map = new HashMap();
			 //由购物车列表中获取此店铺的相关信息

			//计算初始价格
			List<CartItem> cartList  = cartManager.getByCartId(cart_id);
			map.put("address_name", address==null?null:address.getName());
			map.put("address_mobile", address==null?null:address.getMobile());
			map.put("address_id", address==null?null:address.getAddr_id());
			map.put("address_country", address==null?null:address.getCountry());
			map.put("address_province", address==null?null:address.getProvince());
			map.put("address_city", address==null?null:address.getCity());
			map.put("address_region", address==null?null:address.getRegion());
			map.put("address_addr", address==null?null:address.getAddr());
			map.put("address_zip",address==null?null:address.getZip());
			region = address==null?null:""+address.getRegion_id();

			// TODO 此处取出来运费模板 如果为空 给设置上默认的运费模板
			// List<CartItem>

			//传递支付方式
    		HashMap defaultShippingGroupByStoreId = new HashMap<Integer, Integer>();
			for (CartItem cartItem : cartList) {
				Map storeDataMap = null;
				if (cartItem.getStore_id() != null && cartItem.getStore_id() > 0) {
					// 由购物车列表中获取此店铺的相关信息
					storeDataMap = StoreCartContainer.getStoreData(cartItem.getStore_id());
				} else {
					this.logger.error("cart中店铺信息异常");
				}

				if (storeDataMap != null) {

//					TODO 此处要不要再验证一下 之前勾选的配送方式  在默认模板中是否还存在
					if (storeDataMap.get(StoreCartKeyEnum.shiptypeid.toString()) == null) {

						List<DlyType> dlyTypeList = dlyTypeManager.getDefDlyTypesByStoreId(cartItem.getStore_id());

						if (dlyTypeList != null && dlyTypeList.size() > 0) {

							// 设置成默认的运费模板中第一个配送方式
							// 重新压入此店铺的订单价格和配送方式id
							storeDataMap.put(StoreCartKeyEnum.shiptypeid.toString(), dlyTypeList.get(0).getType_id());
							this.logger.info("设置成默认的运费模板中第一个配送方式:" + dlyTypeList.get(0).getType_id());
							defaultShippingGroupByStoreId.put(cartItem.getStore_id(), dlyTypeList.get(0).getType_id());
						} else {
							this.logger.error("没有查询到默认的运费模板，店铺ID：" + cartItem.getStore_id());
						}

					} else {
						defaultShippingGroupByStoreId.put(cartItem.getStore_id(), storeDataMap.get(StoreCartKeyEnum.shiptypeid.toString()));
					}

				} else {
					this.logger.error("session中未查询到店铺信息，店铺ID：" + cartItem.getStore_id());
				}

			}
			
			
			
    		//计算订单价格
    		OrderPrice orderprice  =this.cartManager.countPrice(cartList, 0 ,region);   	

            //激发价格计算事件
            orderprice  = cartPluginBundle.coutPrice(orderprice);
    		//红包列表
    		Double goodsprice = 99999999.0;
			List bonusList  = storeBonusManager.getMemberBonusList(member.getMember_id(), null, goodsprice);
			map.put("store_list", list);
			map.put("bonusList", bonusList);
			map.put("orderprice", orderprice);
			map.put("payment", payment);
			//传递用户预存款
			map.put("myAdvance",member.getAdvance()+member.getVirtual());
				
//			TODO 用户之前选择的默认运费模板信息
			map.put("defaultShippingGroupByStoreId", defaultShippingGroupByStoreId);
			this.json =JsonMessageUtil.getMobileObjectJson(map);
    	} catch(RuntimeException e) {
    	    String errer = (e.toString()).replace("java.lang.RuntimeException:"," ");
			this.showPlainErrorJson(errer);
    	}
    	return WWAction.JSON_MESSAGE;
    }
    /**
     * 结算的时候 先更新购物车
     */
    public String updateNum(){
    		HttpServletRequest request =ThreadContextHolder.getHttpRequest();
    		Member member = UserConext.getCurrentMember();
    		if (member == null) {
    		    this.json = JsonMessageUtil.expireSession();
    			return WWAction.JSON_MESSAGE;
    		}
			String countCart =request.getParameter("countCart");
			String cart_id ="";
			String str = "";
			List<Map<String, Object>> countCartList = JsonUtil.toList(countCart);
			for(Map cart:countCartList){
				Integer cartId = NumberUtils.toInt(cart.get("cart_id").toString());
				Integer cartNum = NumberUtils.toInt(cart.get("cart_num").toString());
				
				cart_id=cart_id+str+cartId;
				str=",";
				Cart cartmap =cartManager.get(NumberUtils.toInt(cart.get("cart_id").toString()));
				Product product = productManager.get(cartmap.getProduct_id());
				
				//add by lxl  增加校验： 添加购物车后删除商品或者下架商品
				Integer goodsId = product.getGoods_id();
				Goods goods = goodsManager.getGoods(goodsId);
				if (goods.getDisabled() ==1){
				    throw new RuntimeException("您要购买的"+goods.getName()+" 已被商家删除！");
				}
				if (goods.getMarket_enable() == 0){
				    throw new RuntimeException("您要购买的"+cartmap.getName()+" 已被商家下架！");
				}
				//end
				
				  //验证活动商品数量
                if(!validCartItemNumber(goodsId, cartNum)){
                   return null;
                }
                
				
				
				Integer store = product.getEnable_store();

				if (store == null)
					store = 0;
				if(store >= cartNum){
					cartManager.updateNumForApp(request.getSession().getId(),
							cartId, cartNum);
				}else{
					  throw new RuntimeException("要购买的"+cartmap.getName()+"数量超出库存！");
				}
				
				
				
				
			}
            ThreadContextHolder.getSessionContext().removeAttribute("user_selected_address");
			ThreadContextHolder.getSessionContext().removeAttribute("cart_id");
			this.storeCartManager.countPriceForApp("yes",cart_id);
			ThreadContextHolder.getSessionContext().setAttribute("cart_id",cart_id);
			return cart_id;
    }


	/**
     * 获得订单总价
     * @param address_id 必填 地址id
     * @param shipping_id 必填 配送方式id
     * @return
     */
    public String getTotalPrice(){
    	
    	try {
    		
    		HttpServletRequest request =ThreadContextHolder.getHttpRequest(); 
    		String cart_id =(String)ThreadContextHolder.getSessionContext().getAttribute("cart_id");
    		Integer shippingId = StringUtil.toInt(request.getParameter("type_id"),0);
	        //使用红包
	        this.useOne(bonus_id);
	        //使用余额
	        this.useBalance();
	       
	        //配送地区
	        String regionid_str = regionid==null?"":regionid+"";
	        if (storeId != null){
	            this.changeArgsType();
	        }
	     
	        List<CartItem> cartList  = cartManager.getByCartId(cart_id);
    		//计算订单价格

    		OrderPrice orderprice  =this.cartManager.countPrice(cartList, shippingId,regionid_str);

    		//激发价格计算事件
    		orderprice  = cartPluginBundle.coutPrice(orderprice);
    		
    		this.json = JsonMessageUtil.getMobileObjectJson(orderprice);
    	} catch(RuntimeException e) {
    		TestUtil.print(e);
    		this.logger.error("获取订单总价出错", e);
			this.showPlainErrorJson("获取订单总价出错[" + e.getMessage() + "]");
    		
    	}
    	
    	return WWAction.JSON_MESSAGE;
    }
    
    /**
     * 获得订单总价(app端专用，按店铺ID循环计算)
     * @param address_id 必填 地址id
     * @param shipping_id 必填 配送方式id
     * @return
     */
    public String getTotalPriceForApp(){
    	
    	try {
    		
    		HttpServletRequest request =ThreadContextHolder.getHttpRequest(); 
    		String cart_id =(String)ThreadContextHolder.getSessionContext().getAttribute("cart_id");
    		Integer shippingId = StringUtil.toInt(request.getParameter("type_id"),0);
	        //使用红包
	        this.useOne(bonus_id);
	        //使用余额
	        this.useBalance();
	       
	        //配送地区
	        String regionid_str = regionid==null?"":regionid+"";
	        
			// 按店铺ID循环计算价格
			String[] storeIdsForAppArray = null;
			if (storeIdsForApp != null) {
				storeIdsForAppArray = storeIdsForApp.split(";");
			}
			if (storeIdsForAppArray != null && storeIdsForAppArray.length > 0) {

				for (String storeIdForApp : storeIdsForAppArray) {
					this.changeArgsTypeForApp(NumberUtils.toInt(storeIdForApp));
				}

			}
	     
	        List<CartItem> cartList  = cartManager.getByCartId(cart_id);
    		//计算订单价格

    		OrderPrice orderprice  =this.cartManager.countPrice(cartList, shippingId,regionid_str);

    		//激发价格计算事件
    		orderprice  = cartPluginBundle.coutPrice(orderprice);
    		
    		this.json = JsonMessageUtil.getMobileObjectJson(orderprice);
    	} catch(RuntimeException e) {
    		TestUtil.print(e);
    		this.logger.error("获取订单总价出错", e);
			this.showPlainErrorJson("获取订单总价出错[" + e.getMessage() + "]");
    		
    	}
    	
    	return WWAction.JSON_MESSAGE;
    }
    
	/**
	 * 根据店铺ID列表， 批量获取默认运费模板
	 * 
	 * @param address_id
	 *            必填 地址id
	 * @return
	 */
	public String getDefDlyTypesMapByStoreIds() {

		try {

			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			// String cart_id = (String)
			// ThreadContextHolder.getSessionContext().getAttribute("cart_id");
			// Integer shippingId =
			// StringUtil.toInt(request.getParameter("type_id"), 0);

			HashMap<String, List<DlyType>> dlyTypeListGroupByStoreId = new HashMap<String, List<DlyType>>();
			// 按店铺ID循环计算价格
			String[] storeIdsForAppArray = null;
			if (storeIdsForApp != null) {
				this.logger.info("根据店铺ID列表， 批量获取默认运费模板中的参数storeIdsForApp:" + storeIdsForApp);
				storeIdsForAppArray = storeIdsForApp.split(";");
			} else {
				this.logger.error("根据店铺ID列表， 批量获取默认运费模板中的参数storeIdsForApp为空");
			}
			if (storeIdsForAppArray != null && storeIdsForAppArray.length > 0) {

				for (String storeIdForApp : storeIdsForAppArray) {

					List<DlyType> dlyTypeList = dlyTypeManager.getDefDlyTypesByStoreId(NumberUtils.toInt(storeIdForApp));
					dlyTypeListGroupByStoreId.put(storeIdForApp, dlyTypeList);
				}

			}

			this.json = JsonMessageUtil.getMobileObjectJson(dlyTypeListGroupByStoreId);
		} catch (RuntimeException e) {
			TestUtil.print(e);
			this.logger.error("根据店铺ID列表， 批量获取默认运费模板出现异常", e);
			this.showPlainErrorJson("根据店铺ID列表， 批量获取默认运费模板出现异常[" + e.getMessage() + "]");

		}

		return WWAction.JSON_MESSAGE;
	}
	
	
	/**
	 * 保存购物车中的配送方式列表
	 * 
	 * @param 保存配送方式时参数（格式是以店铺ID和配送方式ID为简直对的列表
	 *            如：1:3;2:4）
	 * @return
	 */
	public String saveShippingGroupForApp() {

		try {

			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			// String cart_id = (String)
			// ThreadContextHolder.getSessionContext().getAttribute("cart_id");
			// Integer shippingId =
			// StringUtil.toInt(request.getParameter("type_id"), 0);

			// 按店铺ID循环计算价格
			String[] saveShippingGroupParamArray = null;
			if (saveShippingGroupParam != null) {
				this.logger.info("保存购物车中的配送方式列表中的参数saveShippingGroupParam:" + saveShippingGroupParam);
				saveShippingGroupParamArray = saveShippingGroupParam.split(";");
			} else {
				this.logger.error("根据店铺ID列表， 批量获取默认运费模板中的参数saveShippingGroupParam为空");

				this.showPlainErrorJson("保存配送方式失败,失败原因是入参为空！");
				return WWAction.JSON_MESSAGE;
			}
			if (saveShippingGroupParamArray != null && saveShippingGroupParamArray.length > 0) {

				for (String storeIdAndTypeId : saveShippingGroupParamArray) {

					String[] paramArray = null;
					if (storeIdAndTypeId != null) {
						paramArray = storeIdAndTypeId.split(":");
					}

					if (paramArray != null && paramArray.length == 2) {

						// 由购物车列表中获取此店铺的相关信息
						@SuppressWarnings("rawtypes")
						Map storeData = StoreCartContainer.getStoreData(NumberUtils.toInt(paramArray[0]));

						if (storeData != null) {
							// 重新压入此店铺的订单价格和配送方式id
							storeData.put(StoreCartKeyEnum.shiptypeid.toString(), paramArray[1]);
						} else {
							this.logger.error("保存配送方式未全部成功！原因是未取得到购物车店铺缓存，店铺id:" + paramArray[0] != null ? paramArray[0]
									: "null");
						}

					}
				}

			} else {
				this.logger.error("保存配送方式失败,失败原因是入参不符合规范！入参saveShippingGroupParam:" + saveShippingGroupParam);
				this.showPlainErrorJson("保存配送方式失败,失败原因是入参不符合规范！");
				return WWAction.JSON_MESSAGE;
			}

			this.showSuccessJson("保存配送方式成功!");
		} catch (RuntimeException e) {
			TestUtil.print(e);
			this.logger.error("根据店铺ID列表， 批量获取默认运费模板出现异常", e);
			this.showPlainErrorJson("根据店铺ID列表， 批量获取默认运费模板出现异常[" + e.getMessage() + "]");

		}

		return WWAction.JSON_MESSAGE;
	}
    
    /**
     * 提交订单
     * @return
     */
    public String create() {
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
    	try{
    	    String sessionid = request.getSession().getId();
    		Member member = UserConext.getCurrentMember();
	        if (member == null) {
	            this.json = JsonMessageUtil.expireSession();
	            return JSON_MESSAGE;
	        }
	   
	
	        Integer shippingId = StringUtil.toInt(request.getParameter("typeId"),0);
	        if (shippingId == null){
	            this.showPlainErrorJson("配送方式不能为空");
	            return JSON_MESSAGE;
	        }
	
	        Integer paymentId = StringUtil.toInt(request.getParameter("paymentId"), 0);
	        if (paymentId == 0) {
	            this.showPlainErrorJson("支付方式不能为空");
	            return JSON_MESSAGE;
	        }
	        Order order = new Order();
	        order.setShipping_id(shippingId); //配送方式
	        order.setPayment_id(paymentId);//支付方式
	        Integer is_storage =StringUtil.toInt(request.getParameter("is_storage"),0);
	        order.setIs_storage(is_storage);
	        if(is_storage ==1){
	        	order.setStart_storage_time(DateUtil.getDateline());
	        	order.setEnd_storage_time(DateUtil.getDateline()+2592000);
	        	order.setStorage_date(30);
	        }
	
	        //收货地址
	        int addressId = NumberUtils.toInt(request.getParameter("addressId"), 0);
	        MemberAddress address = memberAddressManager.getAddress(addressId);
	        if(address == null){
	            this.showPlainErrorJson("请选择收货地址！");
	            return JSON_MESSAGE;
	        }
	        //check 该地址是否支持货到付款
	        if (paymentManager.get(paymentId).getType().equals("cod") && regionsManager.get(address.getRegion_id()).getCod() != 1) {    
	              this.showPlainErrorJson("该地址不支持货到付款");
	              return JSON_MESSAGE;
	        }
	        order.setShip_provinceid(address.getProvince_id());
	        order.setShip_cityid(address.getCity_id());
	        order.setShip_regionid(address.getRegion_id());
	
	        order.setShip_addr(address.getAddr());
	        order.setShip_mobile(address.getMobile());
	        order.setShip_tel(address.getTel());
	        order.setShip_zip(address.getZip());
//	        order.setShipping_area(address.getProvince() + "-" + address.getCity() + "-" + address.getRegion());
	        order.setShipping_area(address.getProvince() + address.getCity() + address.getRegion());
	        order.setShip_name(address.getName());
	        order.setRegionid(address.getRegion_id());
	
	        order.setMemberAddress(address);
	        order.setShip_day(request.getParameter("shipDay"));
	        order.setShip_time(request.getParameter("shipTime"));
	        order.setRemark(request.getParameter("remark"));
	        order.setAddress_id(address.getAddr_id());//保存本订单的会员地址id
	        order.setPack(request.getParameter("pack"));// 商品包装
	        String cart_id =(String)ThreadContextHolder.getSessionContext().getAttribute("cart_id");

	        //new update  lxl
	        order =this.storeOrderManager.createOrderForApp(order, sessionid ,cart_id);        
	        this.showPlainSuccessJson(order.getSn());
 
    	} catch(RuntimeException e) {
    		TestUtil.print(e);
			this.logger.error("提交订单出错", e);
			this.showPlainErrorJson("提交订单出错[" + e.getMessage() + "]");
    	}
        return JSON_MESSAGE;
    }

    /**
     *  订单列表
     * @return
     */
	public String list(){
		try {
			Member member = UserConext.getCurrentMember();

			if (member == null) {
			    this.json = JsonMessageUtil.expireSession();
				return JSON_MESSAGE;
			}
			
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			Map result = new HashMap();
			int page =NumberUtils.toInt(request.getParameter("page"),1) ;
			String  status =request.getParameter("status");
			Page ordersPage = orderManager.listByStatusForApp(page, PAGE_SIZE, status,member.getMember_id());
			List<ApiOrderList> orderlist= (List<ApiOrderList>) ordersPage.getResult();
			for (ApiOrderList order : orderlist ){
			    order.setOrderStatus(order.getOrderStatus());
			    order.setPayStatus(order.getPayStatus());
			    List<OrderItem> orderItemList = orderManager.listGoodsItems(order.getOrder_id());
			    order.setOrderItem(orderItemList.get(0));
			    
			}			
			result.put("ordersList", orderlist);
			//String data = JsonUtil.MapToJson(result);

			this.json = JsonMessageUtil.getMobileObjectJson(result);

		} catch (RuntimeException e) {
			this.logger.error("获取订单列表出错", e);
			this.showPlainErrorJson(e.getMessage());
		}

		return WWAction.JSON_MESSAGE;
    }
    /**
     * 订单商品详细
     * lxl
     */
    public String orderDetail(){
        try {
            Member member = UserConext.getCurrentMember();

            if (member == null) {
                this.json = JsonMessageUtil.expireSession();
                return JSON_MESSAGE;
            }
            
            List<OrderItem> orderItemList = orderManager.listGoodsItems(order_id);          
            this.json =JsonMessageUtil.getMobileListJson(orderItemList);

        } catch (RuntimeException e) {
            this.logger.error("获取订单列表出错", e);
            this.showPlainErrorJson(e.getMessage());
        }

        return WWAction.JSON_MESSAGE;
    }
	/**
     * 订单详细
     * @return
     */
    public String detail(){
        Member member =UserConext.getCurrentMember();

        if (member == null) {
            this.json = JsonMessageUtil.expireSession();
            return WWAction.JSON_MESSAGE;
        }

        HttpServletRequest request = getRequest();
        Integer orderId  = NumberUtils.toInt(request.getParameter("order_id"));
        Order order = orderManager.get(orderId);
        
        if(order == null){
            this.showPlainErrorJson("此订单不存在！");
            return WWAction.JSON_MESSAGE;
        }
        if(!order.getMember_id().equals(member.getMember_id())){
            this.showPlainErrorJson("您没有权限进行此项操作！");
            return WWAction.JSON_MESSAGE;
        }
        try{
            Map dataMap = new HashMap();
            dataMap.put("order", order);
            dataMap.put("receipt", receiptManager.getByOrderid(order.getOrder_id()));
            this.json = JsonMessageUtil.getMobileObjectJson(dataMap);
        }catch (RuntimeException e){
            this.showPlainErrorJson("数据库运行异常"+e);
        }
       
        return WWAction.JSON_MESSAGE;
    }
   
    /**
     * 取消订单
     * @param sn:订单序列号.String型，必填项
     *
     * @return 返回json串
     * result  为1表示添加成功0表示失败 ，int型
     * message 为提示信息
     */

    public String cancel() {
        Member member =UserConext.getCurrentMember();
        if (member == null) {
            this.json = JsonMessageUtil.expireSession();
        } else {
            HttpServletRequest request = ThreadContextHolder.getHttpRequest();
            try {
                String sn = request.getParameter("sn");
                String reason = request.getParameter("reason");
                this.orderFlowManager.cancel(sn, reason);
                this.showSuccessJson("取消订单成功");
            } catch (RuntimeException re) {
                this.showErrorJson(re.getMessage());
            }
        }
        // 临时添加jsonp支持
        String callback = getRequest().getParameter("callback");
        if (StringUtils.isNotBlank(callback)) {
            PrintWriter writer = null;
            try {
                writer = ThreadContextHolder.getHttpResponse().getWriter();
                writer.write(HttpUtils.jsonp(callback, json));
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                writer.close();
            }
        }
        return WWAction.JSON_MESSAGE;
    }

    /**
     * 确认收货
     * @param orderId:订单id.String型，必填项
     *
     * @return 返回json串
     * result  为1表示添加成功0表示失败 ，int型
     * message 为提示信息
     */
    public String rogConfirm() {
    	JSONObject result = new JSONObject();
		result.put("success", false);
		result.put("errCode", -9);
    	try {
			String orderId = getRequest().getParameter("orderid");
	        Member member = UserConext.getCurrentMember();
	        if (member == null) {
	        	result.put("errCode", -1);
	            this.json = JsonMessageUtil.expireSession();
	        } else {
				Order order = this.orderManager.get(NumberUtils.toInt(orderId));
				if (order == null) {
					this.showErrorJson("订单不存在！");
				} else if (member.getMember_id().compareTo(order.getMember_id()) != 0) {
					this.showErrorJson("对不起，您没有权限进行此项操作！");
				} else if (order.getStatus().intValue() != OrderStatus.ORDER_SHIP) {
					this.showErrorJson("该订单状态已发生变化！");
				} else {
					this.orderFlowManager.rogConfirmtg(NumberUtils.toInt(orderId), member.getMember_id(), member.getUname(), member.getUname(), DateUtil.getDateline(), status);
					this.showSuccessJson("确认收货成功");
				}
	        }
    	} catch (Exception e) {
    		this.showErrorJson("数据库错误");
    	}
    	jsonp(result);
        return WWAction.JSON_MESSAGE;
    }
    
	/**
	 * 接受erp 传过来的参数: 该函数作废
	 *
	 * @see com.enation.app.shop.mobile.action.erp.ErpApiAction
	 */
	@Deprecated
    public String Orderlist(){
        HttpServletRequest request = getRequest();
        
        try{
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("payment_id", request.getParameter("payment_id"));
            map.put("status",  request.getParameter("status"));
            map.put("paystatus", request.getParameter("pay_status") );
            map.put("shipstatus", request.getParameter("ship_status") );
            map.put("store_id", request.getParameter("store_id"));
            String sort ="create_time" ;
            String order ="desc" ;
            List<Map<String, Object>> listorder = this.orderManager.listForErp1(map, sort, order);
            
            for (int i = 0; i < listorder.size(); i++) {
                Map<String, Object> orderItem = listorder.get(i);
                Integer provinceId = NumberUtils.toInt(orderItem.get("ship_provinceid").toString());
                Integer cityId = NumberUtils.toInt(orderItem.get("ship_cityid").toString());
                Integer regionId = NumberUtils.toInt(orderItem.get("ship_regionid").toString());
                Integer orderId = NumberUtils.toInt(orderItem.get("order_id").toString());
                
                if (provinceId != 0) {
                    orderItem.put("ship_provinceid", regionsManager.get(provinceId).getLocal_name() );
                }
                
                if (cityId != 0) {
                    orderItem.put("ship_cityid", regionsManager.get(cityId).getLocal_name());
                }
                
                if (regionId != 0) {
                    orderItem.put("ship_regionid", regionsManager.get(regionId).getLocal_name());
                }
                
                List<Map<String, Object>> goodsList = this.orderManager.getOrderItemList(orderId);
                orderItem.put("goods_list", goodsList);
            }
            
         this.json = JsonMessageUtil.getMobileListJson(listorder);
        }catch(Exception e){
            this.showErrorJson("数据库错误");
        }
        return WWAction.JSON_MESSAGE;
    }

    /**
     * 接受erp 传过来的参数: 该函数作废
	 *
     * @see com.enation.app.shop.mobile.action.erp.ErpApiAction
     */
	@Deprecated
    public String updateShipState(){

        HttpServletRequest request =getRequest();

        try{
            Map<String,String> map = new HashMap<String,String>();
            map.put("order_id", request.getParameter("order_id"));
            map.put("status", request.getParameter("status"));
            map.put("ship_no", request.getParameter("ship_no") );
            map.put("ship_status",  request.getParameter("ship_status"));
            map.put("is_erp_process",request.getParameter("is_erp_process"));
            //boolean flag=this.orderManager.updateShipStatus(map);
//            if(flag){
//                this.showPlainSuccessJson("更新成功");
//            }else{
//                this.showPlainSuccessJson("更新失败");
//            }
        }catch(Exception e){
            this.showErrorJson("数据库错误");
        }
        return WWAction.JSON_MESSAGE;

    }


    /**
     * 红包
     * @return
     */
	public String useOne(Integer bonus_id){
		try {
	
			//清除使用的红包
			if(bonus_id==0||bonus_id==null){
				BonusSession.clean();
				this.showPlainSuccessJson("清除红包成功");
				return JSON_MESSAGE;
			}
	
			MemberBonus bonus  =bonusManager.getBonus(bonus_id);				
			BonusSession.useOne(bonus);
			this.showSuccessJson("红包使用成功");
		} catch (Exception e) {
			this.showErrorJson("使用红包发生错误["+e.getMessage()+"]");
			this.logger.error("使用红包发生错误",e);
		}
		return JSON_MESSAGE;
	}
   public String useStoreBonus(){
        try {
    
            //清除使用的红包
            if(storeBonusId==0||storeBonusId==null){
                BonusSession.clean();
                this.showPlainSuccessJson("清除红包成功");
                return JSON_MESSAGE;
            }	            
            MemberBonus bonus  =bonusManager.getBonus(storeBonusId);                
            BonusSession.use(storeId, bonus);;
            this.showSuccessJson("红包使用成功");
        } catch (Exception e) {
            this.showErrorJson("使用红包发生错误["+e.getMessage()+"]");
            this.logger.error("使用红包发生错误",e);
        }
        return JSON_MESSAGE;
    }
   /**
    * 修改优惠券选项
    * 
    * @author liushuai
    * @version v1.0,2015年9月22日18:21:33
    * @param bonuss 
    * @since v1.0
    */
   private OrderPrice changeBonus(OrderPrice orderprice, MemberBonus bonus,int storeid) {
         
           // set 红包
           BonusSession.use(storeid,bonus);
           if (orderprice.getNeedPayMoney() <= bonus.getBonus_money()) {
               /******** 2015/10/16 humaodong ********/
               orderprice.setDiscountPrice(orderprice.getNeedPayMoney());
               /**************************************/
               orderprice.setNeedPayMoney(0.0);
           } else {
               // 计算需要支付的金额
               orderprice.setNeedPayMoney(CurrencyUtil.add(
                       orderprice.getNeedPayMoney(), -bonus.getBonus_money()));

               orderprice.setDiscountPrice(bonus.getBonus_money());
           }
           return orderprice; 

   } 
	/** 
	 * 我的存酒
	 * @return
	 */
	public String myStorage(){
		HttpServletRequest request =getRequest();
		try{
			Member member = UserConext.getCurrentMember();
			HashMap<String, Object> map = new HashMap<>();
			 if (member == null) {
			     this.json = JsonMessageUtil.expireSession();
		          return WWAction.JSON_MESSAGE;
			}
			long memberId =member.getMember_id();
			int pageNo = NumberUtils.toInt(request.getParameter("page"),1);
			Page mystorage = orderManager.getMystorage(memberId,pageNo,10);	
			map.put("myStorage", mystorage);
			this.json = JsonMessageUtil.getMobileObjectJson(map);
			
		}catch(RuntimeException e){
			this.showPlainErrorJson("数据库运行异常");
		}
		
		return WWAction.JSON_MESSAGE;
	}
	/**
	 * 取酒
	 * @return
	 */
	public String getWine(){

		Member member = UserConext.getCurrentMember();
		
		try{
			 if (member == null) {
			     this.json = JsonMessageUtil.expireSession();
		         return WWAction.JSON_MESSAGE;
			 }
			orderManager.getWine(member.getMember_id(),order_id);
			this.showPlainSuccessJson("取酒成功");
		}catch(RuntimeException e){
			this.showPlainErrorJson("数据库运行异常");
			
		}
		return WWAction.JSON_MESSAGE;
	}
	/**
     * 手动解冻积分 
     */
    public String thaw() {
        try{
            HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
            int orderid = StringUtil.toInt(request.getParameter("orderid"),true);
            this.memberPointManger.thaw(orderid);
            this.showPlainSuccessJson("成功解冻");
        }catch(RuntimeException e){
            this.logger.error("手动解冻积分"+ e.getMessage(),e);
            this.showPlainErrorJson(e.getMessage());
        }
        return WWAction.JSON_MESSAGE;
    }
	
	public String payUseAdv(){
	    
	    try{

	        Member member = UserConext.getCurrentMember();
           if (member == null) {
               this.json = JsonMessageUtil.expireSession();
               return WWAction.JSON_MESSAGE;
            }
	        memberManager.pay(member.getMember_id(), money, 0, order_sn, mark);
            this.showSuccessJson("支付成功");
	    }catch (RuntimeException e){
	        this.showSuccessJson("支付失败");
	    }
	    return WWAction.JSON_MESSAGE;
	}
	   //2015/10/14 humaodong
	public void advancePay() {
        if (advancePay == null || advancePay <= 0) {
            ThreadContextHolder.getSessionContext().removeAttribute("advancePay");
        } else {
            ThreadContextHolder.getSessionContext().setAttribute("advancePay", advancePay);
        }
    }
  //2015/10/26 humaodong
    public String useBalance() {
        if (useBalance == 0) {
            ThreadContextHolder.getSessionContext().removeAttribute("useBalance");
        } else {
            ThreadContextHolder.getSessionContext().setAttribute("useBalance", 1);
        }
        this.showPlainSuccessJson("ok");
        return JSON_MESSAGE;
    }
    /**
     * 改变店铺的配送方式以及红包<br>
     * 调用此api时必须已经访问过购物车列表<br>
     * @return 含有价格信息的json串
     */
    public String  changeArgsType(){
        
        //由购物车列表中获取此店铺的相关信息
        Map storeData =  StoreCartContainer.getStoreData(storeId);
        
        //获取此店铺的购物列表
        List list = (List) storeData.get(StoreCartKeyEnum.goodslist.toString());
        
        //配送地区     
        String regionid_str = regionid==null?"":regionid+"";
        //找到相应的配送方式
        Integer  type_id =(Integer) storeData.get(StoreCartKeyEnum.shiptypeid.toString());
        
        //计算此配送方式时的店铺相关价格
        OrderPrice orderPrice = this.cartManager.countPrice(list, type_id, regionid_str);
        
        //激发计算子订单价格事件
        orderPrice =storeCartPluginBundle.countChildPrice(orderPrice);
        if(storeBonusId!=0){
            MemberBonus bonuss = bonusManager.getBonus(storeBonusId); 
            changeBonus(orderPrice,bonuss,storeId);
        }
        
        Double needPayMoney = CurrencyUtil.add(orderPrice.getGoodsBasePrice(), orderPrice.getShippingPrice());
        needPayMoney = CurrencyUtil.sub(needPayMoney, orderPrice.getDiscountPrice());
        orderPrice.setNeedPayMoney(needPayMoney);
        orderPrice.setOrderPrice(needPayMoney);
        
        //重新压入此店铺的订单价格和配送方式id 
        storeData.put(StoreCartKeyEnum.storeprice.toString(), orderPrice);
        
        this.json = JsonMessageUtil.getObjectJson(orderPrice,"storeprice");
        
        return JSON_MESSAGE;
    }
    
    /**
     * 改变店铺的配送方式以及红包<br>
     * 调用此api时必须已经访问过购物车列表<br>
     * @return 含有价格信息的json串
     */
    public String  changeArgsTypeForApp(Integer pStoreId){
        
        //由购物车列表中获取此店铺的相关信息
        Map storeData =  StoreCartContainer.getStoreData(pStoreId);
        
        //获取此店铺的购物列表
        List list = (List) storeData.get(StoreCartKeyEnum.goodslist.toString());
        
        //配送地区     
        String regionid_str = regionid==null?"":regionid+"";
        //找到相应的配送方式
        Integer  type_id =(Integer) storeData.get(StoreCartKeyEnum.shiptypeid.toString());
        
        //计算此配送方式时的店铺相关价格
        OrderPrice orderPrice = this.cartManager.countPrice(list, type_id, regionid_str);
        
        //激发计算子订单价格事件
        orderPrice =storeCartPluginBundle.countChildPrice(orderPrice);
        if(storeBonusId!=0){
            MemberBonus bonuss = bonusManager.getBonus(storeBonusId); 
            changeBonus(orderPrice,bonuss,pStoreId);
        }
        
        Double needPayMoney = CurrencyUtil.add(orderPrice.getGoodsBasePrice(), orderPrice.getShippingPrice());
        needPayMoney = CurrencyUtil.sub(needPayMoney, orderPrice.getDiscountPrice());
        orderPrice.setNeedPayMoney(needPayMoney);
        orderPrice.setOrderPrice(needPayMoney);
        
        //重新压入此店铺的订单价格和配送方式id 
        storeData.put(StoreCartKeyEnum.storeprice.toString(), orderPrice);
        
        this.json = JsonMessageUtil.getObjectJson(orderPrice,"storeprice");
        
        return JSON_MESSAGE;
    }
    
    static String[] activeTables = new String[]{"groupbuy", "flashbuy", "secbuy", "advbuy"};
    /**
     * 验证添加到购物车中的商品数量是否超出限制
     * @param goodsId
     * @param addNum
     * @return
     */
    private boolean validCartItemNumber(int goodsId, int num){
        //edit by Tension 修改成从缓存获取商品信息    Map goods = goodsManager.get(goods_id);
        Map goodsMap = goodsManager.getByCache(goodsId);
        if(goodsMap == null){
            return false;
        }
        
        for(String tableName : activeTables){
            if(goodsMap.containsKey("is_" + tableName) && NumberUtils.toInt(goodsMap.get("is_" + tableName).toString(), 0) == 1){
                Map activeGoodsMap = this.daoSupport.queryForMap("SELECT * FROM es_" + tableName + "_goods WHERE goods_id=?", goodsMap.get("goods_id"));
                
                if(activeGoodsMap != null) {                   
                    //判断是否超售
                    if(NumberUtils.toInt(activeGoodsMap.get("goods_num").toString(), 0) <= 0){
                        throw new RuntimeException("要购买的活动商品 "+activeGoodsMap.get("goods_name")+" 已售罄！");
                    }
                    
                    //判断剩余商品是否能满足此次购买数量
                    if(num > NumberUtils.toInt(activeGoodsMap.get("goods_num").toString(), 0)){
                        throw new RuntimeException("要购买的"+activeGoodsMap.get("goods_name")+"超出购买限制，此商品仅剩余" + activeGoodsMap.get("goods_num") + "件！");
                    }
                    
                    //是否超出单次购买数量限制
                    int limit_num = NumberUtils.toInt(activeGoodsMap.get("limit_num").toString(), 0);
                    if(limit_num > 0 && num > limit_num){

                        throw new RuntimeException("要购买的 活动商品 "+activeGoodsMap.get("goods_name")+"限制每人购买"+activeGoodsMap.get("limit_num")+"件！");
                    }
                }
            }
        }
        return true;
    }
    
    
    
	public Integer getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}

    public String getOrder_state() {
		return order_state;
	}

	public void setOrder_state(String order_state) {
		this.order_state = order_state;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaystatus() {
		return paystatus;
	}

	public void setPaystatus(String paystatus) {
		this.paystatus = paystatus;
	}

	public String getShipstatus() {
		return shipstatus;
	}

	public void setShipstatus(String shipstatus) {
		this.shipstatus = shipstatus;
	}

	public String getShipping_type() {
		return shipping_type;
	}

	public void setShipping_type(String shipping_type) {
		this.shipping_type = shipping_type;
	}

	public IOrderManager getOrderManager() {
        return orderManager;
    }

    public void setOrderManager(IOrderManager orderManager) {
        this.orderManager = orderManager;
    }

    public IMemberAddressManager getMemberAddressManager() {
        return memberAddressManager;
    }

    public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
        this.memberAddressManager = memberAddressManager;
    }

    public IPaymentManager getPaymentManager() {
        return paymentManager;
    }

    public void setPaymentManager(IPaymentManager paymentManager) {
        this.paymentManager = paymentManager;
    }

    public ICartManager getCartManager() {
        return cartManager;
    }

    public void setCartManager(ICartManager cartManager) {
        this.cartManager = cartManager;
    }

    public IDlyTypeManager getDlyTypeManager() {
        return dlyTypeManager;
    }

    public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
        this.dlyTypeManager = dlyTypeManager;
    }

    public IMemberOrderManager getMemberOrderManager() {
        return memberOrderManager;
    }

    public void setMemberOrderManager(IMemberOrderManager memberOrderManager) {
        this.memberOrderManager = memberOrderManager;
    }

    public IReceiptManager getReceiptManager() {
        return receiptManager;
    }

    public void setReceiptManager(IReceiptManager receiptManager) {
        this.receiptManager = receiptManager;
    }

    public IOrderFlowManager getOrderFlowManager() {
        return orderFlowManager;
    }

    public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
        this.orderFlowManager = orderFlowManager;
    }

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IStoreCartManager getStoreCartManager() {
		return storeCartManager;
	}

	public void setStoreCartManager(IStoreCartManager storeCartManager) {
		this.storeCartManager = storeCartManager;
	}

	public IStoreMemberAddressManager getStoreMemberAddressManager() {
		return storeMemberAddressManager;
	}

	public void setStoreMemberAddressManager(
			IStoreMemberAddressManager storeMemberAddressManager) {
		this.storeMemberAddressManager = storeMemberAddressManager;
	}

	public IStoreTemplateManager getStoreTemplateManager() {
		return storeTemplateManager;
	}

	public void setStoreTemplateManager(IStoreTemplateManager storeTemplateManager) {
		this.storeTemplateManager = storeTemplateManager;
	}

	public IStoreDlyTypeManager getStoreDlyTypeManager() {
		return storeDlyTypeManager;
	}

	public void setStoreDlyTypeManager(IStoreDlyTypeManager storeDlyTypeManager) {
		this.storeDlyTypeManager = storeDlyTypeManager;
	}

	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}

	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}

	public CartPluginBundle getCartPluginBundle() {
		return cartPluginBundle;
	}

	public void setCartPluginBundle(CartPluginBundle cartPluginBundle) {
		this.cartPluginBundle = cartPluginBundle;
	}

	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}
    public IBonusManager getBonusManager() {
        return bonusManager;
    }

    public void setBonusManager(IBonusManager bonusManager) {
        this.bonusManager = bonusManager;
    }
    
    private StoreCartPluginBundle storeCartPluginBundle; 

    
    public IStoreBonusManager getStoreBonusManager() {
        return storeBonusManager;
    }

    
    public void setStoreBonusManager(IStoreBonusManager storeBonusManager) {
        this.storeBonusManager = storeBonusManager;
    }

    
 
    public Integer getBonus_id() {
        return bonus_id;
    }

    
    public void setBonus_id(Integer bonus_id) {
        this.bonus_id = bonus_id;
    }

    
    public Integer getRegionid() {
        return regionid;
    }

    
    public void setRegionid(Integer regionid) {
        this.regionid = regionid;
    }

    
    public StoreOrderAction getStoreOrderAction() {
        return storeOrderAction;
    }

    
    public void setStoreOrderAction(StoreOrderAction storeOrderAction) {
        this.storeOrderAction = storeOrderAction;
    }

    
    public StoreCartPluginBundle getStoreCartPluginBundle() {
        return storeCartPluginBundle;
    }

    
    public void setStoreCartPluginBundle(StoreCartPluginBundle storeCartPluginBundle) {
        this.storeCartPluginBundle = storeCartPluginBundle;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    
    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    
    public Double getMoney() {
        return money;
    }

    
    public void setMoney(Double money) {
        this.money = money;
    }

    
    public String getMark() {
        return mark;
    }

    
    public void setMark(String mark) {
        this.mark = mark;
    }

    
    public Double getAdvancePay() {
        return advancePay;
    }

    
    public void setAdvancePay(Double advancePay) {
        this.advancePay = advancePay;
    }

    
    public Integer getStoreBonusId() {
        return storeBonusId;
    }

    
    public void setStoreBonusId(Integer storeBonusId) {
        this.storeBonusId = storeBonusId;
    }

    
    public Integer getStoreId() {
        return storeId;
    }

    
    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    
    public int getUseBalance() {
        return useBalance;
    }

    
    public void setUseBalance(int useBalance) {
        this.useBalance = useBalance;
    }

    
    public IGoodsManager getGoodsManager() {
        return goodsManager;
    }

    
    public void setGoodsManager(IGoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }

    
    public IRegionsManager getRegionsManager() {
        return regionsManager;
    }

    
    public void setRegionsManager(IRegionsManager regionsManager) {
        this.regionsManager = regionsManager;
    }

    
    public IDaoSupport getDaoSupport() {
        return daoSupport;
    }

    
    public void setDaoSupport(IDaoSupport daoSupport) {
        this.daoSupport = daoSupport;
    }

    
    public IMemberPointManger getMemberPointManger() {
        return memberPointManger;
    }

    
    public void setMemberPointManger(IMemberPointManger memberPointManger) {
        this.memberPointManger = memberPointManger;
    }

	public String getStoreIdsForApp() {
		return storeIdsForApp;
	}

	public void setStoreIdsForApp(String storeIdsForApp) {
		this.storeIdsForApp = storeIdsForApp;
	}

	public String getSaveShippingGroupParam() {
		return saveShippingGroupParam;
	}

	public void setSaveShippingGroupParam(String saveShippingGroupParam) {
		this.saveShippingGroupParam = saveShippingGroupParam;
	}
    
}
