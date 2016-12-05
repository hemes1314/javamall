package com.enation.app.groupbuy.core.action.backend;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.groupbuy.core.model.GroupBuyActive;
import com.enation.app.groupbuy.core.service.IGroupBuyActiveManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;
/**
 * 
 * @ClassName: GroupBuyActiveAction 
 * @Description: 团购活动Action 
 * @author TALON 
 * @date 2015-7-31 上午1:12:05 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/groupbuy/groupbuyActive/act_list.html"),
	 @Result(name="add",type="freemarker", location="/groupbuy/groupbuyActive/act_add.html"),
	 @Result(name="edit",type="freemarker", location="/groupbuy/groupbuyActive/act_edit.html")
})

@Action("groupBuyAct")
public class GroupBuyActiveAction extends WWAction{
	private IGroupBuyActiveManager groupBuyActiveManager;
	private GroupBuyActive groupBuyActive;
	private Long groupbuyActStartTime;
	private Integer act_id[];
	private String act_name;
	private String start_time;
	private String end_time;
	private String join_end_time;
	
	/**
	 * 
	 * @Title: list
	 * @Description: 跳转至团购活动列表
	 * @return 团购活动页
	 */
	public String list(){
		return "list";
	}
	/**
	 * 
	 * @Title: listJson
	 * @Description: 团购活动列表Json
	 * @param 	map 团购活动搜索条件
	 * @param	webpage 团购活动分页列表
	 * @return  团购活动列表Json
	 */
	public String listJson(){
		Map map=new HashMap();
		this.webpage=groupBuyActiveManager.groupBuyActive(this.getPage(), this.getPageSize(), map);
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	/**
	 * 
	 * @Title: add
	 * @Description: 跳转到活动添加页
	 * @param groupbuyActStartTime 团购开启时间（同一时间不能开启多个团购活动）
	 * @return 团购活动添加页
	 */
	public String add(){
		groupbuyActStartTime=groupBuyActiveManager.getLastEndTime();
		return "add";
	}
	/**
	 * 
	 * @Title: saveAdd
	 * @Description: 添加团购活动
	 * @param groupBuyActive 团购活动
	 * @param act_name 活动名称
	 * @param start_time 开始时间
	 * @param end_time 结束时间
	 * @param join_end_time 活动报名截止时间
	 * @return json 1为成功.0为失败.
	 */
	public String saveAdd(){
		try {
	        //判断团购开始时间
            if(groupbuyActStartTime>DateUtil.getDatelineTime(start_time)){
                this.showErrorJson("添加活动失败:团购时间不得小于开启时间");
                return this.JSON_MESSAGE;
            }
            
			GroupBuyActive groupBuyActive = new GroupBuyActive();
			groupBuyActive.setAct_name(act_name);
			groupBuyActive.setStart_time( DateUtil.getDatelineTime(start_time)  );
			groupBuyActive.setEnd_time( DateUtil.getDatelineTime(end_time)  );
			groupBuyActive.setJoin_end_time(DateUtil.getDatelineTime(join_end_time));
			groupBuyActiveManager.add(groupBuyActive);
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
	 * @Description: 删除团购活动
	 * @param act_id 团购活动Id数组
	 * @return json 1为成功.0为失败.
	 */
	public String delete(){
		try {
			this.groupBuyActiveManager.delete(act_id[0]);
			this.showSuccessJson("删除成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 
	 * @Title: edit
	 * @Description: 跳转至团购活动修改页
	 * @param act_id 团购活动Id
	 * @param groupBuyActive 团购活动
	 * @param start_time 团购活动开启时间
	 * @param end_time 团购活动结束时间
	 * @return 团购活动修改页
	 */
	public String edit(){
		groupBuyActive=this.groupBuyActiveManager.get(act_id[0]);
		start_time=groupBuyActive.getStart_time_str();
		end_time=groupBuyActive.getEnd_time_str();
		return "edit";
	}
	/**
	 * @Title: saveEdit
	 * @Description: 保存修改团购活动
	 * @param cat_id 团购活动Id数组
	 * @param act_name 团购活动名称
	 * @param start_time 团购活动开始时间
	 * @param end_time 团购活动结束时间
	 * @param join_end_time 报名截止时间 
	 * @return json 1为成功.0为失败
	 */
	public String saveEdit(){
		try {
			GroupBuyActive groupBuyActive = new GroupBuyActive();
			groupBuyActive.setAct_id(act_id[0]);
			groupBuyActive.setAct_name(act_name);
			groupBuyActive.setStart_time( DateUtil.getDatelineTime(start_time)  );
			groupBuyActive.setEnd_time( DateUtil.getDatelineTime(end_time)  );
			groupBuyActiveManager.update(groupBuyActive);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败");
			this.logger.error("添加活动失败", e);
		}
		return this.JSON_MESSAGE;
	}
	public IGroupBuyActiveManager getGroupBuyActiveManager() {
		return groupBuyActiveManager;
	}
	public void setGroupBuyActiveManager(
			IGroupBuyActiveManager groupBuyActiveManager) {
		this.groupBuyActiveManager = groupBuyActiveManager;
	}
	public GroupBuyActive getGroupBuyActive() {
		return groupBuyActive;
	}
	public void setGroupBuyActive(GroupBuyActive groupBuyActive) {
		this.groupBuyActive = groupBuyActive;
	}
	public Long getGroupbuyActStartTime() {
		return groupbuyActStartTime;
	}
	public void setGroupbuyActStartTime(Long groupbuyActStartTime) {
		this.groupbuyActStartTime = groupbuyActStartTime;
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
