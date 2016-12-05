package com.enation.app.shop.core.action.api;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.enation.app.shop.core.action.pojo.NoticeResponse;

import com.enation.app.shop.core.service.impl.Kuaidi100BackManager;
import com.enation.framework.action.WWAction;

@Component
@SuppressWarnings("serial")
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("kuaidi")
public class Kuaidi100BackApiAction extends WWAction  {
    Kuaidi100BackManager  kuaidi100BackManager;
    //物流回调
	public void kuaidipost() {
        HttpServletRequest request = ServletActionContext.getRequest(); 
        HttpServletResponse response = ServletActionContext.getResponse();
        NoticeResponse resp = new NoticeResponse();
        resp.setResult(false);
        resp.setReturnCode("500");
        resp.setMessage("保存失败");
        String status = "";
        String message = "";
        String com = "";
        String nu = "";
        String state = "";
        String ftime = "";
        String context = "";
        try {
            String param = request.getParameter("param");
            JSONObject json0 = JSON.parseObject(param);
            Iterator iter0 = json0.keySet().iterator(); 
            while (iter0.hasNext()) {  
                String key0 = (String) iter0.next();  
                String value0 = json0.getString(key0); 
                if(key0.equals("lastResult")){
                    JSONObject jsondata = JSON.parseObject(value0);  
                    Iterator iter2 = jsondata.keySet().iterator();
                    while (iter2.hasNext()) {
                        String key2 = (String) iter2.next();  
                        String value2 = jsondata.getString(key2);
    
                        if(key2.equals("status")){
                            status = value2;
                        }
                        if(key2.equals("message")){
                            message = value2;
                        }
                        if(key2.equals("com")){
                            com = value2;
                        }
                        if(key2.equals("nu")){
                            nu = value2;
                        }
                        if(key2.equals("state")){
                            state = value2;
                        }
                    }
                }
            }
            
            JSONObject json = JSON.parseObject(param);
            Iterator iter = json0.keySet().iterator();
            while (iter.hasNext()) {  
             String key = (String) iter.next();  
             String value = json.getString(key); 
             if(key.equals("lastResult")){
                 JSONObject jsondata = JSON.parseObject(value);  
                 Iterator iter2 = jsondata.keySet().iterator();
                 while (iter2.hasNext()) {
                     String key2 = (String) iter2.next();  
                     String value2 = jsondata.getString(key2);
                     if(key2.equals("data")){
                         JSONArray jsonArr = JSON.parseArray(value2);
                         Iterator iter3 = jsonArr.iterator();
                         for (int i = 0; i < jsonArr.size(); i++) { 
                            JSONObject o = jsonArr.getJSONObject(i); 
                            Iterator iter4 = o.keySet().iterator();
                            while (iter4.hasNext()) {
                                String key4 = (String) iter4.next();  
                                String value4 = o.getString(key4);
                                if(key4.equals("ftime")){
                                    ftime = value4;
                                }
                                if(key4.equals("context")){
                                    context = value4;
                                }
                                
                                System.out.println(key4+":"+value4);
                            }
                            kuaidi100BackManager.add(status,message,com,nu,state,ftime,context);
                         }
                     }
                 }
             }
            }
            resp.setResult(true);
            resp.setReturnCode("200");
            try {
                response.getWriter().print(JSON.toJSON(resp));
            } catch(IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } //这里必须返回，否则认为失败，过30分钟又会重复推送。
        } catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
	}

    
    public Kuaidi100BackManager getKuaidi100BackManager() {
        return kuaidi100BackManager;
    }

    
    public void setKuaidi100BackManager(Kuaidi100BackManager kuaidi100BackManager) {
        this.kuaidi100BackManager = kuaidi100BackManager;
    }

}