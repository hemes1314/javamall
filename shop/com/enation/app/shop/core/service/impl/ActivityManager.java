package com.enation.app.shop.core.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.Activity;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;

public class ActivityManager extends BaseSupport<Activity> {
    
    public void add(Activity activity) {
        this.baseDaoSupport.insert("activity", activity);
    }
    
    @Transactional
    public void delete(Integer id) {
        String sql = "delete from es_activity where id=?";
        this.baseDaoSupport.execute(sql, id);
        
        sql = "delete from es_activity_goods where activity_id=?";
        this.baseDaoSupport.execute(sql, id);
        
        sql = "delete from es_activity_gift where activity_id=?";
        this.baseDaoSupport.execute(sql, id);
    }
    
    public void edit(Activity activity) {
        this.baseDaoSupport.update("es_activity", activity, "ID="+ activity.getId());
    }
    
    public Activity get(Integer id) {
        String sql = "select * from activity where id=?";
        return this.baseDaoSupport.queryForObject(sql, Activity.class, id);
    }
    
    //boolean flag 平台为false 商家后台为true 做过滤无效的满减活动 chenzhongwei add
    public Page getPage(String name, String id, String startTime, String endTime, Integer status, int pageNo, int pageSize ,boolean flag) {
        String cond = "";
        
        if (name != null && !name.isEmpty()) {
            cond += "name like '%"+ name +"%'";
        }
        
        if (id != null && !id.isEmpty()) {
            if (cond.length() > 0) {
                cond += " and ";
            }
            
            cond += "id=" + id;
        }
        
        if (startTime != null && !startTime.isEmpty()) {
            Long startTimeLong = DateUtil.getDateline(startTime, "yyyy-MM-dd HH:mm:ss");
            
            if (cond.length() > 0) {
                cond += " and ";
            }
            
            cond += "start_time <= "+ startTimeLong;
        }
        
        if (endTime != null && !endTime.isEmpty()) {
            Long endTimeLong = DateUtil.getDateline(endTime, "yyyy-MM-dd HH:mm:ss");
            
            if (cond.length() > 0) {
                cond += " and ";
            }
            
            cond += "end_time >= "+ endTimeLong;
        }
        
        if (status != null && status != 0) {
            long currentTime = System.currentTimeMillis() / 1000;
            
            if (cond.length() > 0) {
                cond += " and ";
            }
            
            if (status == 1) {
                cond += "start_time > " + currentTime;
            }
            
            if (status == 2) {
               cond += "start_time <= " + currentTime + " and end_time >= " + currentTime + " and is_enable=1";
            }
            
            if (status == 3) {
                cond += "start_time <= " + currentTime + " and end_time >= " + currentTime + " and is_enable=0";
            }
            
            if (status == 4) {
                cond += "end_time < " + currentTime;
            }
        }
        
        if (cond.isEmpty()) {
            cond = "1=1";
        }
        
        if(flag) {
            cond+=" and is_enable=1";
        }
        
        String sql = "select * from activity where "+ cond +" order by id desc";
        Page page = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>)page.getResult();
        
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            Integer isEnable = (Integer)(map.get("is_enable"));
            String statusStr = this.getCurrentStatus(isEnable, (Long)(map.get("start_time")), (Long)(map.get("end_time")));
            map.put("status", statusStr);
            list.set(i, map);
        }
        page.setData(list);
        return page;
    }
    
    //获取当前状态
    public String getCurrentStatus(Integer isEnable, Long startTime, Long endTime) {
        String status = "";
        long currentTime = System.currentTimeMillis() / 1000;
        if (startTime > currentTime) {
            status = "未开始";
        } else if ((startTime <= currentTime) && (endTime >= currentTime) && (isEnable == 1)) {
            status = "进行中";
        } else if ((startTime <= currentTime) && (endTime >= currentTime) && (isEnable == 0)) {
            status = "暂停";
        } else if (endTime < currentTime) {
            status = "结束";
        }
        return status;
    }
    
    //获取当前正在进行的促销活动列表
    public List<Activity> getCurrentActivity() {
        long currentTime = System.currentTimeMillis() / 1000;
        String sql = "select * from es_activity where start_time <= "+ currentTime +" and end_time >= "+currentTime +" and is_enable=1";
        List<Activity> list = this.getBaseDaoSupport().queryForList(sql, Activity.class);
        return list;
    }


}
