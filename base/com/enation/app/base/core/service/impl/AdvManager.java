package com.enation.app.base.core.service.impl;

import java.util.Date;
import java.util.List;

import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.model.AdvMapper;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 广告管理
 * 
 * @author 李志富 lzf<br/>
 *         2010-2-4 下午03:55:33<br/>
 *         version 1.0<br/>
 * <br/>
 * @author LiFenLong 2014-4-1;4.0版本改造,修改deadvs方法参数为integer[]
 */
public class AdvManager extends BaseSupport<Adv> implements IAdvManager {

	
	public void addAdv(Adv adv) {
		this.baseDaoSupport.insert("adv", adv);

	}

	
	public void delAdvs(Integer[] ids) {
		if (ids == null || ids.equals(""))
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from adv where aid in (" + id_str
				+ ")";
		this.baseDaoSupport.execute(sql);
	}

	
	public Adv getAdvDetail(Long advid) {
		Adv adv = this.baseDaoSupport.queryForObject("select * from adv where aid = ?", Adv.class, advid);
		String pic  = adv.getAtturl();
		if(pic!=null){
			pic  =UploadUtil.replacePath(pic); 
			adv.setAtturl(pic);
		}
		return adv;
	}

	
	public Page pageAdv(String order, int page, int pageSize) {
		order = order == null ? " aid desc" : order;
		String sql = "select v.*, c.cname   cname from " + this.getTableName("adv") + " v left join " + this.getTableName("adcolumn") + " c on c.acid = v.acid";
		sql += " order by " + order; 
		Page rpage = this.daoSupport.queryForPage(sql, page, pageSize,new AdvMapper());
		return rpage;
	}

	
	public void updateAdv(Adv adv) {
		this.baseDaoSupport.update("adv", adv, "aid = " + adv.getAid());

	}
	
	
	public List listAdv(Long acid) {
		Long nowtime = (new Date()).getTime();
		
		List<Adv> list = this.baseDaoSupport.queryForList("select a.*,'' cname from adv a where acid = ? and isclose = 0", new AdvMapper(), acid);
		return list;
	}


	@Override
	public Page search(Long acid, String cname,String startTime1,String endTime1,String startTime2,String endTime2,int pageNo,int pageSize,String order) {
		StringBuffer term  = new StringBuffer();
		StringBuffer sql = new StringBuffer( "select v.*, c.cname  cname from " + this.getTableName("adv") + " v left join " + this.getTableName("adcolumn") + " c on c.acid = v.acid ");
		
		if(acid!=null){
			term.append(" where  c.acid="+ acid);
		}
		
		if(!StringUtil.isEmpty(cname)){
			if(term.length()>0){
				term.append(" and ");
			}
			else
			{
				term.append(" where ");
			}
			
			term.append(" aname like'%"+cname+"%'");
		}

		//added by ken 20160303

		if (startTime1 != null && !startTime1.isEmpty()) {
			Long startTimeLong1 = DateUtil.getDateline(startTime1, "yyyy-MM-dd HH:mm:ss");

			if(term.length()>0){
				term.append(" and ");
			}
			else
			{
				term.append(" where ");
			}
			term.append("begintime >=").append(startTimeLong1);

		}

		if (endTime1 != null && !endTime1.isEmpty()) {
			Long endTimeLong1 = DateUtil.getDateline(endTime1, "yyyy-MM-dd HH:mm:ss");

			if(term.length()>0){
				term.append(" and ");
			}
			else
			{
				term.append(" where ");
			}
			term.append("begintime <=").append(endTimeLong1);
		}

		if (startTime2 != null && !startTime2.isEmpty()) {
			Long startTimeLong2 = DateUtil.getDateline(startTime1, "yyyy-MM-dd HH:mm:ss");

			if(term.length()>0){
				term.append(" and ");
			}
			else
			{
				term.append(" where ");
			}
			term.append("endtime >=").append(startTimeLong2);

		}

		if (endTime2 != null && !endTime2.isEmpty()) {
			Long endTimeLong2 = DateUtil.getDateline(endTime1, "yyyy-MM-dd HH:mm:ss");

			if(term.length()>0){
				term.append(" and ");
			}
			else
			{
				term.append(" where ");
			}
			term.append("endtime <=").append(endTimeLong2);
		}

		//end of add by ken 20160303



		sql.append(term);
		
		order = order == null ? " aid desc" : order;
		sql.append(" order by " + order );
		
		Page page = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
		return page;
	}
	
	 

}
