package com.enation.app.shop.core.action.backend;

import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Tag;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IGoodsTagManager;
import com.enation.app.shop.core.service.ITagManager;
import com.enation.framework.action.WWAction;

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("goodsShow")
@Results({
	@Result(name="add",  location="/shop/admin/goodsshow/add.jsp"),
	@Result(name="list", type="freemarker", location="/shop/admin/goodsshow/list.html"),
	@Result(name="taglist", type="freemarker", location="/shop/admin/goodsshow/taglist.html"),
	@Result(name="search_list", type="freemarker", location="/shop/admin/goodsshow/search_list.html")
})
/**
 * 首页显示和列表推荐的统一管理
 * 
 * @author user
 * 
 */
public class GoodsShowAction extends WWAction {

	protected String name;
	protected String sn;
	protected String order;
	private Integer catid;
	protected Integer[] goods_id;
	private Integer[] tagids;
	private Integer[] ordernum;
	protected Integer market_enable = new Integer(1);

	protected IGoodsManager goodsManager;
	protected ITagManager tagManager;

	private Tag tag;

	private int tagid;
	private int goodsid;
	private List<Tag> taglist;
	private IGoodsTagManager goodsTagManager;
	private Map goodsMap;
	private String optype="no";
	/**
	 * 商品标签列表
	 * @return 商品标签列表
	 */
	public String taglist(){
		return "taglist";
	}
	/**
	 * 获取商品标签列表json
	 * @author LiFenLong
	 * @param taglist 标签列表,List
	 * @return 商品标签列表json
	 */
	public String taglistJson(){
		taglist = tagManager.list();
		this.showGridJson(taglist);
		return JSON_MESSAGE;
	}
	/**
	 * 显示   商品标签    商品列表
	 */
	public String execute() {
		return "list";
	}
	/**
	 * 商品标签    商品列表json
	 * @param catid 商品分类Id,Integer
	 * @param tagid 标签Id,Integer
	 * @return 商品标签    商品列表json
	 */
	public String listJson(){
		
		if (catid == null || catid.intValue() == 0) {
			this.webpage = goodsTagManager.getGoodsList(tagid, this.getPage(), this.getPageSize());
		} else {
			this.webpage = goodsTagManager.getGoodsList(tagid, catid.intValue(), this.getPage(), this.getPageSize());
		}
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}

	/**
	 *  商品标签    添加商品
	 * @param tagid 标签Id
	 * @return 商品标签    添加商品页
	 */
	public String add() {
		tag = tagManager.getById(tagid);
		return "add";
	}

	/**
	 * 跳转至标签商品选择列表
	 * @return
	 */
	public String search() {
		return "search_list";
	}

	/**
	 * 批量添加标签
	 * @param goods_id 商品Id,Integer[]
	 * @param tagid 标签Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String batchAdd() {
		try {
			if (goods_id != null && goods_id.length > 0) {
				for (Integer goodsId : goods_id) {
					goodsTagManager.addTag(tagid, goodsId);
				}
			}
			updateHttpCache();
			this.showSuccessJson("添加成功");
		} catch (RuntimeException e) {
			this.showErrorJson("添加失败");
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 删除一条记录
	 * @param tagid 标签Id,Integer
	 * @param goodsid 商品Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String delete() {
		try {
			goodsTagManager.removeTag(tagid, goodsid);
			updateHttpCache();
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.showErrorJson("删除失败");
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 批量更新排序数字
	 * @param goods_id 商品Id数组,Integer[]
	 * @param tagids 标签数组,Integer[]
	 * @param ordernum 排序数组,Integer[]
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String saveOrdernum() {
		try {
			goodsTagManager.updateOrderNum(goods_id, tagids, ordernum);
			int tempCatId = catid == null ? 0 : catid.intValue();
			updateHttpCache();
			this.showSuccessJson("保存排序成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.showErrorJson("保存排序失败");
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 更新Cache
	 */
	private void updateHttpCache(){
//		HttpCacheManager.updateUriModified("/");
//		HttpCacheManager.updateUriModified("/index.html");
//		HttpCacheManager.updateUriModified("/search-(.*).html");
	}
	
	public void setGoodsTagManager(IGoodsTagManager goodsTagManager) {
		this.goodsTagManager = goodsTagManager;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Integer getCatid() {
		return catid;
	}

	public void setCatid(Integer catid) {
		this.catid = catid;
	}

	public Integer[] getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Integer[] goods_id) {
		this.goods_id = goods_id;
	}
	public Integer[] getTagids() {
		return tagids;
	}

	public void setTagids(Integer[] tagids) {
		this.tagids = tagids;
	}

	public int getTagid() {
		return tagid;
	}

	public void setTagid(int tagid) {
		this.tagid = tagid;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
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

	public Integer[] getOrdernum() {
		return ordernum;
	}

	public void setOrdernum(Integer[] ordernum) {
		this.ordernum = ordernum;
	}

	public int getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(int goodsid) {
		this.goodsid = goodsid;
	}
	public List<Tag> getTaglist() {
		return taglist;
	}
	public void setTaglist(List<Tag> taglist) {
		this.taglist = taglist;
	}
	public Map getGoodsMap() {
		return goodsMap;
	}
	public void setGoodsMap(Map goodsMap) {
		this.goodsMap = goodsMap;
	}
	public String getOptype() {
		return optype;
	}
	public void setOptype(String optype) {
		this.optype = optype;
	}

}
