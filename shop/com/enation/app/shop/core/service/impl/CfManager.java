package com.enation.app.shop.core.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.Auction;
import com.enation.app.shop.core.model.Cf;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

public class CfManager extends BaseSupport<Cf> {
    
    private Date date=new Date();
    private DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat dtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
	public void add(Cf cf) {
	    //设置状态为进行中
	    cf.setStatus("1");
		this.baseDaoSupport.insert("es_cf", cf);
	}
	
	public void edit(Cf cf) {
		this.baseDaoSupport.update("es_cf", cf, "id=" + cf.getId());
	}
	
	public void delete(Integer[] id) {
        if (id == null || id.equals(""))
            return;
        String id_str = StringUtil.arrayToString(id, ",");
        String sql = "delete from es_cf where id in (" + id_str + ")";
        String sqlclick = "delete from es_cf_click  cf_id in (" + id_str + ")";
        String sqlmessage = "delete from es_cf_message where cf_id in (" + id_str + ")";
        String sqlrecord = "delete from es_cf_record where cf_id in (" + id_str + ")";
        this.baseDaoSupport.execute(sqlrecord);
        this.baseDaoSupport.execute(sqlmessage);
        this.baseDaoSupport.execute(sql);
	}
	
    public Cf get(Integer id) {
	        if(id!=null&&id!=0){
	            String sql = "select * from es_cf where id=?";
	            Cf cf = this.baseDaoSupport.queryForObject(sql, Cf.class,id);
	            return cf;
	        }else{
	            return null;
	        }
	 }
    
	
    public Page list(String order, int page, int pageSize) {
       // order = order == null ? " id desc" : order;
        order = "status asc,time desc";
        String sql = "select * from es_cf";
        sql += " order by  " + order;
        Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
        return webpage;
    }
    
    public List list() {
        String sql = "select * from es_cf order by id";
        List cflist = this.baseDaoSupport.queryForList(sql, Cf.class);
        return cflist;
    }
    
	/*
	 * 更新众筹状态  0 未开始 1 进行中  2已完成
	 */
    public boolean updateStatus()
    {     
        String cutime=format.format(date); 
        String sql = "select * from es_cf  order by id";
        List<Cf> cflist = this.baseDaoSupport.queryForList(sql, Cf.class);
        for(Cf cur:cflist)
        {
            String overtime = cur.getTime();
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
            {
                cur.setStatus("2");
                String sqlup = "update es_cf set status='2' where id="+cur.getId();
                this.baseDaoSupport.execute(sqlup);
            }
        }
        //System.out.println("更新众筹状态");
        return true;
    }
    
    //后台添加众筹活动商品
    public boolean addCFGoods(int cfId, Integer[] goodsIds) {
        try {
            String sql = "delete from es_cf_goods where cf_id=?";
            this.baseDaoSupport.execute(sql, cfId);
            
            for (Integer goodsId : goodsIds) {
                Map<String, Object> paraMap = new HashMap<String, Object>();
                paraMap.put("cf_id", cfId);
                paraMap.put("goods_id", goodsId);
                sql = "insert into es_cf_goods(?, ?) ";
                this.baseDaoSupport.insert("es_cf_goods", paraMap);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
	
	public Page get(int pageNo, int pageSize) {
		return this.baseDaoSupport.queryForPage("select * from es_cf order by id", pageNo, pageSize);
	}
	
	public int getGoodsCount(int cfId, int goodsId) {
	    String sql = "select count(*) from es_cf_goods where cf_id=? and goods_id=?";
	    return this.baseDaoSupport.queryForInt(sql, cfId, goodsId);
	}
	
}
