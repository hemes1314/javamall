package com.enation.app.b2b2cFlashbuy.core.action.api;

import java.io.File;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2cFlashbuy.core.model.StoreFlashBuy;
import com.enation.app.flashbuy.core.model.FlashBuy;
import com.enation.app.flashbuy.core.service.IFlashBuyManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;

/**
 * 限时抢购api
 * @author humaodong
 *2015-9-18下午7:07:19
 */
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("flashBuy")
@Component
public class FlashBuyApiAction extends WWAction {
	
	private IFlashBuyManager flashBuyManager;
	private IStoreMemberManager storeMemberManager;
	
	private int gb_id;
	private int act_id;
	private int area_id;
	private String gb_name;
	private String gb_title;
	private String goods_name;
	private int goods_id;
	private double price;
	private String img_url;
	private int goods_num;
	private int cat_id;
	private int visual_num;
	private int limit_num;
	private String remark;
	private File image;
	private String imageFileName;
	private double original_price;
	
	private int goods_enable_store;
	
	
	/**
	 * 添加限时抢购
	 * @return
	 */
	public String add(){
		try {
			StoreMember storeMember = storeMemberManager.getStoreMember();
			//判断是否登录
			if(storeMember==null ) {
				throw new RuntimeException("尚未登陆，不能使用此API");
			}
			//判断限时抢购数量是否超过库存
			if(goods_num>goods_enable_store){
				this.showErrorJson("限时抢购数量必须小于商品数量");
				return this.JSON_MESSAGE;
			}
			StoreFlashBuy flashBuy = this.putData();
			int result = flashBuyManager.add(flashBuy);
			this.showSuccessJson("限时抢购添加成功", result);
		} catch (Exception e) {
			this.showErrorJson("限时抢购添加失败"+e.getMessage());
			this.logger.error("限时抢购添加失败", e);
		}
		
		
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 更新限时抢购信息
	 * 
	 * @return
	 */
	public String update(){
		
		StoreMember storeMember = storeMemberManager.getStoreMember();
		if(storeMember==null ) {
			throw new RuntimeException("尚未登陆，不能使用此API");
		}
		
		try {
			FlashBuy flashBuy = this.putData();
			flashBuy.setGb_id(gb_id);
			flashBuyManager.update(flashBuy);
			this.showSuccessJson("限时抢购更新成功");
		} catch (Exception e) {
			this.showErrorJson("限时抢购更新失败"+e.getMessage());
			this.logger.error("限时抢购更新失败", e);
		}
		return this.JSON_MESSAGE;
	}
	
	
	/**
	 * 删除限时抢购
	 * @param gb_id 要删除限时抢购id, int 型
	 * @return 操作结果 json字串
	 */
	public String delete(){
		try {
			this.flashBuyManager.delete(gb_id);
			this.showSuccessJson("删除成功");
			
		} catch (Exception e) {
			this.showErrorJson("删除失败"+e.getMessage());
			this.logger.error("删除失败", e);
		}
		
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 保存数据
	 * @return
	 */
	private StoreFlashBuy  putData(){
		StoreMember storeMember = storeMemberManager.getStoreMember();
		if(storeMember==null ) {
			throw new RuntimeException("尚未登陆，不能使用此API");
		}
		
		StoreFlashBuy flashBuy = new StoreFlashBuy();
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
		
		flashBuy.setStore_id(storeMember.getStore_id());
		flashBuy.setAct_id(act_id);
		flashBuy.setArea_id(area_id);
		flashBuy.setCat_id(cat_id);
		flashBuy.setGb_name(gb_name);
		flashBuy.setGb_title(gb_title);
		flashBuy.setGoods_id(goods_id);
		flashBuy.setGoods_name(goods_name);
		flashBuy.setGoods_num(goods_num);
		flashBuy.setLimit_num(limit_num);
		flashBuy.setPrice(price);
		flashBuy.setOriginal_price(original_price);
		flashBuy.setRemark(remark);
		flashBuy.setVisual_num(visual_num);
		return flashBuy;
	}

	public IFlashBuyManager getFlashBuyManager() {
		return flashBuyManager;
	}

	public void setFlashBuyManager(IFlashBuyManager flashBuyManager) {
		this.flashBuyManager = flashBuyManager;
	}

	public int getGb_id() {
		return gb_id;
	}

	public void setGb_id(int gb_id) {
		this.gb_id = gb_id;
	}

	public int getAct_id() {
		return act_id;
	}

	public void setAct_id(int act_id) {
		this.act_id = act_id;
	}

	public int getArea_id() {
		return area_id;
	}

	public void setArea_id(int area_id) {
		this.area_id = area_id;
	}

	public String getGb_name() {
		return gb_name;
	}

	public void setGb_name(String gb_name) {
		this.gb_name = gb_name;
	}

	public String getGb_title() {
		return gb_title;
	}

	public void setGb_title(String gb_title) {
		this.gb_title = gb_title;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public int getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(int goods_id) {
		this.goods_id = goods_id;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public int getGoods_num() {
		return goods_num;
	}

	public void setGoods_num(int goods_num) {
		this.goods_num = goods_num;
	}

	public int getCat_id() {
		return cat_id;
	}

	public void setCat_id(int cat_id) {
		this.cat_id = cat_id;
	}

	public int getVisual_num() {
		return visual_num;
	}

	public void setVisual_num(int visual_num) {
		this.visual_num = visual_num;
	}

	public int getLimit_num() {
		return limit_num;
	}

	public void setLimit_num(int limit_num) {
		this.limit_num = limit_num;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public double getOriginal_price() {
		return original_price;
	}

	public void setOriginal_price(double original_price) {
		this.original_price = original_price;
	}

	public int getGoods_enable_store() {
		return goods_enable_store;
	}

	public void setGoods_enable_store(int goods_enable_store) {
		this.goods_enable_store = goods_enable_store;
	}
	
}
