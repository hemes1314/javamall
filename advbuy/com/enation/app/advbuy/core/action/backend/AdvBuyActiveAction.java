package com.enation.app.advbuy.core.action.backend;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.advbuy.core.model.AdvBuyActive;
import com.enation.app.advbuy.core.service.IAdvBuyActiveManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;
/**
 * 
 * @ClassName: AdvBuyActiveAction 
 * @Description: 预售活动Action 
 * @author TALON 
 * @date 2015-7-31 上午1:12:05 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/advbuy/advbuyActive/act_list.html"),
	 @Result(name="add",type="freemarker", location="/advbuy/advbuyActive/act_add.html"),
	 @Result(name="edit",type="freemarker", location="/advbuy/advbuyActive/act_edit.html")
})

@Action("advBuyAct")
public class AdvBuyActiveAction extends WWAction{
	private IAdvBuyActiveManager advBuyActiveManager;
	private AdvBuyActive advBuyActive;
	private Long advbuyActStartTime;
	private Integer act_id[];
	private String act_name;
	private String start_time;
	private String end_time;
	private String join_end_time;
	
	/**
	 * 
	 * @Title: list
	 * @Description: 跳转至预售活动列表
	 * @return 预售活动页
	 */
	public String list(){
		return "list";
	}
	/**
	 * 
	 * @Title: listJson
	 * @Description: 预售活动列表Json
	 * @param 	map 预售活动搜索条件
	 * @param	webpage 预售活动分页列表
	 * @return  预售活动列表Json
	 */
	public String listJson(){
		Map map=new HashMap();
		this.webpage=advBuyActiveManager.advBuyActive(this.getPage(), this.getPageSize(), map);
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	/**
	 * 
	 * @Title: add
	 * @Description: 跳转到活动添加页
	 * @param advbuyActStartTime 预售开启时间（同一时间不能开启多个预售活动）
	 * @return 预售活动添加页
	 */
	public String add(){
		advbuyActStartTime=advBuyActiveManager.getLastEndTime();
		return "add";
	}
	/**
	 * 
	 * @Title: saveAdd
	 * @Description: 添加预售活动
	 * @param advBuyActive 预售活动
	 * @param act_name 活动名称
	 * @param start_time 开始时间
	 * @param end_time 结束时间
	 * @param join_end_time 活动报名截止时间
	 * @return json 1为成功.0为失败.
	 */
	public String saveAdd(){
		try {
		    //判断开始时间
            if(advbuyActStartTime>DateUtil.getDatelineTime(start_time)){
                this.showErrorJson("添加活动失败:活动时间不得小于开启时间");
                return this.JSON_MESSAGE;
            }
			AdvBuyActive advBuyActive = new AdvBuyActive();
			advBuyActive.setAct_name(act_name);
			advBuyActive.setStart_time( DateUtil.getDatelineTime(start_time)  );
			advBuyActive.setEnd_time( DateUtil.getDatelineTime(end_time)  );
			advBuyActive.setJoin_end_time(DateUtil.getDatelineTime(join_end_time));
			advBuyActiveManager.add(advBuyActive);
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
	 * @Description: 删除预售活动
	 * @param act_id 预售活动Id数组
	 * @return json 1为成功.0为失败.
	 */
	public String delete(){
		try {
			this.advBuyActiveManager.delete(act_id[0]);
			this.showSuccessJson("删除成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 
	 * @Title: edit
	 * @Description: 跳转至预售活动修改页
	 * @param act_id 预售活动Id
	 * @param advBuyActive 预售活动
	 * @param start_time 预售活动开启时间
	 * @param end_time 预售活动结束时间
	 * @return 预售活动修改页
	 */
	public String edit(){
		advBuyActive=this.advBuyActiveManager.get(act_id[0]);
		start_time=advBuyActive.getStart_time_str();
		end_time=advBuyActive.getEnd_time_str();
		return "edit";
	}
	/**
	 * @Title: saveEdit
	 * @Description: 保存修改预售活动
	 * @param cat_id 预售活动Id数组
	 * @param act_name 预售活动名称
	 * @param start_time 预售活动开始时间
	 * @param end_time 预售活动结束时间
	 * @param join_end_time 报名截止时间 
	 * @return json 1为成功.0为失败
	 */
	public String saveEdit(){
		try {
			AdvBuyActive advBuyActive = new AdvBuyActive();
			advBuyActive.setAct_id(act_id[0]);
			advBuyActive.setAct_name(act_name);
			advBuyActive.setStart_time( DateUtil.getDatelineTime(start_time)  );
			advBuyActive.setEnd_time( DateUtil.getDatelineTime(end_time)  );
			advBuyActiveManager.update(advBuyActive);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败");
			this.logger.error("添加活动失败", e);
		}
		return this.JSON_MESSAGE;
	}
	public IAdvBuyActiveManager getAdvBuyActiveManager() {
		return advBuyActiveManager;
	}
	public void setAdvBuyActiveManager(
			IAdvBuyActiveManager advBuyActiveManager) {
		this.advBuyActiveManager = advBuyActiveManager;
	}
	public AdvBuyActive getAdvBuyActive() {
		return advBuyActive;
	}
	public void setAdvBuyActive(AdvBuyActive advBuyActive) {
		this.advBuyActive = advBuyActive;
	}
	public Long getAdvbuyActStartTime() {
		return advbuyActStartTime;
	}
	public void setAdvbuyActStartTime(Long advbuyActStartTime) {
		this.advbuyActStartTime = advbuyActStartTime;
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
