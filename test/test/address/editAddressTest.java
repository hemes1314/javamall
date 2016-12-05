package test.address;

import static org.junit.Assert.assertEquals;

import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import test.BaseTest;

public class editAddressTest extends BaseTest{
	@Test 
	public void EditaddresTest(){

		PostMethod method= getPostMethod("/api/mobile/address!edit.do");
		method.addParameter("addr_id","5");
		method.addParameter("name", "lifenlong");
	    method.addParameter("province_id", "1");    
	    method.addParameter("city_id", "41");  
	    method.addParameter("region_id", "459");  
	    method.addParameter("addr", "顺丰"); 
	    method.addParameter("mobile", "13124106821");  
	    method.addParameter("zip", "700100");  
	    method.addParameter("def_addr","1");

	    JSONObject o = getJson(method);
	    assertEquals(1, o.getIntValue("result"));
        assertEquals("修改成功", o.getString("message"));

	}

}
