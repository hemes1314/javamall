package com.enation.app.shop.mobile.action.goods;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.shop.core.model.Attribute;
import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.GoodsType;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.impl.GoodsCatManager;
import com.enation.app.shop.core.service.impl.GoodsTypeManager;
import com.enation.app.shop.core.service.IGoodsStoreManager;
import com.enation.app.shop.core.service.impl.BrandManager;
import com.enation.app.shop.core.service.impl.GoodsManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;

import net.sf.json.JSONArray;


//2016 - 3 - 25 因为/api/mobile/erp?xxx.do 跟 ErpApiAction 冲突所以关闭  monsoon

//@SuppressWarnings("serial")
//@Scope("prototype")
//@ParentPackage("eop_default")
//@Namespace("/api/mobile")
//@Action("erp")
public class GetGoodsApi extends WWAction{
    private IBrandManager brandManager;
    private IGoodsManager goodsManager;
    private IStoreManager storeManager;
    private GoodsCatManager goodsCatManagerImpl;
    private GoodsTypeManager goodsTypeManager;
    private IGoodsStoreManager goodsStoreManager;
    
    @Transactional(propagation = Propagation.REQUIRED)
    public String getGoods(){
        
        try{
            Goods goods = new Goods(); 
            HttpServletRequest request = getRequest();
            try {
                request.setCharacterEncoding("UTF-8");
            } catch(UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            //检查商品是否存在不存在添加商品sn
            String goodsSn = request.getParameter("goods_sn");
            
            if (goodsManager.getGoodBySn(goodsSn) != null) {
                this.showPlainErrorJson("商品已经存在");
                return WWAction.JSON_MESSAGE;
            } else {
                goods.setSn(goodsSn);
            }
            
            //添加主类型属性        add by Tension
            goods.setCat_id(NumberUtils.toInt(request.getParameter("main_type")));
            
            //添加商品品牌          add by Tension
            String brandName = request.getParameter("brand_name");
            Brand brand = brandManager.getByName(brandName);
            
            if (brand == null) {
                brand = new Brand();
                brand.setName(brandName);
                
                if (request.getParameter("brand_english_name") != null) {
                    brand.setEnglish_name(request.getParameter("brand_english_name"));
                }
                
                int brandId = brandManager.add2(brand);
                goods.setBrand_id(brandId);
            } else {
                int brandId = brand.getBrand_id();
                goods.setBrand_id(brandId);
            }
            
            //添加商店
            Integer storeId = NumberUtils.toInt(request.getParameter("store_id"));
            Store store = storeManager.getStore(storeId);
            String goodsName = request.getParameter("goods_name");
            
            if(store == null){
                this.showPlainErrorJson("店铺不存在");
                return WWAction.JSON_MESSAGE;
            } else {
                goods.setStore_id(storeId);
                goods.setStore_name(store.getStore_name());
            }
            
            if (request.getParameter("goods_english_name") != null) {
                goodsName =  goodsName + "("+ request.getParameter("goods_english_name") +")";
            }
            
            goods.setCreate_time(DateUtil.getDateline());
            goods.setName(goodsName);
            goods.setGood_lv(request.getParameter("goods_lv"));
            goods.setGood_alcohol(request.getParameter("goods_alcohol"));
            goods.setGood_year(request.getParameter("goods_year"));
            goods.setSpecs_type(request.getParameter("specs_type"));
            goods.setPrice(NumberUtils.toDouble(request.getParameter("price")));
            goods.setMktprice(NumberUtils.toDouble(request.getParameter("price")));
            goods.setCost(NumberUtils.toDouble(request.getParameter("price")));
            goods.setStore(NumberUtils.toInt(request.getParameter("num")));
            goods.setEnable_store(NumberUtils.toInt(request.getParameter("num")));
            goods.setMarket_enable(0);
            goods.setHave_spec(0);
            goods.setDisabled(0);
            //goods.setStore_cat_id(135);
            //goodsManager.add(goods);
            
            //添加商品所有属性          add by Tension
            int goodsTypeId = goodsCatManagerImpl.getById(goods.getCat_id()).getType_id();
            goods.setType_id(goodsTypeId);
            List<Attribute> propList = goodsTypeManager.getAttrListByTypeId(goodsTypeId);
            
            for (int i = 0; i < propList.size(); i++) {
                Attribute attribute = propList.get(i);
                String name = attribute.getName();
                int typePosition = i + 1;                        //P的位置
                String options = attribute.getOptions();         //不可能为空
                String[] optionsArray = options.split(",");      //options逗号分隔数组
                
                //类型属性      add by Tension
                if (StringUtils.equals(name, "类型")) {
                    String goodsType = request.getParameter("goods_type");
                    
                    if (goodsType == null || goodsType.isEmpty()) {
                        continue;
                    }
                                        
                    int position = ArrayUtils.indexOf(optionsArray, goodsType);
                    
                    if (position != -1) {
                        goodsManager.setPPosition(goods, typePosition, String.valueOf(position));
                    } else {
                        options += ","+ goodsType;
                        attribute.setOptions(options);
                        attribute.getOptionAr();
                        attribute.getOptionMap();
                        
                        position = options.split(",").length;
                        goodsManager.setPPosition(goods, typePosition, String.valueOf(position));
                    }
                    
                    continue;
                }
                
                //品牌属性      add by Tension
                if (StringUtils.equals(name, "品牌")) {
                    if (brandName == null || brandName.isEmpty()) {
                        continue;
                    }
                    
                    int position = ArrayUtils.indexOf(optionsArray, brandName);
                    
                    if (position != -1 ) {
                        goodsManager.setPPosition(goods, typePosition, String.valueOf(position));
                    } else {
                        options += ","+ brandName;
                        attribute.setOptions(options);
                        attribute.getOptionAr();
                        attribute.getOptionMap();
                        
                        position = options.split(",").length;
                        goodsManager.setPPosition(goods, typePosition, String.valueOf(position));
                    }
                    
                    continue;
                }
                
                //葡萄品种属性      add by Tension
                if (StringUtils.equals(name, "葡萄品种")) {
                    String grapeType = request.getParameter("grape_type");
                    
                    if (grapeType == null || grapeType.isEmpty()) {
                        continue;
                    }
                    
                    int position = ArrayUtils.indexOf(optionsArray, grapeType);
                    
                    if (position != -1) {
                        goodsManager.setPPosition(goods, typePosition, String.valueOf(position));
                    } else {
                        options += ","+ grapeType;
                        attribute.setOptions(options);
                        attribute.getOptionAr();
                        attribute.getOptionMap();
                        
                        position = options.split(",").length;
                        goodsManager.setPPosition(goods, typePosition, String.valueOf(position));
                    }
                    
                    continue;
                }
                
                //产地属性      add by Tension
                if (StringUtils.equals(name, "产地")) {
                    String goodsOrigin = request.getParameter("goods_origin");
                    
                    if (goodsOrigin == null || goodsOrigin.isEmpty()) {
                        continue;
                    }
                    
                    int position = ArrayUtils.indexOf(optionsArray, goodsOrigin);
                    
                    if (position != -1) {
                        goodsManager.setPPosition(goods, typePosition, String.valueOf(position));
                    } else {
                        options += ","+ goodsOrigin;
                        attribute.setOptions(options);
                        attribute.getOptionAr();
                        attribute.getOptionMap();
                        
                        position = options.split(",").length;
                        goodsManager.setPPosition(goods, typePosition, String.valueOf(position));
                    }
                    
                    continue;
                }
                
                //年份属性      add by Tension
                if (StringUtils.equals(name, "年份")) {
                    String goodsYear = request.getParameter("goods_year");
                    
                    if (goodsYear == null || goodsYear.isEmpty()) {
                        continue;
                    }
                    
                    int position = ArrayUtils.indexOf(optionsArray, goodsYear);
                    
                    if (position != -1) {
                        goodsManager.setPPosition(goods, typePosition, String.valueOf(position));
                    } else {
                        options += ","+ goodsYear;
                        attribute.setOptions(options);
                        attribute.getOptionAr();
                        attribute.getOptionMap();
                        
                        position = options.split(",").length;
                        goodsManager.setPPosition(goods, typePosition, String.valueOf(position));
                    }
                    
                    continue;
                }
                
                //包装属性      add by Tension
                if (StringUtils.equals(name, "包装")) {
                    String goodsPackage = request.getParameter("goods_package");
                    
                    if (goodsPackage == null || goodsPackage.isEmpty()) {
                        continue;
                    }
                    
                    int position = ArrayUtils.indexOf(optionsArray, goodsPackage);
                    
                    if (position != -1) {
                        goodsManager.setPPosition(goods, typePosition, String.valueOf(position));
                    } else {
                        options += ","+ goodsPackage;
                        attribute.setOptions(options);
                        attribute.getOptionAr();
                        attribute.getOptionMap();
                        
                        position = options.split(",").length;
                        goodsManager.setPPosition(goods, typePosition, String.valueOf(position));
                    }
                    
                    continue;
                }
                
                //规格属性      add by Tension
                if (StringUtils.equals(name, "包装")) {
                    String goodsVol = request.getParameter("goods_vol");
                    
                    if (goodsVol == null || goodsVol.isEmpty()) {
                        continue;
                    }
                    
                    int position = ArrayUtils.indexOf(optionsArray, goodsVol);
                    
                    if (position != -1) {
                        goodsManager.setPPosition(goods, typePosition, String.valueOf(position));
                    } else {
                        options += ","+ goodsVol;
                        attribute.setOptions(options);
                        attribute.getOptionAr();
                        attribute.getOptionMap();
                        
                        position = options.split(",").length;
                        goodsManager.setPPosition(goods, typePosition, String.valueOf(position));
                    }
                    
                    continue;
                }
                
                //价格属性      add by Tension
                if (StringUtils.equals(name, "价格")) {  
                    String goodsPrice = request.getParameter("price");
                    
                    if (goodsPrice == null || goodsPrice.isEmpty()) {
                        continue;
                    }
                    
                    Float price = NumberUtils.toFloat(request.getParameter("price"));
                    
                    for (int j = 0; j < optionsArray.length; j++) {
                        String priceInterval = optionsArray[j];
                        String[] priceArray = priceInterval.split("-");
                        
                        if (priceArray.length > 0) {
                            if (price >= NumberUtils.toFloat(priceArray[0]) && price <= NumberUtils.toFloat(priceArray[1])) {
                                goodsManager.setPPosition(goods, typePosition, String.valueOf(j));
                                break;
                            }
                        } else {
                            goodsManager.setPPosition(goods, typePosition, String.valueOf(j));
                            break;
                        }
                    }
                    
                    continue;
                }
                
            }
            
            Integer goodsId = goodsManager.erpAddGoods(goods);
            //goodsManager.updateStore(goodsId, NumberUtils.toInt(request.getParameter("num")), storeId);;
            GoodsType goodsType = goodsTypeManager.getById(goodsTypeId);
            JSONArray propArray = JSONArray.fromObject(propList);
            goodsType.setProps(propArray.toString());
            goodsTypeManager.save(goodsType);
            
            this.showPlainSuccessJson("添加成功");
        }catch(RuntimeException e){
            this.showPlainErrorJson("数据库运行异常");
            
        }
        
        return WWAction.JSON_MESSAGE;
    }
    /**
     * 更新库存
     * @return
     */
    public String updateStore(){
        try{
            HttpServletRequest request =getRequest();
            String sn= request.getParameter("goods_sn");
            Integer storeId = NumberUtils.toInt(request.getParameter("store_id"));
            Integer num =NumberUtils.toInt(request.getParameter("num"));
            int goodsid = goodsManager.getId(sn,storeId);
            if (goodsid == 0){
               this.showPlainErrorJson("商品不存在或者已删除");
            }else if (goodsid == -1){
                this.showPlainErrorJson("存在多个相同的编号");
            }else{
                goodsStoreManager.saveStore(goodsid);
            }
            
            this.goodsManager.updateStore(goodsid,num ,storeId);
            this.showPlainSuccessJson("更新成功");
        }catch (RuntimeException e){
            this.showPlainErrorJson("数据库运行异常");
        
        }
        return WWAction.JSON_MESSAGE;
    }
    /**
     * 更新商品价格
     * @return
     */
    public String updatePrice(){
        try{
            HttpServletRequest request =getRequest();
            String sn= request.getParameter("sn");
            Double price =NumberUtils.toDouble(request.getParameter("price"));
            
            this.goodsManager.updatePrice(sn,price);
            this.showPlainSuccessJson("更新成功");
        }catch (RuntimeException e){
            this.showPlainErrorJson("数据库运行异常");
        }
        
        return WWAction.JSON_MESSAGE;
    }
    public IBrandManager getBrandManager() {
        return brandManager;
    }

    public void setBrandManager(IBrandManager brandManager) {
        this.brandManager = brandManager;
    }

    public IGoodsManager getGoodsManager() {
        return goodsManager;
    }

    public void setGoodsManager(IGoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }
    
    public IStoreManager getStoreManager() {
        return storeManager;
    }
    
    public void setStoreManager(IStoreManager storeManager) {
        this.storeManager = storeManager;
    }
    
    public GoodsCatManager getGoodsCatManagerImpl() {
        return goodsCatManagerImpl;
    }
    
    public void setGoodsCatManagerImpl(GoodsCatManager goodsCatManagerImpl) {
        this.goodsCatManagerImpl = goodsCatManagerImpl;
    }
    
    public GoodsTypeManager getGoodsTypeManager() {
        return goodsTypeManager;
    }
    
    public void setGoodsTypeManager(GoodsTypeManager goodsTypeManager) {
        this.goodsTypeManager = goodsTypeManager;
    }
    
    public IGoodsStoreManager getGoodsStoreManager() {
        return goodsStoreManager;
    }
    
    public void setGoodsStoreManager(IGoodsStoreManager goodsStoreManager) {
        this.goodsStoreManager = goodsStoreManager;
    }
    
}
