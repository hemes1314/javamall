package com.enation.app.b2b2c.core.service.member.impl;

import com.enation.app.b2b2c.component.plugin.goods.StoreGoodsCommentsBundle;
import com.enation.app.b2b2c.core.model.member.StoreMemberComment;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberCommentManager;
import com.enation.app.shop.core.model.ShopOpenApi;
import com.enation.app.shop.core.openapi.service.ICommentOpenApiManager;
import com.enation.app.shop.core.openapi.service.IShopOpenApiiManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
@Component
public class StoreMemberCommentManager extends BaseSupport implements IStoreMemberCommentManager {
	
    private StoreGoodsCommentsBundle storeGoodsCommentsBundle;
    private IStoreGoodsManager storeGoodsManager;
    private ICommentOpenApiManager commentOpenApiManager;
	private IShopOpenApiiManager shopOpenApiManager;

	public IShopOpenApiiManager getShopOpenApiManager() {
		return shopOpenApiManager;
	}

	public void setShopOpenApiManager(IShopOpenApiiManager shopOpenApiManager) {
		this.shopOpenApiManager = shopOpenApiManager;
	}

	public ICommentOpenApiManager getCommentOpenApiManager() {
		return commentOpenApiManager;
	}

	public void setCommentOpenApiManager(ICommentOpenApiManager commentOpenApiManager) {
		this.commentOpenApiManager = commentOpenApiManager;
	}

	/*
         * (non-Javadoc)
         * @see com.enation.app.b2b2c.core.service.member.IStoreMemberCommentManager#getAllComments(int, int, java.util.Map, java.lang.Integer)
         */
	@Override
	public Page getAllComments(int page, int pageSize, Map map, Integer store_id) {
		String sql=this.createTemlSql(map, store_id);
		return this.daoSupport.queryForPage(sql, page, pageSize,store_id);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.member.IStoreMemberCommentManager#get(java.lang.Integer)
	 */
	@Override
	public Map get(Integer comment_id) {
		return this.daoSupport.queryForMap("SELECT * FROM es_member_comment WHERE comment_id=?", comment_id);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.member.IStoreMemberCommentManager#edit(java.util.Map, java.lang.Integer)
	 */
	@Override
	public void edit(Map map,Integer comment_id) {
		this.daoSupport.update("es_member_comment", map, "comment_id="+comment_id);
	}
	
	private String createTemlSql(Map map,Integer store_id){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT m.uname as uname,g.name as goods_name,c.* from es_member_comment c LEFT JOIN es_goods g ON c.goods_id=g.goods_id" +
				" LEFT JOIN es_member m ON c.member_id=m.member_id WHERE g.store_id=? and c.type="+map.get("type"));
		if(map.get("stype")!=null){
			if(map.get("stype").equals("0")){
				if(map.get("keyword")!=null){
					sql.append(" and (m.uname like '%"+map.get("keyword")+"%'");
					sql.append(" or c.content like '%"+map.get("keyword")+"%'");
					sql.append(" or g.name like '%"+map.get("keyword")+"%')");
				}
			}else{
				if(map.get("mname")!=null){
					sql.append(" and m.uname like '%"+map.get("mname")+"%'");
				}
				if(map.get("gname")!=null){
					sql.append(" and g.name like '%"+map.get("gname")+"%'");
				}
				if(map.get("content")!=null){
					sql.append(" and c.content like '%"+map.get("content")+"%'");
				}
			}
			if( map.get("status")!=null){
				sql.append(" and c.status="+ map.get("status"));
			}
			if(map.get("replyStatus")!=null&&!map.get("replyStatus").equals("-1")){
				sql.append(" and c.replystatus ="+map.get("replyStatus"));
			}
		}
		if(map.get("grade")!=null)
			if (NumberUtils.toInt(map.get("grade").toString()) != -1) {
				sql.append(" and c.grade="+map.get("grade"));
			}
		sql.append(" ORDER BY c.comment_id DESC");
		//System.out.println(sql);
		return sql.toString();
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.member.IStoreMemberCommentManager#add(com.enation.app.b2b2c.core.model.member.StoreMemberComment)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(StoreMemberComment memberComment) {
		//添加评论、咨询
		this.daoSupport.insert("es_member_comment", memberComment);
		commentOpenApiManager.insert(memberComment);
		//修改店铺评价信息
		this.updateStoreComment(memberComment);
		//合并code
		// 如果该条信息是评论（咨询不算评论 不增量）
        if(memberComment.getGrade() > 0) {       
            //添加商品评论次数
            this.storeGoodsManager.addStoreGoodsComment(memberComment.getGoods_id());
        }
        this.storeGoodsCommentsBundle.onGoodsCommentsAfterAdd(memberComment);
	}
	/**
	 * 修改店铺评价
	 * @param memberComment
	 */
	private void updateStoreComment(StoreMemberComment memberComment){
		//orcale 数据库异常解决
		/*
		String sql="select store_id,(SELECT COUNT(comment_id)from es_member_comment WHERE grade=3)as grade,sum(store_desccredit) as store_desccredit ,SUM(store_servicecredit)as store_servicecredit,SUM(store_deliverycredit) as store_deliverycredit ,COUNT(comment_id) as comment_num from es_member_comment WHERE store_id=? GROUP BY  STORE_ID";
		Map map= this.daoSupport.queryForMap(sql, memberComment.getStore_id());
		*/
		/*********************** 2015/12/24 humaodong **********************/
		Integer storeId = memberComment.getStore_id();
		String sql="select store_id,(SELECT COUNT(comment_id)from es_member_comment WHERE store_id=? and grade=3)as grade,sum(store_desccredit) as store_desccredit ,SUM(store_servicecredit)as store_servicecredit,SUM(store_deliverycredit) as store_deliverycredit ,COUNT(comment_id) as comment_num from es_member_comment WHERE store_id=? GROUP BY  STORE_ID";
		Map map= this.daoSupport.queryForMap(sql, storeId, storeId);
		/*******************************************************************/
		double grade = NumberUtils.toInt(map.get("grade").toString());//除法运算 int 会得出0  liushuai 2015-9-24
		Double store_desccredit = NumberUtils.toDouble(map.get("store_desccredit").toString());
		Double store_servicecredit = NumberUtils.toDouble(map.get("store_servicecredit").toString());
		Double store_deliverycredit = NumberUtils.toDouble(map.get("store_deliverycredit").toString());
		double comment_num = NumberUtils.toInt(map.get("comment_num").toString());//除法运算 int 会得出0  liushuai 2015-9-24
		
		Double store_credit=Double.valueOf((store_desccredit+store_servicecredit+store_deliverycredit)/(double) 3/comment_num);   //店铺信用由描述+服务+发货的平均值构成d  whj 2015-06-12 
		int store_credit_num = NumberUtils.toInt(new java.text.DecimalFormat("0").format(store_credit));                           //将浮点型店铺信用转换成int型.小叔四舍五入变成整数(store模型就是int型)。 whj 2015-06-12
		
		Map storeInfo=new HashMap(); 
		storeInfo.put("praise_rate",grade/comment_num);
		storeInfo.put("store_desccredit", store_desccredit/comment_num);
		storeInfo.put("store_servicecredit", store_servicecredit/comment_num);
		storeInfo.put("store_deliverycredit", store_deliverycredit/comment_num);
		storeInfo.put("store_credit", store_credit_num);
		ShopOpenApi shopOpenApi = new ShopOpenApi();
		shopOpenApi.setShopId(memberComment.getStore_id()+"");
		shopOpenApi.setDescribeMatchScore(store_desccredit/comment_num);
		shopOpenApi.setPostSpeedScore(store_deliverycredit/comment_num);
		shopOpenApi.setServiceLevelScore(store_servicecredit/comment_num);
		this.daoSupport.update("es_store", storeInfo, "store_id="+memberComment.getStore_id());
		shopOpenApiManager.updateShopScore(shopOpenApi);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.member.IStoreMemberCommentManager#getCommentCount(java.lang.Integer)
	 */
	@Override
	public Integer getCommentCount(Integer type,Integer store_id) {
		String sql="SELECT count(0) from es_member_comment c WHERE c.type=? AND c.replystatus=0 and store_id=?";
		return this.daoSupport.queryForInt(sql, type,store_id);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.member.IStoreMemberCommentManager#getGoodsStore_desccredit(java.lang.Integer)
	 */
	@Override
	public Double getGoodsStore_desccredit(Integer goods_id) {
		//String sql="select AVG(store_deliverycredit) as store_deliverycredit  from es_member_comment WHERE type=1 AND goods_id=?";
	     // update by lxl 修改计算商品评价方法
		String sql=" select  round(sum(tb.a+tb.b+tb.c)/3) as store_deliverycredit from (select AVG(store_deliverycredit) as a,AVG(STORE_DESCCREDIT) as b ,AVG(STORE_SERVICECREDIT) as c from es_member_comment WHERE type=1 AND goods_id=?) tb";
		Map map=this.daoSupport.queryForMap(sql, goods_id);
		return NumberUtils.toDouble(map.get("store_deliverycredit").toString());
	}
    
    public StoreGoodsCommentsBundle getStoreGoodsCommentsBundle() {
        return storeGoodsCommentsBundle;
    }
    
    public void setStoreGoodsCommentsBundle(StoreGoodsCommentsBundle storeGoodsCommentsBundle) {
        this.storeGoodsCommentsBundle = storeGoodsCommentsBundle;
    }
    
    public IStoreGoodsManager getStoreGoodsManager() {
        return storeGoodsManager;
    }
    
    public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
        this.storeGoodsManager = storeGoodsManager;
    }
	
}
