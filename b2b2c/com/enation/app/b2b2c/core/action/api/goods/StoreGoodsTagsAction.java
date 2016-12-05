package com.enation.app.b2b2c.core.action.api.goods;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.goods.StoreTag;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsTagManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.shop.core.service.ITagManager;
import com.enation.framework.action.WWAction;
/**
 * 店铺商品标签
 * @author LiFenLong
 *
 */
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("tag")
public class StoreGoodsTagsAction extends WWAction{
	private IStoreGoodsTagManager storeGoodsTagManager;
	private IStoreMemberManager storeMemberManager;
	private ITagManager tagManager;
	private StoreTag tag;
	private Integer tagId;
	private Integer[] tag_id;
	private Integer[] reg_id;
	private Integer[] regId;
	private Integer[] ordernum;
	/**
	 * 添加店铺商品标签
	 * @param member 店铺会员,StoreMember
	 * @param tag	店铺商品标签,StoreTag
	 * @return	返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String add(){
		try {
			StoreMember member=storeMemberManager.getStoreMember();
			tag.setRel_count(0);
			tag.setStore_id(member.getStore_id());
			storeGoodsTagManager.add(tag);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.logger.error("商品标签添加失败"+e);
			this.showErrorJson("添加失败");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 修改店铺商品标签
	 * @param tag 店铺商品标签,StoreTag
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String edit(){
		try {
			tagManager.update(tag);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.logger.error("商品标签修改失败:"+e);
			this.showErrorJson("修改失败");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 删除店铺商品标签
	 * @param tag_id 商品标签Id, Integer[]
	 * @return  返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String delete(){
		try {
			tagManager.delete(tag_id);
			this.showSuccessJson("删除成功");
		} catch (Exception e) {
			this.logger.error("标签删除失败:"+e);
			this.showErrorJson("删除失败");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 删除商品标签引用
	 * @param tagId 商品标签Id,Integer
	 * @param reg_id 商品标签引用Id,Integer[]
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String deleteRel(){
		try {
			storeGoodsTagManager.deleteRel(tagId, reg_id);
			this.showSuccessJson("删除成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败");
			this.logger.error("标签引用删除失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 添加商品标签引用
	 * @param tagId 商品标签Id,Integer
	 * @param reg_id 商品标签引用Id,Integer[]
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String saveRel(){
		try {
			storeGoodsTagManager.addRels(tagId, reg_id);
			this.showSuccessJson("保存成功");
		} catch (Exception e) {
			this.showErrorJson("保存失败");
			this.logger.error("标签引用保存失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 修改商品标签引用排序
	 * @param tagId 商品标签Id,Integer
	 * @param regId 商品标签引用Id,Integer[]
	 * @param ordernum 排序顺序,Integer[]
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String saveSort(){
		try {
			storeGoodsTagManager.saveSort(tagId, regId, ordernum);
			this.showSuccessJson("保存成功");
		} catch (Exception e) {
			this.showErrorJson("保存失败");
			this.logger.error("商品标签保存失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	public IStoreGoodsTagManager getStoreGoodsTagManager() {
		return storeGoodsTagManager;
	}
	public void setStoreGoodsTagManager(IStoreGoodsTagManager storeGoodsTagManager) {
		this.storeGoodsTagManager = storeGoodsTagManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	public StoreTag getTag() {
		return tag;
	}
	public void setTag(StoreTag tag) {
		this.tag = tag;
	}
	public Integer getTagId() {
		return tagId;
	}
	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}
	public Integer[] getTag_id() {
		return tag_id;
	}
	public void setTag_id(Integer[] tag_id) {
		this.tag_id = tag_id;
	}
	public Integer[] getReg_id() {
		return reg_id;
	}
	public void setReg_id(Integer[] reg_id) {
		this.reg_id = reg_id;
	}
	public ITagManager getTagManager() {
		return tagManager;
	}
	public void setTagManager(ITagManager tagManager) {
		this.tagManager = tagManager;
	}
	public Integer[] getOrdernum() {
		return ordernum;
	}
	public void setOrdernum(Integer[] ordernum) {
		this.ordernum = ordernum;
	}
	public Integer[] getRegId() {
		return regId;
	}
	public void setRegId(Integer[] regId) {
		this.regId = regId;
	}
}
