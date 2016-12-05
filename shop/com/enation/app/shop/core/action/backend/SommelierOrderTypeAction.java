package com.enation.app.shop.core.action.backend;

import java.io.File;
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
import com.enation.app.shop.core.service.impl.SommelierOrderTypeManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.context.webcontext.WebSessionContext;

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("sommelierOrderType")
@Results({
	@Result(name="list", type="freemarker",location="/shop/admin/sommelier/sommelier_order_type_list.html"),
    @Result(name="add_sommelier_type", type="freemarker",location="/shop/admin/sommelier/sommelier_order_type_add.html"),
	@Result(name="edit_sommelier_type", type="freemarker",location="/shop/admin/sommelier/sommelier_order_type_edit.html"),
	@Result(name="edit_sommelier_my_type", type="freemarker",location="/shop/admin/sommelier/sommelier_my_type_edit.html"),
	@Result(name="detail_sommelier_type", type="freemarker",location="/shop/admin/sommelier/sommelier_order_type_detail.html")
})

@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })

public class SommelierOrderTypeAction extends WWAction {

    private SommelierOrderTypeManager sommelierOrderTypeManager;
    private SommelierManager sommelierManager;
    private SommelierOrderType sommelierOrderType;
    private SommelierMyType sommelierMyType;
    private Integer typeId;
    private Integer[] id;
    private AdminUser user;
    private File image;
    private String imageFileName;

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
	
   public String list_sommelier_order_type() {
       WebSessionContext sessonContext = ThreadContextHolder.getSessionContext();
       user = (AdminUser) sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
       String statis = SystemSetting.getStatic_server_domain(); 
        try
        {
            webpage = sommelierOrderTypeManager.getOrderTypeList(this.getSort(), this.getPage(), this.getPageSize());  
            List<SommelierOrderType> list = (List<SommelierOrderType>)webpage.getResult();

            //替换图片路径
            for(SommelierOrderType a :list)
            {
               if(a.getOrder_type_image() != null)
                 a.setOrder_type_image(a.getOrder_type_image().replaceAll("fs:",statis)); 
            }
        }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        this.showGridJson(webpage);
        return JSON_MESSAGE;
   }
   
   public String list_sommelier_my_type() {
       WebSessionContext sessonContext = ThreadContextHolder.getSessionContext();
       user = (AdminUser) sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
       Long uid = user.getUserid();
       Integer sid = 0;
       Sommelier sommelier = sommelierManager.getUserById(uid);
       sid = sommelier.getId();
       try
       {
           this.webpage = sommelierOrderTypeManager.getMyTypeList(this.getSort(), this.getPage(), this.getPageSize(),sid);
           List<Map> list = (List<Map>)webpage.getResult();
       
           for(Map a :list)
           {
              Integer status = (Integer) a.get("status");
              if(status == null){
                  a.put("status","未提供");
              }else if(status.intValue() == 0)
              {
                  a.put("status","未提供");  
              }else
              {
                  a.put("status","已提供");  
              }    
           }
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
       }
       this.showGridJson(webpage);
       return JSON_MESSAGE;
  }
   
   /**
    * 删除服务类型
    * @param 
    * @return  result
    * result 1.操作成功.0.操作失败
    */
   public String deleteSommelierType() {
       try {
           this.sommelierOrderTypeManager.deleteType(id);
           this.showSuccessJson("删除成功");
       } catch (RuntimeException e) {
           this.showErrorJson("删除失败");
       }
       return this.JSON_MESSAGE;
   }
   
   public String edit_order_my_type() { 
       WebSessionContext sessonContext = ThreadContextHolder.getSessionContext();
       user = (AdminUser) sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
       Long uid = user.getUserid();
       Integer sid = 0;
       Sommelier sommelier = sommelierManager.getUserById(uid);
       sid = sommelier.getId();
       try{
           sommelierMyType = this.sommelierOrderTypeManager.getMyType(typeId, sid);
/*           Integer smid = sommelierMyType.getSommelier_id();
           if(smid == null)
           {
               sommelierMyType.setStatus(0); 
           }else
           {
               sommelierMyType.setStatus(1);  
           }*/
           Integer status = sommelierMyType.getStatus();
           if(status == null)
           {
               sommelierMyType.setStatus(0); 
           }
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
       }
       return "edit_sommelier_my_type";
   }
   
   public String edit_order_type() {
       WebSessionContext sessonContext = ThreadContextHolder.getSessionContext();
       user = (AdminUser) sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);

       try{
           sommelierOrderType = this.sommelierOrderTypeManager.getOrderType(typeId); 
           String imageurl = sommelierOrderType.getOrder_type_image();
           if(imageurl == null)
           {
               sommelierOrderType.setOrder_type_image("");
           }else
           {
              String statis = SystemSetting.getStatic_server_domain();
              imageurl = imageurl.replaceAll("fs:",statis);  
              sommelierOrderType.setOrder_type_image(imageurl);
           }
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
       }
       return "edit_sommelier_type";
   }
   
   public String edit_my_type() {
       WebSessionContext sessonContext = ThreadContextHolder.getSessionContext();
       user = (AdminUser) sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
       Long uid = user.getUserid();
       Integer sid = 0;
       Sommelier sommelier = sommelierManager.getByUserId(uid);
       sid = sommelier.getId();
       try{
           sommelierMyType = this.sommelierOrderTypeManager.getMyType(typeId,sid);         
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
       }
       return "edit_sommelier_my_type";
   }
   
   public String saveAddsommelierType(){
       try{
           if(image!=null){
               //判断文件类型
               String allowTYpe = "gif,jpg,bmp,png";
               if (!imageFileName.trim().equals("") && imageFileName.length() > 0) {
                   String ex = imageFileName.substring(imageFileName.lastIndexOf(".") + 1, imageFileName.length());
                   if(allowTYpe.toString().indexOf(ex.toLowerCase()) < 0){
                       throw new RuntimeException("对不起,只能上传gif,jpg,bmp,png格式的图片！");
                   }
               }
               
               //判断文件大小
               if(image.length() > 2000 * 1024){
                   throw new RuntimeException("图片不能大于2MB！");
               }
           }
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
           return JSON_MESSAGE;
       }
       
       String imgPath= uploadImage(image);
       sommelierOrderType.setOrder_type_image(imgPath);
       
       sommelierOrderTypeManager.addOrderType(sommelierOrderType);
       this.showSuccessJson("添加成功");
       return JSON_MESSAGE;
   }
   
   
   public String saveEditSommelierType(){
       try{
           if(image!=null){
               //判断文件类型
               String allowTYpe = "gif,jpg,bmp,png";
               if (!imageFileName.trim().equals("") && imageFileName.length() > 0) {
                   String ex = imageFileName.substring(imageFileName.lastIndexOf(".") + 1, imageFileName.length());
                   if(allowTYpe.toString().indexOf(ex.toLowerCase()) < 0){
                       throw new RuntimeException("对不起,只能上传gif,jpg,bmp,png格式的图片！");
                   }
               }
               
               //判断文件大小
               if(image.length() > 2000 * 1024){
                   throw new RuntimeException("图片不能大于2MB！");
               }
           }
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
           return JSON_MESSAGE;
       }
       
       String imgPath= uploadImage(image);
       sommelierOrderType.setOrder_type_image(imgPath);
       
       Map where = new HashMap();
       where.put("id", sommelierOrderType.getId());
       sommelierOrderTypeManager.modifyOrderType(sommelierOrderType, where);
       this.showSuccessJson("修改成功");
       return JSON_MESSAGE;
   }
   
   public String saveEditSommelierMyType(){
       WebSessionContext sessonContext = ThreadContextHolder.getSessionContext();
       user = (AdminUser) sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
       Long uid = user.getUserid();
       Integer sid = 0;
       Sommelier sommelier = sommelierManager.getUserById(uid);
       sid = sommelier.getId();
       sommelierOrderTypeManager.modifyMyType(sommelierMyType,sid);
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
 


public File getImage() {
    return image;
}


public void setImage(File image) {
    this.image = image;
}


public String getImageFileName() {
    return imageFileName;
}


public void setImageFileName(String imageFileName) {
    this.imageFileName = imageFileName;
}

/**
* 上传图片
* @param 
*/
public String uploadImage(File image) {
    String imgPath = "";
    try{
        if(image!=null){
            //判断文件类型
            String allowTYpe = "gif,jpg,bmp,png";
            if (!imageFileName.trim().equals("") && imageFileName.length() > 0) {
                String ex = imageFileName.substring(imageFileName.lastIndexOf(".") + 1, imageFileName.length());
                if(allowTYpe.toString().indexOf(ex.toLowerCase()) < 0){
                    throw new RuntimeException("对不起,只能上传gif,jpg,bmp,png格式的图片！");
                }
            }
            
            //判断文件大小
            if(image.length() > 2000 * 1024){
                throw new RuntimeException("图片不能大于2MB！");
            }
            imgPath = UploadUtil.upload(image, imageFileName, "sommelier");
        }
    }catch (RuntimeException e) {
        this.logger.error("数据库运行异常", e);
        this.showPlainErrorJson(e.getMessage());
    }
    return imgPath;
}

}
