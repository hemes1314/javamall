package com.enation.app.shop.core.action.backend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.sf.json.JSONArray;

import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.framework.action.WWAction;
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("demoJson")
@Results({
	@Result(name="demolist", type="freemarker", location="/shop/admin/demo/demo_list.html")
})
public class DemoJsonAction extends WWAction{

	private IMemberLvManager memberLvManager;
	
	public String demolist() throws IOException{
		return "demolist";
	}
	
	public String demo(){
		List list = new ArrayList();
		for(int i =0;i<=5;i++){
			Map map = new HashMap();
			map.put("name", i);
			map.put("sex", i+"qq");
			list.add(map);
		}
		this.json= "{\"total\":100,\"rows\":"+JSONArray.fromObject(list).toString()+"}";
		
		return JSON_MESSAGE;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}
	
	
}
