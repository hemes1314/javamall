package com.enation.app.b2b2c.core.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


import org.junit.Before;
import org.junit.Test;

import com.enation.app.b2b2c.core.model.StoreProduct;
import com.enation.app.b2b2c.core.model.cart.StoreCart;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.service.cart.IStoreProductManager;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.framework.test.SpringTestSupport;
import com.enation.framework.util.DateUtil;

public class StoreOrderTest extends SpringTestSupport {
	private ICartManager cartManager;
	private IStoreProductManager storeProductManager;
	private IOrderManager orderManager;
	@Before
	public void mock(){
		cartManager = this.getBean("cartManager");
		storeProductManager = this.getBean("storeProductManager");
		orderManager = this.getBean("orderManager");
       
	}
	@Test
	public void test(){
		for (int i = 0; i < 100; i++) {
			addCart("111111");
			addStoreOrder("111111");
		}
		
	}
	/**
	 * 创建订单
	 * @param sessionId
	 */
	public void addStoreOrder(String sessionId){
		try {
			StoreOrder order=new StoreOrder();
			
			order.setShipping_id(1); //配送方式
			order.setPayment_id(1);//支付方式
			
			order.setShip_provinceid(1);
			order.setShip_cityid(33);
			order.setShip_regionid(451);
			
			order.setShip_addr("详细收货地址");
			order.setShip_mobile("13366236200");
			order.setShip_tel("010-62351822");
			order.setShip_zip("1111111");
			order.setShipping_area("北京-密云-密云县");
			order.setShip_name("客户名称");
			order.setRegionid(451);
			Date date =randomDate("2015-05-01", "2015-05-31");
			String str  = DateUtil.toString(date, "yyyy-MM-dd HH:mm:ss");
			order.setCreate_time((Long) (date.getTime()/1000));
			order.setShip_day("发货时间");
			order.setShip_time("ship_time");
			order.setRemark("来两袋辣条");
			
			order.setMember_id(2L);
//			orderManager.add(order,sessionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	/**
	 * 添加购物车商品
	 */
	public void addCart(String sessionId){
		StoreProduct product = storeProductManager.get(298);
		
		StoreCart cart = new StoreCart();
		cart.setGoods_id(product.getGoods_id());
		cart.setProduct_id(product.getProduct_id());
		cart.setSession_id(sessionId);
		cart.setNum(1);
		cart.setItemtype(0); //0为product和产品 ，当初是为了考虑有赠品什么的，可能有别的类型。
		cart.setWeight(product.getWeight());
		cart.setPrice( product.getPrice() );
		cart.setName(product.getName());
		cart.setStore_id(1);
		this.cartManager.add(cart);
	}
	/**
	 * 获取随机数
	 * @param min 	最小
	 * @param max	最大
	 * @return
	 */
	public Integer getRandom(Integer min,Integer max){
		Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        return s;
	}
	 /** 
     * 获取随机日期 
     *  
     * @param beginDate 
     *            起始日期，格式为：yyyy-MM-dd 
     * @param endDate 
     *            结束日期，格式为：yyyy-MM-dd 
     * @return 
     */  
    private   Date randomDate(String beginDate, String endDate) {  
        try {  
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
            Date start = format.parse(beginDate);// 构造开始日期  
            Date end = format.parse(endDate);// 构造结束日期  
            // getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。  
            if (start.getTime() >= end.getTime()) {  
                return null;  
            }  
            long date = random(start.getTime(), end.getTime());  
  
            return new Date(date);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }
    private   long random(long begin, long end) {  
        long rtn = begin + (long) (Math.random() * (end - begin));  
        // 如果返回的是开始时间和结束时间，则递归调用本函数查找随机值  
        if (rtn == begin || rtn == end) {  
            return random(begin, end);  
        }  
        return rtn;  
    }
}
