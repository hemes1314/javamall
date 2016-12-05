package test.base;

import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;

import test.BaseTest;

public class registerTest extends BaseTest{
	
	@Test
	public void TestRegister(){
		PostMethod method = getPostMethod("/api/mobile/member!register.do");
		
	}

}
