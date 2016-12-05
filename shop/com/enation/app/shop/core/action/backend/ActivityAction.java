package com.enation.app.shop.core.action.backend;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Activity;
import com.enation.app.shop.core.service.impl.ActivityManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("activity")
@Results({ @Result(name = "list", type = "freemarker", location = "/shop/admin/activity/activity_list.jsp"),
        @Result(name = "edit", type = "freemarker", location = "/shop/admin/activity/activity_edit.jsp"),
        @Result(name = "add", type = "freemarker", location = "/shop/admin/activity/activity_add.jsp"), })
public class ActivityAction extends WWAction {

    private static final long serialVersionUID = 5492614969942085075L;

    @Autowired
    private ActivityManager activityManager;

    private String name;

    private String id;

    private String startTime;

    private String endTime;

    private Integer status;

    private Activity activity;

    private String start_time;

    private String end_time;

    //满减规则 需处理
    private String[] prInputs;

    public String list() {
        return "list";
    }

    public String listJson() {
        this.webpage = activityManager.getPage(name, id, startTime, endTime, status, page, rows,false);
        this.showGridJson(webpage);
        return this.JSON_MESSAGE;
    }

    public String add() {
        return "add";
    }

    public String edit() {
        int activityId = NumberUtils.toInt(ThreadContextHolder.getHttpRequest().getParameter("activityId"));
        activity = activityManager.get(activityId);
        return "edit";
    }

    /**
     * 检查输入项
     * 其中 偶数位置的是减下的金额
     * @param prInputs
     * @return
     */
    private boolean checkPrInputs(String[] prInputs){
        
        for(int i=0;i<prInputs.length;i++){
            if(i%2!=0){
                
                boolean flg = prInputs[i].indexOf(".")!=-1?true:false;
                if(flg){
                    return flg;
                }
            }
        }
        
        return false;
    }
    
    public String saveActivity() {
        long nowLong = DateUtil
                .getDateline(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss");
        activity.setStart_time(DateUtil.getDateline(start_time, "yyyy-MM-dd HH:mm:ss"));
        activity.setEnd_time(DateUtil.getDateline(end_time, "yyyy-MM-dd HH:mm:ss"));
        
        boolean flg = checkPrInputs(prInputs);
        if(flg){
            this.showErrorJson("操作失败,满减的金额必须为整数");
            return this.JSON_MESSAGE;
        }
        
        //校验结束时间大于当前时间 chenzhongwei add
        if(activity.getEnd_time() > nowLong) {
            activity.setPromotionRules(prInputs);
            if(activity.getId() == null || activity.getId() == 0) {
                activityManager.add(activity);
                this.showSuccessJson("促销活动添加成功");
            } else {
                activityManager.edit(activity);
                this.showSuccessJson("促销活动修改成功");
            }
        } else {
            this.showErrorJson("结束时间不能小于当前时间");
        }
        return this.JSON_MESSAGE;
    }

    public String delete() {
        int activityId = NumberUtils.toInt(ThreadContextHolder.getHttpRequest().getParameter("activityId"));
        activityManager.delete(activityId);
        this.showSuccessJson("删除促销活动成功");
        return this.JSON_MESSAGE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
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

    public String[] getPrInputs() {
        return prInputs;
    }

    public void setPrInputs(String[] prInputs) {
        this.prInputs = prInputs;
    }

}
