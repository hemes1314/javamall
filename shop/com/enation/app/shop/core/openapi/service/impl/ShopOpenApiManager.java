package com.enation.app.shop.core.openapi.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.enation.app.b2b2c.core.model.store.StoreCat;
import com.enation.app.shop.core.model.ShopOpenApi;
import com.enation.app.shop.core.openapi.service.IShopOpenApiiManager;
import com.gome.open.api.sdk.GomeException;
import com.gome.open.api.shop.client.dto.request.SellerCatAddRequest;
import com.gome.open.api.shop.client.dto.request.SellerCatDeleteRequest;
import com.gome.open.api.shop.client.dto.request.SellerCatUpdateRequest;
import com.gome.open.api.shop.client.dto.request.ShopUpdateRequest;
import com.gome.open.api.shop.client.dto.response.SellerCatAddResponse;
import com.gome.open.api.shop.client.dto.response.SellerCatDeleteResponse;
import com.gome.open.api.shop.client.dto.response.SellerCatUpdateResponse;

/**
 * OpenApi店铺信息管理接口实现.
 * 
 * @author baoxiufeng
 */
public class ShopOpenApiManager implements IShopOpenApiiManager {

    // OpenApi配置管理器
    private OpenApiConfigManager openApiConfigManager;
    
    @Override
    public SellerCatAddResponse addCat(StoreCat storeCat) throws Exception {
        SellerCatAddRequest request = new SellerCatAddRequest();
        request.setShopId(String.valueOf(storeCat.getStore_id()));
        request.setParentId(String.valueOf(storeCat.getStore_cat_pid()));
        request.setIndexId(Long.valueOf(storeCat.getSort()));
        request.setName(storeCat.getStore_cat_name());
        request.setIsHomeShow(storeCat.getDisable() == 1 ? 0 : 1);
        // 执行API调用
        return openApiConfigManager.execute(request);
    }
    
    @Override
    public SellerCatUpdateResponse updateCat(StoreCat storeCat) throws Exception {
        SellerCatUpdateRequest request = new SellerCatUpdateRequest();
        request.setShopId(String.valueOf(storeCat.getStore_id()));
        request.setId(String.valueOf(storeCat.getStore_cat_id()));
        request.setParentId(String.valueOf(storeCat.getStore_cat_pid()));
        request.setIndexId(Long.valueOf(storeCat.getSort()));
        request.setName(storeCat.getStore_cat_name());
        request.setIsHomeShow(storeCat.getDisable() == 1 ? 0 : 1);
        // 执行API调用
        return openApiConfigManager.execute(request);
    }
    
    @Override
    public SellerCatDeleteResponse deleteCat(Integer storeCatId) throws Exception {
        SellerCatDeleteRequest request = new SellerCatDeleteRequest();
        request.setId(String.valueOf(storeCatId));
        // 执行API调用
        return openApiConfigManager.execute(request);
    }

    @Override
    public List<SellerCatDeleteResponse> batchDeleteCat(List<Integer> storeCatIds) throws Exception {
        if (storeCatIds == null || storeCatIds.isEmpty()) return null;
        List<SellerCatDeleteResponse> responses = new ArrayList<SellerCatDeleteResponse>(storeCatIds.size());
        for (Integer storeCatId : storeCatIds) {
            responses.add(deleteCat(storeCatId));
        }
        return responses;
    }

    @Override
    public void updateShopScore(ShopOpenApi shopOpenApi) {
//        ShopUpdateRequest shopUpdateRequest = new ShopUpdateRequest();
//        try {
//            shopUpdateRequest.setId(shopOpenApi.getShopId());
//            shopUpdateRequest.setDescribeMatchScore(shopOpenApi.getDescribeMatchScore());
//            shopUpdateRequest.setPostSpeedScore(shopOpenApi.getPostSpeedScore());
//            shopUpdateRequest.setDescribeMatchScore(shopOpenApi.getDescribeMatchScore());
//            openApiConfigManager.execute(shopUpdateRequest);
//        } catch (GomeException e) {
//            e.printStackTrace();
//            throw new RuntimeException("更新店铺分数错误！");
//        }
    }

    /**
     * @param openApiConfigManager OpenApi配置管理器
     */
    public void setOpenApiConfigManager(OpenApiConfigManager openApiConfigManager) {
        this.openApiConfigManager = openApiConfigManager;
    }
}
