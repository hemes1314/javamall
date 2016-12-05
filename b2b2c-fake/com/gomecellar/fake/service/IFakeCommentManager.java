package com.gomecellar.fake.service;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.Goods;
import com.enation.framework.database.Page;
import com.gomecellar.fake.model.MemberCommentKeyWords;


/**
 * 关键词管理
 * @author wangli-tri
 */
public interface IFakeCommentManager {
    
    /**
     * 关键词搜索 带分页
     * @param commentMap
     * @param page
     * @param pageSize
     * @param other
     * @return
     */
    public Page searchCommentsKeyWords(Integer page,Integer pageSize,String other,String order);
    
    /*删除数据*/
    public void deleteCommentKeyWordsById(String id);
    /*增加数据*/
    public void saveCommentKeyWords(MemberCommentKeyWords model);
    /*修改数据状态*/
    public void updateCommentKeyWordsByStatus(String id);
    /*单记录查询*/
    public MemberCommentKeyWords getByCommentKeyWordsId(String id);
    /*获取id集合*/
    public List<MemberCommentKeyWords> getCommentKeyWordsIds();
    /*更新数据关键字*/
    public void updateCommentKeyWords(MemberCommentKeyWords model);
    /*伪造商品销售量*/
    public void updateGoodBuyCount(Integer goodId,Integer buy_count);
    /*伪造商品评论*/
    public void insertComments(Integer goodId,Integer count);
    /*获取评论关键字*/
    public MemberCommentKeyWords selectOneCommentsByCatId(Integer catId);
}
