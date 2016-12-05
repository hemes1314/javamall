package com.enation.app.groupbuy.core.action.backend;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.groupbuy.core.model.GroupBuy;
import com.enation.app.groupbuy.core.model.GroupBuyActive;
import com.enation.app.groupbuy.core.model.GroupBuyArea;
import com.enation.app.groupbuy.core.model.GroupBuyCat;
import com.enation.app.groupbuy.core.service.IGroupBuyActiveManager;
import com.enation.app.groupbuy.core.service.IGroupBuyAreaManager;
import com.enation.app.groupbuy.core.service.IGroupBuyCatManager;
import com.enation.app.groupbuy.core.service.IGroupBuyManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;

/**
 * 
 * @ClassName: GroupbuyAction 
 * @Description: 团购Action 
 * @author TALON 
 * @date 2015-7-31 上午12:55:05 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/groupbuy/groupbuy/groupbuy_list.html"),
	 @Result(name="add",type="freemarker", location="/groupbuy/groupbuy/groupbuy_add.html"),
	 @Result(name="edit",type="freemarker", location="/groupbuy/groupbuy/groupbuy_edit.html")
})

@Action("groupBuy")
public class GroupbuyAction extends WWAction{
	
	 private IGroupBuyManager groupBuyManager;
	 private IGroupBuyActiveManager groupBuyActiveManager;
	 private IGroupBuyCatManager groupBuyCatManager;
	 private IGroupBuyAreaManager groupBuyAreaManager;
	 private IGoodsManager goodsManager;
	 private List<GroupBuyArea> groupbuy_area_list;
	 private List<GroupBuyCat> groupbuy_cat_list;
	 private GroupBuyActive groupBuyActive;
	 private Map goods;
	 private int actid;
	 private int gbid;
	 private Integer status;
	 
	 private GroupBuy groupBuy;
	 private File image;
	 private String imageFileName;
	 private String image_src;
	/**
	 * 跳转至团购列表
	 * @return
	 */
	public String list(){
		return "list";
	}
	 /**
	  * 按活动id显示团购json
	  * @return
	  */
	 public String listJson(){
		 try {
			this.webpage = this.groupBuyManager.listByActId(this.getPage(), this.getPageSize(), actid, status);
			this.showGridJson(webpage);
		} catch (Exception e) {
			this.logger.error("查询出错",e);
			this.showErrorJson("查询出错");
		}
		 return this.JSON_MESSAGE;
	 }
	 /**
	  * 审核团购
	  * @param gbid 团购Id
	  * @param status 审核状态
	  * @return
	  */
	 public String auth(){
		 try {
			 this.groupBuyManager.auth(gbid, status);
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
	  * @Description: 跳转至添加团购页面
	  * @param	groupBuyActive 团购活动
	  * @param	groupbuy_cat_list 团购分类列表
	  * @param	groupbuy_area_list 团购地区列表
	  * @return String	添加团购页面
	  */
	 public String add(){
		 groupBuyActive= groupBuyActiveManager.get(actid);
		 groupbuy_cat_list= groupBuyCatManager.listAll();
		 groupbuy_area_list= groupBuyAreaManager.listAll();
		 return "add";
	 }
	 /**
	  * @Title: saveAdd
	  * @Description: 添加团购商品
	  * @param 	allowTYpe 判断上传的图片类型
	  * @param	imageFileName 图片名称
	  * @param 	groupBuy 团购
	  * 此功能有待扩展不应该将对象传输过来进行修改 应该传入的是字段然后新建对象存入进去。
	  * 不应该在这里去保存团购商品图片应该在添加团购的时候去调用统一的上传图片控件、并且应该支持多图上传。
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
				String imgPath=	UploadUtil.upload(image, imageFileName, "groupbuy");
				groupBuy.setImg_url(imgPath);
				
				
				
				
				
			}
			groupBuyManager.add(groupBuy);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败");
			this.logger.error("团购添加失败："+e);
		}
		 return this.JSON_MESSAGE;
		 
	 }
	 /**
	  * 
	  * @Title: edit
	  * @Description: 跳转至团购修改页面
	  * @param	groupBuy 团购商品
	  * @param	groupBuyActive 团购活动
	  * @param	goods 商品
	  * @param	groupbuy_cat_list 团购分类列表
	  * @param	groupbuy_area_list 团购地区列表
	  * @param	image_src 团购商品图片
	  * @return String 团购修改页面
	  */
	 public String edit(){
		 groupBuy= groupBuyManager.get(gbid);
		 groupBuyActive= groupBuyActiveManager.get(groupBuy.getAct_id());
		 goods=goodsManager.get(groupBuy.getGoods_id());
		 groupbuy_cat_list= groupBuyCatManager.listAll();
		 groupbuy_area_list= groupBuyAreaManager.listAll();
		 image_src=UploadUtil.replacePath(groupBuy.getImg_url()); 
		 return "edit";
	 }
	 /**
	  * 
	  * @Title: saveEdit
	  * @Description: 保存修改商品
	  * @param groupBuy 团购商品
	  * @return 1为成功。0为失败
	  */
	 public String saveEdit(){
		 try {
			 groupBuyManager.update(groupBuy);
			 this.showSuccessJson("修改团购成功");
		} catch (Exception e) {
			this.logger.error("修改团购失败",e);
			this.showErrorJson("修改团购失败");
		}
		 return this.JSON_MESSAGE;
		 
	 }
	 //get set
	public IGroupBuyManager getGroupBuyManager() {
		return groupBuyManager;
	}
	public void setGroupBuyManager(IGroupBuyManager groupBuyManager) {
		this.groupBuyManager = groupBuyManager;
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
	public IGroupBuyActiveManager getGroupBuyActiveManager() {
		return groupBuyActiveManager;
	}
	public void setGroupBuyActiveManager(
			IGroupBuyActiveManager groupBuyActiveManager) {
		this.groupBuyActiveManager = groupBuyActiveManager;
	}
	public GroupBuyActive getGroupBuyActive() {
		return groupBuyActive;
	}
	public void setGroupBuyActive(GroupBuyActive groupBuyActive) {
		this.groupBuyActive = groupBuyActive;
	}
	public IGroupBuyCatManager getGroupBuyCatManager() {
		return groupBuyCatManager;
	}
	public void setGroupBuyCatManager(IGroupBuyCatManager groupBuyCatManager) {
		this.groupBuyCatManager = groupBuyCatManager;
	}
	public IGroupBuyAreaManager getGroupBuyAreaManager() {
		return groupBuyAreaManager;
	}
	public void setGroupBuyAreaManager(IGroupBuyAreaManager groupBuyAreaManager) {
		this.groupBuyAreaManager = groupBuyAreaManager;
	}
	public List<GroupBuyArea> getGroupbuy_area_list() {
		return groupbuy_area_list;
	}
	public void setGroupbuy_area_list(List<GroupBuyArea> groupbuy_area_list) {
		this.groupbuy_area_list = groupbuy_area_list;
	}
	public List<GroupBuyCat> getGroupbuy_cat_list() {
		return groupbuy_cat_list;
	}
	public void setGroupbuy_cat_list(List<GroupBuyCat> groupbuy_cat_list) {
		this.groupbuy_cat_list = groupbuy_cat_list;
	}
	public GroupBuy getGroupBuy() {
		return groupBuy;
	}
	public void setGroupBuy(GroupBuy groupBuy) {
		this.groupBuy = groupBuy;
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
