package com.enation.app.shop.core.action.backend;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Tag;
import com.enation.app.shop.core.service.ITagManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;

/**
 * 标签action
 * @author kingapex
 * 2010-7-14上午11:54:15
 * @author LiFenLong 2014-4-1;4.0版本改造
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("tag")
@Results({
	@Result(name="add", type="freemarker", location="/shop/admin/tag/add.html"),
	@Result(name="edit", type="freemarker", location="/shop/admin/tag/edit.html"),
	@Result(name="list", type="freemarker", location="/shop/admin/tag/tag_list.html") 
})
public class TagAction extends WWAction {
	
	private ITagManager tagManager;
	private Tag tag;
	private Integer[] tag_id;
	private Integer tagId;
	/**
	 * 检测标签是否有相关联的商品
	 * @param tag_id 标签Id数组,Integer[]
	 * @return json
	 * result 1.有.0.没有
	 */
	public String checkJoinGoods(){
		if(this.tagManager.checkJoinGoods(tag_id)){
			this.json="{result:1}";
		}else{
			this.json="{result:0}";
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 检测标签名是否重名
	 * @param tag 标签,Tag
	 * @return json
	 * result 1.有.0.没有
	 */
	public String checkname(){
		if( this.tagManager.checkname(tag.getTag_name(), tag.getTag_id()) ){
			this.json="{result:1}";
		}else{
			this.json="{result:0}";
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 跳转至添加标签页面
	 * @return 添加标签页面
	 */
	public String add(){
		return "add";
	}
	/**
	 * 跳转至修改标签页面
	 * @param tagId 标签Id,Integer
	 * @return 修改标签页面
	 */
	public String edit(){
		tag = this.tagManager.getById(tagId);
		return "edit";
	}
	/**
	 * 添加标签
	 * @param tag 标签,Tag
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String saveAdd(){
		try {
			this.tagManager.add(tag);
			this.showSuccessJson("添加标签成功");
		} catch (Exception e) {
			this.showErrorJson("添加标签失败");
			logger.error("添加标签失败", e);
		}
		return this.JSON_MESSAGE;
	}
	
	
	/**
	 * 保存修改
	 * @param tag 标签,Tag
	 * @return
	 */
	public String saveEdit(){
		
		if(EopSetting.IS_DEMO_SITE){
			if(tag.getTag_id()<=3){
				this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
				return JSON_MESSAGE;
			}
		}
		
		this.tagManager.update(tag);
		this.showSuccessJson("商品修改成功");		
		return this.JSON_MESSAGE;
	}
	/**
	 * 删除标签
	 * @param tag_id 标签Id数组,Integer[]
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String delete(){
		if(EopSetting.IS_DEMO_SITE){
			for(Integer tid : tag_id){
				if(tid<=3){
					this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
					return JSON_MESSAGE;
				}
			}
		}
		
	 	try {
			this.tagManager.delete(tag_id);
			this.showSuccessJson("标签删除成功");
		} catch (Exception e) {
			this.showErrorJson("标签删除失败");
			logger.error("标签删除失败", e);
		}
		return this.JSON_MESSAGE;	
	}
	/**
	 * 跳转至标签列表页
	 * @return 标签列表页
	 */
	public String list(){
		return "list";
	}
	/**
	 * 获取标签列表Json
	 * @author LiFenLong
	 * @return 标签列表Json
	 */
	public String listJson(){
		this.webpage = this.tagManager.list(this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}

	public ITagManager getTagManager() {
		return tagManager;
	}

	public void setTagManager(ITagManager tagManager) {
		this.tagManager = tagManager;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public Integer[] getTag_id() {
		return tag_id;
	}

	public void setTag_id(Integer[] tag_id) {
		this.tag_id = tag_id;
	}

	public Integer getTagId() {
		return tagId;
	}

	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}
}
