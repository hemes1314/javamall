package com.enation.app.shop.core.tag.detail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.plugin.goods.GoodsDataFilterBundle;
import com.enation.app.shop.core.plugin.goods.GoodsPluginBundle;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.impl.ActivityGoodsManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.ObjectNotFoundException;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 商品详细标签
 * 
 * @author kingapex 2013-7-31下午9:07:41
 */
@Component
@Scope("prototype")
public class GoodsBaseDataTag extends BaseFreeMarkerTag {

    private IGoodsManager goodsManager;

    private GoodsDataFilterBundle goodsDataFilterBundle;

    private GoodsPluginBundle goodsPluginBundle;

    private ActivityGoodsManager activityGoodsManager;

    /**
     * 商品详细标签,如果找不到商品，或商品下架了，会跳转至404页面。
     * 
     * @param goodsid
     *            :商品id
     *            ,int型，如果不指定此参数，则试图由地址栏获取商品id,如：goods-1.html则会得到商品id为1.或phone
     *            -1.html也会得到商品id为1.
     * @return 商品基本信息 {@link Goods} 注：这个goods里有small和big的属性，分别用于输出商品的大图和小图
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected Object exec(Map params) throws TemplateModelException {
        try {
            Integer goods_id = (Integer) params.get("goodsid");
            if(goods_id == null || goods_id == 0) {
                goods_id = this.getGoodsId();
            }
            Map goodsMap = goodsManager.get(goods_id);
            /**
             * 如果商品不存在
             */
            if(goodsMap == null) {

                //标记下架
                goodsMap.put("goods_off", 1);
                return goodsMap;
                //throw new UrlNotFoundException();
            }
            /**
             * 如果已下架
             */
            if(goodsMap.get("market_enable").toString().equals("0")) {

                //标记商品下架
                goodsMap.put("goods_off", 1);
                return goodsMap;
            }
            /**
             * 如果已删除（到回收站）
             */
            if(goodsMap.get("disabled").toString().equals("1")) {

                //标记商品下架
                goodsMap.put("goods_off", 1);
                return goodsMap;
            }
            goodsMap.put("goods_off", 0);//默认商品没有下架
            
            /********** 2015/11/12 humaodong *************/
            Integer availableNum = (Integer)goodsMap.get("enable_store");
            if (availableNum == null || availableNum < 0) goodsMap.put("enable_store", 0);
            /*********************************************/
            
            String intro = (String) goodsMap.get("intro");
            if(!StringUtil.isEmpty(intro)) {
                intro = UploadUtil.replacePath(intro);
                goodsMap.put("intro", intro);
            }

            //查询商品的促销活动
            if(activityGoodsManager.checkGoods(goods_id) > 0) {
                Map map = activityGoodsManager.getActivityByGoods(goods_id);
                String activityName = map.get("name").toString();
                goodsMap.put("activity_name", activityName);
            }

            List<Map> goodsList = new ArrayList<Map>();
            goodsList.add(goodsMap);
            this.goodsDataFilterBundle.filterGoodsData(goodsList);

            this.getRequest().setAttribute("goods", goodsMap);
            goodsPluginBundle.onVisit(goodsMap);
            return goodsMap;

        } catch(ObjectNotFoundException e) {
            throw new UrlNotFoundException();
        }
    }

    private Integer getGoodsId() {
        HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
        String url = RequestUtil.getRequestUrl(httpRequest);
        String goods_id = this.paseGoodsId(url);

        return Integer.valueOf(goods_id);
    }

    private static String paseGoodsId(String url) {
        String pattern = "(-)(\\d+)";
        String value = null;
        Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
        Matcher m = p.matcher(url);
        if(m.find()) {
            value = m.group(2);
        }
        return value;
    }

    public GoodsDataFilterBundle getGoodsDataFilterBundle() {
        return goodsDataFilterBundle;
    }

    public void setGoodsDataFilterBundle(GoodsDataFilterBundle goodsDataFilterBundle) {
        this.goodsDataFilterBundle = goodsDataFilterBundle;
    }

    public IGoodsManager getGoodsManager() {
        return goodsManager;
    }

    public void setGoodsManager(IGoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }

    public GoodsPluginBundle getGoodsPluginBundle() {
        return goodsPluginBundle;
    }

    public void setGoodsPluginBundle(GoodsPluginBundle goodsPluginBundle) {
        this.goodsPluginBundle = goodsPluginBundle;
    }

    public ActivityGoodsManager getActivityGoodsManager() {
        return activityGoodsManager;
    }

    public void setActivityGoodsManager(ActivityGoodsManager activityGoodsManager) {
        this.activityGoodsManager = activityGoodsManager;
    }

}
