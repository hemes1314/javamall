package com.enation.app.shop.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.plugin.user.AdminUserPluginBundle;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.base.core.service.auth.impl.PermissionManager;
import com.enation.app.shop.core.model.Sommelier;
import com.enation.app.shop.core.model.TastingNote;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
import com.enation.framework.util.DateUtil;

@Component
public class SommelierManager extends BaseSupport<Sommelier> {

    private AdminUser adminUser;
    private Member member;
    private IPermissionManager permissionManager;
    private AdminUserPluginBundle adminUserPluginBundle;
    //添加酒评师，在酒评师表中添加，同时添加到管理员表中
	public int add(Sommelier sommelier) {
	    
		String password = StringUtil.md5(sommelier.getPass());
		String username = sommelier.getUsername();
		String mobile = sommelier.getMobile();
		String email = sommelier.getEmail();
		String realname = sommelier.getName();
		
		String sqlm = "select * from es_member where uname=?";
		List mem = this.baseDaoSupport.queryForList(sqlm,username);
		if(mem.size()==0)
		{
    		//添加到会员表
    		member = new Member();
    		member.setUname(username);
    		member.setPassword(password);
    		member.setName(realname);
    		member.setNickname(realname);
    		member.setLv_id(1);
    		member.setMobile(mobile);
    		member.setEmail(email);
    		member.setRegtime(DateUtil.getDateline());
    		member.setLastlogin(DateUtil.getDateline());
    		this.baseDaoSupport.insert("es_member", member);
		}
		
		//添加到管理员表
		adminUser = new AdminUser();
		adminUser.setUsername(username);
		adminUser.setPassword(password);
		adminUser.setRealname(realname);
	    adminUser.setState(1);
	    
        String sql = "select * from es_adminuser where username=?";
        List userList = this.baseDaoSupport.queryForList(sql,username);
        if(userList.size()>0)
            return 0;
        
	    // 添加成为管理员
	    this.baseDaoSupport.insert("adminuser", adminUser);
	    
	    //为其分配酒评师角色
	    permissionManager = new PermissionManager();
	    int userid = this.baseDaoSupport.getLastId("adminuser");
	    sommelier.setUserid(userid);
	    
	    this.baseDaoSupport.insert("es_sommelier", sommelier);
	    int id = 21;
	    this.baseDaoSupport.execute("insert into user_role(roleid,userid)values(?,?)",id,userid);
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
	
	
    public Sommelier get(Integer id) {
	        if(id!=null&&id!=0){
	            String sql = "select * from es_sommelier where id=?";
	            Sommelier sommelier = this.baseDaoSupport.queryForObject(sql, Sommelier.class,id);
	            return sommelier;
	        }else{
	            return null;
	        }
	 }
    
    public Sommelier getByUserId(Long id) {
        if(id!=null&&id!=0){
            String sql = "select * from es_sommelier where userid=?";
            String sqltascount = "select id from es_tasting_note where userid=?";
            List count = this.baseDaoSupport.queryForList(sqltascount, id);
            int counttast = 0;
            if(count != null)
            {
                counttast =  count.size();
            }
            Sommelier sommelier = this.baseDaoSupport.queryForObject(sql, Sommelier.class,id);
            sommelier.setTasting_count(counttast);
            return sommelier;
        }else{
            return null;
        }
 }
    
    public Sommelier getUserById(Long id) {
        if(id!=null&&id!=0){
            String sql = "select * from es_sommelier where userid=?";
 
            Sommelier sommelier = this.baseDaoSupport.queryForObject(sql, Sommelier.class,id);
            return sommelier;
        }else{
            return null;
        }
 }
	
    public Page list(String order, int page, int pageSize) {
        order = order == null ? " id desc" : order;
        String sql = "select * from es_sommelier where userid != 0";
        sql += " order by  " + order;
        Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
        return webpage;
    }
    
    public Page list_tast_note(String order, int page, int pageSize, long uid) {
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
    
    public Page list_tast_note_tag(String order, int page, int pageSize, long uid) {
            String sql = "select * from es_tasting_note where userid=? order by id desc";
            Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize, uid);
            
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
        String sql = "select * from es_sommelier order by tel";
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
