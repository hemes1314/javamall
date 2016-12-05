package com.enation.app.shop.core.openapi.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.GoodsParam;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.ParamGroup;
import com.enation.app.shop.core.openapi.service.IWareOpenApiManager;
import com.gome.open.api.common.AbstractRequest;
import com.gome.open.api.ware.client.dto.WarePropimg;
import com.gome.open.api.ware.client.dto.request.WareAddRequest;
import com.gome.open.api.ware.client.dto.request.WareCleanRequest;
import com.gome.open.api.ware.client.dto.request.WareDeleteRequest;
import com.gome.open.api.ware.client.dto.request.WareRevertRequest;
import com.gome.open.api.ware.client.dto.request.WareUpdateRequest;
import com.gome.open.api.ware.client.dto.response.WareAddResponse;
import com.gome.open.api.ware.client.dto.response.WareCleanResponse;
import com.gome.open.api.ware.client.dto.response.WareDeleteResponse;
import com.gome.open.api.ware.client.dto.response.WareRevertResponse;
import com.gome.open.api.ware.client.dto.response.WareUpdateResponse;

/**
 * OpenApi商品信息管理接口.
 * 
 * @author baoxiufeng
 */
@Component
public class WareOpenApiManager implements IWareOpenApiManager {
    
    // OpenApi配置管理器
    private OpenApiConfigManager openApiConfigManager;
    
    @Override
    public WareAddResponse add(Goods goods) throws Exception {
        // 构建添加请求对象
        WareAddRequest request = new WareAddRequest();
        request.setId(String.valueOf(goods.getGoods_id()));
        request.setVenderId(String.valueOf(goods.getStore_id()));
        request.setShopId(String.valueOf(goods.getStore_id()));
        request.setCategoryId(String.valueOf(goods.getCat_id()));
        request.setBrandId(String.valueOf(goods.getBrand_id()));
        request.setTitle(goods.getName());
        request.setItemNum(goods.getSn());
        request.setStockNum(Long.valueOf(goods.getEnable_store() == null ? 0 : goods.getEnable_store()));
        request.setDescription(goods.getIntro());
        request.setWeight(goods.getWeight() / 1000);
        request.setGmPrice(goods.getPrice());
        request.setMarketPrice(goods.getMktprice());
        request.setCostPrice(goods.getCost());
        request.setWareImage(goods.getOriginal_gfs());
        request.setAttributes(goods.getAttributes());
        if (goods.getMarket_enable() != null && goods.getMarket_enable() == 1) {
            request.setWareStatus("ON_SALE");
        } else {
            request.setWareStatus("NEVER_UP");
        }
        if (goods.getDisabled() == null || goods.getDisabled() == 0) {
            request.setStatus("Valid");
        } else {
            request.setStatus("Invalid");
        }
        request.setTransportId("0");
        // 设置商品参数信息
        getGoodsParams(goods, request);
        // 设置SKU信息
        getSkuInfos(goods, request);
        // 设置商品图片信息
        getGoodsGalleries(goods, request);
        
        // 执行API调用
        return openApiConfigManager.execute(request);
    }
    
    @Override
    public WareUpdateResponse edit(Goods goods) throws Exception {
        // 构建更新请求对象
        WareUpdateRequest request = new WareUpdateRequest();
        request.setId(String.valueOf(goods.getGoods_id()));
        request.setVenderId(String.valueOf(goods.getStore_id()));
        request.setShopId(String.valueOf(goods.getStore_id()));
        request.setCategoryId(String.valueOf(goods.getCat_id()));
        request.setBrandId(String.valueOf(goods.getBrand_id()));
        request.setTitle(goods.getName());
        request.setItemNum(goods.getSn());
        request.setStockNum(Long.valueOf(goods.getEnable_store() == null ? 0 : goods.getEnable_store()));
        request.setDescription(goods.getIntro());
        request.setWeight(goods.getWeight() / 1000);
        request.setGmPrice(goods.getPrice());
        request.setMarketPrice(goods.getMktprice());
        request.setCostPrice(goods.getCost());
        request.setWareImage(goods.getOriginal_gfs());
        request.setAttributes(goods.getAttributes());
        if (goods.getMarket_enable() != null && goods.getMarket_enable() == 1) {
            request.setWareStatus("ON_SALE");
        } else {
            request.setWareStatus("NEVER_UP");
        }
        if (goods.getDisabled() == null || goods.getDisabled() == 0) {
            request.setStatus("Valid");
        } else {
            request.setStatus("Invalid");
        }
        request.setTransportId("0");
        // 设置商品参数信息
        getGoodsParams(goods, request);
        // 设置SKU信息
        getSkuInfos(goods, request);
        // 设置商品图片信息
        getGoodsGalleries(goods, request);
        // 执行API调用
        return openApiConfigManager.execute(request);
    }
    
    @Override
    public WareDeleteResponse delete(Integer goodsId) throws Exception {
        WareDeleteRequest request = new WareDeleteRequest();
        request.setIds(Arrays.asList(String.valueOf(goodsId)));
        return openApiConfigManager.execute(request);
    }
    
    @Override
    public WareDeleteResponse batchDelete(List<Integer> goodsIds) throws Exception {
        if (goodsIds == null || goodsIds.isEmpty()) return null;
        WareDeleteRequest request = new WareDeleteRequest();
        List<String> list = new ArrayList<String>(goodsIds.size());
        for (Integer goodsId : goodsIds) {
            list.add(String.valueOf(goodsId));
        }
        request.setIds(list);
        return openApiConfigManager.execute(request);
    }

    @Override
    public WareRevertResponse revert(Integer goodsId) throws Exception {
        WareRevertRequest request = new WareRevertRequest();
        request.setIds(Arrays.asList(String.valueOf(goodsId)));
        return openApiConfigManager.execute(request);
    }

    @Override
    public WareRevertResponse batchRevert(List<Integer> goodsIds) throws Exception {
        if (goodsIds == null || goodsIds.isEmpty()) return null;
        List<String> list = new ArrayList<String>(goodsIds.size());
        for (Integer goodsId : goodsIds) {
            list.add(String.valueOf(goodsId));
        }
        WareRevertRequest request = new WareRevertRequest();
        request.setIds(list);
        return openApiConfigManager.execute(request);
    }

    @Override
    public WareCleanResponse clean(Integer goodsId) throws Exception {
        WareCleanRequest request = new WareCleanRequest();
        request.setIds(Arrays.asList(String.valueOf(goodsId)));
        return openApiConfigManager.execute(request);
    }

    @Override
    public WareCleanResponse batchClean(List<Integer> goodsIds) throws Exception {
        if (goodsIds == null || goodsIds.isEmpty()) return null;
        List<String> list = new ArrayList<String>(goodsIds.size());
        for (Integer goodsId : goodsIds) {
            list.add(String.valueOf(goodsId));
        }
        WareCleanRequest request = new WareCleanRequest();
        request.setIds(list);
        return openApiConfigManager.execute(request);
    }
    
    @Override
	public WareUpdateResponse updateStatus(Goods goods) throws Exception {
    	WareUpdateRequest request = new WareUpdateRequest();
    	request.setId(String.valueOf(goods.getGoods_id()));
    	if (goods.getMarket_enable() != null && goods.getMarket_enable() == 1) {
            request.setWareStatus("ON_SALE");
        } else {
            request.setWareStatus("NEVER_UP");
        }
        if (goods.getDisabled() == null || goods.getDisabled() == 0) {
            request.setStatus("Valid");
        } else {
            request.setStatus("Invalid");
        }
        request.setStockNum(Long.valueOf(goods.getEnable_store() == null ? 0 : goods.getEnable_store()));
    	return openApiConfigManager.execute(request);
	}

	@Override
	public List<WareUpdateResponse> batchUpdateStatus(List<Goods> goodsList) throws Exception {
		if (goodsList == null || goodsList.isEmpty()) return null;
		List<WareUpdateResponse> responses = new ArrayList<WareUpdateResponse>(goodsList.size());
		for (Goods goods : goodsList) {
            responses.add(updateStatus(goods));
        }
        return responses;
	}

	@Override
	public WareUpdateResponse updateStock(Product product) throws Exception {
    	WareUpdateRequest request = new WareUpdateRequest();
    	request.setId(String.valueOf(product.getGoods_id()));
    	request.setStockNum(Long.valueOf(product.getEnable_store()));
    	return openApiConfigManager.execute(request);
	}

    @Override
    public List<WareUpdateResponse> batchUpdateStock(List<Product> products) throws Exception {
        if (products == null || products.isEmpty()) return null;
        List<WareUpdateResponse> responses = new ArrayList<WareUpdateResponse>(products.size());
        for (Product product : products) {
            responses.add(updateStock(product));
        }
        return responses;
    }
    
    /** 参数中文名称 */
    private static final String PARAM_NAME1 = "别名";
    private static final String PARAM_NAME2 = "卖点描述";
    
    /**
     * 设置商品参数信息.
     * 
     * [
	 *	    {
	 *	        "name": "", 
	 *	        "paramList": [
	 *	            {
	 *	                "name": "别名", 
	 *	                "value": "", 
	 *	                "valueList": [ ]
	 *	            }, 
	 *	            {
	 *	                "name": "卖点描述", 
	 *	                "value": "", 
	 *	                "valueList": [ ]
	 *	            }
	 *	        ], 
	 *	        "paramNum": 2
	 *	    }
	 *	]
     * 
     * @param goods 商品信息
     * @param request 操作请求对象
     */
    private void getGoodsParams(Goods goods, AbstractRequest request) {
    	if (StringUtils.isBlank(goods.getParams())) return;
    	List<ParamGroup> paramGroups = JSONObject.parseArray(goods.getParams(), ParamGroup.class);
    	if (paramGroups.isEmpty()) return;
    	ParamGroup paramGroup = paramGroups.get(0);
    	List<GoodsParam> params = paramGroup.getParamList();
    	for (GoodsParam param : params) {
    		if (PARAM_NAME1.equals(param.getName())) {
    			
    		} else if (PARAM_NAME2.equals(param.getName())) {
    			if (request instanceof WareAddRequest) {
    	            WareAddRequest req = ((WareAddRequest) request);
    	            req.setAdContent(param.getValue());
    	        } else {
    	            WareUpdateRequest req = ((WareUpdateRequest) request);
    	            req.setAdContent(param.getValue());
    	        }
    		}
    	}
    }
    
    /**
     * 设置SKU信息.
     * 
     * @param goods 商品信息
     * @param request 操作请求对象
     */
    private void getSkuInfos(Goods goods, AbstractRequest request) {
        Product p = goods.getProduct();
        if (p == null) return;
        
        if (request instanceof WareAddRequest) {
            WareAddRequest req = ((WareAddRequest) request);
            req.setSkuIds(String.valueOf(p.getProduct_id()));
            req.setSkuPrices(String.valueOf(p.getPrice()));
            req.setSkuStocks(String.valueOf(p.getEnable_store()));
        } else {
            WareUpdateRequest req = ((WareUpdateRequest) request);
            req.setSkuIds(String.valueOf(p.getProduct_id()));
            req.setSkuPrices(String.valueOf(p.getPrice()));
            req.setSkuStocks(String.valueOf(p.getEnable_store()));
        }
    }
    
    /**
     * 设置商品相册图片信息.
     * 
     * @param goods 商品信息
     * @param request 操作请求对象
     */
    private void getGoodsGalleries(Goods goods, AbstractRequest request) {
        List<GoodsGallery> galleries = goods.getGoodsGalleries();
        if (galleries == null || galleries.isEmpty()) return;
        
        List<WarePropimg> propimgs = new ArrayList<WarePropimg>(galleries.size());
        WarePropimg propimg = null;
        int i = 1;
        for (GoodsGallery gallery : galleries) {
            propimg = new WarePropimg();
            propimg.setId(String.valueOf(gallery.getImg_id()));
            propimg.setWareId(String.valueOf(goods.getGoods_id()));
            propimg.setImgId(String.valueOf(gallery.getImg_id()));
            propimg.setImgUrl(gallery.getOriginal_gfs());
            propimg.setIsMain(String.valueOf(gallery.getIsdefault()));
            propimg.setIndexId(Double.valueOf(i++));
            propimg.setIsDeleted(String.valueOf(gallery.getDisabled()));
            propimgs.add(propimg);
        }
        if (request instanceof WareAddRequest) {
            ((WareAddRequest) request).setWarePropimgs(propimgs);
        } else {
            ((WareUpdateRequest) request).setWarePropimgs(propimgs);
        }
    }
    
    /**
     * @return OpenApi配置管理器
     */
    public OpenApiConfigManager getOpenApiConfigManager() {
        return openApiConfigManager;
    }

    /**
     * @param openApiConfigManager OpenApi配置管理器
     */
    public void setOpenApiConfigManager(OpenApiConfigManager openApiConfigManager) {
        this.openApiConfigManager = openApiConfigManager;
    }
}
