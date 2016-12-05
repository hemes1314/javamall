package com.enation.app.advbuy.core.action.backend;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.advbuy.core.model.AdvBuy;
import com.enation.app.advbuy.core.model.AdvBuyActive;
import com.enation.app.advbuy.core.model.AdvBuyArea;
import com.enation.app.advbuy.core.model.AdvBuyCat;
import com.enation.app.advbuy.core.service.IAdvBuyActiveManager;
import com.enation.app.advbuy.core.service.IAdvBuyAreaManager;
import com.enation.app.advbuy.core.service.IAdvBuyCatManager;
import com.enation.app.advbuy.core.service.IAdvBuyManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;

/**
 * 
 * @ClassName: AdvbuyAction 
 * @Description: 预售Action 
 * @author TALON 
 * @date 2015-7-31 上午12:55:05 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/advbuy/advbuy/advbuy_list.html"),
	 @Result(name="add",type="freemarker", location="/advbuy/advbuy/advbuy_add.html"),
	 @Result(name="edit",type="freemarker", location="/advbuy/advbuy/advbuy_edit.html")
})

@Action("advBuy")
public class AdvbuyAction extends WWAction{
	
	 private IAdvBuyManager advBuyManager;
	 private IAdvBuyActiveManager advBuyActiveManager;
	 private IAdvBuyCatManager advBuyCatManager;
	 private IAdvBuyAreaManager advBuyAreaManager;
	 private IGoodsManager goodsManager;
	 private List<AdvBuyArea> advbuy_area_list;
	 private List<AdvBuyCat> advbuy_cat_list;
	 private AdvBuyActive advBuyActive;
	 private Map goods;
	 private int actid;
	 private int gbid;
	 private Integer status;
	 
	 private AdvBuy advBuy;
	 private File image;
	 private String imageFileName;
	 private String image_src;
	/**
	 * 跳转至预售列表
	 * @return
	 */
	public String list(){
		return "list";
	}
	 /**
	  * 按活动id显示预售json
	  * @return
	  */
	 public String listJson(){
		 try {
			this.webpage = this.advBuyManager.listByActId(this.getPage(), this.getPageSize(), actid, status);
			this.showGridJson(webpage);
		} catch (Exception e) {
			this.logger.error("查询出错",e);
			this.showErrorJson("查询出错");
		}
		 return this.JSON_MESSAGE;
	 }
	 /**
	  * 审核预售
	  * @param gbid 预售Id
	  * @param status 审核状态
	  * @return
	  */
	 public String auth(){
		 try {
			 this.advBuyManager.auth(gbid, status);
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
	  * @Description: 跳转至添加预售页面
	  * @param	advBuyActive 预售活动
	  * @param	advbuy_cat_list 预售分类列表
	  * @param	advbuy_area_list 预售地区列表
	  * @return String	添加预售页面
	  */
	 public String add(){
		 advBuyActive= advBuyActiveManager.get(actid);
		 advbuy_cat_list= advBuyCatManager.listAll();
		 advbuy_area_list= advBuyAreaManager.listAll();
		 return "add";
	 }
	 /**
	  * @Title: saveAdd
	  * @Description: 添加预售商品
	  * @param 	allowTYpe 判断上传的图片类型
	  * @param	imageFileName 图片名称
	  * @param 	advBuy 预售
	  * 此功能有待扩展不应该将对象传输过来进行修改 应该传入的是字段然后新建对象存入进去。
	  * 不应该在这里去保存预售商品图片应该在添加预售的时候去调用统一的上传图片控件、并且应该支持多图上传。
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
				String imgPath=	UploadUtil.upload(image, imageFileName, "advbuy");
				advBuy.setImg_url(imgPath);
			}
			advBuyManager.add(advBuy);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败");
			this.logger.error("预售添加失败："+e);
		}
		 return this.JSON_MESSAGE;
		 
	 }
	 /**
	  * 
	  * @Title: edit
	  * @Description: 跳转至预售修改页面
	  * @param	advBuy 预售商品
	  * @param	advBuyActive 预售活动
	  * @param	goods 商品
	  * @param	advbuy_cat_list 预售分类列表
	  * @param	advbuy_area_list 预售地区列表
	  * @param	image_src 预售商品图片
	  * @return String 预售修改页面
	  */
	 public String edit(){
		 advBuy= advBuyManager.get(gbid);
		 advBuyActive= advBuyActiveManager.get(advBuy.getAct_id());
		 goods=goodsManager.get(advBuy.getGoods_id());
		 advbuy_cat_list= advBuyCatManager.listAll();
		 advbuy_area_list= advBuyAreaManager.listAll();
		 image_src=UploadUtil.replacePath(advBuy.getImg_url()); 
		 return "edit";
	 }
	 /**
	  * 
	  * @Title: saveEdit
	  * @Description: 保存修改商品
	  * @param advBuy 预售商品
	  * @return 1为成功。0为失败
	  */
	 public String saveEdit(){
		 try {
			 advBuyManager.update(advBuy);
			 this.showSuccessJson("修改预售成功");
		} catch (Exception e) {
			this.logger.error("修改预售失败",e);
			this.showErrorJson("修改预售失败");
		}
		 return this.JSON_MESSAGE;
		 
	 }
	 //get set
	public IAdvBuyManager getAdvBuyManager() {
		return advBuyManager;
	}
	public void setAdvBuyManager(IAdvBuyManager advBuyManager) {
		this.advBuyManager = advBuyManager;
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
	public IAdvBuyActiveManager getAdvBuyActiveManager() {
		return advBuyActiveManager;
	}
	public void setAdvBuyActiveManager(
			IAdvBuyActiveManager advBuyActiveManager) {
		this.advBuyActiveManager = advBuyActiveManager;
	}
	public AdvBuyActive getAdvBuyActive() {
		return advBuyActive;
	}
	public void setAdvBuyActive(AdvBuyActive advBuyActive) {
		this.advBuyActive = advBuyActive;
	}
	public IAdvBuyCatManager getAdvBuyCatManager() {
		return advBuyCatManager;
	}
	public void setAdvBuyCatManager(IAdvBuyCatManager advBuyCatManager) {
		this.advBuyCatManager = advBuyCatManager;
	}
	public IAdvBuyAreaManager getAdvBuyAreaManager() {
		return advBuyAreaManager;
	}
	public void setAdvBuyAreaManager(IAdvBuyAreaManager advBuyAreaManager) {
		this.advBuyAreaManager = advBuyAreaManager;
	}
	public List<AdvBuyArea> getAdvbuy_area_list() {
		return advbuy_area_list;
	}
	public void setAdvbuy_area_list(List<AdvBuyArea> advbuy_area_list) {
		this.advbuy_area_list = advbuy_area_list;
	}
	public List<AdvBuyCat> getAdvbuy_cat_list() {
		return advbuy_cat_list;
	}
	public void setAdvbuy_cat_list(List<AdvBuyCat> advbuy_cat_list) {
		this.advbuy_cat_list = advbuy_cat_list;
	}
	public AdvBuy getAdvBuy() {
		return advBuy;
	}
	public void setAdvBuy(AdvBuy advBuy) {
		this.advBuy = advBuy;
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
