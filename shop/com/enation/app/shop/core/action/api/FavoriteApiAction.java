package com.enation.app.shop.core.action.api;


import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IFavoriteManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;

/**
 * 收藏API
 * @author fenlongli
 *
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("favorite")
public class FavoriteApiAction extends WWAction{

	private IFavoriteManager favoriteManager;
	private Integer favorite_id;
	
	/**
	 * 删除一个会员收藏  whj 0225 下午18：25
	 * @param favorite_id ：要删除的会员收藏地址id,String型
	 * result  为1表示添加成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	
	public String delete() {
		try {
			Member member = UserConext.getCurrentMember();
			if (member != null) {
				this.favoriteManager.delete(favorite_id);
				this.showSuccessJson("删除成功");
			}
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
			this.showErrorJson("删除失败[" + e.getMessage() + "]");
		}
		return WWAction.JSON_MESSAGE;
	}

	public IFavoriteManager getFavoriteManager() {
		return favoriteManager;
	}

	public void setFavoriteManager(IFavoriteManager favoriteManager) {
		this.favoriteManager = favoriteManager;
	}

	public Integer getFavorite_id() {
		return favorite_id;
	}

	public void setFavorite_id(Integer favorite_id) {
		this.favorite_id = favorite_id;
	}
}
