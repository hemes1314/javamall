/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：购物车api  
 * 修改人：  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.cart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.IStoreDlyTypeManager;
import com.enation.app.b2b2c.core.service.IStoreMemberAddressManager;
import com.enation.app.b2b2c.core.service.IStoreTemplateManager;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.Favorite;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.impl.ActivityGiftManager;
import com.enation.app.shop.core.service.impl.ActivityGoodsManager;
import com.enation.app.shop.mobile.service.IApiFavoriteManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 购物车Api 
 * 提供购物车增删改查、结算
 * @author Sylow
 * @version v1.0 2015-08-24
 * @since v1.0
 */
@SuppressWarnings({"rawtypes", "serial", "unused" })
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("cart")
public class CartApiAction extends WWAction {
	
	private IProductManager productManager;
	private ICartManager cartManager;
	private IStoreCartManager storeCartManager;
	private IStoreMemberAddressManager storeMemberAddressManager;
	private IStoreTemplateManager storeTemplateManager;
	private IStoreDlyTypeManager storeDlyTypeManager;

	private IPromotionManager promotionManager ;
	private IApiFavoriteManager apiFavoriteManager;
	private IGoodsManager goodsManager;
	private IDaoSupport daoSupport;
	private ActivityGoodsManager activityGoodsManager;
	private ActivityGiftManager activityGiftManager;
	private Integer goodsid;
	private Integer productid;
	private String goodsArray;

	
	//在向购物车添加货品时，是否在返回的json串中同时显示购物车数据。
	//0为否,1为是
	private Integer showCartData;
	private Long member_id;
	private String countCart;
	/**
	 * 获取购物车中的商品列表
	 * 
	 * @return
	 */
	public String list() {
		try {
			
			String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
		
			Member member = UserConext.getCurrentMember();
	        if (member == null) {
	            this.json = JsonMessageUtil.expireSession();
	            return JSON_MESSAGE;
	        }
			
			Long member_id=null;
			if (member!=null){
			     member_id = member.getMember_id();
			}
			
			List<CartItem> list = this.cartManager.listGoods(sessionid,member_id);

            for(CartItem cart : list){
                cart.setThumbnail(UploadUtil.replacePath(cart.getImage_default()));
                cart.setCart_id(cart.getId());
                
                if (activityGoodsManager.checkGoods(cart.getGoods_id()) > 0) {
                    Map activityMap =  activityGoodsManager.getActivityByGoods(cart.getGoods_id());
                    String activityName = activityMap.get("name").toString();
                    cart.setActivityName(activityName);
                    Object giftName = activityGiftManager.getNameByActivityId(NumberUtils.toLong(activityMap.get("id").toString()), 
                            cart.getStore_id()).get("gift_name");
                    
                    if (giftName != null) {
                        cart.setGiftName(giftName.toString());
                    }
                   
                }
                // 是否已收藏       
                Favorite favorite = apiFavoriteManager.get(cart.getGoods_id(), member_id);
                if(favorite != null){
                    cart.setFavorite_id(favorite.getFavorite_id());
                }else{
                    cart.setFavorite_id(0);
                }


            }
			this.json = JsonMessageUtil.getMobileListJson(list);
		} catch (RuntimeException e) {
			this.logger.error("获取购物车列表出错", e);
			this.showPlainErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 删除购物车一项
	 * 
	 * @param cartid
	 *            :要删除的购物车id,int型,即 CartItem.item_id
	 * @return 返回json字串 result 为1表示调用成功0表示失败 message 为提示信息
	 *         <p/>
	 *         {@link com.enation.app.shop.core.model.support.CartItem }
	 */
	public String delete() {
		try {
			//判断用户是否登入
			Member member = UserConext.getCurrentMember();
	        if (member == null) {
	            this.json = JsonMessageUtil.expireSession();
	            return JSON_MESSAGE;
	        }
			
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String cartid = request.getParameter("cartid");
			
			cartManager.deleteForApp(cartid);
			long count = this.cartManager.countItemNum(request.getSession().getId());
			this.showPlainSuccessJson("删除成功", count);

		} catch (RuntimeException e) {
			this.logger.error("删除购物项失败", e);
			this.showPlainErrorJson("删除购物项失败:" + e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 更新购物车的数量
	 * 
	 * @param cartid
	 *            :要更新的购物车项id，int型，即 CartItem.item_id
	 * @param num
	 *            :要更新数量,int型
	 * @return 返回json字串 result： 为1表示调用成功0表示失败 int型 store: 此商品的库存 int型
	 */
	public String updateNum() {
		
		try {
			//判断用户是否登入
			Member member = UserConext.getCurrentMember();
	        if (member == null) {
	            this.json = JsonMessageUtil.expireSession();
	            return JSON_MESSAGE;
	        }
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String cartid = request.getParameter("cartid");
			int num = NumberUtils.toInt(request.getParameter("num"), 1);
			String productid = request.getParameter("productid");
			Product product = productManager.get(Integer.valueOf(productid));
			Integer store = product.getEnable_store();

			if (store == null)
				store = 0;

			if (store >= num) {
				cartManager.updateNum(request.getSession().getId(),
						Integer.valueOf(cartid), Integer.valueOf(num));

				this.showPlainSuccessJson("更新成功");
			} else {
				this.showPlainErrorJson("要购买的商品数量超出库存！");
			}
		} catch (RuntimeException e) {
			this.logger.error("更新购物车数量出现意外错误", e);
			this.showPlainErrorJson("更新购物车数量出现意外错误" + e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 购物车的价格总计信息
	 * 
	 * @param 无
	 * @return 返回json字串 result： 为1表示调用成功0表示失败 int型 orderprice: 订单价格，OrderPrice型
	 *         {@link com.enation.app.shop.core.model.support.OrderPrice}
	 */
	public String total() {
		try {
			//验证用户是否登入
	    	Member member = UserConext.getCurrentMember();
	        if (member == null) {
	            this.json = JsonMessageUtil.expireSession();
	            return JSON_MESSAGE;
	        }
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			OrderPrice orderprice = this.cartManager.countPrice(
					cartManager.listGoods(request.getSession().getId()), null,
					null);
			this.json = JsonMessageUtil.getMobileObjectJson(orderprice);
		} catch (RuntimeException e) {
			this.logger.error("计算总价出错", e);
			this.showPlainErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 计算购物车货物总数
	 * 
	 * @return
	 */
	public String count() {
		try {
			Member member = UserConext.getCurrentMember();
	        if (member == null) {
	            this.json = JsonMessageUtil.expireSession();
	            return JSON_MESSAGE;
	        }
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			Integer count = this.cartManager.countItemNum(request.getSession()
					.getId());
			this.json = JsonMessageUtil.getMobileNumberJson("count", count);
		} catch (RuntimeException e) {
			this.logger.error("计算货物总数出错", e);
			this.showPlainErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 清空购物车
	 */
	public String clean() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Member member =UserConext.getCurrentMember();
		if (member == null) {
            this.json = JsonMessageUtil.expireSession();
            return JSON_MESSAGE;
        }
		try {
		     if(member!=null){
		         cartManager.cleanForApp(member.getMember_id()); 
		     }else{
		         cartManager.clean(request.getSession().getId());
		     }
			this.showPlainSuccessJson("清空购物车成功");
		} catch (RuntimeException e) {
			this.logger.error("清空购物车出错", e);
			this.showPlainErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	//2015/9/19 lxl add start 
	/**
	 * 将一个商品添加到购物车
	 * 需要传递goodsid 和num参数
	 * @param goodsid 商品id，int型
	 * @param num 数量，int型
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败
	 * message 为提示信息
	 */
	public String addGoods(){
	    try{
	    	//验证是否登入
	    	Member member = UserConext.getCurrentMember();
	        if (member == null) {
	            this.json = JsonMessageUtil.expireSession();
	            return JSON_MESSAGE;
	        }
	        HttpServletRequest request = getRequest();
			Integer goodsid = NumberUtils.toInt(request.getParameter("goodsid"));
			Integer num = NumberUtils.toInt(request.getParameter("num"));
			Product product = productManager.getByGoodsId(goodsid);
			this.addProductToCart(product,num);
	        
	    }catch(RuntimeException e){
	        this.showPlainSuccessJson(""+e);
	    }
	    return JSON_MESSAGE;
	}
	/**
	 * 添加货品的购物车
	 * @param product
	 * @return
	 */
	private boolean addProductToCart(Product product,Integer num){
		String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
		Member member = UserConext.getCurrentMember();
		if(product!=null){
			try{
				
				int enableStore = product.getEnable_store();
				if (enableStore < num) {
					throw new RuntimeException("抱歉！您所选择的货品库存不足。");
				}
				//查询已经存在购物车里的商品
				Cart tempCart = cartManager.getCartByProductId(product.getProduct_id(), sessionid);
				if(tempCart != null){
					int tempNum = tempCart.getNum();
					if (enableStore < num + tempNum) {
						throw new RuntimeException("抱歉！您所选择的货品库存不足。");
					}
				}
				
				
				//获取购物车商品准确数量
                int cartItemNumber = cartManager.countGoods(product.getGoods_id(), getRequest().getSession().getId()) + num;
                
                //验证活动商品数量
                if(!validCartItemNumber(product.getGoods_id(), cartItemNumber)){
                    return false;
                }
                
				Cart cart = new Cart();
				cart.setGoods_id(product.getGoods_id());
				cart.setProduct_id(product.getProduct_id());
				cart.setSession_id(sessionid);
				cart.setNum(num);
				cart.setItemtype(0); //0为product和产品 ，当初是为了考虑有赠品什么的，可能有别的类型。
				cart.setWeight(product.getWeight());
				cart.setPrice( product.getPrice() );
				cart.setName(product.getName());
				if(member!=null){
					cart.setMember_id(member.getMember_id());
				}
				this.cartManager.add(cart);
				this.showPlainSuccessJson("货品成功添加到购物车");
				return true;
			}catch(RuntimeException e){
				this.logger.error("将货品添加至购物车出错",e);
				this.showPlainErrorJson("将货品添加至购物车出错["+e.getMessage()+"]");
				return false;
			}
			
		}else{
			this.showPlainErrorJson("该货品不存在，未能添加到购物车");
			return false;
		}
	}
	/**
	 * 获取购物车数据
	 * @param 无
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败
	 * data.count：购物车的商品总数,int 型
	 * data.total:购物车总价，int型
	 * 
	 */
	private String getCartList(){
		//验证用户是否登入
    	Member member = UserConext.getCurrentMember();
        if (member == null) {
            this.json = JsonMessageUtil.expireSession();
            return JSON_MESSAGE;
        }
		String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
		List<CartItem> result = this.cartManager.listGoods(sessionid);
		this.json = JsonMessageUtil.getMobileListJson(result);
		return WWAction.JSON_MESSAGE;
	}
	
	public String getCartData(){
		
		try{
			//验证用户是否登入
	    	Member member = UserConext.getCurrentMember();
	        if (member == null) {
	            this.json = JsonMessageUtil.expireSession();
	            return JSON_MESSAGE;
	        }
			String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
			System.out.println("test1=="+sessionid);
			Double goodsTotal  =cartManager.countGoodsTotal( sessionid );
			int count = this.cartManager.countItemNum(sessionid);
			Map<String, Object> data =new HashMap<>();
			data.put("count", count);//购物车中的商品数量
			data.put("total", goodsTotal);//总价
			
			this.json = JsonMessageUtil.getObjectJson(data);
			
		}catch(Throwable e ){
			this.logger.error("获取购物车数据出错",e);
			this.showPlainErrorJson("获取购物车数据出错["+e.getMessage()+"]");
		}
		
		return WWAction.JSON_MESSAGE;
	}
	/**
	 * 购物车添加多个商品
	 * @return
	 */
	public String addmore(){
	    try {
	    	//验证用户是否登入
	    	Member member = UserConext.getCurrentMember();
	        if (member == null) {
	            this.json = JsonMessageUtil.expireSession();
	            return JSON_MESSAGE;
	        }
	        HttpServletRequest request = getRequest();
	        String goodsIdArr =request.getParameter("goodsId");
	        String[] strArr = goodsIdArr.split(",");
	        for (int  i = 0; i < strArr.length; i ++){
	            Product product = productManager.getByGoodsId(NumberUtils.toInt(strArr[i]));
	            this.addProductToCart(product,1);
	        }
	        this.showPlainSuccessJson("添加成功");
	    }catch(RuntimeException e){
	        this.showPlainErrorJson("添加失败");
	    }
	    return WWAction.JSON_MESSAGE;
	}
	
	   static String[] activeTables = new String[]{"groupbuy", "flashbuy", "secbuy", "advbuy"};
	    /**
	     * 验证添加到购物车中的商品数量是否超出限制
	     * @param goodsId
	     * @param addNum
	     * @return
	     */
		private boolean validCartItemNumber(int goodsId, int num){
	        //edit by Tension 修改成从缓存获取商品信息    Map goodsMap = goodsManager.get(goodsId);
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
	                        this.showPlainErrorJson("此活动商品已售罄！");
	                        return false;
	                    }
	                    
	                    //判断剩余商品是否能满足此次购买数量
	                    if(num > NumberUtils.toInt(activeGoodsMap.get("goods_num").toString(), 0)){
	                        this.showPlainErrorJson("超出购买限制，此商品仅剩余" + activeGoodsMap.get("goods_num") + "件！");
	                        return false;
	                    }
	                    
	                    //是否超出单次购买数量限制
	                    int limit_num = NumberUtils.toInt(activeGoodsMap.get("limit_num").toString(), 0);
	                    if(limit_num > 0 && num > limit_num){
	                        this.showPlainErrorJson("此活动商品每人限购" + activeGoodsMap.get("limit_num") + "件！");
	                        return false;
	                    }
	                }
	            }
	        }
	        return true;
	    }
	    
	public int getShowCartData() {
		return showCartData;
	}

	public void setShowCartData(int showCartData) {
		this.showCartData = showCartData;
	}

	public String getCountCart() {
		return countCart;
	}

	public void setCountCart(String countCart) {
		this.countCart = countCart;
	}

	//2015/9/19 lxl add end
	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
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

    
    public IPromotionManager getPromotionManager() {
        return promotionManager;
    }

    
    public void setPromotionManager(IPromotionManager promotionManager) {
        this.promotionManager = promotionManager;
    }

    
    public IApiFavoriteManager getApiFavoriteManager() {
        return apiFavoriteManager;
    }

    
    public void setApiFavoriteManager(IApiFavoriteManager apiFavoriteManager) {
        this.apiFavoriteManager = apiFavoriteManager;
    }

    
    public IGoodsManager getGoodsManager() {
        return goodsManager;
    }

    
    public void setGoodsManager(IGoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }

    
	public IDaoSupport getDaoSupport() {
        return daoSupport;
    }

    
    public void setDaoSupport(IDaoSupport daoSupport) {
        this.daoSupport = daoSupport;
    }

    
    public Integer getProductid() {
        return productid;
    }

    
    public void setProductid(Integer productid) {
        this.productid = productid;
    }

    
    public Long getMember_id() {
        return member_id;
    }

    
    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    
    public Integer getGoodsid() {
        return goodsid;
    }

    
    public void setGoodsid(Integer goodsid) {
        this.goodsid = goodsid;
    }

    
    public ActivityGoodsManager getActivityGoodsManager() {
        return activityGoodsManager;
    }

    
    public void setActivityGoodsManager(ActivityGoodsManager activityGoodsManager) {
        this.activityGoodsManager = activityGoodsManager;
    }

    
    public ActivityGiftManager getActivityGiftManager() {
        return activityGiftManager;
    }

    
    public void setActivityGiftManager(ActivityGiftManager activityGiftManager) {
        this.activityGiftManager = activityGiftManager;
    }


}
