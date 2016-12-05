package test.order;

import static org.junit.Assert.assertEquals;

import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;

import test.BaseTest;

public class AddorderTest extends BaseTest {
	
	@BeforeClass
	public static void login(){
		PostMethod method= getPostMethod("/api/mobile/member!login.do");			
		method.addParameter("username", "13664288387");
	    method.addParameter("password", "123456");    
	    JSONObject o = getJson(method);
	}
//	@Test
//    public void testToCount(){
//        PostMethod method = getPostMethod("/api/mobile/favorite!list.do");
//        
//        JSONObject o =getJson(method);
//        assertEquals(1, o.getIntValue("result"));
//        JSONObject data = o.getJSONObject("data");
//    }
//     @Test
//      public void testToCount(){
//          PostMethod method = getPostMethod("/api/mobile/order!list.do");
//          method.addParameter("status","0");
//          JSONObject o =getJson(method);
//          assertEquals(1, o.getIntValue("result"));
//          JSONObject data = o.getJSONObject("data");
//      }
	@Test
	public void testToCount(){
		PostMethod method = getPostMethod("/api/mobile/order!storeCartGoods.do");
		method.addParameter("countCart","[{cart_id=3857,cart_num=1}]");
		JSONObject o =getJson(method);
		assertEquals(1, o.getIntValue("result"));
		JSONObject data = o.getJSONObject("data");
	}
//	@Test
//	public void testgetTotalPrice(){
//		PostMethod method = getPostMethod("/api/mobile/order!getTotalPrice.do");
//		method.addParameter("type_id","1");
//		method.addParameter("address_id","44");
//		method.addParameter("bonus_id","41");
//		method.addParameter("storeId","1");
//		method.addParameter("storeBonusId","1");
//		method.addParameter("advancePay","150");
//		method.addParameter("cart_id","241,242");
//		JSONObject o =getJson(method);
//		assertEquals(1, o.getIntValue("result"));
//		JSONObject data = o.getJSONObject("data");
//	}
//	@Test
//	public void testCreateOrder() throws Exception{
//		PostMethod method = getPostMethod("/api/mobile/order!create.do");
//		method.addParameter("TypeId","1");
//		method.addParameter("paymentId","1");
//		method.addParameter("addressId","44");
//		method.addParameter("shipDay","234");
//		method.addParameter("shipTime","0");	
//		method.addParameter("cart_id","289");
//		method.addParameter("receipt","1");
//		method.addParameter("receiptType","2");
//		method.addParameter("receiptContent","hello");
//		method.addParameter("receiptTitle","hello");
//		method.addParameter("bonusid","41");
//		JSONObject o =getJson(method);
//		assertEquals(1, o.getIntValue("result"));
//		assertEquals("添加订单成功", o.getString("message"));
//		JSONObject data = o.getJSONObject("data");
//		
//	}
}
