package test.shop;

import static org.junit.Assert.assertEquals;

import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import test.BaseTest;

public class FlashBuyApiTest extends BaseTest {

    @BeforeClass
    public static void login() {
        PostMethod method = getPostMethod("/api/mobile/member!login.do");
        method.addParameter("username", "food");
        method.addParameter("password", "123456");    
        JSONObject o = getJson(method);
    }
    
    @Test
    public void testPage() {
        PostMethod method = getPostMethod("/api/mobile/flashbuy!page.do");
        JSONObject o = getJson(method);
        assertEquals(1, o.getIntValue("result"));
        JSONArray data = o.getJSONArray("data");
        assertEquals(5, data.size());
    }
}
