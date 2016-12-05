package com.enation.app.shop.core.action.backend;

import java.io.File;
import java.util.List;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Sommelier;
import com.enation.app.shop.core.model.TastingNote;
import com.enation.app.shop.core.service.impl.SommelierManager;
import com.enation.app.shop.core.service.impl.StastingNoteManager;
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
@Action("sommelier")
@Results({
	@Result(name="list", type="freemarker",location="/shop/admin/sommelier/sommelier_list.html"),
	@Result(name="list_tast", type="freemarker",location="/shop/admin/sommelier/list_tasting_note.html"),
	@Result(name="add_sommelier", type="freemarker",location="/shop/admin/sommelier/sommelier_add.html"),
	@Result(name="add_tasting_note", type="freemarker",location="/shop/admin/sommelier/tasting_note_add.html"),
	@Result(name="detail_sommelier", type="freemarker",location="/shop/admin/sommelier/sommelier_detail.html"),
	@Result(name="detail_tasting_note", type="freemarker",location="/shop/admin/sommelier/tasting_note_detail.html"),
	@Result(name="edit_tasting_note", type="freemarker",location="/shop/admin/sommelier/tasting_note_edit.html"),
	@Result(name="edit_sommelier", type="freemarker",location="/shop/admin/sommelier/sommelier_edit.html")
})

@SuppressWarnings({ "rawtypes", "serial","static-access" })
public class SommelierAction extends WWAction {

    private SommelierManager sommelierManager;
    private StastingNoteManager stastingNoteManager;
    private TastingNote tastingNote;
	private Sommelier sommelier;
	private Integer sommelierId;
	private Integer noteId;
	private Integer[] id;
	private List<Sommelier> sommelierList;
	private AdminUser user;
	private File image;
    private String imageFileName;
    private String image_src;

	public String list() {
	    return "list";
	}  
	
	public String list_sommelier() {
        WebSessionContext sessonContext = ThreadContextHolder.getSessionContext();
        user = (AdminUser) sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
        try
        {
           this.webpage = sommelierManager.list(this.getSort(), this.getPage(), this.getPageSize());
        }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        this.showGridJson(webpage);
        return JSON_MESSAGE;
    }
	
	public String list_tast() {
	    user = UserConext.getCurrentAdminUser();
	    return "list_tast";
	}  
	
	/**
	 * 当前用户的酒评论列表
	 * @param 
	 * @return  result
	 * result 1.操作成功.0.操作失败
	*/
	public String list_tasting_note() {
	    user = UserConext.getCurrentAdminUser();
        //user = (AdminUser) sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
	    try{
           this.webpage = sommelierManager.list_tast_note(this.getSort(), this.getPage(), this.getPageSize(),user.getUserid());
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        this.showGridJson(webpage);
        return JSON_MESSAGE;
	}
	
	
   public String list_order_type() {
           return "list_sommelier_order_type";
   } 
   
   
   /**
    * 品酒师预约服务列表
    * @param 
    * @return  result
    * result 1.操作成功.0.操作失败
   */
   public String list_sommelier_order_type() {
       user = UserConext.getCurrentAdminUser();
       try{
          this.webpage = sommelierManager.list_tast_note(this.getSort(), this.getPage(), this.getPageSize(),user.getUserid());
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
       }
       this.showGridJson(webpage);
       return JSON_MESSAGE;
   }
	
    /**
     * 添加酒评师页面
     * @param 
     * @return  result
     * result 1.操作成功.0.操作失败
    */
	public String add_sommelier() {
	    return "add_sommelier";
	}
	
    /**
     * 添加酒评页面
     * @param 
     * @return  result
     * result 1.操作成功.0.操作失败
    */
    public String add_tasting_note() {
        return "add_tasting_note";
    }
	
    /**
     * 修改酒评页面
     * @param 
     * @return  result
     * result 1.操作成功.0.操作失败
    */
	public String edit_sommelier() {
	    try{
    	    sommelier = this.sommelierManager.get(sommelierId);
    	    if(sommelier.getIntroduce() == null)
    	        sommelier.setIntroduce("");
            String imageurl = sommelier.getImg_url();
            if(imageurl == null)
            {
                sommelier.setImg_url(imageurl);
            }else
            {
               String statis = SystemSetting.getStatic_server_domain();
               imageurl = imageurl.replaceAll("fs:",statis);  
               sommelier.setImg_url(imageurl);
            }    	    
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
	    return "edit_sommelier";
    }
	
    /**
     * 修改酒评
     * @param 
     * @return  result
     * result 1.操作成功.0.操作失败
    */
   public String edit_tasting_note() {
       WebSessionContext sessonContext = ThreadContextHolder.getSessionContext();
       user = (AdminUser) sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
       try{
       tastingNote = this.stastingNoteManager.get(noteId);
       tastingNote.setUserid(user.getUserid().toString());
        if(tastingNote.getIntroduce() == null)
            tastingNote.setIntroduce("");
        
        String imageurl = tastingNote.getImage();
        if(imageurl == null)
        {
            tastingNote.setImage("");
        }else
        {
           String statis = SystemSetting.getStatic_server_domain();
           imageurl = imageurl.replaceAll("fs:",statis);  
           tastingNote.setImage(imageurl);
        }
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
       }
        return "edit_tasting_note";
    }
	
	

   /**
   * 酒评师详情
     * @param 
     * @return  result
     * result 1.操作成功.0.操作失败
    */
    public String detail_sommelier() {
        try{
            sommelier = this.sommelierManager.get(sommelierId);
            if(sommelier.getIntroduce() == null)
                sommelier.setIntroduce("");
        }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return "detail_sommelier";
	}
	
    
    /**
     * 酒评详情
     * @param 
     * @return  result
     * result 1.操作成功.0.操作失败
    */
   public String detail_tasting_note() {
       try{
           tastingNote = this.stastingNoteManager.get(noteId);
           if(tastingNote.getIntroduce() == null)
               tastingNote.setIntroduce("");
           String imageurl = tastingNote.getImage();
           if(imageurl == null)
           {
               tastingNote.setImage("");
           }else
           {
              String statis = SystemSetting.getStatic_server_domain();
              imageurl = imageurl.replaceAll("fs:",statis);  
              tastingNote.setImage(imageurl);
           }           
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
       }
       return "detail_tasting_note";
   }
	
	
	 /**
     * 添加酒评师
     * @param 
     * @return  result
     * result 1.操作成功.0.操作失败
    */
    public String saveAddsommelier() {
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
                String imgPath= uploadImage(image);
                sommelier.setImg_url(imgPath);
            }
            int result = sommelierManager.add(sommelier);
            if(result == 1)
            {
              this.showSuccessJson("添加成功");
            }else
            {
                this.showErrorJson("此用户已经存在");  
            }
        }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return JSON_MESSAGE;
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
    
    
    /**
    * 添加酒评语
     * @param 
     * @return  result
     * result 1.操作成功.0.操作失败
    */
   public String saveAddTastingNote() {
       WebSessionContext sessonContext = ThreadContextHolder.getSessionContext();
       user = (AdminUser) sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
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
               //String imgPath= uploadImage(image);
               //sommelier.setImg_url(imgPath);
           }

           Sommelier s = sommelierManager.getByUserId(user.getUserid());
           tastingNote.setUserid(user.getUserid().toString());
           tastingNote.setSommelierid(String.valueOf(s.getId()));
           
           String imgPath = uploadImage(image);
           tastingNote.setImage(imgPath);
           sommelierManager.addTastingNode(tastingNote);
             
           this.showSuccessJson("添加成功");
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
       }
       return JSON_MESSAGE;
   }    
   
   
    /**
    * 修改酒评师
     * @param 
     * @return  result
     * result 1.操作成功.0.操作失败
    */
   public String saveEditSommelier() {
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
               String imgPath= uploadImage(image);
               sommelier.setImg_url(imgPath);
           }
           sommelierManager.edit(sommelier);
           this.showSuccessJson("酒评师修改成功");
       }catch (Exception e) {
           this.showErrorJson("非法参数");
       }
       return JSON_MESSAGE;
   }
   
   /**
   * 修改酒评语
     * @param 
     * @return  result
     * result 1.操作成功.0.操作失败
    */
  public String saveEditTastingNote() {
      try{
          if(image != null)
          {
              String imgPath = uploadImage(image);
              tastingNote.setImage(imgPath);
          }
          stastingNoteManager.edit(tastingNote);
          this.showSuccessJson("酒评修改成功");
      }catch (Exception e) {
          this.showErrorJson("非法参数");
      }
      return JSON_MESSAGE;
  }
   
   /**
    * 删除酒评师
    * @param 
    * @return  result
    * result 1.操作成功.0.操作失败
    */
   public String deleteTastingNote() {
       try {
           this.stastingNoteManager.delete(id);
           this.showSuccessJson("删除成功");
       } catch (RuntimeException e) {
           this.showErrorJson("删除失败");
       }
       return this.JSON_MESSAGE;
   }
   
   /**
    * 删除酒评语
    * @param 
    * @return  result
    * result 1.操作成功.0.操作失败
    */
   public String deletesommelier() {
       try {
           this.sommelierManager.delete(id);
           this.showSuccessJson("删除成功");
       } catch (RuntimeException e) {
           this.showErrorJson("删除失败");
       }
       return this.JSON_MESSAGE;
   }
    
   public SommelierManager getSommelierManager() {
       return sommelierManager;
   }

   
   public void setSommelierManager(SommelierManager sommelierManager) {
       this.sommelierManager = sommelierManager;
   }

   
   public Sommelier getSommelier() {
       return sommelier;
   }

   
   public void setSommelier(Sommelier sommelier) {
       this.sommelier = sommelier;
   }

   
   public Integer getSommelierId() {
       return sommelierId;
   }

   
   public void setSommelierId(Integer sommelierId) {
       this.sommelierId = sommelierId;
   }

   
   public Integer[] getId() {
       return id;
   }

   
   public void setId(Integer[] id) {
       this.id = id;
   }

   
   public List<Sommelier> getSommelierList() {
       return sommelierList;
   }

   
   public void setSommelierList(List<Sommelier> sommelierList) {
       this.sommelierList = sommelierList;
   }


    public TastingNote getTastingNote() {
        return tastingNote;
    }
    
    
    public void setTastingNote(TastingNote tastingNote) {
        this.tastingNote = tastingNote;
    }
    
    
    public AdminUser getUser() {
        return user;
    }
    
    
    public void setUser(AdminUser user) {
        this.user = user;
    }    
    	
    public StastingNoteManager getStastingNoteManager() {
        return stastingNoteManager;
    }
    
    
    public void setStastingNoteManager(StastingNoteManager stastingNoteManager) {
        this.stastingNoteManager = stastingNoteManager;
    }
    
    
    public Integer getNoteId() {
        return noteId;
    }
    
    
    public void setNoteId(Integer noteId) {
        this.noteId = noteId;
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

    
    public String getImage_src() {
        return image_src;
    }

    
    public void setImage_src(String image_src) {
        this.image_src = image_src;
    }
    
}
