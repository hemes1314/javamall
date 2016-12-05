package com.enation.app.shop.core.model;

import java.util.List;

import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 商品实体
 * 
 * @author kingapex 2010-4-25下午09:40:24
 */
public class Goods implements java.io.Serializable {

	private Integer goods_id;
	private String name;
	private String sn;
	private Integer brand_id;
	private Integer cat_id;
	private Integer type_id;
	private String goods_type; // enum('normal', 'bind') default 'normal', options 'service'
	private String unit;
	private Double weight;
	private Integer market_enable;
	// private String image_default;
	// private String image_file;
	private String thumbnail;
	private String big;
	private String small;
	private String original;
	private String brief;
	private String intro;
	private String intro2;
	private Double price;
	private Double mktprice;
	private Double cost;
	private Integer store;
	private Integer enable_store;
	private String adjuncts;
	private String params;
	private String specs;
	private Long create_time;
	private Long last_modify;
	private Integer view_count;
	private Integer buy_count;
	private Integer disabled;
	private String page_title;
	private String meta_keywords;
	private String meta_description;
	private Integer point; // 积分
	private Integer sord;
	private String p1;
	private String p2;
	private String p3;
	private String p4;
	private String p5;
	private String p6;
	private String p7;
	private String p8;
	private String p9;
	private String p10;

	private Double 	commission;	//商品佣金比例
	
	//lxl add erp 
	private String good_lv;
	private String good_year ;
	private String good_alcohol;
	private String specs_type ;
	
	//add Tension
	//直降（降价活动）
	private Integer isCostDown;
	private Integer isAdvbuy;
	private Integer isFlashbuy;
	private Integer isGroupbuy;
	private Integer isSecbuy;
	private Integer store_id;
	private String store_name;
	private Integer store_cat_id;
	private Integer have_spec;
	
	// GFS图片地址
	private String original_gfs;
	// 商品销售属性
	private String attributes;
	// 商品SKU信息列表(暂无用)
	private List<Product> products;
	// 商品SKU信息
	private Product product;
	// 商品相册信息列表
	private List<GoodsGallery> goodsGalleries;
	
	public String getGood_lv() {
		return good_lv;
	}
	public void setGood_lv(String good_lv) {
		this.good_lv = good_lv;
	}
	public String getGood_year() {
		return good_year;
	}
	public void setGood_year(String good_year) {
		this.good_year = good_year;
	}
	public String getGood_alcohol() {
		return good_alcohol;
	}
	public void setGood_alcohol(String good_alcohol) {
		this.good_alcohol = good_alcohol;
	}
	public String getSpecs_type() {
		return specs_type;
	}
	public void setSpecs_type(String specs_type) {
		this.specs_type = specs_type;
	}
	
	public Integer getBrand_id() {
		if (brand_id == null)
			brand_id = 0;
		return brand_id;
	}

	public void setBrand_id(Integer brand_id) {
		this.brand_id = brand_id;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public Integer getBuy_count() {
		return buy_count;
	}

	public void setBuy_count(Integer buy_count) {
		this.buy_count = buy_count;
	}

	public Integer getDisabled() {
		return disabled;
	}

	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}

	@PrimaryKeyField
	public Integer getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
	}

	// public String getImage_default() {
	// return image_default;
	// }
	// public void setImage_default(String image_default) {
	// this.image_default = image_default;
	// }
	// public String getImage_file() {
	// return image_file;
	// }
	// public void setImage_file(String image_file) {
	// this.image_file = image_file;
	// }

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getBig() {
		return big;
	}

	public void setBig(String big) {
		this.big = big;
	}

	public String getSmall() {
		return small;
	}

	public void setSmall(String small) {
		this.small = small;
	}

	public String getOriginal() {
		return original;
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getIntro2() {
        return intro2;
    }
    public void setIntro2(String intro2) {
        this.intro2 = intro2;
    }
    public Integer getMarket_enable() {
		return market_enable;
	}

	public void setMarket_enable(Integer market_enable) {
		this.market_enable = market_enable;
	}

	public String getMeta_description() {
		return meta_description;
	}

	public void setMeta_description(String meta_description) {
		this.meta_description = meta_description;
	}

	public String getMeta_keywords() {
		return meta_keywords;
	}

	public void setMeta_keywords(String meta_keywords) {
		this.meta_keywords = meta_keywords;
	}

	public Double getMktprice() {
		return mktprice;
	}

	public void setMktprice(Double mktprice) {
		this.mktprice = mktprice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPage_title() {
		return page_title;
	}

	public void setPage_title(String page_title) {
		this.page_title = page_title;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Integer getType_id() {
		return type_id;
	}

	public void setType_id(Integer type_id) {
		this.type_id = type_id;
	}

	public String getGoods_type() {
		return goods_type;
	}

	public void setGoods_type(String goodsType) {
		goods_type = goodsType;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getView_count() {
		return view_count;
	}

	public void setView_count(Integer view_count) {
		this.view_count = view_count;
	}

	public Double getWeight() {
		weight = weight == null ? weight = 0D : weight;
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Integer getCat_id() {
		return cat_id;
	}

	public void setCat_id(Integer cat_id) {
		this.cat_id = cat_id;
	}

	public Integer getStore() {
		return store;
	}

	public void setStore(Integer store) {
		this.store = store;
	}
	
    public Integer getEnable_store() {
        return enable_store;
    }
    
    public void setEnable_store(Integer enable_store) {
        this.enable_store = enable_store;
    }
    public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}

	public Long getLast_modify() {
		return last_modify;
	}

	public void setLast_modify(Long last_modify) {
		this.last_modify = last_modify;
	}

	public String getSpecs() {
		return specs;
	}

	public void setSpecs(String specs) {
		this.specs = specs;
	}

	public String getAdjuncts() {
		return adjuncts;
	}

	public void setAdjuncts(String adjuncts) {
		this.adjuncts = adjuncts;
	}

	public Integer getPoint() {
		point = point == null ? 0 : point;
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public Integer getSord() {
		return sord;
	}

	public void setSord(Integer sord) {
		this.sord = sord;
	}
	
	public Double getCommission() {
		return commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}
    
    public String getP1() {
        return p1;
    }
    
    public void setP1(String p1) {
        this.p1 = p1;
    }
    
    public String getP2() {
        return p2;
    }
    
    public void setP2(String p2) {
        this.p2 = p2;
    }
    
    public String getP3() {
        return p3;
    }
    
    public void setP3(String p3) {
        this.p3 = p3;
    }
    
    public String getP4() {
        return p4;
    }
    
    public void setP4(String p4) {
        this.p4 = p4;
    }
    
    public String getP5() {
        return p5;
    }
    
    public void setP5(String p5) {
        this.p5 = p5;
    }
    
    public String getP6() {
        return p6;
    }
    
    public void setP6(String p6) {
        this.p6 = p6;
    }
    
    public String getP7() {
        return p7;
    }
    
    public void setP7(String p7) {
        this.p7 = p7;
    }
    
    public String getP8() {
        return p8;
    }
    
    public void setP8(String p8) {
        this.p8 = p8;
    }
    
    public String getP9() {
        return p9;
    }
    
    public void setP9(String p9) {
        this.p9 = p9;
    }
    
    public String getP10() {
        return p10;
    }
    
    public void setP10(String p10) {
        this.p10 = p10;
    }
    
    public Integer getIsAdvbuy() {
        return isAdvbuy;
    }
    
    public void setIsAdvbuy(Integer isAdvbuy) {
        this.isAdvbuy = isAdvbuy;
    }
    
    public Integer getIsFlashbuy() {
        return isFlashbuy;
    }
    
    public void setIsFlashbuy(Integer isFlashbuy) {
        this.isFlashbuy = isFlashbuy;
    }
    
    public Integer getIsGroupbuy() {
        return isGroupbuy;
    }
    
    public void setIsGroupbuy(Integer isGroupbuy) {
        this.isGroupbuy = isGroupbuy;
    }
    
    public Integer getIsSecbuy() {
        return isSecbuy;
    }
    
    public void setIsSecbuy(Integer isSecbuy) {
        this.isSecbuy = isSecbuy;
    }
    
    public Integer getStore_id() {
        return store_id;
    }
    
    public void setStore_id(Integer store_id) {
        this.store_id = store_id;
    }
    
    public Integer getStore_cat_id() {
        return store_cat_id;
    }
    
    public void setStore_cat_id(Integer store_cat_id) {
        this.store_cat_id = store_cat_id;
    }
    
    public Integer getHave_spec() {
        return have_spec;
    }
    
    public void setHave_spec(Integer have_spec) {
        this.have_spec = have_spec;
    }
    
    public String getStore_name() {
        return store_name;
    }
    
    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }
    
    public Double getCost() {
        return cost;
    }
    
    public void setCost(Double cost) {
        this.cost = cost;
    }
    
    /**
     * 直降
     * @return
     */
    public Integer getIsCostDown() {
        return isCostDown;
    }
    
    public void setIsCostDown(Integer isCostDown) {
        this.isCostDown = isCostDown;
    }
    
    public String getOriginal_gfs() {
        return original_gfs;
    }
    
    public void setOriginal_gfs(String original_gfs) {
        this.original_gfs = original_gfs;
    }
    
    @NotDbField
    public String getAttributes() {
        return attributes;
    }
    
    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }
    
    @NotDbField
    public List<Product> getProducts() {
        return products;
    }
    
    public void setProducts(List<Product> products) {
        this.products = products;
    }
    
    @NotDbField
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public List<GoodsGallery> getGoodsGalleries() {
        return goodsGalleries;
    }
    
    public void setGoodsGalleries(List<GoodsGallery> goodsGalleries) {
        this.goodsGalleries = goodsGalleries;
    }
}