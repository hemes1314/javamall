package com.enation.app.shop.core.action.backend;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.dom4j.Document;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.app.shop.core.service.impl.OrderFlowManager;
import com.enation.app.shop.core.service.impl.YuemoManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.processor.core.RemoteRequest;
import com.enation.eop.processor.core.Request;
import com.enation.eop.processor.core.Response;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonUtil;
import com.gomecellar.workflow.utils.Constants;
import com.gomecellar.workflow.utils.HttpClientUtils;

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("yuemo")
@Results({
	@Result(name="list", type="freemarker",location="/shop/admin/yuemo/yuemo_list.html"),
	@Result(name="add_ym", type="freemarker",location="/shop/admin/yuemo/yuemo_add.html"),
	@Result(name="detail_ym", type="freemarker",location="/shop/admin/yuemo/yuemo_detail.html"),
	@Result(name="edit_ym", type="freemarker",location="/shop/admin/yuemo/yuemo_edit.html"),
	@Result(name="uploadfile", type="freemarker",location="/shop/admin/yuemo/upload.html")
})

@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })

public class YuemoAction extends WWAction {

	YuemoManager yuemoManager;
	private Integer[] id;
	private String title;
	private String content;
	private Yuemo yuemo;
	private String stime;
    private Integer yuemoId;	
	private List<Yuemo> yuemoList;
	private List<Member> joinList;
	private String filePath; 
	private Document document; 
    private File image;
    private String imageFileName;	

	public String list() {

	    return "list";
	} 

	public String list_ym() {
	    try{
           this.webpage = yuemoManager.list(this.getSort(), this.getPage(), this.getPageSize());
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        this.showGridJson(webpage);
        return JSON_MESSAGE;
    }
	
    public String add() {
        return "uploadfile";
    }
    
	public String add_ym() {
	    return "add_ym";
	}
	
	public String edit_ym() {
	    try{
	       yuemo = this.yuemoManager.get(yuemoId);
	       
           String imageurl = yuemo.getImage();
           if(imageurl == null)
           {
               yuemo.setImage("");
           }else
           {
              String statis = SystemSetting.getStatic_server_domain();
              imageurl = imageurl.replaceAll("fs:",statis);  
              yuemo.setImage(imageurl);
           }	       
	       
	       
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
	    return "edit_ym";
    }
	
	public String detail_ym() {
	        try{
    	        yuemo = this.yuemoManager.get(yuemoId);
    	        joinList = this.yuemoManager.getJoinList(yuemoId);
    	        String member = yuemo.getMember();
    	        int len = 0;
    	        if((member != null) && (member != ""))
    	        {
    	          String[] memarray=member.split(",");
    	          len = memarray.length;
    	        }
    	        yuemo.setLength(len);
	        }catch (RuntimeException e) {
	            this.logger.error("数据库运行异常", e);
	            this.showPlainErrorJson(e.getMessage());
	        }
	        return "detail_ym";
	}

	 /**
     * 添加约沫
     */
    public String saveAddym() {
        HttpServletRequest request = ServletActionContext.getRequest(); 
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
        
        String time = request.getParameter("syuemotime");
        try{
            if(yuemo.getTitle().length()>100)
            {
                this.showPlainErrorJson("标题长度超长"); 
                return JSON_MESSAGE;
            }
            yuemo.setStatus(1);  
            yuemo.setTime(time);
            String imgPath= uploadImage(image);
            yuemo.setImage(imgPath);            
            yuemoManager.add(yuemo);
        }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        this.showSuccessJson("添加成功");
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

    /**
    * 修改约沫
    */
   public String saveEditYm() {
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
           if(image != null)
           {
             String imgPath= uploadImage(image);
             yuemo.setImage(imgPath);
           }
           yuemoManager.edit(yuemo);
           this.showSuccessJson("约沫修改成功");
       }catch (Exception e) {
           this.showErrorJson("非法参数");
       }
       return JSON_MESSAGE;
   }
   
   /**
    * 删除约沫
    * @param 
    * @return  result
    * result 1.操作成功.0.操作失败
    */
   public String deleteym() {
       try {
           this.yuemoManager.delete(id);
           this.showSuccessJson("删除成功");
       } catch (RuntimeException e) {
           this.showErrorJson("删除失败");
       }
       return this.JSON_MESSAGE;
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
               imgPath = UploadUtil.upload(image, imageFileName, "yuemo");
           }
       }catch (RuntimeException e) {
           this.logger.error("数据库运行异常", e);
           this.showPlainErrorJson(e.getMessage());
       }
       return imgPath;
   }   
    
    public YuemoManager getYuemoManager() {
		return yuemoManager;
	}

	public void setYuemoManager(YuemoManager yuemoManager) {
		this.yuemoManager = yuemoManager;
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
    
    public List<Member> getJoinList() {
        return joinList;
    }

    
    public void setJoinList(List<Member> joinList) {
        this.joinList = joinList;
    }

    
    public String getFilePath() {
        return filePath;
    }

    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
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
    
}
