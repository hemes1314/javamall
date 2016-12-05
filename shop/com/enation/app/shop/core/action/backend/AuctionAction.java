package com.enation.app.shop.core.action.backend;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Auction;
import com.enation.app.shop.core.model.AuctionRecord;
import com.enation.app.shop.core.service.impl.AuctionManager;
import com.enation.app.shop.core.service.impl.AuctionRecordManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;  


@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("auction")
@Results({
	@Result(name="list", type="freemarker",location="/shop/admin/auction/auction_list.html"),
	@Result(name="add_auction", type="freemarker",location="/shop/admin/auction/auction_add.html"),
	@Result(name="detail_auction", type="freemarker",location="/shop/admin/auction/auction_detail.html"),
	@Result(name="good_select", type="freemarker",location="/shop/admin/auction/good_select.html"),
	@Result(name="edit_auction", type="freemarker",location="/shop/admin/auction/auction_edit.html")
})

@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })

public class AuctionAction extends WWAction {

    AuctionManager auctionManager;
    AuctionRecordManager auctionRecordManager;
	private Integer[] id;
	private String title;
	private String content;
	private Auction auction;
	private String stime;
    private Integer auctionId;	
	private List<Auction> auctionList;
	private List<AuctionRecord> auctionRecordList;
    private File image;
    private String imageFileName;
    public int record;


	public String list() {

	    return "list";  
	}  
	
	public String list_auction() {
	    //@Autowired也可以使用  
	    try{

	        //private Properties remoteSettings; 
	        
            this.webpage = auctionManager.list(this.getSort(), this.getPage(), this.getPageSize());
            
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
                   }else if(a.get("status").equals("3"))
                   {
                       a.put("status","流拍");
                   }
               }
                  
            }
            
            this.showGridJson(webpage);
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return JSON_MESSAGE;
    }
	
	public String add_auction() {
	    return "add_auction";
	}
	
	public String edit_auction() {
	    try{
    	    auction = this.auctionManager.get(auctionId);
    	    //图片地址替换
    	    String imageurl = auction.getImage();
    	    if(imageurl == null)
    	    {
    	        auction.setImage("");
    	    }else
    	    {
	           String statis = SystemSetting.getStatic_server_domain();
	           imageurl = imageurl.replaceAll("fs:",statis);  
	           auction.setImage(imageurl);
    	    }
    	    
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
	    return "edit_auction";
    }
	
	public String detail_auction() {
	    try{
            auction = this.auctionManager.get(auctionId);
            if(auction.getStatus().equals("0"))
            {
                auction.setStatus("未开始");
            }else if(auction.getStatus().equals("1"))
            {
                auction.setStatus("进行中");
            }else if(auction.getStatus().equals("2"))
            {
                auction.setStatus("已完成");
            }else if(auction.getStatus().equals("3"))
            {
                auction.setStatus("流拍");
            }
            //String member = auction.getMember();
            /*int len = 0;
            if((member != null) && (member != ""))
            {
              String[] memarray=member.split(",");
              len = memarray.length;
            }*/
            //参与拍卖的人数
            //auction.setLength(len);
            //出价次数
            this.record = this.auctionManager.getRecordCount(auctionId);
            auctionRecordList = auctionRecordManager.getRecordlist(auctionId);
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return "detail_auction";
	}
	
	
	 /**
     * 添加拍卖
     */
    public String saveAddAuction() {
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
        
        //当前时间
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(date); 
        
        //开始时间
        String startTime = request.getParameter("starttime");
        auction.setStart_time(startTime);
        
        //结束时间
        String stime = request.getParameter("sauctiontime");
        auction.setTime(stime);
        
        //当前价格与起始价格相同
        auction.setCprice(auction.getSprice());
        
        //设置拍卖的初始状态
        if(startTime.compareTo(time) > 0)
        {
            auction.setStatus("0");  
        }else
        {
            auction.setStatus("1");  
        }
        
        if(image != null)
        {
          String imgPath= uploadImage(image);
          auction.setImage(imgPath);
        }
        
        try{
            auctionManager.add(auction);
            this.showSuccessJson("添加成功");
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
    * 修改拍卖
    */
   public String saveEditAuction() {
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
             auction.setImage(imgPath);
           }
           //结束时间
           HttpServletRequest request = ServletActionContext.getRequest(); 
           String stime = request.getParameter("sauctiontime");
           auction.setTime(stime);
           auctionManager.edit(auction);
           this.showSuccessJson("拍卖修改成功");
       }catch (Exception e) {
           this.showErrorJson("非法参数");
       }
       return JSON_MESSAGE;
   }
   
   /**
   * 选择商品
   */
  public String good_select() {
      return "good_select";
  }
   /**
    * 删除拍卖
    * @param 
    * @return  result
    * result 1.操作成功.0.操作失败
    */
   public String deleteAuction() {
       try {
           this.auctionManager.delete(id);
           this.showSuccessJson("删除成功");
       } catch (RuntimeException e) {
           this.showErrorJson("删除失败");
       }
       return this.JSON_MESSAGE;
   }
    
    public AuctionManager getAuctionManager() {
		return auctionManager;
	}

	public void setAuctionManager(AuctionManager auctionManager) {
		this.auctionManager = auctionManager;
	}
	
	public List<Auction> getAuctionList() {
        return auctionList;
    }

    public void setAuctionList(List<Auction> auctionList) {
        this.auctionList = auctionList;
    }
    
 
    public Auction getAuction() {
     return this.auction;
    }
    
    public void setAuction(Auction auction) {
     this.auction = auction;
    }
    
    public Integer[] getId() {
        return id;
       }
       
    public void setId(Integer[] id) {
        this.id = id;
    }
    
    public Integer getAuctionId() {
        return auctionId;
       }
       
    public void setAuctionId(Integer auctionId) {
        this.auctionId = auctionId;
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

    
    public int getRecord() {
        return record;
    }

    
    public void setRecord(int record) {
        this.record = record;
    }

       
    
    public List<AuctionRecord> getAuctionRecordList() {
        return auctionRecordList;
    }

    
    public void setAuctionRecordList(List<AuctionRecord> auctionRecordList) {
        this.auctionRecordList = auctionRecordList;
    }

    public AuctionRecordManager getAuctionRecordManager() {
        return auctionRecordManager;
    }

    
    public void setAuctionRecordManager(AuctionRecordManager auctionRecordManager) {
        this.auctionRecordManager = auctionRecordManager;
    }
	
    
}
