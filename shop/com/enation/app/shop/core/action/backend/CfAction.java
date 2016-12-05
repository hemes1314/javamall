package com.enation.app.shop.core.action.backend;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Cf;
import com.enation.app.shop.core.service.impl.CfManager;
import com.enation.app.shop.core.service.impl.CfRecordManager;
import com.enation.app.shop.core.service.impl.GoodsManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("cf")
@Results({
	@Result(name="list", type="freemarker",location="/shop/admin/cf/cf_list.html"),
	@Result(name="add_cf", type="freemarker",location="/shop/admin/cf/cf_add.html"),
	@Result(name="detail_cf", type="freemarker",location="/shop/admin/cf/cf_detail.html"),
	@Result(name="edit_cf", type="freemarker",location="/shop/admin/cf/cf_edit.html"),
	@Result(name="goods_list", type="freemarker",location="/shop/admin/cf/cf_goods_list.html")
})

@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })

public class CfAction extends WWAction {

    	CfManager cfManager;
    	CfRecordManager cfRecordManager;
    	private Integer[] id;
    	private String title;
    	private String content;
    	private Cf cf;
    	private String stime;
        private Integer cfId;
        private Integer[] goodsIds;
    	private List<Cf> cfList;
    	private File image;
    	private String imageFileName;
    	private List<Member> cfMemberList;
    	private GoodsManager goodsManager;
    	private List<Map> goodsList;
    
    	public String list() {
    	    return "list";
    	}  
	
        public String add_cf() {
            return "add_cf";
        }
    	
        /**
        * 众筹列表
        */
       public String list_cf() {
            try{
              this.webpage = cfManager.list(this.getSort(), this.getPage(), this.getPageSize());
              
              List<Map> list = (List<Map>)webpage.getResult();
              //替换图片路径
              for(Map a :list)
              {
                 if(a.get("status") != null){
                     if(a.get("status").equals("0"))
                     {
                       a.put("status","未开始");
                     }else if(a.get("status").equals("1"))
                     {
                         a.put("status","进行中");
                     }else if(a.get("status").equals("2"))
                     {
                         a.put("status","已结束");
                     }
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
       * 编辑众筹信息
       */
       public String edit_cf() {
           try{
             cf = this.cfManager.get(cfId);
             String imageurl = cf.getBimage();
             if(imageurl == null)
             {
                 cf.setBimage("");
             }else
             {
                String statis = SystemSetting.getStatic_server_domain();
                imageurl = imageurl.replaceAll("fs:",statis);  
                cf.setBimage(imageurl);
             }       
           }catch (RuntimeException e) {
               this.logger.error("数据库运行异常", e);
               this.showPlainErrorJson(e.getMessage());
           }
           return "edit_cf";
       }
       
       /**
       * 众筹详情
       */
       public String detail_cf() {
           try{
               cf = this.cfManager.get(cfId);
               String member = cf.getMember();
               
               String imageurl = cf.getBimage();
               if(imageurl == null)
               {
                   cf.setBimage("");
               }else
               {
                  String statis = SystemSetting.getStatic_server_domain();
                  imageurl = imageurl.replaceAll("fs:",statis);  
                  cf.setBimage(imageurl);
               } 
               
               int len = 0;
               if((member != null) && (member != ""))
               {
                 String[] memarray=member.split(",");
                 len = memarray.length;
               }
               cfMemberList = cfRecordManager.getRecordlist(cfId);
               cf.setLength(len);
           }catch (RuntimeException e) {
               this.logger.error("数据库运行异常", e);
               this.showPlainErrorJson(e.getMessage());
           }
               return "detail_cf";
       }
       
       /**
        * 获取众筹属性的商品列表
        */
        public String goods_list() {
            try{
               goodsList = goodsManager.getCFGoods();
               
               for (Map map: goodsList) {
                   Integer goodsId = NumberUtils.toInt(map.get("goods_id").toString());
                   int count = cfManager.getGoodsCount(cfId, goodsId);
                   
                   if (count > 0) {
                       map.put("has", "1");
                   } else {
                       map.put("has", "0");
                   }
               }
            }catch (RuntimeException e) {
                this.logger.error("数据库运行异常", e);
                this.showPlainErrorJson(e.getMessage());
            }
            return "goods_list";
        }
        
        /**
         * 众筹活动添加商品
         */
        public String addCFGoods() {
            boolean addCFGoodsReturn = cfManager.addCFGoods(cfId, goodsIds);
            
            if (addCFGoodsReturn) {
                this.showPlainSuccessJson("添加商品成功");
            } else {
                this.showPlainErrorJson("添加商品失败");
            }
            
            return this.JSON_MESSAGE;
        }
       
       
        /**
        * 添加众筹
        */
       public String saveAddcf() {
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
           
           //截止时间
           String time = request.getParameter("scftime");
           try{
               cf.setTime(time);
               
               //发布时间
               Date date=new Date();
               DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
               String releaseTime=format.format(date); 
               cf.setRelease_time(releaseTime);
               String imagePath = uploadImages();
               cf.setDetail_image(imagePath); 
               cf.setBimage(imagePath);
               cf.setStatus("1");
               cf.setRelease_nickname("酒窖小二");
               //todo
               cf.setRelease_face("http://192.168.0.250//themes/b2b2cv2/images/logo.png");
               cf.setAlready_get((float)0);
               cfManager.add(cf);
               this.showSuccessJson("添加成功");
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
    
       /**
       * 修改众筹
       */
      public String saveEditCf() {
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
              HttpServletRequest request = ServletActionContext.getRequest(); 
              //截止时间
              String time = request.getParameter("scftime");
              cf.setTime(time);
              
              if(image != null)
              {
                  String imagePath = uploadImages();
                  cf.setDetail_image(imagePath); 
                  cf.setBimage(imagePath);           
              }
              
              cf.setSupport(null);
              cfManager.edit(cf);
              this.showSuccessJson("众筹修改成功");
          }catch (Exception e) {
              this.showErrorJson("非法参数");
          }
          return JSON_MESSAGE;
      }
      
      /**
       * 上传图片
       */
      public String uploadImages() {
          String imgPath ="";
          try{
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
             imgPath= UploadUtil.upload(image, imageFileName, "cf");
          }catch (RuntimeException e) {
              this.logger.error("图片上传失败", e);
              this.showPlainErrorJson(e.getMessage());
          }
          return imgPath;
      }
  
      /**
       * 删除众筹
       */
      public String deletecf() {
          try {
              this.cfManager.delete(id);
              this.showSuccessJson("删除成功");
          } catch (RuntimeException e) {
              this.showErrorJson("删除失败");
          }
          return this.JSON_MESSAGE;
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

    
    public CfManager getCfManager() {
		return cfManager;
	}

	public void setCfManager(CfManager cfManager) {
		this.cfManager = cfManager;
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

    
    public CfRecordManager getCfRecordManager() {
        return cfRecordManager;
    }

    
    public void setCfRecordManager(CfRecordManager cfRecordManager) {
        this.cfRecordManager = cfRecordManager;
    }

    
    public List<Member> getCfMemberList() {
        return cfMemberList;
    }

    
    public void setCfMemberList(List<Member> cfMemberList) {
        this.cfMemberList = cfMemberList;
    }

    
    public GoodsManager getGoodsManager() {
        return goodsManager;
    }

    
    public void setGoodsManager(GoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }

    
    public List<Map> getGoodsList() {
        return goodsList;
    }

    
    public void setGoodsList(List<Map> goodsList) {
        this.goodsList = goodsList;
    }

    
    public Integer[] getGoodsIds() {
        return goodsIds;
    }

    
    public void setGoodsIds(Integer[] goodsIds) {
        this.goodsIds = goodsIds;
    }
	
}
