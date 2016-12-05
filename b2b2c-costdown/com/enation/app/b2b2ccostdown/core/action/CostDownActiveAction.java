package com.enation.app.b2b2ccostdown.core.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.b2b2ccostdown.core.model.CostDownActive;
import com.enation.app.b2b2ccostdown.core.service.CostDownActiveManager;
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
	 @Result(name="list",type="freemarker", location="/groupbuy/costdown/act_list.html"),
	 @Result(name="add",type="freemarker", location="/groupbuy/costdown/act_add.html"),
	 @Result(name="edit",type="freemarker", location="/groupbuy/costdown/act_edit.html")
})
@Action("cost-down-act")
public class CostDownActiveAction extends WWAction{
    
	private CostDownActiveManager costDownActiveManager;
	private CostDownActive costDownActive;
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
		this.webpage=costDownActiveManager.groupBuyActive(this.getPage(), this.getPageSize(), map);
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
		groupbuyActStartTime=costDownActiveManager.getLastEndTime();
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
//	        //判断团购开始时间
//            if(groupbuyActStartTime>DateUtil.getDatelineTime(start_time)){
//                this.showErrorJson("添加活动失败:团购时间不得小于开启时间");
//                return this.JSON_MESSAGE;
//            }
            //判断团购报价截止时间
            long join_end_time_l = DateUtil.getDatelineTime(join_end_time);
            long start_time_l = DateUtil.getDatelineTime(start_time);
            long end_time_l = DateUtil.getDatelineTime(end_time);
            long ctime = System.currentTimeMillis() / 1000;
            if (start_time_l < ctime) start_time_l = ctime;
            if (join_end_time_l < ctime) join_end_time_l = ctime;
            if (end_time_l <= ctime) {
                this.showErrorJson("添加活动失败:活动结束时间必须大于当前时间");
                return this.JSON_MESSAGE;
            }
            if (end_time_l <= start_time_l) {
                this.showErrorJson("添加活动失败:活动结束时间必须大于开始时间");
                return this.JSON_MESSAGE;
            }
            if (join_end_time_l > end_time_l) {
                this.showErrorJson("添加活动失败:活动报名截止时间不得大于结束时间");
                return this.JSON_MESSAGE;
            }
            
            if (!costDownActiveManager.getValidActiveCheck(start_time_l, end_time_l)) {
                this.showErrorJson("添加活动失败:此时间段内已存在其他活动");
                return this.JSON_MESSAGE;
            }
            
			CostDownActive cda = new CostDownActive();
			cda.setAct_name(act_name);
			cda.setStart_time(start_time_l);
			cda.setEnd_time(end_time_l);
			cda.setJoin_end_time(join_end_time_l);
			costDownActiveManager.add(cda);
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
			this.costDownActiveManager.delete(act_id[0]);
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
		costDownActive=this.costDownActiveManager.get(act_id[0]);
		start_time=costDownActive.getStart_time_str();
		end_time=costDownActive.getEnd_time_str();
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
			CostDownActive cda = new CostDownActive();
			cda.setAct_id(act_id[0]);
			cda.setAct_name(act_name);
			cda.setStart_time( DateUtil.getDatelineTime(start_time)  );
			cda.setEnd_time( DateUtil.getDatelineTime(end_time)  );
			costDownActiveManager.update(cda);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败");
			this.logger.error("添加活动失败", e);
		}
		return this.JSON_MESSAGE;
	}
	
    public CostDownActiveManager getCostDownActiveManager() {
        return costDownActiveManager;
    }
    
    public void setCostDownActiveManager(CostDownActiveManager costDownActiveManager) {
        this.costDownActiveManager = costDownActiveManager;
    }
    
    public CostDownActive getCostDownActive() {
        return costDownActive;
    }
    
    public void setCostDownActive(CostDownActive costDownActive) {
        this.costDownActive = costDownActive;
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
