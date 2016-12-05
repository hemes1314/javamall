package com.enation.app.shop.core.action.backend;

import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.enation.app.shop.core.model.Sommelier;
import com.enation.app.shop.core.model.SommelierMyType;
import com.enation.app.shop.core.model.SommelierOrderType;
import com.enation.app.shop.core.service.impl.SommelierManager;
import com.enation.app.shop.core.service.impl.SommelierOrderManager;
import com.enation.app.shop.core.service.impl.SommelierOrderTypeManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.context.webcontext.WebSessionContext;

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("sommelierOrder")
@Results({
	@Result(name="list", type="freemarker",location="/shop/admin/sommelier/sommelier_my_order_list.html"),
	@Result(name="edit_sommelier_type", type="freemarker",location="/shop/admin/sommelier/sommelier_order_type_edit.html")	
})

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class SommelierOrderAction extends WWAction {

    private SommelierOrderTypeManager sommelierOrderTypeManager;
    private SommelierManager sommelierManager;
    private SommelierOrderManager sommelierOrderManager;
    private SommelierOrderType sommelierOrderType;
    private SommelierMyType sommelierMyType;
    private Integer typeId;
    private Integer[] id;
    private AdminUser user;

	public String list() {
       WebSessionContext sessonContext = ThreadContextHolder.getSessionContext();
       user = (AdminUser) sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
	    return "list";
	}  
	
   public String add_sommelier_type(){
       return "add_sommelier_type";
   }
   
   
   public String detail_sommelier_type(){
       return "detail_sommelier_type";
   }
	
   public String list_sommelier_my_order() {
       WebSessionContext sessonContext = ThreadContextHolder.getSessionContext();
       user = (AdminUser) sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
        try
        {   
            if(user.getUsername().equals("admin"))
            {
               webpage = sommelierOrderManager.getAllOrderList(this.getSort(), this.getPage(), this.getPageSize());
               List<Map> list = (List<Map>)webpage.getResult();
               for(Map a :list)
               {
                  Integer status = (Integer) a.get("status");
                  if(status == 1){
                      a.put("status","未确认");
                  }else if(status == 2)
                  {
                      a.put("status","已确认");  
                  }else if(status == 3)
                  {
                      a.put("status","已付款");  
                  }else if(status == 4)
                  {
                      a.put("status","已取消");  
                  }
                  
                  Long time1 = (Long) a.get("stime");
                  Long time2 = (Long) a.get("etime");
                  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                  String date1 = sdf.format(new Date(time1*1000));
                  String date2 = sdf.format(new Date(time2*1000));

                  a.put("stime",date1);
                  a.put("etime",date2);
               }
            }else
            {
            	Long uid = user.getUserid();
                Sommelier sommelier = sommelierManager.getUserById(uid);
                Integer sid = sommelier.getId();
                webpage = sommelierOrderManager.getMyOrderList(sid, this.getPage(), this.getPageSize()); 
                List<Map> list = (List<Map>)webpage.getResult();
                for(Map a :list)
                {
                   Integer status = (Integer) a.get("status");
                   if(status == 1){
                       a.put("status","未确认");
                   }else if(status == 2)
                   {
                       a.put("status","已确认");  
                   }else if(status == 3)
                   {
                       a.put("status","已付款");  
                   }else if(status == 4)
                   {
                       a.put("status","已取消");  
                   }
                   
                   Long time1 = (Long) a.get("stime");
                   Long time2 = (Long) a.get("etime");
                   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                   String date1 = sdf.format(new Date(time1*1000));
                   String date2 = sdf.format(new Date(time2*1000));
 
                   a.put("stime",date1);
                   a.put("etime",date2);
                }
            }
        }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        this.showGridJson(webpage);
        return JSON_MESSAGE;
   }
   
/*   public String list_sommelier_my_type() {
       WebSessionContext sessonContext = ThreadContextHolder.getSessionContext();
       user = (AdminUser) sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
       Integer uid = user.getUserid();
       Integer sid = 0;
       Sommelier sommelier = sommelierManager.getByUserId(uid);
       sid = sommelier.getId();
       try
       {
           this.webpage = sommelierOrderTypeManager.getMyTypeList(this.getSort(), this.getPage(), this.getPageSize(),sid);
           List<Map> list = (List<Map>)webpage.getResult();
       
           for(Map a :list)
           {
              if(a.get("sommelier_id") != null){
                  int smid = (int) a.get("sommelier_id");
                  if(smid == 0)
                  {
                    a.put("status","未提供");
                  }else
                  {
                    a.put("status","已提供");
                  }
              }
                 
           }
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
       }
       this.showGridJson(webpage);
       return JSON_MESSAGE;
  }*/
   
   /**
    * 删除服务类型
    * @param 
    * @return  result
    * result 1.操作成功.0.操作失败
    */
/*   public String deleteSommelierType() {
       try {
           this.sommelierOrderTypeManager.deleteType(id);
           this.showSuccessJson("删除成功");
       } catch (RuntimeException e) {
           this.showErrorJson("删除失败");
       }
       return this.JSON_MESSAGE;
   }*/
   
   public String edit_order_type() {
       try{
           sommelierOrderType = this.sommelierOrderTypeManager.getOrderType(typeId);         
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
       }
       return "edit_sommelier_type";
   }
   

   public String saveAddsommelierType(){
       sommelierOrderTypeManager.addOrderType(sommelierOrderType);
       this.showSuccessJson("添加成功");
       return JSON_MESSAGE;
   }
   
   
   public String saveEditSommelierType(){
       Map where = new HashMap();
       where.put("id", sommelierOrderType.getId());
       sommelierOrderTypeManager.modifyOrderType(sommelierOrderType, where);
       this.showSuccessJson("修改成功");
       return JSON_MESSAGE;
   }
   

public SommelierOrderTypeManager getSommelierOrderTypeManager() {
    return sommelierOrderTypeManager;
}


public void setSommelierOrderTypeManager(SommelierOrderTypeManager sommelierOrderTypeManager) {
    this.sommelierOrderTypeManager = sommelierOrderTypeManager;
}


public SommelierOrderType getSommelierOrderType() {
    return sommelierOrderType;
}


public void setSommelierOrderType(SommelierOrderType sommelierOrderType) {
    this.sommelierOrderType = sommelierOrderType;
}


public Integer getTypeId() {
    return typeId;
}


public void setTypeId(Integer typeId) {
    this.typeId = typeId;
}


public Integer[] getId() {
    return id;
}


public void setId(Integer[] id) {
    this.id = id;
}


public AdminUser getUser() {
    return user;
}


public void setUser(AdminUser user) {
    this.user = user;
}

public SommelierMyType getSommelierMyType() {
    return sommelierMyType;
}


public void setSommelierMyType(SommelierMyType sommelierMyType) {
    this.sommelierMyType = sommelierMyType;
}

public SommelierManager getSommelierManager() {
    return sommelierManager;
}


public void setSommelierManager(SommelierManager sommelierManager) {
    this.sommelierManager = sommelierManager;
}


public SommelierOrderManager getSommelierOrderManager() {
    return sommelierOrderManager;
}


public void setSommelierOrderManager(SommelierOrderManager sommelierOrderManager) {
    this.sommelierOrderManager = sommelierOrderManager;
}
 

}
