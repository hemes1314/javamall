package com.enation.app.b2b2c.core.action.api.member;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.MemberCollect;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreCollectManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.framework.action.WWAction;

/**
 * 收藏店铺	Action
 * @author xulipeng
 *
 */
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("storeCollect")
public class StoreCollectApiAction extends WWAction {
	private IStoreCollectManager storeCollectManager;
	private IStoreMemberManager storeMemberManager;
	private IStoreManager storeManager;
	private Integer store_id;
	private Integer collect_id;
	/**
	 * 添加收藏店铺
	 * @param member 店铺会员,StoreMember
	 * @param store_id 店铺Id,Integer
	 * @param collect	收藏店铺,MemberCollect
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String addCollect(){
		StoreMember member=storeMemberManager.getStoreMember();
		if(member!=null){
			if(member.getStore_id()!=null && member.getStore_id().equals(store_id)){
				this.showErrorJson("不能收藏自己的店铺！");
				return JSON_MESSAGE;
			}
			MemberCollect collect = new MemberCollect();
			collect.setMember_id(member.getMember_id());
			collect.setStore_id(store_id);
			try {
				this.storeCollectManager.addCollect(collect);
				this.storeManager.addcollectNum(store_id);
				this.showSuccessJson("收藏成功！");
				
			} catch (RuntimeException e) {
				this.showErrorJson(e.getMessage());
			}
		}else{
			this.showErrorJson("请登录！收藏失败！");
		}
		return JSON_MESSAGE;
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
			this.storeCollectManager.delCollect(collect_id);
			this.storeManager.reduceCollectNum(store_id);
			this.showSuccessJson("删除成功！");
		} catch (Exception e) {
			this.showErrorJson("删除失败，请重试！");
		}
		return JSON_MESSAGE;
	}

	//set get
	public IStoreCollectManager getStoreCollectManager() {
		return storeCollectManager;
	}

	public void setStoreCollectManager(IStoreCollectManager storeCollectManager) {
		this.storeCollectManager = storeCollectManager;
	}


	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}

	

	
    public Integer getCollect_id() {
        return collect_id;
    }
    
    public void setCollect_id(Integer collect_id) {
        this.collect_id = collect_id;
    }
    public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
	
}
