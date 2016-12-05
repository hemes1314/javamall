package com.enation.app.b2b2c.core.action.api.store;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.StoreBonus;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStorePromotionManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;

/**
 * 店铺促销管理
 * @author xulipeng
 *	2015年1月12日23:02:42
 */

@SuppressWarnings("serial")
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("promotion")
@Results({
	 @Result(name="edit", type="freemarker", location="/themes/default/b2b2c/storesite/navication_edit.html") 
})
public class StorePromotionApiAction extends WWAction {

	private IStorePromotionManager storePromotionManager;
	private IStoreMemberManager storeMemberManager;
	private String type_name;
	private Double min_goods_amount;
	private Double type_money;
	private String type_recognition;
	private String sendTimeStart;
	private String sendTimeEnd;
	private String useTimeStart;
	private String useTimeEnd;
	private String img_bonus;
	private Integer store_id;
	private Integer type_id;
	private Integer limit_num;
	private Integer is_given;
	private Integer create_num;
	
	/**
	 * 添加优惠卷
	 * @param member 店铺会员,StoreMember
	 * @param type_name
	 * @param type_money
	 * @param min_goods_amount
	 * @param useTimeStart
	 * @param useTimeEnd
	 * @param bonus StoreBonus
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String add_fullSubtract(){
		
		StoreBonus bonus = this.setParam();
		bonus.setBelong(2);
		try {
			this.storePromotionManager.add_FullSubtract(bonus);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败");
		}
		return JSON_MESSAGE;
	}
	
	/**
     * 查看优惠券识别码是否重复
     * @param recognition 优惠券识别码,String
     * @return 返回json串
     * result  为1表示调用成功0表示失败
     */
    public String checkRecognition(){
        if(this.storePromotionManager.checkRecognition(type_recognition)){
            this.showErrorJson("优惠券识别码重复");
        }else{
            this.showSuccessJson("优惠券识别码可以使用");
        }
        return JSON_MESSAGE;
    }
	
	/**
	 * 修改优惠券
	 * @return
	 */
	public String edit_fullSubtract(){
		StoreBonus bonus = this.setParam();
		bonus.setType_id(type_id);
		bonus.setBelong(2);

        if(bonus.getUse_end_date()<=bonus.getUse_start_date()){ 
            this.showErrorJson("结束时间不得大于开始时间");
            return JSON_MESSAGE;
        } 
		try {
			this.storePromotionManager.edit_FullSubtract(bonus);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 用户领取优惠卷
	 * @return
	 */
	public String receiveBonus(){
		StoreMember member= this.storeMemberManager.getStoreMember(); 
		try {
		    if (member == null) throw new RuntimeException("请您先登录再领取优惠券!");
			if(member.getStore_id()!=store_id){
				this.storePromotionManager.receive_bonus(member.getMember_id(), store_id, type_id);
				this.showSuccessJson("领取成功!");
			}else if(member.getStore_id().intValue()==store_id){
				this.showErrorJson("您不能领自己店铺的优惠券!");

			}
		} catch (Exception e) {
			this.showErrorJson(e.getMessage());
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 用户删除优惠券
	 * @return
	 */
	public String deleteBonus(){
		try {
			this.storePromotionManager.deleteBonus(type_id);
			this.showSuccessJson("删除成功!");
		} catch (RuntimeException e) {
			this.showErrorJson(e.getMessage());
		} catch (Exception e) {
			this.showErrorJson("删除失败!");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 设置优惠卷参数
	 * @return
	 */
	private StoreBonus setParam(){
		
		StoreMember member= this.storeMemberManager.getStoreMember();
		StoreBonus bonus = new StoreBonus();
		/*===============add by lin yang================================
		 * 添加优惠卷识别码
		 * 开始时间
		 * 结束时间
		 */
		bonus.setRecognition(type_recognition);
		if (sendTimeStart != null) bonus.setSend_start_date(DateUtil.getDateline(sendTimeStart+" 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		if (sendTimeEnd != null) bonus.setSend_end_date(DateUtil.getDateline(sendTimeEnd+" 23:59:59", "yyyy-MM-dd HH:mm:ss"));
		bonus.setType_money(type_money);
		bonus.setType_name(type_name);
		bonus.setMin_goods_amount(min_goods_amount);
		bonus.setUse_start_date(DateUtil.getDateline(useTimeStart+" 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		bonus.setUse_end_date(DateUtil.getDateline(useTimeEnd+" 23:59:59", "yyyy-MM-dd HH:mm:ss"));
		bonus.setStore_id(member.getStore_id());
		bonus.setCreate_num(create_num);
		bonus.setLimit_num(limit_num);
	//	bonus.setIs_given(is_given);                页面已经被注销，不明原因。   本处注销，如果需要，请优先处理本处。whj  2015-05-22
		
		return bonus;
	}
	
	//set get

	public IStorePromotionManager getStorePromotionManager() {
		return storePromotionManager;
	}

	public void setStorePromotionManager(
			IStorePromotionManager storePromotionManager) {
		this.storePromotionManager = storePromotionManager;
	}


	public String getType_name() {
		return type_name;
	}


	public void setType_name(String type_name) {
		this.type_name = type_name;
	}


	public Double getMin_goods_amount() {
		return min_goods_amount;
	}


	public void setMin_goods_amount(Double min_goods_amount) {
		this.min_goods_amount = min_goods_amount;
	}


	public Double getType_money() {
		return type_money;
	}


	public void setType_money(Double type_money) {
		this.type_money = type_money;
	}


	public String getUseTimeStart() {
		return useTimeStart;
	}


	public void setUseTimeStart(String useTimeStart) {
		this.useTimeStart = useTimeStart;
	}


	public String getUseTimeEnd() {
		return useTimeEnd;
	}


	public void setUseTimeEnd(String useTimeEnd) {
		this.useTimeEnd = useTimeEnd;
	}


	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}


	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}


	public String getImg_bonus() {
		return img_bonus;
	}


	public void setImg_bonus(String img_bonus) {
		this.img_bonus = img_bonus;
	}

	public Integer getType_id() {
		return type_id;
	}

	public void setType_id(Integer type_id) {
		this.type_id = type_id;
	}

	public Integer getLimit_num() {
		return limit_num;
	}

	public void setLimit_num(Integer limit_num) {
		this.limit_num = limit_num;
	}

	public Integer getIs_given() {
		return is_given;
	}

	public void setIs_given(Integer is_given) {
		this.is_given = is_given;
	}

	public Integer getCreate_num() {
		return create_num;
	}

	public void setCreate_num(Integer create_num) {
		this.create_num = create_num;
	}

	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}


    
    public String getType_recognition() {
        return type_recognition;
    }


    
    public void setType_recognition(String type_recognition) {
        this.type_recognition = type_recognition;
    }


    
    public String getSendTimeStart() {
        return sendTimeStart;
    }


    
    public void setSendTimeStart(String sendTimeStart) {
        this.sendTimeStart = sendTimeStart;
    }


    
    public String getSendTimeEnd() {
        return sendTimeEnd;
    }


    
    public void setSendTimeEnd(String sendTimeEnd) {
        this.sendTimeEnd = sendTimeEnd;
    }
	
	
}
