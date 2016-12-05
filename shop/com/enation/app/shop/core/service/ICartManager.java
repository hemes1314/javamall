package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;

/**
 * 购物车业务接口
 * @author kingapex
 * @see com.enation.test.shop.cart.CartTest#testAdd
 *2010-3-23下午03:26:12
 */
public interface ICartManager {
	
	/**
	 * 添加一个购物项
	 * @param cart
	 * @return cart_id
	 */
	public int add(Cart cart);
	
	/**
	 * 根据购物车ID来获取购物车信息
	 * @param cart_id
	 * @return 购物车
	 */
	public Cart get(int cart_id);
	
	
	/**
	 * 计算购物车中货物总数
	 * @param sessionid
	 * @return 货物总数
	 */
	public Integer countItemNum(String sessionid);
	
	/**
	 * 检测某个货品是否有购物车使用
	 * @param goodsid
	 * @return
	 */
	public boolean checkGoodsInCart(Integer goodsid);
	
	/**
	 * 根据productId和sessionId来判断购物车中是否已经存在了一个物品
	 * @param productId
	 * @param sessionid
	 * @return
	 */
	public Cart getCartByProductId(int productId, String sessionid);
	
	/**
	 * 根据productId和sessionId以及addon来判断购物车中是否已经存在了一个物品
	 * @param productId
	 * @param sessionid
	 * @param addon
	 * @return
	 */
	public Cart getCartByProductId(int productId, String sessionid, String addon);
	
	
	
	/**
	 * 读取某用户的购物车中项列表
	 * @param sessionid
	 * @return
	 */
	public List<CartItem> listGoods(String sessionid);

	/**
	 * 读取某用户的购物车中项列表
	 * @param sessionid
	 * @param onlySelected 是否只显示用户选中的商品
	 * @return
	 */
	public List<CartItem> listGoods(String sessionid, boolean onlySelected);
	
	 
	/**
	 * 清空某用户的购物车
	 * @param sessionid
	 */
	public void  clean(String sessionid);

	/**
	 * 清空某用户的购物车中指定的商品
	 * @param sessionid
	 * @param cartids
	 */
	public void clean(String sessionid,Integer[] cartids);
	
 
	
	/**
	 * 更新购物数量
	 * @param sessionid
	 * @param cartid
	 */
	public void updateNum(String sessionid,Integer cartid,Integer num);
	
	


	/**
	 * 删除购物车中的某几项
	 * @param sessionid
	 * @param cartid
	 */
	public void delete(String sessionid,Integer cartid);
	
 
	/**
	 * 计算购买商品重量，包括商品、捆绑商品、赠品
	 * @param sessionid
	 * @return
	 */
	public Double countGoodsWeight(String sessionid);
	
 
	/**
	 * 计算购物车中货物的总积分
	 * @param sessionid
	 * @return
	 */
	public  Integer countPoint(String sessionid);
	
	/**
	 * 计算订单价格<br>
	 * 根据购物车列表、配送方式，及收货地区id来计算订单价格。
	 * 负责订单创建流程中价格的处理<br>
	 * 包括：购物车中的总价计算，结算页的总价计算，订单创建时的总价计算。<br>
	 * @param cartItemList 购物车列表，在订单价格计算时，商品总价会根据 CartItem 中的coupPrice（优惠后的价格）计算
	 * @see CartItem
	 * @param shippingid 配送方式id，用于计算配送费用，不同的配置方式运费价格可能不一样
	 * @param regionid  地区id，配送地区不同，运费可能不同
	 * @return 订单价格
	 * @see OrderPrice
	 */
	public OrderPrice countPrice(List<CartItem> cartItemList, Integer shippingid, String regionid);
	
	
	
	
	/**
	 * 计算购物商品货物总价(原始的，未处理优惠数据的)
	 * @param sessionid
	 * @return
	 */
	public Double countGoodsTotal(String sessionid);

	public List<CartItem> listGoods(String sessionid, Long member_id);
	/**
	 * 通过购物车Id 获得列表
	 * @param cart_id
	 * @return
	 */
	public List<CartItem> getByCartId(String cart_id);
	/**
	 * app端删除购物车
	 * @param cartid
	 */
	public void deleteForApp(String cartid);
	/**
	 * app 更新购物车
	 * @param cartId
	 * @param cartNum
	 */
	public void updateNumForApp(String sessionid,Integer cartId, Integer cartNum);
	/**
	 * 清空
	 * @param id
	 */
    public void cleanForApp(long member_id);
    
    /**
     * 获取一个商品在购物车中的数量
     * @param goods_id
     * @param session_id
     * @return
     */
    public int countGoods(int goods_id, String session_id);

	/**
	 * 选择购物车商品
	 * @param sessionid
	 * @param cartIdList
	 */
	public void selectItems(String sessionid, List<Integer> cartIdList);


}
