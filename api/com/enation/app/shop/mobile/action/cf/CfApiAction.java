package com.enation.app.shop.mobile.action.cf;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cf;
import com.enation.app.shop.core.model.CfClick;
import com.enation.app.shop.core.model.CfMessage;
import com.enation.app.shop.core.model.CfRecord;
import com.enation.app.shop.mobile.service.impl.ApiCfManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonMessageUtil;

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/api/mobile")
@Action("cf")
@Results({
	@Result(name="list", type="freemarker",location="/shop/admin/cf/cf_list.html"),
	@Result(name="add_cf", type="freemarker",location="/shop/admin/cf/cf_add.html"),
	@Result(name="detail_cf", type="freemarker",location="/shop/admin/cf/cf_detail.html"),
	@Result(name="edit_cf", type="freemarker",location="/shop/admin/cf/cf_edit.html")
})

@SuppressWarnings({ "rawtypes", "unused", "serial"})
public class CfApiAction extends WWAction {
	private Integer[] id;
	private String title;
	private String content;
	private Cf cf;
	private String stime;
    private Integer cfId;	
	private List<Cf> cfList;
	private ApiCfManager apiCfManager;
	private final int PAGE_SIZE = 20;
	
	public String list() {
	    try{
	        HttpServletRequest request =ThreadContextHolder.getHttpRequest();    
            int page =NumberUtils.toInt(request.getParameter("page"),1) ;
            
            Page list = apiCfManager.list(page,PAGE_SIZE);
            list.setCurrentPageNo(page);
            this.json = JsonMessageUtil.getMobileObjectJson(list);
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
	    return WWAction.JSON_MESSAGE;
	} 
	
	public String detailCf() {
	        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
	        Map paramMap = request.getParameterMap();
	        String cfId = request.getParameter("cfId");
	        String memId = request.getParameter("memId");
	        
	        try{
    	        Map cfdetail = apiCfManager.detail(cfId,memId);
    	        this.json = JsonMessageUtil.getMobileListJson(cfdetail);
	        }catch (RuntimeException e) {
	            this.logger.error("数据库运行异常", e);
	            this.showPlainErrorJson(e.getMessage());
	        }
	        
	        return WWAction.JSON_MESSAGE;
	    } 
	
	 //商品详情接口
	 public String detailGoods() {
         HttpServletRequest request = ThreadContextHolder.getHttpRequest();
         Map paramMap = request.getParameterMap();
         String goodsId = request.getParameter("goodsId");
         
         try{
             Map cfdetail = apiCfManager.detailGoods(goodsId);
             this.json = JsonMessageUtil.getMobileListJson(cfdetail);
         }catch (RuntimeException e) {
             this.logger.error("数据库运行异常", e);
             this.showPlainErrorJson(e.getMessage());
         }
         
         return WWAction.JSON_MESSAGE;
     } 
	 
	 //我的众筹接口
     public String myCf() {
         HttpServletRequest request = ThreadContextHolder.getHttpRequest();
         Map paramMap = request.getParameterMap();
         String memberId = request.getParameter("memberId");  
         try{
             Map myCf = apiCfManager.myCf(memberId);
             this.json = JsonMessageUtil.getMobileListJson(myCf);
         }catch (RuntimeException e) {
             this.logger.error("数据库运行异常", e);
             this.showPlainErrorJson(e.getMessage());
         }
         
         return WWAction.JSON_MESSAGE;
     }
     
     //生成订单接口
     public String createOrder(){
         try{
             HttpServletRequest request = ThreadContextHolder.getHttpRequest();
             Map paramMap = request.getParameterMap();
             String memberId = request.getParameter("memberId"); 
             String cfId = request.getParameter("cfId"); 
             String goodsId = request.getParameter("goodsId");
             String address = request.getParameter("address");
             String orderId = request.getParameter("orderId");
             
             apiCfManager.createOrder(memberId,cfId,goodsId,address,orderId);
             this.json = JsonMessageUtil.getMobileStringJson("value", "成功");
         }catch (RuntimeException e) {
             this.logger.error("数据库运行异常", e);
             this.showPlainErrorJson(e.getMessage());
         }
         
         return WWAction.JSON_MESSAGE;
     }
     
	public String joinCf() {
	    HttpServletRequest request = ThreadContextHolder.getHttpRequest();
	    String cfId = request.getParameter("cfId");
	    String memId = request.getParameter("memId");
	    String message = request.getParameter("message");
	    
	    try{
    	    CfRecord record = new CfRecord();
    	    record.setCf_id(Integer.valueOf(cfId));
            record.setMember_id(memId);
            record.setMessage(message);
            
            Date date=new Date();
            DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time=format.format(date); 
            record.setMessage_time(time);
            
    	    apiCfManager.joinCf(record);
    	    this.json = JsonMessageUtil.getMobileStringJson("value", "成功");
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
	    return WWAction.JSON_MESSAGE;
	} 
	
	/*
	 * 点赞接口
	 */
	public String clickCf(){
	    HttpServletRequest request = ThreadContextHolder.getHttpRequest();
	    String cfId = request.getParameter("cfId");
	    String memId = request.getParameter("memId");
	    try{
    	    CfClick click = new CfClick();
    	    click.setCf_id(Integer.valueOf(cfId));
    	    click.setMember_id(memId);
    	    apiCfManager.click_Cf(click);
    	    this.json = JsonMessageUtil.getMobileSuccessJson("报名成功");
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
	    return WWAction.JSON_MESSAGE;
    }
	
	 /*
     * 留言接口
     */
    public String messageCf(){
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        String cfId = request.getParameter("cfId");
        String memId = request.getParameter("memId");
        String message = request.getParameter("message");
        try{
            CfMessage cfmessage = new CfMessage();
            cfmessage.setCf_id(Integer.valueOf(cfId));
            cfmessage.setMember_id(memId);
            cfmessage.setMessage(message);
            apiCfManager.addMessage(cfmessage);
            this.json = JsonMessageUtil.getMobileSuccessJson("留言成功");

        }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return WWAction.JSON_MESSAGE;
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
	
	public List<Cf> getCfList() {
        return cfList;
    }

    public void setCfList(List<Cf> cfList) {
        this.cfList = cfList;
    }
    
 
    public Cf getCf() {
     return this.cf;
    }
    
    public void setCf(Cf cf) {
     this.cf = cf;
    }
    
    public Integer[] getId() {
        return id;
       }
       
    public void setId(Integer[] id) {
        this.id = id;
    }
    
    public Integer getCfId() {
        return cfId;
       }
       
    public void setCfId(Integer cfId) {
        this.cfId = cfId;
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



    
    public ApiCfManager getApiCfManager() {
        return apiCfManager;
    }



    
    public void setApiCfManager(ApiCfManager apiCfManager) {
        this.apiCfManager = apiCfManager;
    }
    
    
	
}
