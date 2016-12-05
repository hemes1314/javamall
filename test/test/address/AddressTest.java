package test.address;

import static org.junit.Assert.assertEquals;

import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;
import com.alibaba.fastjson.JSONObject;

import test.BaseTest;

import java.util.Date;

public class AddressTest extends BaseTest{
	
	@Test 
	public void AddaddresTest(){

		PostMethod method=getPostMethod("/api/mobile/address!add.do");
		method.addParameter("name", "lifenlongss");
	    method.addParameter("province_id", "1");    
	    method.addParameter("city_id", "41");  
	    method.addParameter("region_id", "459");  
	    method.addParameter("addr", "顺丰sss"); 
	    method.addParameter("mobile", "13124106821");  
	    method.addParameter("zip", "700100");  
	    method.addParameter("def_addr","1");

	    JSONObject o = getJson(method);
	    assertEquals(1, o.getIntValue("result"));
        assertEquals("添加成功", o.getString("message"));

	}

	public static void main(String[] args) {
		long l = 189421046;
		System.out.println(new Date(l));
	}

}
