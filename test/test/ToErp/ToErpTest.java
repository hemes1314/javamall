package test.ToErp;

import static org.junit.Assert.assertEquals;

import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import test.BaseTest;

public class ToErpTest extends BaseTest{
	
//	@Test 
//	public void toErpTest(){
//
//		PostMethod method=getPostMethod("/api/mobile/order!updateShipState.do");
//	    method.addParameter("ship_status", "3");
//	    method.addParameter("order_id", "177");    
//	    method.addParameter("ship_no", "AAAsssAA");  
//	    method.addParameter("logi_id", "1");  
//	    method.addParameter("logi_name", "顺丰");  
//	    JSONObject o = getJson(method);
//        assertEquals(1, o.getIntValue("result"));
//        assertEquals("更新成功", o.getString("message"));
//	}
	@Test 
	public void toErpTest(){

		PostMethod method=getPostMethod("/api/mobile/erp!getGoods.do");
	    method.addParameter("brand_name", "helle");
	    method.addParameter("brand_english_name", "helle"); 
	    method.addParameter("sn", "123456");  
	    method.addParameter("goods_name", "AAAsssAA");  
	    method.addParameter("goods_english_name", "hehe");  
	    method.addParameter("goods_lv", "1");  
	    method.addParameter("goods_year", "2"); 
	    method.addParameter("goods_alcohol", "3"); 
	    method.addParameter("goods_type", "4");
	    method.addParameter("specs_type", "4"); 
	    method.addParameter("price", "10.0"); 
	    method.addParameter("store", "1"); 
	    method.addParameter("store_id", "1"); 
	    JSONObject o = getJson(method);
        assertEquals(1, o.getIntValue("result"));
        assertEquals("更新成功", o.getString("message"));
	}
}
