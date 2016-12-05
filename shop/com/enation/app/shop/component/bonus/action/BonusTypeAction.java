package com.enation.app.shop.component.bonus.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.b2b2c.core.model.StoreBonus;
import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 优惠卷类型管理
 * @author kingapex
 *2013-8-17下午12:02:39
 */
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_type_list.html") ,
	 @Result(name="add", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_type_add.html"),
	 @Result(name="edit", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_type_edit.html") 
})
public class BonusTypeAction extends WWAction {
	
	private IBonusTypeManager bonusTypeManager;
	private StoreBonus bonusType;
	private String useTimeStart;
	private String useTimeEnd;
	private String sendTimeStart;
	private String sendTimeEnd;
	private int typeid;
	private Integer[] type_id;
	private String recognition; //红包识别码 chenzhongwei add
	
	public String list(){
		return "list";
	}
	
	public String listJson(){
		this.webpage = this.bonusTypeManager.list(this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	
	public String add(){
		
		return "add";
	}

	
	public String edit(){

		this.bonusType = this.bonusTypeManager.get(typeid);
		return "edit";
	}
	
	public String saveAdd(){
		
		if( StringUtil.isEmpty( bonusType.getRecognition() )){
			this.showErrorJson("请输入优惠卷识别码");
			return this.JSON_MESSAGE;
		}
		
		if( StringUtil.isEmpty( bonusType.getType_name() )){
			this.showErrorJson("请输入类型名称");
			return this.JSON_MESSAGE;
		}
		
		
		if( bonusType.getType_money() ==null  ){
			this.showErrorJson("请输入金额");
			return this.JSON_MESSAGE;
		}
		
		//金额不能为负数  chenzhongwei add
        if(bonusType.getType_money() < 0) {
            this.showErrorJson("金额不能为负数");
            return this.JSON_MESSAGE;
        }
		
		if( StringUtil.isEmpty(useTimeStart)){
			this.showErrorJson("请输入使用起始日期");
			return this.JSON_MESSAGE;
		}
		bonusType.setUse_start_date(DateUtil.getDateline(useTimeStart));
		
		if( StringUtil.isEmpty(useTimeEnd)){
			this.showErrorJson("请输入使用结束日期");
			return this.JSON_MESSAGE;
		}
		
		//校验红包类型重复 chenzhongwei add
        boolean flag = this.bonusTypeManager.isExistRecognition(bonusType.getRecognition());
        if(flag) {
            this.showErrorJson("红包识别码不能重复");
            return this.JSON_MESSAGE;
        }
        
		bonusType.setUse_end_date( DateUtil.getMaxDateline(useTimeEnd));
		
		if(!StringUtil.isEmpty(sendTimeStart)){
			bonusType.setSend_start_date(DateUtil.getDateline(sendTimeStart));
		}
		
		if(!StringUtil.isEmpty(sendTimeEnd)){
			bonusType.setSend_end_date(DateUtil.getMaxDateline(sendTimeEnd));
		}
		
		bonusType.setBelong(1);
		try {
			this.bonusTypeManager.add(bonusType);
			this.showSuccessJson("保存优惠卷类型成功");
		} catch (Throwable e) {
			this.logger.error("保存优惠卷类型出错", e);
			this.showErrorJson("保存优惠卷类型出错"+e.getMessage());
		}

		
		return this.JSON_MESSAGE;
	}
	
	
	public String saveEdit(){
	   
	    StoreBonus oldType = this.bonusTypeManager.get(bonusType.getType_id());
	    
	      if(oldType.getCreate_num()>0){
              this.showErrorJson("该红包已发放,不能修改");
              return this.JSON_MESSAGE;
      
	      }
          

	    //chenzhongwei add
	    bonusType.setRecognition(recognition);
		
		if( StringUtil.isEmpty( bonusType.getType_name() )){
			this.showErrorJson("请输入类型名称");
			return this.JSON_MESSAGE;
		}
		
		
		if( bonusType.getType_money() ==null  ){
			this.showErrorJson("请输入金额");
			return this.JSON_MESSAGE;
		}
		
		//金额不能为负数  chenzhongwei add
        if(bonusType.getType_money() < 0) {
            this.showErrorJson("金额不能为负数");
            return this.JSON_MESSAGE;
        }
		
		if( StringUtil.isEmpty(this.useTimeStart)){
			this.showErrorJson("请输入使用起始日期");
			return this.JSON_MESSAGE;
		}
		bonusType.setUse_start_date(  DateUtil.getDateline(useTimeStart));
		
		if( StringUtil.isEmpty(this.useTimeEnd)){
			this.showErrorJson("请输入使用结束日期");
			return this.JSON_MESSAGE;
		}
		bonusType.setUse_end_date( DateUtil.getDateline(useTimeEnd));
		
		if(!StringUtil.isEmpty(sendTimeStart)){
			bonusType.setSend_start_date(DateUtil.getDateline(sendTimeStart));
		}
		
		if(!StringUtil.isEmpty(sendTimeEnd)){
			bonusType.setSend_end_date(DateUtil.getDateline(sendTimeEnd));
		}
		
		bonusType.setBelong(1);
		try {
			bonusTypeManager.update(bonusType);
			this.showSuccessJson("保存优惠卷类型成功");
		} catch (Throwable e) {
			this.logger.error("保存优惠卷类型出错", e);
			this.showErrorJson("保存优惠卷类型出错"+e.getMessage());
		}
		
		
		return this.JSON_MESSAGE;
	}
	
	
	public String delete(){
		
		try {
			this.bonusTypeManager.delete(type_id);
			this.showSuccessJson("删除优惠卷类型成功");
		} catch (Throwable e) {
			this.logger.error("删除优惠卷类型出错", e);
			this.showErrorJson("删除优惠卷类型出错"+e.getMessage());
		}
		
		return this.JSON_MESSAGE;
	}


	public IBonusTypeManager getBonusTypeManager() {
		return bonusTypeManager;
	}


	public void setBonusTypeManager(IBonusTypeManager bonusTypeManager) {
		this.bonusTypeManager = bonusTypeManager;
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


	public int getTypeid() {
		return typeid;
	}


	public void setTypeid(int typeid) {
		this.typeid = typeid;
	}


	public StoreBonus getBonusType() {
		return bonusType;
	}


	public void setBonusType(StoreBonus bonusType) {
		this.bonusType = bonusType;
	}

	public Integer[] getType_id() {
		return type_id;
	}

	public void setType_id(Integer[] type_id) {
		this.type_id = type_id;
	}

    
    public String getRecognition() {
        return recognition;
    }

    
    public void setRecognition(String recognition) {
        this.recognition = recognition;
    }

}
