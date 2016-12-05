package com.enation.app.shop.mobile.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Auction;
import com.enation.app.shop.core.model.Sommelier;
import com.enation.app.shop.core.model.SommelierOrder;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.app.shop.core.service.impl.MemberManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;


@Component
public class ApiSommelierManager extends BaseSupport {
    private MemberManager memberManager;
    public List sommelierList() {
        String sql = "select id, USERNAME,INTRODUCE,NAME,MOBILE,TASTING_COUNT,GOOD_COMMENT,BAD_COMMENT,GRADE,IMG_URL from es_sommelier  order by id desc";
        List<Map> webpage = this.baseDaoSupport.queryForList(sql);
        int n = 0;
        for (int i = 0;i<webpage.size();i++) {
            if(webpage.get(i) != null)
            {
                Map s =  (Map) webpage.get(i);
                sql = "select * from es_tasting_note where SOMMELIERID="+s.get("id");
                List t = this.baseDaoSupport.queryForList(sql);
                s.put("recommend", t);
            }
            n++;
        }
        return webpage;
    }
    
    public Page listPage(int pageNo, int pageSize) {
        String statis = SystemSetting.getStatic_server_domain(); 
        String sql = "select * from es_sommelier  order by tel";
        Page page = this.baseDaoSupport.queryForPage(sql, pageNo,pageSize, Sommelier.class);
        
        int n = 0;
        List<Sommelier> list = (List<Sommelier>)page.getResult();
        for (int i = 0;i<list.size();i++) {
            if(list.get(i) != null)
            {
                Sommelier s =  list.get(i);
                sql = "select * from es_tasting_note where SOMMELIERID="+s.getId();
                List<Map> t = this.baseDaoSupport.queryForList(sql);
                //替换图片路径
                for(Map a :t)
                {
                   if(a.get("image") != null)
                     a.put("image",((String) a.get("image")).replaceAll("fs:",statis)); 
                }
                   
                s.setRecommendList(t);
                s.setTasting_count(t.size());
            }
            n++;
        }
        return page;
    }
    
    public void apiPostOrderSommelier(SommelierOrder order)
    {
        this.baseDaoSupport.insert("ES_SOMMELIER_ORDER", order); 
    }
    
    public boolean apiConfirmOrder(String orderid)
    {
      String upsql = "update es_sommelier_order set status = 2 where id=?";
      this.baseDaoSupport.execute(upsql,orderid);
      return true;
    }
    
    public boolean apiOrderPay(Integer memid,Integer orderid)
    {
        String sql = "select m.my_price from es_sommelier_order o,es_sommelier_my_type m, es_sommelier_order_type t where o.typeid=t.id and m.order_type_id=t.id and o.sommelier_id=m.sommelier_id   and o.id=?"; 
        Map mprice = this.baseDaoSupport.queryForMap(sql,orderid);
        Double price = (Double) mprice.get("my_price");
        if(price!=null)
        {
            Member ordermember = memberManager.get(memid);
            //ordermember.setVirtual(1000.00);
            Double advance = ordermember.getAdvance();
            Double virtual = ordermember.getVirtual();
            Double total = advance + virtual;
            if(price < total)
            {   
                memberManager.pay(memid, price, 1, "侍酒", "订单号:"+orderid);
                String sqlpay = "update es_sommelier_order set status = 3 where id = ?"; 
                this.baseDaoSupport.execute(sqlpay, orderid);
              return true;
            }else
            {
              return false;
            }
        }else
        {
            return false;  
        }
    }
    
    public boolean apiOrderCancel(Integer orderid)
    {
        String sql = "update es_sommelier_order set status = 4 where id = ?"; 
        this.baseDaoSupport.execute(sql, orderid);
        return true;
    }
    
    public Map apiGetOrderStatusList(String memId)
    {
        String type = "member";
        //判断是普通会员还是酒评师
        String sql ="select uname from es_member m,es_sommelier s  where m.uname = s.username and member_id = ?";
        List mem = this.baseDaoSupport.queryForList(sql, memId);
        
        if(mem.size()>0)
        {
          type = "sommelier";
        }else
        {
          type = "member"; 
        }
 
        Map orderDetail = new HashMap();
        
        List<SommelierOrder> sommelierOrderList1 = new ArrayList();
        List<SommelierOrder> sommelierOrderList2 = new ArrayList();
        List<SommelierOrder> sommelierOrderList3 = new ArrayList();
        List<SommelierOrder> sommelierOrderList4 = new ArrayList();
        
        if(type.equals("member"))
        {
            String sql0 = "select o.id,m.my_price as myprice, o.name,o.address,o.area,o.zipcode,o.mobile,o.remark,o.stime,o.etime,s.id as sommelier_id,s.name as sommelier_name,t.id as typeid,t.order_type_name as typename from es_sommelier_order o left join es_sommelier s on o.sommelier_id = s.id   left join es_sommelier_order_type t on o.typeid = t.id left join es_sommelier_my_type m on m.order_type_id = t.id and m.sommelier_id = s.id  where  o.memid=? and "; 
            String sql1 = sql0 + "o.status=1"; 
            sommelierOrderList1 = this.baseDaoSupport.queryForList(sql1,Integer.valueOf(memId));
            String sql2 = sql0 + "o.status=2"; 
            sommelierOrderList2 = this.baseDaoSupport.queryForList(sql2, Integer.valueOf(memId));
            String sql3 = sql0 + "o.status=3"; 
            sommelierOrderList3 = this.baseDaoSupport.queryForList(sql3,Integer.valueOf(memId));
            String sql4 = sql0 + "o.status=4";
            sommelierOrderList4 = this.baseDaoSupport.queryForList(sql4,Integer.valueOf(memId));
        }else
        {
            String sqlu ="select s.id as sid from es_member m,es_sommelier s  where m.uname = s.username and m.member_id = ?";
            Map somap = this.baseDaoSupport.queryForMap(sqlu,memId);
            Long sommelierId =  (Long) somap.get("sid");
            
            String sql0 = "select o.id,m.my_price as myprice,o.name,o.address,o.area,o.zipcode,o.mobile,o.remark,o.stime,o.etime,s.id as sommelier_id,s.name as sommelier_name,t.id as typeid,t.order_type_name as typename from es_sommelier_order o left join es_sommelier s on o.sommelier_id = s.id   left join es_sommelier_order_type t on o.typeid = t.id left join es_sommelier_my_type m on m.order_type_id = t.id and m.sommelier_id = s.id  where  o.sommelier_id=? and "; 
            String sql1 = sql0 + " o.status=1"; 
            sommelierOrderList1 = this.baseDaoSupport.queryForList(sql1,sommelierId);
            String sql2 = sql0 + " o.status=2";
            sommelierOrderList2 = this.baseDaoSupport.queryForList(sql2,sommelierId);
            String sql3 = sql0 + " o.status=3"; 
            sommelierOrderList3 = this.baseDaoSupport.queryForList(sql3,sommelierId);
            String sql4 = sql0 + " o.status=4";  
            sommelierOrderList4 = this.baseDaoSupport.queryForList(sql4,sommelierId); 
        }
        
        orderDetail.put("memType", type);
        orderDetail.put("wait_confirm_order", sommelierOrderList1);
        orderDetail.put("wait_pay_order", sommelierOrderList2);
        orderDetail.put("already_pay_order", sommelierOrderList3);
        orderDetail.put("canneled_pay_order", sommelierOrderList4);
        orderDetail.put("type", type);
        return orderDetail;  
    }
    
    
    public Page recommendWinesList(String order, int page, int pageSize,String sommelierId) {
        order = order == null ? " id desc" : order;
        String sql = "select * from es_tasting_note where SOMMELIERID="+sommelierId;
        sql += " order by  " + order;
        Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
        return webpage;
    }
    
    public Page hotSommelierList(String order, int page, int pageSize) {
        order = order == null ? " id desc" : order;
        String sql = "select USERNAME,INTRODUCE,NAME,MOBILE,TASTING_COUNT from es_sommelier order by good_comment desc";
        sql += " order by  " + order;
        Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
        return webpage;
    }
     
    public Page detail(String order, int page, int pageSize,String sommelierId)
    {
        String statis = SystemSetting.getStatic_server_domain(); 
        order = order == null ? " id desc" : order;
        String sql = "select * from es_sommelier  where id="+sommelierId;
        Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
        ArrayList templist =(ArrayList) webpage.getResult();
        if(templist.size()>0)
        {
            Map tempmap = (Map) templist.get(0);
            if(tempmap.get("img_url") != null){
                tempmap.put("img_url", ((String) tempmap.get("img_url")).replaceAll("fs:",statis));  
            }
            
            String sqla = "select * from ES_TASTING_NOTE  where SOMMELIERID="+sommelierId+ "and rownum<2";
            List<Map> note = this.baseDaoSupport.queryForList(sqla);
            for(Map a:note)
            { 
                if(a.get("image") != null)
                {
                  a.put("image", ((String) a.get("image")).replaceAll("fs:",statis));
                }
            }
            List result = (List) webpage.getResult();
            result.add(note);
            webpage.setData(result);
        }
       // webpage.setData(data);
        return webpage;
    }
    
    public int addComment(Sommelier sommelier,Integer sommelierId,String comment,Integer userid) {
        Integer good;
        Integer bad;
        String sqlif = "select * from es_sommelier_comment  where sommelierid=? and userid=?";
        List ifcomment  = this.baseDaoSupport.queryForList(sqlif, sommelierId,userid);
        if(ifcomment.size()>0){
            return 0;
        }else
        {
            String sqlin = "insert into es_sommelier_comment(sommelierid,userid,comments) values(?,?,?)";
            this.baseDaoSupport.execute(sqlin,sommelierId,userid,comment);
        }
        
        if(comment.equals("good"))
        {
          String sql = "select good_comment from es_sommelier  where id=" + sommelierId;
          good  = this.baseDaoSupport.queryForInt(sql);
          good = good + 1;
          //sommelier.setGood_comment(good);
          //this.baseDaoSupport.update("es_sommelier", sommelier, "id=" + sommelierId);
          
          String sqlup = "update es_sommelier set good_comment=? where id=?";
          this.baseDaoSupport.execute(sqlup,good,sommelierId);
        }else
        {
           String sql = "select bad_comment from es_sommelier  where id=" + sommelierId;
           bad  = this.baseDaoSupport.queryForInt(sql);
           bad = bad + 1;
           //sommelier.setBad_comment(bad);
           //this.baseDaoSupport.update("es_sommelier", sommelier, "id=" + sommelierId);
           
           String sqlup = "update es_sommelier set bad_comment=? where id=?";
           this.baseDaoSupport.execute(sqlup,bad,sommelierId);
        }
        
        return 1;
        
    }

    
    public MemberManager getMemberManager() {
        return memberManager;
    }

    
    public void setMemberManager(MemberManager memberManager) {
        this.memberManager = memberManager;
    }

     
}
