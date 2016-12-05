package com.enation.app.shop.core.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.model.AdvanceLogs;
import com.enation.app.shop.core.model.Auction;
import com.enation.app.shop.core.model.AuctionRecord;
import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.model.Favorite;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.OrderLog;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.mobile.action.order.OrderApiAction;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;
import net.sf.json.JSONArray;

import com.enation.app.b2b2c.core.action.api.order.StoreOrderApiAction;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.service.impl.MemberAddressManager;
import com.enation.app.shop.core.service.impl.MemberManager;
import com.enation.app.shop.core.service.IPaymentManager;

import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.store.impl.StoreManager;

@Component
public class Kuaidi100BackManager extends BaseSupport {
    
    public void add(String status,String message,String com,String nu,String state,String ftime,String context){
          String sqld = "delete from  es_logi_info  where com=? and nu=?";
          String sqli = "insert into es_logi_info(com,nu,message,status,time,context) values(?,?,?,?,?,?)";
          this.baseDaoSupport.execute(sqld,com,nu);
          this.baseDaoSupport.execute(sqli,com,nu,message,status,ftime,context);
    }
    
    public List get(String com,String nu){
        String sql = "select * from es_logi_info where com=? and nu=?";
        ArrayList result = (ArrayList) this.baseDaoSupport.queryForList(sql,com,nu);
        return result;
    }
}
