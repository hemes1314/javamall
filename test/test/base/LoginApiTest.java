package test.base;

import org.apache.commons.httpclient.methods.PostMethod;
import static org.junit.Assert.*;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import test.BaseTest;


public class LoginApiTest extends BaseTest {

    @Test
    public void testLoginFailed() throws Exception {
        PostMethod method = getPostMethod("/api/mobile/member!login.do");
        method.addParameter("username", "demo_man");
        method.addParameter("password", "111112");    
        JSONObject o = getJson(method);
        assertEquals(0, o.getIntValue("result"));
        assertEquals("账号密码错误", o.getString("message"));
        System.out.println("result:" + o.getIntValue("result"));
    }

    @Test
    public void testLoginOk() throws Exception {
        PostMethod method = getPostMethod("/api/mobile/member!login.do");
        method.addParameter("username", "demo_man");
        method.addParameter("password", "111111");    
        JSONObject o = getJson(method);
        assertEquals(1, o.getIntValue("result"));
        JSONObject data = o.getJSONObject("data");
        assertEquals(4, data.getIntValue("member_id"));
        assertEquals("普通会员", data.getString("level"));
        //System.out.println("result:" + o.getIntValue("result"));
    }
}
