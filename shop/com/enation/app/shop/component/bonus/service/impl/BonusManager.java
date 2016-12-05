package com.enation.app.shop.component.bonus.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.app.shop.core.model.Goods;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.ExcelUtil;
import com.enation.framework.util.StringUtil;

/**
 * 红包管理
 * @author kingapex
 *2013-8-13下午3:21:18
 */
@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
@Component
public class BonusManager extends BaseSupport implements IBonusManager {
	private IBonusTypeManager bonusTypeManager;
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private IMemberManager memberManager; 
	
	/**
	 * 更新某个红包类型的生成数量
	 * @param typeid
	 * @param count
	 */
	private void updateNum(int typeid,int count){
		this.baseDaoSupport.execute("update bonus_type set create_num=? where type_id=?", count,typeid);
	}
	private void increaseNum(int typeid,int count){
		this.baseDaoSupport.execute("update bonus_type set create_num=create_num+? where type_id=?", count,typeid);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int sendForMemberLv(int typeid,int lvid, int onlyEmailChecked) {
		
		String sql ="select * from member where lv_id=? ";
		if(onlyEmailChecked==1){
			sql+=" and is_checked=1";
		}
		List<Member> mebmerList  =this.baseDaoSupport.queryForList(sql,Member.class, lvid);
		
		int count=0;
		BonusType bonusType = this.bonusTypeManager.get(typeid);
		//插入会员红包表
		count =this.send(mebmerList, typeid,bonusType.getType_name(),bonusType.getSend_type());
		this.increaseNum(typeid, count);
		return count;
	} 

	private int send(List<Member> mebmerList,int typeid,String type_name,int bonus_type){
		int count=0;
		//插入会员红包表
		for(Member member:mebmerList){
			Map memberBonus=new HashMap();
			memberBonus.put("bonus_type_id", typeid);
			memberBonus.put("member_id",member.getMember_id() );
			memberBonus.put("type_name",type_name );
			memberBonus.put("bonus_type",bonus_type );
			memberBonus.put("member_name",member.getUname()+"["+member.getName()+"]" );
			memberBonus.put("emailed",0 );
			memberBonus.put("create_time", DateUtil.getDateline());
			this.baseDaoSupport.insert("member_bonus",memberBonus);
			count++;
		}
		return count;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.bonus.service.IBronusManager#sendForMember(int, java.lang.Integer[])
	 */
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int sendForMember(int typeid,Long[] memberids) {
		if(memberids==null || memberids.length==0) return 0;
		
		String sql ="select * from member where member_id in("+StringUtil.arrayToString(memberids, ",")+") ";
		List<Member> mebmerList  =this.baseDaoSupport.queryForList(sql,Member.class);
		int count =0;
		BonusType bonusType = this.bonusTypeManager.get(typeid);
		count  = this.send(mebmerList, typeid,bonusType.getType_name(),bonusType.getSend_type());
		this.increaseNum(typeid, count);
		return count;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.bonus.service.IBronusManager#sendForGoods(int, java.lang.Integer[])
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int sendForGoods(int typeid,Integer[] goodsids) {
		
		if(goodsids==null || goodsids.length==0) return 0;
		this.baseDaoSupport.execute("delete from bonus_goods where bonus_type_id=?", typeid);
		String sql ="select * from goods where goods_id in("+StringUtil.arrayToString(goodsids, ",")+")";
		List<Goods> goodsList = this.baseDaoSupport.queryForList(sql,Goods.class);
		for(Goods goods:goodsList){
			this.baseDaoSupport.execute("insert into bonus_goods(bonus_type_id,goods_id)values(?,?)", typeid,goods.getGoods_id());
		}
		int count = goodsList.size();
		this.updateNum(typeid, count);
		return count;
	}

	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int sendForOffLine(int typeid,int count) {
		Set<String> cardNoSet = new HashSet();
		int successCount = 0;
		int i=0;
		try {
			BonusType bronusType =	this.bonusTypeManager.get(typeid);
			String prefix = bronusType.getRecognition();
			
			int step =10;
			if(count>1000){
				step=100;
			}
			
			Set<MemberBonus> bronusSet = new HashSet();
			
			while(cardNoSet.size() < count){
				String sn =this.createSn(prefix);
				int c= this.baseDaoSupport.queryForInt("select count(0) from member_bonus where bonus_sn=?", sn);
				if( cardNoSet.contains(sn) ||c>0){
					continue;
				}
				cardNoSet.add(sn);//压入sn
				
				MemberBonus bronus = new MemberBonus();
				bronus.setBonus_type_id(typeid);
				bronus.setBonus_sn(sn);
				bronusSet.add(bronus);
				
				if(bronusSet.size() >= step) {
					this.batchCreate(bronusSet,bronusType);//批量生成
					int size = bronusSet.size();
					successCount += size;
					bronusSet.clear();
				}
				i++;
			}
			
			if(bronusSet.size() > 0) {
				this.batchCreate(bronusSet,bronusType);//批量生成
				int size = bronusSet.size();
				successCount += size;
				bronusSet.clear();
			}
			 
		} catch (Throwable e) {
			this.logger.error("生成个优惠卷第["+i+"]出错，已生成["+successCount+"]个",e);
		}
		
		this.increaseNum(typeid, successCount);

		return successCount;
	}
	
	
	/**
	 * 批量生成优惠卷码
	 * @param bronusSet
	 */
	private void batchCreate(Set<MemberBonus> bronusSet,BonusType bronusType){
		List list =new ArrayList();
		Iterator<MemberBonus> itor=bronusSet.iterator();
		
		while(itor.hasNext()){
			MemberBonus bronus=itor.next();
			Object[] params = new Object[5];
			params[0]=bronus.getBonus_type_id();
			params[1]= bronus.getBonus_sn();
			params[2]= bronusType.getType_name();
			params[3]=bronusType.getSend_type();
			params[4]=DateUtil.getDateline();
			list.add(params);
		}
		simpleJdbcTemplate.batchUpdate("insert into es_member_bonus(bonus_type_id,bonus_sn,type_name,bonus_type,create_time,emailed)values(?,?,?,?,?,0)", list);
	}
	
	
	private String createSn(String prefix){
		
		StringBuffer sb = new StringBuffer();
		sb.append(prefix);
		sb.append( DateUtil.toString(new Date(), "yyMM"));
		sb.append( createRandom() );
		
		return sb.toString();
	}
	
	private String createRandom(){
		Random random  = new Random();
		StringBuffer pwd=new StringBuffer();
		for(int i=0;i<6;i++){
			pwd.append(random.nextInt(9));
			 
		}
		return pwd.toString();
	}
	
	@Override
	public Page list(int page, int pageSize, int typeid) {
		String sql="select * from es_member_bonus where bonus_type_id=? order by bonus_id asc";
		Page webPage = baseDaoSupport.queryForPage(sql, page,pageSize, MemberBonus.class,typeid);
		return webPage;
	}

	@Override
	public List<Map> getMemberBonusList(long memberid, Double goodsprice,Integer type) {
		String sql="select mb.bonus_id,bt.*,mb.order_sn as order_sn from es_bonus_type bt,es_member_bonus mb where "
				+" bt.type_id=mb.bonus_type_id and mb.member_id=?  and bt.min_goods_amount<=?";
		if(type==0){
			sql+="and  order_sn is NULL ";
		}
		long now =DateUtil.getDateline();
		sql+=" and bt.use_start_date<="+now;
		sql+=" and bt.use_end_date>="+now;
		sql+=" order by bonus_id asc";
		
		return this.daoSupport.queryForList(sql, memberid,goodsprice);
	}

	@Override
	public List<Map> getMemberBonusListForApp(long memberid, Double goodsprice,Integer type) {
		String sql="select mb.bonus_id,mb.bonus_sn,bt.type_name,bt.type_money from es_bonus_type bt,es_member_bonus mb where "
				+" bt.type_id=mb.bonus_type_id and mb.member_id=?  and bt.min_goods_amount<=?";
		if(type==0){
			sql+="and  order_sn is NULL ";
		}
		long now =DateUtil.getDateline();
		sql+=" and bt.use_start_date<="+now;
		sql+=" and bt.use_end_date>="+now;
		sql+=" order by bonus_id asc";
		
		return this.daoSupport.queryForList(sql, memberid,goodsprice);
	}
	@Override
	public void delete(int bronusid) {
		this.baseDaoSupport.execute("delete from member_bonus where bonus_id=?", bronusid);
	}
	
	@Override
	public List<Map> getGoodsList(int typeid) {
		String sql="select g.goods_id,g.name from "+ this.getTableName("goods")+" g ,"+this.getTableName("bonus_goods")+" bg where bg.goods_id =g.goods_id and bg.bonus_type_id=?";
		return this.daoSupport.queryForList(sql, typeid);
	}

	
	@Override
	public String exportToExcel(int typeid) {
		try {
			
	
		BonusType  bonusType =this.bonusTypeManager.get(typeid);
		String sql="select * from member_bonus where bonus_type_id=? order by bonus_id asc";
		List<MemberBonus> list = this.baseDaoSupport.queryForList(sql,MemberBonus.class, typeid);
		
		ExcelUtil excelUtil = new ExcelUtil(); 
		
		String app_apth = StringUtil.getRootPath();

		InputStream in = new FileInputStream(new File(app_apth+"/WEB-INF/excel/bonus.xls")) ;// FileUtil.getResourceAsStream("com/enation/app/shop/component/bonus/service/impl/bonus_list.xls");
		
		excelUtil.openModal( in );
		int i=1;
		for (MemberBonus memberBonus : list) {
			excelUtil.writeStringToCell(i, 0,memberBonus.getBonus_sn() ); //号码
			excelUtil.writeStringToCell(i, 1,bonusType.getType_money().toString()); //金额
			excelUtil.writeStringToCell(i, 2,bonusType.getType_name()); //类型名称
			long time = bonusType.getUse_end_date();
			excelUtil.writeStringToCell(i, 3, DateUtil.toString( new Date(time*1000),"yyyy年MM月dd")   ); //类型名称
			i++;
		}
		String static_server_path= SystemSetting.getStatic_server_path();
		String filePath =static_server_path+"/bouns_excel/"+typeid+".xls";
		excelUtil.writeToFile(filePath);
		return filePath;
		} catch (Exception e) {
			e.printStackTrace();
		  return null;
		}
	}
	
	@Override
	public Page pageList(int page, int pageSize, long memberid) {
	   Page pages=this.daoSupport.queryForPage("select bo.*,botype.type_id,botype.min_amount,botype.type_money,botype.use_end_date from es_member_bonus bo ,es_bonus_type botype where  bo.bonus_type_id =botype.type_id and member_id=? ", page, pageSize, memberid);
		return pages;
	}

	
	@Override
	public MemberBonus getBonus(int bounusid) {
		

		String sql="select mb.*,bt.type_money bonus_money,bt.min_goods_amount,bt.use_start_date,bt.use_end_date  from "+this.getTableName("member_bonus")+" mb , "+this.getTableName("bonus_type")
				+" bt where  bt.type_id=mb.bonus_type_id and mb.bonus_id=? ";
		
		
		return (MemberBonus)this.daoSupport.queryForObject(sql, MemberBonus.class, bounusid);
	}
	
	@Override
	public MemberBonus getBonus(String sn) {
		String sql="select mb.*,bt.type_money bonus_money,bt.min_goods_amount,bt.use_start_date,bt.use_end_date  from "+this.getTableName("member_bonus")+" mb , "+this.getTableName("bonus_type")
				+" bt where  bt.type_id=mb.bonus_type_id and mb.bonus_sn=? ";
		
		return (MemberBonus)this.daoSupport.queryForObject(sql, MemberBonus.class, sn);
	}
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void use(int bonusid, long memberid, Integer orderid,String ordersn,int bonus_type_id) {
		Member member  = this.memberManager.get(memberid);
		
		String sql="update member_bonus set order_id=?,order_sn=?,member_id=?,used_time=?,member_name=?,used = 1 where bonus_id=?";
		this.baseDaoSupport.execute(sql, orderid,ordersn,memberid,DateUtil.getDateline(),member.getUname()+"-"+member.getName(),bonusid);
		
		this.baseDaoSupport.execute("update es_bonus_type set use_num=use_num+1 where type_id=?",bonus_type_id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void returned(int orderid) {
		String sql="update member_bonus set order_sn=null,used_time=null,order_id=null,member_name=null  where order_id=?";
		this.baseDaoSupport.execute(sql, orderid);
		
	}
	
	
	public IBonusTypeManager getBonusTypeManager() {
		return bonusTypeManager;
	}

	public void setBonusTypeManager(IBonusTypeManager bonusTypeManager) {
		this.bonusTypeManager = bonusTypeManager;
	}

	public SimpleJdbcTemplate getSimpleJdbcTemplate() {
		return simpleJdbcTemplate;
	}

	public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
		this.simpleJdbcTemplate = simpleJdbcTemplate;
	}
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
	
	
	@Override
	public Page getMemberBonusList(long memberid, Double goodsprice,
			Integer type, int pageNo, int pageSize) {
		String sql="select mb.bonus_id,bt.*,mb.order_sn as order_sn from es_bonus_type bt,es_member_bonus mb where "
				+" bt.type_id=mb.bonus_type_id and mb.member_id=?  and bt.min_goods_amount<=?";
		if(type==0){
			sql+="and order_sn is NULL ";
		}
		long now =DateUtil.getDateline();
		sql+=" and bt.use_start_date<="+now;
		sql+=" and bt.use_end_date>="+now;
		sql+=" order by bonus_id asc";
		
		return this.daoSupport.queryForPage(sql, pageNo, pageSize, memberid,goodsprice);
	}
    @Override
    public Page getBonusListForApp(int pageNo , int pageSize ,long member_id, Integer type ,int cat) {
        String  sql = "select mb.bonus_id,mb.bonus_sn,bt.type_name,bt.type_money, bt.send_start_date,bt.use_end_date,"
                + " min_goods_amount ,bt.img_bonus,is_given ,bt.store_id ,s.store_name from es_member_bonus mb left join es_bonus_type bt on "
                + " bt.type_id=mb.bonus_type_id left join es_store s on bt.store_id = s.store_id  where mb.member_id=? and bt.type_money is not null ";
        long now =DateUtil.getDateline();
        if(type ==1){ //未使用
            sql += " and mb.used =0 and mb.used_time is  null and bt.use_end_date>"+now ;
        }else if(type==2){//已使用
            sql += " and mb.used_time is not null  " ;
        }else if(type==3){//已过期
            sql += " and bt.use_end_date<"+now ;
        }
        if(cat==1){
            sql += " and bt.store_id is not null ";
        }else {
            sql += " and bt.store_id is  null ";
        }
         
        return this.daoSupport.queryForPage(sql, pageNo, pageSize, member_id);
    }
	
	

	


}
