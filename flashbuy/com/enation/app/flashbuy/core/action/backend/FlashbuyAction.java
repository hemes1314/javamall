package com.enation.app.flashbuy.core.action.backend;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.flashbuy.core.model.FlashBuy;
import com.enation.app.flashbuy.core.model.FlashBuyActive;
import com.enation.app.flashbuy.core.model.FlashBuyArea;
import com.enation.app.flashbuy.core.model.FlashBuyCat;
import com.enation.app.flashbuy.core.service.IFlashBuyActiveManager;
import com.enation.app.flashbuy.core.service.IFlashBuyAreaManager;
import com.enation.app.flashbuy.core.service.IFlashBuyCatManager;
import com.enation.app.flashbuy.core.service.IFlashBuyManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;

/**
 * 
 * @ClassName: FlashbuyAction 
 * @Description: 抢购Action 
 * @author humaodong 
 * @date 2015-9-21 上午12:55:05 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/flashbuy/flashbuy/flashbuy_list.html"),
	 @Result(name="add",type="freemarker", location="/flashbuy/flashbuy/flashbuy_add.html"),
	 @Result(name="edit",type="freemarker", location="/flashbuy/flashbuy/flashbuy_edit.html")
})

@Action("flashBuy")
public class FlashbuyAction extends WWAction{
	
	 private IFlashBuyManager flashBuyManager;
	 private IFlashBuyActiveManager flashBuyActiveManager;
	 private IFlashBuyCatManager flashBuyCatManager;
	 private IFlashBuyAreaManager flashBuyAreaManager;
	 private IGoodsManager goodsManager;
	 private List<FlashBuyArea> flashbuy_area_list;
	 private List<FlashBuyCat> flashbuy_cat_list;
	 private FlashBuyActive flashBuyActive;
	 private Map goods;
	 private int actid;
	 private int gbid;
	 private Integer status;
	 
	 private FlashBuy flashBuy;
	 private File image;
	 private String imageFileName;
	 private String image_src;
	/**
	 * 跳转至抢购列表
	 * @return
	 */
	public String list(){
		return "list";
	}
	 /**
	  * 按活动id显示抢购json
	  * @return
	  */
	 public String listJson(){
		 try {
			this.webpage = this.flashBuyManager.listByActId(this.getPage(), this.getPageSize(), actid, status);
			this.showGridJson(webpage);
		} catch (Exception e) {
			this.logger.error("查询出错",e);
			this.showErrorJson("查询出错");
		}
		 return this.JSON_MESSAGE;
	 }
	 /**
	  * 审核抢购
	  * @param gbid 限时抢购Id
	  * @param status 审核状态
	  * @return
	  */
	 public String auth(){
		 try {
			 this.flashBuyManager.auth(gbid, status);
			 this.showSuccessJson("操作成功");
		} catch (Exception e) {
			this.logger.error("审核操作失败",e);
			this.showErrorJson("审核操作失败"+e.getMessage());
		}
		 return this.JSON_MESSAGE;
	 }
	 /**
	  * 
	  * @Title: add
	  * @Description: 跳转至添加抢购页面
	  * @param	flashBuyActive 活动
	  * @param	flashbuy_cat_list 活动分类列表
	  * @param	flashbuy_area_list 活动地区列表
	  * @return String	添加活动页面
	  */
	 public String add(){
		 flashBuyActive= flashBuyActiveManager.get(actid);
		 flashbuy_cat_list= flashBuyCatManager.listAll();
		 flashbuy_area_list= flashBuyAreaManager.listAll();
		 return "add";
	 }
	 /**
	  * @Title: saveAdd
	  * @Description: 添加抢购商品
	  * @param 	allowTYpe 判断上传的图片类型
	  * @param	imageFileName 图片名称
	  * @param 	flashBuy 抢购
	  * 此功能有待扩展不应该将对象传输过来进行修改 应该传入的是字段然后新建对象存入进去。
	  * 不应该在这里去保存限时抢购商品图片应该在添加限时抢购的时候去调用统一的上传图片控件、并且应该支持多图上传。
	  * @return String	1为成功，0为失败	
	  */
	 public String saveAdd(){
		 try {
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
				String imgPath=	UploadUtil.upload(image, imageFileName, "flashbuy");
				flashBuy.setImg_url(imgPath);
			}
			flashBuyManager.add(flashBuy);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败");
			this.logger.error("限时抢购添加失败："+e);
		}
		 return this.JSON_MESSAGE;
		 
	 }
	 /**
	  * 
	  * @Title: edit
	  * @Description: 跳转至限时抢购修改页面
	  * @param	flashBuy 限时抢购商品
	  * @param	flashBuyActive 限时抢购活动
	  * @param	goods 商品
	  * @param	flashbuy_cat_list 限时抢购分类列表
	  * @param	flashbuy_area_list 限时抢购地区列表
	  * @param	image_src 限时抢购商品图片
	  * @return String 限时抢购修改页面
	  */
	 public String edit(){
		 flashBuy= flashBuyManager.get(gbid);
		 flashBuyActive= flashBuyActiveManager.get(flashBuy.getAct_id());
		 goods=goodsManager.get(flashBuy.getGoods_id());
		 flashbuy_cat_list= flashBuyCatManager.listAll();
		 flashbuy_area_list= flashBuyAreaManager.listAll();
		 image_src=UploadUtil.replacePath(flashBuy.getImg_url()); 
		 return "edit";
	 }
	 /**
	  * 
	  * @Title: saveEdit
	  * @Description: 保存修改商品
	  * @param flashBuy 限时抢购商品
	  * @return 1为成功。0为失败
	  */
	 public String saveEdit(){
		 try {
			 flashBuyManager.update(flashBuy);
			 this.showSuccessJson("修改限时抢购成功");
		} catch (Exception e) {
			this.logger.error("修改限时抢购失败",e);
			this.showErrorJson("修改限时抢购失败");
		}
		 return this.JSON_MESSAGE;
		 
	 }
	 //get set
	public IFlashBuyManager getFlashBuyManager() {
		return flashBuyManager;
	}
	public void setFlashBuyManager(IFlashBuyManager flashBuyManager) {
		this.flashBuyManager = flashBuyManager;
	}
	public int getActid() {
		return actid;
	}
	public void setActid(int actid) {
		this.actid = actid;
	}
	public int getGbid() {
		return gbid;
	}
	public void setGbid(int gbid) {
		this.gbid = gbid;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public IFlashBuyActiveManager getFlashBuyActiveManager() {
		return flashBuyActiveManager;
	}
	public void setFlashBuyActiveManager(
			IFlashBuyActiveManager flashBuyActiveManager) {
		this.flashBuyActiveManager = flashBuyActiveManager;
	}
	public FlashBuyActive getFlashBuyActive() {
		return flashBuyActive;
	}
	public void setFlashBuyActive(FlashBuyActive flashBuyActive) {
		this.flashBuyActive = flashBuyActive;
	}
	public IFlashBuyCatManager getFlashBuyCatManager() {
		return flashBuyCatManager;
	}
	public void setFlashBuyCatManager(IFlashBuyCatManager flashBuyCatManager) {
		this.flashBuyCatManager = flashBuyCatManager;
	}
	public IFlashBuyAreaManager getFlashBuyAreaManager() {
		return flashBuyAreaManager;
	}
	public void setFlashBuyAreaManager(IFlashBuyAreaManager flashBuyAreaManager) {
		this.flashBuyAreaManager = flashBuyAreaManager;
	}
	public List<FlashBuyArea> getFlashbuy_area_list() {
		return flashbuy_area_list;
	}
	public void setFlashbuy_area_list(List<FlashBuyArea> flashbuy_area_list) {
		this.flashbuy_area_list = flashbuy_area_list;
	}
	public List<FlashBuyCat> getFlashbuy_cat_list() {
		return flashbuy_cat_list;
	}
	public void setFlashbuy_cat_list(List<FlashBuyCat> flashbuy_cat_list) {
		this.flashbuy_cat_list = flashbuy_cat_list;
	}
	public FlashBuy getFlashBuy() {
		return flashBuy;
	}
	public void setFlashBuy(FlashBuy flashBuy) {
		this.flashBuy = flashBuy;
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
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}
	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}
	public Map getGoods() {
		return goods;
	}
	public void setGoods(Map goods) {
		this.goods = goods;
	}
	public String getImage_src() {
		return image_src;
	}
	public void setImage_src(String image_src) {
		this.image_src = image_src;
	}
	
}
