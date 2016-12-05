package com.enation.app.shop.component.giftcard.action;

import java.io.File;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.shop.component.giftcard.model.GiftcardType;
import com.enation.app.shop.component.giftcard.service.IGiftcardTypeManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.StringUtil;

/**
 * 礼品卡类型管理
 * @author humaodong
 *2015-10-11下午12:02:39
 */
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list", type="freemarker", location="/com/enation/app/shop/component/giftcard/action/html/giftcard_type_list.html") ,
	 @Result(name="add", type="freemarker", location="/com/enation/app/shop/component/giftcard/action/html/giftcard_type_add.html"),
	 @Result(name="edit", type="freemarker", location="/com/enation/app/shop/component/giftcard/action/html/giftcard_type_edit.html") 
})
public class GiftcardTypeAction extends WWAction {
	
	private IGiftcardTypeManager giftcardTypeManager;
	private GiftcardType giftcardType;
	private int typeid;
	private Integer[] type_id;
	private File image;
    private String imageFileName;
	
	public String list(){
		return "list";
	}
	
	public String listJson(){
		this.webpage = this.giftcardTypeManager.list(this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	
	public String add(){
		
		return "add";
	}

	
	public String edit(){
		
		this.giftcardType = this.giftcardTypeManager.get(typeid);
		this.giftcardType.setType_image(UploadUtil.replacePath(this.giftcardType.getType_image()));
		return "edit";
	}
	
	public String saveAdd(){
	    
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
            String imgPath= UploadUtil.upload(image, imageFileName, "giftcard");
            giftcardType.setType_image(imgPath);
        }
		if( StringUtil.isEmpty( giftcardType.getType_name() )){
			this.showErrorJson("请输入类型名称");
			return this.JSON_MESSAGE;
		}
		
		
		if( giftcardType.getMoney() == 0  ){
			this.showErrorJson("请输入礼品卡面值");
			return this.JSON_MESSAGE;
		}
		
		if( giftcardType.getPrice() == null  ){
            this.showErrorJson("请输入礼品卡价格");
            return this.JSON_MESSAGE;
        }
		
		try {
			this.giftcardTypeManager.add(giftcardType);
			this.showSuccessJson("保存礼品卡类型成功");
		} catch (Throwable e) {
			this.logger.error("保存礼品卡类型出错", e);
			this.showErrorJson("保存礼品卡类型出错"+e.getMessage());
		}

		
		return this.JSON_MESSAGE;
	}
	
	
	public String saveEdit(){
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
            String imgPath= UploadUtil.upload(image, imageFileName, "giftcard");
            giftcardType.setType_image(imgPath);
        }
	    
		if( StringUtil.isEmpty( giftcardType.getType_name() )){
			this.showErrorJson("请输入类型名称");
			return this.JSON_MESSAGE;
		}
		
		
		if( giftcardType.getMoney() ==0  ){
			this.showErrorJson("请输入礼品卡面值");
			return this.JSON_MESSAGE;
		}
		
		if( giftcardType.getPrice() == null  ){
            this.showErrorJson("请输入礼品卡价格");
            return this.JSON_MESSAGE;
        }
		try {
			giftcardTypeManager.update(giftcardType);
			this.showSuccessJson("保存礼品卡类型成功");
		} catch (Throwable e) {
			this.logger.error("保存礼品卡类型出错", e);
			this.showErrorJson("保存礼品卡类型出错"+e.getMessage());
		}
		
		
		return this.JSON_MESSAGE;
	}
	
	
	public String delete(){
		
		try {
			this.giftcardTypeManager.delete(type_id);
			this.showSuccessJson("删除礼品卡类型成功");
		} catch (Throwable e) {
			this.logger.error("删除礼品卡类型出错", e);
			this.showErrorJson("删除礼品卡类型出错"+e.getMessage());
		}
		
		return this.JSON_MESSAGE;
	}


	public IGiftcardTypeManager getGiftcardTypeManager() {
		return giftcardTypeManager;
	}


	public void setGiftcardTypeManager(IGiftcardTypeManager giftcardTypeManager) {
		this.giftcardTypeManager = giftcardTypeManager;
	}

	public int getTypeid() {
		return typeid;
	}


	public void setTypeid(int typeid) {
		this.typeid = typeid;
	}


	public GiftcardType getGiftcardType() {
		return giftcardType;
	}


	public void setGiftcardType(GiftcardType giftcardType) {
		this.giftcardType = giftcardType;
	}

	public Integer[] getType_id() {
		return type_id;
	}

	public void setType_id(Integer[] type_id) {
		this.type_id = type_id;
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
