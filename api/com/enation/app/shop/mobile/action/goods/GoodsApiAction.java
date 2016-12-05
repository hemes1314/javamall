/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：商品api
 * 修改人：Sylow
 * 修改时间：2015-08-22
 * 修改内容：增加获得商品标签api
 */
package com.enation.app.shop.mobile.action.goods;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.enation.app.advbuy.core.model.AdvBuy;
import com.enation.app.advbuy.core.model.AdvBuyActive;
import com.enation.app.advbuy.core.service.IAdvBuyActiveManager;
import com.enation.app.advbuy.core.service.IAdvBuyManager;
import com.enation.app.b2b2c.core.model.member.StoreMemberComment;
import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberCommentManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.b2b2cGroupbuy.core.service.impl.IStoreGroupBuyManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.flashbuy.core.model.FlashBuy;
import com.enation.app.flashbuy.core.model.FlashBuyActive;
import com.enation.app.flashbuy.core.service.IFlashBuyActiveManager;
import com.enation.app.flashbuy.core.service.IFlashBuyManager;
import com.enation.app.groupbuy.core.model.GroupBuy;
import com.enation.app.groupbuy.core.model.GroupBuyActive;
import com.enation.app.groupbuy.core.service.IGroupBuyActiveManager;
import com.enation.app.groupbuy.core.service.IGroupBuyManager;
import com.enation.app.secbuy.core.model.SecBuy;
import com.enation.app.secbuy.core.model.SecBuyActive;
import com.enation.app.secbuy.core.service.ISecBuyActiveManager;
import com.enation.app.secbuy.core.service.ISecBuyManager;
import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.app.shop.component.goodsindex.service.impl.GoodsIndexManager;
import com.enation.app.shop.core.model.Attribute;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.GoodsType;
import com.enation.app.shop.core.model.Goodslist;
import com.enation.app.shop.core.model.MemberOrderItem;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.Specification;
import com.enation.app.shop.core.model.support.ParamGroup;
import com.enation.app.shop.core.service.GoodsTypeUtil;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IGoodsSearchManager;
import com.enation.app.shop.core.service.IGoodsTagManager;
import com.enation.app.shop.core.service.IGoodsTypeManager;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.app.shop.core.service.IMemberOrderItemManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.impl.ActivityGoodsManager;
import com.enation.app.shop.core.service.impl.ActivityManager;
import com.enation.app.shop.core.utils.ParseXml;
import com.enation.app.shop.core.utils.UrlUtils;
import com.enation.app.shop.mobile.model.ApiGoods;
import com.enation.app.shop.mobile.service.IApiCommentManager;
import com.enation.app.shop.mobile.service.IApiFavoriteManager;
import com.enation.app.shop.mobile.util.gfs.service.IGFSManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;
import com.enation.framework.util.TestUtil;

/**
 * 商品api
 *
 * @author Dawei
 * @date 2015-07-15
 * @version v1.1 2015-08-22
 * @since v1.0
 */
@SuppressWarnings({ "unchecked", "serial", "unused", "rawtypes" })
@Component("mobileGoodsApiAction")
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("goods")
public class GoodsApiAction extends WWAction {
    public static final Pattern PATTERN = Pattern.compile("<img\\s+(?:[^>]*)src\\s*=\\s*([^>]+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    private IGoodsManager goodsManager;
    private IApiCommentManager apiCommentManager;
    private IGoodsGalleryManager goodsGalleryManager;
    private IProductManager productManager;
    private IMemberCommentManager memberCommentManager;
    private IApiFavoriteManager apiFavoriteManager;
    private IGoodsSearchManager goodsSearchManager;
    private IGoodsTypeManager goodsTypeManager;
    private IGoodsTagManager goodsTagManager;
    private final int PAGE_SIZE = 10;
    private String sn;
    private ActivityManager activityManager;
    private ActivityGoodsManager activityGoodsManager;
    private IBrandManager brandManager;
    private GoodsIndexManager goodsIndexManager;
    private ISecBuyActiveManager secBuyActiveManager;
    private ISecBuyManager secBuyManager;
    private IFlashBuyActiveManager flashBuyActiveManager;
    private IStoreGroupBuyManager storeGroupBuyManager;
    private IAdvBuyActiveManager advBuyActiveManager;
    private IGroupBuyActiveManager groupBuyActiveManager;
    private IGroupBuyManager groupBuyManager;
    private IFlashBuyManager flashBuyManager;
    private IAdvBuyManager advBuyManager;
    private IStoreMemberCommentManager storeMemberCommentManager;
    private IMemberOrderItemManager memberOrderItemManager;
    private Integer goods_id;
    private String content;
    private File file;
    private String fileFileName;
    private Integer type;
    private Integer grade;
    private Integer store_deliverycredit;
    private Integer store_desccredit;
    private Integer store_servicecredit;
    private IStoreGoodsManager storeGoodsManager;
    private IStoreManager storeManager;
    private IGFSManager gfsManager;


    /**
     * 根据标签获得商品列表
     *
     * @author Sylow
     * @param <b>catid</b>:分类id.int型，必填项
     * @param <b>tagid</b>:标签id，int型，必填项
     * @param <b>goodsnum</b>:数量，int型，必填项
     * @return 返回json串 <br />
     *         <b>result</b>: 1表示添加成功0表示失败 ，int型 <br />
     *         <b>message</b>: 提示信息 <br />
     *         <b>data</b>: 商品列表数据
     */
    public String listByTag() {
        try {
            HttpServletRequest request = getRequest();

            String catid = (String) request.getParameter("catid");

            String tagid = (String) request.getParameter("tagid");
            String goodsnum = (String) request.getParameter("goodsnum");

            if (catid == null || catid.equals("")) {
                String uri = ThreadContextHolder.getHttpRequest().getServletPath();
                catid = UrlUtils.getParamStringValue(uri, "cat");
            }
            List<Map> goodsList = goodsManager.listByCat(tagid, catid, goodsnum);
//			for(Map goods:goodsList){
//				goods.put("original", UploadUtil.replacePath((String)goods.get("original")));
//				goods.put("big", UploadUtil.replacePath((String)goods.get("big")));
//				goods.put("small", UploadUtil.replacePath((String)goods.get("small")));
//				goods.put("thumbnail", UploadUtil.replacePath((String)goods.get("thumbnail")));
//			}
            this.json = JsonMessageUtil.getMobileListJson(goodsList);

        } catch (RuntimeException e) {
            this.logger.error("获取商品列表出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return WWAction.JSON_MESSAGE;
    }

    /**
     * 商品列表
     *
     * @return
     * @throws Exception
     */
    public String list() {
        try {
//			HttpServletRequest request = getRequest();
//
//			int page = NumberUtils.toInt(request.getParameter("page"), 1);
//			Page webpage = goodsSearchManager.search(page, PAGE_SIZE);
            List<ApiGoods> goodsList = new ArrayList<ApiGoods>();
//			List list = (List) webpage.getResult();
            List<Map> list = goodsManager.list();
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
//				map.put("original", UploadUtil.replacePath((String)map.get("original")));
//				map.put("big", UploadUtil.replacePath((String)map.get("big")));
//				map.put("small", UploadUtil.replacePath((String)map.get("small")));
//				map.put("thumbnail", UploadUtil.replacePath((String)map.get("thumbnail")));
                ApiGoods goods = new ApiGoods();
                BeanUtils.populate(goods, map);
                goodsList.add(goods);

            }
            this.json = JsonMessageUtil.getMobileListJson(goodsList);

        } catch (RuntimeException e) {
            this.logger.error("获取商品列表出错", e);
            this.showPlainErrorJson(e.getMessage());

        } catch (IllegalAccessException e) {
            this.logger.error("获取商品列表出错", e);
            this.showPlainErrorJson(e.getMessage());

        } catch (InvocationTargetException e) {
            this.logger.error("获取商品列表出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return WWAction.JSON_MESSAGE;
    }

    /**
     * 搜索商品
     * @author Sylow
     * @param <b>catid</b>:分类id.int型,可为空
     * @param <b>goods_name</b>:商品名称,String,可为空
     * @param <b>sort</b>:排序名.String,可为空
     * @param <b>order</b>:正序倒序关键字,String,可为空
     * @param <b>brandId</b>:品牌
     * @param <b>price</b>价格
     * @return
     */
    public String search() {
        try {
            HttpServletRequest request = getRequest();
            String cat = (String) request.getParameter("cat_id") == null ? "0" : request.getParameter("cat_id");
            String brand = request.getParameter("brand");
            Page goodsListPage = null;
            int pageNo = request.getParameter("page") == null ? 1 : Integer.valueOf(request.getParameter("page"));

            //除了tag_id外其余全走全文检索
            if ((cat.indexOf("&tag")) == -1) {

                if (cat.indexOf("&prop") != -1) {
                    String[] catArr = cat.split("&");
                    cat = catArr[0];
                    request.setAttribute("cat", cat);
                    request.setAttribute("prop", catArr[1].split("=")[1]);
                } else {
                    cat = cat.matches("\\d.*") ? cat : this.show(cat);
                    request.setAttribute("cat", cat);
                }
                if (brand != null) {
                    request.removeAttribute("prop");
                    request.setAttribute("prop", brand);
                }
                goodsListPage = goodsIndexManager.searchForApp(pageNo, PAGE_SIZE);
                List<Goodslist> goodsList = (List) goodsListPage.getResult();

                for (Goodslist goods : goodsList) {
                    goods.setOriginal(UploadUtil.replacePath((String) goods.getOriginal()));
                    goods.setBig(UploadUtil.replacePath((String) goods.getBig()));
                    goods.setSmall(UploadUtil.replacePath((String) goods.getSmall()));
                    goods.setThumbnail(UploadUtil.replacePath((String) goods.getThumbnail()));
                    //促銷活动
                    int result = activityGoodsManager.checkGoods(goods.getGoods_id());
                    if (result == 1) {
                        Integer goodsId = goods.getGoods_id();
                        Map activitys = activityGoodsManager.getActivityByGoods(goodsId);
                        goods.setActivity_name((String) activitys.get("name"));
                        Double discount = activitys.get("discount") == null ? null : NumberUtils.toDouble(activitys.get("discount").toString());
                        goods.setDiscount(discount);
                    }

                    goods.setGoods_transfee_charge(goods.getGoods_transfee_charge().equals("1") ? "包邮" : "不包邮");
                    //  评论
                    int commentCount = apiCommentManager.getCommentsCount(goods.getGoods_id());
                    goods.setComment_num(commentCount);
                    //评分 和pc 详情页一致
                    double grade = storeMemberCommentManager.getGoodsStore_desccredit(goods.getGoods_id());
                    int grades = (int) (grade == 0.0 ? 5 : grade);
                    goods.setGrade(grades);
                }
            } else {
                //区分cat 是否为字符串
                String keyword = request.getParameter("keyword");
                String sort = request.getParameter("sort");
                String order = null;
                String price = request.getParameter("price");
                String minprice = null;
                String maxprice = null;

                if (sort != null) {
                    String[] sortArr = sort.split("_");
                    sort = sortArr[0];
                    order = sortArr[1];

                    if (sort.equals("buynum")) {
                        sort = "buy_num";
                    }
                } else {
                    sort = "price";
                    order = "asc";
                }

                Map<String, Object> param = new HashMap<String, Object>();

                if (cat.indexOf("&tag") != -1) {
                    String[] catArr = cat.split("&");
                    cat = catArr[0];
                    param.put("tag_id", catArr[1].split("=")[1]);
                }

                if (brand != null) {
                    param.put("brand", brand);
                }

                if (price != null) {
                    String[] priceArr = price.split("_");
                    minprice = priceArr[0];
                    maxprice = priceArr[1];
                    param.put("minprice", minprice);
                    param.put("maxprice", maxprice);
                }

                param.put("goods_name", keyword);

                // 标签查询
                goodsListPage = goodsManager.searchGoodsForApp(param, sort, order, pageNo, PAGE_SIZE);
                List<Goodslist> goodsList = (List) goodsListPage.getResult();
                for (Goodslist goods : goodsList) {
                    goods.setOriginal(UploadUtil.replacePath((String) goods.getOriginal()));
                    goods.setBig(UploadUtil.replacePath((String) goods.getBig()));
                    goods.setSmall(UploadUtil.replacePath((String) goods.getSmall()));
                    goods.setThumbnail(UploadUtil.replacePath((String) goods.getThumbnail()));
                    //促銷活动
                    int result = activityGoodsManager.checkGoods(goods.getGoods_id());
                    if (result == 1) {
                        Integer goodsId = goods.getGoods_id();
                        Map activitys = activityGoodsManager.getActivityByGoods(goodsId);
                        goods.setActivity_name((String) activitys.get("name"));
                        Double discount = activitys.get("discount") == null ? null : NumberUtils.toDouble(activitys.get("discount").toString());
                        goods.setDiscount(discount);
                    }
                    goods.setGoods_transfee_charge(goods.getGoods_transfee_charge().equals("1") ? "包邮" : "不包邮");
                    //  评论
                    int commentCount = apiCommentManager.getCommentsCount(goods.getGoods_id());
                    goods.setComment_num(commentCount);
                    //评分 和pc 详情页一致
                    double grade = storeMemberCommentManager.getGoodsStore_desccredit(goods.getGoods_id());
                    int grades = (int) (grade == 0.0 ? 5 : grade);
                    goods.setGrade(grades);
                }
            }
            goodsListPage.setCurrentPageNo(pageNo);
            this.json = JsonMessageUtil.getMobileObjectJson(goodsListPage);

        } catch (RuntimeException e) {
            TestUtil.print(e);
            this.logger.error("搜索商品出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return WWAction.JSON_MESSAGE;
    }

    /**
     * 商品详细
     *
     * @return
     */
    public String detail() {
        try {
            HttpServletRequest request = getRequest();
            int goods_id = NumberUtils.toInt(request.getParameter("id"), 0);
            Map<String, Object> productMap;
            Product product = productManager.getByGoodsId(goods_id);
            productMap = BeanUtils.describe(product);

            //edit by Tension 修改成从缓存获取商品信息    Map goods = goodsManager.get(goods_id);
            Map goods = goodsManager.getByCache(goods_id);
            // 2016-10-11-baoxiufeng 添加商品是否已被删除校验
            if (goods == null) {
            	this.logger.error("商品已被删除，无法查看");
                this.showPlainErrorJson("商品已被删除，无法查看");
                return WWAction.JSON_MESSAGE;
            }
            Integer marketEnabled = NumberUtils.toInt(goods.get("market_enable").toString(), 0);
            if (marketEnabled == 0) {
            	this.logger.error("商品已下架，无法查看");
                this.showPlainErrorJson("商品已下架，无法查看");
                return WWAction.JSON_MESSAGE;
            }
            Integer disabled = NumberUtils.toInt(goods.get("disabled").toString(), 0);
            if (disabled == 1) {
            	this.logger.error("商品已被删除，无法查看");
                this.showPlainErrorJson("商品已被删除，无法查看");
                return WWAction.JSON_MESSAGE;
            }

            String intro;
            Integer buyCount = 0;
            String actName = "";
            boolean flag = true;
            if (goods.get("intro2") != null) {
                intro = (String) goods.get("intro2");
            } else {
                intro = (String) goods.get("intro");
            }
            if (intro != null) {
                List imgs = getImgSrc(intro);
                intro = "";
                if (imgs != null) {
                    for (int i = 0; i < imgs.size(); i++) {
                        intro = intro + "<img src=\"" + imgs.get(i) + "\">";
                    }
                }
            } else {
                intro = "";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("<html>");
            sb.append("<head>");
            sb.append("<meta charset='utf-8' />");
            sb.append("<title></title>");
            sb.append("<style type='text/css'>");
            sb.append("body {margin:0px; text-align:center;}");
            sb.append("img {width:100%; max-width:100%; height:auto; vertical-align:bottom;}");
            sb.append(".img {width:100%; max-width:100%; height:auto; vertical-align:bottom;clear:both;}");
            sb.append("p {width:100%; max-width:100%; height:auto; line-height:25px;float:left; text-align:center;");
            sb.append("font-size:14px;font-family:'Microsoft YaHei';}");
            sb.append("</style>");
            sb.append("</head>");
            sb.append("<body>");
            sb.append(intro);
            sb.append("</body>");
            sb.append("</html>");

            //  add 活动价格 和活动虚拟数量 显示
            if (NumberUtils.toInt(goods.get("is_advbuy").toString()) == 1) { //lxl  预售
                AdvBuyActive advbuyAct = advBuyActiveManager.get();
                if (advbuyAct != null) {
                    AdvBuy advBuy = advBuyManager.getBuyGoodsId(product.getGoods_id());
                    if (advBuy != null) {
                        product.setPrice(advBuy.getPrice());
                        buyCount = advBuy.getVisual_num() + advBuy.getBuy_num();
                        actName = "预售价";
                        flag = false;
                    }

                }

            } else if (NumberUtils.toInt(goods.get("is_flashbuy").toString()) == 1) {// 限时抢购
                FlashBuyActive flashbuyAct = flashBuyActiveManager.get();
                if (flashbuyAct != null) {
                    FlashBuy flashBuy = flashBuyManager.getBuyGoodsId(product.getGoods_id());
                    if (flashBuy != null) {
                        product.setPrice(flashBuy.getPrice());
                        buyCount = flashBuy.getVisual_num() + flashBuy.getBuy_num();
                        actName = "抢购价";
                        flag = false;
                    }

                }

            } else if (NumberUtils.toInt(goods.get("is_groupbuy").toString()) == 1) { //团购
                GroupBuyActive groupbuyAct = groupBuyActiveManager.get();
                if (groupbuyAct != null) {
                    GroupBuy groupBuy = groupBuyManager.getBuyGoodsId(product.getGoods_id());
                    if (groupBuy != null) {
                        product.setPrice(groupBuy.getPrice());
                        buyCount = groupBuy.getVisual_num() + groupBuy.getBuy_num();
                        actName = "闪购价";
                        flag = false;
                    }

                }

            } else if (NumberUtils.toInt(goods.get("is_secbuy").toString()) == 1) { // 秒拍
                SecBuyActive secbuyAct = secBuyActiveManager.get();
                if (secbuyAct != null) {
                    SecBuy secBuy = secBuyManager.getBuyGoodsId(product.getGoods_id());
                    if (secBuy != null) {
                        product.setPrice(secBuy.getPrice());
                        buyCount = secBuy.getVisual_num() + secBuy.getBuy_num();
                        actName = "秒拍价";
                        flag = false;
                    }
                }

            }
            if (flag) {
                buyCount = (Integer) goods.get("buy_count");
                actName = "商品价";
            }
            productMap.put("actName", actName);
            productMap.put("price", product.getPrice());
            productMap.put("enable_store", goods.get("enable_store"));
            productMap.put("thumbnail", goods.get("thumbnail"));
            productMap.put("intro", sb.toString());
            productMap.put("buy_count", buyCount);
            productMap.put("brief", goods.get("brief"));
            productMap.put("goods_transfee_charge", goods.get("goods_transfee_charge") == "1" ? "包邮" : "不包邮");
            productMap.put("params", goods.get("params"));
            int commentCount = apiCommentManager.getCommentsCount(goods_id);
            int goodCommentCount = apiCommentManager.getCommentsCount(goods_id, 3);
            int favoriteCount = apiFavoriteManager.getFavoriteCount(goods_id);
            productMap.put("comment_count", commentCount);
            productMap.put("favorite_count", favoriteCount);

            //和pc 详情页一致
            double grade = storeMemberCommentManager.getGoodsStore_desccredit(goods_id);
            int grades = (int) (grade == 0.0 ? 5 : grade);
            productMap.put("garde", grades);
            //check 商品是否存在促销
            int result = activityGoodsManager.checkGoods(goods_id);
            if (result == 1) {
                Map activitys = activityGoodsManager.getActivityByGoods(goods_id);
                productMap.put("activity_name", activitys.get("name"));
                productMap.put("activity_discount", activitys.get("discount"));
                productMap.put("activity_gift", activitys.get("give_product"));
            } else {
                productMap.put("activity_name", null);
                productMap.put("activity_discount", null);
                productMap.put("activity_gift", null);
            }
            if (commentCount > 0) {
                java.text.NumberFormat percentFormat = java.text.NumberFormat.getPercentInstance();
                percentFormat.setMaximumFractionDigits(0); // 最大小数位数
                percentFormat.setMaximumIntegerDigits(2);
                percentFormat.setMaximumIntegerDigits(3);// 最大整数位数
                percentFormat.setMinimumFractionDigits(0); // 最小小数位数
                percentFormat.setMinimumIntegerDigits(1);// 最小整数位数
                productMap.put("comment_percent", percentFormat.format((float) goodCommentCount / commentCount));
            } else {
                productMap.put("comment_percent", "100%");
            }

            // 是否已收藏
            Member member = UserConext.getCurrentMember();

            if (member == null) {
                productMap.put("favorited", false);
            } else {
                productMap.put("favorited", apiFavoriteManager.isFavorited(goods_id, member.getMember_id()));
            }

            //商品相册
            List<GoodsGallery> galleryList = this.goodsGalleryManager.list(goods_id);
            if (galleryList == null || galleryList.size() == 0) {
                String img = SystemSetting.getDefault_img_url();
                GoodsGallery gallery = new GoodsGallery();
                gallery.setSmall(img);
                gallery.setBig(img);
                gallery.setThumbnail(img);
                gallery.setTiny(img);
                gallery.setOriginal(img);
                gallery.setIsdefault(1);
                galleryList.add(gallery);
            }
            productMap.put("imgList", galleryList);

            //2015-08-28  规格在第一版本直接显示在商品页里  _  by  _ Sylow
            List<Product> productList = this.productManager.list(goods_id);
            if (("" + goods.get("have_spec")).equals("0")) {
                productMap.put("productid", productList.get(0).getProduct_id());// 商品的货品id
                productMap.put("productList", productList);// 商品的货品列表
            } else {
                List<Specification> specList = this.productManager.listSpecs(goods_id);
                productMap.put("specList", specList);// 商品规格数据列表
                productMap.put("productList", productList);// 商品的货品列表
            }
            productMap.put("have_spec", goods.get("have_spec") == null ? 0 : goods.get("have_spec"));// 是否有规格
            productMap.put("store_id", goods.get("store_id"));
            Store store = storeManager.getStore(NumberUtils.toInt(productMap.get("store_id").toString()));
            if(store != null) {
                productMap.put("store_name", store.getStore_name().trim());
            }
            this.json = JsonMessageUtil.getMobileObjectJson(productMap);
        } catch (RuntimeException e) {
            this.logger.error("获取商品详情出错", e);
            this.showPlainErrorJson(e.getMessage());

        } catch (IllegalAccessException e) {
            this.logger.error("获取商品详情出错", e);
            this.showPlainErrorJson(e.getMessage());

        } catch (InvocationTargetException e) {
            this.logger.error("获取商品详情出错", e);
            this.showPlainErrorJson(e.getMessage());

        } catch (NoSuchMethodException e) {
            this.logger.error("获取商品详情出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return JSON_MESSAGE;
    }

    /**
     * 商品相册
     *
     * @return
     */
    public String gallery() {
        try {
            HttpServletRequest request = getRequest();
            int goods_id = NumberUtils.toInt(request.getParameter("id"), 0);

            List<GoodsGallery> galleryList = this.goodsGalleryManager.list(goods_id);
            if (galleryList == null || galleryList.size() == 0) {
                String img = SystemSetting.getDefault_img_url();
                GoodsGallery gallery = new GoodsGallery();
                gallery.setSmall(img);
                gallery.setBig(img);
                gallery.setThumbnail(img);
                gallery.setTiny(img);
                gallery.setOriginal(img);
                gallery.setIsdefault(1);
                galleryList.add(gallery);
            }
            this.json = JsonMessageUtil.getMobileListJson(galleryList);
        } catch (RuntimeException e) {
            this.logger.error("获取商品相册出错", e);
            this.showPlainErrorJson(e.getMessage());

        }

        return JSON_MESSAGE;
    }

    /**
     * 商品规格
     *
     * @return
     */
    public String spec() {
        try {

            HttpServletRequest request = getRequest();
            int goods_id = NumberUtils.toInt(request.getParameter("id"), 0);

            //edit by Tension 修改成从缓存获取商品信息    Map goods = goodsManager.get(goods_id);
            Map goods = goodsManager.getByCache(goods_id);

            List<Product> productList = this.productManager.list(goods_id);
            Map data = new HashMap();
            if (("" + goods.get("have_spec")).equals("0")) {
                data.put("productid", productList.get(0).getProduct_id());// 商品的货品id
                data.put("productList", productList);// 商品的货品列表
            } else {
                List<Specification> specList = this.productManager.listSpecs(goods_id);
                data.put("specList", specList);// 商品规格数据列表
                data.put("productList", productList);// 商品的货品列表
            }
            data.put("have_spec", goods.get("have_spec") == null ? 0 : goods.get("have_spec"));// 是否有规格

            this.json = JsonMessageUtil.getMobileObjectJson(data);
        } catch (RuntimeException e) {
            this.logger.error("获取商品规格出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return JSON_MESSAGE;
    }

    /**
     * 商品评论
     *
     * @return
     */
    public String comment() {
        try {
            HttpServletRequest request = getRequest();
            int goods_id = NumberUtils.toInt(request.getParameter("id"), 0);
            int type = NumberUtils.toInt(request.getParameter("type"), 1);
            int page = NumberUtils.toInt(request.getParameter("page"), 1);
            Page pageData = memberCommentManager.getGoodsComments(goods_id, page, PAGE_SIZE, type);
            List list = (List) pageData.getResult();
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = (Map<String, Object>) list.get(i);
                if (map.containsKey("face") && map.get("face") != null) {
                    map.put("face", com.enation.eop.sdk.utils.UploadUtil.replacePath(map.get("face").toString()));
                }
                if (map.get("img") != null) {
                    map.put("img", UploadUtil.replacePath((String) map.get("img")));
                }
            }
            this.json = JsonMessageUtil.getMobileListJson(list);
        } catch (RuntimeException e) {
            this.logger.error("获取商品评论出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return JSON_MESSAGE;
    }

    /**
     * 商品属性
     *
     * @return
     */
    public String attrList() {
        try {
            HttpServletRequest request = getRequest();
            Integer goodsid = NumberUtils.toInt(request.getParameter("id"), 0);
            //edit by Tension 修改成从缓存获取商品信息    Map goods = goodsManager.get(goods_id);
            Map goodsmap = this.goodsManager.getByCache(goodsid);
            Integer typeid = (Integer) goodsmap.get("type_id");

            List<Attribute> list = this.goodsTypeManager.getAttrListByTypeId(typeid);
            List attrList = new ArrayList();

            int i = 1;
            for (Attribute attribute : list) {
                Map attrmap = new HashMap();
                if (attribute.getType() == 3) {
                    String[] s = attribute.getOptionAr();
                    String p = (String) goodsmap.get("p" + i);
                    Integer num = 0;
                    if (!StringUtil.isEmpty(p)) {
                        num = NumberUtils.toInt(p);
                    }
                    attrmap.put("attrName", attribute.getName());
                    attrmap.put("attrValue", s[num]);
                } else if (attribute.getType() == 6) {
                    attrmap.put("attrName", attribute.getName());
                    String value = goodsmap.get("p" + i).toString().replace("#", ",").substring(1);
                    attrmap.put("attrValue", value);
                } else {
                    attrmap.put("attrName", attribute.getName());
                    attrmap.put("attrValue", goodsmap.get("p" + i));
                }
                attrList.add(attrmap);
                i++;
            }
            this.json = JsonMessageUtil.getMobileListJson(attrList);
        } catch (RuntimeException e) {
            this.logger.error("获取商品属性出错", e);
            this.showPlainErrorJson(e.getMessage());

        }

        return JSON_MESSAGE;
    }

    /**
     * 商品参数
     * @return
     */
    public String parameList() {
        try {
            HttpServletRequest request = getRequest();
            Integer goodsid = NumberUtils.toInt(request.getParameter("id"), 0);
            //edit by Tension 修改成从缓存获取商品信息    Map goods = goodsManager.get(goods_id);
            Map goodsmap = this.goodsManager.getByCache(goodsid);
            String goodParams = (String) goodsmap.get("params");
            Map result = new HashMap();
            if (goodParams != null && !goodParams.equals("")) {
                ParamGroup[] paramList = GoodsTypeUtil.converFormString(goodParams);

                result.put("paramListss", paramList);


                if (paramList != null && paramList.length > 0) {
                    result.put("hasParam", true);
                } else {
                    result.put("hasParam", false);
                }
            } else {
                result.put("hasParam", false);
            }
            if (result.get("paramListss") == null) {
                //
                this.json = "{result :1,data:[{paramListss:[]}]}";
            } else {
                this.json = JsonMessageUtil.getMobileObjectJson(result);
            }
        } catch (RuntimeException e) {
            this.logger.error("获取商品参数出错", e);
            this.showPlainErrorJson(e.getMessage());

        }

        return JSON_MESSAGE;
    }

    /**
     * 查询商品品牌
     * (从数据库里直接取品牌列表)
     */
    public String brandList() {

        try {
            HttpServletRequest request = getRequest();

            int pageNo = NumberUtils.toInt(request.getParameter("page"), 1);
            Page brandPage = brandManager.brandlist(pageNo, PAGE_SIZE);
            this.json = JsonMessageUtil.getMobileObjectJson(brandPage);
        } catch (RuntimeException e) {
            this.showPlainErrorJson("数据库运行异常,查询商品品牌失败");
        }
        return JSON_MESSAGE;
    }

    /**
     * 获取商品属性里面的品牌
     *
     */
    public String brand() {
        try {
            String[] StrBrand = null;
            List<GoodsType> list = goodsTypeManager.listAll();
            for (GoodsType goodsType : list) {
                String props = goodsType.getProps();
                List<Attribute> listBrand = GoodsTypeUtil.converAttrFormString(props);
                if (listBrand.size() > 1) {
                    String[] strArr = (String[]) listBrand.get(1).getOptionAr();
                    StrBrand = (String[]) ArrayUtils.addAll(strArr, StrBrand);
                }
            }
            List<String> arrList = new ArrayList<String>();

            for (int i = 0; i < StrBrand.length; i++) {
                if (!arrList.contains(StrBrand[i]) && !StrBrand[i].equals("")) {
                    arrList.add(StrBrand[i]);
                }
            }
            JSONObject jo = new JSONObject();
            jo.put("Brand", arrList);

            this.json = JsonMessageUtil.getMobileObjectJson(jo);
        } catch (RuntimeException e) {
            this.showPlainErrorJson("error" + e);
        }
        return WWAction.JSON_MESSAGE;
    }

    /**
     * 转存图片到GFS.
     * @return
     * @author baoxiufeng
     */
    public void storeImageToGFS() {
        if (type == null) return;
        switch (type) {
            case IGFSManager.IMAGE_GOODS: {
                Map<Integer, String> imageMap = gfsManager.handleImageToGFS(this.goodsManager.list(), 
                        "goods_id", "original", "original_gfs");
                this.goodsManager.updateGoodsField(imageMap, "original_gfs");
            }
                break;
            case IGFSManager.IMAGE_STORE: {
                Map<Integer, String> imageMap = gfsManager.handleImageToGFS(this.storeManager.list(), 
                        "store_id", "store_logo", "store_logo_gfs");
                this.storeManager.updateStoreField(imageMap, "store_logo_gfs");
            }
                break;
            case IGFSManager.IMAGE_GALLERY: {
                Map<Integer, String> imageMap = gfsManager.handleImageToGFS(this.goodsGalleryManager.list(), 
                        "img_id", "original", "original_gfs");
                this.goodsGalleryManager.updateGoodsGalleryField(imageMap, "original_gfs");
            }
            break;
        }
        JSONObject json = new JSONObject();
        json.put("result", "1");
        json.put("message", "图片转存GFS完成");
        PrintWriter writer = null;
        try {
            writer = ThreadContextHolder.getHttpResponse().getWriter();
            writer.write(json.toJSONString());
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }
    
    /**
     * 处理商品属性到新库表中.
     * @return
     * @author baoxiufeng
     */
    public void handleGoodsAttr() {
        List<Map> mapList = this.goodsManager.list();
        if (mapList.isEmpty()) return;
        Map<Integer, String> attrMapListMap = new HashMap<Integer, String>(mapList.size());
        List<Map<String, Object>> attrMapList = null;
        Map<String, Object> attrMap = null;
        List<Attribute> attrs = null;    
        for (Map map : mapList) {
            attrs = goodsTypeManager.getAttrListByTypeId((Integer) map.get("type_id"));
            int i = 1;
            attrMapList = new ArrayList<Map<String, Object>>();
            for (Attribute attr : attrs) {
                attrMap = new HashMap<String, Object>(2);
                switch (attr.getType()) {
                case 3:
                    String[] s = attr.getOptionAr();
                    String p = (String) map.get("p" + i);
                    Integer num = 0;
                    if (StringUtils.isNotBlank(p)) {
                        num = NumberUtils.toInt(p);
                    }
                    if (s != null && s.length > num) {
                        attrMap.put("an", attr.getName());
                        attrMap.put("av", s[num]);
                    }
                    break;
                case 6:
                    attrMap.put("an", attr.getName());
                    String value = map.get("p" + i).toString().replace("#", ",").substring(1);
                    attrMap.put("av", value);
                    break;
                default:
                    attrMap.put("an", attr.getName());
                    attrMap.put("av", map.get("p" + i));
                    break;
                }
                attrMapList.add(attrMap);
                i++;
            }
            attrMapListMap.put((Integer) map.get("goods_id"), JSON.toJSONString(attrMapList));
        }
        this.goodsManager.updateGoodsField(attrMapListMap, "attributes");
        JSONObject json = new JSONObject();
        json.put("result", "1");
        json.put("message", "商品销售属性处理完成");
        PrintWriter writer = null;
        try {
            writer = ThreadContextHolder.getHttpResponse().getWriter();
            writer.write(json.toJSONString());
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    /**
     * 扫描码查询产品
     * @return
     */
    public String getGoods() {

        HttpServletRequest request = getRequest();
        String sn = request.getParameter("sn");
        Goods goods = this.goodsManager.getGoodBySn(sn);

        if (goods == null || goods.getGoods_id() == null) {
            this.showPlainErrorJson("此商品不存在");
            return WWAction.JSON_MESSAGE;
        }

        this.json = JsonMessageUtil.getMobileNumberJson("id", goods.getGoods_id().toString());
//      HashMap<String,Object>  map = new HashMap <String,Object>();
//      map.put("goods_id", goods.getGoods_id());
//		this.json =  JsonMessageUtil.getMobileObjectJson(map);
        return WWAction.JSON_MESSAGE;
    }

    /**
     * 推荐商品
     * @return
     */
    public String LikeGoods() {

        try {
            HttpServletRequest request = getRequest();
            int pageNo = NumberUtils.toInt(request.getParameter("page"), 1);
            String tagId = this.show(request.getParameter("tagid"));
            int tag_id = NumberUtils.toInt(tagId);
            int pageSize = NumberUtils.toInt(request.getParameter("pageSize"), 10);
            Page webpage = this.goodsTagManager.getGoodsList(tag_id, pageNo, pageSize);

            if (webpage.getTotalCount() == 0) {
                webpage = goodsSearchManager.search(1, 4);
            }
            List list = (List) webpage.getResult();
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = (Map<String, Object>) list.get(i);
                if (map != null) {
                    map.put("thumbnail", UploadUtil.replacePath((String) map.get("thumbnail")));
                }
            }


            this.json = JsonMessageUtil.getMobileObjectJson(webpage);
        } catch (RuntimeException e) {
            this.showPlainErrorJson("数据库运行异常" + e);
        }

        return WWAction.JSON_MESSAGE;
    }

    /**
     * app首页展示
     * @return
     */
    public String firstShow() {
        try {
            Map<String, Object> result = new HashMap<String, Object>();
            //热门推荐
            Page rementuijian = this.goodsTagManager.getGoodsList(NumberUtils.toInt((show("rementuijian"))), 1, 6);
            if (rementuijian.getTotalCount() == 0) {
                rementuijian = goodsSearchManager.search(1, 6);

            }
            List list1 = (List) rementuijian.getResult();
            for (int i = 0; i < list1.size(); i++) {
                Map<String, Object> map1 = (Map<String, Object>) list1.get(i);
                if (map1 != null) {
                    map1.put("thumbnail", UploadUtil.replacePath((String) map1.get("thumbnail")));
                }
            }
            result.put("rementuijian", rementuijian);
            //新品发现
            Page xinpinfaxian = this.goodsTagManager.getGoodsList(NumberUtils.toInt((show("xinpinfaxian"))), 1, 1);

            if (xinpinfaxian.getTotalCount() == 0) {
                xinpinfaxian = goodsSearchManager.search(1, 1);
            }
            List list2 = (List) xinpinfaxian.getResult();
            Map<String, Object> map2 = (Map<String, Object>) list2.get(0);
            if (map2 != null) {
                map2.put("thumbnail", UploadUtil.replacePath((String) map2.get("thumbnail")));
            }

            result.put("xinpinfaxian", xinpinfaxian);
            //每日给力
            Page meirigeili = this.goodsTagManager.getGoodsList(NumberUtils.toInt((show("meirigeili"))), 1, 1);

            if (meirigeili.getTotalCount() == 0) {
                meirigeili = goodsSearchManager.search(1, 1);
            }
            List list3 = (List) meirigeili.getResult();
            Map<String, Object> map3 = (Map<String, Object>) list3.get(0);
            if (map3 != null) {
                map3.put("thumbnail", UploadUtil.replacePath((String) map3.get("thumbnail")));
            }

            result.put("meirigeili", meirigeili);
            //清仓嗨购
            Page qingcanghaigou = this.goodsTagManager.getGoodsList(NumberUtils.toInt((show("qingcanghaigou"))), 1, 1);

            if (qingcanghaigou.getTotalCount() == 0) {
                qingcanghaigou = goodsSearchManager.search(1, 1);
            }
            List list4 = (List) qingcanghaigou.getResult();
            Map<String, Object> map4 = (Map<String, Object>) list4.get(0);
            if (map4 != null) {
                map4.put("thumbnail", UploadUtil.replacePath((String) map4.get("thumbnail")));
            }

            result.put("qingcanghaigou", qingcanghaigou);

            //秒拍
            SecBuyActive secBuyAct = secBuyActiveManager.get();
            Page secbuy = null;
            if (secBuyAct != null) {
                secbuy = secBuyManager.listByActId(1, 4, secBuyAct.getAct_id(), 1);
                List list5 = (List) secbuy.getResult();
                long nowtime = com.enation.framework.util.DateUtil.getDateline();
                for (int i = 0; i < list5.size(); i++) {
                    Map<String, Object> map5 = (Map<String, Object>) list5.get(i);
                    map5.put("img_url", UploadUtil.replacePath((String) map5.get("img_url")));
                    //返回系统当前时间
                    map5.put("nowtTime", nowtime);
                }


            }
            Object jsonText = JSON.parse("{}");
            result.put("secBuy", secbuy == null ? jsonText : secbuy);

            this.json = JsonMessageUtil.getMobileObjectJson(result);
        } catch (RuntimeException e) {

            this.showPlainErrorJson("数据库运行异常" + e);
        }

        return WWAction.JSON_MESSAGE;
    }

    public String show(String cat) {
        //解析xml
        String path = StringUtil.getRootPath();
        ParseXml px = new ParseXml(path + "/mappingToArticle.xml");
        String elementPath = "body/firstPageShow/" + cat;
        String catid = px.getElementText(elementPath).trim().toString();
        return catid;
    }

    public String getChild() {
        HttpServletRequest request = getRequest();
        String cat = request.getParameter("cat");
        Integer catid = NumberUtils.toInt(this.show(cat));

        return WWAction.JSON_MESSAGE;
    }

    /**
     * 发表评论
     * @param goods_id:商品id,int型，必填项
     * @param type:评论类型，int型，必填项，可选值：1或2，1为评论，2为咨询。
     * @param content:评论内容，String型，必填项。
     * @param file:评论的图片，File类型，可选项。
     * @param grade :评分
     */
    public String addGoodsComment() {

        Member member = UserConext.getCurrentMember();
        if (member == null) {
            this.json = JsonMessageUtil.expireSession();
            return JSON_MESSAGE;
        }

        try {
            HttpServletRequest request = ThreadContextHolder.getHttpRequest();
            StoreMemberComment memberComment = new StoreMemberComment();
            //先上传图片
            String subFolder = "comment";
            if (file != null) {

                //判断文件类型
                String allowTYpe = "gif,jpg,bmp,png";
                if (!fileFileName.trim().equals("") && fileFileName.length() > 0) {
                    String ex = fileFileName.substring(fileFileName.lastIndexOf(".") + 1, fileFileName.length());
                    if (allowTYpe.toString().indexOf(ex.toLowerCase()) < 0) {
                        this.showPlainErrorJson("对不起,只能上传gif,jpg,bmp,png格式的图片！");
                        return JSON_MESSAGE;
                    }
                }

                //判断文件大小

                if (file.length() > 200 * 1024) {
                    this.showPlainErrorJson("'对不起,图片不能大于200K！");
                    return JSON_MESSAGE;
                }

                String imgPath = UploadUtil.upload(file, fileFileName, subFolder);
                memberComment.setImg(imgPath);
            }

            //判断是不是评论或咨询
            if (type != 1 && type != 2) {
                this.showPlainErrorJson("系统参数错误！");
                return JSON_MESSAGE;
            }
            memberComment.setType(type);

            //edit by Tension 修改成从缓存获取商品信息    Map goods = goodsManager.get(goods_id);
            Map goods = goodsManager.getByCache(goods_id);

            //判断是否存在此商品
            if (goods == null) {
                this.showErrorJson("此商品不存在！");
                return JSON_MESSAGE;
            }


            memberComment.setGoods_id(goods_id);
            //判断评论内容
            if (StringUtil.isEmpty(content)) {
                this.showErrorJson("评论或咨询内容不能为空！");
                return JSON_MESSAGE;
            } else if (content.length() > 1000) {
                this.showErrorJson("请输入1000以内的内容！");
                return JSON_MESSAGE;
            }
            content = StringUtil.htmlDecode(content);
            memberComment.setContent(content);


            int store_id = (Integer) goods.get("store_id");
            memberComment.setStore_id(store_id);
            if (type == 1) {
                int buyCount = memberOrderItemManager.count(member.getMember_id(), goods_id);
                int commentCount = memberOrderItemManager.count(member.getMember_id(), goods_id, 1);
                if (commentCount >= buyCount) {
                    this.showPlainErrorJson("您已经评论过此商品");
                    return JSON_MESSAGE;
                }
                //评论不审核全部通过
                memberComment.setStatus(1);
                if (grade < 1 || grade > 3) {
                    this.showPlainErrorJson("请选择对商品的评价！");
                    return JSON_MESSAGE;
                } else {
                    memberComment.setGrade(grade);
                }
            }
            memberComment.setMember_id(member == null ? 0 : member.getMember_id());
            memberComment.setDateline(DateUtil.getDateline());
            memberComment.setIp(request.getRemoteHost());

            memberComment.setStore_deliverycredit(store_deliverycredit);
            memberComment.setStore_desccredit(store_desccredit);
            memberComment.setStore_servicecredit(store_servicecredit);

            storeMemberCommentManager.add(memberComment);
            //更新已经评论过的商品
            if (type == 1) {
                MemberOrderItem memberOrderItem = memberOrderItemManager.get(member.getMember_id(), goods_id, 0);
                if (memberOrderItem != null) {
                    memberOrderItem.setComment_time(System.currentTimeMillis());
                    memberOrderItem.setCommented(1);
                    memberOrderItemManager.update(memberOrderItem);
                }
            }
            //添加商品评论次数
            storeGoodsManager.addStoreGoodsComment(goods_id);
            this.showPlainSuccessJson("发表成功");
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("发表评论出错", e);
            this.showErrorJson("发表评论出错：" + e.getMessage());
        }

        return JSON_MESSAGE;
    }


    public IGoodsTagManager getGoodsTagManager() {
        return goodsTagManager;
    }

    public void setGoodsTagManager(IGoodsTagManager goodsTagManager) {
        this.goodsTagManager = goodsTagManager;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public IGoodsManager getGoodsManager() {
        return goodsManager;
    }

    public void setGoodsManager(IGoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }

    public IGoodsGalleryManager getGoodsGalleryManager() {
        return goodsGalleryManager;
    }

    public void setGoodsGalleryManager(IGoodsGalleryManager goodsGalleryManager) {
        this.goodsGalleryManager = goodsGalleryManager;
    }

    public IProductManager getProductManager() {
        return productManager;
    }

    public void setProductManager(IProductManager productManager) {
        this.productManager = productManager;
    }

    public IMemberCommentManager getMemberCommentManager() {
        return memberCommentManager;
    }

    public void setMemberCommentManager(IMemberCommentManager memberCommentManager) {
        this.memberCommentManager = memberCommentManager;
    }

    public IGoodsSearchManager getGoodsSearchManager() {
        return goodsSearchManager;
    }

    public void setGoodsSearchManager(IGoodsSearchManager goodsSearchManager) {
        this.goodsSearchManager = goodsSearchManager;
    }

    public IApiFavoriteManager getApiFavoriteManager() {
        return apiFavoriteManager;
    }

    public void setApiFavoriteManager(IApiFavoriteManager apiFavoriteManager) {
        this.apiFavoriteManager = apiFavoriteManager;
    }

    public IApiCommentManager getApiCommentManager() {
        return apiCommentManager;
    }

    public void setApiCommentManager(IApiCommentManager apiCommentManager) {
        this.apiCommentManager = apiCommentManager;
    }

    public IGoodsTypeManager getGoodsTypeManager() {
        return goodsTypeManager;
    }

    public void setGoodsTypeManager(IGoodsTypeManager goodsTypeManager) {
        this.goodsTypeManager = goodsTypeManager;
    }

    public int getPAGE_SIZE() {
        return PAGE_SIZE;
    }

    public ActivityGoodsManager getActivityGoodsManager() {
        return activityGoodsManager;
    }

    public void setActivityGoodsManager(ActivityGoodsManager activityGoodsManager) {
        this.activityGoodsManager = activityGoodsManager;
    }

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }

    public IBrandManager getBrandManager() {
        return brandManager;
    }

    public void setBrandManager(IBrandManager brandManager) {
        this.brandManager = brandManager;
    }

    public GoodsIndexManager getGoodsIndexManager() {
        return goodsIndexManager;
    }

    public void setGoodsIndexManager(GoodsIndexManager goodsIndexManager) {
        this.goodsIndexManager = goodsIndexManager;
    }


    public ISecBuyActiveManager getSecBuyActiveManager() {
        return secBuyActiveManager;
    }


    public void setSecBuyActiveManager(ISecBuyActiveManager secBuyActiveManager) {
        this.secBuyActiveManager = secBuyActiveManager;
    }


    public ISecBuyManager getSecBuyManager() {
        return secBuyManager;
    }


    public void setSecBuyManager(ISecBuyManager secBuyManager) {
        this.secBuyManager = secBuyManager;
    }


    public IFlashBuyActiveManager getFlashBuyActiveManager() {
        return flashBuyActiveManager;
    }


    public void setFlashBuyActiveManager(IFlashBuyActiveManager flashBuyActiveManager) {
        this.flashBuyActiveManager = flashBuyActiveManager;
    }


    public IStoreGroupBuyManager getStoreGroupBuyManager() {
        return storeGroupBuyManager;
    }


    public void setStoreGroupBuyManager(IStoreGroupBuyManager storeGroupBuyManager) {
        this.storeGroupBuyManager = storeGroupBuyManager;
    }


    public IAdvBuyActiveManager getAdvBuyActiveManager() {
        return advBuyActiveManager;
    }


    public void setAdvBuyActiveManager(IAdvBuyActiveManager advBuyActiveManager) {
        this.advBuyActiveManager = advBuyActiveManager;
    }


    public IGroupBuyActiveManager getGroupBuyActiveManager() {
        return groupBuyActiveManager;
    }


    public void setGroupBuyActiveManager(IGroupBuyActiveManager groupBuyActiveManager) {
        this.groupBuyActiveManager = groupBuyActiveManager;
    }


    public IGroupBuyManager getGroupBuyManager() {
        return groupBuyManager;
    }


    public void setGroupBuyManager(IGroupBuyManager groupBuyManager) {
        this.groupBuyManager = groupBuyManager;
    }


    public IFlashBuyManager getFlashBuyManager() {
        return flashBuyManager;
    }


    public void setFlashBuyManager(IFlashBuyManager flashBuyManager) {
        this.flashBuyManager = flashBuyManager;
    }


    public IAdvBuyManager getAdvBuyManager() {
        return advBuyManager;
    }


    public void setAdvBuyManager(IAdvBuyManager advBuyManager) {
        this.advBuyManager = advBuyManager;
    }


    public IStoreMemberCommentManager getStoreMemberCommentManager() {
        return storeMemberCommentManager;
    }


    public void setStoreMemberCommentManager(IStoreMemberCommentManager storeMemberCommentManager) {
        this.storeMemberCommentManager = storeMemberCommentManager;
    }


    public IMemberOrderItemManager getMemberOrderItemManager() {
        return memberOrderItemManager;
    }


    public void setMemberOrderItemManager(IMemberOrderItemManager memberOrderItemManager) {
        this.memberOrderItemManager = memberOrderItemManager;
    }


    public Integer getGoods_id() {
        return goods_id;
    }


    public void setGoods_id(Integer goods_id) {
        this.goods_id = goods_id;
    }


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    public File getFile() {
        return file;
    }


    public void setFile(File file) {
        this.file = file;
    }


    public String getFileFileName() {
        return fileFileName;
    }


    public void setFileFileName(String fileFileName) {
        this.fileFileName = fileFileName;
    }


    public Integer getType() {
        return type;
    }


    public void setType(Integer type) {
        this.type = type;
    }


    public Integer getGrade() {
        return grade;
    }


    public void setGrade(Integer grade) {
        this.grade = grade;
    }


    public Integer getStore_deliverycredit() {
        return store_deliverycredit;
    }


    public void setStore_deliverycredit(Integer store_deliverycredit) {
        this.store_deliverycredit = store_deliverycredit;
    }


    public Integer getStore_desccredit() {
        return store_desccredit;
    }


    public void setStore_desccredit(Integer store_desccredit) {
        this.store_desccredit = store_desccredit;
    }


    public Integer getStore_servicecredit() {
        return store_servicecredit;
    }


    public void setStore_servicecredit(Integer store_servicecredit) {
        this.store_servicecredit = store_servicecredit;
    }


    public IStoreGoodsManager getStoreGoodsManager() {
        return storeGoodsManager;
    }


    public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
        this.storeGoodsManager = storeGoodsManager;
    }

    public IStoreManager getStoreManager() {
        return storeManager;
    }

    public void setStoreManager(IStoreManager storeManager) {
        this.storeManager = storeManager;
    }
    
    public void setGfsManager(IGFSManager gfsManager) {
        this.gfsManager = gfsManager;
    }

    public static List getImgSrc(String html) {
        Matcher matcher = PATTERN.matcher(html);
        List list = new ArrayList();
        while (matcher.find()) {
            String group = matcher.group(1);
            if (group == null) {
                continue;
            }
            //   这里可能还需要更复杂的判断,用以处理src="...."内的一些转义符   
            if (group.startsWith("'")) {
                list.add(group.substring(1, group.indexOf("'", 1)));
            } else if (group.startsWith("\"")) {
                list.add(group.substring(1, group.indexOf("\"", 1)));
            } else {
                list.add(group.split("\\s")[0]);
            }
        }
        return list;
    }


}
