package com.enation.app.shop.mobile.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Auction;
import com.enation.app.shop.core.model.AuctionRecord;
import com.enation.app.shop.core.model.Cf;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.Page;


@Component
public class ApiAuctionManager extends BaseSupport<Auction> { 
    AuctionRecord auctionRecord;
    public List<Auction> list() {
        String sql = "select * from es_auction order by id desc";
        List<Auction> auctionlist = this.baseDaoSupport.queryForList(sql, Auction.class);
        return auctionlist;
    }
    
    public Page listPage(int pageNo, int pageSize) {
        String sql = "select * from es_auction order by id desc";
        Page page = this.baseDaoSupport.queryForPage(sql, pageNo,pageSize, Auction.class);
        return page;
    }
    
    public boolean addLooks(String auid)
    {
        this.baseDaoSupport.execute("update es_auction set onlookers=onlookers+1 where id="+auid);
        return true;
    }
    
    /*
     * 参加拍卖函数 ---  更新拍卖表  ， 拍卖记录表
     */
    public boolean joinAuction(String auctionId,String memId,String add)
    {   
        
         if((auctionId == null)||"".equals(auctionId))
        {
            return false;
        }
        if((memId == null)||"".equals(memId))
        {
            return false;
        }
        if((add == null)||"".equals(add))
        {
            return false;
        }
        
        //取得此拍卖当前价格
        String sqlc = "select  cprice from es_auction  where id="+auctionId;
        String cprice = this.baseDaoSupport.queryForString(sqlc);
        
        //取得此拍卖参与人数
        String sqll = "select  length from es_auction  where id=?";
        int clength = this.baseDaoSupport.queryForInt(sqll,auctionId);
        
        if((cprice == null)||"".equals(cprice))
        {
            cprice = "0";
        }
        
        //if((cprice == null)||"".equals(cprice))
       // {
           // clength = 0;
        //}


        float temp0 = NumberUtils.toFloat(cprice);
        float temp1 = NumberUtils.toFloat(add);
        float temp2 = temp0 + temp1;
        cprice = Float.toString(temp2);
        
        //取得当前时间，作为参与拍卖的时间
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=format.format(date); 
        
        //取得参与拍卖者的用户名和昵称
        String sqlmem = "select uname from es_member  where member_id="+memId;
        String user_name = "";
        String nickname = "";
        try{
            user_name = this.baseDaoSupport.queryForString(sqlmem);
            String sqlmem1 = "select nickname from es_member  where member_id="+memId;
            nickname = this.baseDaoSupport.queryForString(sqlmem1);
        }catch (RuntimeException e)
        {
            return false;
        }
        
        if((user_name == null)||"".equals(user_name))
        {
            return false;
        }
        
        //add 更新拍卖记录
        this.baseDaoSupport.execute("update es_auction_record set status=0 where auction_id="+auctionId);
        
        AuctionRecord auctionRecord = new AuctionRecord();
        auctionRecord.setAuction_id(Integer.valueOf(auctionId));
        auctionRecord.setUserid(memId);
        auctionRecord.setUser_name(user_name);
        auctionRecord.setNickname(nickname);
        auctionRecord.setStatus(1);
        auctionRecord.setPrice(cprice);
        auctionRecord.setTime(time);
   
        //如果该用户没有对此商品出过价，则参与拍卖的人数加1
        String sqls = "select id from es_auction_record where userid=? and auction_id =?";
        List tem = this.baseDaoSupport.queryForList(sqls, memId,auctionId); 
        if(tem.size() == 0)
        {
            clength = clength + 1; 
        }
        
        this.daoSupport.insert("ES_AUCTION_RECORD", auctionRecord);
        //修改拍卖表中的当前价格
        Map aumap = new HashMap();
        aumap.put("CPRICE", cprice);
        aumap.put("LENGTH", clength);
        this.daoSupport.update("ES_AUCTION", aumap, "id="+auctionId);
        return true;
    }
    
    
    public Map detail(String auctionId)
    {
        Map detail = new HashMap();
        
        //参与人列表
        ArrayList memberDetail = new ArrayList();
        //出价记录
        ArrayList recordList = new ArrayList();
        
        String sqlMember = "select  * from es_auction  where id="+auctionId;
        memberDetail = (ArrayList) this.baseDaoSupport.queryForList(sqlMember);
        HashMap <String,Object>  map = (HashMap<String, Object>) memberDetail.get(0);
        map.put("image", UploadUtil.replacePath((String)map.get("image")));
        String sqlrec = "select ES_AUCTION_RECORD.user_name, ES_AUCTION_RECORD.price,ES_AUCTION_RECORD.time,ES_AUCTION_RECORD.status,ES_AUCTION_RECORD.nickname from ES_AUCTION_RECORD  where AUCTION_ID="+auctionId+" order by id desc";
        recordList = (ArrayList) this.baseDaoSupport.queryForList(sqlrec);
        
        detail.put("memlist", memberDetail);
        detail.put("recordlist", recordList);
        return detail;
    }
     
    //使用String的split 方法  
    public static String[] convertStrToArray(String str){  
        String[] strArray = null;  
        strArray = str.split(","); //拆分字符为"," ,然后把结果交给数组strArray
        return strArray;
    } 
}
