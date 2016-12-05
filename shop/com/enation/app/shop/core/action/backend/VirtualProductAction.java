package com.enation.app.shop.core.action.backend;

import java.io.File;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Maimo;
import com.enation.app.shop.core.model.VirtualProduct;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.app.shop.core.service.impl.VirtualProductManager;
import com.enation.app.shop.core.service.impl.YuemoManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("vp")
@Results({
	@Result(name="list", type="freemarker",location="/shop/admin/virtualProduct/vp_list.html"),
	@Result(name="add_vp", type="freemarker",location="/shop/admin/virtualProduct/vp_add.html"),
	@Result(name="detail_vp", type="freemarker",location="/shop/admin/virtualProduct/vp_detail.html"),
	@Result(name="edit_vp", type="freemarker",location="/shop/admin/virtualProduct/vp_edit.html")
})

@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })

public class VirtualProductAction extends WWAction {
    VirtualProductManager virtualProductManager;
	private int vpId;
	private Integer[] id;
	private String name;
	private String intro;
	private String price;
	private String images;
	private File image;
	private String imageFileName;
    private String valid;	
    private VirtualProduct virtualProduct;
	private List<VirtualProduct> vpList;

	public String list() {
	    return "list";
	}  
	
	public String list_vp() {
	    try{
        this.webpage = virtualProductManager.list(this.getSort(), this.getPage(), this.getPageSize());
        List<VirtualProduct> virtuallist= (List<VirtualProduct>) webpage.getResult();
        String temp = "";
        for(VirtualProduct a :virtuallist)
        {
             if(a.getImages() != null)
             {
                temp = a.getImages();
                temp = UploadUtil.replacePath(temp);
                a.setImages(temp);
             }
             if(a.getImages() != null)
             {
                temp = a.getImages();
                temp = UploadUtil.replacePath(temp);
                a.setImages(temp);
             }
        } 
        
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        this.showGridJson(this.webpage);
        return JSON_MESSAGE;
    }
	  
	public String add_vp() {
	    return "add_vp";
	}
	
	public String edit_vp() {
	    try{
    	    virtualProduct = this.virtualProductManager.get(vpId);
    	    String temp="";
    	    
            if(virtualProduct.getImages()==null)
            {
                virtualProduct.setImages("default");    
            }
            
            if(virtualProduct.getImages() != null)
            {
               temp = virtualProduct.getImages();
               temp = UploadUtil.replacePath(temp);
               virtualProduct.setImages(temp);
            }
            
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
	    return "edit_vp";
    }
	
	public String detail_vp() {
	    try{
            virtualProduct = this.virtualProductManager.get(vpId);
            String temp="";
            
            if(virtualProduct.getImages()==null)
            {
                virtualProduct.setImages("default");    
            }
            if(virtualProduct.getImages() != null)
            {
               temp = virtualProduct.getImages();
               temp = UploadUtil.replacePath(temp);
               virtualProduct.setImages(temp);
            }
            int len = 0;
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return "detail_vp";
	}
	
	
	 /**
     * 添加保存虚拟商品
     */
    public String saveAddvp() {
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
                String imgPath= UploadUtil.upload(image, imageFileName, "vp");
                virtualProduct.setImages(imgPath);
            }
            virtualProductManager.add(virtualProduct);
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
    * 修改虚拟商品
    */
   public String saveEditVp() {
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
               String imgPath= UploadUtil.upload(image,imageFileName,"vp");
               virtualProduct.setImages(imgPath);
           } 
           virtualProductManager.edit(virtualProduct);
           this.showSuccessJson("虚拟物品修改成功");
       }catch (Exception e) {
           this.showErrorJson("非法参数");
       }
       return JSON_MESSAGE;
   }
   
   /**
    * 删除虚拟商品
    * @param 
    * @return  result
    * result 1.操作成功.0.操作失败
    */
   public String deletevp() {
       try {
           this.virtualProductManager.delete(id);
           this.showSuccessJson("删除成功");
       } catch (RuntimeException e) {
           this.showErrorJson("删除失败");
       }
       return this.JSON_MESSAGE;
   }
       
    public Integer[] getId() {
        return id;
       }
       
    public void setId(Integer[] id) {
        this.id = id;
    }

    
    public VirtualProductManager getVirtualProductManager() {
        return virtualProductManager;
    }

    
    public void setVirtualProductManager(VirtualProductManager virtualProductManager) {
        this.virtualProductManager = virtualProductManager;
    }

    
    
    public int getVpId() {
        return vpId;
    }

    
    public void setVpId(int vpId) {
        this.vpId = vpId;
    }

    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    
    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getPrice() {
        return price;
    }

    
    public void setPrice(String price) {
        this.price = price;
    }

    
    public String getImages() {
        return images;
    }

    
    public void setImages(String images) {
        this.images = images;
    }

    
    public String getValid() {
        return valid;
    }

    
    public void setValid(String valid) {
        this.valid = valid;
    }

    
    public VirtualProduct getVirtualProduct() {
        return virtualProduct;
    }

    
    public void setVirtualProduct(VirtualProduct virtualProduct) {
        this.virtualProduct = virtualProduct;
    }

    
    public List<VirtualProduct> getVpList() {
        return vpList;
    }

    
    public void setVpList(List<VirtualProduct> vpList) {
        this.vpList = vpList;
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
