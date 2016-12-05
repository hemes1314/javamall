package com.enation.app.shop.mobile.action.sommelier;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Auction;
import com.enation.app.shop.core.model.Sommelier;
import com.enation.app.shop.core.model.SommelierOrder;
import com.enation.app.shop.core.service.impl.SommelierOrderTypeManager;
import com.enation.app.shop.mobile.service.impl.ApiSommelierManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.resource.model.AdminUser;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.sms.SmsSender;
import com.enation.framework.util.JsonMessageUtil;

@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/api/mobile")
@Action("sommelier")
@Results({
	@Result(name="list", type="freemarker",location="/shop/admin/yuemo/yuemo_list.html"),
	@Result(name="add_ym", type="freemarker",location="/shop/admin/yuemo/yuemo_add.html"),
	@Result(name="detail_ym", type="freemarker",location="/shop/admin/yuemo/yuemo_detail.html"),
	@Result(name="edit_ym", type="freemarker",location="/shop/admin/yuemo/yuemo_edit.html")
})

@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })

public class SommelierApiAction extends WWAction {
	private Integer[] id;
	private SommelierOrderTypeManager sommelierOrderTypeManager;
	private ApiSommelierManager apiSommelierManager;
	private AdminUser user;
	private Sommelier sommelier;
	private final int PAGE_SIZE = 20;
	
	//酒评师列表
	public String sommelierList() {
	    try{
	        String statis = SystemSetting.getStatic_server_domain(); 
            
            HttpServletRequest request =ThreadContextHolder.getHttpRequest();    
            int page =NumberUtils.toInt(request.getParameter("page"),1);
            
            Page sommelierPage = apiSommelierManager.listPage(page, PAGE_SIZE);   
            List<Sommelier> list = (List<Sommelier>)sommelierPage.getResult();

            //替换图片路径
            for(Sommelier a :list)
            {
               if(a.getImg_url() != null)
                 a.setImg_url(a.getImg_url().replaceAll("fs:",statis)); 
            }
                    
            this.json = JsonMessageUtil.getMobileListJson(list);
            //this.json = this.json.replaceAll("</?[^<]+>", "");
            //this.json = this.json.replaceAll("\\s*|\t|\r|\n", "");
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return JSON_MESSAGE;
	} 
	
	public String hotSommelierList() {
	    try{
	        this.setOrder("GOOD_COMMENT desc");
	        List  webpage = apiSommelierManager.sommelierList();
	        //this.showGridJson(webpage);
	        this.json = JsonMessageUtil.getMobileListJson(webpage);
	       }catch (RuntimeException e) {
	            this.logger.error("数据库运行异常", e);
	            this.showPlainErrorJson(e.getMessage());
	       }
	       return JSON_MESSAGE;
	} 
	
	//酒评师详情
	public String detailSommelier() {
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        String sommelierId = request.getParameter("sommelierId");
        try{
            this.webpage = apiSommelierManager.detail(this.getSort(), this.getPage(), this.getPageSize(),sommelierId);
            this.json = JsonMessageUtil.getMobileObjectJson(this.webpage);
        }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        //this.showGridJson(webpage);
        return WWAction.JSON_MESSAGE;
	} 
	
   public String recommendWinesList() {
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        String sommelierId = request.getParameter("sommelierId");
        try{
            this.webpage = apiSommelierManager.recommendWinesList(this.getSort(), this.getPage(), this.getPageSize(),sommelierId);
            this.json = JsonMessageUtil.getMobileObjectJson(this.webpage);
        }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return WWAction.JSON_MESSAGE;
    }
   
   //酒评师服务类型
   public String getSommelierTypeList() {
       HttpServletRequest request = ThreadContextHolder.getHttpRequest();
       int page =NumberUtils.toInt(request.getParameter("page"),1);
       try{
           this.webpage = sommelierOrderTypeManager.getApiOrderTypeList("id",page,PAGE_SIZE);
           List list = (List)webpage.getResult();
           this.json = JsonMessageUtil.getMobileListJson(list);
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
       }
       return WWAction.JSON_MESSAGE;
   }
   
   //can service 酒评师
   public String getOrderSommelierList() {
       HttpServletRequest request = ThreadContextHolder.getHttpRequest();
       int page =NumberUtils.toInt(request.getParameter("page"),1);
       String typeId = request.getParameter("typeId");
       String stime = request.getParameter("stime");
       String etime = request.getParameter("etime");
       try{
           this.webpage = sommelierOrderTypeManager.getApiOrderSommelierList(typeId,"id",page,PAGE_SIZE);
           List list = (List)webpage.getResult();
           this.json = JsonMessageUtil.getMobileListJson(list);
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
       }
       return WWAction.JSON_MESSAGE;
   }
   
   //order sommelier interface
   public String postOrderSommelier() {
       HttpServletRequest request = ThreadContextHolder.getHttpRequest();
       String sommelierId = request.getParameter("sommelierId");
       String memid = request.getParameter("memberId");
       String stime = request.getParameter("stime");
       String etime = request.getParameter("etime");
       String name = request.getParameter("name");
       String mobile = request.getParameter("mobile");
       String address = request.getParameter("address");
       String remark = request.getParameter("remark");
       String area = request.getParameter("area");
       String typeId = request.getParameter("typeId");
       String zipcode = request.getParameter("zipcode");
       
       SommelierOrder order = new SommelierOrder();
       if(address != null){
       order.setAddress(address);
       }
       if(area != null){
       order.setArea(area);
       }
       if(etime != null){
       order.setEtime(Long.valueOf(etime));
       }
       if(memid != null){
       order.setMemid(Integer.valueOf(memid));
       }
       if(mobile != null){
       order.setMobile(mobile);
       }
       if(name != null){
       order.setName(name);
       }
       if(remark != null){
       order.setRemark(remark);
       }
       if(sommelierId != null){
       order.setSommelier_id(Integer.valueOf(sommelierId));
       }
       order.setStatus(1);
       if(stime != null){
       order.setStime(Long.valueOf(stime));
       }
       if(typeId != null){
       order.setTypeid(Integer.valueOf(typeId));
       }
       if(zipcode != null){
       order.setZipcode(zipcode);
       }
       try{
           apiSommelierManager.apiPostOrderSommelier(order);
           SmsSender.sendSms(mobile, "尊敬的品酒师，您有新的侍酒服务订单待确认");
           this.showPlainSuccessJson("成功");
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
       } catch(Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
       return WWAction.JSON_MESSAGE;
   }
   
   //获取订单接口（普通会员，品酒师）
   public String getOrderStatusList(){
       HttpServletRequest request = ThreadContextHolder.getHttpRequest();
       String memId = request.getParameter("memberId");
       HashMap orderStatusList = (HashMap) apiSommelierManager.apiGetOrderStatusList(memId);
       this.json = JsonMessageUtil.getMobileListJson(orderStatusList);
       return WWAction.JSON_MESSAGE; 
   }
   
   //品酒师确认订单接口
   public String confirmOrder(){
       HttpServletRequest request = ThreadContextHolder.getHttpRequest();
       String orderId = request.getParameter("orderId");
       try{
           boolean confirm =  apiSommelierManager.apiConfirmOrder(orderId);
           if(confirm)
           {
             this.showPlainSuccessJson("成功");
           }else
           {
             this.showPlainErrorJson("失败"); 
           }
       }catch (RuntimeException e) {
           this.showPlainErrorJson("失败"); 
       }
       return WWAction.JSON_MESSAGE;   
   }
   
   //付款接口
   public String postOrderPay(){
       HttpServletRequest request = ThreadContextHolder.getHttpRequest();
       String memId = request.getParameter("memberId");
       String orderId = request.getParameter("orderId");
       boolean pay = apiSommelierManager.apiOrderPay(Integer.valueOf(memId),Integer.valueOf(orderId));
       if(pay)
       {
         this.showPlainSuccessJson("成功");
       }else
       {
         this.showPlainErrorJson("失败"); 
       }
       return WWAction.JSON_MESSAGE;  
   }
   
   //取消订单接口
   public String postOrderCancel(){
       HttpServletRequest request = ThreadContextHolder.getHttpRequest();
       String memId = request.getParameter("memberId");
       String orderId = request.getParameter("orderId");
       boolean cancel = apiSommelierManager.apiOrderCancel(Integer.valueOf(orderId));
       if(cancel)
       {
         this.showPlainSuccessJson("成功");
       }else
       {
         this.showPlainErrorJson("失败"); 
       }
       return WWAction.JSON_MESSAGE; 
   }

   
   public String addAppraise() {
       HttpServletRequest request = ThreadContextHolder.getHttpRequest();
       Integer sommelierId = Integer.valueOf(request.getParameter("sommelierId"));
       String comment = request.getParameter("comment");
       Integer userid = Integer.valueOf(request.getParameter("userid"));
       try{
           int ifcom = apiSommelierManager.addComment(sommelier,sommelierId,comment,userid);
           if(ifcom == 0)
           {
              this.showPlainErrorJson("已经评价过了"); 
              return WWAction.JSON_MESSAGE;
           }
           this.showPlainSuccessJson("评价成功");
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
       }
       return WWAction.JSON_MESSAGE;
   } 
	
    
    public Integer[] getId() {
        return id;
       }
       
    public void setId(Integer[] id) {
        this.id = id;
    }

    public void setApiSommelierManager(ApiSommelierManager apiSommelierManager) {
        this.apiSommelierManager = apiSommelierManager;
    }

    
    public AdminUser getUser() {
        return user;
    }

    
    public void setUser(AdminUser user) {
        this.user = user;
    }

    
    public Sommelier getSommelier() {
        return sommelier;
    }

    
    public void setSommelier(Sommelier sommelier) {
        this.sommelier = sommelier;
    }

    
    public ApiSommelierManager getApiSommelierManager() {
        return apiSommelierManager;
    }

     
    public SommelierOrderTypeManager getSommelierOrderTypeManager() {
        return sommelierOrderTypeManager;
    }

    
    public void setSommelierOrderTypeManager(SommelierOrderTypeManager sommelierOrderTypeManager) {
        this.sommelierOrderTypeManager = sommelierOrderTypeManager;
    }

    public int getPAGE_SIZE() {
        return PAGE_SIZE;
    }
    
    
}
