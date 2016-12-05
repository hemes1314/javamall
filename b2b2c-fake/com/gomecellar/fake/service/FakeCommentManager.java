package com.gomecellar.fake.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.gomecellar.fake.model.MemberCommentKeyWords;

@Service
@Transactional
public class FakeCommentManager extends BaseSupport<MemberCommentKeyWords> implements IFakeCommentManager{

    @Override
    public Page searchCommentsKeyWords(Integer page, Integer pageSize, String other, String order) {
        // TODO Auto-generated method stub
        String sql="select * from ES_MEMBER_COMMENT_KEYWORDS t where t.status =1 order by t.cat_id";
        Page webPage = this.baseDaoSupport.queryForPage(sql.toString(), page, pageSize);
        return webPage;
    }

    @Override
    public void deleteCommentKeyWordsById(String id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void saveCommentKeyWords(MemberCommentKeyWords model) {
        this.daoSupport.insert("ES_MEMBER_COMMENT_KEYWORDS", model);
    }

    @Override
    public void updateCommentKeyWordsByStatus(String id) {
        // TODO Auto-generated method stub
        String sql = "update ES_MEMBER_COMMENT_KEYWORDS set status=0 where id = ?";
        this.baseDaoSupport.execute(sql,id);
    }

    @Override
    public MemberCommentKeyWords getByCommentKeyWordsId(String id) {
        String sql = "select * from  ES_MEMBER_COMMENT_KEYWORDS where id=?";
        MemberCommentKeyWords m = this.daoSupport.queryForObject(sql, MemberCommentKeyWords.class, id);
        return m;
    }

    @Override
    public List<MemberCommentKeyWords> getCommentKeyWordsIds() {
        String sql = "select * from ES_MEMBER_COMMENT_KEYWORDS t where t.status=1";
        return this.baseDaoSupport.queryForList(sql, MemberCommentKeyWords.class);
    }

    @Override
    public void updateCommentKeyWords(MemberCommentKeyWords model) {
        String sql ="update ES_MEMBER_COMMENT_KEYWORDS set key_words=? where id = ?";
        this.baseDaoSupport.execute(sql,model.getKey_words(),model.getId());
    }

    @Override
    public void updateGoodBuyCount(Integer goodId, Integer buy_count) {
        // TODO Auto-generated method stub
        String sql ="update es_goods set buy_count=? where goods_id = ?";
        this.baseDaoSupport.execute(sql,buy_count,goodId);
    }

    @Override
    public void insertComments(Integer goodId, Integer count) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public MemberCommentKeyWords selectOneCommentsByCatId(Integer catId) {
        String sql = "select * from  ES_MEMBER_COMMENT_KEYWORDS where cat_id=?";
        MemberCommentKeyWords m = this.daoSupport.queryForObject(sql, MemberCommentKeyWords.class, catId);
        return m;
    }
}
