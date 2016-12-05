package com.enation.app.shop.component.bonus.action;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 红包管理action
 * @author kingapex
 *2013-8-18上午11:32:35
 */
@SuppressWarnings({ "rawtypes", "serial" })
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Results({
	@Result(name="send_for_member", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/send_for_member.html") ,
	@Result(name="send_for_goods", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/send_for_goods.html") ,
	@Result(name="send_for_order", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/send_for_order.html") ,
	@Result(name="send_for_offline", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/send_for_offline.html") ,
	@Result(name="list", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_list.html") ,
	@Result(name="edit", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_edit.html") 
})
public class BonusAction extends WWAction {
	private IBonusManager bonusManager;
	private IBonusTypeManager bonusTypeManager;
	private IMemberLvManager memberLvManager;
	private int typeid;
	private int bonusid;
	private Long[] memberids;
	private Integer[] goodsids;
	private int send_type;
	private List lvList;
	
	/**
	 * 
	 * @return
	 */
	public String send(){
		BonusType bonusType =this.bonusTypeManager.get(typeid);
		send_type = bonusType.getSend_type();
		String result="";
		switch (this.send_type) {
		case 0:
			this.lvList = this.memberLvManager.list();
			result="send_for_member";
			break;
		case 1:
			result="send_for_goods";
			break;	
		case 2:
			result="send_for_order";
			break;	
		case 3:
			result="send_for_offline";
			break;	
		default:
			result="send_for_member";
			break;
		}
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	public String sendForMemberLv(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		int lvid  = StringUtil.toInt(request.getParameter("lvid"),0);
		
		if(lvid==0){
			this.showErrorJson("必须选择会员级别");
			return JSON_MESSAGE;
		}
		
		BonusType bonus = bonusTypeManager.get(typeid);
		if (bonus == null) {
			this.showErrorJson("您输入的红包编号不正确");
			return JSON_MESSAGE;
		}

		long now = DateUtil.getDateline();
		if (bonus.getSend_start_date() > now) {
			long l = Long.valueOf(bonus.getSend_start_date()) * 1000;
			this.showErrorJson("此红包还未到使用期，开始使用时间为[" + DateUtil.toString(new Date(l), "yyyy年MM月dd日") + "]");
			return JSON_MESSAGE;
		}

		if (bonus.getSend_end_date() < now) {
			long l = Long.valueOf(bonus.getSend_end_date()) * 1000;
			this.showErrorJson("此红包已过期，使用截至时间为[" + DateUtil.toString(new Date(l), "yyyy年MM月dd日") + "]");
			return JSON_MESSAGE;
		}
		
		int onlyEmailChecked= StringUtil.toInt(request.getParameter("onlyEmailChecked"),0);
		
		try {
			int count =bonusManager.sendForMemberLv(typeid, lvid, onlyEmailChecked);
			this.json = JsonMessageUtil.getNumberJson("count", count);
		} catch (Exception e) {
			this.logger.error("发放红包出错", e);
			this.showErrorJson("发放红包出错["+e.getMessage()+"]");
		}
		
		return JSON_MESSAGE;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String sendForMember(){
		try {
			BonusType bonus = bonusTypeManager.get(typeid);
			if (bonus == null) {
				this.showErrorJson("您输入的红包编号不正确");
				return JSON_MESSAGE;
			}

			long now = DateUtil.getDateline();
			if (bonus.getSend_start_date() > now) {
				long l = Long.valueOf(bonus.getSend_start_date()) * 1000;
				this.showErrorJson("此红包还未到使用期，开始使用时间为[" + DateUtil.toString(new Date(l), "yyyy年MM月dd日") + "]");
				return JSON_MESSAGE;
			}

			if (bonus.getSend_end_date() < now) {
				long l = Long.valueOf(bonus.getSend_end_date()) * 1000;
				this.showErrorJson("此红包已过期，使用截至时间为[" + DateUtil.toString(new Date(l), "yyyy年MM月dd日") + "]");
				return JSON_MESSAGE;
			}
			
			int count =this.bonusManager.sendForMember(typeid, memberids);
			this.json = JsonMessageUtil.getNumberJson("count", count);
		} catch (Exception e) {
			this.logger.error("发放红包出错", e);
			this.showErrorJson("发放红包出错["+e.getMessage()+"]");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 
	 * @return
	 */
	public String sendForGoods(){
		try {
			int count = this.bonusManager.sendForGoods(typeid, goodsids);
			this.json = JsonMessageUtil.getNumberJson("count", count);
		} catch (Exception e) {
			this.logger.error("发放红包出错", e);
			this.showErrorJson("发放红包出错["+e.getMessage()+"]");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 
	 * @return
	 */
	public String sendForOffLine(){
		try {
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			int createnum = StringUtil.toInt(request.getParameter("createnum"),0); 
			int count =this.bonusManager.sendForOffLine(typeid, createnum);
			this.json = JsonMessageUtil.getNumberJson("count", count);
		} catch (Exception e) {
			this.logger.error("发放红包出错", e);
			this.showErrorJson("发放红包出错["+e.getMessage()+"]");
		}
		return JSON_MESSAGE;
	}
	
	
	/**
	 * 读取某类型的红包列表
	 * @return
	 */
	public String list(){
		return "list";
	}
	
	public String listJson(){
		this.webpage =this.bonusManager.list(this.getPage(), this.getPageSize(), typeid);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	
	/**
	 * 移除一个红包
	 * @return
	 */
	public String delete(){
		try {
			this.bonusManager.delete(bonusid);
			this.showSuccessJson("删除成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败【"+e.getMessage()+"】");
		}
		return JSON_MESSAGE;
	}

	
	/**
	 * 重新发送邮件
	 * @return
	 */
	public String reSendMail(){
		return JSON_MESSAGE;
	}
	
	public String getGoodsList(){
		try {
			List<Map> goodsList  =this.bonusManager.getGoodsList(typeid);
			this.json= JsonMessageUtil.getListJson(goodsList);
			
		} catch (Exception e) {
			this.logger.error("获取已绑定商品出错",e);
			 this.showErrorJson("获取已绑定商品出错");
		}
		return JSON_MESSAGE;
	}
	
	private String excelPath;
	private String filename;
	
	public String exportExcel(){
		BonusType bonusType = bonusTypeManager.get(typeid);
		filename=bonusType.getType_name()+"红包列表.xls";
		excelPath=this.bonusManager.exportToExcel(typeid);
		return "download";
	}
	
 
 
    public InputStream getInputStream() {
    	
    	if(StringUtil.isEmpty(excelPath)) return null;
    	
    	InputStream in =null;
    	try {
			in = new java.io.FileInputStream(excelPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	 return in;
    }
    
    public String getFileName() {   
    	  
        String downFileName = filename;
  
        try {   
  
            downFileName = new String(downFileName.getBytes(), "ISO8859-1");   
  
        } catch (UnsupportedEncodingException e) {   
  
            e.printStackTrace();   
  
        }   
  
        return downFileName;   
  
    }   
    
   
    public String execute(){
            return "success";
    }
	
	
	public int getTypeid() {
		return typeid;
	}

	public void setTypeid(int typeid) {
		this.typeid = typeid;
	}

	public Long[] getMemberids() {
		return memberids;
	}

	public void setMemberids(Long[] memberids) {
		this.memberids = memberids;
	}


	public Integer[] getGoodsids() {
		return goodsids;
	}

	public void setGoodsids(Integer[] goodsids) {
		this.goodsids = goodsids;
	}

	public int getSend_type() {
		return send_type;
	}

	public void setSend_type(int send_type) {
		this.send_type = send_type;
	}

	public IBonusManager getBonusManager() {
		return bonusManager;
	}

	public void setBonusManager(IBonusManager bonusManager) {
		this.bonusManager = bonusManager;
	}

	public List getLvList() {
		return lvList;
	}

	public void setLvList(List lvList) {
		this.lvList = lvList;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	public int getBonusid() {
		return bonusid;
	}

	public void setBonusid(int bonusid) {
		this.bonusid = bonusid;
	}

	public IBonusTypeManager getBonusTypeManager() {
		return bonusTypeManager;
	}

	public void setBonusTypeManager(IBonusTypeManager bonusTypeManager) {
		this.bonusTypeManager = bonusTypeManager;
	}

	
}
