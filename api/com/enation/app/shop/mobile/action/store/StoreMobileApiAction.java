/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：购物车api  
 * 修改人：  wanghongjun
 * 修改时间：2015-08-31
 * 修改内容：增加商家信息、商家商品等。
 */
package com.enation.app.shop.mobile.action.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.MemberCollect;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.IStoreCollectManager;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsTagManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 商家api
 * 
 * @author wanghongjun
 * @version v1.1 2015-08-31
 * @since v1.0
 */
@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("store")
public class StoreMobileApiAction extends WWAction {
	
	private Integer type;
	private String mark;
	private Integer storeid;
	private Integer num;
	private String keyword;
	private String start_price;
	private String end_price;
	private Integer stc_id;
	private Integer key;
	private String order;
	private Integer store_id;
	
	private IStoreManager storeManager;
	private IStoreMemberManager storeMemberManager;
	private IStoreGoodsTagManager storeGoodsTagManager;
	private IStoreGoodsManager storeGoodsManager;
	private IStoreCollectManager storeCollectManager;

	/**
	 * 通过商家ID，获得商家详细，
	 * store_id 商家ID
	 * type     商家类型
	 * @return
	 */
	public String storeIntro() {
		try {
			HttpServletRequest request = getRequest();
			Map data = new HashMap();
			Store store = storeManager.getStore(NumberUtils.toInt(request.getParameter("storeid"), 0));
			
			// 是否收藏
			StoreMember member = storeMemberManager.getStoreMember();
			if(member == null) {
				data.put("isCollect", false);
			} else {
				data.put("isCollect", storeCollectManager.isCollect(member.getMember_id(), store.getStore_id()));
			}
			data.put("store", store);
			this.json = JsonMessageUtil.getObjectJson(data);
		} catch (RuntimeException e) {
			this.logger.error("获取商品列表出错", e);
			this.showErrorJson(e.getMessage());

		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 商品商品列表
	 * num  显示数量
	 * storeid  商家ID
	 * @return
	 */
	public String storeGoodsList() {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		String mark = request.getParameter("mark");
		try {
			//xulipeng 增加店铺id，查找数量  条件
			if(storeid==null || storeid==0){
				StoreMember storeMember = storeMemberManager.getStoreMember();
				storeid = storeMember.getStore_id();
			}
			if(num==null || num==0){
				num=this.getPageSize();
			}
			Map map = new HashMap();
			map.put("mark", mark);
			map.put("storeid", storeid);
			
			String page = request.getParameter("page");
			page = (page == null || page.equals("")) ? "1" : page;
			Page webpage=new Page();
			//查询标签商品列表
			webpage = storeGoodsTagManager.getGoodsList(map, this.getPage(), num);
			//获取总记录数
			Long totalCount = webpage.getTotalCount();
			if(totalCount==0){
				this.json = "{result :1,data:[]}";
			}else{
				this.json = JsonMessageUtil.getListJson((List)webpage.getResult());
			}
		} catch (RuntimeException e) {
			this.logger.error("获取商品列表出错", e);
			this.showErrorJson(e.getMessage());

		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 添加收藏店铺
	 * @param member 店铺会员,StoreMember
	 * @param store_id 店铺Id,Integer
	 * @param collect	收藏店铺,MemberCollect
	 * result 	为1表示调用成功0表示失败
	 * @return 返回json串
	 */
	public String addCollect(){
		try {
			StoreMember member = storeMemberManager.getStoreMember();
			if (member!=null) {
				if(member.getStore_id()!=null && member.getStore_id().equals(store_id)){
					this.showErrorJson("不能收藏自己的店铺！");
					return WWAction.JSON_MESSAGE;
				}
				MemberCollect collect = new MemberCollect();
				collect.setMember_id(member.getMember_id());
				collect.setStore_id(store_id);
				
				this.storeCollectManager.addCollect(collect);
				this.storeManager.addcollectNum(store_id);
				this.showSuccessJson("收藏成功！");
				
			} else {
				
				this.showErrorJson("请登录！收藏失败！");
			}
		} catch (RuntimeException e) {
			this.logger.error("收藏店铺出错", e);
			this.showErrorJson(e.getMessage() + "");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 删除收藏店铺
	 * @param celloct_id 收藏店铺Id,Integer
	 * @param store_id 店铺Id,Integer
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String del(){
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			int collectId = NumberUtils.toInt(request.getParameter("collect_id").toString());
			this.storeCollectManager.delCollect(collectId);
			this.storeManager.reduceCollectNum(store_id);
			this.showSuccessJson("删除成功！");
		} catch (Exception e) {
			this.logger.error("删除店铺收藏出错", e);
			this.showErrorJson("删除失败，请重试！");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	
	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public Integer getStoreid() {
		return storeid;
	}

	public void setStoreid(Integer storeid) {
		this.storeid = storeid;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public IStoreGoodsTagManager getStoreGoodsTagManager() {
		return storeGoodsTagManager;
	}

	public void setStoreGoodsTagManager(IStoreGoodsTagManager storeGoodsTagManager) {
		this.storeGoodsTagManager = storeGoodsTagManager;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getStart_price() {
		return start_price;
	}

	public void setStart_price(String start_price) {
		this.start_price = start_price;
	}

	public String getEnd_price() {
		return end_price;
	}

	public void setEnd_price(String end_price) {
		this.end_price = end_price;
	}

	public Integer getStc_id() {
		return stc_id;
	}

	public void setStc_id(Integer stc_id) {
		this.stc_id = stc_id;
	}

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public IStoreGoodsManager getStoreGoodsManager() {
		return storeGoodsManager;
	}

	public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
		this.storeGoodsManager = storeGoodsManager;
	}

	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}

	public IStoreCollectManager getStoreCollectManager() {
		return storeCollectManager;
	}

	public void setStoreCollectManager(IStoreCollectManager storeCollectManager) {
		this.storeCollectManager = storeCollectManager;
	}

	
}
