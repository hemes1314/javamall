package com.enation.app.secbuy.core.action.backend;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.secbuy.core.model.SecBuyActive;
import com.enation.app.secbuy.core.service.ISecBuyActiveManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;
/**
 * 
 * @ClassName: SecBuyActiveAction 
 * @Description: 秒拍活动Action 
 * @author TALON 
 * @date 2015-7-31 上午1:12:05 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/secbuy/secbuyActive/act_list.html"),
	 @Result(name="add",type="freemarker", location="/secbuy/secbuyActive/act_add.html"),
	 @Result(name="edit",type="freemarker", location="/secbuy/secbuyActive/act_edit.html")
})

@Action("secBuyAct")
public class SecBuyActiveAction extends WWAction{
	private ISecBuyActiveManager secBuyActiveManager;
	private SecBuyActive secBuyActive;
	private Long secbuyActStartTime;
	private Integer act_id[];
	private String act_name;
	private String start_time;
	private String end_time;
	private String join_end_time;
	
	/**
	 * 
	 * @Title: list
	 * @Description: 跳转至秒拍活动列表
	 * @return 秒拍活动页
	 */
	public String list(){
		return "list";
	}
	/**
	 * 
	 * @Title: listJson
	 * @Description: 秒拍活动列表Json
	 * @param 	map 秒拍活动搜索条件
	 * @param	webpage 秒拍活动分页列表
	 * @return  秒拍活动列表Json
	 */
	public String listJson(){
		Map map=new HashMap();
		this.webpage=secBuyActiveManager.secBuyActive(this.getPage(), this.getPageSize(), map);
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	/**
	 * 
	 * @Title: add
	 * @Description: 跳转到活动添加页
	 * @param secbuyActStartTime 秒拍开启时间（同一时间不能开启多个秒拍活动）
	 * @return 秒拍活动添加页
	 */
	public String add(){
		secbuyActStartTime=secBuyActiveManager.getLastEndTime();
		return "add";
	}
	/**
	 * 
	 * @Title: saveAdd
	 * @Description: 添加秒拍活动
	 * @param secBuyActive 秒拍活动
	 * @param act_name 活动名称
	 * @param start_time 开始时间
	 * @param end_time 结束时间
	 * @param join_end_time 活动报名截止时间
	 * @return json 1为成功.0为失败.
	 */
	public String saveAdd(){
		try {
		    //判断开始时间
            if(secbuyActStartTime>DateUtil.getDatelineTime(start_time)){
                this.showErrorJson("添加活动失败:活动时间不得小于开启时间");
                return this.JSON_MESSAGE;
            }
            
			SecBuyActive secBuyActive = new SecBuyActive();
			secBuyActive.setAct_name(act_name);
			secBuyActive.setStart_time( DateUtil.getDatelineTime(start_time)  );
			secBuyActive.setEnd_time( DateUtil.getDatelineTime(end_time)  );
			secBuyActive.setJoin_end_time(DateUtil.getDatelineTime(join_end_time));
			secBuyActiveManager.add(secBuyActive);
			this.showSuccessJson("添加活动成功");
		} catch (Exception e) {
			this.showErrorJson("添加活动失败"+e.getMessage());
			this.logger.error("添加活动失败", e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 
	 * @Title: delete
	 * @Description: 删除秒拍活动
	 * @param act_id 秒拍活动Id数组
	 * @return json 1为成功.0为失败.
	 */
	public String delete(){
		try {
			this.secBuyActiveManager.delete(act_id[0]);
			this.showSuccessJson("删除成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 
	 * @Title: edit
	 * @Description: 跳转至秒拍活动修改页
	 * @param act_id 秒拍活动Id
	 * @param secBuyActive 秒拍活动
	 * @param start_time 秒拍活动开启时间
	 * @param end_time 秒拍活动结束时间
	 * @return 秒拍活动修改页
	 */
	public String edit(){
		secBuyActive=this.secBuyActiveManager.get(act_id[0]);
		start_time=secBuyActive.getStart_time_str();
		end_time=secBuyActive.getEnd_time_str();
		return "edit";
	}
	/**
	 * @Title: saveEdit
	 * @Description: 保存修改秒拍活动
	 * @param cat_id 秒拍活动Id数组
	 * @param act_name 秒拍活动名称
	 * @param start_time 秒拍活动开始时间
	 * @param end_time 秒拍活动结束时间
	 * @param join_end_time 报名截止时间 
	 * @return json 1为成功.0为失败
	 */
	public String saveEdit(){
		try {
			SecBuyActive secBuyActive = new SecBuyActive();
			secBuyActive.setAct_id(act_id[0]);
			secBuyActive.setAct_name(act_name);
			secBuyActive.setStart_time( DateUtil.getDatelineTime(start_time)  );
			secBuyActive.setEnd_time( DateUtil.getDatelineTime(end_time)  );
			secBuyActiveManager.update(secBuyActive);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败");
			this.logger.error("添加活动失败", e);
		}
		return this.JSON_MESSAGE;
	}
	public ISecBuyActiveManager getSecBuyActiveManager() {
		return secBuyActiveManager;
	}
	public void setSecBuyActiveManager(
			ISecBuyActiveManager secBuyActiveManager) {
		this.secBuyActiveManager = secBuyActiveManager;
	}
	public SecBuyActive getSecBuyActive() {
		return secBuyActive;
	}
	public void setSecBuyActive(SecBuyActive secBuyActive) {
		this.secBuyActive = secBuyActive;
	}
	public Long getSecbuyActStartTime() {
		return secbuyActStartTime;
	}
	public void setSecbuyActStartTime(Long secbuyActStartTime) {
		this.secbuyActStartTime = secbuyActStartTime;
	}
	public Integer[] getAct_id() {
		return act_id;
	}
	public void setAct_id(Integer[] act_id) {
		this.act_id = act_id;
	}
	public String getAct_name() {
		return act_name;
	}
	public void setAct_name(String act_name) {
		this.act_name = act_name;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getJoin_end_time() {
		return join_end_time;
	}
	public void setJoin_end_time(String join_end_time) {
		this.join_end_time = join_end_time;
	}
}
