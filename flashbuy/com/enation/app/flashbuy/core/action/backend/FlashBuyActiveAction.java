package com.enation.app.flashbuy.core.action.backend;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.flashbuy.core.model.FlashBuyActive;
import com.enation.app.flashbuy.core.service.IFlashBuyActiveManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;
/**
 * 
 * @ClassName: FlashBuyActiveAction 
 * @Description: 限时抢购活动Action 
 * @author TALON 
 * @date 2015-7-31 上午1:12:05 
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/flashbuy/flashbuyActive/act_list.html"),
	 @Result(name="add",type="freemarker", location="/flashbuy/flashbuyActive/act_add.html"),
	 @Result(name="edit",type="freemarker", location="/flashbuy/flashbuyActive/act_edit.html")
})

@Action("flashBuyAct")
public class FlashBuyActiveAction extends WWAction{
	private IFlashBuyActiveManager flashBuyActiveManager;
	private FlashBuyActive flashBuyActive;
	private Long flashbuyActStartTime;
	private Integer act_id[];
	private String act_name;
	private String start_time;
	private String end_time;
	private String join_end_time;
	
	/**
	 * 
	 * @Title: list
	 * @Description: 跳转至限时抢购活动列表
	 * @return 限时抢购活动页
	 */
	public String list(){
		return "list";
	}
	/**
	 * 
	 * @Title: listJson
	 * @Description: 限时抢购活动列表Json
	 * @param 	map 限时抢购活动搜索条件
	 * @param	webpage 限时抢购活动分页列表
	 * @return  限时抢购活动列表Json
	 */
	public String listJson(){
		Map map=new HashMap();
		this.webpage=flashBuyActiveManager.flashBuyActive(this.getPage(), this.getPageSize(), map);
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	/**
	 * 
	 * @Title: add
	 * @Description: 跳转到活动添加页
	 * @param flashbuyActStartTime 限时抢购开启时间（同一时间不能开启多个限时抢购活动）
	 * @return 限时抢购活动添加页
	 */
	public String add(){
		flashbuyActStartTime=flashBuyActiveManager.getLastEndTime();
		return "add";
	}
	/**
	 * 
	 * @Title: saveAdd
	 * @Description: 添加限时抢购活动
	 * @param flashBuyActive 限时抢购活动
	 * @param act_name 活动名称
	 * @param start_time 开始时间
	 * @param end_time 结束时间
	 * @param join_end_time 活动报名截止时间
	 * @return json 1为成功.0为失败.
	 */
	public String saveAdd(){
		try {
		    //判断开始时间
            if(flashbuyActStartTime>DateUtil.getDatelineTime(start_time)){
                this.showErrorJson("添加活动失败:活动时间不得小于开启时间");
                return this.JSON_MESSAGE;
            }
            
			FlashBuyActive flashBuyActive = new FlashBuyActive();
			flashBuyActive.setAct_name(act_name);
			flashBuyActive.setStart_time( DateUtil.getDatelineTime(start_time)  );
			flashBuyActive.setEnd_time( DateUtil.getDatelineTime(end_time)  );
			flashBuyActive.setJoin_end_time(DateUtil.getDatelineTime(join_end_time));
			flashBuyActiveManager.add(flashBuyActive);
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
	 * @Description: 删除限时抢购活动
	 * @param act_id 限时抢购活动Id数组
	 * @return json 1为成功.0为失败.
	 */
	public String delete(){
		try {
			this.flashBuyActiveManager.delete(act_id[0]);
			this.showSuccessJson("删除成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 
	 * @Title: edit
	 * @Description: 跳转至限时抢购活动修改页
	 * @param act_id 限时抢购活动Id
	 * @param flashBuyActive 限时抢购活动
	 * @param start_time 限时抢购活动开启时间
	 * @param end_time 限时抢购活动结束时间
	 * @return 限时抢购活动修改页
	 */
	public String edit(){
		flashBuyActive=this.flashBuyActiveManager.get(act_id[0]);
		start_time=flashBuyActive.getStart_time_str();
		end_time=flashBuyActive.getEnd_time_str();
		return "edit";
	}
	/**
	 * @Title: saveEdit
	 * @Description: 保存修改限时抢购活动
	 * @param cat_id 限时抢购活动Id数组
	 * @param act_name 限时抢购活动名称
	 * @param start_time 限时抢购活动开始时间
	 * @param end_time 限时抢购活动结束时间
	 * @param join_end_time 报名截止时间 
	 * @return json 1为成功.0为失败
	 */
	public String saveEdit(){
		try {
			FlashBuyActive flashBuyActive = new FlashBuyActive();
			flashBuyActive.setAct_id(act_id[0]);
			flashBuyActive.setAct_name(act_name);
			flashBuyActive.setStart_time( DateUtil.getDatelineTime(start_time)  );
			flashBuyActive.setEnd_time( DateUtil.getDatelineTime(end_time)  );
			flashBuyActiveManager.update(flashBuyActive);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败");
			this.logger.error("添加活动失败", e);
		}
		return this.JSON_MESSAGE;
	}
	public IFlashBuyActiveManager getFlashBuyActiveManager() {
		return flashBuyActiveManager;
	}
	public void setFlashBuyActiveManager(
			IFlashBuyActiveManager flashBuyActiveManager) {
		this.flashBuyActiveManager = flashBuyActiveManager;
	}
	public FlashBuyActive getFlashBuyActive() {
		return flashBuyActive;
	}
	public void setFlashBuyActive(FlashBuyActive flashBuyActive) {
		this.flashBuyActive = flashBuyActive;
	}
	public Long getFlashbuyActStartTime() {
		return flashbuyActStartTime;
	}
	public void setFlashbuyActStartTime(Long flashbuyActStartTime) {
		this.flashbuyActStartTime = flashbuyActStartTime;
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
