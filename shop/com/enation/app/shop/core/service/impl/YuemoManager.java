package com.enation.app.shop.core.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.model.Auction;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

public class YuemoManager extends BaseSupport {

	public void add(Yuemo yuemo) {
		this.baseDaoSupport.insert("es_yuemo", yuemo);
	}
	
	public void edit(Yuemo yuemo) {
		this.baseDaoSupport.update("es_yuemo", yuemo, "id=" + yuemo.getId());
		updateStatus();
	}
	
	public void delete(Integer[] id) {
        if (id == null || id.equals(""))
            return;
        String id_str = StringUtil.arrayToString(id, ",");
        String sql = "delete from es_yuemo where id in (" + id_str + ")";
        this.baseDaoSupport.execute(sql);
	}
	
	
    public Yuemo get(Integer id) {
	        if(id!=null&&id!=0){
	            String sql = "select * from es_yuemo where id=?";
	            Yuemo ym = (Yuemo) this.baseDaoSupport.queryForObject(sql, Yuemo.class,id);
	            return ym;
	        }else{
	            return null;
	        }
	 }
    
    public List<Member> getJoinList(Integer id)
    {
        String sql = "select  member from es_yuemo  where id="+id;
        String mem = this.baseDaoSupport.queryForString(sql);
        String[] strArray=null;
        List joinList = new ArrayList();
        if(mem != null)
        {
          strArray = convertStrToArray(mem);
          for(String mid : strArray){
            String sqltemp = "select  * from es_member  where member_id=?";
            Member temp = (Member) this.baseDaoSupport.queryForObject(sqltemp, Member.class, mid);
            if(temp != null)
            {
                joinList.add(temp);   
            }  
          } 
        }  
        return joinList;
    }
    
    //使用String的split 方法  
    public static String[] convertStrToArray(String str){  
        String[] strArray = null;  
        strArray = str.split(","); //拆分字符为"," ,然后把结果交给数组strArray
        return strArray;
    } 
	
    public Page list(String order, int page, int pageSize) {
        order = order == null ? " id desc" : order;
        String sql = "select * from es_yuemo";
        sql += " order by  " + order;
        Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
        return webpage;
    }
    
    public List list() {
        String sql = "select * from es_yuemo order by id";
        List yuemolist = this.baseDaoSupport.queryForList(sql, Yuemo.class);
        return yuemolist;
    }
	
    public boolean updateStatus()
    {     
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String cutime=format.format(date); 
        String sql = "select * from es_yuemo  order by id";
        List<Yuemo> yuemolist = this.baseDaoSupport.queryForList(sql, Yuemo.class);
        for(Yuemo cur:yuemolist)
        {
            String overtime = cur.getTime();
            SimpleDateFormat dtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long otime = 0;
            long ctime = 0;
            try {
                java.util.Date dateover = dtime.parse(overtime);
                java.util.Date datecur = dtime.parse(cutime);
                otime = dateover.getTime();
                ctime = datecur.getTime();
            } catch(ParseException e) {
                e.printStackTrace();
            } 
            //如果当前时间大于截止时间
            if(ctime>otime)
            {   //更新约沫
                String sqlup = "update es_yuemo set status=2 where id="+cur.getId();
                this.baseDaoSupport.execute(sqlup);
            }else{
                String sqlup = "update es_yuemo set status=1 where id="+cur.getId();
                this.baseDaoSupport.execute(sqlup); 
            }
                
        }
        //System.out.println("更新约沫状态");
        return true;
    }	
	
	
	public Page get(int pageNo, int pageSize) {
		return this.baseDaoSupport.queryForPage("select * from es_yuemo order by id", pageNo, pageSize);
	}
	
}
