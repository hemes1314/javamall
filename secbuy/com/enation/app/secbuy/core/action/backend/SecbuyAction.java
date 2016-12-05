package com.enation.app.secbuy.core.action.backend;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.secbuy.core.model.SecBuy;
import com.enation.app.secbuy.core.model.SecBuyActive;
import com.enation.app.secbuy.core.model.SecBuyArea;
import com.enation.app.secbuy.core.model.SecBuyCat;
import com.enation.app.secbuy.core.service.ISecBuyActiveManager;
import com.enation.app.secbuy.core.service.ISecBuyAreaManager;
import com.enation.app.secbuy.core.service.ISecBuyCatManager;
import com.enation.app.secbuy.core.service.ISecBuyManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;

/**
 * 
 * @ClassName: SecbuyAction 
 * @Description: 秒拍Action 
 * @author TALON 
 * @date 2015-7-31 上午12:55:05 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/secbuy/secbuy/secbuy_list.html"),
	 @Result(name="add",type="freemarker", location="/secbuy/secbuy/secbuy_add.html"),
	 @Result(name="edit",type="freemarker", location="/secbuy/secbuy/secbuy_edit.html")
})

@Action("secBuy")
public class SecbuyAction extends WWAction{
	
	 private ISecBuyManager secBuyManager;
	 private ISecBuyActiveManager secBuyActiveManager;
	 private ISecBuyCatManager secBuyCatManager;
	 private ISecBuyAreaManager secBuyAreaManager;
	 private IGoodsManager goodsManager;
	 private List<SecBuyArea> secbuy_area_list;
	 private List<SecBuyCat> secbuy_cat_list;
	 private SecBuyActive secBuyActive;
	 private Map goods;
	 private int actid;
	 private int gbid;
	 private Integer status;
	 
	 private SecBuy secBuy;
	 private File image;
	 private String imageFileName;
	 private String image_src;
	/**
	 * 跳转至秒拍列表
	 * @return
	 */
	public String list(){
		return "list";
	}
	 /**
	  * 按活动id显示秒拍json
	  * @return
	  */
	 public String listJson(){
		 try {
			this.webpage = this.secBuyManager.listByActId(this.getPage(), this.getPageSize(), actid, status);
			this.showGridJson(webpage);
		} catch (Exception e) {
			this.logger.error("查询出错",e);
			this.showErrorJson("查询出错");
		}
		 return this.JSON_MESSAGE;
	 }
	 /**
	  * 审核秒拍
	  * @param gbid 秒拍Id
	  * @param status 审核状态
	  * @return
	  */
	 public String auth(){
		 try {
			 this.secBuyManager.auth(gbid, status);
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
	  * @Description: 跳转至添加秒拍页面
	  * @param	secBuyActive 秒拍活动
	  * @param	secbuy_cat_list 秒拍分类列表
	  * @param	secbuy_area_list 秒拍地区列表
	  * @return String	添加秒拍页面
	  */
	 public String add(){
		 secBuyActive= secBuyActiveManager.get(actid);
		 secbuy_cat_list= secBuyCatManager.listAll();
		 secbuy_area_list= secBuyAreaManager.listAll();
		 return "add";
	 }
	 /**
	  * @Title: saveAdd
	  * @Description: 添加秒拍商品
	  * @param 	allowTYpe 判断上传的图片类型
	  * @param	imageFileName 图片名称
	  * @param 	secBuy 秒拍
	  * 此功能有待扩展不应该将对象传输过来进行修改 应该传入的是字段然后新建对象存入进去。
	  * 不应该在这里去保存秒拍商品图片应该在添加秒拍的时候去调用统一的上传图片控件、并且应该支持多图上传。
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
				String imgPath=	UploadUtil.upload(image, imageFileName, "secbuy");
				secBuy.setImg_url(imgPath);
			}
			secBuyManager.add(secBuy);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败");
			this.logger.error("秒拍添加失败："+e);
		}
		 return this.JSON_MESSAGE;
		 
	 }
	 /**
	  * 
	  * @Title: edit
	  * @Description: 跳转至秒拍修改页面
	  * @param	secBuy 秒拍商品
	  * @param	secBuyActive 秒拍活动
	  * @param	goods 商品
	  * @param	secbuy_cat_list 秒拍分类列表
	  * @param	secbuy_area_list 秒拍地区列表
	  * @param	image_src 秒拍商品图片
	  * @return String 秒拍修改页面
	  */
	 public String edit(){
		 secBuy= secBuyManager.get(gbid);
		 secBuyActive= secBuyActiveManager.get(secBuy.getAct_id());
		 goods=goodsManager.get(secBuy.getGoods_id());
		 secbuy_cat_list= secBuyCatManager.listAll();
		 secbuy_area_list= secBuyAreaManager.listAll();
		 image_src=UploadUtil.replacePath(secBuy.getImg_url()); 
		 return "edit";
	 }
	 /**
	  * 
	  * @Title: saveEdit
	  * @Description: 保存修改商品
	  * @param secBuy 秒拍商品
	  * @return 1为成功。0为失败
	  */
	 public String saveEdit(){
		 try {
			 secBuyManager.update(secBuy);
			 this.showSuccessJson("修改秒拍成功");
		} catch (Exception e) {
			this.logger.error("修改秒拍失败",e);
			this.showErrorJson("修改秒拍失败");
		}
		 return this.JSON_MESSAGE;
		 
	 }
	 //get set
	public ISecBuyManager getSecBuyManager() {
		return secBuyManager;
	}
	public void setSecBuyManager(ISecBuyManager secBuyManager) {
		this.secBuyManager = secBuyManager;
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
	public ISecBuyActiveManager getSecBuyActiveManager() {
		return secBuyActiveManager;
	}
	public void setSecBuyActiveManager(
			ISecBuyActiveManager secBuyActiveManager) {
		this.secBuyActiveManager = secBuyActiveManager;
	}
	public SecBuyActive getSecBuyActive() {
		return secBuyActive;
	}
	public void setSecBuyActive(SecBuyActive secBuyActive) {
		this.secBuyActive = secBuyActive;
	}
	public ISecBuyCatManager getSecBuyCatManager() {
		return secBuyCatManager;
	}
	public void setSecBuyCatManager(ISecBuyCatManager secBuyCatManager) {
		this.secBuyCatManager = secBuyCatManager;
	}
	public ISecBuyAreaManager getSecBuyAreaManager() {
		return secBuyAreaManager;
	}
	public void setSecBuyAreaManager(ISecBuyAreaManager secBuyAreaManager) {
		this.secBuyAreaManager = secBuyAreaManager;
	}
	public List<SecBuyArea> getSecbuy_area_list() {
		return secbuy_area_list;
	}
	public void setSecbuy_area_list(List<SecBuyArea> secbuy_area_list) {
		this.secbuy_area_list = secbuy_area_list;
	}
	public List<SecBuyCat> getSecbuy_cat_list() {
		return secbuy_cat_list;
	}
	public void setSecbuy_cat_list(List<SecBuyCat> secbuy_cat_list) {
		this.secbuy_cat_list = secbuy_cat_list;
	}
	public SecBuy getSecBuy() {
		return secBuy;
	}
	public void setSecBuy(SecBuy secBuy) {
		this.secBuy = secBuy;
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
