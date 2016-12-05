/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：收藏api  
 * 修改人：  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.goods;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Favorite;
import com.enation.app.shop.mobile.service.IApiCommentManager;
import com.enation.app.shop.mobile.service.IApiFavoriteManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.TestUtil;

/**
 * 收藏API 提供收藏 增删改查
 * 
 * @author Sylow
 * @version v1.0 , 2015-08-24
 * @since v1.0
 */
@SuppressWarnings("serial")
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("favorite")
public class FavoriteApiAction extends WWAction {


	private IApiFavoriteManager apiFavoriteManager;
	private IApiCommentManager apiCommentManager;
								
	private final int PAGE_SIZE = 20;

	/**
	 * 添加收藏
	 * @param id 商品id  必填
	 * @return
	 */
	public String add() {
		try {
			Member member = UserConext.getCurrentMember();
			if (member == null) {
			    this.json = JsonMessageUtil.expireSession();
	            return WWAction.JSON_MESSAGE;
			}
			int goods_id = NumberUtils.toInt(getRequest().getParameter("id"));
			if (apiFavoriteManager.isFavorited(goods_id, member.getMember_id())){
                this.showPlainSuccessJson("已收藏");
                return WWAction.JSON_MESSAGE;
            }
			apiFavoriteManager.add(goods_id, member.getMember_id());
			Favorite favorite =	apiFavoriteManager.get(goods_id, member.getMember_id());
			this.json = JsonMessageUtil.getMobileObjectJson(favorite);
		} catch (RuntimeException e) {
			TestUtil.print(e);
			this.logger.error("添加收藏出错", e);
			this.showPlainErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 添加收藏商铺
	 * @param store_id 商铺id  必填
	 * @return
	 */
	public String addStore() {
		try {
			Member member = UserConext.getCurrentMember();
			if (member == null) {
			    this.json = JsonMessageUtil.expireSession();
	            return WWAction.JSON_MESSAGE;
			}
			Integer store_id = NumberUtils.toInt(getRequest().getParameter("store_id"));
			apiFavoriteManager.addStore(store_id, member.getMember_id());
			this.showPlainSuccessJson("收藏成功！");

		} catch (RuntimeException e) {
			TestUtil.print(e);
			this.logger.error("添加收藏出错", e);
			this.showPlainErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 删除一个会员收藏
	 * 
	 * get参数favoriteId ：要删除的会员收藏地址id,String型 返回的json : result 为1表示添加成功，0表示失败
	 * ，int型 返回的json : message 为提示信息 ，String型
	 */
	public String delete() {
	
			Member member = UserConext.getCurrentMember();
			if (member == null) {
			    this.json = JsonMessageUtil.expireSession();
	            return WWAction.JSON_MESSAGE;
			}

			HttpServletRequest request = getRequest();
			String favorite_id = request.getParameter("favoriteId");
				try {
					this.apiFavoriteManager.deleteForApp(favorite_id);
					this.showPlainSuccessJson("删除成功");
				} catch (Exception e) {
					if (this.logger.isDebugEnabled()) {
						logger.error(e.getStackTrace());
					}
					this.showPlainErrorJson("删除失败！");
				}
				return WWAction.JSON_MESSAGE;
		
	}
	/**
	 * 删除一个店铺收藏
	 * 
	 * get参数favoriteId ：要删除的会员收藏地址id,String
	 * 返回的json : result 为1表示添加成功，0表示失败
	 * 
	 */
	public String deleteStore() {
	
			Member member = UserConext.getCurrentMember();
			if (member == null) {
			    this.json = JsonMessageUtil.expireSession();
	            return WWAction.JSON_MESSAGE;
			}

			HttpServletRequest request = getRequest();
			String favorite_id = request.getParameter("id");
				try {
					this.apiFavoriteManager.deleteForApp(favorite_id);
					this.showPlainSuccessJson("删除成功");
				} catch (Exception e) {
					if (this.logger.isDebugEnabled()) {
						logger.error(e.getStackTrace());
					}
					this.showPlainErrorJson("删除失败！");
				}
				return WWAction.JSON_MESSAGE;
		
	}
	/**
	 * 所有收藏列表
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String list() {
		try {
			Member member = UserConext.getCurrentMember();
			if (member == null) {
			    this.json = JsonMessageUtil.expireSession();
	            return WWAction.JSON_MESSAGE;
			}
			HttpServletRequest request = getRequest();
			int page = NumberUtils.toInt(request.getParameter("page"), 1);

			Page webpage = apiFavoriteManager.list(member.getMember_id(), page,
					PAGE_SIZE);
			List list = (List) webpage.getResult();
			 for(int i = 0; i < list.size(); i ++){
	                Map<String, Object> map = (Map<String, Object>) list.get(i);
	                map.put("thumbnail", UploadUtil.replacePath(map.get("thumbnail").toString()));
	                int commentCount = apiCommentManager.getCommentsCount((Integer)map.get("goods_id"));
	                map.put("comment_num",commentCount);
	            }
			this.json = JsonMessageUtil.getMobileListJson(list);
		} catch (RuntimeException e) {
			this.logger.error("获取收藏列表出错", e);
			this.showPlainErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	/**
	 * 所有收藏列表
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String listStore() {
		try {
			Member member = UserConext.getCurrentMember();
			if (member == null) {
			    this.json = JsonMessageUtil.expireSession();
	            return WWAction.JSON_MESSAGE;
			}
			HttpServletRequest request = getRequest();
			int page = NumberUtils.toInt(request.getParameter("page"), 1);

			Page webpage = apiFavoriteManager.listForApp(member.getMember_id(), page,
					PAGE_SIZE);
			this.json = JsonMessageUtil.getMobileObjectJson(webpage);
		} catch (RuntimeException e) {
			this.logger.error("获取收藏列表出错", e);
			this.showPlainErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	public IApiFavoriteManager getApiFavoriteManager() {
		return apiFavoriteManager;
	}

	public void setApiFavoriteManager(IApiFavoriteManager apiFavoriteManager) {
		this.apiFavoriteManager = apiFavoriteManager;
	}

    
    public IApiCommentManager getApiCommentManager() {
        return apiCommentManager;
    }

    
    public void setApiCommentManager(IApiCommentManager apiCommentManager) {
        this.apiCommentManager = apiCommentManager;
    }
	
}
