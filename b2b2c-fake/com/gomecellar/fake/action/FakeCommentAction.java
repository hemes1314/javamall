package com.gomecellar.fake.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.member.StoreMemberComment;
import com.enation.app.b2b2c.core.model.store.StoreCat;
import com.enation.app.b2b2c.core.service.member.IStoreMemberCommentManager;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
import com.gomecellar.fake.model.GoodsForFake;
import com.gomecellar.fake.model.MemberCommentKeyWords;
import com.gomecellar.fake.model.MemberCommentsForFake;
import com.gomecellar.fake.service.IFakeCommentManager;
import com.gomecellar.fake.utils.KeyWordsUtil;
import com.gomecellar.fake.utils.RandomUserNameUtil;
import com.gomecellar.fake.utils.UUIDUtil;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;

/**
 * 伪造评论数据
 *
 * @author wangli-tri
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/b2b2c/admin")
@Action("fake")
@Results({
        @Result(name = "list", type = "freemarker", location = "/b2b2c/fake/fake_cat_list.html"),
        @Result(name = "goodslist", type = "freemarker", location = "/b2b2c/fake/fake_goods_list.html"),
        @Result(name = "fake_edit_goods", type = "freemarker", location = "/b2b2c/fake/fake_edit_goods.html"),
        @Result(name = "fake_edit_base_info", type = "freemarker", location = "/b2b2c/fake/fake_edit_base_info.html")
})
public class FakeCommentAction extends WWAction {

    private static final long serialVersionUID = 1L;
    private String optype = "no";
    @Autowired
    private IFakeCommentManager iFakeCommentManager;
    /*分类*/
    @Autowired
    private IGoodsCatManager goodsCatManager;
    private MemberCommentKeyWords memberCommentKeyWordsPage;
    @Autowired
    private IGoodsManager goodsManager;
    @Autowired
    private IStoreMemberCommentManager storeMemberCommentManager;
    @Autowired
    private IGoodsManager iGoodsManager;

    private String cat_name;
    private Integer status;
    private String key_words;
    private String id;
    private Integer cat_id;
    private Integer goods_id;
    private Integer goods_fake_salesNumber;
    private Integer goods_fake_commentsNumber;
    private GoodsForFake goods_fake;
    private Integer store_id;
    private String start_time;
    private String end_time;

    // 生成评论时间段起始时间后缀  结束时间后缀
    private String start_time_box = " 00:00:00";
    private String end_time_box = " 23:59:59";

    // 当天生成随机评论时间段是 凌晨5点
    private String start_time_suffix = " 05:00:00";
    private String end_time_box_suffix = " 23:59:59";

    /*引导列表页*/
    public String list() {
        return "list";
    }

    /*查看商品列表*/
    public String goodsList() {
        return "goodslist";
    }

    /*创建销售数据和评论数据*/
    public String generateFakeData() {
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        try {

            // 获取销售数量
            Goods good = iGoodsManager.getGoods(goods_id);
            int bycount  =  good.getBuy_count();
            int totaleCount = goods_fake_commentsNumber+bycount+goods_fake_salesNumber;
            // 修改销售数量
//            System.out.println(start_time + ":::" + end_time);
            if (goods_fake_commentsNumber != null || goods_fake_salesNumber!=null) {
                if (goods_fake_commentsNumber > 0 || goods_fake_salesNumber>0 ) {
                    //暂时不开通修改数量
                    this.iFakeCommentManager.updateGoodBuyCount(goods_id, totaleCount);
                }
            }

            // 获取分类评论关键字
            MemberCommentKeyWords keywords = this.iFakeCommentManager.selectOneCommentsByCatId(cat_id);
//            System.out.println(keywords.getKey_words());
            String keyWordsStr = keywords.getKey_words();

            if (keyWordsStr!=null && !"".equals(keyWordsStr)) {// 没有评论数
                List<String> keyWordsList = KeyWordsUtil.splitCommentsKeyWords(keywords.getKey_words(), goods_fake_commentsNumber, 2, 3);

                int max = 5;
                int min = 4;
                Random random = new Random();
                for (int i = 0; i < keyWordsList.size(); i++) {
//                  System.out.println(keyWordsList.get(i));
                  /*逐步添加数据*/
                    MemberCommentsForFake memberComment = new MemberCommentsForFake();
                    memberComment.setGoods_id(goods_id);
                    memberComment.setContent(keyWordsList.get(i));
                    memberComment.setStatus(1);
                    memberComment.setStore_id(store_id);
                    memberComment.setGrade(3);
                    memberComment.setIp("0:0:0:0:0:0:0:0");
                    memberComment.setType(1);
                    long date = KeyWordsUtil.randomDate(start_time + " " + start_time_box, end_time + " " + end_time_box, start_time_suffix, end_time_box_suffix);
//                  System.out.println(date/1000);
//                  System.out.println(System.currentTimeMillis()/1000);
                    memberComment.setDateline(date / 1000);
                    memberComment.setStore_deliverycredit(random.nextInt(max) % (max - min + 1) + min);
                    memberComment.setStore_desccredit(random.nextInt(max) % (max - min + 1) + min);
                    memberComment.setStore_servicecredit(random.nextInt(max) % (max - min + 1) + min);
                    //memberComment.setFake_name(RandomUserNameUtil.generate());// 伪造用户名

                    storeMemberCommentManager.add(memberComment);
                }
            }

            // 生成评论数量
            this.showSuccessJson("保存成功!");
        } catch (Exception e) {
            this.showErrorJson("保存失败!");
        }
        return JSON_MESSAGE;
    }

    /*获取商品概要信息*/
    public String getGoodDetail() {
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        String goodid = request.getParameter("goodsId");
        Goods good = goodsManager.getGoods(NumberUtils.toInt(goodid));
        goods_fake = new GoodsForFake();
        goods_fake.setStore_id(good.getStore_id());
        goods_fake.setGoods_id(good.getGoods_id());
        goods_fake.setSn(good.getSn());
        goods_fake.setGoods_fake_salesNumber(0);
        goods_fake.setGoods_fake_commentsNumber(0);
        goods_fake.setName(good.getName());
        goods_fake.setCat_id(good.getCat_id());
        // 查询分类
        List<Cat> catlist = this.goodsCatManager.listChildren(0);
        boolean hasName = true;
        for (Cat cat : catlist) {
            if (good.getCat_id().equals(cat.getCat_id())) {
                goods_fake.setCat_name(cat.getName());
                hasName = false;
                break;
            }
        }
        if (hasName) {
            goods_fake.setCat_name("此商品无分类！");
        }
        return "fake_edit_goods";
    }

    /*查询评论*/
    public String getCommentsKeyWordsById() {
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        String commentid = request.getParameter("id");
        String cat_name = request.getParameter("cat_name");
        memberCommentKeyWordsPage = this.iFakeCommentManager.getByCommentKeyWordsId(commentid);
        if (memberCommentKeyWordsPage.getKey_words() == null) {
            memberCommentKeyWordsPage.setKey_words("");
        }
        memberCommentKeyWordsPage.setCat_name(cat_name);
        return "fake_edit_base_info";
    }

    public String update() {
        MemberCommentKeyWords commentRecord = new MemberCommentKeyWords();
        commentRecord.setId(id);
        commentRecord.setKey_words(key_words);
        try {
            this.iFakeCommentManager.updateCommentKeyWords(commentRecord);
            this.showSuccessJson("保存成功!");
        } catch (Exception e) {
            this.showErrorJson("保存失败!");
        }
        return JSON_MESSAGE;
    }

    /*查询字典的表*/
    public String listJson() {

        // 查询分类
        List<Cat> catlist = this.goodsCatManager.listChildren(0);
        List<MemberCommentKeyWords> commentList = this.iFakeCommentManager.getCommentKeyWordsIds();
        // 初始化数据
        if (commentList.isEmpty()) {
            for (Cat cat : catlist) {
                MemberCommentKeyWords commentRecord = new MemberCommentKeyWords();
                commentRecord.setId(UUID.randomUUID().toString().trim().replaceAll("-", ""));
                commentRecord.setCat_id(cat.getCat_id());
                this.iFakeCommentManager.saveCommentKeyWords(commentRecord);
            }
        } else {
            // 补录数据
            for (Cat cat : catlist) {
                int cat_id = cat.getCat_id();
                boolean isadd = true;
                // 对比查询结果集
                for (MemberCommentKeyWords memberCommentKeyWords : commentList) {
                    if (cat_id == memberCommentKeyWords.getCat_id()) {
                        isadd = false;
                        break;
                    }
                }
                if (isadd) {
                    // 加入数据
                    MemberCommentKeyWords commentRecord = new MemberCommentKeyWords();
                    commentRecord.setId(UUIDUtil.generateUUID());
                    commentRecord.setCat_id(cat_id);
                    this.iFakeCommentManager.saveCommentKeyWords(commentRecord);
                }
            }

            // 反向对比 修改数据
            for (MemberCommentKeyWords memberCommentKeyWords : commentList) {
                int comment_id = memberCommentKeyWords.getCat_id();
                boolean isupdate = true;
                // 对比查询结果集
                for (Cat cat : catlist) {
                    if (comment_id == cat.getCat_id()) {
                        isupdate = false; // 不修改
                        break;
                    }
                }
                if (isupdate) {
                    this.iFakeCommentManager.updateCommentKeyWordsByStatus(memberCommentKeyWords.getId()); // 数据过期
                }
            }
        }

        // 拼接参数

        this.webpage = this.iFakeCommentManager.searchCommentsKeyWords(this.getPage(), this.getPageSize(), this.getSort(), this.getOrder());

        List list = (List) webpage.getResult();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = (Map<String, Object>) list.get(i);
            if (map != null) {
                for (Cat cat : catlist) {
                    if (map.get("cat_id").equals(cat.getCat_id())) {
                        map.put("cat_name", cat.getName());
                        break;
                    }
                }
            }
        }


        // 拼装新模型
        this.showGridJson(webpage);
        return JSON_MESSAGE;
    }

    public MemberCommentKeyWords getMemberCommentKeyWordsPage() {
        return memberCommentKeyWordsPage;
    }

    public void setMemberCommentKeyWordsPage(MemberCommentKeyWords memberCommentKeyWordsPage) {
        this.memberCommentKeyWordsPage = memberCommentKeyWordsPage;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getKey_words() {
        return key_words;
    }

    public void setKey_words(String key_words) {
        this.key_words = key_words;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCat_id() {
        return cat_id;
    }

    public void setCat_id(Integer cat_id) {
        this.cat_id = cat_id;
    }

    public String getOptype() {
        return optype;
    }

    public void setOptype(String optype) {
        this.optype = optype;
    }

    public GoodsForFake getGoods_fake() {
        return goods_fake;
    }

    public void setGoods_fake(GoodsForFake goods_fake) {
        this.goods_fake = goods_fake;
    }

    public Integer getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(Integer goods_id) {
        this.goods_id = goods_id;
    }

    public Integer getGoods_fake_salesNumber() {
        return goods_fake_salesNumber;
    }

    public void setGoods_fake_salesNumber(Integer goods_fake_salesNumber) {
        this.goods_fake_salesNumber = goods_fake_salesNumber;
    }

    public Integer getGoods_fake_commentsNumber() {
        return goods_fake_commentsNumber;
    }

    public void setGoods_fake_commentsNumber(Integer goods_fake_commentsNumber) {
        this.goods_fake_commentsNumber = goods_fake_commentsNumber;
    }

    public Integer getStore_id() {
        return store_id;
    }

    public void setStore_id(Integer store_id) {
        this.store_id = store_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
