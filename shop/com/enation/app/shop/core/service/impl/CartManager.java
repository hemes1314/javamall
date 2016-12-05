package com.enation.app.shop.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.cart.StoreCartItem;
import com.enation.app.b2b2c.core.service.impl.CallDroolsManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.mapper.CartItemMapper;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.DoubleMapper;
import com.enation.framework.util.CurrencyUtil;

/**
 * 购物车业务实现
 * 
 * @author kingapex 2010-3-23下午03:30:50
 * edited by lzf 2011-10-08
 */

@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class CartManager extends BaseSupport implements ICartManager {
	private IDlyTypeManager dlyTypeManager;

	private CartPluginBundle cartPluginBundle;
	private IMemberLvManager memberLvManager;
	private IPromotionManager promotionManager;
	private ActivityGoodsManager activityGoodsManager;
	private CallDroolsManager callDroolsManager;
	
	@Transactional(propagation = Propagation.REQUIRED)

	public int add(Cart cart) {
		//HttpCacheManager.sessionChange();
		
		/*
		 * 触发购物车添加事件
		 */
		this.cartPluginBundle.onAdd(cart);
		
		Long memberId = cart.getMember_id();
		if (memberId == null) {
    		String sql ="select count(0) from cart where  product_id=? and session_id=? and itemtype=? ";	
    		int count = this.baseDaoSupport.queryForInt(sql, cart.getProduct_id(),cart.getSession_id(),cart.getItemtype());
    		if(count>0){
    			this.baseDaoSupport.execute("update cart set num=num+?,member_id=? where  product_id=? and session_id=? and itemtype=?  ", cart.getNum(),cart.getMember_id(),cart.getProduct_id(),cart.getSession_id(),cart.getItemtype());
    			return 0;
    		}
		} else {
		    String sql ="select count(0) from cart where  product_id=? and member_id=? and itemtype=? "; 
            int count = this.baseDaoSupport.queryForInt(sql, cart.getProduct_id(),memberId,cart.getItemtype());
            if(count>0){
                this.baseDaoSupport.execute("update cart set num=num+?,member_id=? where  product_id=? and member_id=? and itemtype=?  ", cart.getNum(),cart.getMember_id(),cart.getProduct_id(),memberId,cart.getItemtype());
                return 0;
            }
		}
			
		this.baseDaoSupport.insert("cart", cart);
		Integer cartid  = this.baseDaoSupport.getLastId("cart");
		cart.setCart_id(cartid);
		
		this.cartPluginBundle.onAfterAdd(cart);
		return cartid;

	}
	
	/**
	 * 
	 */
	public Cart get(int cart_id){
		return (Cart)this.baseDaoSupport.queryForObject("SELECT * FROM cart WHERE cart_id=?", Cart.class, cart_id);
	}
	
	public Cart getCartByProductId(int productId, String sessionid){
		return (Cart)this.baseDaoSupport.queryForObject("SELECT * FROM cart WHERE product_id=? AND session_id=?", Cart.class, productId,sessionid);
	}
	
	public Cart getCartByProductId(int productId, String sessionid, String addon){
		return (Cart)this.baseDaoSupport.queryForObject("SELECT * FROM cart WHERE product_id=? AND session_id=? AND addon=?", Cart.class, productId, sessionid, addon);
	}

	public Integer countItemNum(String sessionid) {
		//String sql = "select count(0) from cart where session_id =?";
	    // 2015/10/28 humaodong
	    Member member = UserConext.getCurrentMember();
	    if (member != null) {
	        String sql = "select sum(num) from cart where member_id =?";
            return this.baseDaoSupport.queryForInt(sql, member.getMember_id());
	    } else {
	        String sql = "select sum(num) from cart where session_id =?";
	        return this.baseDaoSupport.queryForInt(sql, sessionid);
	    }
	}
	
	
	public List<CartItem> listGoods(String sessionid) {
		return listGoods(sessionid, false);
	}

	public List<CartItem> listGoods(String sessionid, boolean onlySelected) {
		List<CartItem>  list = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select g.cat_id as catid,g.goods_id,g.thumbnail,g.store_id,g.store_name,c.name ,  p.sn, p.specs  ,g.mktprice,g.unit,g.point,p.product_id,c.price,c.cart_id as cart_id,c.num as num,c.itemtype,c.addon,c.weight, "
		        + "c.current_activity, c.current_activity_name from "+ this.getTableName("cart") +" c,"+ this.getTableName("product") +" p,"+ this.getTableName("goods")+" g ");
		sql.append("where c.itemtype=0 and c.product_id=p.product_id and p.goods_id= g.goods_id and g.market_enable=1");
		if(onlySelected){
			sql.append(" and c.selected='1'");
		}
		Member member = UserConext.getCurrentMember();
		if (member == null) {
			sql.append(" and c.session_id=?");
			list = this.daoSupport.queryForList(sql.toString(), new CartItemMapper(), sessionid);
		} else {
			sql.append(" and c.member_id=?");
			list = this.daoSupport.queryForList(sql.toString(), new CartItemMapper(), member.getMember_id());
		}
		cartPluginBundle.filterList(list, sessionid);
		return list;
	}

	public List<CartItem> listGoods(String sessionid,Long member_id) {
		
		 
//		StringBuffer sql = new StringBuffer();
		String sql ="";
		List<CartItem> list =null;	
		// 2015/9/25 lxl add start
		if(member_id!=null){
			sql +=" select g.cat_id as catid,c.goods_id,g.thumbnail,g.store_id,g.store_name,c.cart_id,c.name ,  p.sn, p.specs  ,g.mktprice,g.unit,g.point,p.product_id,c.price,c.num as num,c.itemtype,c.addon,c.weight ";
			sql +=" ,f.favorite_id, c.current_activity, c.current_activity_name from es_cart c left join es_product p on p.goods_id=c.goods_id left JOIN es_goods g on g.goods_id=p.goods_id left join es_favorite f on f.goods_id=g.goods_id and f.member_id=c.member_id";
			sql +=" where  c.member_id=? and g.market_enable=1";
			 list  =this.daoSupport.queryForList(sql, new CartItemMapper(), member_id);
		
		}else{
			sql +=" select g.cat_id as catid,c.goods_id,g.thumbnail,g.store_id,g.store_name,c.name ,c.cart_id ,  p.sn, p.specs  ,g.mktprice,g.unit,g.point,p.product_id,c.price,c.num as num,c.itemtype,c.addon,c.weight,"
			        + " c.current_activity, c.current_activity_name  ";
			sql +="  from es_cart c left join es_product p on p.goods_id=c.goods_id left JOIN es_goods g on g.goods_id=p.goods_id ";
			sql +=" where c.session_id=? and g.market_enable=1";
			 list  =this.daoSupport.queryForList(sql, new CartItemMapper(), sessionid);

		}
		// 2015/9/25 lxl add end
//		sql.append("select g.cat_id as catid,g.goods_id,g.thumbnail,c.name ,  p.sn, p.specs  ,g.mktprice,g.unit,g.point,p.product_id,c.price,c.cart_id as cart_id,c.num as num,c.itemtype,c.addon,c.weight  from "+ this.getTableName("cart") +" c,"+ this.getTableName("product") +" p,"+ this.getTableName("goods")+" g ");
//		sql.append("where c.itemtype=0 and c.product_id=p.product_id and p.goods_id= g.goods_id and c.session_id=?");
		//cartPluginBundle.filterList(list, sessionid);
		   return list;

	}

	
	
	public void  clean(String sessionid){
	    // 2015/10/30 humaodong
	    Member member = UserConext.getCurrentMember();
        if (member != null) {
            String sql ="delete from cart where member_id=?";
            this.baseDaoSupport.execute(sql, member.getMember_id());
        } else {
            String sql ="delete from cart where session_id=?";
            this.baseDaoSupport.execute(sql, sessionid);
        }
//		HttpCacheManager.sessionChange();
	}

	public void clean(String sessionid, Integer userid, Integer siteid) {

			String sql = "delete from cart where session_id=?";
			this.baseDaoSupport.execute(sql, sessionid);

		if (this.logger.isDebugEnabled()) {
			this.logger.debug("clean cart sessionid[" + sessionid + "]");
		}
//		HttpCacheManager.sessionChange();
	}

	public void clean(String sessionid,Integer[] cartids){
		Member member = UserConext.getCurrentMember();
		if (member != null) {
			String sql ="delete from cart where member_id=? and cart_id in (" + org.apache.commons.lang3.StringUtils.join(cartids, ",") + ")";
			this.baseDaoSupport.execute(sql, member.getMember_id());
		} else {
			String sql ="delete from cart where session_id=? and cart_id in (" + org.apache.commons.lang3.StringUtils.join(cartids, ",") + ")";
			this.baseDaoSupport.execute(sql, sessionid);
		}
	}

	public void delete(String sessionid, Integer cartid) {
	    /*
		String sql = "delete from cart where session_id=? and cart_id=?";
		this.baseDaoSupport.execute(sql, sessionid, cartid);
		*/
	    // 2015/10/30 humaodong
	    String sql = "delete from cart where cart_id=?";
        this.baseDaoSupport.execute(sql, cartid);
		this.cartPluginBundle.onDelete(sessionid, cartid);
//		HttpCacheManager.sessionChange();
	}



	public void updateNum(String sessionid, Integer cartid, Integer num) {
		String sql = "update cart set num=? where session_id =? and cart_id=?";
		this.baseDaoSupport.execute(sql, num, sessionid, cartid);
		this.cartPluginBundle.onUpdate(sessionid, cartid);
	}
	public void updateNumForApp(String sessionid, Integer cartid, Integer num) {
		String sql = "update cart set num=? where cart_id=?";
		this.baseDaoSupport.execute(sql, num, cartid);
		this.cartPluginBundle.onUpdate(sessionid, cartid);
	}

	public Double countGoodsTotal(String sessionid) {
		StringBuffer sql = new StringBuffer();
		sql.append("select sum( c.price * c.num ) as num from cart c ");
		sql.append("where  c.session_id=? and c.itemtype=0 ");
		Double price = (Double) this.baseDaoSupport.queryForObject(sql
				.toString(), new DoubleMapper(), sessionid);
		return price;
	}



	
	public Double  countGoodsDiscountTotal(String sessionid){
		

		List<CartItem> itemList = this.listGoods(sessionid);

		double price = 0; // 计算商品促销规则优惠后的总价
		for (CartItem item : itemList) {
			// price+=item.getSubtotal();
			price = CurrencyUtil.add(price, item.getSubtotal());
		}

		return price;
	}

	
	public Integer countPoint(String sessionid) {

//		Member member = UserServiceFactory.getUserService().getCurrentMember();
//		if (member != null) {
//			Integer memberLvId = member.getLv_id();
//			StringBuffer sql = new StringBuffer();
//			sql
//					.append("select c.*, g.goods_id, g.point from "
//							+ this.getTableName("cart")
//							+ " c,"
//							+ this.getTableName("goods")
//							+ " g, "
//							+ this.getTableName("product")
//							+ " p where p.product_id = c.product_id and g.goods_id = p.goods_id and c.session_id = ?");
//			List<Map> list = this.daoSupport.queryForList(sql.toString(),
//					sessionid);
//			Integer result = 0;
//			for (Map map : list) {
//				Integer goodsid = StringUtil.toInt(map.get("goods_id")
//						.toString());
//				List<Promotion> pmtList = new ArrayList();
//				
//				if(memberLvId!=null){
//					pmtList = promotionManager.list(goodsid, memberLvId);
//				}
//				
//				for (Promotion pmt : pmtList) {
//
//					// 查找相应插件
//					String pluginBeanId = pmt.getPmts_id();
//					IPromotionPlugin plugin = promotionManager
//							.getPlugin(pluginBeanId);
//
//					if (plugin == null) {
//						logger.error("plugin[" + pluginBeanId + "] not found ");
//						throw new ObjectNotFoundException("plugin["
//								+ pluginBeanId + "] not found ");
//					}
//
//					// 查找相应优惠方式
//					String methodBeanName = plugin.getMethods();
//					if (this.logger.isDebugEnabled()) {
//						this.logger.debug("find promotion method["
//								+ methodBeanName + "]");
//					}
//					IPromotionMethod promotionMethod = SpringContextHolder
//							.getBean(methodBeanName);
//					if (promotionMethod == null) {
//						logger.error("plugin[" + methodBeanName
//								+ "] not found ");
//						throw new ObjectNotFoundException("promotion method["
//								+ methodBeanName + "] not found ");
//					}
//
//					// 翻倍积分方式
//					if (promotionMethod instanceof ITimesPointBehavior) {
//						Integer point = StringUtil.toInt(map.get("point")
//								.toString());
//						ITimesPointBehavior timesPointBehavior = (ITimesPointBehavior) promotionMethod;
//						point = timesPointBehavior.countPoint(pmt, point);
//						result += point;
//					}
//
//				}
//			}
//			return result;
//		} else {
			StringBuffer sql = new StringBuffer();
			sql.append("select  sum(g.point * c.num) from "
					+ this.getTableName("cart") + " c,"
					+ this.getTableName("product") + " p,"
					+ this.getTableName("goods") + " g ");
			sql
					.append("where (c.itemtype=0  or c.itemtype=1)  and c.product_id=p.product_id and p.goods_id= g.goods_id and c.session_id=?");

			return this.daoSupport.queryForInt(sql.toString(), sessionid);
//		}
	}

	public Double countGoodsWeight(String sessionid) {
		StringBuffer sql = new StringBuffer(
				"select sum( c.weight * c.num )  from cart c where c.session_id=?");
		Double weight = (Double) this.baseDaoSupport.queryForObject(sql
				.toString(), new DoubleMapper(), sessionid);
		return weight;
	}
	

 
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ICartManager#countPrice(java.util.List, java.lang.Integer, java.lang.String)
	 */
	@Override
	public OrderPrice countPrice(List<CartItem> cartItemList, Integer shippingid,String regionid) {
		
		OrderPrice orderPrice = new OrderPrice();
		orderPrice.setShippingid(shippingid);
		orderPrice.setRegionid(regionid);
		
		//计算商品重量
		Double weight=0.0;
 
		//订单总价格
		Double  orderTotal = 0d;
		
		//配送费用
		Double dlyPrice = 0d;  
		
		//优惠后的订单价格,默认为商品原始价格
		Double goodsPrice =0.0; 
		
		//是否包邮        add by Tension
		boolean freeShip = false;
		
		//storeCartItemList  add by Tension
		List<StoreCartItem> storeCartItemList = new ArrayList<StoreCartItem>();
	 
		
		//计算商品重量及商品价格
		for (CartItem cartItem : cartItemList) {
		    //添加到storeCartItemList        add by Tension
		    StoreCartItem storeCartItem = new StoreCartItem();
		    storeCartItem.setId(cartItem.getId());
		    storeCartItem.setGoods_id(cartItem.getGoods_id());
		    storeCartItem.setNum(cartItem.getNum());
		    storeCartItem.setPrice(cartItem.getPrice());
		    storeCartItem.setCurrent_activity(cartItem.getCurrent_activity());
		    storeCartItem.setCurrent_activity_name(cartItem.getCurrent_activity_name());
			storeCartItem.setStore_id(cartItem.getStore_id());
			storeCartItemList.add(storeCartItem);
			
			// 计算商品重量
			weight = CurrencyUtil.add(weight, CurrencyUtil.mul(cartItem.getWeight(), cartItem.getNum()));
			
		 
			//计算商品优惠后的价格小计
//			Double itemTotal = CurrencyUtil.mul(cartItem.getCoupPrice(), cartItem.getNum());
			Double itemTotal = CurrencyUtil.mul(cartItem.getPrice(), cartItem.getNum());
			goodsPrice=CurrencyUtil.add(goodsPrice, itemTotal);
			
		}
		
		 
		//如果传递了配送信息，计算配送费用
		if(regionid!=null &&shippingid!=null && (freeShip == false)){
			if(shippingid!=0){
				//计算原始配置送费用
				Double[] priceArray = this.dlyTypeManager.countPrice(shippingid, weight, goodsPrice, regionid);
				
				//费送费用
				dlyPrice = priceArray[0];
			}
			
		}

		
		//商品金额 
		orderPrice.setGoodsPrice(goodsPrice);
		orderPrice.setGoodsBasePrice(goodsPrice);
		//********配送费用 Dawei 2015-12-01
		orderPrice.setShippingPrice(dlyPrice);
		//********配送费用END
		
		//订单可获得积分
        orderPrice.setPoint(0); 
        
        //商品总重量
        orderPrice.setWeight(weight);
		
		//应付金额为订单总金额 
        //通过规则引擎得到优惠后的价格，已经优惠详情  add by Tension
	    callDroolsManager.getRestfulResult(storeCartItemList, orderPrice);
		return orderPrice;
		 
	 
	}
	

	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}
 

	public CartPluginBundle getCartPluginBundle() {
		return cartPluginBundle;
	}

	public void setCartPluginBundle(CartPluginBundle cartPluginBundle) {
		this.cartPluginBundle = cartPluginBundle;
	}

 
	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	public IDlyTypeManager getDlyTypeManager() {
		return dlyTypeManager;
	}

	public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
		this.dlyTypeManager = dlyTypeManager;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}

	@Override
	public boolean checkGoodsInCart(Integer goodsid) {
		String sql ="select count(0) from cart where goods_id=?";
		return this.baseDaoSupport.queryForInt(sql, goodsid)>0;
	}

	@Override
	public List<CartItem> getByCartId(String cart_id) {
		String sql = "select * from es_cart where selected = 1 and cart_id in (" + cart_id + ")";
		return this.baseDaoSupport.queryForList(sql,CartItem.class);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void deleteForApp(String cartid) {
		String sql ="delete from  es_cart  where cart_id in ("+cartid+")";
		this.baseDaoSupport.execute(sql);
		
	}

    @Override
    public void cleanForApp(long member_id) {
        this.baseDaoSupport.execute("delete from cart where member_id=?", member_id);
        
    }
    
    public int countGoods(int goods_id, String session_id){
        Member member = UserConext.getCurrentMember();
        if(member != null){
            return this.daoSupport.queryForInt("SELECT SUM(num) from es_cart WHERE goods_id=? AND member_id=?", goods_id, member.getMember_id());
        }else{
            return this.daoSupport.queryForInt("SELECT SUM(num) from es_cart WHERE goods_id=? AND session_id=?", goods_id, session_id);
        }
    }

	/**
	 * 选择购物车商品
	 * @param sessionid
	 * @param cartIdList
	 */
	public void selectItems(String sessionid, List<Integer> cartIdList){
		Member member = UserConext.getCurrentMember();
		String where = member != null ? where = "member_id=" + member.getMember_id() : "session_id='" + sessionid + "'";
		daoSupport.execute("UPDATE es_cart SET selected='0' WHERE " + where);
		if(cartIdList == null || cartIdList.size() == 0)
			return;

		daoSupport.execute("UPDATE es_cart SET selected='1' WHERE cart_id in (" + StringUtils.join(cartIdList, ",") + ") AND " + where);
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

}
