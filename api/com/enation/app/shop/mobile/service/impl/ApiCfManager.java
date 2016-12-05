package com.enation.app.shop.mobile.service.impl;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Auction;
import com.enation.app.shop.core.model.Cf;
import com.enation.app.shop.core.model.CfClick;
import com.enation.app.shop.core.model.CfMessage;
import com.enation.app.shop.core.model.CfRecord;
import com.enation.app.shop.core.model.UtastingNote;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonMessageUtil;


@Component
public class ApiCfManager extends BaseSupport<Cf> {
    
    public Page list(int pageNo , int pageSize) {
        String order = "status asc,time desc";
        String sql = "select * from es_cf";
        sql += " order by  " + order;
        //String sql = "select * from es_cf order by id desc";
     
        Page webPage = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize,Cf.class);
        List<Cf> cflist = (List<Cf>) webPage.getResult();
        for(Cf c:cflist)
        {
            //商品列表 
            /*String sql_goods = "select g.goods_id, g.name,g.price,g.brief,g.store from es_cf_goods c,es_goods g where c.goods_id = g.goods_id and c.cf_id=?";
            List<Map> goodsList = this.baseDaoSupport.queryForList(sql_goods,c.getId());*/
            String sql_record = "select * from  es_cf_record where CF_ID = ?";
            List<Map> recordList = this.baseDaoSupport.queryForList(sql_record,c.getId());
            c.setSupport(recordList.size());
/*            for(Map mapgood :recordList)
            {
                String sql_re = "select count(*)  count from es_cf_record where cf_goods_id = ?";
                List<Map> reList = this.baseDaoSupport.queryForList(sql_re,mapgood.get("goods_id"));
                int count = reList.size();
                int temp = c.getSupport();
                c.setSupport(temp+count);
                //mapgood.put("support", count);
            }*/
            
            c.setBimage(UploadUtil.replacePath(c.getBimage()));
            c.setIimage(UploadUtil.replacePath(c.getIimage()));
            c.setDetail_image(UploadUtil.replacePath(c.getDetail_image()));
        }
        return webPage;
    }
    
    /*
     * 商品详情
     */
    public HashMap detailGoods(String goodsId)
    {
        HashMap goods = new HashMap(); 
        String sql = "select * from es_goods where goods_id = ?";
        List<Map> goodsList = this.baseDaoSupport.queryForList(sql,goodsId);
        if(goodsList.size()>0)
        {
            goods = (HashMap) goodsList.get(0);
        }
  
        goods.put("thumbnail",UploadUtil.replacePath((String) goods.get("thumbnail")));
        goods.put("original",UploadUtil.replacePath((String) goods.get("original")));
        goods.put("small",UploadUtil.replacePath((String) goods.get("small")));
        goods.put("big",UploadUtil.replacePath((String) goods.get("big")));
        return goods;
    }
    
    /*
     * 我的众筹
     */
    public HashMap myCf(String memberId)
    {
        HashMap myCfs = new HashMap(); 
        //众筹ID、众筹名称、支付金额、状态、收货地址、众筹描述
        String sqlwaitcf = "select r.order_id as sn,r.address as address,r.cf_id as cfid,c.title as title,g.price as price,c.status as status,r.status as recStatus, c.content as content from es_cf_record r,es_cf c,es_goods g  where r.cf_id = c.id and r.cf_goods_id = g.goods_id and c.status = 0 and r.member_id = ?";
        List<Map> mywaitcfList = this.baseDaoSupport.queryForList(sqlwaitcf,memberId);
   /*     for(Map mymap :mywaitcfList)
        {
            String sqlmem = "select address from es_member where  member_id = ?";
            List<Map> addressList = this.baseDaoSupport.queryForList(sqlmem,memberId);
            if(addressList.size()>0)
            {
                String address = (String) addressList.get(0).get("address");
                mymap.put("address", address);
            }      
        }*/
        
        String sqloncf = "select r.order_id as sn,r.address as address,r.cf_id as cfid,c.title as title,g.price as price,c.status as status,r.status as recStatus, c.content as content from es_cf_record r,es_cf c,es_goods g  where r.cf_id = c.id and r.cf_goods_id = g.goods_id and c.status = 1 and r.member_id = ?";
        List<Map> myoncfList = this.baseDaoSupport.queryForList(sqloncf,memberId);
/*        for(Map mymap :myoncfList)
        {
            String sqlmem = "select address from es_member where  member_id = ?";
            List<Map> addressList = this.baseDaoSupport.queryForList(sqlmem,memberId);
            if(addressList.size()>0)
            {
                String address = (String) addressList.get(0).get("address");
                mymap.put("address", address);
            }      
        }*/
        
        String sqlfailcf = "select r.order_id as sn,r.address as address,r.cf_id as cfid,c.title as title,g.price as price,c.status as status,r.status as recStatus,c.content as content from es_cf_record r,es_cf c,es_goods g  where r.cf_id = c.id and r.cf_goods_id = g.goods_id and c.status = 4 and r.member_id = ?";
        List<Map> myfailcfList = this.baseDaoSupport.queryForList(sqlfailcf,memberId);
/*        for(Map mymap :myoncfList)
        {
            String sqlmem = "select address from es_member where member_id = ?";
            List<Map> addressList = this.baseDaoSupport.queryForList(sqlmem,memberId);
            if(addressList.size()>0)
            {
                String address = (String) addressList.get(0).get("address");
                mymap.put("address", address);
            }      
        }*/
        
        String sqlsuccesscf = "select r.order_id as sn,r.address as address,r.cf_id as cfid,c.title as title,g.price as price,c.status as status,r.status as recStatus,c.content as content from es_cf_record r,es_cf c,es_goods g  where r.cf_id = c.id and r.cf_goods_id = g.goods_id and c.status = 3 and r.member_id = ?";
        List<Map> mysuccesscfList = this.baseDaoSupport.queryForList(sqlsuccesscf,memberId);
/*        for(Map mymap :myoncfList)
        {
            String sqlmem = "select address from es_member where member_id = ?";
            List<Map> addressList = this.baseDaoSupport.queryForList(sqlmem,memberId);
            if(addressList.size()>0)
            {
                String address = (String) addressList.get(0).get("address");
                mymap.put("address", address);
            }      
        }*/
        
        myCfs.put("mywaitcfList", mywaitcfList);
        myCfs.put("myoncfList", myoncfList);
        myCfs.put("myfailcfList", myfailcfList);
        myCfs.put("mysuccesscfList", mysuccesscfList);
        return myCfs;
    }
    
    /*
     * 众筹详情
     */
    public HashMap detail(String cfId,String memId)
    {
        
        String upclick = "update es_cf set click = click + 1 where id = ?";
        this.baseDaoSupport.execute(upclick, cfId);
        
        HashMap cfDetail = new HashMap(); 
        //众筹付款与回复列表  时间  昵称 与头像  ES_CF_RECORD
        String sql_record = "select es_cf_record.MEMBER_ID, es_cf_record.MESSAGE,"
                + "es_cf_record.MESSAGE_TIME,es_member.face,es_member.UNAME, es_member.NICKNAME from es_cf_record,es_member "
                + "where es_cf_record.CF_ID="+cfId+" and es_cf_record.MEMBER_ID=es_member.MEMBER_ID";
        List<Map> recordList = this.baseDaoSupport.queryForList(sql_record);
        
        //替换图片
        for(Map rmap :recordList)
        {
            String face = (String) rmap.get("face");
            face = UploadUtil.replacePath(face);
            rmap.put("face", face);
        }
        
        //点赞列表（名与头像）
/*        String sql_click = "select es_cf_click.MEMBER_ID,es_member.face,es_member.UNAME, es_member.NICKNAME,es_member.face from es_cf_click,es_member "
                + "where es_cf_click.CF_ID="+cfId+" and es_cf_click.MEMBER_ID=es_member.MEMBER_ID";
        List<Map> clickList = this.baseDaoSupport.queryForList(sql_click); */
        
        //点赞列表变成点赞数量
        String sql_click = "select * from es_cf_click where CF_ID="+cfId;
        List<Map> clickList = this.baseDaoSupport.queryForList(sql_click);
        int clickCount = 0;
        if(clickList != null)
        {
            clickCount = clickList.size(); 
        }
        
        
        //替换图片
        for(Map cmap :clickList)
        {
            String face = (String) cmap.get("face");
            face = UploadUtil.replacePath(face);
            cmap.put("face", face);
        }


        //留言列表
        String sql_message = "select es_cf_message.MEMBER_ID,es_cf_message.message,es_member.UNAME, es_member.NICKNAME,es_member.face,es_cf_message.IF_JOIN, es_cf_message.TIME from es_cf_message,es_member "
                + "where es_cf_message.CF_ID="+cfId+" and es_cf_message.MEMBER_ID=es_member.MEMBER_ID";
        List<Map> messageList = this.baseDaoSupport.queryForList(sql_message);
        
        //商品列表 
        String sql_goods = "select g.goods_id, g.name,g.price,g.brief,g.store from es_cf_goods c,es_goods g where c.goods_id = g.goods_id and c.cf_id=?";
        List<Map> goodsList = this.baseDaoSupport.queryForList(sql_goods,cfId);
        for(Map mapgood :goodsList)
        {
            String sql_re = "select count(*)  count from es_cf_record where cf_goods_id = ?";
            List<Map> reList = this.baseDaoSupport.queryForList(sql_re,mapgood.get("goods_id"));
            int count = reList.size();
            mapgood.put("support", count);
        }
        
        //替换图片
        for(Map mmap :messageList)
        {
            String face = (String) mmap.get("face");
            face = UploadUtil.replacePath(face);
            mmap.put("face", face);
        }
        
        //剩余天数,已筹金额
        String sql_day_get = "select * from es_cf where id= "+cfId;
        List<Cf> cflist = this.baseDaoSupport.queryForList(sql_day_get,Cf.class);
        
        //是否点过赞
        cfDetail.put("ifclick", "0");
        if((memId != null) && !"".equals(memId))
        {
            String if_click = "select id from es_cf_click where cf_id=? and member_id=?";
            List<Map> ifClick = this.baseDaoSupport.queryForList(if_click, cfId,memId);
            //add by lin 
             
            if(ifClick.size()>0)
            {
                cfDetail.put("ifclick", "1");
            }
        }
        
        Cf cf = cflist.get(0);
        String time = cf.getTime();
        float already_get = 0;
        
        if(cf.getAlready_get() != null)
        {
         already_get = cf.getAlready_get();
        }
        
        String release_nickname = cf.getRelease_nickname();
        String release_face = cf.getRelease_face();
        
        String subheading = cf.getSubheading();
        String brief = cf.getBrief();
        
        String title = cf.getTitle();
        String target = cf.getTarget();
        
        String content = cf.getContent();
        
        int click  = cf.getClick();
        
        Date d1 = new Date();
        Date d2 = null;
        try {
            d2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(time);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        long timeLeft = (d2.getTime()-d1.getTime())/(24*60*60*1000);
        
        //cfsql //图片列表 //发表时间
        String cfsql = "select DETAIL_IMAGE, RELEASE_TIME from es_cf where id= "+ cfId;
        Map result = this.baseDaoSupport.queryForMap(cfsql);
        
        String detailImage = "";
        if(result.get("detail_image")!= null)
        {
          detailImage = result.get("detail_image").toString();
        }
        detailImage = UploadUtil.replacePath(detailImage);
        
        String releaseTime = "";
        if(result.get("release_time")!= null)
        {
          releaseTime = result.get("release_time").toString();
        }
        
        //访问数量
        //cfDetail.put("clickCount", clickCount);
        //留言列表
        cfDetail.put("messageList", messageList);
        //回复列表
        cfDetail.put("recordList", recordList);
        //点赞列表
        //cfDetail.put("clickList", clickList);
        //商品列表

        cfDetail.put("goodsList", goodsList);
        //剩余天数
        cfDetail.put("timeLeft", timeLeft);
        //已筹金额
        cfDetail.put("already_get", already_get);
        //图片列表
        cfDetail.put("detailImage", detailImage);
        //发表时间
        cfDetail.put("releaseTime", releaseTime);
        //发布人
        cfDetail.put("release_nickname", release_nickname);
        //头像
        release_face = UploadUtil.replacePath(release_face);
        cfDetail.put("release_face", release_face);
        
        //副标题
        cfDetail.put("subheading", subheading); 
        //副标题
        cfDetail.put("click", click);
        //支持数
        cfDetail.put("support",recordList.size());
        //简介
        cfDetail.put("brief",brief);
        //标题
        String titles = cf.getTitle();
        cfDetail.put("title",titles);
        //目标
        String target1 = cf.getTarget();
        cfDetail.put("target",target1);
        //状态
        String status = cf.getStatus();
        cfDetail.put("status",status);
        
        cfDetail.put("target",target1);
        //详情
        String detail = cf.getContent();
        cfDetail.put("detail",detail);
        //百分比
        NumberFormat formatter = new DecimalFormat("0.00");
        Double x = new Double(Double.valueOf(already_get)/Double.valueOf(target1));
        //x = x*100;
        String xx = formatter.format(x);
       //  String percent = xx + "%";
        
            cfDetail.put("percent",xx);
        return cfDetail;
    }
    
    /*
     * 参与众筹
     */
    public void joinCf(CfRecord record)
    {
        String sql = "select  member,already_get,price,length from es_cf  where id="+record.getCf_id();
        List<Cf> cflist = this.baseDaoSupport.queryForList(sql, Cf.class);
        Cf mem_get =  cflist.get(0);
        String mem = mem_get.getMember();
        
        float get = 0;
        if(mem_get.getAlready_get()!=null)
        {
            get = mem_get.getAlready_get();
        }
        
        float price = 0;
        if(mem_get.getPrice()!=null)
        {
            price = mem_get.getPrice();
        }
        
        int length = 0;
        length = mem_get.getLength();
        length = length + 1;

        
        float already = get + price;
        
        if(mem != null)
        {
          mem = mem + "," + record.getMember_id();
        }else
        {
            mem =   record.getMember_id();
        }
        
        String sql1 = "update es_cf set member='"+mem+"',already_get="+already+",length="+length+"  where id="+record.getCf_id();
        this.daoSupport.execute(sql1);
        this.daoSupport.insert("es_cf_record",record);
    }
    
    /*
     * 众筹点赞
     */
    public boolean click_Cf(CfClick click){
        this.daoSupport.insert("es_cf_click",click);
        return true;
    }
    
    /*
     * 生成订单
     */
    public void createOrder(String memberId, String cfId,String goodsId, String address, String orderId)
    {
        long nowTime=System.currentTimeMillis();
        String ordersql = "insert into es_cf_record(MEMBER_ID,CF_ID,CF_GOODS_ID,ADDRESS,ORDER_ID) values(?,?,?,?,?)";
        this.daoSupport.execute(ordersql,memberId,cfId,goodsId,address,nowTime);
    }
    
    /*
     * 众筹留言
     */
    public boolean addMessage(CfMessage cfmessage){        
       String sql = "select * from es_cf_record  where member_id="+cfmessage.getMember_id()+" and cf_id="+cfmessage.getCf_id();

           List a = this.baseDaoSupport.queryForList(sql);
           if(a.isEmpty())
           {
               cfmessage.setIf_join("否");
           }else
           {
               cfmessage.setIf_join("是");
           }
   
       
       Date date=new Date();
       DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       String time=format.format(date); 
       
       cfmessage.setTime(time);
       this.daoSupport.insert("es_cf_message",cfmessage);
       return true;
    }
    
    //使用String的split 方法  
    public static String[] convertStrToArray(String str){  
        String[] strArray = null;  
        strArray = str.split(","); //拆分字符为"," ,然后把结果交给数组strArray
        return strArray;
    } 
}
