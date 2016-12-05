package com.enation.app.shop.mobile.action.maimo;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Maimo;
import com.enation.app.shop.mobile.service.impl.ApiMaimoManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonMessageUtil;

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/api/mobile")
@Action("maimo")
@Results({
	@Result(name="list", type="freemarker",location="/shop/admin/yuemo/yuemo_list.html"),
	@Result(name="add_ym", type="freemarker",location="/shop/admin/yuemo/yuemo_add.html"),
	@Result(name="detail_ym", type="freemarker",location="/shop/admin/yuemo/yuemo_detail.html"),
	@Result(name="edit_ym", type="freemarker",location="/shop/admin/yuemo/yuemo_edit.html")
})

@SuppressWarnings({ "unchecked", "serial" })
public class MaimoApiAction extends WWAction {
	private Integer[] id;
	private String title;
	private String content;
	private Maimo maimo;
	private String stime;
	private File image;
	private String imageFileName;
	private ApiMaimoManager apiMaimoManager;
	private final int PAGE_SIZE = 20;
	

	private  String price;
	private  String type;
	private  String address;

	public String list() {
	    try{      
	      HttpServletRequest request =ThreadContextHolder.getHttpRequest();
	      int page =NumberUtils.toInt(request.getParameter("page"),1) ;
	      Page maimoPage = apiMaimoManager.listPage(page, PAGE_SIZE);
	      
	      List<Maimo> maimolist= (List<Maimo>) maimoPage.getResult();
	      String temp = "";
          for(Maimo a :maimolist)
          {
               if(a.getFace() != null)
               {
                  temp = a.getFace();
                  temp = UploadUtil.replacePath(temp);
                  a.setFace(temp);
               }
               if(a.getImages() != null)
               {
                  temp = a.getImages();
                  temp = UploadUtil.replacePath(temp);
                  a.setImages(temp);
               }
          }
          this.json = JsonMessageUtil.getMobileListJson(maimolist);   
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
	    return WWAction.JSON_MESSAGE;
	} 
	 	
	public String addMaimo() {

        Member member = UserConext.getCurrentMember();
        if (member == null){
            this.json = JsonMessageUtil.expireSession();
            return WWAction.JSON_MESSAGE;
        }
        Long memberid = member.getMember_id();
	    try{
    	    maimo = new Maimo();
    	    maimo.setMember_id(memberid.toString());
    	    maimo.setAddress(address);
    	    maimo.setContent(content);
    	    maimo.setPrice(price);
    	    maimo.setTitle(title);
    	    maimo.setType(type);
    	    String urlimg="";
            if(image != null)
            {
                urlimg = UploadUtil.upload(image, imageFileName, "maimo");
            } 
            maimo.setImages(urlimg);
            
    	    boolean sta = apiMaimoManager.add(maimo);
    	    if(sta)
    	    {
    	      this.json = JsonMessageUtil.getMobileSuccessJson("发布成功");
    	    }
	    }catch (RuntimeException e) {
	           this.logger.error("数据库运行异常", e);
	           this.json = JsonMessageUtil.getMobileErrorJson("数据库异常");
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
  
    public Integer[] getId() {
        return id;
       }
       
    public void setId(Integer[] id) {
        this.id = id;
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

    
    public Maimo getMaimo() {
        return maimo;
    }

    
    public void setMaimo(Maimo maimo) {
        this.maimo = maimo;
    }

    
    public ApiMaimoManager getApiMaimoManager() {
        return apiMaimoManager;
    }

    
    public void setApiMaimoManager(ApiMaimoManager apiMaimoManager) {
        this.apiMaimoManager = apiMaimoManager;
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

    
    public String getPrice() {
        return price;
    }

    
    public void setPrice(String price) {
        this.price = price;
    }

    
    public String getType() {
        return type;
    }

    
    public void setType(String type) {
        this.type = type;
    }

    
    public String getAddress() {
        return address;
    }

    
    public void setAddress(String address) {
        this.address = address;
    }	
    
    
}
