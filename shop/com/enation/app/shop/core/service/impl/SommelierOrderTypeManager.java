package com.enation.app.shop.core.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.user.AdminUserPluginBundle;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.base.core.service.auth.impl.PermissionManager;
import com.enation.app.shop.core.model.Sommelier;
import com.enation.app.shop.core.model.SommelierMyType;
import com.enation.app.shop.core.model.TastingNote;
import com.enation.app.shop.core.model.SommelierOrder;
import com.enation.app.shop.core.model.SommelierOrderType;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

@Component
public class SommelierOrderTypeManager extends BaseSupport {

    private SommelierOrderType sommelierOrderType;

    

    public Page getOrderTypeList(String order, int page, int pageSize) {
        String sql = "select * from es_sommelier_order_type order by id desc";
        Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
        return webpage;
    }
	
    public SommelierOrderType getSommelierOrderType() {
        return sommelierOrderType;
    }

    
    public void setSommelierOrderType(SommelierOrderType sommelierOrderType) {
        this.sommelierOrderType = sommelierOrderType;
    }

    public SommelierOrderType getOrderType(Integer typeId) {
	    String sql = "select * from es_sommelier_order_type where id=? order by id desc";
	    SommelierOrderType orderType = (SommelierOrderType) this.baseDaoSupport.queryForObject(sql, SommelierOrderType.class, typeId);
	    return orderType;
	}
	
   public Page getMyTypeList(String order, int page, int pageSize,Integer sid) {
       String sql = "select t.id,t.order_type_name,t.order_type_price,t.order_type_intro,t.order_type_image,m.sommelier_id,m.my_price,m.status from es_sommelier_order_type t LEFT JOIN es_sommelier_my_type m on t.id = m.order_type_id and m.sommelier_id = ?";
       Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize,sid);

       return webpage;
   }
   
   public SommelierMyType getMyType(Integer typeId,Integer sid) {
       String sql = "select t.order_type_intro,t.id,m.my_price,t.order_type_name,m.status,t.order_type_price from es_sommelier_order_type  t left join  es_sommelier_my_type  m  on t.id = m.order_type_id and m.sommelier_id=? where t.id=?";
       Map mmytype = this.baseDaoSupport.queryForMap(sql,sid,typeId);
       SommelierMyType smytype = new SommelierMyType();
       smytype.setId((long) mmytype.get("id"));
       smytype.setMy_price((Double) mmytype.get("my_price"));
       smytype.setOrder_type_id((long) mmytype.get("id"));
       smytype.setOrder_type_name((String) mmytype.get("order_type_name"));
       smytype.setOrder_type_info((String) mmytype.get("order_type_intro"));
       smytype.setOrder_type_name((String) mmytype.get("order_type_name"));
       smytype.setStatus((int) mmytype.get("status"));
       smytype.setOrder_type_price((double) mmytype.get("order_type_price"));
       return smytype;
   }
   
   public void deleteType(Integer[] id) {
       if (id == null || id.equals(""))
           return;
       String id_str = StringUtil.arrayToString(id, ",");
       String sql = "delete from es_sommelier_order_type where id in (" + id_str + ")";
       this.baseDaoSupport.execute(sql);
   }
	
    public boolean addOrderType(SommelierOrderType orderType) {
      this.baseDaoSupport.insert("es_sommelier_order_type", orderType);
      return true;
    }
    
    public boolean modifyOrderType(SommelierOrderType orderType,Map where) {
        this.baseDaoSupport.update("es_sommelier_order_type", orderType, where);
        return true;
    }
    
    public boolean modifyMyType(SommelierMyType myType,Integer sid) {
        int status = myType.getStatus();
        String sql = "select * from es_sommelier_my_type where order_type_id = ? and sommelier_id = ?";
        List result = this.baseDaoSupport.queryForList(sql, SommelierMyType.class, myType.getOrder_type_id(),sid);
        int size = result.size();
        if(size>0)
        {
            String sqlu = "update es_sommelier_my_type set status = ?, my_price = ? where order_type_id = ? and sommelier_id = ?";
            this.baseDaoSupport.execute(sqlu,status,myType.getMy_price(),myType.getOrder_type_id(),sid); 
        }else
        {
            String sqli = "insert into es_sommelier_my_type(sommelier_id,order_type_id,my_price,status) values(?,?,?,?)"; 
            this.baseDaoSupport.execute(sqli,sid,myType.getOrder_type_id(),myType.getMy_price(),status);
        }
       
        return true;
    }
    
    public boolean deleteOrderType(Integer typeId) {
        String sql = "delete from es_sommelier_order_type where id=?";
        this.baseDaoSupport.execute(sql, typeId);
        return true;
    }
    
    /*
     * api interface     
     */
    public Page getApiOrderTypeList(String order, int page, int pageSize) {
        String sql = "select * from es_sommelier_order_type order by id desc";
        Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
        return webpage;
    }
    

   
   public Page getApiOrderSommelierList(String typeId,String order, int page, int pageSize) {
       String sql = "select s.id,s.username,s.introduce,s.name,s.mobile,s.sex,s.email,s.img_url,s.grade,s.userid,s.detail,s.good_comment,s.bad_comment from  es_sommelier  s, es_sommelier_my_type  t  where s.id = t.sommelier_id and t.order_type_id = ?";
       Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize, typeId);
       return webpage;
   }

}
