package com.enation.app.shop.mobile.action.yuemo;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.action.api.JacksonHelper;
import com.enation.app.shop.core.action.pojo.NoticeRequest;
import com.enation.app.shop.core.action.pojo.NoticeResponse;
import com.enation.app.shop.core.action.pojo.Result;
import com.enation.app.shop.core.action.pojo.ResultItem;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.app.shop.core.service.impl.Kuaidi100BackManager;
import com.enation.app.shop.mobile.service.impl.ApiYuemoManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonMessageUtil;



@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/api/mobile")
@Action("yuemo")
@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })

public class YuemoApiAction extends WWAction {
	private Integer[] id;
	private String title;
	private String content;
	private Yuemo yuemo;
	private String stime;
    private Integer yuemoId;	
	private List<Yuemo> yuemoList;
	private ApiYuemoManager apiYuemoManager;
	private final int PAGE_SIZE = 20;

    Kuaidi100BackManager  kuaidi100BackManager;
    //物流回调
    public String kuaidipost() throws ParseException{
        HttpServletRequest request = ServletActionContext.getRequest(); 
        HttpServletResponse response = ServletActionContext.getResponse();
        NoticeResponse resp = new NoticeResponse();
        resp.setResult(false);
        resp.setReturnCode("500");
        resp.setMessage("保存失败");
        try {
            String param = request.getParameter("param");
            NoticeRequest nReq = JacksonHelper.fromJSON(param,
                    NoticeRequest.class);

            Result result = nReq.getLastResult();
            //监控状态:polling:监控中，shutdown:结束，abort:中止，updateall：重新推送。
            //其中当快递单为已签收时status=shutdown 
            String status = result.getStatus();
            //信息 监控状态相关消息，如:3天查询无记录，60天无变化
            String message = result.getMessage();
            //快递公司代码
            String com = result.getCom();
            /*单号*/
            String nu = result.getNu();
            /*是否签收 0在途中、1已揽收、2疑难、3已签收*/
            String state = result.getState();
            
            // 处理快递结果
            ArrayList<ResultItem> resutlItems =  result.getData();
            
            for(ResultItem item:resutlItems){
                String context = item.getContext();
                String ftime = item.getFtime();
                kuaidi100BackManager.add(status,message,com,nu,state,ftime,context);
            }
            resp.setResult(true);
            resp.setReturnCode("200");
            try {
                response.getWriter().print(JacksonHelper.toJSON(resp));
            } catch(IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } //这里必须返回，否则认为失败，过30分钟又会重复推送。
        } catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return WWAction.JSON_MESSAGE;
    }
    
    //物流回调
    public String test1() throws ParseException{
        HttpServletRequest request = ServletActionContext.getRequest(); 
        HttpServletResponse response = ServletActionContext.getResponse();
        NoticeResponse resp = new NoticeResponse();
        resp.setResult(false);
        resp.setReturnCode("500");
        resp.setMessage("保存失败");
        try {
            String param = request.getParameter("param");
            NoticeRequest nReq = JacksonHelper.fromJSON(param,
                    NoticeRequest.class);

            Result result = nReq.getLastResult();
            //监控状态:polling:监控中，shutdown:结束，abort:中止，updateall：重新推送。
            //其中当快递单为已签收时status=shutdown 
            String status = result.getStatus();
            //信息 监控状态相关消息，如:3天查询无记录，60天无变化
            String message = result.getMessage();
            //快递公司代码
            String com = result.getCom();
            /*单号*/
            String nu = result.getNu();
            /*是否签收 0在途中、1已揽收、2疑难、3已签收*/
            String state = result.getState();
            
            // 处理快递结果
            ArrayList<ResultItem> resutlItems =  result.getData();
            
            for(ResultItem item:resutlItems){
                String context = item.getContext();
                String ftime = item.getFtime();
                kuaidi100BackManager.add(status,message,com,nu,state,ftime,context);
            }
            resp.setResult(true);
            resp.setReturnCode("200");

        } catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return WWAction.JSON_MESSAGE;
    }
    
    //物流回调
    public String test2() throws ParseException{
        HttpServletRequest request = ServletActionContext.getRequest(); 
        HttpServletResponse response = ServletActionContext.getResponse();
        NoticeResponse resp = new NoticeResponse();
        resp.setResult(false);
        resp.setReturnCode("500");
        resp.setMessage("保存失败");
        try {
            String param = request.getParameter("param");
            NoticeRequest nReq = JacksonHelper.fromJSON(param,
                    NoticeRequest.class);

            Result result = nReq.getLastResult();
            //监控状态:polling:监控中，shutdown:结束，abort:中止，updateall：重新推送。
            //其中当快递单为已签收时status=shutdown 
            String status = result.getStatus();
            //信息 监控状态相关消息，如:3天查询无记录，60天无变化
            String message = result.getMessage();
            //快递公司代码
            String com = result.getCom();
            /*单号*/
            String nu = result.getNu();
            /*是否签收 0在途中、1已揽收、2疑难、3已签收*/
            String state = result.getState();
            
 
        } catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return WWAction.JSON_MESSAGE;
    }
    
    //物流回调
    public String test3() throws ParseException{
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
                response.getWriter().print(JacksonHelper.toJSON(resp));
            } catch(IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } //这里必须返回，否则认为失败，过30分钟又会重复推送。
        } catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return WWAction.JSON_MESSAGE;
    }
    
    //物流回调
  /*  public String test4() throws ParseException{
        import net.sf.json.JSONArray;
        import net.sf.json.JSONObject;
        HttpServletRequest request = ServletActionContext.getRequest(); 
        HttpServletResponse response = ServletActionContext.getResponse();
        NoticeResponse resp = new NoticeResponse();
        resp.setResult(false);
        resp.setReturnCode("500");
        resp.setMessage("保存失败");
        try {
            JSONObject json = null;  
            String param = request.getParameter("param");
            json = JSONObject.fromObject(param);  
            Iterator iter = json.keySet().iterator();  
            Map<String,String> map = new HashMap<String,String>();  
            while (iter.hasNext()) {  
             String key = (String) iter.next();  
             String value = json.getString(key); 
             if(key.equals("lastResult")){
                 JSONObject jsondata = JSONObject.fromObject(value);  
                 Iterator iter2 = jsondata.keySet().iterator();
                 Map<String,String> map2 = new HashMap<String,String>(); 
                 while (iter2.hasNext()) {
                     String key2 = (String) iter2.next();  
                     String value2 = jsondata.getString(key2);
                     String status = "";
                     String message = "";
                     String com = "";
                     String nu = "";
                     String state = "";
                     String ftime = "";
                     String context = "";
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
                     if(key2.equals("data")){
                         JSONArray jsonArr = JSONArray.fromObject(value2);
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
                response.getWriter().print(JacksonHelper.toJSON(resp));
            } catch(IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } //这里必须返回，否则认为失败，过30分钟又会重复推送。
 
        } catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return WWAction.JSON_MESSAGE;
    }*/
    
    
    //物流回调
    public String test5() throws ParseException{
        HttpServletRequest request = ServletActionContext.getRequest(); 
        HttpServletResponse response = ServletActionContext.getResponse();
 
        return WWAction.JSON_MESSAGE;
    }
    
    //物流回调
    public String test6() throws ParseException{
        HttpServletRequest request = ServletActionContext.getRequest(); 
 
        return WWAction.JSON_MESSAGE;
    }
    
    public String list() throws ParseException {
        String statis = SystemSetting.getStatic_server_domain();
        try{
            HttpServletRequest request =ThreadContextHolder.getHttpRequest();
            Date now = new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1;

            int page =NumberUtils.toInt(request.getParameter("page"),1) ;
            Page yuemoPage = apiYuemoManager.listPage(page, PAGE_SIZE);
            List<Yuemo> list = (List<Yuemo>)yuemoPage.getResult();
            List<Yuemo> resultlist = new ArrayList();
            //替换图片路径
            for(Yuemo a :list)
            {
               if(a.getImage() != null)
               {
                 a.setImage(a.getImage().replaceAll("fs:",statis)); 
               }
               String overTime = a.getTime();
               d1 = sdf.parse(overTime);
               long timeStemp = d1.getTime();
               long timenow = now.getTime();
               if(timeStemp>timenow)
               {
                   resultlist.add(a);
               }
            }
            this.json = JsonMessageUtil.getMobileListJson(resultlist);
        }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return WWAction.JSON_MESSAGE;
    }  
	
	public String detailYuemo() {
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        String statis = SystemSetting.getStatic_server_domain();
        String ymId = request.getParameter("ymId");
        try{
           List<Map> list = apiYuemoManager.detail(ymId);
           for(Map a :list)
           {
              if(a.get("face") != null)
                a.put("face",((String) a.get("face")).replaceAll("fs:",statis)); 
           }
           this.json = JsonMessageUtil.getMobileListJson(list);
        }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
	    return WWAction.JSON_MESSAGE;
	} 
	
	public String joinYuemo() {
	    Member member = UserConext.getCurrentMember();

        if (member == null) {
            this.json = JsonMessageUtil.expireSession();
            return WWAction.JSON_MESSAGE;
        }
	    HttpServletRequest request = ThreadContextHolder.getHttpRequest();
	    String ymId = request.getParameter("ymId");
	    String memId = request.getParameter("memId");
	    try{
    	    int sta = apiYuemoManager.joinYuemo(ymId,memId);
    	    if(sta == 1)
    	    {
    	      this.showPlainSuccessJson("成功加入");
    	    }else if(sta == 2)
    	    {
    	      this.showPlainErrorJson("报名已经满");
    	    }else if(sta == 0)
    	    {
    	        this.showPlainErrorJson("您已经报名，不能再次报名"); 
    	    }else
    	    {
    	        this.showPlainErrorJson("数据库运行异常");  
    	    }
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
	    return JSON_MESSAGE;
	} 

    public static int DateToInt(String timeString, String format) {  
        
        int time = 0;  
        try {  
            SimpleDateFormat sdf = new SimpleDateFormat(format);  
            Date date = sdf.parse(timeString);
            String strTime = date.getTime() + "";
            strTime = strTime.substring(0, 10);
            time = NumberUtils.toInt(strTime);
   
        }  
        catch (ParseException e) {  
            e.printStackTrace();  
        }  
        return time;  
    }  


   


	
	public List<Yuemo> getYuemoList() {
        return yuemoList;
    }

    public void setYuemoList(List<Yuemo> yuemoList) {
        this.yuemoList = yuemoList;
    }
    
 
    public Yuemo getYuemo() {
     return this.yuemo;
    }
    
    public void setYuemo(Yuemo yuemo) {
     this.yuemo = yuemo;
    }
    
    public Integer[] getId() {
        return id;
       }
       
    public void setId(Integer[] id) {
        this.id = id;
    }
    
    public Integer getYuemoId() {
        return yuemoId;
       }
       
    public void setYuemoId(Integer yuemoId) {
        this.yuemoId = yuemoId;
    }



    
    public String getTitle() {
        return title;
    }



    
    public void setTitle(String title) {
        this.title = title;
    }



    
    public String getContent() {
        return content;
    }



    
    public void setContent(String content) {
        this.content = content;
    }



    
    public String getStime() {
        return stime;
    }



    
    public void setStime(String stime) {
        this.stime = stime;
    }



    
    public ApiYuemoManager getApiYuemoManager() {
        return apiYuemoManager;
    }



    
    public void setApiYuemoManager(ApiYuemoManager apiYuemoManager) {
        this.apiYuemoManager = apiYuemoManager;
    }

    
    public Kuaidi100BackManager getKuaidi100BackManager() {
        return kuaidi100BackManager;
    }

    
    public void setKuaidi100BackManager(Kuaidi100BackManager kuaidi100BackManager) {
        this.kuaidi100BackManager = kuaidi100BackManager;
    }
    
    
	
}
