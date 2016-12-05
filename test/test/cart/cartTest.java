package test.cart;

import static org.junit.Assert.assertEquals;

import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import test.BaseTest;

public class cartTest extends BaseTest{	

	@BeforeClass
	public static void login(){
		PostMethod method= getPostMethod("/api/mobile/member!login.do");			
		method.addParameter("username", "feng2");
	    method.addParameter("password", "999999");    
	    JSONObject o = getJson(method);
	}
//	@Test
//	public void testaddCart() throws Exception{
//		PostMethod method =getPostMethod("/api/mobile/cart!addGoods.do");
//		method.addParameter("goodsid", "146");
//	    method.addParameter("num", "9"); 
//		JSONObject o =getJson(method);
//		assertEquals(1, o.getIntValue("result"));
//		JSONObject data = o.getJSONObject("data");
//	}
	   @Test 
	    public void testaddCartList() throws Exception{
	        PostMethod method =getPostMethod("/api/mobile/cart!list.do"); 
	        JSONObject o =getJson(method);
	        assertEquals(1, o.getIntValue("result"));
	        JSONObject data = o.getJSONObject("data");
	    }
//	@Test 
//	public void teststoreCartGoods() throws Exception{
//		PostMethod method =getPostMethod("/api/mobile/order!storeCartGoods.do");
//		method.addParameter("countCart","[{cart_id=1590,cart_num=2},{cart_id=1694,cart_num=2}]");
//		JSONObject o =getJson(method);
//		
//		assertEquals(1, o.getIntValue("result"));
//        JSONArray data = o.getJSONArray("data");
//        assertEquals(0, data.size());
//	}

}
