package com.enation.app.shop.core.action.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.b2b2ccostdown.component.act.ICostDownActAddEvent;
import com.enation.app.b2b2ccostdown.core.model.CostDown;
import com.enation.app.b2b2ccostdown.core.service.CostDownManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 购物车api
 * @author kingapex
 *2013-7-19下午12:58:43
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("cart")
public class CartApiAction extends WWAction {
	private ICartManager cartManager;
	private IPromotionManager promotionManager ;
	private int goodsid;
	private int productid;
	private int num;//要向购物车中活加的货品数量
	private IProductManager productManager ;
	private IGoodsManager goodsManager;
	private IDaoSupport daoSupport;

	private IStoreCartManager storeCartManager;

	@Autowired
	private CostDownManager costDownManager;

	//在向购物车添加货品时，是否在返回的json串中同时显示购物车数据。
	//0为否,1为是
	private int showCartData;
	
	/**
	 * 将一个货品添加至购物车。
	 * 需要传递productid和num参数
	 * 
	 * @param productid 货品id，int型
	 * @num 数量，int 型
	 * 
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 为提示信息
	 *lee.li修改了addProduct
	 */
	public String addProduct(){
	      System.out.println("/api/shop  cart   productid"+productid);
	      
	        
	      Product product = null;
	      if(productid!=0){
	         product = productManager.get(productid);
	           }
	       else if(goodsid!=0){
	           product = productManager.getByGoodsId(goodsid); 
	           
        }
	       
	        
	        System.out.println("product========="+product.getGoods_id());
	      
	    
		
		this.addProductToCart(product);
 
		return this.JSON_MESSAGE;
	}
	
	
	
	
	/**
	 * 将一个商品添加到购物车
	 * 需要传递goodsid 和num参数
	 * @param goodsid 商品id，int型
	 * @param num 数量，int型
	 * 
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败
	 * message 为提示信息
	 */
	public String addGoods(){
System.out.println(goodsid);
		Product product = productManager.getByGoodsId(goodsid);
		this.addProductToCart(product);
		return this.JSON_MESSAGE;
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
	public String getCartData(){
		
		try{
			
			String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
			
			Double goodsTotal  =cartManager.countGoodsTotal( sessionid );
			int count = this.cartManager.countItemNum(sessionid);
			
			java.util.Map<String, Object> data =new HashMap();
			data.put("count", count);//购物车中的商品数量
			data.put("total", goodsTotal);//总价
			
			this.json = JsonMessageUtil.getObjectJson(data);
			
		}catch(Throwable e ){
			this.logger.error("获取购物车数据出错",e);
			this.showErrorJson("获取购物车数据出错["+e.getMessage()+"]");
		}
		
		return this.JSON_MESSAGE;
	}
	
	
	/**
	 * 添加货品的购物车
	 * @param product
	 * @return
	 */
	private boolean addProductToCart(Product product){
		String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
		//add by linyang 判断是否是下架
		Integer pid = product.getGoods_id();
		Map goodsMap = goodsManager.get(pid);
		int market = (int) goodsMap.get("market_enable");
		if(market == 0) {
	        this.showErrorJson("该商品已经下架");
		    return false;
		}
		if(product!=null){
			try{
				int enableStore = product.getEnable_store();
				if (enableStore < num) {
					throw new RuntimeException("抱歉！您所选择的货品库存不足。");
				}
				//查询已经存在购物车里的商品
				Member member = UserConext.getCurrentMember();
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
				if (member!=null){
					cart.setMember_id(member.getMember_id());
				}
				this.cartManager.add(cart);
				this.showSuccessJson("货品成功添加到购物车");
				
		
				//需要同时显示购物车信息
				if(showCartData==1){
					this.getCartData();
				}
				
				
				return true;
			}catch(RuntimeException e){
				this.logger.error("将货品添加至购物车出错",e);
				this.showErrorJson(e.getMessage());
				return false;
			}
			
		}else{
			this.showErrorJson("该货品不存在，未能添加到购物车");
			return false;
		}
	}
	
	static String[] activeTables = new String[]{"groupbuy", "flashbuy", "secbuy", "advbuy"};
	/**
	 * 验证添加到购物车中的商品数量是否超出限制
	 * @param goodsId
	 * @return
	 */
	private boolean validCartItemNumber(int goodsId, int num){
	    Map goodsMap = goodsManager.get(goodsId);
	    if(goodsMap == null){
	        return false;
	    }
        
        for(String tableName : activeTables){
            if(goodsMap.containsKey("is_" + tableName) && NumberUtils.toInt(goodsMap.get("is_" + tableName).toString(), 0) == 1){
                Map activeGoodsMap = this.daoSupport.queryForMap("SELECT * FROM es_" + tableName + "_goods WHERE goods_id=?", goodsMap.get("goods_id"));
                
                if(activeGoodsMap != null) {                   
                    //判断是否超售
                    if(NumberUtils.toInt(activeGoodsMap.get("goods_num").toString(), 0) <= 0){
                        this.showErrorJson("此活动商品已售罄！");
                        return false;
                    }
                    
                    //判断剩余商品是否能满足此次购买数量
                    if(num > NumberUtils.toInt(activeGoodsMap.get("goods_num").toString(), 0)){
                        this.showErrorJson("超出购买限制，此商品仅剩余" + activeGoodsMap.get("goods_num") + "件！");
                        return false;
                    }
                    
                    //是否超出单次购买数量限制
                    int limit_num = NumberUtils.toInt(activeGoodsMap.get("limit_num").toString(), 0);
                    if(limit_num > 0 && num > limit_num){
                        this.showErrorJson("此活动商品每人限购" + activeGoodsMap.get("limit_num") + "件！");
                        return false;
                    }
                }
            }
        }

		//判断该商品是否超出直降限制
		CostDown costDown = costDownManager.getBuyGoodsId(goodsId);
		if (costDown != null) {
			if (costDown.getGoods_num() == 0) {
				this.showErrorJson("此活动商品已售罄！");
				return false;
			}
			if (costDown.getGoods_num() < num) {
				this.showErrorJson("超出购买限制，此商品还可以购买" + costDown.getGoods_num() + "件！");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 删除购物车一项
	 * @param cartid:要删除的购物车id,int型,即 CartItem.item_id
	 * 
	 * @return 返回json字串
	 * result  为1表示调用成功0表示失败
	 * message 为提示信息
	 * 
	 * {@link CartItem }
	 */
	public String delete(){
		try{
			HttpServletRequest request =ThreadContextHolder.getHttpRequest();
			String cartid= request.getParameter("cartid");
			cartManager.delete(request.getSession().getId(), Integer.valueOf(cartid));
			this.showSuccessJson("删除成功");
		}catch(RuntimeException e){
			this.logger.error("删除购物项失败",e);
			this.showErrorJson("删除购物项失败");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 更新购物车的数量
	 * @param cartid:要更新的购物车项id，int型，即 CartItem.item_id
	 * @param num:要更新数量,int型
	 * @return 返回json字串
	 * result： 为1表示调用成功0表示失败 int型
	 * store: 此商品的库存 int型
	 */
	public String updateNum(){
		try{
			HttpServletRequest request =ThreadContextHolder.getHttpRequest();
			String cartid= request.getParameter("cartid");
			Integer num= StringUtil.toInt(request.getParameter("num"),null);
			if (num == null || num <= 0) num = 1;

			String productid= request.getParameter("productid");
			Product product=productManager.get(Integer.valueOf(productid));
			
			//验证活动商品数量
			if(!validCartItemNumber(product.getGoods_id(), num)){
                return this.JSON_MESSAGE;
            }
			
			Integer store=product.getEnable_store();
			if(store==null) store=0;
			
			if(store < num) num = store;
			cartManager.updateNum(request.getSession().getId(),  Integer.valueOf(cartid),  num);
			
			json=JsonMessageUtil.getNumberJson("store",store);
		}catch(RuntimeException e){
			this.logger.error("更新购物车数量出现意外错误", e);
			this.showErrorJson("更新购物车数量出现错误");
		}
		return this.JSON_MESSAGE; 
	}
	
	
	/**
     * 更新购物车的数量并且判断商品是否下架
     * @param cartid:要更新的购物车项id，int型，即 CartItem.item_id
     * @param num:要更新数量,int型
     * @return 返回json字串
     * result： 为1表示调用成功0表示失败 int型
     * store: 此商品的库存 int型
     */
    public String updateNumAnd(){
        try{
            HttpServletRequest request =ThreadContextHolder.getHttpRequest();
            String cartid= request.getParameter("cartid");
            Integer num= StringUtil.toInt(request.getParameter("num"),null);
            if (num == null || num <= 0) num = 1;
            String productid= request.getParameter("productid");
            Product product=productManager.get(Integer.valueOf(productid));
            Map good = goodsManager.get(product.getGoods_id());
            //验证活动商品数量
            if(!validCartItemNumber(product.getGoods_id(), num)){
                return this.JSON_MESSAGE;
            }
            
            Integer store=product.getEnable_store();
            if(store==null) store=0;
            
            if(store < num) num = store;
            cartManager.updateNum(request.getSession().getId(),  Integer.valueOf(cartid),  num);
            if(good.get("market_enable").toString().equals("0")){
                store = -1;
            }
            json=JsonMessageUtil.getNumberJson("store",store);
        }catch(RuntimeException e){
            this.logger.error("更新购物车数量出现意外错误", e);
            this.showErrorJson("更新购物车数量出现错误");
        }
        return this.JSON_MESSAGE; 
    }
	
	
	
	
	/**
	 * 购物车的价格总计信息
	 * @param 无
	 * @return 返回json字串
	 * result： 为1表示调用成功0表示失败 int型
	 * orderprice: 订单价格，OrderPrice型
	 * {@link OrderPrice}  
	 */
	public String getTotal(){
		HttpServletRequest request =ThreadContextHolder.getHttpRequest();
		String sessionid  = request.getSession().getId();
		OrderPrice orderprice  =this.cartManager.countPrice(cartManager.listGoods(sessionid), null, null);
		this.json = JsonMessageUtil.getObjectJson(orderprice);
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 清空购物车
	 */
	
	public String clean(){	
		HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
		try{
			cartManager.clean(request.getSession().getId());
			this.showSuccessJson("清空购物车成功");
		}catch(RuntimeException e){
			this.logger.error("清空购物车",e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE; 
	}

	/**
	 * 选择购物车商品进行部分结算
	 */
	public String selectItems(){
		try{
			HttpServletRequest request =ThreadContextHolder.getHttpRequest();
			String itemids = request.getParameter("itemids");
			String sessionid = request.getSession().getId();

			List<Integer> cartIdList = new ArrayList<Integer>();
			if(!StringUtils.isEmpty(itemids)){
				String[] itemidArray = StringUtils.split(itemids, ",");
				if(itemidArray!= null && itemidArray.length > 0) {
					for(String itemid : itemidArray){
						cartIdList.add(NumberUtils.toInt(itemid, 0));
					}
				}
			}

			this.cartManager.selectItems(sessionid, cartIdList);
			this.storeCartManager.countPrice("no", false);
			this.showSuccessJson("选择商品成功！");
		}catch(RuntimeException e){
			this.logger.error("选择购物车商品出现意外错误", e);
			this.showErrorJson("选择购物车商品出现错误");
		}
		return this.JSON_MESSAGE;
	}
	
	public ICartManager getCartManager() {
		return cartManager;
	}
	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}


	public int getGoodsid() {
		return goodsid;
	}


	public void setGoodsid(int goodsid) {
		this.goodsid = goodsid;
	}


	public int getProductid() {
		return productid;
	}


	public void setProductid(int productid) {
		this.productid = productid;
	}


	public IProductManager getProductManager() {
		return productManager;
	}


	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}


	public int getNum() {
		return num;
	}


	public void setNum(int num) {
		this.num = num;
	}




	public int getShowCartData() {
		return showCartData;
	}




	public void setShowCartData(int showCartData) {
		this.showCartData = showCartData;
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

	public IStoreCartManager getStoreCartManager() {
		return storeCartManager;
	}

	public void setStoreCartManager(IStoreCartManager storeCartManager) {
		this.storeCartManager = storeCartManager;
	}
}
