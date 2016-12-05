package test.toWorkFlow;

import static org.junit.Assert.assertEquals;

import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import test.BaseTest;

public class TestWorkFlow extends BaseTest{
	
	@BeforeClass
	public static void login(){
		PostMethod method= getPostMethod("/api/mobile/member!login.do");			
		method.addParameter("username", "lifenlong");
	    method.addParameter("password", "lifenlong");    
	    JSONObject o = getJson(method);
	}
	@Test 
	public void toTestWorkFlow(){
		
		PostMethod method=getPostMethod("/api/store/storeSellBack!save.do");
	    method.addParameter("remark", "testApitestApi");
	    method.addParameter("orderId", "177");    
	    method.addParameter("file", "http://localhost:8080/javamall/statics/attachment/spec/201510101038267680.jpg,http://localhost:8080/javamall/statics/attachment/spec/201510101038328655.jpg");  
	    method.addParameter("refund_way", "1");
	    method.addParameter("return_account", "200");  
	    JSONObject o = getJson(method);
        assertEquals(1, o.getIntValue("result"));
	}
}
