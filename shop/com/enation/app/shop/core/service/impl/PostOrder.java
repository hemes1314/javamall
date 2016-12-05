package com.enation.app.shop.core.service.impl;

import java.util.HashMap;

import com.enation.app.shop.component.express.pojo.TaskRequest;
import com.enation.app.shop.component.express.pojo.TaskResponse;
import com.enation.app.shop.component.express.plugin.*;

public class PostOrder {

	public static void main(String[] args){
		TaskRequest req = new TaskRequest();
		req.setCompany("yuantong");
		req.setFrom("上海浦东新区");
		req.setTo("广东深圳南山区");
		req.setNumber("12345678");
		req.getParameters().put("callbackurl", "http://www.yourdmain.com/kuaidi");
		req.setKey("testkuaidi1031");
		
		HashMap<String, String> p = new HashMap<String, String>(); 
		p.put("schema", "json");
		p.put("param", JacksonHelper.toJSON(req));
		try {
			String ret = HttpRequest.postData("http://www.kuaidi100.com/poll", p, "UTF-8");
			TaskResponse resp = JacksonHelper.fromJSON(ret, TaskResponse.class);
			if(resp.getResult()==true){
				System.out.println("订阅成功");
			}else{
				System.out.println("订阅失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
