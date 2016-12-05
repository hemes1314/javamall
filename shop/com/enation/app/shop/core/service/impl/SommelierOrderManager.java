package com.enation.app.shop.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.user.AdminUserPluginBundle;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.base.core.service.auth.impl.PermissionManager;
import com.enation.app.shop.core.model.Sommelier;
import com.enation.app.shop.core.model.TastingNote;
import com.enation.app.shop.core.model.SommelierOrder;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

@Component
public class SommelierOrderManager extends BaseSupport {

    private AdminUser adminUser;
    private IPermissionManager permissionManager;
    private AdminUserPluginBundle adminUserPluginBundle;
    
    //添加酒评师，在酒评师表中添加，同时添加到管理员表中
	public int add(SommelierOrder order) {

	    this.baseDaoSupport.insert("es_sommelier_order", order);
	    return 1;

	}
	
    public void addTastingNode(TastingNote tastingNode) {
        String img = tastingNode.getImage();
        //如果酒评师没有上传图片，就用商品默认图片
        if((img==null) && ("".equals(img)))
        {
            String sql = "select original from es_goods where sn=";
            img = this.baseDaoSupport.queryForString(sql);
            tastingNode.setImage(img);
        }
        this.baseDaoSupport.insert("es_tasting_note", tastingNode);
    }
	
	public void edit(Sommelier sommelier) {
		this.baseDaoSupport.update("es_sommelier", sommelier, "id=" + sommelier.getId());
	}
	
	public void delete(Integer[] id) {
        if (id == null || id.equals(""))
            return;
        String id_str = StringUtil.arrayToString(id, ",");
        String sql = "delete from es_sommelier where id in (" + id_str + ")";
        for(int i = 0; i < id.length; i++){
            Integer sid = id[i];
            String usql = "select userid from es_sommelier where id=?";
            int userId = this.baseDaoSupport.queryForInt(usql, sid);
            String udsql = "delete from es_adminuser where userid="+userId;
            this.baseDaoSupport.execute(udsql); 
        }
        this.baseDaoSupport.execute(sql);
	}
	
	
    public Page getMyOrderList(Integer sid,int page,int pageSize) {
        String sql = "select o.*,s.name as sname,m.my_price from es_sommelier_order o, es_sommelier s，es_sommelier_my_type  m where o.sommelier_id = s.id and o.sommelier_id = m.sommelier_id  and o.typeid = m.order_type_id and o.sommelier_id=? ";
        Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize,sid);
        //List<SommelierOrder> sommelierOrderList = this.baseDaoSupport.queryForList(sql, SommelierOrder.class,sommelierid);
        return webpage;

	 }
    
    public Page getAllOrderList(String order, int page, int pageSize) {
        String sql = "select o.name,o.stime,o.etime,o.address,o.mobile,o.status,s.name as sname from es_sommelier_order o left join es_sommelier s on o.sommelier_id = s.id";
        order = order == null ? " o.id desc" : order;
        Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
        return webpage;
     }
    
	
    public Page list(String order, int page, int pageSize) {
        order = order == null ? " id desc" : order;
        String sql = "select * from es_sommelier";
        sql += " order by  " + order;
        Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
        return webpage;
    }
    
    public Page list_tast_note(String order, int page, int pageSize, int uid) {
        permissionManager = new PermissionManager();
        boolean isSuperAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("super_admin"));//超级管理员权限
        if(!isSuperAdmin)
        {
            order = order == null ? " id desc" : order;
            String sql = "select * from es_tasting_note where userid="+uid+" order by id desc";
            //sql += " order by  " + order;
            Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
            return webpage;
        }else
        {
            String sql = "select * from es_tasting_note order by id desc";
            Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
            return webpage; 
        }
    }
    
    public Page list_tast_note_tag(String order, int page, int pageSize, int uid) {
            String sql = "select * from es_tasting_note where userid="+uid+"order by id desc";
            Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
            
            //该品酒师访问人数加1
            String sqlup = "update es_sommelier set vcount = vcount+1 where userid=?";
            this.baseDaoSupport.execute(sqlup, uid);
            return webpage; 
    }
    
    public List tast_note(int nid) {
        String sql = "select * from es_tasting_note where id="+nid;
        List tast_note = this.baseDaoSupport.queryForList(sql,TastingNote.class,nid);
        return tast_note;
    }
    
    public List list() {
        String sql = "select * from es_sommelier order by id desc";
        List yuemolist = this.baseDaoSupport.queryForList(sql, Sommelier.class);
        return yuemolist;
    }
    
    public List list(String from,String to) {
        String sql = "select * from (select rownum as rn， es_sommelier.* from es_sommelier  order by vcount desc) where rn between "+from+" and "+to;
        List yuemolist = this.baseDaoSupport.queryForList(sql, Sommelier.class);
        return yuemolist;
    }
	
	
	public Page get(int pageNo, int pageSize) {
		return this.baseDaoSupport.queryForPage("select * from es_sommelier order by id desc", pageNo, pageSize);
	}
	
}
