package com.enation.app.shop.component.express.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.express.AbstractExpressPlugin;
import com.enation.app.base.core.plugin.express.IExpressEvent;
import com.enation.app.shop.component.express.pojo.TaskRequest;
import com.enation.app.shop.component.express.pojo.TaskResponse;
import com.enation.app.shop.core.action.pojo.ResultItem;
import com.enation.app.shop.core.service.impl.Kuaidi100BackManager;
import com.enation.eop.processor.core.RemoteRequest;
import com.enation.eop.processor.core.Request;
import com.enation.eop.processor.core.Response;
import com.enation.framework.util.JsonUtil;
import com.google.gson.JsonArray;

/**
 * 快递100插件接口
 * @author xulipeng
 */

@Component
public class Kuaidi100Plugin extends AbstractExpressPlugin implements IExpressEvent {
    public Kuaidi100BackManager  kuaidi100BackManager;
	@Override
	public 	Map getExpressDetail(String com, String nu, Map params) {

	    //物流查询
	    ArrayList<Map> result = (ArrayList) kuaidi100BackManager.get(com, nu);

		//add by lin todo 等待正式合同和协议，从数据库中取物流信息
        Map map = new LinkedHashMap();
        if(result.size()>0)
        {
            Map m = (Map) result.get(0);
            map.put("com",m.get("com"));
            map.put("nu",m.get("nu"));
            map.put("status",m.get("status"));
            map.put("message",m.get("message"));
            
            ArrayList<Map> dataList = new ArrayList();
            for(Map item: result){
                dataList.add(item);
            }
       
            map.put("data", dataList);
         
    		//物流各个时间的状态信息放到tempList中
/*            ArrayList tempList = new ArrayList();
            Map a = new LinkedHashMap();
            a.put("time", "物流系统");
            a.put("location", "location");
            a.put("context", "暂无信息");
            tempList.add(a);
            
            map.put("data", tempList);*/
        }else{//发送api请求
            //keyid 是快递100给的密钥
                String keyid = (String) params.get("keyid");
                    
                    Request remoteRequest  = new RemoteRequest();
                    String kuaidiurl="http://api.kuaidi100.com/api?id="+keyid+"&nu="+nu+"&com="+getCodeByComtext(com)+"&muti=1&order=asc";
                    
                    Response remoteResponse = remoteRequest.execute(kuaidiurl);
                    String content  = remoteResponse.getContent();
                    
                    Map apiMap = JsonUtil.toMap(content);
                    if(apiMap.get("status").equals("1")){
                        apiMap.put("message", "ok");
                    }
                    
                    map = apiMap;
        }
		return map;
	}
	
	
    private String getCodeByComtext(String com) {

        if("debang".equals(com)) {
            return "debangwuliu";
        }

        else if("zjs".equals(com)) { return "zhaijisong"; }

        return com;
    }

	@Override
	public String getId() {
		return "kuaidi100Plugin";
	}

	@Override
	public String getName() {
		return "快递100接口插件";
	}

    
    public Kuaidi100BackManager getKuaidi100BackManager() {
        return kuaidi100BackManager;
    }

    
    public void setKuaidi100BackManager(Kuaidi100BackManager kuaidi100BackManager) {
        this.kuaidi100BackManager = kuaidi100BackManager;
    }

	

}
