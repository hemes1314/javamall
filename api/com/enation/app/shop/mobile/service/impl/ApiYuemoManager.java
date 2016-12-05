package com.enation.app.shop.mobile.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Maimo;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;


@Component
public class ApiYuemoManager extends BaseSupport<Yuemo> {
    
    public List<Yuemo> list() {
        String sql = "select * from es_yuemo order by time desc";
        List<Yuemo> yuemolist = this.baseDaoSupport.queryForList(sql, Yuemo.class);
        return yuemolist;
    }
    
    public Page listPage(int pageNo, int pageSize) {
        String sql = "select * from es_yuemo where status=1 order by time desc";
        Page page = this.baseDaoSupport.queryForPage(sql, pageNo,pageSize, Yuemo.class);
        return page;
    }
    
    public int joinYuemo(String ymId,String memId)
    {  
        String sqlli = "select  LENGTH, PLIMIT from es_yuemo  where id="+ymId;
        Yuemo a = this.baseDaoSupport.queryForObject(sqlli, Yuemo.class);
        if(a == null)
            return 3;
        if(a.getLength() == a.getPlimit())
        {
            return 2;
        }
        
        String sql = "select  member from es_yuemo  where id="+ymId;
        String mem = this.baseDaoSupport.queryForString(sql);
        String[] strArray=null;
        if(mem != null)
        {
          strArray = convertStrToArray(mem);
          for(String mid : strArray){
              if(mid.equals(memId))
                  return 0;
          } 
        }  
        
        if((mem != null)&&(!mem.equals("")))
        {
          mem = mem + "," + memId;
        }else
        {
            mem =   memId;
        }
        
        int length = 1;
        if(strArray != null)
        {
            length = strArray.length + 1;
        }
        
        String sql1 = "update es_yuemo set member='"+mem+"'"+",length="+ length+"  where id="+ymId;
        this.baseDaoSupport.execute(sql1);
        return 1;
    }
    
    
    public ArrayList<Map> detail(String ymId)
    {
        ArrayList memberDetail = new ArrayList();
        String sql = "select  member from es_yuemo  where id=?";
        List memlist = this.baseDaoSupport.queryForList(sql, ymId);
        String mem = "";
        if(memlist.size()>0)
        {
            String[] strArray=null;
            HashMap mapmem = (HashMap) memlist.get(0);
            mem = (String) mapmem.get("member");
            strArray = convertStrToArray(mem);
            for(String mid : strArray){
                String msql1 = "select uname from es_member  where member_id=?";
                try
                {
                    List m = this.baseDaoSupport.queryForList(msql1, mid);
                    if(m==null)
                        continue;
                    if(m.size()==0)
                        continue;
                }catch (RuntimeException e)
                {
                    continue;
                }

                String msql = "select  uname,face,nickname from es_member  where member_id="+mid;
                Map memnameface = this.baseDaoSupport.queryForMap(msql);
                if(memnameface != null)
                {
                   memberDetail.add(memnameface);
                }
            }
        }
        return memberDetail;
    }
    
    //使用String的split 方法  
    public static String[] convertStrToArray(String str){  
        String[] strArray = null;  
        strArray = str.split(","); //拆分字符为"," ,然后把结果交给数组strArray
        return strArray;
    } 
}
