package com.enation.app.shop.core.openapi.service;

import java.util.List;

import com.enation.app.b2b2c.core.model.store.StoreCat;
import com.enation.app.shop.core.model.ShopOpenApi;
import com.gome.open.api.shop.client.dto.response.SellerCatAddResponse;
import com.gome.open.api.shop.client.dto.response.SellerCatDeleteResponse;
import com.gome.open.api.shop.client.dto.response.SellerCatUpdateResponse;

/**
 * OpenApi店铺信息管理接口.
 * 
 * @author baoxiufeng
 */
public interface IShopOpenApiiManager {

    /**
     * 店铺类目信息新增请求.
     * 
     * @param storeCat 店铺类目信息
     * @return 店铺类目信息新增请求响应
     * @throws Exception 操作异常信息
     */
    public SellerCatAddResponse addCat(StoreCat storeCat) throws Exception;
    
    /**
     * 店铺类目信息修改请求.
     * 
     * @param storeCat 店铺类目信息
     * @return 店铺类目信息修改请求响应
     * @throws Exception 操作异常信息
     */
    public SellerCatUpdateResponse updateCat(StoreCat storeCat) throws Exception;
    
    /**
     * 店铺类目信息删除请求.
     * 
     * @param storeCatId 店铺类目ID
     * @return 店铺类目信息删除请求响应
     * @throws Exception 操作异常信息
     */
    public SellerCatDeleteResponse deleteCat(Integer storeCatId) throws Exception;
    
    /**
     * 店铺类目信息批量删除请求.
     * 
     * @param storeCatIds 店铺类目ID列表
     * @return 店铺类目信息删除请求响应列表
     * @throws Exception 操作异常信息
     */
    public List<SellerCatDeleteResponse> batchDeleteCat(List<Integer> storeCatIds) throws Exception;

    void updateShopScore(ShopOpenApi shopOpenApi);
}
