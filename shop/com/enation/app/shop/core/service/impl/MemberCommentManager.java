package com.enation.app.shop.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.MemberComment;
import com.enation.app.shop.core.openapi.service.ICommentOpenApiManager;
import com.enation.app.shop.core.openapi.service.IWareOpenApiManager;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.redis.SystemConstants;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 
 * @author LiFenLong 2014-4-1;4.0版本改造，修改delete方法
 *
 */
public class MemberCommentManager extends BaseSupport<MemberComment> implements IMemberCommentManager{
	/**
	 * 增加openapi的方法
	 */
	private ICommentOpenApiManager commentOpenApiManager;

	public ICommentOpenApiManager getCommentOpenApiManager() {
		return commentOpenApiManager;
	}

	public void setCommentOpenApiManager(ICommentOpenApiManager commentOpenApiManager) {
		this.commentOpenApiManager = commentOpenApiManager;
	}

	@Override
	public void add(MemberComment memberComment) {
//		this.daoSupport.execute("INSERT INTO es_member_comment(goods_id, member_id,content,img,dateline,ip,reply,replytime,status,type,replystatus,grade) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)",
//				memberComment.getGoods_id(),memberComment.getMember_id(),memberComment.getContent(),memberComment.getImg(),memberComment.getDateline(),
//				memberComment.getIp(),"",0,0,memberComment.getType(),0,memberComment.getGrade());	
		this.baseDaoSupport.insert("member_comment", memberComment);
		commentOpenApiManager.insert(memberComment);
	}

	@Override
	public Page getGoodsComments(int goods_id, int page, int pageSize, int type) {
		return this.daoSupport.queryForPage("SELECT m.lv_id,m.sex,m.uname,m.face,l.name as levelname,c.* FROM " + this.getTableName("member_comment") + " c LEFT JOIN " + this.getTableName("member") + " m ON c.member_id=m.member_id LEFT JOIN " + this.getTableName("member_lv") + " l ON m.lv_id=l.lv_id " +
				"WHERE c.goods_id=? AND c.type=? AND c.status=1 ORDER BY c.dateline DESC", page, pageSize, goods_id, type);
	}
	
	public int getGoodsGrade(int goods_id){
		int sumGrade = this.baseDaoSupport.queryForInt("SELECT SUM(grade) FROM member_comment WHERE status=1 AND goods_id=? AND type=1", goods_id);
		int total = this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_comment WHERE status=1 AND goods_id=? AND type=1", goods_id);
		if(sumGrade != 0 && total != 0){
			return (sumGrade/total);
		}else{
			return 0;
		}
	}
	
	public Page getAllComments(int page, int pageSize, int type){
		String sql="SELECT m.uname muname,g.name gname,c.* FROM " + this.getTableName("member_comment") + " c LEFT JOIN " + this.getTableName("goods") + " g ON c.goods_id=g.goods_id LEFT JOIN " + this.getTableName("member") + " m ON c.member_id=m.member_id " +
		"WHERE c.type=? ORDER BY c.comment_id DESC";
		//System.out.println(sql);
		return this.daoSupport.queryForPage(sql, page, pageSize, type);
	}

	@Override
	public MemberComment get(int comment_id) {
		return this.baseDaoSupport.queryForObject("SELECT * FROM member_comment WHERE comment_id=?", MemberComment.class, comment_id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void update(MemberComment memberComment) {
		this.baseDaoSupport.update("member_comment", memberComment, "comment_id="+memberComment.getComment_id());
		if(memberComment.getStatus()==1){
			String updatesql = "update es_goods set grade=grade+1 where goods_id=?";
			this.daoSupport.execute(updatesql,memberComment.getGoods_id());
			 //hp清除缓存
            com.enation.framework.cache.ICache iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
            iCache.remove(String.valueOf(memberComment.getGoods_id()));

		}
		commentOpenApiManager.update(memberComment);
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map statistics(int goodsid){
		String sql="select grade,count(grade) num from es_member_comment c where c.goods_id =? GROUP BY c.grade ";
		List<Map> gradeList =this.daoSupport.queryForList(sql, goodsid);
		Map gradeMap  = new HashMap();
		for(Map grade:gradeList){
			Object gradeValue =grade.get("grade");
			long num = NumberUtils.toLong(grade.get("num").toString().trim());
			gradeMap.put("grade_"+gradeValue, num);
		}
		return gradeMap;
	}
	
	@Override
	public int getGoodsCommentsCount(int goods_id) {
		return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_comment WHERE goods_id=? AND status=1 AND type=1", goods_id);
	}

	@Override
	public void delete(Integer[] comment_id) {
		if (comment_id== null || comment_id.equals(""))
			return;
		String id_str = StringUtil.arrayToString(comment_id, ",");
		String sql = "DELETE FROM member_comment where comment_id in (" + id_str + ")";
		this.baseDaoSupport.execute(sql);
		commentOpenApiManager.delete(comment_id);
		
	}

	@Override
	public Page getMemberComments(int page, int pageSize, int type, long member_id) {
	    String sql = "SELECT g.name,g.cat_id, c.* FROM " + this.getTableName("member_comment") + " c," + this.getTableName("goods") + " g where c.goods_id=g.goods_id " +
                "and c.type=? AND c.member_id=? ORDER BY c.comment_id DESC";
	    
	    Page tempPage = this.daoSupport.queryForPage(sql, page, pageSize, type, member_id);
	    
		return tempPage;
	}

	@Override
	public int getMemberCommentTotal(long member_id, int type) {
		return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_comment WHERE member_id=? AND type=?", member_id, type);
	}

	@Override
	public Page getCommentsByStatus(int page, int pageSize, int type, int status) {
		return this.daoSupport.queryForPage("SELECT m.uname,g.name,c.* FROM " + this.getTableName("member_comment") + " c LEFT JOIN " + this.getTableName("goods") + " g ON c.goods_id=g.goods_id LEFT JOIN " + this.getTableName("member") + " m ON c.member_id=m.member_id " +
				"WHERE c.type=? and c.status = ? ORDER BY c.comment_id DESC", page, pageSize, type,status);
	}

	@Override
	public void deletealone(int comment_id) {
		
		this.baseDaoSupport.execute("DELETE FROM member_comment WHERE comment_id=?", comment_id);
		commentOpenApiManager.delete(comment_id);
	}
	
	/**
	 * 根据商品id获取商品评论总数
	 * @param goods_id
	 * @return
	 */
	@Override
	public int getCommentsCount(int goods_id) {
        return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_comment WHERE goods_id=? AND status=1 AND type=1", goods_id);
    }
	
	@Override
    public int getCommentsCount(int goods_id, int grade){
        return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_comment WHERE goods_id=? AND status=1 AND type=1 AND grade >= ?", goods_id, grade);
    }

	@Override
	public Page getCommentsForGoods(int page, int pageSize, long member_id,int type) {
		String sql ="select  g.small,g.thumbnail,g.name,g.buy_count, mc.goods_id,mc.content,"
				+ "mc.grade as member_grade,g.grade as goods_grade,mc.dateline,"
				+ "mc.reply,mc.replytime from es_member_comment mc "
				+ "left join es_goods g on g.goods_id = mc.goods_id where mc.type=? and mc.member_id=? order by mc.dateline desc";
		return this.baseDaoSupport.queryForPage(sql, page, pageSize,type,member_id);
	
	}

    @Override
    public Page getGoodsComments(Integer goods_id, int pageNo, int pageSize, Integer type, Integer grade) {
        String  sql = "SELECT m.lv_id,m.sex,m.uname,m.face,l.name as levelname,c.* FROM " + this.getTableName("member_comment") + " c LEFT JOIN " + this.getTableName("member") + " m ON c.member_id=m.member_id LEFT JOIN " + this.getTableName("member_lv") + " l ON m.lv_id=l.lv_id " ;
         sql += " where c.goods_id=? AND c.type=? AND c.status=1 " ;
         if (grade==2 || grade == 3 ){
             sql += " and c.grade=? ";
         }else{
             sql += " and c.grade <=? " ;
         }
         sql += " ORDER BY c.DATELINE DESC " ;
                
        return this.daoSupport.queryForPage(sql, pageNo, pageSize, goods_id, type,grade);

    }

	@Override
	public String syncOpenAPI() {
		List<MemberComment> list = this.daoSupport.queryForList("SELECT * FROM "+this.getTableName("member_comment"),MemberComment.class);
		String msg = commentOpenApiManager.syncOpenAPI(list);
		return msg;
	}

}
