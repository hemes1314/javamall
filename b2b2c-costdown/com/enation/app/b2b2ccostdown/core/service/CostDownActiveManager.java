package com.enation.app.b2b2ccostdown.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2ccostdown.core.model.CostDownActive;
import com.enation.app.groupbuy.component.plugin.GroupbuyPluginBundle;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 直降活动
 * 
 * @author Jeffrey
 *
 */

@Component
public class CostDownActiveManager {

    private IDaoSupport daoSupport;

    private GroupbuyPluginBundle groupbuyPluginBundle;

    public Page groupBuyActive(Integer pageNo, Integer pageSize, Map map) {
        String sql = "select * from es_cost_down_active order by add_time desc";
        return this.daoSupport.queryForPage(sql, pageNo, pageSize, CostDownActive.class);
    }
    
    /**
     * 检查指定时间范围内是否存在其他活动.
     * 
     * @param stime 当前要添加的活动开始时间
     * @param etime 当前要添加的活动结束时间
     * @return true-不存在其他活动时、false-存在其他活动
     */
    public boolean getValidActiveCheck(long stime, long etime) {
        String sql = "select count(act_id) from es_cost_down_active where start_time between ? and ? or end_time between ? and ?";
        int count = this.daoSupport.queryForInt(sql, stime, etime, stime, etime);
        return count == 0;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void add(CostDownActive cba) {

        cba.setAdd_time(DateUtil.getDateline());

        //为了演示添加的代码：如果团购活动开启时间超过当前时间则开启团购.
        if(cba.getEnd_time() < DateUtil.getDateline()) {
            cba.setAct_status(2);
        } else {
            if(cba.getStart_time() < DateUtil.getDateline()) {
                cba.setAct_status(1);
            } else {
                cba.setAct_status(0);
            }
        }
        this.daoSupport.insert("es_cost_down_active", cba);
        //判断是否开启团购活动
        cba.setAct_id(this.daoSupport.getLastId("es_cost_down_active"));
//        this.groupbuyPluginBundle.onGroupBuyAdd(cba);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void update(CostDownActive cba) {
        this.daoSupport.update("es_cost_down_active", cba, "act_id=" + cba.getAct_id());
    }

    public void delete(Integer[] ids) {
        String idstr = StringUtil.arrayToString(ids, ",");
        this.daoSupport.execute("delete from es_cost_down_active where act_id in (" + idstr + ")");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(int id) {
        this.groupbuyPluginBundle.onGroupBuyEnd(id);
        this.groupbuyPluginBundle.onGroupBuyDelete(id);
        this.daoSupport.execute("delete from es_cost_down_active where act_id=?", id);

        //删除标签引用
        this.daoSupport.execute("delete from es_tag_rel where tag_id in (select tag_id from es_tags where is_groupbuy=?)", id);

        //删除团购标签
        this.daoSupport.execute("delete from es_tags where is_groupbuy=?", id);

    }

    public CostDownActive get(int id) {
        return (CostDownActive) this.daoSupport.queryForObject("select * from es_cost_down_active where act_id=?",
                CostDownActive.class, id);
    }

    public CostDownActive get() {
        return (CostDownActive) this.daoSupport.queryForObject(
                "select * from es_cost_down_active where end_time>? and act_status=1", CostDownActive.class,
                DateUtil.getDateline());
    }

    public Long getLastEndTime() {
        return this.daoSupport.queryForLong("SELECT max(end_time) from es_cost_down_active");
    }

    public List<CostDownActive> listEnable() {
        String sql = "select * from es_cost_down_active where end_time>=? order by add_time desc";
        long now = DateUtil.getDateline();
        return this.daoSupport.queryForList(sql, CostDownActive.class, now);
    }

    public List<CostDownActive> listJoinEnable() {
        String sql = "select * from es_cost_down_active where join_end_time>=? order by add_time desc";
        long now = DateUtil.getDateline();
        return this.daoSupport.queryForList(sql, CostDownActive.class, now);
    }

    public IDaoSupport getDaoSupport() {
        return daoSupport;
    }

    public void setDaoSupport(IDaoSupport daoSupport) {
        this.daoSupport = daoSupport;
    }

    public GroupbuyPluginBundle getGroupbuyPluginBundle() {
        return groupbuyPluginBundle;
    }

    public void setGroupbuyPluginBundle(GroupbuyPluginBundle groupbuyPluginBundle) {
        this.groupbuyPluginBundle = groupbuyPluginBundle;
    }
}
