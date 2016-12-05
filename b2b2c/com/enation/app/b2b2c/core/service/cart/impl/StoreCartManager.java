package com.enation.app.b2b2c.core.service.cart.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.enation.app.advbuy.core.model.AdvBuy;
import com.enation.app.advbuy.core.service.impl.AdvBuyManager;
import com.enation.app.b2b2c.core.model.cart.StoreCartItem;
import com.enation.app.b2b2c.core.model.goods.StoreGoods;
import com.enation.app.b2b2c.core.service.IStoreBonusManager;
import com.enation.app.b2b2c.core.service.IStoreDlyTypeManager;
import com.enation.app.b2b2c.core.service.IStoreTemplateManager;
import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.b2b2c.core.service.StoreCartKeyEnum;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.impl.CallDroolsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2ccostdown.core.model.CostDown;
import com.enation.app.b2b2ccostdown.core.service.CostDownManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.groupbuy.core.model.GroupBuy;
import com.enation.app.groupbuy.core.service.impl.GroupBuyManager;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.impl.ActivityGoodsManager;
import com.enation.app.shop.core.service.impl.CartManager;
import com.enation.app.utils.PriceUtils;
import com.enation.app.utils.PriceUtils.IActivity;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
@Component
public class StoreCartManager<JSONOAarry> extends BaseSupport implements IStoreCartManager {
	private CartPluginBundle cartPluginBundle;
	private IDlyTypeManager dlyTypeManager;
	private IPromotionManager promotionManager;
	private IStoreGoodsManager storeGoodsManager;
	private IStoreMemberManager storeMemberManager;
	private IStoreDlyTypeManager storeDlyTypeManager;
	private IStoreTemplateManager storeTemplateManager;
	private IMemberAddressManager memberAddressManager;
	@Autowired
	private CartManager cartManager;
	private ActivityGoodsManager activityGoodsManager;
	private CallDroolsManager callDroolsManager;
	private IStoreBonusManager storeBonusManager;
	
	@Autowired
    private GroupBuyManager groupBuyManager;
    
    @Autowired
    private CostDownManager costDownManager;
    
    @Autowired
    private AdvBuyManager advBuyManager;
	

	@SuppressWarnings("rawtypes")
    @Override
	public void countPrice(String isCountShip) {
		countPrice(isCountShip, false);
	}

	public void countPrice(String isCountShip, boolean onlySelected) {

		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();

		//sessionid
		String sessionid = request.getSession().getId();

		//获取各个店铺购物车列表
		List<Map> storeGoodsList=storeListGoods(sessionid, onlySelected);

		//用户的默认地区（自己建立的或者根据ip得到的）
		Integer regionid = this.memberAddressManager.getMemberDefaultRegionId();
        
        //如果计算价格说明进入结算页了，那么要将用户的默认地址压入session
        if("yes".equals(isCountShip)){
            
            /**
             * 获取结算时的用户地址，优先级为：
             * 1.用户选择过的
             * 2.用户建立的默认地址
             * 3.根据用户所在ip的到
             */
            MemberAddress address=this.getCheckoutAddress();
            if(address!=null){
                regionid=address.getRegion_id();
            }
             
        }
        
        
        //循环每个店铺，计算各种费用，如果要求计算运费还要根据配送方式和地区计算运费
        for(Map map : storeGoodsList){ 
            List list = (List) map.get(StoreCartKeyEnum.goodslist.toString());

            String storeId = String.valueOf(map.get(StoreCartKeyEnum.store_id.toString()));

            //计算店铺价格，不计算运费
			OrderPrice orderPrice = this.cartManager.countPrice(list, null, null);

			//为店铺信息压入店铺的各种价格
            map.put(StoreCartKeyEnum.storeprice.toString(), orderPrice);
            
            //如果指定计算运费，则计算每个店的的运费，并设置好配送方式列表，在结算页中此参数应该设置为yes
            if("yes".equals(isCountShip)){
                orderPrice =this.countShipPrice(map,regionid);
            } 
        } 
		
		//向session中压入购物车列表
		StoreCartContainer.putStoreCartListToSession(storeGoodsList);
	}
	
	/**
     * 获取结算时的用户地址，优先级为：
     * 1.用户选择过的
     * 2.用户建立的默认地址
     * @return 用戶地址
     */
    private MemberAddress getCheckoutAddress(){
        Member member = UserConext.getCurrentMember();
        if(member!=null){
            
            
            /**
             * 先检查是否选择过地区，有选择过，则使用用户选择的地址。
             */
            MemberAddress address= StoreCartContainer.getUserSelectedAddress();
            if(address!=null){
                return address;
            }
            
            
            /**
             * 如果用户没有选择过收货地址，则使用用户的默认地址
             */
            address= memberAddressManager.getMemberDefault(member.getMember_id());
            if(address!=null){
                
                /**
                 * 同时将默认地址设置为用户的选择过的地址
                 */
                if(StoreCartContainer.getUserSelectedAddress()==null){
                    StoreCartContainer.putSelectedAddress(address);
                }
                
                return address;
                
            }
            
            
        }
        
        return null;
    }
    
    
	
	@Override
	public void countPriceForApp(String isCountShip ,String cart_id) {

		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		//sessionid
		String sessionid = request.getSession().getId();
		//获取各个店铺购物车列表
		List<Map> storeGoodsList=storeListGoodsForApp(cart_id,sessionid);
		
		//用户的默认地区（自己建立的或者根据ip得到的）
		Integer regionid = this.memberAddressManager.getMemberDefaultRegionId();
		
		//如果计算价格说明进入结算页了，那么要将用户的默认地址压入session
		Member member = UserConext.getCurrentMember();
		if("yes".equals(isCountShip)){
			
			if(member!=null){
				MemberAddress address= memberAddressManager.getMemberDefault(member.getMember_id());
				if(address!=null){
					if(StoreCartContainer.getUserSelectedAddress()==null){
						StoreCartContainer.putSelectedAddress(address);
					}
				}
			}
		}
		
		//循环每个店铺，计算各种费用，如果要求计算运费还要根据配送方式和地区计算运费
		for(Map map : storeGoodsList){
			
			List list = (List) map.get(StoreCartKeyEnum.goodslist.toString());

            String storeId = String.valueOf(map.get(StoreCartKeyEnum.store_id.toString()));

			//计算店铺价格，不计算运费
			OrderPrice orderPrice = this.cartManager.countPrice(list, null, null);

			//为店铺信息压入店铺的各种价格
			map.put(StoreCartKeyEnum.storeprice.toString(), orderPrice);
			//店铺优惠券
			Integer store_id =(Integer)map.get(StoreCartKeyEnum.store_id.toString());
			 List<Map> listBonus =null;
			if(member!=null){
		          listBonus = this.storeBonusManager.getMemberBonusList(member.getMember_id(), store_id,orderPrice.getGoodsPrice());
			}	
			 Object jsonText =JSON.parse("[]"); 
             map.put("storeBonus",listBonus==null?jsonText:listBonus);
			//如果指定计算运费，则计算每个店的的运费，并设置好配送方式列表，在结算页中此参数应该设置为yes
			if("yes".equals(isCountShip)){
				orderPrice =this.countShipPrice(map,regionid);
			}
			
		}
		
		//向session中压入购物车列表
		StoreCartContainer.putStoreCartListToSession(storeGoodsList);
	}
	
	

	
	private OrderPrice countShipPrice(Map map,int regionid){
		
		int storeid= (Integer)map.get(StoreCartKeyEnum.store_id.toString());
		List<StoreCartItem> list = (List) map.get(StoreCartKeyEnum.goodslist.toString());
		
		//取出之前计算好的订单价格
        OrderPrice orderPrice = (OrderPrice)map.get(StoreCartKeyEnum.storeprice.toString());
		
		//得到商品总计和重量，以便计算运费之用
		Double goodsprice = orderPrice.getGoodsPrice();
		Double weight = orderPrice.getWeight();
		
		//生成配送方式列表，此map中已经含有计算后的运费
		List<Map> shipList  = this.getShipTypeList(storeid, regionid, weight, goodsprice);
		
		//向店铺信息中压入配送方式列表
		map.put(StoreCartKeyEnum.shiptype_list.toString(),shipList);
		
		
		
		//如果免运费，向配送方式列表中加入免运费项
		if(shipList.isEmpty()){
			
			 //生成免运费项
			 Map freeType = new HashMap();
			 freeType.put("type_id", 0);
			 freeType.put("name", "免运费");
			 freeType.put("shipPrice", 0);
			 
			 //将免运费项加入在第一项
			 shipList.add(0, freeType);
			 
			 //设置运费价格和配送方式id
			 orderPrice.setShippingPrice(0d);
			 map.put(StoreCartKeyEnum.shiptypeid.toString(), 0);
			 
		}else{
		 
			
			//如果不免运费用第一个配送方式 设置运费价格和配送方式id
			Map firstShipType =shipList.get(0);
			Double shipprice = (Double)firstShipType.get("shipPrice");
			orderPrice.setShippingPrice(shipprice);
			map.put(StoreCartKeyEnum.shiptypeid.toString(), firstShipType.get("type_id"));
		}
		
		//通过规则引擎得到优惠后的价格，已经优惠详情  add by Tension
        callDroolsManager.getRestfulResult(list, orderPrice);
		
		return orderPrice;
	}
	
	

	private List<Map> getShipTypeList(int storeid,int regionid,double weight,double goodsprice){
		
		List<Map> newList  = new ArrayList();
		if(Double.valueOf(weight)!=0d){
			Integer tempid = this.storeTemplateManager.getDefTempid(storeid);
			List<Map> list =   this.storeDlyTypeManager.getDlyTypeList(tempid);
			
			for(Map maps:list){
				Map newMap = new HashMap();
				String name = (String)maps.get("name");
				Integer typeid = (Integer) maps.get("type_id");
				Double[] priceArray = this.dlyTypeManager.countPrice(typeid, Double.valueOf(weight), goodsprice, regionid+"");
				Double dlyPrice = priceArray[0];//配送费用
				newMap.put("name", name);
				newMap.put("type_id", typeid);
				newMap.put("shipPrice", dlyPrice);
				newList.add(newMap);
			}
		}
		
		return newList;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.cart.IStoreCartManager#listGoods(java.lang.String)
	 */
	@Override
	public List<StoreCartItem> listGoods(String sessionid) {
		StringBuffer sql = new StringBuffer();
		List list = null;
		// 2015/10/30 humaodong
		Member member = UserConext.getCurrentMember();
		if (member == null) {
    		sql.append("select c.selected as selected,s.store_id as store_id,s.store_name as store_name,p.weight AS weight,c.cart_id as id,g.goods_id,g.thumbnail as image_default,g.goods_transfee_charge as goods_transfee_charge,c.name ,  p.sn, p.specs  ,g.mktprice,g.unit,g.point, g.is_cost_down, g.is_groupbuy, g.is_flashbuy, g.is_secbuy, g.is_advbuy, p.product_id,c.price,c.cart_id as cart_id,c.num as num,c.itemtype,c.addon,  c.price  as coupPrice, "
    		        + "c.current_activity, c.current_activity_name, g.type_id as goodsType, g.cat_id as goodsCatId from "+ this.getTableName("cart") +" c,"+ this.getTableName("product") +" p,"+ this.getTableName("goods")+" g ,"+this.getTableName("store")+" s ");
    		sql.append("where c.itemtype=0 and c.product_id=p.product_id and p.goods_id= g.goods_id and c.session_id=?  AND c.store_id=s.store_id");
    		//System.out.println(sql);
    		list  =this.daoSupport.queryForList(sql.toString(), StoreCartItem.class, sessionid);
		} else {
		    // 2015/11/13 humaodong: update sessionid for cartItem added by mobile app.
	        this.daoSupport.execute("update es_cart set session_id=? where member_id=?", sessionid, member.getMember_id());
		    
		    sql.append("select c.selected as selected,s.store_id as store_id,s.store_name as store_name,p.weight AS weight,c.cart_id as id,g.goods_id,g.thumbnail as image_default,g.goods_transfee_charge as goods_transfee_charge,c.name ,  p.sn, p.specs  ,g.mktprice,g.unit,g.point, g.is_cost_down, g.is_groupbuy, g.is_flashbuy, g.is_secbuy, g.is_advbuy, p.product_id,c.price,c.cart_id as cart_id,c.num as num,c.itemtype,c.addon,  c.price  as coupPrice, "
		            + "c.current_activity, c.current_activity_name, g.type_id as goodsType, g.cat_id as goodsCatId from "+ this.getTableName("cart") +" c,"+ this.getTableName("product") +" p,"+ this.getTableName("goods")+" g ,"+this.getTableName("store")+" s ");
            sql.append("where c.itemtype=0 and c.product_id=p.product_id and p.goods_id= g.goods_id and c.member_id=?  AND c.store_id=s.store_id");
            //System.out.println(sql);
            list  =this.daoSupport.queryForList(sql.toString(), StoreCartItem.class, member.getMember_id());
		}
		cartPluginBundle.filterList(list, sessionid);
		return list;
	}
	/**
	 *  用cart_id  String[]
	 *  代替 sessionid 
	 * @param sessionid
	 * @return
	 */
	@Override
	public List<StoreCartItem> listGoodsForApp(String cart_id,String sessionid) {
		StringBuffer sql = new StringBuffer();

		sql.append("select s.store_id as store_id,s.store_name as store_name,p.weight AS weight,c.cart_id as id,g.goods_id,g.thumbnail as image_default,g.goods_transfee_charge as goods_transfee_charge,c.name ,  p.sn, p.specs  ,g.mktprice,g.unit,g.point,p.product_id,c.price,c.cart_id as cart_id,c.num as num,c.itemtype,c.addon,  c.price  as coupPrice"
		        + ", g.is_cost_down, g.is_groupbuy, g.is_flashbuy, g.is_secbuy, g.is_advbuy, "
		        + "c.current_activity, c.current_activity_name, g.type_id as goodsType, g.cat_id as goodsCatId from "
		+ this.getTableName("cart") +" c,"+ this.getTableName("product") +" p,"+ this.getTableName("goods")+" g ,"+this.getTableName("store")+" s ");
		sql.append("where c.itemtype=0 and c.product_id=p.product_id and p.goods_id= g.goods_id and c.cart_id in ("+cart_id+")  AND c.store_id=s.store_id");
		//System.out.println(sql);
		List list  =this.daoSupport.queryForList(sql.toString(), StoreCartItem.class);
		cartPluginBundle.filterList(list, sessionid);
		return list;
	}
	
	

	
	@Override
	public List<Map> storeListGoods(String sessionid, boolean onlySelected) {
		List<Map> storeGoodsList = new ArrayList<Map>();
		List<StoreCartItem> goodsList = this.listGoods(sessionid); //商品列表
		for (StoreCartItem item : goodsList) {
			if(onlySelected && item.getSelected().intValue() != 1){
				continue;
			}
			findStoreMap(storeGoodsList, item);
		}
		for (Map map: storeGoodsList) {
			//店铺是否需要设置选中状态
			int selected = 1;
			int selectCount = 0;
			List<StoreCartItem> cartItem = (List<StoreCartItem>)map.get("goodslist");
			for (StoreCartItem storeCartItem : cartItem) {
				if(storeCartItem.getSelected().intValue() == 0){
					selected = 0;
				}else{
					selectCount++;
				}
			}
			map.put("selected", selected);
			map.put("selectcount", selectCount);
		}
		return storeGoodsList;
	}
	/**
	 * 用cart_id  String []
	 * 代替 sessionid
	 * lxl
	 */
	@Override
	public List<Map> storeListGoodsForApp(String cart_id, String sessionid) {
		List<Map> storeGoodsList= new ArrayList<Map>();
		List<StoreCartItem> goodsList =new ArrayList();
		
		goodsList= this.listGoodsForApp(cart_id,sessionid); //商品列表
		for (StoreCartItem item : goodsList) {
		    // add by lxl
		    item.setImage_default(UploadUtil.replacePath(item.getImage_default()));
		    item.setSelected(1);
			findStoreMap(storeGoodsList, item);
		}
		
		for (Map map: storeGoodsList) {
            //店铺是否需要设置选中状态
            int selected = 1;
            int selectCount = 0;
            List<StoreCartItem> cartItem = (List<StoreCartItem>)map.get("goodslist");
            for (StoreCartItem storeCartItem : cartItem) {                
                    selectCount++;  
                    
            }
            map.put("selected", selected);
            map.put("selectcount", selectCount);
        }
		
		return storeGoodsList;
	}
	
	/**
	 * 重新计算用户购物车商品价格
	 */
	public void resetCartPrice(){
	    Member member = UserConext.getCurrentMember();
	    List<?> cartGoodsList = null;
        if (member == null) {
            HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
            //sessionid
            String sessionid = request.getSession().getId();
            cartGoodsList =  this.daoSupport.queryForList("SELECT c.cart_id, g.*, p.enable_store pes, c.num cnum FROM es_cart c LEFT JOIN es_goods g ON c.goods_id=g.goods_id LEFT JOIN es_product p ON c.goods_id=p.goods_id WHERE c.session_id=?", sessionid);
        } else {
            cartGoodsList =  this.daoSupport.queryForList("SELECT c.cart_id, g.*, p.enable_store pes, c.num cnum FROM es_cart c LEFT JOIN es_goods g ON c.goods_id=g.goods_id LEFT JOIN es_product p ON c.goods_id=p.goods_id WHERE c.member_id=?", member.getMember_id());
        }
        if(cartGoodsList == null || cartGoodsList.size() == 0)
            return;
        
        List<Map> storeGoodsList = StoreCartContainer.getStoreCartListFromSession();
        
        for (int i = 0; i < cartGoodsList.size(); i++){
            Map<?, ?> map = (Map<?, ?>)cartGoodsList.get(i);

            
//            商品下架 没有库存 貌似前台有限制
//            if ("1".equals(map.get("disabled").toString()) 
//                    || "0".equals(map.get("market_enable").toString()) 
//                    || "0".equals(map.get("pes").toString())) {
//                this.daoSupport.execute("delete from es_cart where cart_id=?", map.get("cart_id"));
//                continue;
//            }
            
            int pes = NumberUtils.toInt(map.get("pes").toString(), 0);
            int num = NumberUtils.toInt(map.get("cnum").toString(), 0);
            
            String resetSql = "";
            //库存 少于商品数量
            if (num > pes && pes >= 0){
                resetSql = ", num = " + pes;
            }
            
            GroupBuy gb = null;
            if (map.containsKey("is_groupbuy") && NumberUtils.toInt(map.get("is_groupbuy").toString(), 0) > 0)
                gb = groupBuyManager.getBuyGoodsId((Integer) map.get("goods_id"));
            AdvBuy ab = null;
            if (map.containsKey("is_advbuy") && NumberUtils.toInt(map.get("is_advbuy").toString(), 0) > 0)
                ab = advBuyManager.getBuyGoodsId((Integer) map.get("goods_id"));
            CostDown cd = null;
            if (map.containsKey("is_cost_down") && NumberUtils.toInt(map.get("is_cost_down").toString(), 0) > 0)
                cd = costDownManager.getBuyGoodsId((Integer) map.get("goods_id"));
            
            StoreGoods pt = storeGoodsManager.getGoods((Integer) map.get("goods_id"));
            
            IActivity a = PriceUtils.getMinimumPriceActivity(gb, ab, cd);
            if (null != gb && gb.equals(a)) {
                this.daoSupport.execute("UPDATE es_cart SET price=?, current_activity_name = ?, current_activity = ?"+resetSql+" WHERE cart_id=?", a.getPrice(), gb.getGb_name(), Cart.TYPE_GROUPBUY, map.get("cart_id"));
            } else if (null != ab && ab.equals(a)) {
                this.daoSupport.execute("UPDATE es_cart SET price=?, current_activity_name = ?, current_activity = ?"+resetSql+" WHERE cart_id=?", a.getPrice(), ab.getGb_name(), Cart.TYPE_AVDBUY, map.get("cart_id"));
            } else if (null != cd && cd.equals(a)) {
                this.daoSupport.execute("UPDATE es_cart SET price=?, current_activity_name = ?, current_activity = ?"+resetSql+" WHERE cart_id=?", a.getPrice(), cd.getGb_name(), Cart.TYPE_COST_DOWN, map.get("cart_id"));
            } else {
                this.daoSupport.execute("UPDATE es_cart SET price=?, current_activity_name = '', current_activity = ?"+resetSql+" WHERE cart_id=?", pt.getPrice(), Cart.TYPE_NO_ACTIVITY, map.get("cart_id"));
            }
//            if(storeGoodsList!=null){
//                for (Map mapc : storeGoodsList) {
//                    List<CartItem> itemlist = (List<CartItem>) mapc.get(StoreCartKeyEnum.goodslist.toString());
//                    for (CartItem ci : itemlist) {
//                        if(ci.getGoods_id().equals(map.get("goods_id"))){
//                            ci.setPrice(pt.getPrice());
//                            ci.setCoupPrice(pt.getPrice()*ci.getNum());
//                        }
//                    }
//                }
//            }
        }
	}
	
	/**
	 * 获取店铺商品列表
	 * @param storeGoodsList
	 * @return list<Map>
	 */
	private void findStoreMap(List<Map> storeGoodsList,StoreCartItem item){
		int is_store=0;
		if (storeGoodsList.isEmpty()){
			addGoodsList(item, storeGoodsList);
		}else{
			for (Map map: storeGoodsList) {
				if(map.containsValue(item.getStore_id())){
					List list=(List) map.get(StoreCartKeyEnum.goodslist.toString());
					list.add(item);
					is_store=1;
				}
			}
			if(is_store==0){
				addGoodsList(item, storeGoodsList);
			}
		}
	}
	
	/**
	 * 添加至店铺列表
	 * @param item
	 * @param storeGoodsList
	 */
	private void addGoodsList(StoreCartItem item,List<Map> storeGoodsList){
		Map map=new HashMap();
		List list=new ArrayList();
		list.add(item);
		map.put(StoreCartKeyEnum.store_id.toString(), item.getStore_id());
		map.put(StoreCartKeyEnum.store_name.toString(), item.getStore_name());
		map.put(StoreCartKeyEnum.goodslist.toString(), list);
		storeGoodsList.add(map);
	}
	@Override
	public void  clean(String sessionid){
		String sql ="delete from cart where session_id=?";
		
		this.baseDaoSupport.execute(sql, sessionid);
	}
	public IDlyTypeManager getDlyTypeManager() {
		return dlyTypeManager;
	}
	public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
		this.dlyTypeManager = dlyTypeManager;
	}
	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}
	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}
	public IStoreGoodsManager getStoreGoodsManager() {
		return storeGoodsManager;
	}
	public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
		this.storeGoodsManager = storeGoodsManager;
	}
	public CartPluginBundle getCartPluginBundle() {
		return cartPluginBundle;
	}
	public void setCartPluginBundle(CartPluginBundle cartPluginBundle) {
		this.cartPluginBundle = cartPluginBundle;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public IStoreDlyTypeManager getStoreDlyTypeManager() {
		return storeDlyTypeManager;
	}

	public void setStoreDlyTypeManager(IStoreDlyTypeManager storeDlyTypeManager) {
		this.storeDlyTypeManager = storeDlyTypeManager;
	}

	public IStoreTemplateManager getStoreTemplateManager() {
		return storeTemplateManager;
	}

	public void setStoreTemplateManager(IStoreTemplateManager storeTemplateManager) {
		this.storeTemplateManager = storeTemplateManager;
	}

	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}

	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}

    public ActivityGoodsManager getActivityGoodsManager() {
        return activityGoodsManager;
    }
    
    public void setActivityGoodsManager(ActivityGoodsManager activityGoodsManager) {
        this.activityGoodsManager = activityGoodsManager;
    }
    
    public CallDroolsManager getCallDroolsManager() {
        return callDroolsManager;
    }
    
    public void setCallDroolsManager(CallDroolsManager callDroolsManager) {
        this.callDroolsManager = callDroolsManager;
    }

    
    public IStoreBonusManager getStoreBonusManager() {
        return storeBonusManager;
    }

    
    public void setStoreBonusManager(IStoreBonusManager storeBonusManager) {
        this.storeBonusManager = storeBonusManager;
    }

    
    @Override
    public List<Map<String,Object>> listSelectedGoods() {
       
        Member member = UserConext.getCurrentMember();
        List<Map<String,Object>> cartGoodsList = null;
        if (member != null) {
           cartGoodsList =  this.daoSupport.queryForList("SELECT * from es_cart where selected=1 and member_id=?",member.getMember_id());
        }
        return cartGoodsList;
    }

	@Override
	public void updateGoodsNoSelected(int goodsId) {
		String sql = "UPDATE ES_CART SET SELECTED=0 WHERE GOODS_ID = ?";
		this.daoSupport.execute(sql, goodsId);
	}

}
